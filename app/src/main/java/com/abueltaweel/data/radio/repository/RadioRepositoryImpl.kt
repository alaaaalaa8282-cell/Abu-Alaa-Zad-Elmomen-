package com.abueltaweel.data.radio.repository

import com.abueltaweel.domain.entity.radio.Category
import com.abueltaweel.domain.entity.radio.RadioChannel
import com.abueltaweel.domain.repository.radio.RadioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RadioRepositoryImpl : RadioRepository {

    override fun getAllChannels(): Flow<List<RadioChannel>> = flow {
        emit(allChannels)
    }

    override suspend fun getChannelById(id: Int): RadioChannel? =
        allChannels.firstOrNull { it.id == id }

    override suspend fun getChannelsByCategory(categoryId: String): Flow<List<RadioChannel>> = flow {
        emit(allChannels.filter { it.categoryId == categoryId })
    }

    override suspend fun getCategories(): Flow<List<Category>> = flow {
        emit(allCategories)
    }

    companion object {
        val allCategories = listOf(
            Category(id = "1", nameAr = "قرآن كريم",   nameEn = "Quran"),
            Category(id = "2", nameAr = "إذاعات إسلامية", nameEn = "Islamic"),
            Category(id = "3", nameAr = "أناشيد",      nameEn = "Nasheeds")
        )

        val allChannels = listOf(
            // قرآن كريم
            RadioChannel(id = 1,  categoryId = "1", nameAr = "إذاعة القرآن الكريم السعودية",    nameEn = "Saudi Quran",      streamUrl = "https://Quraan.msaBroadcasting.com/quran"),
            RadioChannel(id = 2,  categoryId = "1", nameAr = "إذاعة القرآن الكريم المصرية",     nameEn = "Egypt Quran",      streamUrl = "https://n04.radiojar.com/8s5u5tpdtwzuv"),
            RadioChannel(id = 3,  categoryId = "1", nameAr = "إذاعة القرآن الكريم من مكة",      nameEn = "Makkah Quran",     streamUrl = "https://stream.radiojar.com/0tpy1h0kxtzuv"),
            RadioChannel(id = 4,  categoryId = "1", nameAr = "إذاعة القرآن الكريم المغربية",    nameEn = "Morocco Quran",    streamUrl = "https://streaming.radio.co/s2882dd558/listen"),
            RadioChannel(id = 5,  categoryId = "1", nameAr = "إذاعة القرآن الكريم الكويتية",    nameEn = "Kuwait Quran",     streamUrl = "https://n04.radiojar.com/kwtv-quran.mp3"),
            RadioChannel(id = 6,  categoryId = "1", nameAr = "إذاعة القرآن الكريم الليبية",     nameEn = "Libya Quran",      streamUrl = "https://stream.zeno.fm/0r0xa792kwzuv"),

            // إذاعات إسلامية
            RadioChannel(id = 7,  categoryId = "2", nameAr = "إذاعة نور الإسلام",               nameEn = "Nour Al-Islam",    streamUrl = "https://stream.zeno.fm/yn65m6u0h5zuv"),
            RadioChannel(id = 8,  categoryId = "2", nameAr = "راديو القرآن والسنة",              nameEn = "Quran & Sunnah",   streamUrl = "https://stream.zeno.fm/6tgkpqn2p3zuv"),
            RadioChannel(id = 9,  categoryId = "2", nameAr = "إذاعة الرحمة",                    nameEn = "Al-Rahma Radio",   streamUrl = "https://stream.zeno.fm/vqtdn01v3mzuv"),
            RadioChannel(id = 10, categoryId = "2", nameAr = "راديو المجد الإسلامي",             nameEn = "Al-Majd Islamic",  streamUrl = "https://stream.zeno.fm/4d7x19h3kwzuv"),

            // أناشيد
            RadioChannel(id = 11, categoryId = "3", nameAr = "إذاعة الأناشيد الإسلامية",        nameEn = "Islamic Nasheeds", streamUrl = "https://stream.zeno.fm/2tqhfn4q5mzuv"),
            RadioChannel(id = 12, categoryId = "3", nameAr = "راديو مشاري العفاسي",              nameEn = "Mishary Alafasy",  streamUrl = "https://stream.zeno.fm/0r5b0aedyzzuv")
        )
    }
}
