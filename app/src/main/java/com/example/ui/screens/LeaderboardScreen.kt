package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.LeaderboardEntryEntity
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.PixelBirdAvatar
import com.example.ui.components.PixelButton
import com.example.ui.components.PixelCard
import com.example.ui.theme.PixelBorderDark
import com.example.ui.theme.PixelGold
import com.example.ui.theme.RetroFontFamily

@Composable
fun LeaderboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val leaderboard by viewModel.leaderboard.collectAsState()
    val friendsLeaderboard by viewModel.friendLeaderboard.collectAsState()
    val userProfile by viewModel.playerProfile.collectAsState()
    val compareTarget by viewModel.compareTarget.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var difficultyFilter by remember { mutableStateOf("ALL") }

    val rawList: List<LeaderboardEntryEntity> = when (selectedTab) {
        1 -> friendsLeaderboard
        2 -> leaderboard.take(15)
        else -> leaderboard
    }

    val displayList: List<LeaderboardEntryEntity> = remember(rawList, difficultyFilter) {
        if (difficultyFilter == "ALL") rawList else rawList.filter { it.difficulty == difficultyFilter }
    }

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
                        .size(40.dp)
                        .testTag("leaderboard_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "ARCADE LEADERBOARD",
                    fontFamily = RetroFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Box(
                    modifier = Modifier
                        .border(2.dp, PixelBorderDark)
                        .background(Color(0xFF240046))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🏆 LIVE",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.Cyan
                    )
                }
            }

            // Tabs: Global, Friends, Weekly
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LeaderboardTab(
                    title = "GLOBAL",
                    icon = Icons.Default.Public,
                    isSelected = selectedTab == 0,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.soundManager.playButtonClick()
                        selectedTab = 0
                    }
                )
                LeaderboardTab(
                    title = "FRIENDS",
                    icon = Icons.Default.Group,
                    isSelected = selectedTab == 1,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.soundManager.playButtonClick()
                        selectedTab = 1
                    }
                )
                LeaderboardTab(
                    title = "WEEKLY",
                    icon = Icons.Default.EmojiEvents,
                    isSelected = selectedTab == 2,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.soundManager.playButtonClick()
                        selectedTab = 2
                    }
                )
            }

            // Difficulty Filter Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("ALL", "NORMAL", "HARD", "INSANE").forEach { diff ->
                    val isFilterSelected = difficultyFilter == diff
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, if (isFilterSelected) Color.White else Color.Transparent)
                            .background(
                                if (isFilterSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.3f)
                            )
                            .clickable {
                                viewModel.soundManager.playButtonClick()
                                difficultyFilter = diff
                            }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = diff,
                            fontFamily = RetroFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFilterSelected) MaterialTheme.colorScheme.onPrimary else Color.Gray
                        )
                    }
                }
            }

            // Leaderboard Entries List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = displayList,
                    key = { _, item -> item.id }
                ) { index, entry ->
                    LeaderboardRow(
                        rank = index + 1,
                        entry = entry,
                        isUser = entry.isUser || (userProfile != null && entry.playerName == userProfile?.playerName),
                        onCompare = { viewModel.openCompare(entry) }
                    )
                }
            }

            // User Current High Score Footer
            PixelCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PixelBirdAvatar(skinId = userProfile?.equippedSkin ?: "classic_yellow", modifier = Modifier.size(34.dp))
                        Column {
                            Text(
                                text = "YOUR HIGH SCORE",
                                fontFamily = RetroFontFamily,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = userProfile?.playerName ?: "PixelHero",
                                fontFamily = RetroFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = "${userProfile?.highScore ?: 0} PTS",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = PixelGold
                    )
                }
            }
        }

        // Compare Dialog
        compareTarget?.let { target ->
            CompareWithFriendDialog(
                target = target,
                userProfile = userProfile,
                onDismiss = { viewModel.closeCompare() },
                onChallenge = {
                    viewModel.sendChatMessage("I'm coming for your score of ${target.score}, @${target.playerName}!")
                    viewModel.closeCompare()
                    viewModel.navigateTo(AppScreen.CHAT)
                }
            )
        }
    }
}

