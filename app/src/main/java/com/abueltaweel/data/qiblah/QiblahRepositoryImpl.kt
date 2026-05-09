package com.abueltaweel.data.qiblah

import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.Qibla
import com.abueltaweel.domain.entity.location.Location
import com.abueltaweel.domain.repository.qiblah.QiblahRepository

class QiblahRepositoryImpl: QiblahRepository {
    override suspend fun getQiblahDirection(location: Location): Double {
        val coordinates = Coordinates(location.latitude, location.longitude)
        val qiblaDirection = Qibla(coordinates).direction
        return qiblaDirection
    }
}