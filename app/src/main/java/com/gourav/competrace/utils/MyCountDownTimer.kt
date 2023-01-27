package com.gourav.competrace.utils

import android.os.CountDownTimer

class MyCountDownTimer(totalTimeInMillis: Long, val onTik: (Long) -> Unit) :
    CountDownTimer(totalTimeInMillis, 1000) {
    override fun onTick(p0: Long) {
        this.onTik(p0)
    }

    override fun onFinish() {}
}