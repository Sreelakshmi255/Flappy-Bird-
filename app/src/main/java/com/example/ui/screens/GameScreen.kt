package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.BirdSkin
import com.example.game.GameDifficulty
import com.example.game.GameEngine
import com.example.game.GameState
import com.example.game.GameTheme
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.PixelButton
import com.example.ui.components.PixelCard
import com.example.ui.components.PixelHeartRow
import com.example.ui.components.ScanlineOverlay
import com.example.ui.components.drawPixelBird
import com.example.ui.theme.PixelBorderDark
import com.example.ui.theme.PixelGold
import com.example.ui.theme.RetroFontFamily
import kotlin.random.Random

@Composable
fun GameScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val theme by viewModel.selectedTheme.collectAsState()
    val difficulty by viewModel.selectedDifficulty.collectAsState()
    val sensitivity by viewModel.sensitivity.collectAsState()
    val profile by viewModel.playerProfile.collectAsState()
    val scanlines by viewModel.scanlinesEnabled.collectAsState()

    val equippedSkin = remember(profile?.equippedSkin) {
        BirdSkin.getById(profile?.equippedSkin ?: "classic_yellow")
    }

    var gameScore by remember { mutableStateOf(0) }
    var gameHearts by remember(difficulty) { mutableStateOf(difficulty.startingHearts) }
    var maxHearts by remember(difficulty) { mutableStateOf(difficulty.startingHearts) }
    var gameCoins by remember { mutableStateOf(0) }
    var gameState by remember { mutableStateOf(GameState.READY) }
    var shakeX by remember { mutableFloatStateOf(0f) }
    var shakeY by remember { mutableFloatStateOf(0f) }

    val extraHeartsBonus = profile?.extraHeartsUnlocked ?: 0
    val hasShieldMagnet = profile?.shieldMagnetUnlocked ?: false

    val engine = remember(difficulty, sensitivity, extraHeartsBonus, hasShieldMagnet) {
        GameEngine(
            difficulty = difficulty,
            sensitivity = sensitivity,
            extraHeartsBonus = extraHeartsBonus,
            hasShieldMagnet = hasShieldMagnet,
            onFlapEvent = { viewModel.soundManager.playFlap() },
            onScoreEvent = { viewModel.soundManager.playScore() },
            onCoinEvent = { viewModel.soundManager.playCoin() },
            onDamageEvent = { heartsRemaining ->
                viewModel.soundManager.playHit()
                gameHearts = heartsRemaining
            },
            onHeartPickupEvent = {
                viewModel.soundManager.playHeartPickup()
            },
            onGameOverEvent = { finalScore, coins, pipes ->
                viewModel.soundManager.playGameOver()
                viewModel.onGameFinished(finalScore, coins, pipes)
            }
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        engine.tap(size.height.toFloat())
                        gameState = engine.state
                        gameHearts = engine.currentHearts
                        maxHearts = engine.maxHearts
                    }
                )
            }
            .testTag("game_screen")
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        var lastFrameTime by remember { mutableLongStateOf(0L) }

        // Game Loop
        LaunchedEffect(engine.state) {
            lastFrameTime = 0L
            while (true) {
                withFrameNanos { frameTime ->
                    if (lastFrameTime != 0L) {
                        val dt = (frameTime - lastFrameTime) / 1_000_000_000f
                        engine.update(dt, widthPx, heightPx)
                        gameScore = engine.totalScore
                        gameHearts = engine.currentHearts
                        maxHearts = engine.maxHearts
                        gameCoins = engine.coinsCollected
                        gameState = engine.state

                        if (engine.screenShakeTimer > 0f) {
                            shakeX = (Random.nextFloat() - 0.5f) * 14f
                            shakeY = (Random.nextFloat() - 0.5f) * 14f
                        } else {
                            shakeX = 0f
                            shakeY = 0f
                        }
                    }
                    lastFrameTime = frameTime
                }
            }
        }

        // Initialize engine positions
        LaunchedEffect(heightPx) {
            if (heightPx > 0) {
                engine.reset(heightPx)
                gameHearts = engine.currentHearts
                maxHearts = engine.maxHearts
                gameState = engine.state
            }
        }

        // Canvas Rendering
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = shakeX.dp, y = shakeY.dp)
        ) {
            // 1. Draw Sky Background & Parallax Scenery
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(theme.skyColorTop, theme.skyColorBottom),
                    startY = 0f,
                    endY = size.height - 110f
                ),
                size = Size(size.width, size.height - 110f)
            )

            // Draw Parallax Scenery aligned with 8-bit scale
            drawRetroScenery(this, engine.groundScrollX, theme)

            // 2. Draw Pipes
            for (pipe in engine.pipes) {
                drawRetroPipe(this, pipe.x, 0f, pipe.width, pipe.topHeight, isTop = true, theme = theme)
                val bottomY = pipe.gapY + pipe.gapHeight / 2f
                val bottomHeight = (size.height - 110f) - bottomY
                drawRetroPipe(this, pipe.x, bottomY, pipe.width, bottomHeight, isTop = false, theme = theme)
            }

            // 3. Draw Collectibles: Coins
            for (coin in engine.coins) {
                if (!coin.collected) {
                    drawRetroCoin(this, coin.x, coin.y)
                }
            }

            // 4. Draw Collectibles: Floating Hearts
            for (heart in engine.hearts) {
                if (!heart.collected) {
                    drawFloatingHeart(this, heart.x, heart.y)
                }
            }

            // 5. Draw Ground
            drawRetroGround(this, engine.groundScrollX, theme)

            // 6. Draw Bird with Flapping Animation (Compact 8-bit scale aligned with background)
            val birdX = size.width * 0.28f
            val birdY = engine.birdY

            // Flashing when invincible
            val shouldDrawBird = !engine.isInvincible || ((engine.invincibleTimer * 16).toInt() % 2 == 0)
            if (shouldDrawBird) {
                val wingFrame = when {
                    engine.birdVelocity < -150f -> 1 // Up
                    engine.birdVelocity > 200f -> 2 // Down
                    else -> 0 // Mid
                }

                rotate(degrees = engine.birdRotation, pivot = Offset(birdX, birdY)) {
                    // Optional aura
                    if (equippedSkin.auraColor != Color.Transparent) {
                        drawCircle(
                            color = equippedSkin.auraColor,
                            radius = 24f,
                            center = Offset(birdX, birdY)
                        )
                    }

                    // Bird pixel art scaled nicely relative to the 72f pillars
                    val birdWidth = 46f
                    val birdHeight = birdWidth * (10f / 14f)
                    val topLeft = Offset(birdX - birdWidth / 2f, birdY - birdHeight / 2f)
                    drawContext.canvas.save()
                    drawContext.transform.translate(topLeft.x, topLeft.y)
                    drawPixelBird(equippedSkin, wingFrame, birdWidth = birdWidth)
                    drawContext.canvas.restore()
                }
            }
        }

        // Optional Scanlines
        if (scanlines) {
            ScanlineOverlay(alpha = 0.08f)
        }

        // Top HUD
        GameHUD(
            score = gameScore,
            currentHearts = gameHearts,
            maxHearts = maxHearts,
            coins = gameCoins,
            difficulty = difficulty,
            theme = theme,
            onPauseClick = {
                engine.pause()
                gameState = engine.state
            }
        )

        // Overlay: READY (Tap to Flap)
        AnimatedVisibility(
            visible = gameState == GameState.READY,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            ReadyOverlay(difficulty = difficulty)
        }

        // Overlay: PAUSED
        AnimatedVisibility(
            visible = gameState == GameState.PAUSED,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            PauseOverlay(
                onResume = {
                    engine.resume()
                    gameState = engine.state
                },
                onRestart = {
                    engine.reset(heightPx)
                    gameState = engine.state
                },
                onQuit = {
                    viewModel.navigateTo(AppScreen.HOME)
                }
            )
        }

        // Overlay: GAME OVER
        AnimatedVisibility(
            visible = gameState == GameState.GAME_OVER,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            GameOverOverlay(
                score = gameScore,
                highScore = profile?.highScore ?: 0,
                coins = gameCoins,
                difficulty = difficulty,
                onRestart = {
                    engine.reset(heightPx)
                    gameState = engine.state
                },
                onShare = {
                    viewModel.sendChatMessage(
                        text = "I just scored $gameScore on ${difficulty.displayName}! Beat that! 🎮",
                        shareScore = true
                    )
                    viewModel.navigateTo(AppScreen.CHAT)
                },
                onLeaderboard = {
                    viewModel.navigateTo(AppScreen.LEADERBOARD)
                },
                onHome = {
                    viewModel.navigateTo(AppScreen.HOME)
                }
            )
        }
    }
}

