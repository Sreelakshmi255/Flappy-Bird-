package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Procedural 8-bit chiptune sound generator using AudioTrack.
 * Generates square waves, frequency sweeps, and noise bursts for genuine arcade feel.
 */
class RetroSoundManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val sampleRate = 22050

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    @Suppress("DEPRECATION")
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private fun vibrate(durationMs: Long) {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun playToneSweep(
        startFreq: Float,
        endFreq: Float,
        durationMs: Int,
        waveform: String = "square",
        volume: Float = 0.6f
    ) {
        if (!isSoundEnabled) return
        scope.launch {
            try {
                val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
                val buffer = ShortArray(numSamples)

                var phase = 0.0
                for (i in 0 until numSamples) {
                    val progress = i.toFloat() / numSamples
                    val currentFreq = startFreq + (endFreq - startFreq) * progress
                    val phaseIncrement = (2.0 * PI * currentFreq) / sampleRate
                    phase += phaseIncrement
                    if (phase > 2.0 * PI) phase -= 2.0 * PI

                    // Envelope: fast attack, gradual decay
                    val envelope = (1.0f - progress * 0.8f) * volume

                    val sampleValue: Double = when (waveform) {
                        "square" -> if (sin(phase) >= 0) 1.0 else -1.0
                        "triangle" -> {
                            val normalized = (phase / (2.0 * PI)) % 1.0
                            if (normalized < 0.5) 4.0 * normalized - 1.0 else 3.0 - 4.0 * normalized
                        }
                        "noise" -> (Math.random() * 2.0 - 1.0)
                        else -> sin(phase)
                    }

                    buffer[i] = (sampleValue * Short.MAX_VALUE * envelope).toInt().coerceIn(
                        Short.MIN_VALUE.toInt(),
                        Short.MAX_VALUE.toInt()
                    ).toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()

                // Release track after playback completes
                kotlinx.coroutines.delay(durationMs.toLong() + 50)
                track.stop()
                track.release()
            } catch (_: Exception) {}
        }
    }

    fun playFlap() {
        // Quick 8-bit upward chirp
        playToneSweep(startFreq = 380f, endFreq = 780f, durationMs = 70, waveform = "square", volume = 0.5f)
        vibrate(15)
    }

    fun playScore() {
        // High 8-bit positive bell chime
        playToneSweep(startFreq = 950f, endFreq = 1400f, durationMs = 120, waveform = "square", volume = 0.6f)
    }

    fun playCoin() {
        // 8-bit coin sparkle (fast high double ping)
        scope.launch {
            playToneSweep(startFreq = 988f, endFreq = 1319f, durationMs = 60, waveform = "square", volume = 0.6f)
            kotlinx.coroutines.delay(65)
            playToneSweep(startFreq = 1319f, endFreq = 1760f, durationMs = 100, waveform = "square", volume = 0.7f)
        }
        vibrate(25)
    }

    fun playHit() {
        // Low crunch noise burst for losing a heart
        playToneSweep(startFreq = 180f, endFreq = 60f, durationMs = 160, waveform = "noise", volume = 0.8f)
        vibrate(80)
    }

    fun playHeartPickup() {
        // Pleasant rising health restore chime
        scope.launch {
            playToneSweep(startFreq = 523f, endFreq = 659f, durationMs = 80, waveform = "square", volume = 0.6f)
            kotlinx.coroutines.delay(85)
            playToneSweep(startFreq = 659f, endFreq = 784f, durationMs = 80, waveform = "square", volume = 0.6f)
            kotlinx.coroutines.delay(85)
            playToneSweep(startFreq = 784f, endFreq = 1046f, durationMs = 120, waveform = "square", volume = 0.7f)
        }
        vibrate(40)
    }

    fun playGameOver() {
        // Classic retro sad descending melody
        scope.launch {
            playToneSweep(startFreq = 587f, endFreq = 523f, durationMs = 120, waveform = "square", volume = 0.7f)
            kotlinx.coroutines.delay(130)
            playToneSweep(startFreq = 523f, endFreq = 440f, durationMs = 130, waveform = "square", volume = 0.7f)
            kotlinx.coroutines.delay(140)
            playToneSweep(startFreq = 440f, endFreq = 330f, durationMs = 140, waveform = "square", volume = 0.7f)
            kotlinx.coroutines.delay(150)
            playToneSweep(startFreq = 330f, endFreq = 165f, durationMs = 260, waveform = "triangle", volume = 0.8f)
        }
        vibrate(150)
    }

    fun playAchievement() {
        // Glorious victory fanfare
        scope.launch {
            val notes = listOf(523f, 659f, 784f, 1046f)
            for (freq in notes) {
                playToneSweep(freq, freq * 1.05f, 90, "square", 0.6f)
                kotlinx.coroutines.delay(95)
            }
        }
        vibrate(60)
    }

    fun playButtonClick() {
        playToneSweep(startFreq = 700f, endFreq = 850f, durationMs = 35, waveform = "square", volume = 0.4f)
        vibrate(12)
    }
}
