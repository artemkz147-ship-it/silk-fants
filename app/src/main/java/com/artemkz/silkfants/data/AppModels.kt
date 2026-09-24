package com.artemkz.silkfants.data

enum class AppScreen {
    MAIN_MENU,
    DRAW,
    ALL_TASKS,
    RULES,
    ABOUT,
}

enum class WhoPlays {
    PARTNER_A, // Мужчина
    PARTNER_B, // Женщина
    BOTH,
}

data class AppState(
    val ageVerified: Boolean = false,
    val doneIds: Set<String> = emptySet(),
    val deckOrder: List<String> = emptyList(),
    val deckIndex: Int = 0,
    val whoPlays: WhoPlays = WhoPlays.BOTH,
    /** Play / catalog level 1–4; 0 in catalog means «all». Default play level = 1. */
    val levelFilter: Int = 1,
    val categoryFilter: String = "", // empty = all
    val deckLoaded: Boolean = false,
) {
    val remainingCount: Int
        get() {
            if (!Fantasies.isLoaded()) return 0
            val pool = Fantasies.filtered(levelFilter.coerceIn(1, 4), whoPlays)
            return pool.count { it.id !in doneIds }
        }

    val currentCard: FantasyCard?
        get() {
            if (deckOrder.isEmpty()) return null
            if (deckIndex !in deckOrder.indices) return null
            return Fantasies.byId(deckOrder[deckIndex])
        }
}
