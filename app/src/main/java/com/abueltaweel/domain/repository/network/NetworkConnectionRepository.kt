package com.abueltaweel.domain.repository.network

interface NetworkConnectionRepository {
    fun isCurrentlyConnected(): Boolean
}