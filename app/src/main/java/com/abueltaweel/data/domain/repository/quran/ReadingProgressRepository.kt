package com.abueltaweel.domain.repository.quran

import com.abueltaweel.domain.entity.quran.ReadingProgress
import kotlinx.coroutines.flow.Flow

interface ReadingProgressRepository {
    suspend fun save(surahId: Int, ayahId: Int)
    fun observe(): Flow<ReadingProgress?>
}