package com.abueltaweel.data.azkar.repository

import com.abueltaweel.data.azkar.local.AzkarLocalDataSource
import com.abueltaweel.data.azkar.local.dto.AzkarCategoryDto
import com.abueltaweel.data.azkar.mapper.toDomain
import com.abueltaweel.domain.entity.azkar.AzkarCategory
import com.abueltaweel.domain.repository.azkar.AzkarRepository

class AzkarRepositoryImpl(
    private val localDataSource: AzkarLocalDataSource
) : AzkarRepository {

    override suspend fun getAzkarCategories(): List<AzkarCategory> {
        val rawData =  localDataSource.getAzkar()
        val categories = rawData.map { (title, items) ->
            AzkarCategoryDto(title = title, items = items)
        }
        return categories.map { it.toDomain() }
    }
}