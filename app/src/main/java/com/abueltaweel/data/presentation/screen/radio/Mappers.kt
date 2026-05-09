package com.abueltaweel.presentation.screen.radio

import com.abueltaweel.domain.entity.radio.Category

fun Category.toUi(): RadioUiState.CategoryUi{
    return RadioUiState.CategoryUi(
        id = this.id,
        nameAr = nameAr,
        nameEn = nameEn
    )
}