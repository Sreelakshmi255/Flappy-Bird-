package com.example.game

import androidx.compose.ui.graphics.Color

enum class GameDifficulty(
    val displayName: String,
    val scoreMultiplier: Float,
    val pipeGapRatio: Float, // Multiplier for vertical gap between pipes
    val pipeSpeedRatio: Float,
    val startingHearts: Int,
    val description: String
) {
    EASY(
        displayName = "EASY",
        scoreMultiplier = 1.0f,
        pipeGapRatio = 1.35f,
        pipeSpeedRatio = 0.85f,
        startingHearts = 4,
        description = "Wider gaps, gentle speed, 4 hearts"
    ),
    NORMAL(
        displayName = "NORMAL",
        scoreMultiplier = 1.5f,
        pipeGapRatio = 1.0f,
        pipeSpeedRatio = 1.0f,
        startingHearts = 3,
        description = "Classic arcade challenge, 3 hearts"
    ),
    HARD(
        displayName = "HARD",
        scoreMultiplier = 2.0f,
        pipeGapRatio = 0.85f,
        pipeSpeedRatio = 1.25f,
        startingHearts = 2,
        description = "Tight gaps, faster speed, 2 hearts"
    ),
    INSANE(
        displayName = "CYBER INSANE",
        scoreMultiplier = 3.0f,
        pipeGapRatio = 0.72f,
        pipeSpeedRatio = 1.55f,
        startingHearts = 1,
        description = "Brutal speed, narrow gaps, 1 heart"
    )
}

enum class GameTheme(
    val displayName: String,
    val skyColorTop: Color,
    val skyColorBottom: Color,
    val pipeMainColor: Color,
    val pipeRimColor: Color,
    val pipeHighlightColor: Color,
    val groundColor: Color,
    val groundGrassColor: Color,
    val textColor: Color,
    val accentColor: Color,
    val isEventTheme: Boolean = false
) {
    CLASSIC_DAY(
        displayName = "8-Bit Retro Day",
        skyColorTop = Color(0xFF4EC0CA),
        skyColorBottom = Color(0xFF90E0EF),
        pipeMainColor = Color(0xFF73BF2E),
        pipeRimColor = Color(0xFF558022),
        pipeHighlightColor = Color(0xFF9CE659),
        groundColor = Color(0xFFDED895),
        groundGrassColor = Color(0xFF73BF2E),
        textColor = Color(0xFF1E293B),
        accentColor = Color(0xFFFFB703)
    ),
    DARK_NIGHT(
        displayName = "Retro Dark Mode",
        skyColorTop = Color(0xFF0F172A),
        skyColorBottom = Color(0xFF1E293B),
        pipeMainColor = Color(0xFF334155),
        pipeRimColor = Color(0xFF1E293B),
        pipeHighlightColor = Color(0xFF64748B),
        groundColor = Color(0xFF1E1E24),
        groundGrassColor = Color(0xFF0284C7),
        textColor = Color(0xFFF1F5F9),
        accentColor = Color(0xFF38BDF8)
    ),
    VIOLET_CYAN_EVENT(
        displayName = "Violet & Cyan Cyber Event",
        skyColorTop = Color(0xFF1A0B2E), // Deep Cyber Violet
        skyColorBottom = Color(0xFF050510), // Pitch Black Void
        pipeMainColor = Color(0xFF00E5FF), // Radiant Neon Cyan
        pipeRimColor = Color(0xFF7B2CBF), // Electric Violet rim
        pipeHighlightColor = Color(0xFFE0AAFF), // Magenta highlight
        groundColor = Color(0xFF10002B),
        groundGrassColor = Color(0xFF00F0FF), // Cyber grid neon
        textColor = Color(0xFF00FFFF),
        accentColor = Color(0xFFFF007F), // Neon Pink/Magenta
        isEventTheme = true
    )
}

data class BirdSkin(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
    val eyeColor: Color,
    val beakColor: Color,
    val auraColor: Color = Color.Transparent
) {
    companion object {
        val ALL_SKINS = listOf(
            BirdSkin(
                id = "classic_yellow",
                name = "Classic 8-Bit",
                description = "The timeless retro yellow flyer.",
                price = 0,
                primaryColor = Color(0xFFF7D51D),
                secondaryColor = Color(0xFFE69138),
                eyeColor = Color(0xFFFFFFFF),
                beakColor = Color(0xFFE85D04)
            ),
            BirdSkin(
                id = "cyber_violet",
                name = "Cyber Falcon",
                description = "Violet & Cyan event special edition!",
                price = 300,
                primaryColor = Color(0xFF9D4EDD),
                secondaryColor = Color(0xFF00F5D4),
                eyeColor = Color(0xFF00FFFF),
                beakColor = Color(0xFFFF007F),
                auraColor = Color(0x669D4EDD)
            ),
            BirdSkin(
                id = "golden_bird",
                name = "Golden Phoenix",
                description = "Shines brightly in the 8-bit sky.",
                price = 500,
                primaryColor = Color(0xFFFFD700),
                secondaryColor = Color(0xFFFFA500),
                eyeColor = Color(0xFFFFFFFF),
                beakColor = Color(0xFFFF4500),
                auraColor = Color(0x88FFD700)
            ),
            BirdSkin(
                id = "shadow_bat",
                name = "Pixel Bat",
                description = "Flits through the darkest night pipes.",
                price = 250,
                primaryColor = Color(0xFF3A0CA3),
                secondaryColor = Color(0xFF7209B7),
                eyeColor = Color(0xFFFF0055),
                beakColor = Color(0xFFF72585)
            ),
            BirdSkin(
                id = "astro_robo",
                name = "Astro Robo",
                description = "Cybernetic engine with laser precision.",
                price = 400,
                primaryColor = Color(0xFF48CAE4),
                secondaryColor = Color(0xFF0077B6),
                eyeColor = Color(0xFF00FFCC),
                beakColor = Color(0xFFF77F00)
            ),
            BirdSkin(
                id = "pixel_dragon",
                name = "Fire Drake",
                description = "Blazing red mythical winged flyer.",
                price = 600,
                primaryColor = Color(0xFFD90429),
                secondaryColor = Color(0xFFEF233C),
                eyeColor = Color(0xFFFFDD00),
                beakColor = Color(0xFFFF9F1C),
                auraColor = Color(0x66FF0000)
            )
        )

        fun getById(id: String): BirdSkin {
            return ALL_SKINS.find { it.id == id } ?: ALL_SKINS[0]
        }
    }
}

enum class FlapSensitivity(val displayName: String, val impulseFactor: Float) {
    GENTLE("Gentle", 0.90f),
    STANDARD("Standard", 1.0f),
    SNAPPY("Snappy", 1.12f)
}

enum class GameState {
    READY,
    PLAYING,
    PAUSED,
    GAME_OVER
}

data class PipeObstacle(
    val id: Long,
    var x: Float, // X coordinate in normalized screen width (0f..1f) or pixels
    val topHeight: Float, // height of top pipe in pixels
    val gapY: Float, // center of gap
    val gapHeight: Float, // height of gap in pixels
    val width: Float,
    var passed: Boolean = false
)

data class CollectibleCoin(
    val id: Long,
    var x: Float,
    var y: Float,
    var collected: Boolean = false,
    val value: Int = 1
)

data class CollectibleHeart(
    val id: Long,
    var x: Float,
    var y: Float,
    var collected: Boolean = false
)
