package com.abueltaweel.data.radio.repository

import com.abueltaweel.data.radio.dto.CategoryDto
import com.abueltaweel.data.radio.dto.RadioChannelDto
import com.abueltaweel.data.radio.mapper.toDomain
import com.abueltaweel.data.radio.mapper.toDomainList
import com.abueltaweel.data.util.helpers.safeCall
import com.abueltaweel.data.util.helpers.safeFlow
import com.abueltaweel.domain.entity.radio.Category
import com.abueltaweel.domain.entity.radio.RadioChannel
import com.abueltaweel.domain.repository.radio.RadioRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RadioRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : RadioRepository {

    override fun getAllChannels(): Flow<List<RadioChannel>> = flow {
        val channelsDto: List<RadioChannelDto> = supabaseClient
            .from(RADIO_CHANNELS_TABLE)
            .select()
            .decodeList<RadioChannelDto>()
        emit(channelsDto.toDomainList())
    }.safeFlow()

    override suspend fun getChannelById(id: Int) =
        safeCall {
            val dto: RadioChannelDto? = supabaseClient
                .from(RADIO_CHANNELS_TABLE)
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<RadioChannelDto>()
            dto?.toDomain()
        }
    override suspend fun getChannelsByCategory(categoryId: String): Flow<List<RadioChannel>> = flow {
        val channelsDto: List<RadioChannelDto> = supabaseClient
            .from(RADIO_CHANNELS_TABLE)
            .select {
                filter {
                    eq("category_id", categoryId)
                }
            }
            .decodeList<RadioChannelDto>()

        emit(channelsDto.toDomainList())
    }.safeFlow()

    override suspend fun getCategories(): Flow<List<Category>> = flow {
        val result = supabaseClient
            .from(CATEGORIES_TABLE)
            .select()
            .decodeList<CategoryDto>()

        emit(result.map { it.toDomain() })
    }.safeFlow()

    private companion object{
        const val RADIO_CHANNELS_TABLE = "radio_channels"
        const val CATEGORIES_TABLE = "categories"
    }
}