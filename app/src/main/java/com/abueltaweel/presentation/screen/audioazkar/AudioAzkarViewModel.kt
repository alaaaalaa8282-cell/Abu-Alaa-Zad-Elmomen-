package com.abueltaweel.presentation.screen.audioazkar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abueltaweel.presentation.screen.radio.player.PlayerController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AzkarTrack(
    val id: Int,
    val titleAr: String,
    val reciter: String,
    val url: String,
    val category: AzkarCategory
)

enum class AzkarCategory(val titleAr: String) {
    MORNING("أذكار الصباح"),
    EVENING("أذكار المساء"),
    SLEEP("أذكار النوم"),
    WAKE("أذكار الاستيقاظ"),
    AFTER_PRAYER("أذكار بعد الصلاة"),
    GENERAL("أذكار عامة")
}

enum class RepeatInterval(val labelAr: String, val minutes: Long) {
    ONCE("مرة واحدة", 0),
    MIN_1("كل دقيقة", 1),
    MIN_5("كل 5 دقائق", 5),
    MIN_10("كل 10 دقائق", 10),
    MIN_15("كل 15 دقيقة", 15),
    MIN_20("كل 20 دقيقة", 20),
    MIN_30("كل 30 دقيقة", 30),
    HOUR_1("كل ساعة", 60),
    HOUR_2("كل ساعتين", 120),
    HOUR_3("كل 3 ساعات", 180),
    HOUR_6("كل 6 ساعات", 360),
    HOUR_12("كل 12 ساعة", 720)
}

data class AudioAzkarUiState(
    val tracks: List<AzkarTrack> = emptyList(),
    val selectedCategory: AzkarCategory = AzkarCategory.MORNING,
    val currentTrack: AzkarTrack? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val selectedInterval: RepeatInterval = RepeatInterval.ONCE,
    val showIntervalPicker: Boolean = false
)

class AudioAzkarViewModel(
    private val playerController: PlayerController
) : ViewModel() {

    private val _uiState = MutableStateFlow(AudioAzkarUiState(tracks = allTracks))
    val uiState: StateFlow<AudioAzkarUiState> = _uiState.asStateFlow()

    private var repeatJob: Job? = null

    fun selectCategory(category: AzkarCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun playTrack(track: AzkarTrack) {
        repeatJob?.cancel()
        _uiState.value = _uiState.value.copy(
            currentTrack = track,
            isPlaying = true,
            isLoading = true
        )
        playerController.play(track.url)
        _uiState.value = _uiState.value.copy(isLoading = false)

        // لو المستخدم اختار تكرار
        val interval = _uiState.value.selectedInterval
        if (interval != RepeatInterval.ONCE) {
            repeatJob = viewModelScope.launch {
                while (true) {
                    delay(interval.minutes * 60 * 1000)
                    if (_uiState.value.isPlaying) {
                        playerController.play(track.url)
                    } else break
                }
            }
        }
    }

    fun pauseTrack() {
        repeatJob?.cancel()
        playerController.pause()
        _uiState.value = _uiState.value.copy(isPlaying = false)
    }

    fun stopTrack() {
        repeatJob?.cancel()
        playerController.stop()
        _uiState.value = _uiState.value.copy(isPlaying = false, currentTrack = null)
    }

    fun selectInterval(interval: RepeatInterval) {
        _uiState.value = _uiState.value.copy(
            selectedInterval = interval,
            showIntervalPicker = false
        )
    }

    fun toggleIntervalPicker() {
        _uiState.value = _uiState.value.copy(
            showIntervalPicker = !_uiState.value.showIntervalPicker
        )
    }

    // مش بنوقف عند الخروج من الصفحة عشان الصوت يكمل
    override fun onCleared() {
        super.onCleared()
    }

    companion object {
        val allTracks = listOf(
            AzkarTrack(1, "أذكار الصباح", "مشاري العفاسي",
                "https://server8.mp3quran.net/afs/Rewayat-Hafs-A-n-Asim/024.mp3",
                AzkarCategory.MORNING),
            AzkarTrack(2, "أذكار الصباح", "ماهر المعيقلي",
                "https://server12.mp3quran.net/maher/Rewayat-Hafs-A-n-Asim/001.mp3",
                AzkarCategory.MORNING),
            AzkarTrack(3, "أذكار الصباح والمساء", "سعد الغامدي",
                "https://server7.mp3quran.net/s_gmd/001.mp3",
                AzkarCategory.MORNING),
            AzkarTrack(4, "أذكار المساء", "مشاري العفاسي",
                "https://server8.mp3quran.net/afs/Rewayat-Hafs-A-n-Asim/001.mp3",
                AzkarCategory.EVENING),
            AzkarTrack(5, "أذكار المساء", "ماهر المعيقلي",
                "https://server12.mp3quran.net/maher/Rewayat-Hafs-A-n-Asim/002.mp3",
                AzkarCategory.EVENING),
            AzkarTrack(6, "أذكار النوم", "مشاري العفاسي",
                "https://server8.mp3quran.net/afs/Rewayat-Hafs-A-n-Asim/067.mp3",
                AzkarCategory.SLEEP),
            AzkarTrack(7, "سورة البقرة - للنوم", "ماهر المعيقلي",
                "https://server12.mp3quran.net/maher/Rewayat-Hafs-A-n-Asim/002.mp3",
                AzkarCategory.SLEEP),
            AzkarTrack(8, "أذكار الاستيقاظ", "سعد الغامدي",
                "https://server7.mp3quran.net/s_gmd/036.mp3",
                AzkarCategory.WAKE),
            AzkarTrack(9, "دعاء الاستيقاظ", "مشاري العفاسي",
                "https://server8.mp3quran.net/afs/Rewayat-Hafs-A-n-Asim/036.mp3",
                AzkarCategory.WAKE),
            AzkarTrack(10, "أذكار بعد الصلاة", "ماهر المعيقلي",
                "https://server12.mp3quran.net/maher/Rewayat-Hafs-A-n-Asim/036.mp3",
                AzkarCategory.AFTER_PRAYER),
            AzkarTrack(11, "تسبيح وتحميد وتكبير", "مشاري العفاسي",
                "https://server8.mp3quran.net/afs/Rewayat-Hafs-A-n-Asim/055.mp3",
                AzkarCategory.AFTER_PRAYER),
            AzkarTrack(12, "آية الكرسي", "مشاري العفاسي",
                "https://server8.mp3quran.net/afs/Rewayat-Hafs-A-n-Asim/002.mp3",
                AzkarCategory.GENERAL),
            AzkarTrack(13, "سورة الملك", "ماهر المعيقلي",
                "https://server12.mp3quran.net/maher/Rewayat-Hafs-A-n-Asim/067.mp3",
                AzkarCategory.GENERAL),
            AzkarTrack(14, "سورة الكهف", "سعد الغامدي",
                "https://server7.mp3quran.net/s_gmd/018.mp3",
                AzkarCategory.GENERAL)
        )
    }
}
