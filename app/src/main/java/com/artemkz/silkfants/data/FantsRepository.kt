package com.artemkz.silkfants.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "silk_fants")

class FantsRepository(private val context: Context) {
    private object Keys {
        val AGE = booleanPreferencesKey("age_verified")
        val DONE_IDS = stringPreferencesKey("done_ids")
        val DECK_ORDER = stringPreferencesKey("deck_order")
        val DECK_INDEX = intPreferencesKey("deck_index")
        val WHO = stringPreferencesKey("who_plays")
    }

    val stateFlow: Flow<AppState> = context.dataStore.data.map { prefs ->
        val done = (prefs[Keys.DONE_IDS] ?: "")
            .split(',')
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
        val order = (prefs[Keys.DECK_ORDER] ?: "")
            .split(',')
            .mapNotNull { it.trim().toIntOrNull() }
        val who = when (prefs[Keys.WHO]) {
            WhoPlays.PARTNER_A.name -> WhoPlays.PARTNER_A
            WhoPlays.PARTNER_B.name -> WhoPlays.PARTNER_B
            else -> WhoPlays.BOTH
        }
        AppState(
            ageVerified = prefs[Keys.AGE] ?: false,
            doneIds = done,
            deckOrder = order,
            deckIndex = prefs[Keys.DECK_INDEX] ?: 0,
            whoPlays = who,
        )
    }

    suspend fun setAgeVerified(verified: Boolean) {
        context.dataStore.edit { it[Keys.AGE] = verified }
    }

    suspend fun saveSession(
        doneIds: Set<Int>,
        deckOrder: List<Int>,
        deckIndex: Int,
        whoPlays: WhoPlays,
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DONE_IDS] = doneIds.joinToString(",")
            prefs[Keys.DECK_ORDER] = deckOrder.joinToString(",")
            prefs[Keys.DECK_INDEX] = deckIndex
            prefs[Keys.WHO] = whoPlays.name
        }
    }

    suspend fun saveAgeAndSession(state: AppState) {
        context.dataStore.edit { prefs ->
            prefs[Keys.AGE] = state.ageVerified
            prefs[Keys.DONE_IDS] = state.doneIds.joinToString(",")
            prefs[Keys.DECK_ORDER] = state.deckOrder.joinToString(",")
            prefs[Keys.DECK_INDEX] = state.deckIndex
            prefs[Keys.WHO] = state.whoPlays.name
        }
    }
}
