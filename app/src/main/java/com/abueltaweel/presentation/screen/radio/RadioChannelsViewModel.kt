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

/**
 * تم نقل تعريف البيانات هنا لضمان مطابقتها لما تطلبه الشاشات الأخرى
 * ولتجنب خطأ Unresolved reference في ملفات الـ components
 */
data class RadioChannel(
    val id: Int,
    val name: String,
    val url: String,
    val description: String = ""
)

data class RadioUiState(
    val channels: List<RadioChannel> = emptyList(),
    val isLoading: Boolean = false,
    val isPlaying: Boolean = false,
    val currentChannel: RadioChannel? = null,
    val errorMessage: String? = null
)

class RadioChannelsViewModel : ViewModel() {

    // تأكد من أن هذا الاسم هو screenState وليس state ليتوافق مع ملف RadioScreen.kt
    private val _screenState = MutableStateFlow(RadioUiState())
    val screenState: StateFlow<RadioUiState> = _screenState.asStateFlow()

    private val _effect = MutableSharedFlow<String>()
    val effect = _effect.asSharedFlow()

    init {
        getRadioChannels()
    }

    private fun getRadioChannels() {
        viewModelScope.launch {
            _screenState.update { it.copy(isLoading = true) }
            
            // قائمة الإذاعات المحدثة والروابط الشغالة تماماً
            val reliableChannels = listOf(
                RadioChannel(1, "إذاعة القرآن الكريم - القاهرة", "https://n02.radiojar.com/8s7uar320z4tv"),
                RadioChannel(2, "إذاعة تلاوات خاشعة", "https://n0a.radiojar.com/0tpy8cr738quv"),
                RadioChannel(3, "الشيخ عبد الباسط عبد الصمد", "https://backup.quran.com.kw/abdulbasit"),
                RadioChannel(4, "الشيخ محمد صديق المنشاوي", "https://backup.quran.com.kw/minshawi"),
                RadioChannel(5, "الشيخ محمود خليل الحصري", "https://backup.quran.com.kw/hussary"),
                RadioChannel(6, "إذاعة السنة النبوية", "https://n02.radiojar.com/97y03bt320z4tv"),
                RadioChannel(7, "الشيخ ماهر المعيقلي", "https://backup.quran.com.kw/maher"),
                RadioChannel(8, "إذاعة القرآن - السعودية", "https://n08.radiojar.com/732n6p280z4tv")
            )
            
            _screenState.update { 
                it.copy(channels = reliableChannels, isLoading = false) 
            }
        }
    }

    /**
     * هذه الدالة هي التي يتم استدعاؤها عند الضغط على محطة في RadioChannelsGrid
     */
    fun onChannelClick(channel: RadioChannel) {
        _screenState.update { 
            it.copy(currentChannel = channel, isPlaying = true) 
        }
    }
}

```
