package com.abueltaweel.presentation.screen.home.component

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

private const val PREFS_NAME        = "weather_cache"
private const val KEY_TEMP          = "temp"
private const val KEY_DESC          = "desc"
private const val KEY_WIND          = "wind"
private const val KEY_TIMESTAMP     = "timestamp"
private const val KEY_LAST_UPDATED  = "last_updated"
private const val CACHE_DURATION_MS = 30 * 60 * 1000L

private data class WeatherData(
    val temperature: Int,
    val description: String,
    val windSpeed: Int,
    val lastUpdated: String = ""
)

private fun weatherCodeToArabic(code: Int): String = when (code) {
    0          -> "صافٍ"
    1, 2       -> "غائم جزئياً"
    3          -> "غائم"
    45, 48     -> "ضباب"
    51, 53, 55,
    56, 57     -> "رذاذ"
    61, 63, 65,
    66, 67     -> "مطر"
    71, 73, 75,
    77         -> "ثلج"
    80, 81, 82 -> "زخات مطر"
    85, 86     -> "زخات ثلج"
    95, 96, 99 -> "عاصفة رعدية"
    else       -> "—"
}

private fun saveWeather(context: Context, data: WeatherData) {
    val cal = java.util.Calendar.getInstance()
    val h = cal.get(java.util.Calendar.HOUR_OF_DAY)
    val m = cal.get(java.util.Calendar.MINUTE)
    val amPm = if (h < 12) "ص" else "م"
    val displayHour = if (h % 12 == 0) 12 else h % 12
    val timeStr = "$displayHour:${m.toString().padStart(2, '0')} $amPm"
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        .putInt(KEY_TEMP, data.temperature)
        .putString(KEY_DESC, data.description)
        .putInt(KEY_WIND, data.windSpeed)
        .putLong(KEY_TIMESTAMP, System.currentTimeMillis())
        .putString(KEY_LAST_UPDATED, timeStr)
        .apply()
}

private fun loadWeather(context: Context): WeatherData? {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val desc = prefs.getString(KEY_DESC, null) ?: return null
    return WeatherData(
        temperature = prefs.getInt(KEY_TEMP, 0),
        description = desc,
        windSpeed   = prefs.getInt(KEY_WIND, 0),
        lastUpdated = prefs.getString(KEY_LAST_UPDATED, "") ?: ""
    )
}

private fun isCacheExpired(context: Context): Boolean {
    val last = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getLong(KEY_TIMESTAMP, 0L)
    return System.currentTimeMillis() - last > CACHE_DURATION_MS
}

private suspend fun fetchFromNetwork(lat: Double, lon: Double): WeatherData? =
    withContext(Dispatchers.IO) {
        try {
            val url = "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=$lat&longitude=$lon&current_weather=true"
            val json = URL(url).readText()
            val current = JSONObject(json).getJSONObject("current_weather")
            WeatherData(
                temperature = current.getDouble("temperature").toInt(),
                description = weatherCodeToArabic(current.getInt("weathercode")),
                windSpeed   = current.getDouble("windspeed").toInt()
            )
        } catch (e: Exception) {
            null
        }
    }

private sealed class WeatherUiState {
    object Loading : WeatherUiState()
    object NoPermission : WeatherUiState()
    data class Success(val data: WeatherData) : WeatherUiState()
}

@SuppressLint("MissingPermission")
@Composable
fun WeatherWidget(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    var uiState by remember {
        val cached = loadWeather(context)
        mutableStateOf(
            if (cached != null) WeatherUiState.Success(cached)
            else WeatherUiState.Loading
        )
    }

    LaunchedEffect(Unit) {
        if (uiState is WeatherUiState.Success && !isCacheExpired(context)) return@LaunchedEffect

        val granted =
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            uiState = WeatherUiState.NoPermission
            return@LaunchedEffect
        }

        val fusedClient = LocationServices.getFusedLocationProviderClient(context)

        // أولاً جرب lastLocation
        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                scope.launch {
                    val fresh = fetchFromNetwork(location.latitude, location.longitude)
                    if (fresh != null) {
                        saveWeather(context, fresh)
                        uiState = WeatherUiState.Success(
                            fresh.copy(lastUpdated = loadWeather(context)?.lastUpdated ?: "")
                        )
                    }
                }
            } else {
                // lastLocation فاضي — اطلب location جديد لمرة واحدة
                val request = LocationRequest.Builder(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000L
                ).setMaxUpdates(1).build()

                val callback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        fusedClient.removeLocationUpdates(this)
                        val loc = result.lastLocation ?: return
                        scope.launch {
                            val fresh = fetchFromNetwork(loc.latitude, loc.longitude)
                            if (fresh != null) {
                                saveWeather(context, fresh)
                                uiState = WeatherUiState.Success(
                                    fresh.copy(lastUpdated = loadWeather(context)?.lastUpdated ?: "")
                                )
                            } else {
                                if (uiState is WeatherUiState.Loading)
                                    uiState = WeatherUiState.NoPermission
                            }
                        }
                    }
                }

                fusedClient.requestLocationUpdates(
                    request, callback, Looper.getMainLooper()
                )
            }
        }.addOnFailureListener {
            if (uiState is WeatherUiState.Loading)
                uiState = WeatherUiState.NoPermission
        }
    }

    when (val state = uiState) {
        is WeatherUiState.Loading -> {
            WeatherContainer(modifier = modifier) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "جاري تحميل الطقس...",
                        fontSize = 13.sp,
                        color = Color(0xFF9E8E84)
                    )
                }
            }
        }

        is WeatherUiState.NoPermission -> { /* اخفي الـ Widget بصمت */ }

        is WeatherUiState.Success -> {
            val w = state.data
            WeatherContainer(modifier = modifier) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${w.temperature}°C",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE2B96F)
                        )
                        Text(
                            text = w.description,
                            fontSize = 13.sp,
                            color = Color(0xFF6B5A4E)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${w.windSpeed} km/h",
                            fontSize = 12.sp,
                            color = Color(0xFF6B5A4E)
                        )
                        if (w.lastUpdated.isNotEmpty()) {
                            Text(
                                text = "آخر تحديث ${w.lastUpdated}",
                                fontSize = 10.sp,
                                color = Color(0xFF9E8E84)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF2EBE0))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        content()
    }
}
