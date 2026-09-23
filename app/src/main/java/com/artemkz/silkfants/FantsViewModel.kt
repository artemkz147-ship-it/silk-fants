package com.artemkz.silkfants

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.artemkz.silkfants.data.AppScreen
import com.artemkz.silkfants.data.AppState
import com.artemkz.silkfants.data.Fantasies
import com.artemkz.silkfants.data.FantsRepository
import com.artemkz.silkfants.data.WhoPlays
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FantsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = FantsRepository(application)

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private val _screen = MutableStateFlow(AppScreen.MAIN_MENU)
    val screen: StateFlow<AppScreen> = _screen.asStateFlow()

    private val _toast = MutableStateFlow<String?>(null)
    val toast: StateFlow<String?> = _toast.asStateFlow()

    private var persistJob: Job? = null
    private var loaded = false

    init {
        viewModelScope.launch {
            repo.stateFlow.collect { saved ->
                if (!loaded) {
                    val withDeck = if (saved.deckOrder.isEmpty()) {
                        saved.copy(deckOrder = shuffledIds(saved.doneIds), deckIndex = 0)
                    } else {
                        saved
                    }
                    _state.value = withDeck
                    loaded = true
                    if (saved.deckOrder.isEmpty()) schedulePersist()
                }
            }
        }
    }

    fun navigate(to: AppScreen) {
        _screen.value = to
    }

    fun confirmAdult() {
        _state.update { it.copy(ageVerified = true) }
        _screen.value = AppScreen.MAIN_MENU
        persistNow()
    }

    fun declineAdult() {
        _toast.value = "Игра только для взрослых 18+"
    }

    fun consumeToast() {
        _toast.value = null
    }

    fun setWhoPlays(who: WhoPlays) {
        _state.update { it.copy(whoPlays = who) }
        schedulePersist()
    }

    fun setIntensityFilter(level: Int) {
        _state.update { it.copy(intensityFilter = level.coerceIn(0, 3)) }
    }

    fun ensureDeckAndShowDraw() {
        _state.update { s ->
            if (s.deckOrder.isEmpty() || s.deckIndex >= s.deckOrder.size) {
                val order = shuffledIds(s.doneIds)
                s.copy(deckOrder = order, deckIndex = 0)
            } else s
        }
        // If all done, reshuffle fresh
        val s = _state.value
        if (s.doneIds.size >= Fantasies.ALL.size) {
            resetSession(reshuffle = true)
        } else if (s.currentCard == null || s.currentCard!!.id in s.doneIds) {
            advanceToNextUndone()
        }
        _screen.value = AppScreen.DRAW
        schedulePersist()
    }

    fun markDone() {
        val card = _state.value.currentCard ?: return
        _state.update { it.copy(doneIds = it.doneIds + card.id) }
        _toast.value = "Фант выполнен ✓"
        drawAnother()
    }

    fun drawAnother() {
        advanceToNextUndone()
        val s = _state.value
        if (s.currentCard == null) {
            if (s.doneIds.size >= Fantasies.ALL.size) {
                _toast.value = "Колода закончилась! Сбросьте сессию."
            } else {
                // reshuffle remaining
                val order = shuffledIds(s.doneIds)
                _state.update { it.copy(deckOrder = order, deckIndex = 0) }
                if (_state.value.currentCard == null) {
                    _toast.value = "Нет доступных фантов"
                }
            }
        }
        schedulePersist()
    }

    fun resetSession(reshuffle: Boolean = true) {
        val order = if (reshuffle) Fantasies.ALL.map { it.id }.shuffled() else emptyList()
        _state.update {
            it.copy(
                doneIds = emptySet(),
                deckOrder = order,
                deckIndex = 0,
            )
        }
        _toast.value = "Сессия сброшена"
        schedulePersist()
    }

    private fun advanceToNextUndone() {
        _state.update { s ->
            if (s.deckOrder.isEmpty()) {
                val order = shuffledIds(s.doneIds)
                return@update s.copy(deckOrder = order, deckIndex = 0)
            }
            var idx = s.deckIndex + 1
            while (idx < s.deckOrder.size) {
                val id = s.deckOrder[idx]
                if (id !in s.doneIds) {
                    return@update s.copy(deckIndex = idx)
                }
                idx++
            }
            // try from start for any leftover
            idx = 0
            while (idx < s.deckOrder.size) {
                val id = s.deckOrder[idx]
                if (id !in s.doneIds) {
                    return@update s.copy(deckIndex = idx)
                }
                idx++
            }
            s.copy(deckIndex = s.deckOrder.size) // exhausted
        }
    }

    private fun shuffledIds(exclude: Set<Int>): List<Int> {
        val remaining = Fantasies.ALL.map { it.id }.filter { it !in exclude }
        return if (remaining.isEmpty()) Fantasies.ALL.map { it.id }.shuffled()
        else remaining.shuffled()
    }

    private fun schedulePersist() {
        persistJob?.cancel()
        persistJob = viewModelScope.launch {
            delay(250)
            persistNow()
        }
    }

    private fun persistNow() {
        val s = _state.value
        viewModelScope.launch {
            repo.saveAgeAndSession(s)
        }
    }
}
