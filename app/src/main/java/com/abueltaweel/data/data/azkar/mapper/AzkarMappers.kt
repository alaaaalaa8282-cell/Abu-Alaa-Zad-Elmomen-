package com.abueltaweel.data.azkar.mapper

import com.abueltaweel.data.azkar.local.dto.AzkarCategoryDto
import com.abueltaweel.data.azkar.local.dto.AzkarItemDto
import com.abueltaweel.domain.entity.azkar.AzkarCategory
import com.abueltaweel.domain.entity.azkar.AzkarItem

fun AzkarItemDto.toDomain() = AzkarItem(
    content = content,
    count = count,
    description = description.orEmpty(),
    reference = reference.orEmpty()
)

fun AzkarCategoryDto.toDomain() = AzkarCategory(
    title = title,
    items = items.map { it.toDomain() }
)