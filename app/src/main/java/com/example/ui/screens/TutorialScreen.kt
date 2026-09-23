package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.PixelBirdAvatar
import com.example.ui.components.PixelButton
import com.example.ui.components.PixelCard
import com.example.ui.components.PixelHeartRow
import com.example.ui.theme.PixelGold
import com.example.ui.theme.RetroFontFamily

@Composable
fun TutorialScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var practiceFlaps by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
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
                        .testTag("tutorial_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "FLIGHT ACADEMY",
                    fontFamily = RetroFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Box(
                    modifier = Modifier
                        .border(1.5.dp, Color(0xFF00F5D4))
                        .background(Color(0xFF052B24))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "MANUAL",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = Color(0xFF00F5D4)
                    )
                }
            }

            // Step 1: Flapping
            TutorialCard(
                stepNum = "01",
                title = "WING CONTROL & FLAP RHYTHM",
                body = "Tap anywhere on the screen to flap upward. Gravity will continuously pull you down. Consistent light taps keep your flight altitude steady!",
                badge = "CONTROLS"
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PixelButton(
                        text = "PRACTICE FLAP ($practiceFlaps)",
                        onClick = {
                            practiceFlaps++
                            viewModel.soundManager.playFlap()
                        },
                        backgroundColor = Color(0xFF73BF2E),
                        fontSize = 12
                    )
                }
            }

            // Step 2: Pixel Hearts Health
            TutorialCard(
                stepNum = "02",
                title = "PIXEL HEART HEALTH SYSTEM",
                body = "Unlike traditional one-hit deaths, you start with 3 (or 4 on Easy) Pixel Hearts! If you bump a pipe, you lose 1 heart and gain 1.3s of flashing invulnerability. Ground collisions or 0 hearts = Game Over.",
                badge = "HEALTH"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    PixelHeartRow(currentHearts = 2, maxHearts = 3)
                }
            }

            // Step 3: Difficulty Multipliers
            TutorialCard(
                stepNum = "03",
                title = "DIFFICULTY MULTIPLIERS",
                body = "Choose your risk level: Easy (1.0x), Normal (1.5x), Hard (2.0x), or Cyber Insane (3.0x). Higher difficulty tightens gaps and speeds up pipes, but yields multiplied points and coins!",
                badge = "SCORING"
            )

            // Step 4: Collectibles
            TutorialCard(
                stepNum = "04",
                title = "COINS & FLOATING HEARTS",
                body = "Look out for 🟡 Gold Coins floating between pipe gaps. Spend them in the shop for cool 8-bit skins! You may also encounter floating ❤️ hearts to replenish lost health.",
                badge = "LOOT"
            )

            // Step 5: Events & Themes
            TutorialCard(
                stepNum = "05",
                title = "CYBER NEON EVENT",
                body = "Switch to the Violet, Black & Cyan theme from the home screen to access limited-time arcade event bonuses and unlock special event achievements!",
                badge = "EVENT"
            )

            PixelButton(
                text = "READY FOR TAKEOFF! ▶",
                onClick = { viewModel.navigateTo(AppScreen.GAME) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                backgroundColor = Color(0xFF00F5D4),
                textColor = Color.Black,
                fontSize = 15,
                testTag = "tutorial_ready_button"
            )
        }
    }
}

@Composable
private fun TutorialCard(
    stepNum: String,
    title: String,
    body: String,
    badge: String,
    extraContent: (@Composable () -> Unit)? = null
) {
    PixelCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .border(1.5.dp, PixelGold)
                            .background(Color(0xFF240046))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "STEP $stepNum",
                            fontFamily = RetroFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = PixelGold
                        )
                    }
                    Text(
                        text = title,
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        fontFamily = RetroFontFamily,
                        fontSize = 8.sp,
                        color = Color.Cyan
                    )
                }
            }

            Text(
                text = body,
                fontFamily = RetroFontFamily,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = Color.LightGray
            )

            extraContent?.invoke()
        }
    }
}
