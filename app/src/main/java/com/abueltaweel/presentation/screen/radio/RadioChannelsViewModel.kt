package com.abueltaweel.presentation.screen.radio

import androidx.lifecycle.viewModelScope
import com.abueltaweel.presentation.base.BaseViewModel
import com.abueltaweel.presentation.screen.radio.player.PlayerController
import com.abueltaweel.presentation.screen.radio.player.PlayerState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// قنوات إسلامية ثابتة - مش محتاجة Supabase
private val islamicRadioChannels = listOf(
    RadioUiState.RadioChannelUiState(1,  "إذاعة القرآن الكريم المصرية",    "Egypt Quran Radio",      "http://stream.radiojar.com/0tpy1h0kxtzuv"),
    RadioUiState.RadioChannelUiState(2,  "إذاعة القرآن الكريم السعودية",   "Saudi Quran Radio",      "http://stream.live.vc.bbcmedia.co.uk/bbc_arabic_radio"),
    RadioUiState.RadioChannelUiState(3,  "إذاعة نداء الإسلام",             "Nida Al Islam",          "http://stream.radiojar.com/8s5u5tpdtwzuv"),
    RadioUiState.RadioChannelUiState(4,  "إذاعة القرآن الكريم الكويت",     "Kuwait Quran Radio",     "http://www.radioislam.net:8100/listen.pls"),
    RadioUiState.RadioChannelUiState(5,  "إذاعة المجد للقرآن",             "Al Majd Quran",          "http://stream.radiojar.com/4xgb4952n8zuv"),
    RadioUiState.RadioChannelUiState(6,  "إذاعة القرآن الكريم المغرب",     "Morocco Quran Radio",    "http://stream.radiojar.com/0tpy1h0kxtzuv"),
    RadioUiState.RadioChannelUiState(7,  "راديو إسلام",                    "Radio Islam",            "http://radioislam.net:8100"),
    RadioUiState.RadioChannelUiState(8,  "إذاعة القرآن الكريم تونس",       "Tunisia Quran Radio",    "http://radiotunis.com:8000/quran.mp3"),
    RadioUiState.RadioChannelUiState(9,  "إذاعة صوت الإسلام",              "Voice of Islam",         "http://stream.radiojar.com/8s5u5tpdtwzuv"),
    RadioUiState.RadioChannelUiState(10, "إذاعة القرآن الكريم الجزائر",    "Algeria Quran Radio",    "http://stream.radiojar.com/4xgb4952n8zuv")
)

class RadioChannelsViewModel(
    private val playerController: PlayerController
) : BaseViewModel<RadioUiState, RadioChannelsEffect>(RadioUiState()),
    RadioChannelsInteractionListener {

    init {
        loadChannels()
        observePlayerState()
    }

    private fun loadChannels() {
        updateState { it.copy(channels = islamicRadioChannels, isLoading = false) }
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
