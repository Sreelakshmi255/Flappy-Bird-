package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DailyBonusDialog
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.TutorialScreen
import com.example.ui.theme.RetroFlapTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val currentScreen by viewModel.currentScreen.collectAsState()
            val selectedTheme by viewModel.selectedTheme.collectAsState()
            val showDailyBonus by viewModel.showDailyBonusDialog.collectAsState()

            // Android 13+ Notification Permission Launcher
            val notificationLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { _ -> }
            )

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            // Android system back button handler
            BackHandler(enabled = currentScreen != AppScreen.HOME) {
                viewModel.navigateTo(AppScreen.HOME)
            }

            RetroFlapTheme(selectedTheme = selectedTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                            AppScreen.GAME -> GameScreen(viewModel = viewModel)
                            AppScreen.LEADERBOARD -> LeaderboardScreen(viewModel = viewModel)
                            AppScreen.CHAT -> ChatScreen(viewModel = viewModel)
                            AppScreen.SHOP -> ShopScreen(viewModel = viewModel)
                            AppScreen.ACHIEVEMENTS -> AchievementsScreen(viewModel = viewModel)
                            AppScreen.TUTORIAL -> TutorialScreen(viewModel = viewModel)
                            AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel)
                        }

                        if (showDailyBonus) {
                            DailyBonusDialog(
                                viewModel = viewModel,
                                onDismiss = { viewModel.closeDailyBonusDialog() }
                            )
                        }
                    }
                }
            }
        }
    }
}
