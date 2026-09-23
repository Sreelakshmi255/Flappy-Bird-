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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.ChatMessageEntity
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.PixelBirdAvatar
import com.example.ui.theme.PixelBorderDark
import com.example.ui.theme.PixelGold
import com.example.ui.theme.RetroFontFamily

@Composable
fun ChatScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val userProfile by viewModel.playerProfile.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickPhrases = listOf(
        "GG! 🎮",
        "Beat my high score! 🏆",
        "Pipes are brutal today! 💥",
        "Cyber Event is awesome! ⚡",
        "Anyone in Insane mode? 💀",
        "Need more hearts! ❤️"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                        .testTag("chat_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "GLOBAL 8-BIT TERMINAL",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "🟢 COMMUNITY ONLINE",
                        fontFamily = RetroFontFamily,
                        fontSize = 9.sp,
                        color = Color(0xFF00F5D4),
                        fontWeight = FontWeight.Bold
                    )
                }

                // Share Score Action Button
                Box(
                    modifier = Modifier
                        .border(1.5.dp, Color(0xFFFFD700))
                        .background(Color(0xFF7B2CBF))
                        .clickable {
                            viewModel.sendChatMessage(
                                text = "🏆 Check out my high score of ${userProfile?.highScore ?: 0}!",
                                shareScore = true
                            )
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("share_score_chat_button")
                ) {
                    Text(
                        text = "SHARE 🏆",
                        fontFamily = RetroFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PixelGold
                    )
                }
            }

            // Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(2.dp, MaterialTheme.colorScheme.outline)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = messages,
                    key = { it.id }
                ) { msg ->
                    ChatMessageBubble(
                        msg = msg,
                        isMe = msg.isCurrentUser || (userProfile != null && msg.senderName == userProfile?.playerName)
                    )
                }
            }

            // Quick Chat Horizontal Bar
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(quickPhrases) { phrase ->
                    Box(
                        modifier = Modifier
                            .border(1.5.dp, Color.DarkGray)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable {
                                viewModel.sendChatMessage(phrase)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = phrase,
                            fontFamily = RetroFontFamily,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Message Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(2.dp, MaterialTheme.colorScheme.primary)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    if (inputText.isEmpty()) {
                        Text(
                            text = "Broadcast message to pilots...",
                            fontFamily = RetroFontFamily,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        textStyle = TextStyle(
                            fontFamily = RetroFontFamily,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth().testTag("chat_input_field")
                    )
                }

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendChatMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .border(2.dp, PixelBorderDark)
                        .background(MaterialTheme.colorScheme.primary)
                        .size(44.dp)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(msg: ChatMessageEntity, isMe: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isMe) {
            PixelBirdAvatar(skinId = msg.avatarId, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(6.dp))
        }

        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            // Sender name & verified badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (isMe) "YOU" else msg.senderName,
                    fontFamily = RetroFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isMe) Color(0xFF00F5D4) else PixelGold
                )
                if (msg.sharedScore != null) {
                    Text(
                        text = "[SCORE VERIFIED]",
                        fontFamily = RetroFontFamily,
                        fontSize = 8.sp,
                        color = Color(0xFFFF007F),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Message Bubble
            Box(
                modifier = Modifier
                    .border(
                        1.5.dp,
                        if (isMe) Color(0xFF00F5D4) else Color(0xFF7B2CBF)
                    )
                    .background(
                        if (isMe) Color(0xFF052B24) else Color(0xFF1E1138)
                    )
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = msg.message,
                        fontFamily = RetroFontFamily,
                        fontSize = 12.sp,
                        color = Color.White
                    )

                    // Shared Score Card embedded in chat
                    if (msg.sharedScore != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, PixelGold)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "★ VERIFIED FLIGHT ★",
                                    fontFamily = RetroFontFamily,
                                    fontSize = 9.sp,
                                    color = PixelGold
                                )
                                Text(
                                    text = "${msg.sharedScore} PTS",
                                    fontFamily = RetroFontFamily,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = Color(0xFF00FFFF)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isMe) {
            Spacer(modifier = Modifier.width(6.dp))
            PixelBirdAvatar(skinId = msg.avatarId, modifier = Modifier.size(32.dp))
        }
    }
}
