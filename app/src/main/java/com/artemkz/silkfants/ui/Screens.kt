package com.artemkz.silkfants.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artemkz.silkfants.data.AppState
import com.artemkz.silkfants.data.Fantasies
import com.artemkz.silkfants.data.FantasyCard
import com.artemkz.silkfants.data.WhoPlays

internal val BgTop = Color(0xFF1A0A14)
internal val BgBottom = Color(0xFF2D1528)
internal val Accent = Color(0xFFE85A8C)
internal val AccentDark = Color(0xFFAD1457)
internal val TextSoft = Color(0xFFFFE4EC)
internal val CardBg = Color(0xFF3D1F32)
internal val Gold = Color(0xFFFFD54F)

private val LEVEL_CHIPS = listOf(
    1 to "Флирт",
    2 to "Петтинг",
    3 to "Жар",
    4 to "Максимум",
)

@Composable
fun SilkGradientBg(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgTop, BgBottom, Color(0xFF3A1830))))
    ) { content() }
}

@Composable
fun AgeGateScreen(
    onConfirm: () -> Unit,
    onDecline: () -> Unit,
) {
    SilkGradientBg {
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp),
            ) {
                Column(
                    Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("18+", fontSize = 56.sp, fontWeight = FontWeight.Bold, color = Accent)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Шёлковые фанты",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSoft,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Это приложение содержит эротический контент " +
                            "для взрослых. Только для пары мужчина + женщина.\n\n" +
                            "Подтвердите, что вам исполнилось 18 лет.",
                        color = TextSoft.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                    )
                    Spacer(Modifier.height(28.dp))
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentDark),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text("Мне есть 18 лет — войти", fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text("Мне нет 18 — выйти", color = TextSoft)
                    }
                }
            }
        }
    }
}

@Composable
fun MainMenuScreen(
    state: AppState,
    toast: String?,
    onDraw: () -> Unit,
    onAllTasks: () -> Unit,
    onRules: () -> Unit,
    onAbout: () -> Unit,
    onResetSession: () -> Unit,
    onConsumeToast: () -> Unit,
) {
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(toast) {
        toast?.let { snackbar.showSnackbar(it); onConsumeToast() }
    }
    val total = if (Fantasies.isLoaded()) Fantasies.ALL.size else 0
    Scaffold(snackbarHost = { SnackbarHost(snackbar) }, containerColor = BgTop) { pad ->
        SilkGradientBg(Modifier.padding(pad)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(36.dp))
                Text(
                    "Шёлковые фанты",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSoft,
                    letterSpacing = 1.sp,
                )
                Text("Silk Fants", color = Accent, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "v0.3.0 · 18+ · М+Ж · CoupleDares",
                    color = TextSoft.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Выполнено: ${state.doneIds.size} / $total",
                    color = TextSoft.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                )
                Spacer(Modifier.height(28.dp))

                MenuButton("Тянуть фант", Icons.Default.Casino, onDraw, primary = true)
                Spacer(Modifier.height(12.dp))
                MenuButton("Все задания", Icons.Default.List, onAllTasks)
                Spacer(Modifier.height(12.dp))
                MenuButton("Правила", Icons.Default.MenuBook, onRules)
                Spacer(Modifier.height(12.dp))
                MenuButton("О игре", Icons.Default.Info, onAbout)
                Spacer(Modifier.height(20.dp))
                OutlinedButton(
                    onClick = onResetSession,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = TextSoft)
                    Spacer(Modifier.width(8.dp))
                    Text("Сбросить сессию", color = TextSoft)
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MenuButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    primary: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (primary) AccentDark else Color(0xFF3D2433),
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(10.dp))
        Text(label, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun DrawScreen(
    state: AppState,
    toast: String?,
    onWho: (WhoPlays) -> Unit,
    onLevel: (Int) -> Unit,
    onDone: () -> Unit,
    onAnother: () -> Unit,
    onBack: () -> Unit,
    onConsumeToast: () -> Unit,
) {
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(toast) {
        toast?.let { snackbar.showSnackbar(it); onConsumeToast() }
    }
    val card = state.currentCard
    val playLevel = state.levelFilter.coerceIn(1, 4)
    val poolSize = if (Fantasies.isLoaded()) {
        Fantasies.filtered(playLevel, state.whoPlays).size
    } else 0
    val doneInPool = if (Fantasies.isLoaded()) {
        Fantasies.filtered(playLevel, state.whoPlays).count { it.id in state.doneIds }
    } else 0

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }, containerColor = BgTop) { pad ->
        SilkGradientBg(Modifier.padding(pad)) {
            Column(Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = TextSoft,
                        )
                    }
                    Text(
                        "Тянем фант",
                        color = TextSoft,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        "$doneInPool/$poolSize",
                        color = TextSoft.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                    )
                }

                Text(
                    "Уровень",
                    color = TextSoft.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    LEVEL_CHIPS.forEach { (lvl, name) ->
                        WhoChip(name, playLevel == lvl) { onLevel(lvl) }
                    }
                }

                Text(
                    "Кто выполняет",
                    color = TextSoft.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                )
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    WhoChip("Мужчина", state.whoPlays == WhoPlays.PARTNER_A) {
                        onWho(WhoPlays.PARTNER_A)
                    }
                    WhoChip("Женщина", state.whoPlays == WhoPlays.PARTNER_B) {
                        onWho(WhoPlays.PARTNER_B)
                    }
                    WhoChip("Для вас двоих", state.whoPlays == WhoPlays.BOTH) {
                        onWho(WhoPlays.BOTH)
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (card == null) {
                    Box(
                        Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Колода закончилась",
                                color = TextSoft,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Смените уровень / кто играет или сбросьте сессию",
                                color = TextSoft.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                } else {
                    FantasyCardView(
                        card = card,
                        who = state.whoPlays,
                        whoLabel = whoLabel(state.whoPlays),
                        levelName = Fantasies.levelName(card.level),
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = onDone,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentDark),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text("Выполнено", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = onAnother,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text("Другой фант", color = TextSoft)
                    }
                }
            }
        }
    }
}

