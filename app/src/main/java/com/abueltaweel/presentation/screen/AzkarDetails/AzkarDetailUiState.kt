package com.abueltaweel.presentation.screen.AzkarDetails

import com.abueltaweel.domain.entity.azkar.AzkarItem

data class AzkarDetailUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val items: List<AzkarItem> = emptyList()
)