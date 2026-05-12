```kotlin
package com.abueltaweel.presentation.screen.radio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abueltaweel.presentation.screen.radio.components.RadioContent

@Composable
fun RadioScreen(
    viewModel: RadioChannelsViewModel = viewModel() // تأكد من طريقة استدعاء الـ ViewModel المناسبة لمشروعك
) {
    // حل مشكلة Unresolved reference 'screenState'
    val screenState by viewModel.screenState.collectAsState()

    // حل مشكلة Unresolved reference 'effect' (إذا كنت تستخدمها للإشعارات)
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            // هنا يمكنك التعامل مع الـ effects مثل إظهار Toast
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
