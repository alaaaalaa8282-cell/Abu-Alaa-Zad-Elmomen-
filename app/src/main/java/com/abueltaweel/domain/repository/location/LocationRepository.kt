package com.abueltaweel.domain.repository.location

import com.abueltaweel.domain.entity.location.Location
import org.maplibre.android.geometry.LatLng

interface LocationRepository {
    suspend fun getLocation(): Location
    suspend fun getLocation(lat: Double, lng: Double): Location
    suspend fun getCurrentDeviceLocation(): LatLng
}