package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.BirdSkin
import com.example.game.FlapSensitivity
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.PixelBirdAvatar
import com.example.ui.components.PixelButton
import com.example.ui.components.PixelCard
import com.example.ui.theme.PixelGold
import com.example.ui.theme.RetroFontFamily

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.playerProfile.collectAsState()
    val sensitivity by viewModel.sensitivity.collectAsState()
    val cloudMessage by viewModel.cloudMessage.collectAsState()

    var nameInput by remember(profile?.playerName) { mutableStateOf(profile?.playerName ?: "PixelPilot") }
    var selectedAvatar by remember(profile?.avatarId) { mutableStateOf(profile?.avatarId ?: "classic_yellow") }
    var selectedTitle by remember(profile?.selectedTitle) { mutableStateOf(profile?.selectedTitle ?: "Arcade Pilot") }
    var restoreCodeInput by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    val availableTitles = listOf(
        "Rookie Flapper",
        "Arcade Pilot",
        "Sky Ace",
        "Cyber Hacker",
        "Pipe Whisperer",
        "Retro Legend"
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
                        .testTag("profile_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "PILOT ID & CLOUD SAVE",
                    fontFamily = RetroFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Box(
                    modifier = Modifier
                        .border(1.5.dp, Color(0xFF00F5D4))
                        .background(Color(0xFF052B24))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SYNCED",
                        fontFamily = RetroFontFamily,
                        fontSize = 10.sp,
                        color = Color(0xFF00F5D4),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Pilot Profile Identity Card
            PixelCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "CUSTOMIZE PILOT IDENTITY:",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Avatar Selector
                    Text(
                        text = "SELECT AVATAR ICON:",
                        fontFamily = RetroFontFamily,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BirdSkin.ALL_SKINS.forEach { skin ->
                            val isChosen = selectedAvatar == skin.id
                            Box(
                                modifier = Modifier
                                    .border(
                                        2.dp,
                                        if (isChosen) Color(0xFF00F5D4) else Color.DarkGray
                                    )
                                    .background(
                                        if (isChosen) Color(0xFF10002B) else Color.Black.copy(alpha = 0.4f)
                                    )
                                    .clickable { selectedAvatar = skin.id }
                                    .padding(4.dp)
                            ) {
                                PixelBirdAvatar(skinId = skin.id, modifier = Modifier.size(34.dp))
                            }
                        }
                    }

                    // Handle / Name Input
                    Text(
                        text = "PILOT HANDLE:",
                        fontFamily = RetroFontFamily,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, MaterialTheme.colorScheme.outline)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(10.dp)
                    ) {
                        BasicTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it.take(16) },
                            textStyle = TextStyle(
                                fontFamily = RetroFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Title Selector
                    Text(
                        text = "CHOOSE TITLE:",
                        fontFamily = RetroFontFamily,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        availableTitles.take(3).forEach { title ->
                            val isTitle = selectedTitle == title
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, if (isTitle) PixelGold else Color.DarkGray)
                                    .background(if (isTitle) Color(0xFF240046) else Color.Black.copy(alpha = 0.3f))
                                    .clickable { selectedTitle = title }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontFamily = RetroFontFamily,
                                    fontSize = 9.sp,
                                    color = if (isTitle) PixelGold else Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    PixelButton(
                        text = "SAVE PROFILE CHANGES",
                        onClick = { viewModel.updateProfile(nameInput, selectedAvatar, selectedTitle) },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color(0xFF0284C7),
                        fontSize = 12,
                        testTag = "save_profile_button"
                    )
                }
            }

            // Cloud Save & Cross-Device Sync
            PixelCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CLOUD SAVE & RESTORE:",
                            fontFamily = RetroFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(imageVector = Icons.Default.Cloud, contentDescription = null, tint = Color(0xFF00F5D4))
                    }

                    Text(
                        text = "Your progress is saved locally offline and synced across devices using an arcade backup code.",
                        fontFamily = RetroFontFamily,
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )

                    // Current Cloud Code
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, Color(0xFF7B2CBF))
                            .background(Color(0xFF10002B))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "YOUR CLOUD BACKUP CODE:",
                                fontFamily = RetroFontFamily,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = profile?.cloudSyncCode ?: "RETRO-8BIT-7749",
                                fontFamily = RetroFontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = PixelGold
                            )
                        }
                    }

                    PixelButton(
                        text = "☁️ SYNC NOW TO CLOUD",
                        onClick = { viewModel.syncCloud() },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color(0xFF7B2CBF),
                        fontSize = 12,
                        testTag = "sync_cloud_button"
                    )

                    // Restore from code
                    Text(
                        text = "RESTORE ON ANOTHER DEVICE:",
                        fontFamily = RetroFontFamily,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.5.dp, MaterialTheme.colorScheme.outline)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(10.dp)
                        ) {
                            if (restoreCodeInput.isEmpty()) {
                                Text(
                                    text = "Enter backup code...",
                                    fontFamily = RetroFontFamily,
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            BasicTextField(
                                value = restoreCodeInput,
                                onValueChange = { restoreCodeInput = it.uppercase() },
                                textStyle = TextStyle(
                                    fontFamily = RetroFontFamily,
                                    fontSize = 12.sp,
                                    color = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        PixelButton(
                            text = "RESTORE",
                            onClick = { viewModel.restoreCloud(restoreCodeInput) },
                            backgroundColor = Color(0xFF00F5D4),
                            textColor = Color.Black,
                            fontSize = 11,
                            testTag = "restore_cloud_button"
                        )
                    }

                    val message = cloudMessage
                    if (message != null) {
                        Text(
                            text = message,
                            fontFamily = RetroFontFamily,
                            fontSize = 11.sp,
                            color = Color(0xFF00F5D4),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Responsive Controls: Sensitivity Setting
            PixelCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "TOUCH FLAP SENSITIVITY:",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FlapSensitivity.entries.forEach { sens ->
                            val isSens = sensitivity == sens
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.5.dp, if (isSens) Color.White else Color.DarkGray)
                                    .background(if (isSens) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.3f))
                                    .clickable { viewModel.setSensitivity(sens) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sens.displayName,
                                    fontFamily = RetroFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isSens) MaterialTheme.colorScheme.onPrimary else Color.LightGray
                                )
                            }
                        }
                    }
                }
            }

            // Notification Test Triggers
            PixelCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "NOTIFICATIONS & EVENT ALERTS:",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PixelButton(
                            text = "🎁 TEST DAILY ALERT",
                            onClick = { viewModel.triggerDailyNotification() },
                            modifier = Modifier.weight(1f),
                            backgroundColor = Color(0xFF240046),
                            fontSize = 10,
                            testTag = "test_daily_alert_button"
                        )
                        PixelButton(
                            text = "⚡ TEST EVENT ALERT",
                            onClick = { viewModel.triggerEventNotification() },
                            modifier = Modifier.weight(1f),
                            backgroundColor = Color(0xFF10002B),
                            fontSize = 10,
                            testTag = "test_event_alert_button"
                        )
                    }
                }
            }
        }
    }
}
