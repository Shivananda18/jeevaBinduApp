package com.jeevabindu.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.jeevabindu.domain.repository.ThemeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore("jeeva_bindu_theme")

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext context: Context
) : ThemeRepository {
    private val dataStore = context.themeDataStore

    override val isDarkMode: Flow<Boolean?> = dataStore.data.map { prefs ->
        prefs[DARK_MODE_KEY]
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[DARK_MODE_KEY] = enabled }
    }

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }
}
