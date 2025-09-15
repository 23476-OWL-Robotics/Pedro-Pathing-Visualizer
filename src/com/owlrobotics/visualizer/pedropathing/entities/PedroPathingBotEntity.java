package com.owlrobotics.visualizer.pedropathing.entities;

import com.owlrobotics.visualizer.pedropathing.paths.PathChain;
import com.owlrobotics.visualizer.util.enums.RobotImages;

import java.awt.*;

/*
    The PedroPathingBotEntity class provides a constructor and builder for each instance
    It implements BotEntity to provide the botImage, botSize and chain to the rest of the visualizer
 */

public class PedroPathingBotEntity implements BotEntity {

    // Create robotImage and Chain
    RobotImages imageType;
    Image robotImage;
    PathChain chain;

    // RobotWidth and RobotHeight
    int robotWidth;
    int robotHeight;

    @Override
    public RobotImages imageType() {
        return imageType;
    }

    // robotImage override
    @Override
    public Image robotImage() {
        return robotImage;
    }

    // robotSize override
    @Override
    public Dimension robotSize() {
        return new Dimension(robotWidth, robotHeight);
    }

    // PathChain override
    @Override
    public PathChain chain() {
        return chain;
    }

    // PedroPathingBotEntity class Constructor
    public PedroPathingBotEntity(Builder builder) {

        // Set the image, width, and height to the builder image, width, and height
        robotImage = builder.image;
        robotWidth = builder.robotWidth;
        robotHeight = builder.robotHeight;
        imageType = builder.imageType;
    }

    // The builder class is used for setting the botEntity variables
    public static class Builder {

        // Create Image, robotWidth, and robotHeight
        Image image;
        RobotImages imageType;
        int robotWidth;
        int robotHeight;

        // The setRobotImage builders set the image to either a preset or a custom image
        public Builder setRobotImage(RobotImages image) {
            this.image = image.getImage();
            this.imageType = image;
            return this;
        }
        public Builder setRobotImage(Image image) {
            this.image = image;
            this.imageType = RobotImages.Pedro_CLASSIC;
            return this;
        }

        // The setRobotSize sets the robot size
        public Builder setRobotSize(int width, int height) {
            this.robotWidth = width;
            this.robotHeight = height;
            return this;
        }

        // The build returns the settings to the PedroPathingBotEntity
        public PedroPathingBotEntity build() {
            return new PedroPathingBotEntity(this);
        }
    }

    // Creates a new path for the entity
    public void createNewPath(PathChain pathChain) {
        this.chain = pathChain;
    }
}