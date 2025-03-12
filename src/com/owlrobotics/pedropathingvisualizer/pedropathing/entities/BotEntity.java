package com.owlrobotics.pedropathingvisualizer.pedropathing.entities;

import com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen.PathChain;

import java.awt.*;

// This is the BotEntity interface
// All Custom bot entities must implement this
public interface BotEntity {

    Image robotImage();
    PathChain chain();

    Dimension robotSize();
}
