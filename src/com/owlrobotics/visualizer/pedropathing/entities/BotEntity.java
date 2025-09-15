package com.owlrobotics.visualizer.pedropathing.entities;

import com.owlrobotics.visualizer.pedropathing.paths.PathChain;
import com.owlrobotics.visualizer.util.enums.RobotImages;

import java.awt.*;

// This is the BotEntity interface
// All Custom bot entities must implement this
public interface BotEntity {

    RobotImages imageType();
    Image robotImage();
    PathChain chain();

    Dimension robotSize();
}
