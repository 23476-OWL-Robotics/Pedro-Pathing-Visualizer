package com.owlrobotics.pedropathingvisualizer.pedropathing.entities

import com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen.PathChain
import java.awt.Dimension
import java.awt.Image

// This is the BotEntity interface
// All Custom bot entities must implement this
interface BotEntity {
    fun robotImage(): Image
    fun chain(): PathChain

    fun robotSize(): Dimension
}