@Composable
private fun GameHUD(
    score: Int,
    currentHearts: Int,
    maxHearts: Int,
    coins: Int,
    difficulty: GameDifficulty,
    theme: GameTheme,
    onPauseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Health System (Hearts) & Multiplier
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            PixelHeartRow(currentHearts = currentHearts, maxHearts = maxHearts)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .border(2.dp, Color.Black)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${difficulty.displayName} ${difficulty.scoreMultiplier}x",
                        fontFamily = RetroFontFamily,
                        fontSize = 11.sp,
                        color = theme.accentColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Coins collected
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(2.dp, Color.Black)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🟡 $coins",
                        fontFamily = RetroFontFamily,
                        fontSize = 11.sp,
                        color = PixelGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Center Score Counter (8-bit style with drop shadow)
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "$score",
                fontFamily = RetroFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 38.sp,
                color = Color.Black,
                modifier = Modifier.offset(x = 2.dp, y = 2.dp)
            )
            Text(
                text = "$score",
                fontFamily = RetroFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 38.sp,
                color = Color.White
            )
        }

        // Pause Button
        IconButton(
            onClick = onPauseClick,
            modifier = Modifier
                .border(2.dp, PixelBorderDark)
                .background(Color.Black.copy(alpha = 0.5f))
                .size(40.dp)
                .testTag("pause_button")
        ) {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Pause",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ReadyOverlay(difficulty: GameDifficulty) {
    PixelCard(
        backgroundColor = Color.Black.copy(alpha = 0.85f),
        borderColor = PixelGold,
        modifier = Modifier.padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "RETRO FLAP",
                fontFamily = RetroFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = PixelGold
            )

            Text(
                text = "TAP SCREEN TO FLY",
                fontFamily = RetroFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )

            Text(
                text = "❤️ 3 Hearts | Avoid Pipes | Collect Coins",
                fontFamily = RetroFontFamily,
                fontSize = 12.sp,
                color = Color.LightGray
            )

            Box(
                modifier = Modifier
                    .border(2.dp, Color.White)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${difficulty.displayName} MODE (${difficulty.scoreMultiplier}x COINS)",
                    fontFamily = RetroFontFamily,
                    fontSize = 11.sp,
                    color = Color.Cyan,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PauseOverlay(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onQuit: () -> Unit
) {
    PixelCard(
        backgroundColor = Color(0xFF1E293B),
        borderColor = Color.White,
        modifier = Modifier.padding(32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "PAUSED",
                fontFamily = RetroFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = Color.White
            )

            PixelButton(
                text = "RESUME",
                onClick = onResume,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFF73BF2E),
                testTag = "resume_button"
            )

            PixelButton(
                text = "RESTART",
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFFF25A24),
                testTag = "restart_button"
            )

            PixelButton(
                text = "MAIN MENU",
                onClick = onQuit,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFF475569),
                testTag = "menu_button"
            )
        }
    }
}

