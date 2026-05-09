package com.abueltaweel.domain.entity.azkar

import com.abueltaweel.domain.entity.azkar.AzkarItem

data class AzkarCategory(
    val title: String,
    val items: List<AzkarItem>
)