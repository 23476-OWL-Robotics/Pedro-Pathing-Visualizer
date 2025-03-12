package com.owlrobotics.pedropathingvisualizer

import com.owlrobotics.pedropathingvisualizer.pedropathing.entities.BotEntity
import com.owlrobotics.pedropathingvisualizer.pedropathing.util.Backgrounds
import com.owlrobotics.pedropathingvisualizer.pedropathing.util.FieldRotation
import com.owlrobotics.pedropathingvisualizer.pedropathing.util.PlaneOrigin
import java.awt.EventQueue
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.IOException
import javax.swing.JFrame

class PathVisualizer {
    // All the required parameters for com.owlrobotics.pedropathingvisualizer.FieldPanel
    var fieldSize: Int
    var pixelsPerInch: Double
    var targetFPS: Int
    var entities: ArrayList<BotEntity>
    private var planeOrigin: PlaneOrigin? = null
    private var fieldRotation = 0.0

    // BufferedImages
    // fieldImage is the raw image file
    // resizedFieldImaged is a resized instance of fieldImage given to the Field Panel
    private var fieldImage: BufferedImage? = null
    private var resizedFieldImage: BufferedImage? = null

    // Path Visualizer class constructors
    constructor(fieldSize: Int) {
        this.entities = ArrayList()

        this.fieldSize = fieldSize
        this.targetFPS = 60
        this.pixelsPerInch = fieldSize.toDouble() / 144
    }

    constructor(fieldSize: Int, targetFPS: Int) {
        this.entities = ArrayList()

        this.fieldSize = fieldSize
        this.targetFPS = targetFPS
        this.pixelsPerInch = fieldSize.toDouble() / 144
    }

    // Path Visualizer Settings
    // Some are optional but most are required to start the visualizer
    // setBackground Setting
    // Option to either use a custom image or a Backgrounds image
    fun setBackground(background: Backgrounds): PathVisualizer {
        try {
            this.fieldImage = background.image
        } catch (e: IOException) {
            println(e.localizedMessage)
        }

        return this
    }

    fun setBackground(image: BufferedImage?): PathVisualizer {
        this.fieldImage = image
        return this
    }

    // Sets the planeOrigin
    fun setPlaneOrigin(planeOrigin: PlaneOrigin?): PathVisualizer {
        this.planeOrigin = planeOrigin
        return this
    }

    // Sets the fieldRotation
    fun setFieldRotation(fieldRotation: FieldRotation): PathVisualizer {
        this.fieldRotation = fieldRotation.rotation
        return this
    }

    // Adds entities
    fun addEntity(entity: BotEntity): PathVisualizer {
        entities.add(entity)

        return this
    }

    // Starts the visualizer
    fun start() {
        runVisualizer()
    }

    fun runVisualizer() {
        // Create a new JFrame

        val frame = JFrame("Pedro Pathing Visualizer")

        // Set the resizedFieldImage size and type
        // The graphics are required otherwise the image will be null when called in com.owlrobotics.pedropathingvisualizer.FieldPanel
        resizedFieldImage = BufferedImage(fieldSize, fieldSize, BufferedImage.TYPE_INT_RGB)
        val g: Graphics = resizedFieldImage!!.createGraphics()
        g.drawImage(fieldImage, 0, 0, fieldSize, fieldSize, null)

        EventQueue.invokeLater {
            // Set the frame to Exit when closed
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

            // Set the frame to be non-resizable
            frame.isResizable = false


            // Add the com.owlrobotics.pedropathingvisualizer.MainPanel class to the frame
            frame.add(
                MainPanel(
                    resizedFieldImage!!,
                    fieldRotation,
                    frame,
                    entities,
                    planeOrigin!!.getOrigin(fieldSize),
                    pixelsPerInch,
                    fieldSize,
                    targetFPS
                )
            )

            // Pack the frame to set the correct size
            frame.pack()

            // Set the frame to be visible
            frame.isVisible = true

            // Set the frame to be in the middle of the screen
            frame.setLocationRelativeTo(null)
        }
    }
}