@Composable
private fun GameOverOverlay(
    score: Int,
    highScore: Int,
    coins: Int,
    difficulty: GameDifficulty,
    onRestart: () -> Unit,
    onShare: () -> Unit,
    onLeaderboard: () -> Unit,
    onHome: () -> Unit
) {
    PixelCard(
        backgroundColor = Color(0xFF10002B),
        borderColor = Color(0xFFFF007F),
        modifier = Modifier.padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "GAME OVER",
                fontFamily = RetroFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 26.sp,
                color = Color(0xFFFF2252)
            )

            // Score board card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFF7B2CBF))
                    .background(Color(0xFF240046))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "SCORE:",
                            fontFamily = RetroFontFamily,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "$score",
                            fontFamily = RetroFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF00FFFF)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "BEST:",
                            fontFamily = RetroFontFamily,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "$highScore",
                            fontFamily = RetroFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PixelGold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "COINS:",
                            fontFamily = RetroFontFamily,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "+$coins 🟡",
                            fontFamily = RetroFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PixelGold
                        )
                    }

                    if (score > highScore && score > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFB703))
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "★ NEW HIGH SCORE! ★",
                                fontFamily = RetroFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // Action Buttons
            PixelButton(
                text = "PLAY AGAIN",
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFF00F5D4),
                textColor = Color.Black,
                testTag = "play_again_button"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PixelButton(
                    text = "SHARE",
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFFFF007F),
                    fontSize = 12,
                    testTag = "share_score_button"
                )
                PixelButton(
                    text = "RANKS",
                    onClick = onLeaderboard,
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFF7B2CBF),
                    fontSize = 12,
                    testTag = "ranks_button"
                )
            }

            PixelButton(
                text = "MAIN MENU",
                onClick = onHome,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFF334155),
                fontSize = 12,
                testTag = "game_over_home_button"
            )
        }
    }
}

