package com.abueltaweel.data.quran.repository

import com.abueltaweel.data.quran.local.dao.BookmarkDao
import com.abueltaweel.data.quran.mappers.toDomain
import com.abueltaweel.data.quran.mappers.toEntity
import com.abueltaweel.domain.entity.quran.Bookmark
import com.abueltaweel.domain.repository.quran.BookmarkRepository

class BookmarkRepositoryImpl(
    private val dao: BookmarkDao
) : BookmarkRepository {

    override suspend fun addBookmark(bookmark: Bookmark) {
        dao.insertBookmark(bookmark.toEntity())
    }

    override fun getBookmarks():List<Bookmark> {
        return dao.getAllBookmarks().map { list ->
            list.toDomain()
        }
    }

    override suspend fun removeBookmark(surahId: Int, ayahId: Int) {
       dao.deleteBookmark(surahId, ayahId)
    }
}