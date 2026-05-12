package com.abueltaweel.presentation.screen.radio.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abueltaweel.design_system.theme.Theme
import com.abueltaweel.presentation.components.LoadingContainer
import com.abueltaweel.presentation.screen.radio.RadioChannelsViewModel
import com.abueltaweel.presentation.screen.radio.RadioUiState

@Composable
fun RadioChannelsGrid(
    state: RadioUiState,
    viewModel: RadioChannelsViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState: LazyGridState = rememberLazyGridState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        if (state.isLoading) {
            LoadingContainer(Modifier.align(Alignment.Center))
        } else {
            LazyVerticalGrid(
                state = scrollState,
                modifier = modifier.fillMaxSize(),
                columns = GridCells.Adaptive(320.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(state.channels, key = { it.id }) {
                    RadioReciterItem(state = it, listener = viewModel)
                }
            }
        }
    }
}
