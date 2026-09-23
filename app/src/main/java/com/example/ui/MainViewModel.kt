package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.RetroSoundManager
import com.example.data.AchievementEntity
import com.example.data.AppDatabase
import com.example.data.ChatMessageEntity
import com.example.data.DailyBonusResult
import com.example.data.GameRepository
import com.example.data.LeaderboardEntryEntity
import com.example.data.PlayerProfileEntity
import com.example.game.FlapSensitivity
import com.example.game.GameDifficulty
import com.example.game.GameTheme
import com.example.notification.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    GAME,
    LEADERBOARD,
    CHAT,
    SHOP,
    ACHIEVEMENTS,
    TUTORIAL,
    PROFILE
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val soundManager = RetroSoundManager(application)
    val notificationHelper = NotificationHelper(application)

    private val repository: GameRepository
    init {
        val db = AppDatabase.getDatabase(application)
        repository = GameRepository(
            playerDao = db.playerDao(),
            leaderboardDao = db.leaderboardDao(),
            chatDao = db.chatDao(),
            achievementDao = db.achievementDao()
        )
        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
        }
    }

    val playerProfile: StateFlow<PlayerProfileEntity?> = repository.playerProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val leaderboard: StateFlow<List<LeaderboardEntryEntity>> = repository.allLeaderboard
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val friendLeaderboard: StateFlow<List<LeaderboardEntryEntity>> = repository.friendLeaderboard
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.allChatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements: StateFlow<List<AchievementEntity>> = repository.allAchievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Configuration & States
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedTheme = MutableStateFlow(GameTheme.CLASSIC_DAY)
    val selectedTheme: StateFlow<GameTheme> = _selectedTheme.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow(GameDifficulty.NORMAL)
    val selectedDifficulty: StateFlow<GameDifficulty> = _selectedDifficulty.asStateFlow()

    private val _sensitivity = MutableStateFlow(FlapSensitivity.STANDARD)
    val sensitivity: StateFlow<FlapSensitivity> = _sensitivity.asStateFlow()

    private val _scanlinesEnabled = MutableStateFlow(true)
    val scanlinesEnabled: StateFlow<Boolean> = _scanlinesEnabled.asStateFlow()

    private val _showDailyBonusDialog = MutableStateFlow(false)
    val showDailyBonusDialog: StateFlow<Boolean> = _showDailyBonusDialog.asStateFlow()

    private val _dailyBonusResult = MutableStateFlow<DailyBonusResult?>(null)
    val dailyBonusResult: StateFlow<DailyBonusResult?> = _dailyBonusResult.asStateFlow()

    private val _compareTarget = MutableStateFlow<LeaderboardEntryEntity?>(null)
    val compareTarget: StateFlow<LeaderboardEntryEntity?> = _compareTarget.asStateFlow()

    private val _cloudMessage = MutableStateFlow<String?>(null)
    val cloudMessage: StateFlow<String?> = _cloudMessage.asStateFlow()

    private val _latestAchievementUnlocked = MutableStateFlow<String?>(null)
    val latestAchievementUnlocked: StateFlow<String?> = _latestAchievementUnlocked.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        soundManager.playButtonClick()
        _currentScreen.value = screen
    }

    fun setTheme(theme: GameTheme) {
        soundManager.playButtonClick()
        _selectedTheme.value = theme
        if (theme == GameTheme.VIOLET_CYAN_EVENT) {
            viewModelScope.launch {
                repository.unlockAchievement("cyber_event")
            }
        }
    }

    fun setDifficulty(diff: GameDifficulty) {
        soundManager.playButtonClick()
        _selectedDifficulty.value = diff
    }

    fun setSensitivity(sens: FlapSensitivity) {
        soundManager.playButtonClick()
        _sensitivity.value = sens
    }

    fun toggleScanlines() {
        soundManager.playButtonClick()
        _scanlinesEnabled.value = !_scanlinesEnabled.value
    }

    fun toggleSound() {
        soundManager.isSoundEnabled = !soundManager.isSoundEnabled
        if (soundManager.isSoundEnabled) soundManager.playButtonClick()
    }

    fun toggleHaptics() {
        soundManager.isHapticsEnabled = !soundManager.isHapticsEnabled
        soundManager.playButtonClick()
    }

    fun onGameFinished(score: Int, coins: Int, pipes: Int) {
        viewModelScope.launch {
            val isHighScore = repository.saveGameResult(
                score = score,
                coinsEarned = coins,
                pipesPassed = pipes,
                difficultyName = _selectedDifficulty.value.name,
                isEventTheme = _selectedTheme.value.isEventTheme
            )
            if (isHighScore) {
                soundManager.playAchievement()
            }
        }
    }

    fun sendChatMessage(text: String, shareScore: Boolean = false) {
        if (text.isBlank() && !shareScore) return
        viewModelScope.launch {
            val profile = playerProfile.value
            val sharedScoreVal = if (shareScore) (profile?.highScore ?: 0) else null
            val sharedDiffVal = if (shareScore) _selectedDifficulty.value.displayName else null
            val msg = if (shareScore && text.isBlank()) "Check out my high score! Can anyone beat this? 🏆" else text
            repository.sendChatMessage(msg, sharedScoreVal, sharedDiffVal)
            soundManager.playButtonClick()
        }
    }

    fun openDailyBonusDialog() {
        soundManager.playButtonClick()
        _dailyBonusResult.value = null
        _showDailyBonusDialog.value = true
    }

    fun closeDailyBonusDialog() {
        _showDailyBonusDialog.value = false
    }

    fun claimDailyBonus() {
        viewModelScope.launch {
            val result = repository.claimDailyBonus()
            _dailyBonusResult.value = result
            if (result.success) {
                soundManager.playAchievement()
            } else {
                soundManager.playButtonClick()
            }
        }
    }

    fun purchaseSkin(skinId: String, price: Int) {
        viewModelScope.launch {
            val success = repository.purchaseSkin(skinId, price)
            if (success) {
                soundManager.playCoin()
            } else {
                soundManager.playHit()
            }
        }
    }

    fun equipSkin(skinId: String) {
        viewModelScope.launch {
            repository.equipSkin(skinId)
            soundManager.playButtonClick()
        }
    }

    fun purchaseUpgrade(type: String, cost: Int) {
        viewModelScope.launch {
            val ok = repository.purchaseUpgrade(type, cost)
            if (ok) {
                soundManager.playCoin()
            } else {
                soundManager.playHit()
            }
        }
    }

    fun updateProfile(name: String, avatarId: String, title: String) {
        viewModelScope.launch {
            repository.updateProfile(name, avatarId, title)
            soundManager.playButtonClick()
        }
    }

    fun syncCloud() {
        viewModelScope.launch {
            val code = repository.syncCloud()
            _cloudMessage.value = "Progress synced to Cloud! Code: $code"
            soundManager.playAchievement()
        }
    }

    fun restoreCloud(code: String) {
        viewModelScope.launch {
            val success = repository.restoreFromCloudCode(code)
            if (success) {
                _cloudMessage.value = "Profile & save restored successfully!"
                soundManager.playAchievement()
            } else {
                _cloudMessage.value = "Invalid cloud code. Try again."
                soundManager.playHit()
            }
        }
    }

    fun clearCloudMessage() {
        _cloudMessage.value = null
    }

    fun openCompare(target: LeaderboardEntryEntity) {
        soundManager.playButtonClick()
        _compareTarget.value = target
    }

    fun closeCompare() {
        _compareTarget.value = null
    }

    fun triggerEventNotification() {
        notificationHelper.sendEventReminderNotification()
    }

    fun triggerDailyNotification() {
        notificationHelper.sendDailyBonusNotification()
    }
}
