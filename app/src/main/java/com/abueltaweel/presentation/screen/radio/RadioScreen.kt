```kotlin
package com.abueltaweel.presentation.screen.radio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abueltaweel.presentation.screen.radio.components.RadioContent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RadioScreen(
    viewModel: RadioChannelsViewModel = viewModel()
) {
    // تم إضافة الـ imports اللازمة لحل أخطاء التعبير 'by'
    val screenState by viewModel.screenState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            // هنا يتم استقبال أي تأثيرات جانبية
        }
    }

    // تمرير البيانات للدالة RadioContent الموجودة في مشروعك
    RadioContent(
        state = screenState,
        onChannelClick = { channel ->
            viewModel.onChannelClick(channel)
        }
    )
}

```
