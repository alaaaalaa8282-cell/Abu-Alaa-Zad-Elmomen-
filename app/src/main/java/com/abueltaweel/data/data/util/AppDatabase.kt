package com.abueltaweel.data.util

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abueltaweel.data.quran.local.dao.BookmarkDao
import com.abueltaweel.data.quran.local.dto.BookmarkEntity

@Database(
    entities = [
        BookmarkEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao

}