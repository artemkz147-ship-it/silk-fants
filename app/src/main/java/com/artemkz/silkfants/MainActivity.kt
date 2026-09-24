package com.artemkz.silkfants

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.artemkz.silkfants.data.AppScreen
import com.artemkz.silkfants.ui.AboutScreen
import com.artemkz.silkfants.ui.AgeGateScreen
import com.artemkz.silkfants.ui.AllTasksScreen
import com.artemkz.silkfants.ui.DrawScreen
import com.artemkz.silkfants.ui.MainMenuScreen
import com.artemkz.silkfants.ui.RulesScreen
import com.artemkz.silkfants.ui.SilkFantsTheme

class MainActivity : ComponentActivity() {
    private val vm: FantsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SilkFantsTheme {
                Surface(Modifier.fillMaxSize()) {
                    val state by vm.state.collectAsState()
                    val toast by vm.toast.collectAsState()
                    val screen by vm.screen.collectAsState()

                    if (!state.ageVerified) {
                        AgeGateScreen(
                            onConfirm = { vm.confirmAdult() },
                            onDecline = {
                                vm.declineAdult()
                                finish()
                            },
                        )
                    } else {
                        when (screen) {
                            AppScreen.MAIN_MENU -> MainMenuScreen(
                                state = state,
                                toast = toast,
                                onDraw = { vm.ensureDeckAndShowDraw() },
                                onAllTasks = { vm.navigate(AppScreen.ALL_TASKS) },
                                onRules = { vm.navigate(AppScreen.RULES) },
                                onAbout = { vm.navigate(AppScreen.ABOUT) },
                                onResetSession = { vm.resetSession() },
                                onConsumeToast = { vm.consumeToast() },
                            )
                            AppScreen.DRAW -> DrawScreen(
                                state = state,
                                toast = toast,
                                onWho = { vm.setWhoPlays(it) },
                                onLevel = { vm.setLevelFilter(it) },
                                onDone = { vm.markDone() },
                                onAnother = { vm.drawAnother() },
                                onBack = { vm.navigate(AppScreen.MAIN_MENU) },
                                onConsumeToast = { vm.consumeToast() },
                            )
                            AppScreen.ALL_TASKS -> AllTasksScreen(
                                state = state,
                                onFilter = { vm.setLevelFilter(it) },
                                onCategoryFilter = { vm.setCategoryFilter(it) },
                                onBack = { vm.navigate(AppScreen.MAIN_MENU) },
                            )
                            AppScreen.RULES -> RulesScreen(
                                onBack = { vm.navigate(AppScreen.MAIN_MENU) },
                            )
                            AppScreen.ABOUT -> AboutScreen(
                                onBack = { vm.navigate(AppScreen.MAIN_MENU) },
                            )
                        }
                    }
                }
            }
        }
    }
}
