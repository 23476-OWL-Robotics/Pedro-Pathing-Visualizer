package com.owlrobotics.pedropathingvisualizer.pedropathing.util

import java.awt.Dimension

enum class PlaneOrigin {
    CENTER,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT;

    fun getOrigin(windowSize: Int): Dimension {
        return when (this) {
            CENTER -> Dimension(windowSize / 2, windowSize / 2)
            TOP_LEFT -> Dimension(0, 0)
            TOP_RIGHT -> Dimension(windowSize, 0)
            BOTTOM_LEFT -> Dimension(0, windowSize)
            BOTTOM_RIGHT -> Dimension(windowSize, windowSize)
        }
    }
}
