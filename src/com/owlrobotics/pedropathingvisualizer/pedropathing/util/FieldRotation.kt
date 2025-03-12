package com.owlrobotics.pedropathingvisualizer.pedropathing.util

enum class FieldRotation {
    ROTATION_90,
    ROTATION_180,
    ROTATION_270;

    val rotation: Double
        get() = when (this) {
            ROTATION_90 -> Math.toRadians(90.0)
            ROTATION_180 -> Math.toRadians(180.0)
            ROTATION_270 -> Math.toRadians(270.0)
        }
}
