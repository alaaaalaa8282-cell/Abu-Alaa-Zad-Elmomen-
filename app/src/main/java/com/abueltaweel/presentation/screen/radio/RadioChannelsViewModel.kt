```kotlin
package com.abueltaweel.presentation.screen.radio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * بيانات محطة الإذاعة
 */
data class RadioChannel(
    val id: Int,
    val name: String,
    val url: String,
    val description: String
)

/**
 * حالة الشاشة (UI State)
 */
data class RadioUiState(
    val channels: List<RadioChannel> = emptyList(),
    val isLoading: Boolean = false,
    val currentPlayingId: Int? = null,
    val isPlaying: Boolean = false,
    val errorMessage: String? = null
)

class RadioChannelsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RadioUiState())
    val uiState: StateFlow<RadioUiState> = _uiState.asStateFlow()

    init {
        loadChannels()
    }

    /**
     * تحميل قائمة الإذاعات الموثوقة
     * ملاحظة: هذه الروابط تعمل بشكل مباشر (Direct Stream)
     */
    private fun loadChannels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val liveChannels = listOf(
                RadioChannel(
                    1, 
                    "إذاعة القرآن الكريم من القاهرة", 
                    "https://n02.radiojar.com/8s7uar320z4tv?1611574676", 
                    "البث الرئيسي والمباشر"
                ),
                RadioChannel(
                    2, 
                    "إذاعة التلاوات المختارة", 
                    "https://n0a.radiojar.com/0tpy8cr738quv", 
                    "تلاوات خاشعة على مدار الساعة"
                ),
                RadioChannel(
                    3, 
                    "إذاعة الشيخ عبد الباسط عبد الصمد", 
                    "https://backup.quran.com.kw/abdulbasit", 
                    "المصحف المجود والترتيل"
                ),
                RadioChannel(
                    4, 
                    "إذاعة الشيخ محمد صديق المنشاوي", 
                    "https://backup.quran.com.kw/minshawi", 
                    "تلاوات نادرة ومؤثرة"
                ),
                RadioChannel(
                    5, 
                    "إذاعة الشيخ محمود خليل الحصري", 
                    "https://backup.quran.com.kw/hussary", 
                    "المعلم والمجود"
                ),
                RadioChannel(
                    6, 
                    "إذاعة السنة النبوية", 
                    "https://n02.radiojar.com/97y03bt320z4tv", 
                    "أحاديث نبوية وشروحات"
                ),
                RadioChannel(
                    7, 
                    "إذاعة الفتاوى - السعودية", 
                    "https://n08.radiojar.com/732n6p280z4tv", 
                    "بث مباشر للفتاوى والدروس"
                ),
                RadioChannel(
                    8, 
                    "إذاعة القرآن الكريم من نابلس", 
                    "http://162.244.80.118:8160/stream", 
                    "بث مباشر من فلسطين"
                )
            )

            _uiState.value = _uiState.value.copy(
                channels = liveChannels,
                isLoading = false
            )
        }
    }

    /**
     * تشغيل أو إيقاف الإذاعة
     */
    fun togglePlay(channel: RadioChannel) {
        val isCurrentlyPlaying = _uiState.value.currentPlayingId == channel.id && _uiState.value.isPlaying
        
        if (isCurrentlyPlaying) {
            // منطق إيقاف البث هنا
            _uiState.value = _uiState.value.copy(isPlaying = false)
        } else {
            // منطق تشغيل الرابط channel.url هنا باستخدام ExoPlayer أو Media3
            _uiState.value = _uiState.value.copy(
                currentPlayingId = channel.id,
                isPlaying = true
            )
        }
    }

    /**
     * تحديث حالة التشغيل من المشغل الخارجي
     */
    fun updatePlaybackState(isPlaying: Boolean) {
        _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
    }
}

```
