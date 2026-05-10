package com.abueltaweel.data.radio.mapper

import com.abueltaweel.data.radio.dto.CategoryDto
import com.abueltaweel.data.radio.dto.RadioChannelDto
import com.abueltaweel.domain.entity.radio.Category
import com.abueltaweel.domain.entity.radio.RadioChannel
fun RadioChannelDto.toDomain(): RadioChannel {
    return RadioChannel(
        id = this.id,
        nameAr = this.nameAr,
        nameEn = this.nameEn,
        streamUrl = this.streamUrl,
        categoryId = this.categoryId
    )
}
fun CategoryDto.toDomain() = Category(id, nameAr, nameEn)
fun List<RadioChannelDto>.toDomainList(): List<RadioChannel> = map { it.toDomain() }