package com.bruhascended.cv

import android.util.Log
import java.util.*

class RunTimeAnalyzer(
    private val debug: Boolean,
) {
    companion object {
        private const val beta = .8
    }

    private var v = 0.0
    private var startTime = -1L
    private var betaExp = 1.0

    init {
        startTime = Calendar.getInstance().timeInMillis
    }

    val movingAverage: Double?
        get() = if (v == 0.0) null else v / (1 - betaExp)

    fun log() {
        val time = Calendar.getInstance().timeInMillis
        val u = time - startTime
        v = v * beta + u * (1 - beta)
        betaExp *= beta
        if (debug) Log.d("Average Latency:", movingAverage.toString())
        startTime = time
    }
}
