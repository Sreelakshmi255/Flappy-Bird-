package com.example.game

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class GameEngine(
    var difficulty: GameDifficulty = GameDifficulty.NORMAL,
    var sensitivity: FlapSensitivity = FlapSensitivity.STANDARD,
    var extraHeartsBonus: Int = 0,
    var hasShieldMagnet: Boolean = false,
    val onFlapEvent: () -> Unit = {},
    val onScoreEvent: () -> Unit = {},
    val onCoinEvent: (Int) -> Unit = {},
    val onDamageEvent: (Int) -> Unit = {},
    val onHeartPickupEvent: () -> Unit = {},
    val onGameOverEvent: (score: Int, coins: Int, pipes: Int) -> Unit = { _, _, _ -> }
) {
    var state: GameState = GameState.READY
        private set

    var birdY: Float = 0f
    var birdVelocity: Float = 0f
    var birdRotation: Float = 0f // In degrees: -25f (ascending) to 70f (diving)

    var currentHearts: Int = 3
    var maxHearts: Int = 3

    var baseScore: Int = 0
    val totalScore: Int
        get() = (baseScore * difficulty.scoreMultiplier).toInt()

    var coinsCollected: Int = 0
    var pipesPassed: Int = 0

    var invincibleTimer: Float = 0f
    val isInvincible: Boolean
        get() = invincibleTimer > 0f

    var groundScrollX: Float = 0f
    var screenShakeTimer: Float = 0f

    val pipes = mutableListOf<PipeObstacle>()
    val coins = mutableListOf<CollectibleCoin>()
    val hearts = mutableListOf<CollectibleHeart>()

    private var nextPipeId = 0L
    private var nextCoinId = 0L
    private var nextHeartId = 0L

    private val birdRadius = 17f
    private val pipeWidth = 72f
    private var spawnDistanceTimer = 0f

    fun reset(screenHeight: Float) {
        state = GameState.READY
        birdY = screenHeight * 0.45f
        birdVelocity = 0f
        birdRotation = 0f
        maxHearts = difficulty.startingHearts + extraHeartsBonus
        currentHearts = maxHearts
        baseScore = 0
        coinsCollected = 0
        pipesPassed = 0
        invincibleTimer = 0f
        screenShakeTimer = 0f
        spawnDistanceTimer = 0f
        pipes.clear()
        coins.clear()
        hearts.clear()
    }

    fun tap(screenHeight: Float) {
        when (state) {
            GameState.READY -> {
                state = GameState.PLAYING
                flap()
            }
            GameState.PLAYING -> {
                flap()
            }
            GameState.PAUSED -> {
                state = GameState.PLAYING
            }
            GameState.GAME_OVER -> {
                reset(screenHeight)
                state = GameState.PLAYING
                flap()
            }
        }
    }

    private fun flap() {
        // Upward impulse
        val baseImpulse = -580f
        birdVelocity = baseImpulse * sensitivity.impulseFactor
        onFlapEvent()
    }

    fun pause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED
        }
    }

    fun resume() {
        if (state == GameState.PAUSED) {
            state = GameState.PLAYING
        }
    }

    fun update(dt: Float, screenWidth: Float, screenHeight: Float) {
        if (state != GameState.PLAYING) return

        val clampedDt = dt.coerceIn(0.001f, 0.05f)

        // Update invincible flashing & screen shake
        if (invincibleTimer > 0f) {
            invincibleTimer = max(0f, invincibleTimer - clampedDt)
        }
        if (screenShakeTimer > 0f) {
            screenShakeTimer = max(0f, screenShakeTimer - clampedDt)
        }

        // Physics Constants
        val gravity = 1550f
        val maxFallSpeed = 750f
        val groundHeight = 110f
        val groundY = screenHeight - groundHeight

        // Apply gravity to bird
        birdVelocity = min(birdVelocity + gravity * clampedDt, maxFallSpeed)
        birdY += birdVelocity * clampedDt

        // Bird rotation based on velocity
        birdRotation = if (birdVelocity < 0) {
            // Ascending
            max(-25f, birdVelocity * 0.06f)
        } else {
            // Diving
            min(75f, (birdVelocity - 100f) * 0.12f)
        }

        // Ground collision -> Immediate game over
        if (birdY + birdRadius >= groundY) {
            birdY = groundY - birdRadius
            triggerGameOver()
            return
        }

        // Ceiling clamping
        if (birdY - birdRadius < 0) {
            birdY = birdRadius
            birdVelocity = 0f
        }

        // World Speed
        val baseSpeed = 220f
        val speed = baseSpeed * difficulty.pipeSpeedRatio

        // Ground scroll
        groundScrollX = (groundScrollX + speed * clampedDt) % 48f

        // Pipes spawning
        val pipeSpacing = 280f / difficulty.pipeSpeedRatio
        spawnDistanceTimer += speed * clampedDt

        if (pipes.isEmpty() || spawnDistanceTimer >= pipeSpacing) {
            spawnDistanceTimer = 0f
            spawnPipe(screenWidth, screenHeight, groundY)
        }

        // Bird horizontal position in canvas
        val birdX = screenWidth * 0.28f

        // Magnet range if upgrade unlocked
        val magnetRadius = if (hasShieldMagnet) 95f else 35f

        // Update Pipes
        val pipeIterator = pipes.iterator()
        while (pipeIterator.hasNext()) {
            val pipe = pipeIterator.next()
            pipe.x -= speed * clampedDt

            // Check passing pipe
            if (!pipe.passed && pipe.x + pipe.width < birdX) {
                pipe.passed = true
                baseScore++
                pipesPassed++
                onScoreEvent()
            }

            // Pipe Collision Check
            if (!isInvincible) {
                val inPipeX = (birdX + birdRadius > pipe.x) && (birdX - birdRadius < pipe.x + pipe.width)
                if (inPipeX) {
                    val hitTopPipe = (birdY - birdRadius) < pipe.topHeight
                    val hitBottomPipe = (birdY + birdRadius) > (pipe.gapY + pipe.gapHeight / 2f)

                    if (hitTopPipe || hitBottomPipe) {
                        handlePipeHit()
                        if (state == GameState.GAME_OVER) return
                    }
                }
            }

            // Remove offscreen pipes
            if (pipe.x + pipe.width < -50f) {
                pipeIterator.remove()
            }
        }

        // Update Coins
        val coinIterator = coins.iterator()
        while (coinIterator.hasNext()) {
            val coin = coinIterator.next()
            coin.x -= speed * clampedDt

            if (!coin.collected) {
                val dx = birdX - coin.x
                val dy = birdY - coin.y
                val distSq = dx * dx + dy * dy

                // Magnet effect pull if within radius
                if (hasShieldMagnet && distSq < magnetRadius * magnetRadius) {
                    coin.x += (birdX - coin.x) * 8f * clampedDt
                    coin.y += (birdY - coin.y) * 8f * clampedDt
                }

                // Pickup collision check
                if (distSq < (birdRadius + 16f) * (birdRadius + 16f)) {
                    coin.collected = true
                    coinsCollected += coin.value
                    onCoinEvent(coin.value)
                }
            }

            if (coin.x < -40f || coin.collected) {
                coinIterator.remove()
            }
        }

        // Update Hearts
        val heartIterator = hearts.iterator()
        while (heartIterator.hasNext()) {
            val heart = heartIterator.next()
            heart.x -= speed * clampedDt

            if (!heart.collected) {
                val dx = birdX - heart.x
                val dy = birdY - heart.y
                val distSq = dx * dx + dy * dy

                if (distSq < (birdRadius + 18f) * (birdRadius + 18f)) {
                    heart.collected = true
                    if (currentHearts < maxHearts) {
                        currentHearts++
                    }
                    onHeartPickupEvent()
                }
            }

            if (heart.x < -40f || heart.collected) {
                heartIterator.remove()
            }
        }
    }

    private fun handlePipeHit() {
        currentHearts--
        invincibleTimer = 1.3f // 1.3 seconds invulnerability flashing
        screenShakeTimer = 0.25f // Screen shake burst

        if (currentHearts <= 0) {
            triggerGameOver()
        } else {
            onDamageEvent(currentHearts)
        }
    }

    private fun triggerGameOver() {
        state = GameState.GAME_OVER
        onGameOverEvent(totalScore, coinsCollected, pipesPassed)
    }

    private fun spawnPipe(screenWidth: Float, screenHeight: Float, groundY: Float) {
        val baseGap = 200f * difficulty.pipeGapRatio
        val minTop = 80f
        val maxTop = groundY - baseGap - 80f

        val topHeight = Random.nextFloat() * (maxTop - minTop) + minTop
        val gapY = topHeight + baseGap / 2f

        val pipe = PipeObstacle(
            id = nextPipeId++,
            x = screenWidth + 20f,
            topHeight = topHeight,
            gapY = gapY,
            gapHeight = baseGap,
            width = pipeWidth
        )
        pipes.add(pipe)

        // 65% chance to spawn a gold coin in the gap
        if (Random.nextFloat() < 0.65f) {
            coins.add(
                CollectibleCoin(
                    id = nextCoinId++,
                    x = pipe.x + pipeWidth / 2f,
                    y = gapY + Random.nextFloat() * 40f - 20f,
                    value = if (difficulty == GameDifficulty.INSANE) 2 else 1
                )
            )
        }

        // 18% chance to spawn a floating heart in gap if damaged or lucky
        val heartChance = if (currentHearts < maxHearts) 0.28f else 0.12f
        if (Random.nextFloat() < heartChance) {
            hearts.add(
                CollectibleHeart(
                    id = nextHeartId++,
                    x = pipe.x + pipeWidth / 2f + 55f,
                    y = gapY + (if (Random.nextBoolean()) -35f else 35f)
                )
            )
        }
    }
}
