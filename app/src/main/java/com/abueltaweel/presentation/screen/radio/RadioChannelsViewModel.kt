```kotlin
package com.abueltaweel.presentation.screen.radio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RadioChannelsViewModel : ViewModel() {

    // نستخدم RadioUiState الأصلي الخاص بمشروعك
    private val _screenState = MutableStateFlow(RadioUiState())
    val screenState: StateFlow<RadioUiState> = _screenState.asStateFlow()

    private val _effect = MutableSharedFlow<String>()
    val effect = _effect.asSharedFlow()

    init {
        loadChannels()
    }

    private fun loadChannels() {
        viewModelScope.launch {
            _screenState.update { it.copy(isLoading = true) }

            // نستخدم RadioChannelUiState كما هو معرف في ملفك
            val list = listOf(
                RadioChannelUiState(id = 1, nameAr = "إذاعة القرآن الكريم - القاهرة", streamUrl = "https://n02.radiojar.com/8s7uar320z4tv"),
                RadioChannelUiState(id = 2, nameAr = "إذاعة تلاوات خاشعة", streamUrl = "https://n0a.radiojar.com/0tpy8cr738quv"),
                RadioChannelUiState(id = 3, nameAr = "الشيخ عبد الباسط عبد الصمد", streamUrl = "https://backup.quran.com.kw/abdulbasit"),
                RadioChannelUiState(id = 4, nameAr = "الشيخ محمد صديق المنشاوي", streamUrl = "https://backup.quran.com.kw/minshawi"),
                RadioChannelUiState(id = 5, nameAr = "الشيخ محمود خليل الحصري", streamUrl = "https://backup.quran.com.kw/hussary"),
                RadioChannelUiState(id = 6, nameAr = "إذاعة السنة النبوية", streamUrl = "https://n02.radiojar.com/97y03bt320z4tv")
            )

            _screenState.update { 
                it.copy(channels = list, isLoading = false) 
            }
        }
    }

    fun onChannelClick(channel: RadioChannelUiState) {
        _screenState.update { state ->
            state.copy(
                channels = state.channels.map { 
                    it.copy(isPlaying = it.id == channel.id) 
                }
            )
        }
    }
}

```
