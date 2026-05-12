package com.abueltaweel.presentation.screen.radio

import androidx.lifecycle.viewModelScope
import com.abueltaweel.R
import com.abueltaweel.design_system.component.ToastDetails
import com.abueltaweel.presentation.base.BaseViewModel
import com.abueltaweel.presentation.screen.radio.player.PlayerController
import com.abueltaweel.presentation.screen.radio.player.PlayerState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RadioChannelsViewModel(
    private val playerController: PlayerController
) : BaseViewModel<RadioUiState, RadioChannelsEffect>(RadioUiState()),
    RadioChannelsInteractionListener {

    companion object {

        // =====================================================================
        // إذاعات القرآن الكريم - Quran Recitation Stations
        // =====================================================================
        private val QURAN_CHANNELS = listOf(

            RadioUiState.RadioChannelUiState(
                id = 1,
                nameAr = "إذاعة القرآن الكريم - ماهر المعيقلي",
                nameEn = "Radio Quran - Maher Al Mueaqly",
                streamUrl = "https://radio.mp3islam.com:8020/radio.mp3"
            ),
            RadioUiState.RadioChannelUiState(
                id = 2,
                nameAr = "إذاعة القرآن الكريم - مشاري العفاسي",
                nameEn = "Radio Quran - Mishary Alafasy",
                streamUrl = "https://radio.mp3islam.com:8030/radio.mp3"
            ),
            RadioUiState.RadioChannelUiState(
                id = 3,
                nameAr = "إذاعة القرآن الكريم - عبد الرحمن السديس",
                nameEn = "Radio Quran - Abdulrahman Alsudais",
                streamUrl = "https://radio.mp3islam.com:8070/radio.mp3"
            ),
            RadioUiState.RadioChannelUiState(
                id = 4,
                nameAr = "إذاعة القرآن الكريم - علي الحذيفي",
                nameEn = "Radio Quran - Ali Al-Hudhaifi",
                streamUrl = "https://radio.mp3islam.com:8040/radio.mp3"
            ),
            RadioUiState.RadioChannelUiState(
                id = 5,
                nameAr = "إذاعة القرآن الكريم - ياسر الدوسري",
                nameEn = "Quran - Yasser AlDosari",
                streamUrl = "https://radio.mp3islam.com:8060/radio.mp3"
            ),
            RadioUiState.RadioChannelUiState(
                id = 6,
                nameAr = "إذاعة القرآن الكريم - قراءات متنوعة",
                nameEn = "Radio Quran - Various Reciters",
                streamUrl = "https://radio.mp3islam.com:8010/radio.mp3"
            ),
            RadioUiState.RadioChannelUiState(
                id = 7,
                nameAr = "إذاعة القرآن الكريم - حسن صالح",
                nameEn = "Radio Quraan - Hassan Saleh",
                streamUrl = "https://radio.radioquraan.com:9994/stream"
            ),
            RadioUiState.RadioChannelUiState(
                id = 8,
                nameAr = "إذاعة الحسناء للقرآن الكريم",
                nameEn = "Al Husnaa Quran Radio",
                streamUrl = "https://stream.zeno.fm/pyc8kax6f2zuv"
            ),
            RadioUiState.RadioChannelUiState(
                id = 9,
                nameAr = "إذاعة القرآن الكريم - كندا",
                nameEn = "Quran Radio of Canada",
                streamUrl = "http://74.59.177.99:21003/;"
            ),
            RadioUiState.RadioChannelUiState(
                id = 10,
                nameAr = "إذاعة ختم القرآن الكريم",
                nameEn = "KhatmAlQuran",
                streamUrl = "http://eu1.bilalbox.com:1380/stream"
            ),

            // =====================================================================
            // إذاعات إسلامية متنوعة - Islamic Radio Stations
            // =====================================================================

            RadioUiState.RadioChannelUiState(
                id = 11,
                nameAr = "إيمان سيتي - دروس ومحاضرات",
                nameEn = "Eman City",
                streamUrl = "http://216.158.232.126:9990/stream"
            ),
            RadioUiState.RadioChannelUiState(
                id = 12,
                nameAr = "إذاعة القرآن الكريم والتفسير",
                nameEn = "Quran Radio Tafsir",
                streamUrl = "https://216.158.232.126:9992/stream"
            ),
            RadioUiState.RadioChannelUiState(
                id = 13,
                nameAr = "إذاعة نور الإسلام",
                nameEn = "Radio Nur Islam",
                streamUrl = "http://ec6.yesstreaming.net:3320/stream"
            ),
            RadioUiState.RadioChannelUiState(
                id = 14,
                nameAr = "إذاعة القرآن الكريم - السودان",
                nameEn = "Quran Radio Sudan 1",
                streamUrl = "https://82.167.181.65:443/stream/2/"
            ),
            RadioUiState.RadioChannelUiState(
                id = 15,
                nameAr = "إذاعة القرآن الكريم - السودان 2",
                nameEn = "Quran Radio Sudan 2",
                streamUrl = "https://82.167.181.199:443/stream/2/"
            ),
            RadioUiState.RadioChannelUiState(
                id = 16,
                nameAr = "إذاعة القرآن للروح",
                nameEn = "Quran For The Soul",
                streamUrl = "http://104.7.66.64:8095/;"
            ),
            RadioUiState.RadioChannelUiState(
                id = 17,
                nameAr = "تفسير الشعراوي والنابلسي",
                nameEn = "Quran Tafsir - Sharawi & Nabulsi",
                streamUrl = "http://104.7.66.64:8088"
            ),
            RadioUiState.RadioChannelUiState(
                id = 18,
                nameAr = "إذاعة سيانج القرآن",
                nameEn = "Radio Sayang Quran",
                streamUrl = "https://radio.sayangquran.com/listen/radiosq/radio.mp3"
            ),
        )
    }

    init {
        loadChannels()
        observePlayerState()
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            playerController.playerState.collectLatest { serviceState ->
                if (serviceState.isError) {
                    sendEffect(
                        RadioChannelsEffect.ShowToast(
                            ToastDetails(
                                title = R.string.error,
                                message = R.string.no_internet_connection,
                                icon = R.drawable.ic_close_circle
                            )
                        )
                    )
                }
                updateUiBasedOnServiceState(serviceState)
            }
        }
    }

    private fun updateUiBasedOnServiceState(serviceState: PlayerState) {
        updateState { oldState ->
            val updatedChannels = oldState.channels.map { channel ->
                val isSelected = channel.streamUrl == serviceState.currentUrl
                val isPlaying = serviceState.isPlaying && isSelected
                val isLoading =
                    if (!isSelected) false
                    else if (isPlaying) false
                    else channel.isLoading || serviceState.isBuffering

                if (
                    channel.isPlaying == isPlaying &&
                    channel.selected == isSelected &&
                    channel.isLoading == isLoading
                ) {
                    channel
                } else {
                    channel.copy(
                        isPlaying = isPlaying,
                        selected = isSelected,
                        isLoading = isLoading
                    )
                }
            }
            oldState.copy(channels = updatedChannels)
        }
    }

    fun loadChannels() {
        updateState {
            it.copy(
                channels = QURAN_CHANNELS,
                isLoading = false,
                isNoInternet = false
            )
        }
    }

    override fun onPlayClick(id: Int) {
        val channel = screenState.value.channels.firstOrNull { it.id == id } ?: return
        sendEffect(RadioChannelsEffect.PlaySound(channel.streamUrl, channel.nameAr))
    }

    override fun onPauseClick(id: Int) {
        updateState { old ->
            old.copy(
                channels = old.channels.map {
                    if (it.id == id) it.copy(isPlaying = false)
                    else it
                }
            )
        }
        sendEffect(RadioChannelsEffect.PauseSound)
    }
}
