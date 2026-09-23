package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class GameRepository(
    private val playerDao: PlayerDao,
    private val leaderboardDao: LeaderboardDao,
    private val chatDao: ChatDao,
    private val achievementDao: AchievementDao
) {
    val playerProfile: Flow<PlayerProfileEntity?> = playerDao.getPlayerProfile()
    val allLeaderboard: Flow<List<LeaderboardEntryEntity>> = leaderboardDao.getAllLeaderboardEntries()
    val friendLeaderboard: Flow<List<LeaderboardEntryEntity>> = leaderboardDao.getFriendLeaderboardEntries()
    val allChatMessages: Flow<List<ChatMessageEntity>> = chatDao.getAllMessages()
    val allAchievements: Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()

    suspend fun initializeDefaultsIfNeeded() {
        // Init profile if empty
        if (playerDao.getPlayerProfileSync() == null) {
            val initialProfile = PlayerProfileEntity(
                playerName = "PixelHero",
                avatarId = "classic_yellow",
                selectedTitle = "Arcade Pilot",
                coins = 200,
                equippedSkin = "classic_yellow",
                unlockedSkins = "classic_yellow",
                cloudSyncCode = generateCloudCode()
            )
            playerDao.insertOrUpdateProfile(initialProfile)
        }

        // Init achievements if empty
        if (achievementDao.getCount() == 0) {
            val defaultAchievements = listOf(
                AchievementEntity("first_flap", "First Wings", "Take your very first flight", 50, "wing"),
                AchievementEntity("pipe_10", "Pipeliner", "Clear 10 pipes in a single flight", 100, "pipe"),
                AchievementEntity("pipe_25", "Sky Master", "Clear 25 pipes in a single flight", 250, "medal"),
                AchievementEntity("score_50", "Half-Century", "Reach a score of 50 in any difficulty", 300, "trophy"),
                AchievementEntity("score_100", "Century Legend", "Score 100 points in one game", 600, "crown"),
                AchievementEntity("coins_50", "Coin Collector", "Collect 50 gold coins total", 150, "coin"),
                AchievementEntity("coins_200", "Arcade Tycoon", "Collect 200 coins total", 400, "gem"),
                AchievementEntity("hard_mode", "Daredevil", "Survive past 15 score on Hard difficulty", 350, "fire"),
                AchievementEntity("insane_mode", "Cyber Survivor", "Score 20+ on Cyber Insane difficulty", 500, "skull"),
                AchievementEntity("heart_saver", "Heart Saver", "Pick up a floating heart during flight", 100, "heart"),
                AchievementEntity("cyber_event", "Neon Hacker", "Play with the Violet & Cyan Cyber theme", 150, "sparkles"),
                AchievementEntity("fashion_bird", "Style Icon", "Unlock and equip 3 distinct bird skins", 300, "palette"),
                AchievementEntity("streak_3", "Loyal Flapper", "Log in for 3 consecutive days", 250, "calendar")
            )
            achievementDao.insertDefaultAchievements(defaultAchievements)
        }

        // Init leaderboard with vibrant arcade competitors & friends if empty
        if (leaderboardDao.getCount() == 0) {
            val seedLeaderboard = listOf(
                LeaderboardEntryEntity("lb_1", "CyberAce", "cyber_violet", 142, "INSANE", false, false, System.currentTimeMillis() - 3600000),
                LeaderboardEntryEntity("lb_2", "RetroFox", "golden_bird", 128, "HARD", true, false, System.currentTimeMillis() - 7200000),
                LeaderboardEntryEntity("lb_3", "PixelQueen", "classic_yellow", 115, "NORMAL", false, false, System.currentTimeMillis() - 10800000),
                LeaderboardEntryEntity("lb_4", "NeonKnight", "cyber_violet", 98, "HARD", false, false, System.currentTimeMillis() - 14400000),
                LeaderboardEntryEntity("lb_5", "Alex_Bestie", "astro_robo", 84, "NORMAL", true, false, System.currentTimeMillis() - 18000000),
                LeaderboardEntryEntity("lb_6", "BitCrusher", "shadow_bat", 76, "INSANE", false, false, System.currentTimeMillis() - 21600000),
                LeaderboardEntryEntity("lb_7", "Sarah_Pixel", "classic_yellow", 65, "EASY", true, false, System.currentTimeMillis() - 25200000),
                LeaderboardEntryEntity("lb_8", "ChiptuneHero", "golden_bird", 54, "NORMAL", false, false, System.currentTimeMillis() - 28800000),
                LeaderboardEntryEntity("lb_9", "VoxelMaster", "shadow_bat", 48, "HARD", false, false, System.currentTimeMillis() - 32400000),
                LeaderboardEntryEntity("lb_10", "Dave_Flaps", "astro_robo", 39, "NORMAL", true, false, System.currentTimeMillis() - 36000000)
            )
            leaderboardDao.insertEntries(seedLeaderboard)
        }

        // Init chat with lively retro community chatter if empty
        if (chatDao.getCount() == 0) {
            val seedChat = listOf(
                ChatMessageEntity(UUID.randomUUID().toString(), "CyberAce", "cyber_violet", "Cyber event is insane! Who can beat 142?", 142, "INSANE", System.currentTimeMillis() - 1800000, false),
                ChatMessageEntity(UUID.randomUUID().toString(), "RetroFox", "golden_bird", "Pipes in hard mode are so fast! Golden skin gives good luck though ✨", null, null, System.currentTimeMillis() - 1200000, false),
                ChatMessageEntity(UUID.randomUUID().toString(), "PixelQueen", "classic_yellow", "Just got Century Legend achievement! 🏆", 115, "NORMAL", System.currentTimeMillis() - 600000, false),
                ChatMessageEntity(UUID.randomUUID().toString(), "ArcadeBot", "astro_robo", "Welcome all pilots to the Global 8-Bit Flap Terminal!", null, null, System.currentTimeMillis() - 300000, false)
            )
            chatDao.insertMessages(seedChat)
        }
    }

    suspend fun saveGameResult(
        score: Int,
        coinsEarned: Int,
        pipesPassed: Int,
        difficultyName: String,
        isEventTheme: Boolean
    ): Boolean {
        val currentProfile = playerDao.getPlayerProfileSync() ?: return false
        var isNewHighScore = false

        val updatedHighScore = if (score > currentProfile.highScore) {
            isNewHighScore = true
            score
        } else currentProfile.highScore

        val updatedEasy = if (difficultyName == "EASY" && score > currentProfile.easyHighScore) score else currentProfile.easyHighScore
        val updatedNormal = if (difficultyName == "NORMAL" && score > currentProfile.normalHighScore) score else currentProfile.normalHighScore
        val updatedHard = if (difficultyName == "HARD" && score > currentProfile.hardHighScore) score else currentProfile.hardHighScore
        val updatedInsane = if (difficultyName == "INSANE" && score > currentProfile.insaneHighScore) score else currentProfile.insaneHighScore

        val updated = currentProfile.copy(
            coins = currentProfile.coins + coinsEarned,
            highScore = updatedHighScore,
            easyHighScore = updatedEasy,
            normalHighScore = updatedNormal,
            hardHighScore = updatedHard,
            insaneHighScore = updatedInsane,
            totalGamesPlayed = currentProfile.totalGamesPlayed + 1,
            totalPipesPassed = currentProfile.totalPipesPassed + pipesPassed,
            totalCoinsCollected = currentProfile.totalCoinsCollected + coinsEarned
        )
        playerDao.insertOrUpdateProfile(updated)

        // Add or update to leaderboard if high score or valid attempt
        if (score > 0) {
            val userEntry = LeaderboardEntryEntity(
                id = "user_high_score",
                playerName = currentProfile.playerName,
                avatarId = currentProfile.equippedSkin,
                score = updatedHighScore,
                difficulty = difficultyName,
                isFriend = false,
                isUser = true,
                timestamp = System.currentTimeMillis()
            )
            leaderboardDao.insertEntry(userEntry)
        }

        // Check Achievements
        unlockAchievement("first_flap")
        if (pipesPassed >= 10) unlockAchievement("pipe_10")
        if (pipesPassed >= 25) unlockAchievement("pipe_25")
        if (score >= 50) unlockAchievement("score_50")
        if (score >= 100) unlockAchievement("score_100")
        if (updated.totalCoinsCollected >= 50) unlockAchievement("coins_50")
        if (updated.totalCoinsCollected >= 200) unlockAchievement("coins_200")
        if (difficultyName == "HARD" && score >= 15) unlockAchievement("hard_mode")
        if (difficultyName == "INSANE" && score >= 20) unlockAchievement("insane_mode")
        if (isEventTheme) unlockAchievement("cyber_event")

        return isNewHighScore
    }

    suspend fun unlockAchievement(id: String) {
        val affected = achievementDao.unlockAchievement(id, System.currentTimeMillis())
        if (affected > 0) {
            // Reward coins
            val profile = playerDao.getPlayerProfileSync()
            if (profile != null) {
                // Determine reward
                val reward = when (id) {
                    "first_flap" -> 50
                    "pipe_10" -> 100
                    "pipe_25" -> 250
                    "score_50" -> 300
                    "score_100" -> 600
                    "coins_50" -> 150
                    "coins_200" -> 400
                    "hard_mode" -> 350
                    "insane_mode" -> 500
                    "heart_saver" -> 100
                    "cyber_event" -> 150
                    "fashion_bird" -> 300
                    "streak_3" -> 250
                    else -> 100
                }
                playerDao.insertOrUpdateProfile(profile.copy(coins = profile.coins + reward))
            }
        }
    }

    suspend fun sendChatMessage(message: String, sharedScore: Int? = null, sharedDifficulty: String? = null) {
        val profile = playerDao.getPlayerProfileSync() ?: return
        val newMsg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            senderName = profile.playerName,
            avatarId = profile.equippedSkin,
            message = message,
            sharedScore = sharedScore,
            sharedDifficulty = sharedDifficulty,
            timestamp = System.currentTimeMillis(),
            isCurrentUser = true
        )
        chatDao.insertMessage(newMsg)
    }

    suspend fun updateProfile(name: String, avatarId: String, title: String) {
        val profile = playerDao.getPlayerProfileSync() ?: return
        val updated = profile.copy(
            playerName = name.trim().ifEmpty { "PixelHero" },
            avatarId = avatarId,
            selectedTitle = title
        )
        playerDao.insertOrUpdateProfile(updated)
    }

    suspend fun purchaseSkin(skinId: String, price: Int): Boolean {
        val profile = playerDao.getPlayerProfileSync() ?: return false
        val ownedSkins = profile.unlockedSkins.split(",").map { it.trim() }
        if (ownedSkins.contains(skinId)) {
            // Already owned, just equip
            playerDao.insertOrUpdateProfile(profile.copy(equippedSkin = skinId))
            return true
        }
        if (profile.coins >= price) {
            val newOwned = (ownedSkins + skinId).joinToString(",")
            val updated = profile.copy(
                coins = profile.coins - price,
                equippedSkin = skinId,
                unlockedSkins = newOwned
            )
            playerDao.insertOrUpdateProfile(updated)
            if (newOwned.split(",").size >= 3) {
                unlockAchievement("fashion_bird")
            }
            return true
        }
        return false
    }

    suspend fun equipSkin(skinId: String) {
        val profile = playerDao.getPlayerProfileSync() ?: return
        val ownedSkins = profile.unlockedSkins.split(",").map { it.trim() }
        if (ownedSkins.contains(skinId)) {
            playerDao.insertOrUpdateProfile(profile.copy(equippedSkin = skinId))
        }
    }

    suspend fun purchaseUpgrade(upgradeType: String, cost: Int): Boolean {
        val profile = playerDao.getPlayerProfileSync() ?: return false
        if (profile.coins < cost) return false

        val updated = when (upgradeType) {
            "extra_heart" -> {
                if (profile.extraHeartsUnlocked >= 2) return false
                profile.copy(coins = profile.coins - cost, extraHeartsUnlocked = profile.extraHeartsUnlocked + 1)
            }
            "shield_magnet" -> {
                if (profile.shieldMagnetUnlocked) return false
                profile.copy(coins = profile.coins - cost, shieldMagnetUnlocked = true)
            }
            else -> return false
        }
        playerDao.insertOrUpdateProfile(updated)
        return true
    }

    suspend fun claimDailyBonus(): DailyBonusResult {
        val profile = playerDao.getPlayerProfileSync() ?: return DailyBonusResult(false, "No profile", 0, 1)
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        val diff = now - profile.lastBonusClaimDateMillis

        val todayDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(now))
        val lastClaimDateStr = if (profile.lastBonusClaimDateMillis > 0) {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(profile.lastBonusClaimDateMillis))
        } else ""

        if (todayDateStr == lastClaimDateStr) {
            val hoursLeft = (24 - (diff % oneDayMillis) / (60 * 60 * 1000L)).coerceIn(1, 24)
            return DailyBonusResult(false, "Already claimed today! Next reward in ~$hoursLeft hours", 0, profile.loginStreakDays)
        }

        // Check if streak broke (> 48 hours)
        val newStreak = if (diff < 48 * 60 * 60 * 1000L && profile.lastBonusClaimDateMillis > 0) {
            ((profile.loginStreakDays % 7) + 1)
        } else {
            1
        }

        val bonusCoins = when (newStreak) {
            1 -> 50
            2 -> 100
            3 -> 150
            4 -> 200
            5 -> 300
            6 -> 500
            7 -> 1000
            else -> 100
        }

        val updated = profile.copy(
            coins = profile.coins + bonusCoins,
            loginStreakDays = newStreak,
            lastBonusClaimDateMillis = now
        )
        playerDao.insertOrUpdateProfile(updated)

        if (newStreak >= 3) {
            unlockAchievement("streak_3")
        }

        return DailyBonusResult(true, "Claimed Day $newStreak bonus: +$bonusCoins Coins!", bonusCoins, newStreak)
    }

    suspend fun syncCloud(): String {
        val profile = playerDao.getPlayerProfileSync() ?: return "Error: No profile"
        val now = System.currentTimeMillis()
        val code = profile.cloudSyncCode.ifEmpty { generateCloudCode() }
        val updated = profile.copy(
            cloudSyncCode = code,
            lastCloudSyncTimestamp = now
        )
        playerDao.insertOrUpdateProfile(updated)
        return code
    }

    suspend fun restoreFromCloudCode(code: String): Boolean {
        if (code.isBlank() || code.length < 6) return false
        val profile = playerDao.getPlayerProfileSync() ?: return false

        // Parse or restore simulated cloud state for code
        val hash = code.hashCode()
        val restoredCoins = (profile.coins + 300).coerceAtLeast(400)
        val restoredHighScore = (profile.highScore + 15).coerceAtLeast(35)
        val updated = profile.copy(
            cloudSyncCode = code.uppercase().trim(),
            coins = restoredCoins,
            highScore = restoredHighScore,
            lastCloudSyncTimestamp = System.currentTimeMillis()
        )
        playerDao.insertOrUpdateProfile(updated)
        return true
    }

    private fun generateCloudCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val part1 = (1..4).map { chars.random() }.joinToString("")
        val part2 = (1..4).map { chars.random() }.joinToString("")
        return "RETRO-$part1-$part2"
    }
}

data class DailyBonusResult(
    val success: Boolean,
    val message: String,
    val coinsAwarded: Int,
    val currentStreak: Int
)
