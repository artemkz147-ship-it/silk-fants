package com.artemkz.silkfants.data

import android.content.Context
import org.json.JSONObject

data class LevelInfo(
    val id: Int,
    val name: String,
    val subtitle: String,
    val c1: String = "",
    val c2: String = "",
    val c3: String = "",
)

data class FantasyCard(
    val id: String,
    val level: Int,
    val actorSex: String,
    val category: String,
    val textTemplate: String,
    val seconds: Int,
    val item: String = "",
) {
    /** Intensity maps 1:1 from CoupleDares level (Флирт…Максимум). */
    val intensity: Int get() = level

    val title: String get() = category

    /** Stable mapping onto fant_01…fant_50.webp. */
    val artIndex: Int
        get() {
            val h = id.hashCode().and(0x7fffffff)
            return (h % 50) + 1
        }

    fun resolvedText(who: WhoPlays): String =
        Placeholder.substitute(textTemplate, actorSex, who)
}

object Placeholder {
    private val FEMALE = mapOf(
        "p_nom" to "партнёрша",
        "p_gen" to "партнёрши",
        "p_dat" to "партнёрше",
        "p_acc" to "партнёршу",
        "p_ins" to "партнёршей",
    )
    private val MALE = mapOf(
        "p_nom" to "партнёр",
        "p_gen" to "партнёра",
        "p_dat" to "партнёру",
        "p_acc" to "партнёра",
        "p_ins" to "партнёром",
    )
    private val NEUTRAL = mapOf(
        "p_nom" to "партнёр",
        "p_gen" to "партнёра",
        "p_dat" to "партнёру",
        "p_acc" to "партнёра",
        "p_ins" to "партнёром",
    )

    fun partnerForms(actorSex: String, who: WhoPlays): Map<String, String> {
        val effectiveActor = when (actorSex) {
            "M" -> "M"
            "F" -> "F"
            else -> when (who) {
                WhoPlays.PARTNER_A -> "M" // Мужчина → partner female
                WhoPlays.PARTNER_B -> "F" // Женщина → partner male
                WhoPlays.BOTH -> "U"
            }
        }
        return when (effectiveActor) {
            "M" -> FEMALE
            "F" -> MALE
            else -> NEUTRAL
        }
    }

    fun substitute(template: String, actorSex: String, who: WhoPlays): String {
        val forms = partnerForms(actorSex, who)
        var out = template
        for ((key, value) in forms) {
            out = out.replace("{$key}", value)
        }
        return out
    }
}

/**
 * Runtime loader for CoupleDares tasks.json (520 tasks, 4 levels).
 * Call [load] once from Application / ViewModel before reading [ALL].
 */
object Fantasies {
    @Volatile
    private var loaded = false

    private var _levels: List<LevelInfo> = emptyList()
    private var _all: List<FantasyCard> = emptyList()
    private var byIdMap: Map<String, FantasyCard> = emptyMap()

    val levels: List<LevelInfo> get() = _levels
    val ALL: List<FantasyCard> get() = _all
    val categories: List<String> get() = _all.map { it.category }.distinct().sorted()

    fun isLoaded(): Boolean = loaded

    fun byId(id: String): FantasyCard? = byIdMap[id]

    fun levelName(level: Int): String =
        _levels.find { it.id == level }?.name ?: "Уровень $level"

    @Synchronized
    fun load(context: Context) {
        if (loaded) return
        val json = context.assets.open("tasks.json").bufferedReader().use { it.readText() }
        val root = JSONObject(json)

        val levelArr = root.getJSONArray("levels")
        val levels = ArrayList<LevelInfo>(levelArr.length())
        for (i in 0 until levelArr.length()) {
            val o = levelArr.getJSONObject(i)
            levels.add(
                LevelInfo(
                    id = o.getInt("id"),
                    name = o.getString("name"),
                    subtitle = o.optString("subtitle", ""),
                    c1 = o.optString("c1", ""),
                    c2 = o.optString("c2", ""),
                    c3 = o.optString("c3", ""),
                ),
            )
        }

        val taskArr = root.getJSONArray("tasks")
        val tasks = ArrayList<FantasyCard>(taskArr.length())
        for (i in 0 until taskArr.length()) {
            val o = taskArr.getJSONObject(i)
            tasks.add(
                FantasyCard(
                    id = o.getString("id"),
                    level = o.getInt("level"),
                    actorSex = o.getString("actorSex"),
                    category = o.getString("category"),
                    textTemplate = o.getString("text"),
                    seconds = o.optInt("seconds", 0),
                    item = o.optString("item", ""),
                ),
            )
        }

        _levels = levels
        _all = tasks
        byIdMap = tasks.associateBy { it.id }
        loaded = true
    }

    /** Filter by level and who-plays chip. */
    fun filtered(level: Int, who: WhoPlays): List<FantasyCard> {
        val inLevel = _all.filter { it.level == level }
        return when (who) {
            WhoPlays.PARTNER_A -> inLevel.filter { it.actorSex == "M" || it.actorSex == "U" }
            WhoPlays.PARTNER_B -> inLevel.filter { it.actorSex == "F" || it.actorSex == "U" }
            WhoPlays.BOTH -> {
                val uOnly = inLevel.filter { it.actorSex == "U" }
                if (uOnly.isNotEmpty()) uOnly else inLevel
            }
        }
    }
}
