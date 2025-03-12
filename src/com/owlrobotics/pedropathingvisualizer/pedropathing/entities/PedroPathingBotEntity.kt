package com.owlrobotics.pedropathingvisualizer.pedropathing.entities

import com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen.PathChain
import com.owlrobotics.pedropathingvisualizer.pedropathing.util.RobotImages
import java.awt.Dimension
import java.awt.Image

/*
   The PedroPathingBotEntity class provides a constructor and builder for each instance
   It implements BotEntity to provide the botImage, botSize and chain to the rest of the visualizer
*/
class PedroPathingBotEntity(builder: Builder) :
    BotEntity {
    // Create robotImage and Chain

    // Set the image, width, and height to the builder image, width, and height
    var robotImage: Image = builder.image
    lateinit var chain: PathChain;

    // RobotWidth and RobotHeight
    var robotWidth: Int
    var robotHeight: Int

    // robotImage override
    override fun robotImage(): Image {
        return robotImage
    }

    // robotSize override
    override fun robotSize(): Dimension {
        return Dimension(robotWidth, robotHeight)
    }

    // PathChain override
    override fun chain(): PathChain {
        return chain
    }

    // PedroPathingBotEntity class Constructor
    init {
        robotWidth = builder.robotWidth
        robotHeight = builder.robotHeight
    }

    // The builder class is used for setting the botEntity variables
    class Builder {
        // Create Image, robotWidth, and robotHeight
        lateinit var image: Image;
        var robotWidth: Int = 0
        var robotHeight: Int = 0

        // The setRobotImage builders set the image to either a preset or a custom image
        fun setRobotImage(image: RobotImages): Builder {
            this.image = image.image
            return this
        }

        fun setRobotImage(image: Image): Builder {
            this.image = image
            return this
        }

        // The setRobotSize sets the robot size
        fun setRobotSize(width: Int, height: Int): Builder {
            this.robotWidth = width
            this.robotHeight = height
            return this
        }

        // The build returns the settings to the PedroPathingBotEntity
        fun build(): PedroPathingBotEntity {
            return PedroPathingBotEntity(this)
        }
    }

    // Creates a new path for the entity
    fun createNewPath(pathChain: PathChain) {
        this.chain = pathChain
    }
}