// Retro Canvas Drawing Helpers
private fun drawRetroPipe(
    scope: DrawScope,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    isTop: Boolean,
    theme: GameTheme
) {
    if (height <= 0) return
    val rimHeight = 28f
    val rimOverhang = 8f

    // Pipe Body
    val bodyY = if (isTop) y else y + rimHeight
    val bodyHeight = if (isTop) height - rimHeight else height - rimHeight

    if (bodyHeight > 0) {
        // Base pipe fill
        scope.drawRect(
            color = theme.pipeMainColor,
            topLeft = Offset(x, bodyY),
            size = Size(width, bodyHeight)
        )
        // Highlight band
        scope.drawRect(
            color = theme.pipeHighlightColor,
            topLeft = Offset(x + 6f, bodyY),
            size = Size(10f, bodyHeight)
        )
        // Shadow band
        scope.drawRect(
            color = theme.pipeRimColor,
            topLeft = Offset(x + width - 14f, bodyY),
            size = Size(14f, bodyHeight)
        )
        // Outlines
        scope.drawRect(
            color = Color.Black,
            topLeft = Offset(x, bodyY),
            size = Size(3f, bodyHeight)
        )
        scope.drawRect(
            color = Color.Black,
            topLeft = Offset(x + width - 3f, bodyY),
            size = Size(3f, bodyHeight)
        )
    }

    // Pipe Rim
    val rimY = if (isTop) y + height - rimHeight else y
    val rimX = x - rimOverhang
    val rimW = width + rimOverhang * 2f

    scope.drawRect(
        color = theme.pipeMainColor,
        topLeft = Offset(rimX, rimY),
        size = Size(rimW, rimHeight)
    )
    scope.drawRect(
        color = theme.pipeHighlightColor,
        topLeft = Offset(rimX + 8f, rimY),
        size = Size(12f, rimHeight)
    )
    scope.drawRect(
        color = theme.pipeRimColor,
        topLeft = Offset(rimX + rimW - 16f, rimY),
        size = Size(16f, rimHeight)
    )
    // Rim 8-bit black border
    scope.drawRect(
        color = Color.Black,
        topLeft = Offset(rimX, rimY),
        size = Size(rimW, 3f)
    )
    scope.drawRect(
        color = Color.Black,
        topLeft = Offset(rimX, rimY + rimHeight - 3f),
        size = Size(rimW, 3f)
    )
    scope.drawRect(
        color = Color.Black,
        topLeft = Offset(rimX, rimY),
        size = Size(3f, rimHeight)
    )
    scope.drawRect(
        color = Color.Black,
        topLeft = Offset(rimX + rimW - 3f, rimY),
        size = Size(3f, rimHeight)
    )
}

