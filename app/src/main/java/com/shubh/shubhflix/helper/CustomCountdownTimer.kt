package com.shubh.shubhflix.helper

import android.os.CountDownTimer

class CustomCountdownTimer(
    private val totalSeconds: Long,
    private val onTick: (String) -> Unit, // Callback to update UI
    private val onFinish: () -> Unit // Callback when the timer finishes
) {
    private var remainingTimeMillis = totalSeconds * 1000
    private var isRunning = false
    private var countDownTimer: CountDownTimer? = null

    // Start the timer
    fun start() {
        if (!isRunning) {
            isRunning = true
            countDownTimer = object : CountDownTimer(remainingTimeMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    remainingTimeMillis = millisUntilFinished
                    onTick(formatSecondsToTime((millisUntilFinished / 1000).toInt()))
                }

                override fun onFinish() {
                    isRunning = false
                    onFinish()
                }
            }.start()
        }
    }

    // Pause the timer
    fun pause() {
        if (isRunning) {
            countDownTimer?.cancel()
            isRunning = false
        }
    }

    // Resume the timer
    fun resume() {
        start()
    }

    // Reset the timer
    fun reset() {
        pause()
        remainingTimeMillis = totalSeconds * 1000
        onTick(formatSecondsToTime(totalSeconds.toInt()))
    }

    // Format seconds to HH:mm:ss
    private fun formatSecondsToTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }
}
