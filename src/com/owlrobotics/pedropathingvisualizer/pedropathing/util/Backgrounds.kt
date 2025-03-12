package com.owlrobotics.pedropathingvisualizer.pedropathing.util

import java.awt.image.BufferedImage
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO

enum class Backgrounds {
    IntoTheDeep_DARK;

    @get:Throws(IOException::class)
    val image: BufferedImage
        get() {
            val loader = Thread.currentThread().contextClassLoader

            return when (this) {
                IntoTheDeep_DARK -> ImageIO.read(Objects.requireNonNull(loader.getResource("com/owlrobotics/pedropathingvisualizer/pedropathing/images/field/intothedeep_dark.png")))
            }
        }
}