private fun drawRetroCoin(scope: DrawScope, x: Float, y: Float) {
    val r = 12f
    scope.drawCircle(color = Color.Black, radius = r + 2f, center = Offset(x, y))
    scope.drawCircle(color = PixelGold, radius = r, center = Offset(x, y))
    scope.drawCircle(color = Color(0xFFFFF3B0), radius = r - 4f, center = Offset(x, y))
    scope.drawCircle(color = PixelGold, radius = r - 6f, center = Offset(x, y))
}

private fun drawFloatingHeart(scope: DrawScope, x: Float, y: Float) {
    val p = 3.5f
    val matrix = arrayOf(
        intArrayOf(0, 1, 1, 0, 1, 1, 0),
        intArrayOf(1, 2, 2, 1, 2, 2, 1),
        intArrayOf(1, 2, 2, 2, 2, 2, 1),
        intArrayOf(0, 1, 2, 2, 2, 1, 0),
        intArrayOf(0, 0, 1, 2, 1, 0, 0),
        intArrayOf(0, 0, 0, 1, 0, 0, 0)
    )
    for (r in matrix.indices) {
        for (c in matrix[r].indices) {
            val cell = matrix[r][c]
            if (cell != 0) {
                val color = if (cell == 1) Color.Black else Color(0xFFFF2252)
                scope.drawRect(
                    color = color,
                    topLeft = Offset(x - 12f + c * p, y - 10f + r * p),
                    size = Size(p, p)
                )
            }
        }
    }
}

private fun drawRetroGround(scope: DrawScope, scrollOffset: Float, theme: GameTheme) {
    val groundY = scope.size.height - 110f
    val groundHeight = 110f
    val width = scope.size.width

    // Dirt base
    scope.drawRect(
        color = theme.groundColor,
        topLeft = Offset(0f, groundY),
        size = Size(width, groundHeight)
    )

    // Top border line
    scope.drawRect(
        color = Color.Black,
        topLeft = Offset(0f, groundY),
        size = Size(width, 4f)
    )

    // Grass / Neon grid top strip
    val grassHeight = 18f
    scope.drawRect(
        color = theme.groundGrassColor,
        topLeft = Offset(0f, groundY + 4f),
        size = Size(width, grassHeight)
    )

    // Repeating 8-bit blocks
    val blockSize = 32f
    var startX = -scrollOffset
    while (startX < width) {
        scope.drawRect(
            color = theme.pipeRimColor.copy(alpha = 0.35f),
            topLeft = Offset(startX, groundY + 26f),
            size = Size(16f, 8f)
        )
        scope.drawRect(
            color = Color.Black.copy(alpha = 0.2f),
            topLeft = Offset(startX + 16f, groundY + 54f),
            size = Size(16f, 8f)
        )
        startX += blockSize
    }
}

