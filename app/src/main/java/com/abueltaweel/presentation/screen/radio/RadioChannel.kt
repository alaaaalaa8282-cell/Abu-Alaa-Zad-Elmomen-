package com.abueltaweel.domain.entity.radio
data class RadioChannel(
    val id: Int,
    val nameAr: String,
    val nameEn: String,
    val streamUrl: String,
    val categoryId: String
)