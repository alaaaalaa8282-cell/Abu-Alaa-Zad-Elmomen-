package com.abueltaweel.domain.repository.quran

import com.abueltaweel.domain.entity.quran.Bookmark

interface BookmarkRepository {
    suspend fun addBookmark(bookmark: Bookmark)
    fun getBookmarks(): List<Bookmark>
    suspend fun removeBookmark(surahId: Int, ayahId: Int)
}