@Composable
private fun WhoChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 11.sp, maxLines = 1) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = AccentDark,
            selectedLabelColor = Color.White,
            containerColor = Color(0xFF3D2433),
            labelColor = TextSoft,
        ),
    )
}

private fun whoLabel(who: WhoPlays): String = when (who) {
    WhoPlays.PARTNER_A -> "Мужчина"
    WhoPlays.PARTNER_B -> "Женщина"
    WhoPlays.BOTH -> "Для вас двоих"
}

/**
 * Single-card play view: NO verticalScroll. Illustration is compact;
 * body text auto-shrinks so the full task fits on one screen.
 */
@Composable
fun FantasyCardView(
    card: FantasyCard,
    who: WhoPlays,
    whoLabel: String,
    levelName: String,
    modifier: Modifier = Modifier,
) {
    val body = remember(card.id, who) { card.resolvedText(who) }
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(6.dp),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            FantIllustration(
                card = card,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.32f),
            )
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "$levelName · ${card.category}",
                    color = Accent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                IntensityBadge(card.intensity)
            }
            Spacer(Modifier.height(2.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    whoLabel,
                    color = TextSoft.copy(alpha = 0.55f),
                    fontSize = 11.sp,
                )
                if (card.seconds != 0) {
                    Text(
                        "до сигнала таймера",
                        color = Gold.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                card.title,
                color = TextSoft,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(6.dp))
            AutoSizeText(
                text = body,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                color = TextSoft.copy(alpha = 0.92f),
                maxSp = 16f,
                minSp = 11f,
            )
        }
    }
}

/**
 * Shrinks font from [maxSp] down to [minSp] until the full text fits
 * the available height. Never scrolls; clips only if still too long at min.
 */
