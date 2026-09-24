package com.artemkz.silkfants.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
                            "для взрослых (игры фантазий для пар).\n\n" +
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
                    "v0.2.0 · 18+",
                    color = TextSoft.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Выполнено: ${state.doneIds.size} / ${Fantasies.ALL.size}",
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
    Scaffold(snackbarHost = { SnackbarHost(snackbar) }, containerColor = BgTop) { pad ->
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
                        "Тянем фант",
                        color = TextSoft,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        "${state.doneIds.size}/${Fantasies.ALL.size}",
                        color = TextSoft.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                    )
                }

                Text(
                    "Кто выполняет",
                    color = TextSoft.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    WhoChip("Партнёр А", state.whoPlays == WhoPlays.PARTNER_A) {
                        onWho(WhoPlays.PARTNER_A)
                    }
                    WhoChip("Партнёр Б", state.whoPlays == WhoPlays.PARTNER_B) {
                        onWho(WhoPlays.PARTNER_B)
                    }
                    WhoChip("Для вас двоих", state.whoPlays == WhoPlays.BOTH) {
                        onWho(WhoPlays.BOTH)
                    }
                }

                Spacer(Modifier.height(12.dp))

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
                                "Сбросьте сессию в меню, чтобы начать заново",
                                color = TextSoft.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                } else {
                    FantasyCardView(
                        card = card,
                        whoLabel = whoLabel(state.whoPlays),
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onDone,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentDark),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text("Выполнено", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onAnother,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
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
        label = { Text(label, fontSize = 12.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = AccentDark,
            selectedLabelColor = Color.White,
            containerColor = Color(0xFF3D2433),
            labelColor = TextSoft,
        ),
    )
}

private fun whoLabel(who: WhoPlays): String = when (who) {
    WhoPlays.PARTNER_A -> "Партнёр А"
    WhoPlays.PARTNER_B -> "Партнёр Б"
    WhoPlays.BOTH -> "Для вас двоих"
}

@Composable
fun FantasyCardView(
    card: FantasyCard,
    whoLabel: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            FantIllustration(card = card, modifier = Modifier.fillMaxWidth().aspectRatio(1.1f))
            Spacer(Modifier.height(14.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    card.category,
                    color = Accent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
                IntensityBadge(card.intensity)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                whoLabel,
                color = TextSoft.copy(alpha = 0.55f),
                fontSize = 12.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                card.title,
                color = TextSoft,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                card.text,
                color = TextSoft.copy(alpha = 0.9f),
                fontSize = 16.sp,
                lineHeight = 24.sp,
            )
        }
    }
}

@Composable
fun FantIllustration(card: FantasyCard, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val name = "fant_%02d".format(card.id)
    val resId = remember(card.id) {
        context.resources.getIdentifier(name, "drawable", context.packageName)
    }
    Box(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Accent.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
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
        else -> listOf(Color(0xFF6B1538), Color(0xFF4A1028), Color(0xFFFF6B9D))
    }
    Box(
        modifier.background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 56.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                card.category,
                color = TextSoft.copy(alpha = 0.85f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                "№ ${card.id}",
                color = TextSoft.copy(alpha = 0.5f),
                fontSize = 12.sp,
            )
        }
    }
}

private fun categoryEmoji(category: String): String = when (category) {
    "Поцелуи" -> "💋"
    "Оральные" -> "👅"
    "Массаж" -> "💆"
    "Раздевание" -> "👗"
    "Дразнилки" -> "🔥"
    "Ролевая" -> "🎭"
    "БДСМ-лайт" -> "⛓️"
    "Игрушки" -> "✨"
    else -> "🌹"
}

@Composable
private fun IntensityBadge(level: Int) {
    val label = when (level) {
        1 -> "♥ мягко"
        2 -> "♥♥ средне"
        else -> "♥♥♥ горячо"
    }
    Text(
        label,
        color = Gold,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(Color(0x33FFD54F), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Composable
fun AllTasksScreen(
    state: AppState,
    onFilter: (Int) -> Unit,
    onCategoryFilter: (String) -> Unit,
    onBack: () -> Unit,
) {
    val filtered = remember(state.intensityFilter, state.categoryFilter) {
        Fantasies.ALL.filter { card ->
            (state.intensityFilter == 0 || card.intensity == state.intensityFilter) &&
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
                        "Все задания",
                        color = TextSoft,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf(0 to "Все", 1 to "1", 2 to "2", 3 to "3").forEach { (level, label) ->
                        FilterChip(
                            selected = state.intensityFilter == level,
                            onClick = { onFilter(level) },
                            label = { Text(if (level == 0) label else "♥$label") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentDark,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF3D2433),
                                labelColor = TextSoft,
                            ),
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
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentDark,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF3D2433),
                                labelColor = TextSoft,
                            ),
                        )
                    }
                    items(Fantasies.categories) { cat ->
                        FilterChip(
                            selected = state.categoryFilter == cat,
                            onClick = { onCategoryFilter(cat) },
                            label = { Text("${categoryEmoji(cat)} $cat") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentDark,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF3D2433),
                                labelColor = TextSoft,
                            ),
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
                    "${card.id}. ${card.title}",
                    color = TextSoft.copy(alpha = if (done) 0.5f else 1f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${card.category} · ${card.text}",
                    color = TextSoft.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "♥".repeat(card.intensity),
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
                    title = "Без проникновения",
                    body = "В этой колоде нет заданий с проникновением (ни PIV, ни анальным, " +
                        "ни оральным). Только мягкая эротика: поцелуи, массаж, раздевание, " +
                        "дразнилки, внешние ласки.",
                )
                RulesCard(
                    title = "Только 18+",
                    body = "Приложение предназначено исключительно для взрослых. " +
                        "Не делитесь экраном с несовершеннолетними.",
                )
                RulesCard(
                    title = "Как играть",
                    body = "Выберите, кто выполняет задание, тяните фант, отмечайте «Выполнено» " +
                        "или берите другой. Колода не повторяет карточки, пока не закончится " +
                        "или пока вы не сбросите сессию.",
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
                Text("Silk Fants · v0.2.0", color = Accent, fontSize = 14.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    "Карточная игра фантазий для пар.\n" +
                        "50 острых эротических заданий без проникновения.\n\n" +
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

