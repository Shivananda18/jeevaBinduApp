package com.jeevabindu.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "jeeva_bindu_auth"
)

data class UserSession(
    val uid: String,
    val phone: String
)

@Singleton
class AuthPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.authDataStore

    val sessionFlow: Flow<UserSession?> = dataStore.data.map { prefs ->
        val uid = prefs[KEY_UID] ?: return@map null
        val phone = prefs[KEY_PHONE] ?: return@map null
        UserSession(uid = uid, phone = phone)
    }

    suspend fun saveSession(uid: String, phone: String) {
        dataStore.edit { prefs ->
            prefs[KEY_UID] = uid
            prefs[KEY_PHONE] = phone
        }
    }

    suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }

    companion object {
        private val KEY_UID = stringPreferencesKey("uid")
        private val KEY_PHONE = stringPreferencesKey("phone")
    }
}
