package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.BirdSkin
import com.example.game.GameDifficulty
import com.example.ui.theme.PixelBorderDark
import com.example.ui.theme.PixelGold
import com.example.ui.theme.PixelHeartBorder
import com.example.ui.theme.PixelHeartRed
import com.example.ui.theme.RetroFontFamily

@Composable
fun PixelButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    borderColor: Color = PixelBorderDark,
    enabled: Boolean = true,
    fontSize: Int = 16,
    testTag: String = "pixel_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offsetY = if (isPressed) 3.dp else 0.dp

    Box(
        modifier = modifier
            .testTag(testTag)
            .offset(y = offsetY)
            .background(if (enabled) backgroundColor else Color.Gray)
            .border(3.dp, borderColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Pixel shadow text effect
        Text(
            text = text,
            fontFamily = RetroFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize.sp,
            color = Color.Black.copy(alpha = 0.4f),
            modifier = Modifier.offset(x = 1.5.dp, y = 1.5.dp)
        )
        Text(
            text = text,
            fontFamily = RetroFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize.sp,
            color = if (enabled) textColor else Color.LightGray
        )
    }
}

@Composable
fun PixelCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    borderWidth: Dp = 3.dp,
    padding: PaddingValues = PaddingValues(12.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .border(borderWidth, borderColor)
            .background(backgroundColor)
            .padding(padding)
    ) {
        content()
    }
}

@Composable
fun PixelHeartRow(
    currentHearts: Int,
    maxHearts: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until maxHearts) {
            val isFull = i < currentHearts
            PixelHeart(isFull = isFull)
        }
    }
}

@Composable
fun PixelHeart(
    isFull: Boolean,
    size: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "heart_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isFull) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heart_scale"
    )

    Canvas(
        modifier = modifier
            .size(size * pulseScale)
    ) {
        drawPixelHeart(isFull)
    }
}

private fun DrawScope.drawPixelHeart(isFull: Boolean) {
    val pixelSize = size.width / 9f
    val heartMatrix = arrayOf(
        intArrayOf(0, 1, 1, 0, 0, 0, 1, 1, 0),
        intArrayOf(1, 2, 2, 1, 0, 1, 2, 2, 1),
        intArrayOf(1, 2, 2, 2, 1, 2, 2, 2, 1),
        intArrayOf(1, 2, 2, 2, 2, 2, 2, 2, 1),
        intArrayOf(0, 1, 2, 2, 2, 2, 2, 1, 0),
        intArrayOf(0, 0, 1, 2, 2, 2, 1, 0, 0),
        intArrayOf(0, 0, 0, 1, 2, 1, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 1, 0, 0, 0, 0)
    )

    val fillColor = if (isFull) PixelHeartRed else Color(0xFF333333)
    val highlightColor = if (isFull) Color(0xFFFF85A1) else Color(0xFF555555)
    val borderColor = if (isFull) PixelHeartBorder else Color(0xFF111111)

    for (r in heartMatrix.indices) {
        for (c in heartMatrix[r].indices) {
            val cell = heartMatrix[r][c]
            if (cell != 0) {
                val color = when {
                    cell == 1 -> borderColor
                    r == 1 && (c == 2 || c == 6) -> highlightColor
                    else -> fillColor
                }
                drawRect(
                    color = color,
                    topLeft = Offset(c * pixelSize, r * pixelSize),
                    size = Size(pixelSize, pixelSize)
                )
            }
        }
    }
}

@Composable
fun PixelBirdAvatar(
    skinId: String,
    modifier: Modifier = Modifier.size(44.dp)
) {
    val skin = BirdSkin.getById(skinId)
    Canvas(modifier = modifier) {
        drawPixelBird(skin, wingFrame = 1)
    }
}

fun DrawScope.drawPixelBird(skin: BirdSkin, wingFrame: Int = 0, birdWidth: Float = size.width) {
    val p = birdWidth / 14f // 14x10 pixel grid

    // Simple pixel bird sprite matrix
    val body = arrayOf(
        intArrayOf(0,0,0,0,1,1,1,1,1,0,0,0,0,0),
        intArrayOf(0,0,0,1,2,2,2,3,3,1,0,0,0,0),
        intArrayOf(0,0,1,2,2,2,3,4,3,3,1,0,0,0),
        intArrayOf(0,1,2,2,2,2,3,3,3,1,5,5,1,0),
        intArrayOf(1,2,2,6,6,2,2,3,1,5,5,5,5,1),
        intArrayOf(1,2,6,6,6,6,2,1,5,5,1,1,1,0),
        intArrayOf(1,2,6,6,6,6,2,1,5,5,5,1,0,0),
        intArrayOf(0,1,2,6,6,2,2,2,1,1,1,0,0,0),
        intArrayOf(0,0,1,2,2,2,2,1,0,0,0,0,0,0),
        intArrayOf(0,0,0,1,1,1,1,0,0,0,0,0,0,0)
    )

    for (r in body.indices) {
        for (c in body[r].indices) {
            val type = body[r][c]
            if (type != 0) {
                val color = when (type) {
                    1 -> Color.Black // Outline
                    2 -> skin.primaryColor // Body
                    3 -> skin.secondaryColor // Cheeks/belly
                    4 -> skin.eyeColor // Eye pupil
                    5 -> skin.beakColor // Beak
                    6 -> when (wingFrame) {
                        0 -> skin.secondaryColor // wing neutral
                        1 -> skin.primaryColor // wing up
                        else -> Color(0xFFC07000) // wing down
                    }
                    else -> skin.primaryColor
                }
                drawRect(
                    color = color,
                    topLeft = Offset(c * p, r * p),
                    size = Size(p, p)
                )
            }
        }
    }
}

@Composable
fun DifficultyBadge(
    difficulty: GameDifficulty,
    modifier: Modifier = Modifier
) {
    val bgColor = when (difficulty) {
        GameDifficulty.EASY -> Color(0xFF2E7D32)
        GameDifficulty.NORMAL -> Color(0xFF0284C7)
        GameDifficulty.HARD -> Color(0xFFD97706)
        GameDifficulty.INSANE -> Color(0xFF9333EA)
    }

    Box(
        modifier = modifier
            .border(2.dp, Color.Black)
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = "${difficulty.displayName} (${difficulty.scoreMultiplier}x)",
            color = Color.White,
            fontFamily = RetroFontFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ScanlineOverlay(
    modifier: Modifier = Modifier,
    alpha: Float = 0.08f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val scanlineHeight = 3.dp.toPx()
        var y = 0f
        while (y < size.height) {
            drawRect(
                color = Color.Black.copy(alpha = alpha),
                topLeft = Offset(0f, y),
                size = Size(size.width, scanlineHeight)
            )
            y += scanlineHeight * 2f
        }
    }
}
