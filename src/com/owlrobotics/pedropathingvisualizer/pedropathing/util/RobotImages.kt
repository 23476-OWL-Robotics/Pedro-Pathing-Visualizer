package com.owlrobotics.pedropathingvisualizer.pedropathing.util

import java.awt.Image
import java.util.*
import javax.swing.ImageIcon

enum class RobotImages {
    Pedro_CLASSIC,
    Pedro_BLUE,
    Pedro_RED;

    val image: Image
        get() {
            val loader = Thread.currentThread().contextClassLoader

            return when (this) {
                Pedro_CLASSIC -> ImageIcon(Objects.requireNonNull(loader.getResource("com/owlrobotics/pedropathingvisualizer/pedropathing/images/robot/pedro_robot_classic.png"))).image
                Pedro_BLUE -> ImageIcon(Objects.requireNonNull(loader.getResource("com/owlrobotics/pedropathingvisualizer/pedropathing/images/robot/pedro_robot_blue.png"))).image
                Pedro_RED -> ImageIcon(Objects.requireNonNull(loader.getResource("com/owlrobotics/pedropathingvisualizer/pedropathing/images/robot/pedro_robot_red.png"))).image
            }
        }
}
