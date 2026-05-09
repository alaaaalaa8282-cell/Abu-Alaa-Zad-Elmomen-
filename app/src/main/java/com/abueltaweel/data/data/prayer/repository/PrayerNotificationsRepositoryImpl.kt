package com.abueltaweel.data.prayer.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.abueltaweel.data.settings.SettingsKeys
import com.abueltaweel.domain.entity.prayer.Prayer
import com.abueltaweel.domain.repository.prayer.PrayerNotificationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PrayerNotificationsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : PrayerNotificationsRepository {

    override suspend fun setPrayerEnabled(
        prayer: Prayer.PrayerName,
        enabled: Boolean
    ) {
        dataStore.edit {
            it[SettingsKeys.prayerKey(prayer)] = enabled
        }
    }

    override fun observePrayerEnabled(
        prayer: Prayer.PrayerName
    ): Flow<Boolean> =
        dataStore.data.map {
            it[SettingsKeys.prayerKey(prayer)] ?: false
        }

    override fun observeAll(): Flow<Map<Prayer.PrayerName, Boolean>> =
        dataStore.data.map { prefs ->
            val map = Prayer.PrayerName.entries.associateWith { prayer ->
                prefs[SettingsKeys.prayerKey(prayer)] ?: false
            }
            map
        }
}