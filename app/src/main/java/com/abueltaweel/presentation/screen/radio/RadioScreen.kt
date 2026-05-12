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
    // السطر القادم كان يسبب خطأ بسبب نقص import getValue
    val screenState by viewModel.screenState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            // التعامل مع الأحداث الجانبية مثل التنبيهات
        }
    }

    RadioContent(
        state = screenState,
        onChannelClick = { channel ->
            viewModel.onChannelClick(channel)
        }
    )
}

```
