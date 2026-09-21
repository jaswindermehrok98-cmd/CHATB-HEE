package com.example.focus

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

enum class SoundPreset(val title: String, val frequencyDesc: String, val benefits: String) {
    SOLFEGGIO_432("432 HZ SOLFEGGIO", "432 Hz Pure Sine", "Deep autonomic nervous system grounding"),
    BINAURAL_ALPHA("10 HZ ALPHA BEAT", "432 Hz / 442 Hz Stereo", "Enhanced neuro-focus and cognitive flow"),
    BROWN_NOISE("DEEP BROWN NOISE", "1/f² Low Frequency", "Masks intrusive external distractions"),
    PINK_NOISE("SOFT PINK NOISE", "1/f Equal Octave", "Relaxed alertness and memory consolidation")
}

class BinauralSynthesizer {
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val sampleRate = 44100
    private var isPlaying = false
    private var currentPreset = SoundPreset.SOLFEGGIO_432

    fun play(preset: SoundPreset = SoundPreset.SOLFEGGIO_432, scope: CoroutineScope) {
        stop()
        currentPreset = preset
        isPlaying = true

        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 2

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack = track
        track.play()

        playbackJob = scope.launch(Dispatchers.Default) {
            val chunkSamples = 2048
            val audioBuffer = ShortArray(chunkSamples * 2) // Stereo
            var phaseLeft = 0.0
            var phaseRight = 0.0
            var brownVal = 0.0

            val freqLeft = when (preset) {
                SoundPreset.SOLFEGGIO_432 -> 432.0
                SoundPreset.BINAURAL_ALPHA -> 432.0
                SoundPreset.BROWN_NOISE, SoundPreset.PINK_NOISE -> 0.0
            }

            val freqRight = when (preset) {
                SoundPreset.SOLFEGGIO_432 -> 432.0
                SoundPreset.BINAURAL_ALPHA -> 442.0 // 10Hz beat
                SoundPreset.BROWN_NOISE, SoundPreset.PINK_NOISE -> 0.0
            }

            val twoPi = 2.0 * Math.PI

            while (isActive && isPlaying) {
                for (i in 0 until chunkSamples) {
                    when (preset) {
                        SoundPreset.SOLFEGGIO_432, SoundPreset.BINAURAL_ALPHA -> {
                            val sampleL = (sin(phaseLeft) * 0.45 * Short.MAX_VALUE).toInt().toShort()
                            val sampleR = (sin(phaseRight) * 0.45 * Short.MAX_VALUE).toInt().toShort()

                            phaseLeft += twoPi * freqLeft / sampleRate
                            if (phaseLeft >= twoPi) phaseLeft -= twoPi

                            phaseRight += twoPi * freqRight / sampleRate
                            if (phaseRight >= twoPi) phaseRight -= twoPi

                            audioBuffer[i * 2] = sampleL
                            audioBuffer[i * 2 + 1] = sampleR
                        }
                        SoundPreset.BROWN_NOISE -> {
                            val white = (Random.nextDouble() * 2.0 - 1.0)
                            brownVal = (brownVal + (0.02 * white)) / 1.02
                            val sample = (brownVal * 0.6 * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                            audioBuffer[i * 2] = sample
                            audioBuffer[i * 2 + 1] = sample
                        }
                        SoundPreset.PINK_NOISE -> {
                            val white = (Random.nextDouble() * 2.0 - 1.0)
                            brownVal = (brownVal * 0.95) + (white * 0.05)
                            val sample = (brownVal * 0.8 * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                            audioBuffer[i * 2] = sample
                            audioBuffer[i * 2 + 1] = sample
                        }
                    }
                }

                track.write(audioBuffer, 0, audioBuffer.size)
            }
        }
    }

    fun stop() {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.release()
        } catch (_: Exception) {
        }
        audioTrack = null
    }

    fun isPlaying(): Boolean = isPlaying
    fun getPreset(): SoundPreset = currentPreset
}
