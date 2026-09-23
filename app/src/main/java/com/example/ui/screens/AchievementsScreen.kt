package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
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
import com.example.data.AchievementEntity
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.PixelCard
import com.example.ui.theme.PixelGold
import com.example.ui.theme.RetroFontFamily

@Composable
fun AchievementsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val achievements by viewModel.achievements.collectAsState()
    val unlockedCount = achievements.count { it.isUnlocked }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    modifier = Modifier
                        .border(2.dp, MaterialTheme.colorScheme.outline)
                        .background(MaterialTheme.colorScheme.surface)
                        .size(38.dp)
                        .testTag("achievements_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "ARCADE TROPHIES",
                    fontFamily = RetroFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Box(
                    modifier = Modifier
                        .border(1.5.dp, Color(0xFFFFD700))
                        .background(Color(0xFF240046))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$unlockedCount / ${achievements.size}",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = PixelGold
                    )
                }
            }

            // List of Achievements
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = achievements,
                    key = { it.id }
                ) { ach ->
                    AchievementRow(ach = ach)
                }
            }
        }
    }
}

@Composable
private fun AchievementRow(ach: AchievementEntity) {
    val iconEmoji = when (ach.iconName) {
        "wing" -> "🪽"
        "pipe" -> "🧱"
        "medal" -> "🎖️"
        "trophy" -> "🏆"
        "crown" -> "👑"
        "coin" -> "🪙"
        "gem" -> "💎"
        "fire" -> "🔥"
        "skull" -> "💀"
        "heart" -> "❤️"
        "sparkles" -> "⚡"
        "palette" -> "🎨"
        "calendar" -> "📅"
        else -> "⭐"
    }

    PixelCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (ach.isUnlocked) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        borderColor = if (ach.isUnlocked) PixelGold else Color.DarkGray
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .border(1.5.dp, if (ach.isUnlocked) PixelGold else Color.Gray)
                        .background(if (ach.isUnlocked) Color(0xFF240046) else Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = iconEmoji,
                        fontSize = 20.sp
                    )
                }

                Column {
                    Text(
                        text = ach.title,
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (ach.isUnlocked) MaterialTheme.colorScheme.onSurface else Color.Gray
                    )
                    Text(
                        text = ach.description,
                        fontFamily = RetroFontFamily,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Reward: +${ach.rewardCoins} Coins",
                        fontFamily = RetroFontFamily,
                        fontSize = 10.sp,
                        color = PixelGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (ach.isUnlocked) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Unlocked",
                    tint = Color(0xFF73BF2E),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