@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color,
    maxSp: Float = 16f,
    minSp: Float = 11f,
) {
    BoxWithConstraints(modifier) {
        val measurer = rememberTextMeasurer()
        val maxW = constraints.maxWidth
        val maxH = constraints.maxHeight

        val fontSp = remember(text, maxW, maxH) {
            if (maxW <= 0 || maxH <= 0) return@remember minSp
            var size = maxSp
            while (size > minSp) {
                val result = measurer.measure(
                    text = AnnotatedString(text),
                    style = TextStyle(
                        fontSize = size.sp,
                        lineHeight = (size * 1.35f).sp,
                    ),
                    constraints = Constraints(maxWidth = maxW),
                    softWrap = true,
                    overflow = TextOverflow.Clip,
                )
                if (result.size.height <= maxH) break
                size -= 0.5f
            }
            size.coerceAtLeast(minSp)
        }

        Text(
            text = text,
            color = color,
            fontSize = fontSp.sp,
            lineHeight = (fontSp * 1.35f).sp,
            overflow = TextOverflow.Clip,
            softWrap = true,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun FantIllustration(card: FantasyCard, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val name = "fant_%02d".format(card.artIndex)
    val resId = remember(card.id) {
        context.resources.getIdentifier(name, "drawable", context.packageName)
    }
    Box(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Accent.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (resId != 0) {
            Image(
                painter = painterResource(resId),
                contentDescription = card.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            PlaceholderGradient(card = card, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun PlaceholderGradient(card: FantasyCard, modifier: Modifier = Modifier) {
    val emoji = categoryEmoji(card.category)
    val colors = when (card.intensity) {
        1 -> listOf(Color(0xFF4A2040), Color(0xFF2D1528), Color(0xFFAD1457))
        2 -> listOf(Color(0xFF5C1A3A), Color(0xFF3A1830), Color(0xFFE85A8C))
        3 -> listOf(Color(0xFF6B1538), Color(0xFF4A1028), Color(0xFFFF6B9D))
        else -> listOf(Color(0xFF4A2060), Color(0xFF2A1040), Color(0xFFA95CFF))
    }
    Box(
        modifier.background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 40.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                card.category,
                color = TextSoft.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun categoryEmoji(category: String): String = when {
    category.contains("Поцел", ignoreCase = true) -> "💋"
    category.contains("Шёпот") || category.contains("Шепот") -> "👄"
    category.contains("Массаж") -> "💆"
    category.contains("Раздев") || category.contains("Стрип") -> "👗"
    category.contains("Дразн") || category.contains("Флирт") -> "🔥"
    category.contains("Роль") -> "🎭"
    category.contains("БДСМ") || category.contains("Шлеп") || category.contains("Повяз") -> "⛓️"
    category.contains("Игруш") || category.contains("Вибратор") -> "✨"
    category.contains("Минет") || category.contains("Куни") || category.contains("Оральн") ||
        category.contains("Римминг") -> "👅"
    category.contains("Проник") || category.contains("Пальц") || category.contains("Анальн") -> "🌶️"
    category.contains("Танец") || category.contains("Объят") -> "💃"
    else -> "🌹"
}

@Composable
private fun IntensityBadge(level: Int) {
    val label = when (level) {
        1 -> "♥ Флирт"
        2 -> "♥♥ Петтинг"
        3 -> "♥♥♥ Жар"
        else -> "♥♥♥♥ Макс"
    }
    Text(
        label,
        color = Gold,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(Color(0x33FFD54F), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Composable
fun AllTasksScreen(
    state: AppState,
    onFilter: (Int) -> Unit,
    onCategoryFilter: (String) -> Unit,
    onBack: () -> Unit,
) {
    val filtered = remember(state.levelFilter, state.categoryFilter, state.deckLoaded) {
        if (!Fantasies.isLoaded()) emptyList()
        else Fantasies.ALL.filter { card ->
            (state.levelFilter == 0 || card.level == state.levelFilter) &&
                (state.categoryFilter.isEmpty() || card.category == state.categoryFilter)
        }
    }
    Scaffold(containerColor = BgTop) { pad ->
        SilkGradientBg(Modifier.padding(pad)) {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = TextSoft,
                        )
                    }
                    Text(
                        "Все задания (${filtered.size})",
                        color = TextSoft,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(
                    "Уровень",
                    color = TextSoft.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 4.dp),
                ) {
                    item {
                        FilterChip(
                            selected = state.levelFilter == 0,
                            onClick = { onFilter(0) },
                            label = { Text("Все") },
                            colors = catalogChipColors(),
                        )
                    }
                    items(LEVEL_CHIPS) { (lvl, name) ->
                        FilterChip(
                            selected = state.levelFilter == lvl,
                            onClick = { onFilter(lvl) },
                            label = { Text(name) },
                            colors = catalogChipColors(),
                        )
                    }
                }
                Text(
                    "Категории",
                    color = TextSoft.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp),
                ) {
                    item {
                        FilterChip(
                            selected = state.categoryFilter.isEmpty(),
                            onClick = { onCategoryFilter("") },
                            label = { Text("Все") },
                            colors = catalogChipColors(),
                        )
                    }
                    items(Fantasies.categories) { cat ->
                        FilterChip(
                            selected = state.categoryFilter == cat,
                            onClick = { onCategoryFilter(cat) },
                            label = { Text("${categoryEmoji(cat)} $cat") },
                            colors = catalogChipColors(),
                        )
                    }
                }
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(filtered, key = { it.id }) { card ->
                        TaskListItem(card = card, done = card.id in state.doneIds)
                    }
                }
            }
        }
    }
}

@Composable
private fun catalogChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = AccentDark,
    selectedLabelColor = Color.White,
    containerColor = Color(0xFF3D2433),
    labelColor = TextSoft,
)

@Composable
private fun TaskListItem(card: FantasyCard, done: Boolean) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (done) Color(0xFF2A1A28) else Color(0xFF3D2433),
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(categoryEmoji(card.category), fontSize = 28.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "${card.id} · ${card.category}",
                    color = TextSoft.copy(alpha = if (done) 0.5f else 1f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${Fantasies.levelName(card.level)} · ${card.textTemplate}",
                    color = TextSoft.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "♥".repeat(card.intensity.coerceIn(1, 4)),
                color = Gold,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
fun RulesScreen(onBack: () -> Unit) {
    Scaffold(containerColor = BgTop) { pad ->
        SilkGradientBg(Modifier.padding(pad)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = TextSoft,
                        )
                    }
                    Text(
                        "Правила",
                        color = TextSoft,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.height(12.dp))
                RulesCard(
                    title = "Согласие",
                    body = "Играйте только по взаимному согласию. В любой момент можно отказаться " +
                        "от задания без объяснений.",
                )
                RulesCard(
                    title = "Стоп-слово",
                    body = "Договоритесь о стоп-слове заранее. Услышав его — немедленно остановитесь " +
                        "и проверьте, всё ли в порядке.",
                )
                RulesCard(
                    title = "4 уровня CoupleDares",
                    body = "Флирт → Петтинг → Жар → Максимум. Уровни 3–4 включают проникновения " +
                        "и острые практики — выбирайте уровень осознанно. Задания с таймером " +
                        "заканчиваются по сигналу.",
                )
                RulesCard(
                    title = "Только 18+",
                    body = "Приложение только для взрослых и только для пары мужчина + женщина. " +
                        "Не делитесь экраном с несовершеннолетними.",
                )
                RulesCard(
                    title = "Как играть",
                    body = "Выберите уровень и кто выполняет, тяните фант, отмечайте «Выполнено» " +
                        "или берите другой. Колода не повторяет карточки выбранного пула, " +
                        "пока не закончится или пока вы не сбросите сессию.",
                )
            }
        }
    }
}

@Composable
private fun RulesCard(title: String, body: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, color = Accent, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(6.dp))
            Text(body, color = TextSoft.copy(alpha = 0.9f), fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(containerColor = BgTop) { pad ->
        SilkGradientBg(Modifier.padding(pad)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = TextSoft,
                        )
                    }
                    Text(
                        "О игре",
                        color = TextSoft,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.height(24.dp))
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Accent, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(12.dp))
                Text(
                    "Шёлковые фанты",
                    color = TextSoft,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text("Silk Fants · v0.3.0", color = Accent, fontSize = 14.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    "Карточная игра фантазий для пары мужчина + женщина.\n" +
                        "Колода CoupleDares: 520 заданий, 4 уровня " +
                        "(Флирт, Петтинг, Жар, Максимум).\n\n" +
                        "Package: com.artemkz.silkfants\n" +
                        "Kotlin · Jetpack Compose · офлайн\n\n" +
                        "Контент 18+. Личный проект.",
                    color = TextSoft.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                )
            }
        }
    }
}
