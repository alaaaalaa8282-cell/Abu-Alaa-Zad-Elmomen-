```kotlin
package com.abueltaweel.presentation.screen.radio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RadioChannelsViewModel : ViewModel() {

    // تأكد من استخدام هذا الاسم ليتوافق مع RadioScreen
    private val _screenState = MutableStateFlow(RadioUiState())
    val screenState: StateFlow<RadioUiState> = _screenState.asStateFlow()

    // إضافة الـ effect لكي لا يعترض ملف RadioScreen
    private val _effect = MutableSharedFlow<String>()
    val effect = _effect.asSharedFlow()

    init {
        loadRadioChannels()
    }

    private fun loadRadioChannels() {
        viewModelScope.launch {
            _screenState.update { it.copy(isLoading = true) }
            
            val reliableChannels = listOf(
                RadioChannel(1, "إذاعة القرآن الكريم - القاهرة", "https://n02.radiojar.com/8s7uar320z4tv"),
                RadioChannel(2, "إذاعة تلاوات خاشعة", "https://n0a.radiojar.com/0tpy8cr738quv"),
                RadioChannel(3, "الشيخ عبد الباسط عبد الصمد", "https://backup.quran.com.kw/abdulbasit"),
                RadioChannel(4, "الشيخ محمد صديق المنشاوي", "https://backup.quran.com.kw/minshawi"),
                RadioChannel(5, "الشيخ محمود خليل الحصري", "https://backup.quran.com.kw/hussary"),
                RadioChannel(6, "إذاعة السنة النبوية", "https://n02.radiojar.com/97y03bt320z4tv")
            )
            
            _screenState.update { it.copy(channels = reliableChannels, isLoading = false) }
        }
    }

    fun onChannelClick(channel: RadioChannel) {
        _screenState.update { it.copy(currentChannel = channel, isPlaying = true) }
    }
}


```
