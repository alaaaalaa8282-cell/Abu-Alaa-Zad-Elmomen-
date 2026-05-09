package com.abueltaweel.domain.repository.settings

interface BatteryOptimizationRepository {
    fun getBrandInstructions(
        manufacturer: String,
        isArabic: Boolean
    ): List<String>
}