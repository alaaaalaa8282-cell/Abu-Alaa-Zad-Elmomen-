package com.abueltaweel.domain.repository.radio

import com.abueltaweel.domain.entity.radio.Category
import com.abueltaweel.domain.entity.radio.RadioChannel
import kotlinx.coroutines.flow.Flow

interface RadioRepository {
    fun getAllChannels(): Flow<List<RadioChannel>>
    suspend fun getChannelById(id: Int): RadioChannel?
    suspend fun getChannelsByCategory(categoryId: String): Flow<List<RadioChannel>>
   suspend fun getCategories(): Flow<List<Category>>
}