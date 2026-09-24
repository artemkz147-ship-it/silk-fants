package com.artemkz.silkfants.data

enum class AppScreen {
    MAIN_MENU,
    DRAW,
    ALL_TASKS,
    RULES,
    ABOUT,
}

enum class WhoPlays {
    PARTNER_A,
    PARTNER_B,
    BOTH,
}

data class AppState(
    val ageVerified: Boolean = false,
    val doneIds: Set<Int> = emptySet(),
    val deckOrder: List<Int> = emptyList(),
    val deckIndex: Int = 0,
    val whoPlays: WhoPlays = WhoPlays.BOTH,
    val intensityFilter: Int = 0, // 0 = all, 1–3 = filter
    val categoryFilter: String = "", // empty = all
) {
    val remainingCount: Int
        get() = Fantasies.ALL.size - doneIds.size

    val currentCard: FantasyCard?
        get() {
            if (deckOrder.isEmpty()) return null
            if (deckIndex !in deckOrder.indices) return null
            return Fantasies.byId(deckOrder[deckIndex])
        }
}