private fun drawRetroScenery(scope: DrawScope, groundScrollX: Float, theme: GameTheme) {
    val groundY = scope.size.height - 110f
    val width = scope.size.width

    // Distant background parallax layer (moves at gentle 20% speed)
    val bgScroll = groundScrollX * 0.2f

    when (theme) {
        GameTheme.CLASSIC_DAY -> {
            // Distant rolling 8-bit hills
            val hillColor = Color(0xFF5BA842).copy(alpha = 0.55f)
            val hillHighlight = Color(0xFF7DD860).copy(alpha = 0.45f)
            val hillStep = 80f
            var hx = - (bgScroll % hillStep) - hillStep
            while (hx < width + hillStep) {
                scope.drawRect(color = hillColor, topLeft = Offset(hx, groundY - 42f), size = Size(50f, 42f))
                scope.drawRect(color = hillHighlight, topLeft = Offset(hx + 10f, groundY - 56f), size = Size(30f, 14f))
                hx += hillStep
            }

            // Distant soft bushes right above ground
            val bushColor = Color(0xFF438A30).copy(alpha = 0.65f)
            val bushStep = 48f
            var bx = - (groundScrollX * 0.4f % bushStep) - bushStep
            while (bx < width + bushStep) {
                scope.drawRect(color = bushColor, topLeft = Offset(bx, groundY - 18f), size = Size(32f, 18f))
                scope.drawRect(color = Color(0xFF336B23).copy(alpha = 0.5f), topLeft = Offset(bx + 4f, groundY - 24f), size = Size(24f, 6f))
                bx += bushStep
            }

            // Parallax clouds
            drawRetroClouds(scope, groundScrollX * 0.15f)
        }
        GameTheme.DARK_NIGHT -> {
            // Distant 8-bit City Skyline
            val buildingColor = Color(0xFF162032).copy(alpha = 0.75f)
            val windowColor = Color(0xFFFFD166).copy(alpha = 0.45f)
            val step = 56f
            var bx = - (bgScroll % step) - step
            var i = 0
            while (bx < width + step) {
                val bHeight = 36f + (i % 5) * 14f
                scope.drawRect(color = buildingColor, topLeft = Offset(bx, groundY - bHeight), size = Size(46f, bHeight))
                if (i % 2 == 0) {
                    scope.drawRect(color = windowColor, topLeft = Offset(bx + 10f, groundY - bHeight + 10f), size = Size(8f, 10f))
                    scope.drawRect(color = windowColor, topLeft = Offset(bx + 26f, groundY - bHeight + 24f), size = Size(8f, 10f))
                }
                bx += step
                i++
            }
            drawRetroStars(scope, theme)
        }
        GameTheme.VIOLET_CYAN_EVENT -> {
            // Synthwave cyber grid backdrop
            val horizonY = groundY - 45f
            scope.drawLine(
                color = Color(0xFF00E5FF).copy(alpha = 0.45f),
                start = Offset(0f, horizonY),
                end = Offset(width, horizonY),
                strokeWidth = 2f
            )

            // Cyber pyramids
            val pyrColor = Color(0xFF2B0A4E).copy(alpha = 0.7f)
            val neonEdge = Color(0xFF00E5FF).copy(alpha = 0.55f)
            val pStep = 90f
            var px = - (bgScroll % pStep) - pStep
            while (px < width + pStep) {
                scope.drawRect(color = pyrColor, topLeft = Offset(px, horizonY - 36f), size = Size(64f, 36f))
                scope.drawRect(color = neonEdge, topLeft = Offset(px + 18f, horizonY - 50f), size = Size(28f, 14f))
                px += pStep
            }
            drawRetroStars(scope, theme)
        }
    }
}

private fun drawRetroClouds(scope: DrawScope, offset: Float = 0f) {
    val cloudColor = Color.White.copy(alpha = 0.8f)
    val width = scope.size.width
    val cloudY1 = 120f
    val cloudY2 = 175f

    val c1X = ((50f - offset) % (width + 120f) + width + 120f) % (width + 120f) - 60f
    val c2X = ((240f - offset * 0.7f) % (width + 150f) + width + 150f) % (width + 150f) - 60f

    // Cloud 1
    scope.drawRect(color = cloudColor, topLeft = Offset(c1X, cloudY1), size = Size(74f, 20f))
    scope.drawRect(color = cloudColor, topLeft = Offset(c1X + 16f, cloudY1 - 12f), size = Size(42f, 12f))

    // Cloud 2
    scope.drawRect(color = cloudColor, topLeft = Offset(c2X, cloudY2), size = Size(96f, 22f))
    scope.drawRect(color = cloudColor, topLeft = Offset(c2X + 22f, cloudY2 - 14f), size = Size(52f, 14f))
}

private fun drawRetroStars(scope: DrawScope, theme: GameTheme) {
    val starColor = if (theme == GameTheme.VIOLET_CYAN_EVENT) Color.Cyan else Color.White
    val stars = listOf(
        Pair(30f, 60f), Pair(90f, 140f), Pair(180f, 50f), Pair(260f, 120f),
        Pair(320f, 40f), Pair(370f, 160f), Pair(120f, 220f), Pair(290f, 240f)
    )
    for ((sx, sy) in stars) {
        scope.drawRect(
            color = starColor,
            topLeft = Offset(sx, sy),
            size = Size(4f, 4f)
        )
    }
}
