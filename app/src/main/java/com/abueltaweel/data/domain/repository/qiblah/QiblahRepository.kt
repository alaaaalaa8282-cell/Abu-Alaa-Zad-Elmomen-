package com.abueltaweel.domain.repository.qiblah

import com.abueltaweel.domain.entity.location.Location

interface QiblahRepository {
    suspend fun getQiblahDirection(location: Location): Double
}