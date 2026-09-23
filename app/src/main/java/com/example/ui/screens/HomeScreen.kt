package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameDifficulty
import com.example.game.GameTheme
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.PixelBirdAvatar
import com.example.ui.components.PixelButton
import com.example.ui.components.PixelCard
import com.example.ui.theme.PixelBorderDark
import com.example.ui.theme.PixelGold
import com.example.ui.theme.RetroFontFamily

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.playerProfile.collectAsState()
    val theme by viewModel.selectedTheme.collectAsState()
    val difficulty by viewModel.selectedDifficulty.collectAsState()
    val scanlines by viewModel.scanlinesEnabled.collectAsState()

    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "title_bounce")
    val titleOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "title_y"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Bar: Profile Summary & Daily Bonus Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar & Handle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(AppScreen.PROFILE) }
                        .border(2.dp, MaterialTheme.colorScheme.outline)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                ) {
                    PixelBirdAvatar(skinId = profile?.equippedSkin ?: "classic_yellow", modifier = Modifier.size(36.dp))
                    Column {
                        Text(
                            text = profile?.playerName ?: "PixelPilot",
                            fontFamily = RetroFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = profile?.selectedTitle ?: "Rookie Flapper",
                            fontFamily = RetroFontFamily,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Daily Reward & Coins
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Coins Display
                    Box(
                        modifier = Modifier
                            .border(2.dp, PixelBorderDark)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🟡 ${profile?.coins ?: 0}",
                            fontFamily = RetroFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = PixelGold
                        )
                    }

                    // Daily Bonus Gift
                    Box(
                        modifier = Modifier
                            .border(2.dp, Color(0xFFFF007F))
                            .background(Color(0xFF7B2CBF))
                            .clickable { viewModel.openDailyBonusDialog() }
                            .padding(8.dp)
                            .testTag("daily_bonus_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "Daily Bonus",
                            tint = PixelGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Hero Title with Retro Arcade Shadow
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = titleOffset.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "RETRO FLAP",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 34.sp,
                        color = Color.Black,
                        modifier = Modifier.offset(x = 3.dp, y = 3.dp)
                    )
                    Text(
                        text = "RETRO FLAP",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 34.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Box(
                    modifier = Modifier
                        .border(2.dp, Color.Black)
                        .background(Color.Black)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "★ 8-BIT ARCADE EDITION ★",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = PixelGold
                    )
                }
            }

            // High Score Banner
            PixelCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ALL-TIME BEST",
                            fontFamily = RetroFontFamily,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${profile?.highScore ?: 0}",
                            fontFamily = RetroFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = PixelGold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(30.dp)
                            .background(MaterialTheme.colorScheme.outline)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "GAMES PLAYED",
                            fontFamily = RetroFontFamily,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${profile?.totalGamesPlayed ?: 0}",
                            fontFamily = RetroFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Big Play Button
            PixelButton(
                text = "▶ INSERT COIN & PLAY",
                onClick = { viewModel.navigateTo(AppScreen.GAME) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                backgroundColor = Color(0xFF73BF2E),
                textColor = Color.White,
                fontSize = 18,
                testTag = "play_button"
            )

            // Difficulty Multiplier Selector
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "DIFFICULTY MULTIPLIER:",
                    fontFamily = RetroFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GameDifficulty.entries.forEach { diff ->
                        val isSelected = difficulty == diff
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    2.dp,
                                    if (isSelected) Color.White else Color.Black
                                )
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.4f)
                                )
                                .clickable { viewModel.setDifficulty(diff) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = diff.name,
                                    fontFamily = RetroFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Gray
                                )
                                Text(
                                    text = "${diff.scoreMultiplier}x",
                                    fontFamily = RetroFontFamily,
                                    fontSize = 9.sp,
                                    color = if (isSelected) PixelGold else Color.LightGray
                                )
                            }
                        }
                    }
                }
            }

            // Theme Options (including requested Violet & Cyan Cyber Event!)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ARCADE THEMES:",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "EVENT ACTIVE 🔥",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = Color(0xFFFF007F)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Classic Day
                    ThemePill(
                        title = "Classic Day",
                        themeItem = GameTheme.CLASSIC_DAY,
                        isSelected = theme == GameTheme.CLASSIC_DAY,
                        badge = "8-BIT",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.setTheme(GameTheme.CLASSIC_DAY) }
                    )

                    // Dark Mode Night
                    ThemePill(
                        title = "Night Mode",
                        themeItem = GameTheme.DARK_NIGHT,
                        isSelected = theme == GameTheme.DARK_NIGHT,
                        badge = "DARK",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.setTheme(GameTheme.DARK_NIGHT) }
                    )

                    // Violet & Cyan Event
                    ThemePill(
                        title = "Cyber Violet",
                        themeItem = GameTheme.VIOLET_CYAN_EVENT,
                        isSelected = theme == GameTheme.VIOLET_CYAN_EVENT,
                        badge = "CYBER",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.setTheme(GameTheme.VIOLET_CYAN_EVENT) }
                    )
                }
            }

            // Arcade Feature Navigation Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PixelButton(
                    text = "🏆 RANKS",
                    onClick = { viewModel.navigateTo(AppScreen.LEADERBOARD) },
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFF0284C7),
                    fontSize = 13,
                    testTag = "home_ranks_button"
                )
                PixelButton(
                    text = "💬 CHAT",
                    onClick = { viewModel.navigateTo(AppScreen.CHAT) },
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFF9333EA),
                    fontSize = 13,
                    testTag = "home_chat_button"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PixelButton(
                    text = "🛍️ SHOP & SKINS",
                    onClick = { viewModel.navigateTo(AppScreen.SHOP) },
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFFD97706),
                    fontSize = 13,
                    testTag = "home_shop_button"
                )
                PixelButton(
                    text = "⭐ BADGES",
                    onClick = { viewModel.navigateTo(AppScreen.ACHIEVEMENTS) },
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFFE11D48),
                    fontSize = 13,
                    testTag = "home_badges_button"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PixelButton(
                    text = "📖 TUTORIAL",
                    onClick = { viewModel.navigateTo(AppScreen.TUTORIAL) },
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFF0D9488),
                    fontSize = 12,
                    testTag = "home_tutorial_button"
                )
                PixelButton(
                    text = "☁️ CLOUD SYNC",
                    onClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFF4F46E5),
                    fontSize = 12,
                    testTag = "home_cloud_button"
                )
            }

            // Bottom Arcade Controls: Sound, Haptics, CRT Scanlines, Notifications
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.toggleSound() },
                    modifier = Modifier
                        .border(2.dp, MaterialTheme.colorScheme.outline)
                        .background(MaterialTheme.colorScheme.surface)
                        .size(42.dp)
                ) {
                    Icon(
                        imageVector = if (viewModel.soundManager.isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Sound Toggle",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleHaptics() },
                    modifier = Modifier
                        .border(2.dp, MaterialTheme.colorScheme.outline)
                        .background(MaterialTheme.colorScheme.surface)
                        .size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = "Haptics Toggle",
                        tint = if (viewModel.soundManager.isHapticsEnabled) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleScanlines() },
                    modifier = Modifier
                        .border(2.dp, MaterialTheme.colorScheme.outline)
                        .background(MaterialTheme.colorScheme.surface)
                        .size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = "Scanlines Toggle",
                        tint = if (scanlines) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }

                IconButton(
                    onClick = { viewModel.triggerEventNotification() },
                    modifier = Modifier
                        .border(2.dp, Color(0xFFFF007F))
                        .background(MaterialTheme.colorScheme.surface)
                        .size(42.dp)
                ) {
                    Text(
                        text = "⚡",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemePill(
    title: String,
    themeItem: GameTheme,
    isSelected: Boolean,
    badge: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .border(
                2.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else Color.Black
            )
            .background(
                if (isSelected) themeItem.skyColorTop else Color.Black.copy(alpha = 0.5f)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = badge,
                fontFamily = RetroFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = if (isSelected) themeItem.accentColor else Color.Gray
            )
            Text(
                text = title,
                fontFamily = RetroFontFamily,
                fontSize = 9.sp,
                color = Color.White
            )
        }
    }
}
