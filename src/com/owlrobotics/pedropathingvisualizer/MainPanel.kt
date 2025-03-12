package com.owlrobotics.pedropathingvisualizer

import com.owlrobotics.pedropathingvisualizer.pedropathing.entities.BotEntity
import com.owlrobotics.pedropathingvisualizer.pedropathing.util.XYLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.JPanel

class MainPanel(
    fieldImg: BufferedImage, fieldRotation: Double,
    var frame: JFrame,
    var entities: ArrayList<BotEntity>,
    var planeOrigin: Dimension,
    var pixelsPerInch: Double, // Variables required for com.owlrobotics.pedropathingvisualizer.ControlPanel and com.owlrobotics.pedropathingvisualizer.FieldPanel
    var fieldSize: Int, targetFPS: Int
) :
    JPanel() {
    // Set the variables
    var fieldImage: BufferedImage = fieldImg


    var fieldPanel: FieldPanel
    var controlPanel: ControlPanel? = null

    // com.owlrobotics.pedropathingvisualizer.MainPanel class constructor
    init {
        // Panel settings
        this.background = Color.BLACK
        this.preferredSize = Dimension(fieldSize + 60 + 800, fieldSize + 40)
        this.layout = XYLayout()
        this.isVisible = true

        // Create fieldPanel
        fieldPanel = FieldPanel(
            fieldImg,
            frame,
            entities,
            pixelsPerInch,
            planeOrigin,
            fieldRotation,
            fieldSize,
            targetFPS
        )

        // Add com.owlrobotics.pedropathingvisualizer.FieldPanel
        this.add(fieldPanel)

        // Create and add com.owlrobotics.pedropathingvisualizer.ControlPanel
        this.add(
            ControlPanel(
                fieldSize,
                entities,
                fieldPanel
            )
        )
    }
}
