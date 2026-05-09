package com.abueltaweel.presentation.screen.audioazkar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abueltaweel.presentation.screen.radio.player.PlayerController
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

data class AudioAzkarUiState(
    val tracks: List<AzkarTrack> = emptyList(),
    val selectedCategory: AzkarCategory = AzkarCategory.MORNING,
    val currentTrack: AzkarTrack? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false
)

class AudioAzkarViewModel(
    private val playerController: PlayerController
) : ViewModel() {

    private val _uiState = MutableStateFlow(AudioAzkarUiState(tracks = allTracks))
    val uiState: StateFlow<AudioAzkarUiState> = _uiState.asStateFlow()

    fun selectCategory(category: AzkarCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun playTrack(track: AzkarTrack) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                currentTrack = track,
                isPlaying = true,
                isLoading = true
            )
            playerController.play(track.url, track.titleAr)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun stopTrack() {
        playerController.stop()
        _uiState.value = _uiState.value.copy(isPlaying = false, currentTrack = null)
    }

    override fun onCleared() {
        playerController.stop()
        super.onCleared()
    }

    companion object {
        val allTracks = listOf(
            // أذكار الصباح
            AzkarTrack(1, "أذكار الصباح", "مشاري العفاسي",
                "https://server8.mp3quran.net/afs/Rewayat-Hafs-A-n-Asim/024.mp3",
                AzkarCategory.MORNING),
            AzkarTrack(2, "أذكار الصباح", "ماهر المعيقلي",
                "https://server12.mp3quran.net/maher/Rewayat-Hafs-A-n-Asim/001.mp3",
                AzkarCategory.MORNING),
            AzkarTrack(3, "أذكار الصباح والمساء", "سعد الغامدي",
                "https://server7.mp3quran.net/s_gmd/001.mp3",
                AzkarCategory.MORNING),

            // أذكار المساء
            AzkarTrack(4, "أذكار المساء", "مشاري العفاسي",
                "https://server8.mp3quran.net/afs/Rewayat-Hafs-A-n-Asim/001.mp3",
                AzkarCategory.EVENING),
            AzkarTrack(5, "أذكار المساء", "ماهر المعيقلي",
                "https://server12.mp3quran.net/maher/Rewayat-Hafs-A-n-Asim/002.mp3",
                AzkarCategory.EVENING),

            // أذكار النوم
            AzkarTrack(6, "أذكار النوم", "مشاري العفاسي",
                "https://server8.mp3quran.net/afs/Rewayat-Hafs-A-n-Asim/067.mp3",
                AzkarCategory.SLEEP),
            AzkarTrack(7, "سورة البقرة - للنوم", "ماهر المعيقلي",
                "https://server12.mp3quran.net/maher/Rewayat-Hafs-A-n-Asim/002.mp3",
                AzkarCategory.SLEEP),

            // أذكار الاستيقاظ
            AzkarTrack(8, "أذكار الاستيقاظ", "سعد الغامدي",
                "https://server7.mp3quran.net/s_gmd/036.mp3",
                AzkarCategory.WAKE),
            AzkarTrack(9, "دعاء الاستيقاظ", "مشاري العفاسي",
                "https://server8.mp3quran.net/afs/Rewayat-Hafs-A-n-Asim/036.mp3",
                AzkarCategory.WAKE),

            // أذكار بعد الصلاة
            AzkarTrack(10, "أذكار بعد الصلاة", "ماهر المعيقلي",
                "https://server12.mp3quran.net/maher/Rewayat-Hafs-A-n-Asim/036.mp3",
                AzkarCategory.AFTER_PRAYER),
            AzkarTrack(11, "تسبيح وتحميد وتكبير", "مشاري العفاسي",
                "https://server8.mp3quran.net/afs/Rewayat-Hafs-A-n-Asim/055.mp3",
                AzkarCategory.AFTER_PRAYER),

            // أذكار عامة
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
