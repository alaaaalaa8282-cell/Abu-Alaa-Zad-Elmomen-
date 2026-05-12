package com.abueltaweel.presentation.screen.radio

import androidx.lifecycle.viewModelScope
import com.abueltaweel.presentation.base.BaseViewModel
import com.abueltaweel.presentation.screen.radio.player.PlayerController
import com.abueltaweel.presentation.screen.radio.player.PlayerState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val islamicRadioChannels = listOf(
    RadioUiState.RadioChannelUiState(1,  "إذاعة القرآن الكريم المصرية",  "Egypt Quran",    "http://stream.radiojar.com/0tpy1h0kxtzuv"),
    RadioUiState.RadioChannelUiState(2,  "إذاعة القرآن الكريم السعودية", "Saudi Quran",    "https://n01.radiojar.com/ksa-quran.mp3"),
    RadioUiState.RadioChannelUiState(3,  "إذاعة المجد للقرآن الكريم",    "Al Majd Quran",  "https://n01.radiojar.com/almajdquran.mp3"),
    RadioUiState.RadioChannelUiState(4,  "إذاعة القرآن الكريم الكويت",   "Kuwait Quran",   "https://n01.radiojar.com/kuwaitquran.mp3"),
    RadioUiState.RadioChannelUiState(5,  "إذاعة نداء الإسلام",           "Nida Al Islam",  "http://nadaalislamstream.com:8000/stream"),
    RadioUiState.RadioChannelUiState(6,  "إذاعة القرآن الكريم المغرب",   "Morocco Quran",  "https://n01.radiojar.com/morocquran.mp3"),
    RadioUiState.RadioChannelUiState(7,  "إذاعة القرآن الكريم الأردن",   "Jordan Quran",   "https://n01.radiojar.com/jordanquran.mp3"),
    RadioUiState.RadioChannelUiState(8,  "إذاعة القرآن الكريم تونس",     "Tunisia Quran",  "https://n01.radiojar.com/tunisquran.mp3"),
    RadioUiState.RadioChannelUiState(9,  "إذاعة القرآن الكريم الجزائر",  "Algeria Quran",  "https://n01.radiojar.com/algeriaQuran.mp3"),
    RadioUiState.RadioChannelUiState(10, "راديو الإسلام",                 "Radio Islam",    "https://n01.radiojar.com/radioislam.mp3")
)

class RadioChannelsViewModel(
    private val playerController: PlayerController
) : BaseViewModel<RadioUiState, RadioChannelsEffect>(RadioUiState()),
    RadioChannelsInteractionListener {

    init {
        updateState { it.copy(channels = islamicRadioChannels, isLoading = false) }
        observePlayerState()
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            playerController.playerState.collectLatest { serviceState ->
                updateUiBasedOnServiceState(serviceState)
            }
        }
    }

    private fun updateUiBasedOnServiceState(serviceState: PlayerState) {
        updateState { oldState ->
            val updatedChannels = oldState.channels.map { channel ->
                val isSelected = channel.streamUrl == serviceState.currentUrl
                val isPlaying  = serviceState.isPlaying && isSelected
                val isLoading  = if (!isSelected) false
                    else if (isPlaying) false
                    else channel.isLoading || serviceState.isBuffering
                channel.copy(isPlaying = isPlaying, selected = isSelected, isLoading = isLoading)
            }
            oldState.copy(channels = updatedChannels)
        }
    }

    override fun onPlayClick(id: Int) {
        val channel = screenState.value.channels.firstOrNull { it.id == id } ?: return
        sendEffect(RadioChannelsEffect.PlaySound(channel.streamUrl, channel.nameAr))
    }

    override fun onPauseClick(id: Int) {
        updateState { old ->
            old.copy(channels = old.channels.map {
                if (it.id == id) it.copy(isPlaying = false) else it
            })
        }
        sendEffect(RadioChannelsEffect.PauseSound)
    }
}
