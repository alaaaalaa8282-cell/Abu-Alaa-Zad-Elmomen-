package com.abueltaweel.domain.repository.azkar

import com.abueltaweel.domain.entity.azkar.AzkarCategory

interface AzkarRepository {
    suspend fun getAzkarCategories(): List<AzkarCategory>
}