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
        private val QURAN_CHANNELS = listOf(
            RadioUiState.RadioChannelUiState(
                id = 1,
                nameAr = "إذاعة أبو بكر الشاطري",
                nameEn = "Abu Bakr Al-Shatri",
                streamUrl = "https://backup.qurango.net/radio/shaik_abu_bakr_al_shatri"
            ),
            RadioUiState.RadioChannelUiState(
                id = 2,
                nameAr = "إذاعة أحمد خضر الطرابلسي",
                nameEn = "Ahmad Khader Al-Tarabulsi",
                streamUrl = "https://backup.qurango.net/radio/ahmad_khader_altarabulsi"
            ),
            RadioUiState.RadioChannelUiState(
                id = 3,
                nameAr = "إذاعة إبراهيم الأخضر",
                nameEn = "Ibrahim Al-Akhdar",
                streamUrl = "https://backup.qurango.net/radio/ibrahim_alakdar"
            ),
            RadioUiState.RadioChannelUiState(
                id = 4,
                nameAr = "إذاعة خالد الجليل",
                nameEn = "Khalid Al-Jalil",
                streamUrl = "https://backup.qurango.net/radio/khalid_aljileel"
            ),
            RadioUiState.RadioChannelUiState(
                id = 5,
                nameAr = "إذاعة صلاح الهاشم",
                nameEn = "Salah Al-Hashim",
                streamUrl = "https://backup.qurango.net/radio/salah_alhashim"
            ),
            RadioUiState.RadioChannelUiState(
                id = 6,
                nameAr = "إذاعة صلاح بو خاطر",
                nameEn = "Salah Bu Khatir",
                streamUrl = "https://backup.qurango.net/radio/slaah_bukhatir"
            ),
            RadioUiState.RadioChannelUiState(
                id = 7,
                nameAr = "إذاعة عبد الباسط عبد الصمد",
                nameEn = "Abdul Basit Abdul Samad",
                streamUrl = "https://backup.qurango.net/radio/abdulbasit_abdulsamad_mojawwad"
            ),
            RadioUiState.RadioChannelUiState(
                id = 8,
                nameAr = "إذاعة عبد العزيز سحيم",
                nameEn = "Abdul Aziz Sheim",
                streamUrl = "https://backup.qurango.net/radio/a_sheim"
            ),
            RadioUiState.RadioChannelUiState(
                id = 9,
                nameAr = "إذاعة فارس عباد",
                nameEn = "Fares Abbad",
                streamUrl = "https://backup.qurango.net/radio/fares_abbad"
            ),
            RadioUiState.RadioChannelUiState(
                id = 10,
                nameAr = "إذاعة ماهر المعيقلي",
                nameEn = "Maher Al-Mueaqly",
                streamUrl = "https://backup.qurango.net/radio/maher"
            ),
            RadioUiState.RadioChannelUiState(
                id = 11,
                nameAr = "إذاعة محمد صديق المنشاوي",
                nameEn = "Mohammed Siddiq Al-Minshawi",
                streamUrl = "https://backup.qurango.net/radio/mohammed_siddiq_alminshawi_mojawwad"
            ),
            RadioUiState.RadioChannelUiState(
                id = 12,
                nameAr = "إذاعة محمود خليل الحصري",
                nameEn = "Mahmoud Khalil Al-Hussary",
                streamUrl = "https://backup.qurango.net/radio/mahmoud_khalil_alhussary_mojawwad"
            ),
            RadioUiState.RadioChannelUiState(
                id = 13,
                nameAr = "إذاعة محمود علي البنا",
                nameEn = "Mahmoud Ali Al-Banna",
                streamUrl = "https://backup.qurango.net/radio/mahmoud_ali__albanna_mojawwad"
            ),
            // ✅ الحذيفي بدل مشاري العفاسي
            RadioUiState.RadioChannelUiState(
                id = 14,
                nameAr = "إذاعة علي الحذيفي",
                nameEn = "Ali Al-Hudhaifi",
                streamUrl = "http://backup.qurango.net/radio/ali_alhuthaify"
            ),
            RadioUiState.RadioChannelUiState(
                id = 15,
                nameAr = "إذاعة ناصر القطامي",
                nameEn = "Nasser Al-Qatami",
                streamUrl = "https://backup.qurango.net/radio/nasser_alqatami"
            ),
            RadioUiState.RadioChannelUiState(
                id = 16,
                nameAr = "إذاعة نبيل الرفاعي",
                nameEn = "Nabil Al-Rifai",
                streamUrl = "https://backup.qurango.net/radio/nabil_al_rifay"
            ),
            RadioUiState.RadioChannelUiState(
                id = 17,
                nameAr = "إذاعة هيثم الجدعاني",
                nameEn = "Hitham Al-Jadani",
                streamUrl = "https://backup.qurango.net/radio/hitham_aljadani"
            ),
            RadioUiState.RadioChannelUiState(
                id = 18,
                nameAr = "إذاعة ياسر الدوسري",
                nameEn = "Yasser Al-Dosari",
                streamUrl = "https://backup.qurango.net/radio/yasser_aldosari"
            ),
            RadioUiState.RadioChannelUiState(
                id = 19,
                nameAr = "إذاعة القرآن الكريم من القاهرة",
                nameEn = "Quran Radio Cairo",
                streamUrl = "https://stream.radiojar.com/8s5u5tpdtwzuv"
            ),
            RadioUiState.RadioChannelUiState(
                id = 20,
                nameAr = "إذاعة السنة النبوية",
                nameEn = "Sunnah Radio",
                streamUrl = "https://stream.radiojar.com/x0vs2vzy6k0uv"
            ),
            RadioUiState.RadioChannelUiState(
                id = 21,
                nameAr = "إذاعة تلاوات خاشعة",
                nameEn = "Khashi'a Recitations",
                streamUrl = "https://backup.qurango.net/radio/salma"
            ),
            RadioUiState.RadioChannelUiState(
                id = 22,
                nameAr = "إذاعة الرقية الشرعية",
                nameEn = "Al-Ruqyah Al-Shar'iyyah",
                streamUrl = "https://backup.qurango.net/radio/roqiah"
            ),
            // ✅ إذاعة جديدة - سعد الغامدي
            RadioUiState.RadioChannelUiState(
                id = 23,
                nameAr = "إذاعة سعد الغامدي",
                nameEn = "Saad Al-Ghamdi",
                streamUrl = "https://backup.qurango.net/radio/saad_alghamdi"
            ),
            RadioUiState.RadioChannelUiState(
                id = 24,
                nameAr = "المختصر في تفسير القرآن الكريم",
                nameEn = "Al-Mukhtasar Tafsir",
                streamUrl = "https://backup.qurango.net/radio/mukhtasartafsir"
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
                ) channel
                else channel.copy(
                    isPlaying = isPlaying,
                    selected = isSelected,
                    isLoading = isLoading
                )
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
                    if (it.id == id) it.copy(isPlaying = false) else it
                }
            )
        }
        sendEffect(RadioChannelsEffect.PauseSound)
    }
}