@Composable
private fun LeaderboardTab(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .border(2.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Black)
            .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Black.copy(alpha = 0.4f))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                fontFamily = RetroFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Gray
            )
        }
    }
}

@Composable
private fun LeaderboardRow(
    rank: Int,
    entry: LeaderboardEntryEntity,
    isUser: Boolean,
    onCompare: () -> Unit
) {
    val rankBadgeColor = when (rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> Color(0xFF334155)
    }

    PixelCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (isUser) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface,
        borderColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number & Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .border(1.5.dp, Color.Black)
                        .background(rankBadgeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$rank",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (rank <= 3) Color.Black else Color.White
                    )
                }

                PixelBirdAvatar(skinId = entry.avatarId, modifier = Modifier.size(32.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = entry.playerName,
                            fontFamily = RetroFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (entry.isFriend) {
                            Text(
                                text = "[FRIEND]",
                                fontFamily = RetroFontFamily,
                                fontSize = 9.sp,
                                color = Color(0xFF00F5D4)
                            )
                        }
                    }

                    Text(
                        text = "${entry.difficulty} MODE",
                        fontFamily = RetroFontFamily,
                        fontSize = 9.sp,
                        color = Color.Gray
                    )
                }
            }

            // Score & Compare Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${entry.score}",
                    fontFamily = RetroFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = PixelGold
                )

                if (!isUser) {
                    Box(
                        modifier = Modifier
                            .border(1.5.dp, Color.Black)
                            .background(Color(0xFF0284C7))
                            .clickable(onClick = onCompare)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "VS",
                            fontFamily = RetroFontFamily,
                            fontSize = 9.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompareWithFriendDialog(
    target: LeaderboardEntryEntity,
    userProfile: com.example.data.PlayerProfileEntity?,
    onDismiss: () -> Unit,
    onChallenge: () -> Unit
) {
    val userScore = userProfile?.highScore ?: 0
    val scoreDiff = userScore - target.score

    Dialog(onDismissRequest = onDismiss) {
        PixelCard(
            backgroundColor = Color(0xFF10002B),
            borderColor = Color(0xFF00F5D4),
            modifier = Modifier.padding(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HEAD-TO-HEAD VS",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color(0xFF00F5D4)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Comparison Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // You
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        PixelBirdAvatar(skinId = userProfile?.equippedSkin ?: "classic_yellow", modifier = Modifier.size(42.dp))
                        Text(
                            text = "YOU",
                            fontFamily = RetroFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Text(
                            text = "$userScore",
                            fontFamily = RetroFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = PixelGold
                        )
                    }

                    Text(
                        text = "VS",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color(0xFFFF007F)
                    )

                    // Target
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        PixelBirdAvatar(skinId = target.avatarId, modifier = Modifier.size(42.dp))
                        Text(
                            text = target.playerName,
                            fontFamily = RetroFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${target.score}",
                            fontFamily = RetroFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = PixelGold
                        )
                    }
                }

                // Outcome Analysis
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, Color.Black)
                        .background(Color(0xFF240046))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val statusText = when {
                        scoreDiff > 0 -> "🏆 You are ahead by +$scoreDiff points! Pure mastery!"
                        scoreDiff == 0 -> "⚔️ Perfect tie! You are evenly matched!"
                        else -> "⚡ You are behind by ${-scoreDiff} points! Flap higher!"
                    }
                    Text(
                        text = statusText,
                        fontFamily = RetroFontFamily,
                        fontSize = 11.sp,
                        color = if (scoreDiff >= 0) Color(0xFF00F5D4) else Color(0xFFFF007F),
                        fontWeight = FontWeight.Bold
                    )
                }

                PixelButton(
                    text = "CHALLENGE IN CHAT",
                    onClick = onChallenge,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFFFF007F),
                    fontSize = 12,
                    testTag = "challenge_in_chat_button"
                )
            }
        }
    }
}
