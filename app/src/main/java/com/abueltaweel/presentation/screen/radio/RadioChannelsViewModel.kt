package com.abueltaweel.presentation.screen.radio

import androidx.lifecycle.viewModelScope
import com.abueltaweel.presentation.base.BaseViewModel
import com.abueltaweel.presentation.screen.radio.player.PlayerController
import com.abueltaweel.presentation.screen.radio.player.PlayerState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val islamicRadioChannels = listOf(
    RadioUiState.RadioChannelUiState(1, "إذاعة القرآن الكريم المصرية",  "Egypt Quran",   "http://cana.be/4978"),
    RadioUiState.RadioChannelUiState(2, "إذاعة القرآن الكريم السعودية", "Saudi Quran",   "http://stream.srrs.sa:8000/quran/"),
    RadioUiState.RadioChannelUiState(3, "إذاعة المجد للقرآن الكريم",   "Al Majd Quran", "http://www.almajdtv.com:1935/live/quran/playlist.m3u8"),
    RadioUiState.RadioChannelUiState(4, "إذاعة القرآن الكريم الكويت",  "Kuwait Quran",  "http://www.snrplayer.com:8005/stream"),
    RadioUiState.RadioChannelUiState(5, "إذاعة القرآن الكريم المغرب",  "Morocco Quran", "http://radio.snrt.ma:1935/live/quran/playlist.m3u8"),
    RadioUiState.RadioChannelUiState(6, "إذاعة نداء الإسلام",          "Nida Al Islam", "http://stream.zeno.fm/yn65y9gklzzuv"),
    RadioUiState.RadioChannelUiState(7, "إذاعة القرآن الكريم الأردن",  "Jordan Quran",  "http://stream.zeno.fm/2r0km5ekl5zuv"),
    RadioUiState.RadioChannelUiState(8, "إذاعة القرآن الكريم تونس",    "Tunisia Quran", "http://stream.zeno.fm/0r0xa792kwzuv"),
    RadioUiState.RadioChannelUiState(9, "إذاعة القرآن الكريم الجزائر", "Algeria Quran", "http://stream.zeno.fm/4d6d4952n8zuv"),
    RadioUiState.RadioChannelUiState(10,"راديو الإسلام",                "Radio Islam",   "http://stream.zeno.fm/wrnyq6eklzzuv")
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
