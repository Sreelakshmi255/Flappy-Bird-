package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val playerName: String = "PixelPilot",
    val avatarId: String = "classic_yellow",
    val selectedTitle: String = "Rookie Flapper",
    val coins: Int = 150,
    val equippedSkin: String = "classic_yellow",
    val unlockedSkins: String = "classic_yellow", // comma-separated skin ids
    val extraHeartsUnlocked: Int = 0,
    val shieldMagnetUnlocked: Boolean = false,
    val highScore: Int = 0,
    val easyHighScore: Int = 0,
    val normalHighScore: Int = 0,
    val hardHighScore: Int = 0,
    val insaneHighScore: Int = 0,
    val totalGamesPlayed: Int = 0,
    val totalPipesPassed: Int = 0,
    val totalCoinsCollected: Int = 0,
    val loginStreakDays: Int = 1,
    val lastBonusClaimDateMillis: Long = 0L,
    val cloudSyncCode: String = "RETRO-8BIT-7749",
    val lastCloudSyncTimestamp: Long = 0L
)

@Entity(tableName = "leaderboard")
data class LeaderboardEntryEntity(
    @PrimaryKey val id: String,
    val playerName: String,
    val avatarId: String,
    val score: Int,
    val difficulty: String, // EASY, NORMAL, HARD, INSANE
    val isFriend: Boolean = false,
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val senderName: String,
    val avatarId: String,
    val message: String,
    val sharedScore: Int? = null,
    val sharedDifficulty: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isCurrentUser: Boolean = false
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val rewardCoins: Int,
    val iconName: String,
    val isUnlocked: Boolean = false,
    val unlockedTimestamp: Long? = null
)
