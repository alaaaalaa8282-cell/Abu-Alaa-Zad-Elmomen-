package com.abueltaweel.presentation.screen.radio.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abueltaweel.R
import com.abueltaweel.design_system.component.AppBar
import com.abueltaweel.design_system.component.PrimaryToast
import com.abueltaweel.design_system.component.ToastDetails
import com.abueltaweel.design_system.theme.Theme
import com.abueltaweel.presentation.base.localizedString
import com.abueltaweel.presentation.screen.radio.RadioChannelsViewModel
import com.abueltaweel.presentation.screen.radio.RadioUiState

@Composable
fun RadioContent(
    state: RadioUiState,
    viewModel: RadioChannelsViewModel,
    toast: ToastDetails?
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppBar(
                title = localizedString(R.string.quran_radio),
                isBackEnabled = false,
                onBackClick = {}
            )
            RadioChannelsGrid(state, viewModel)
        }

        toast?.let {
            PrimaryToast(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp),
                data = it,
                isSuccess = false
            )
        }
    }
}
