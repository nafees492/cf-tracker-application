package com.gourav.competrace.contests.util

import android.os.CountDownTimer

class MyCountDownTimer(totalTimeInMillis: Long, val onTik: (Long) -> Unit) :
    CountDownTimer(totalTimeInMillis, 1000) {
    override fun onTick(timeRemaining: Long) {
        this.onTik(timeRemaining)
    }
    override fun onFinish() {}
}