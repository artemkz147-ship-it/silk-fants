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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            withContext(Dispatchers.IO) {
                Fantasies.load(getApplication())
            }
            _state.update { it.copy(deckLoaded = true) }
            repo.stateFlow.collect { saved ->
                if (!loaded) {
                    val level = saved.levelFilter.coerceIn(1, 4)
                    val withDeck = if (saved.deckOrder.isEmpty()) {
                        saved.copy(
                            levelFilter = level,
                            deckOrder = shuffledIds(level, saved.whoPlays, saved.doneIds),
                            deckIndex = 0,
                            deckLoaded = true,
                        )
                    } else {
                        saved.copy(levelFilter = level, deckLoaded = true)
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
        _state.update { s ->
            if (s.whoPlays == who) return@update s
            val order = shuffledIds(s.levelFilter, who, s.doneIds)
            s.copy(whoPlays = who, deckOrder = order, deckIndex = 0)
        }
        schedulePersist()
    }

    fun setLevelFilter(level: Int) {
        val lvl = level.coerceIn(0, 4)
        _state.update { s ->
            if (s.levelFilter == lvl) return@update s
            // 0 = «все» only for catalog; play always uses 1–4
            if (lvl == 0) {
                s.copy(levelFilter = 0)
            } else {
                val order = shuffledIds(lvl, s.whoPlays, s.doneIds)
                s.copy(levelFilter = lvl, deckOrder = order, deckIndex = 0)
            }
        }
        schedulePersist()
    }

    fun setCategoryFilter(category: String) {
        _state.update { it.copy(categoryFilter = category) }
    }

    fun ensureDeckAndShowDraw() {
        _state.update { s ->
            val level = s.levelFilter.coerceIn(1, 4)
            val base = if (s.levelFilter == 0) s.copy(levelFilter = level) else s
            if (base.deckOrder.isEmpty() || base.deckIndex >= base.deckOrder.size) {
                val order = shuffledIds(level, base.whoPlays, base.doneIds)
                base.copy(deckOrder = order, deckIndex = 0, levelFilter = level)
            } else base.copy(levelFilter = level)
        }
        val s = _state.value
        val pool = Fantasies.filtered(s.levelFilter.coerceIn(1, 4), s.whoPlays)
        if (pool.isNotEmpty() && pool.all { it.id in s.doneIds }) {
            // Exhausted filtered pool → reshuffle within level/who
            resetFilteredDeck()
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
            val pool = Fantasies.filtered(s.levelFilter.coerceIn(1, 4), s.whoPlays)
            if (pool.isEmpty()) {
                _toast.value = "Нет фантов для выбранных фильтров"
            } else if (pool.all { it.id in s.doneIds }) {
                _toast.value = "Колода уровня закончилась — перемешиваем"
                resetFilteredDeck()
            } else {
                val order = shuffledIds(s.levelFilter, s.whoPlays, s.doneIds)
                _state.update { it.copy(deckOrder = order, deckIndex = 0) }
                if (_state.value.currentCard == null) {
                    _toast.value = "Нет доступных фантов"
                }
            }
        }
        schedulePersist()
    }

    fun resetSession(reshuffle: Boolean = true) {
        val s = _state.value
        val level = s.levelFilter.coerceIn(1, 4)
        val order = if (reshuffle) {
            Fantasies.filtered(level, s.whoPlays).map { it.id }.shuffled()
        } else {
            emptyList()
        }
        _state.update {
            it.copy(
                doneIds = emptySet(),
                deckOrder = order,
                deckIndex = 0,
                levelFilter = level,
            )
        }
        _toast.value = "Сессия сброшена"
        schedulePersist()
    }

    private fun resetFilteredDeck() {
        val s = _state.value
        val level = s.levelFilter.coerceIn(1, 4)
        val order = Fantasies.filtered(level, s.whoPlays).map { it.id }.shuffled()
        _state.update {
            it.copy(doneIds = emptySet(), deckOrder = order, deckIndex = 0, levelFilter = level)
        }
    }

    private fun advanceToNextUndone() {
        _state.update { s ->
            val level = s.levelFilter.coerceIn(1, 4)
            if (s.deckOrder.isEmpty()) {
                val order = shuffledIds(level, s.whoPlays, s.doneIds)
                return@update s.copy(deckOrder = order, deckIndex = 0, levelFilter = level)
            }
            var idx = s.deckIndex + 1
            while (idx < s.deckOrder.size) {
                val id = s.deckOrder[idx]
                if (id !in s.doneIds) {
                    return@update s.copy(deckIndex = idx)
                }
                idx++
            }
            idx = 0
            while (idx < s.deckOrder.size) {
                val id = s.deckOrder[idx]
                if (id !in s.doneIds) {
                    return@update s.copy(deckIndex = idx)
                }
                idx++
            }
            s.copy(deckIndex = s.deckOrder.size)
        }
    }

    private fun shuffledIds(level: Int, who: WhoPlays, exclude: Set<String>): List<String> {
        val lvl = level.coerceIn(1, 4)
        val pool = Fantasies.filtered(lvl, who)
        val remaining = pool.map { it.id }.filter { it !in exclude }
        return if (remaining.isEmpty()) pool.map { it.id }.shuffled()
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
