package com.owlrobotics.pedropathingvisualizer.pedropathing.util

open class SingleRunAction(private val runnable: Runnable) {
    private var hasBeenRun = false

    fun hasBeenRun(): Boolean {
        return this.hasBeenRun
    }

    fun run(): Boolean {
        if (!this.hasBeenRun) {
            this.hasBeenRun = true
            runnable.run()
            return true
        } else {
            return false
        }
    }

    fun reset() {
        this.hasBeenRun = false
    }
}
