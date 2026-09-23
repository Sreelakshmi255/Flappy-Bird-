package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.MainViewModel
import com.example.ui.components.PixelButton
import com.example.ui.components.PixelCard
import com.example.ui.theme.PixelGold
import com.example.ui.theme.RetroFontFamily

@Composable
fun DailyBonusDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val profile by viewModel.playerProfile.collectAsState()
    val bonusResult by viewModel.dailyBonusResult.collectAsState()
    val streak = profile?.loginStreakDays ?: 1

    val dayRewards = listOf(
        Pair("Day 1", "50 🟡"),
        Pair("Day 2", "100 🟡"),
        Pair("Day 3", "150 🟡"),
        Pair("Day 4", "200 🟡"),
        Pair("Day 5", "300 🟡"),
        Pair("Day 6", "500 🟡"),
        Pair("Day 7", "1000 👑")
    )

    Dialog(onDismissRequest = onDismiss) {
        PixelCard(
            backgroundColor = Color(0xFF10002B),
            borderColor = Color(0xFFFF007F),
            modifier = Modifier.padding(6.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DAILY ARCADE BONUS",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = PixelGold
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Text(
                    text = "Current Streak: $streak Day(s) 🔥",
                    fontFamily = RetroFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF00F5D4)
                )

                // 7-day grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(dayRewards) { index, (day, reward) ->
                        val dayNum = index + 1
                        val isCurrent = dayNum == streak
                        val isPassed = dayNum < streak

                        Box(
                            modifier = Modifier
                                .border(
                                    1.5.dp,
                                    if (isCurrent) PixelGold else if (isPassed) Color.DarkGray else Color(0xFF7B2CBF)
                                )
                                .background(
                                    if (isCurrent) Color(0xFF3C096C) else if (isPassed) Color.Black.copy(alpha = 0.5f) else Color(0xFF1E1138)
                                )
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = day,
                                    fontFamily = RetroFontFamily,
                                    fontSize = 10.sp,
                                    color = if (isCurrent) PixelGold else Color.LightGray,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = reward,
                                    fontFamily = RetroFontFamily,
                                    fontSize = 10.sp,
                                    color = if (isCurrent) Color.White else Color.Gray
                                )
                                if (isPassed) {
                                    Text(
                                        text = "✓ CLAIMED",
                                        fontFamily = RetroFontFamily,
                                        fontSize = 8.sp,
                                        color = Color(0xFF73BF2E)
                                    )
                                }
                            }
                        }
                    }
                }

                // Claim Result Feedback Banner
                val result = bonusResult
                if (result != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, if (result.success) Color(0xFF00F5D4) else Color(0xFFFF007F))
                            .background(Color(0xFF240046))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = result.message,
                            fontFamily = RetroFontFamily,
                            fontSize = 11.sp,
                            color = if (result.success) Color(0xFF00F5D4) else Color(0xFFFF85A1),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                PixelButton(
                    text = "🎁 CLAIM DAILY REWARD",
                    onClick = { viewModel.claimDailyBonus() },
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFFFF007F),
                    textColor = Color.White,
                    fontSize = 13,
                    testTag = "claim_daily_bonus_button"
                )
            }
        }
    }
}
