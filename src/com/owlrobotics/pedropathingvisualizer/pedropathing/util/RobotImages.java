package com.owlrobotics.pedropathingvisualizer.pedropathing.util;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public enum RobotImages {
    Pedro_CLASSIC,
    Pedro_BLUE,
    Pedro_RED;

    public Image getImage() {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        return switch (this) {
            case Pedro_CLASSIC -> new ImageIcon(Objects.requireNonNull(loader.getResource("com/owlrobotics/pedropathingvisualizer/pedropathing/images/robot/pedro_robot_classic.png"))).getImage();
            case Pedro_BLUE -> new ImageIcon(Objects.requireNonNull(loader.getResource("com/owlrobotics/pedropathingvisualizer/pedropathing/images/robot/pedro_robot_blue.png"))).getImage();
            case Pedro_RED -> new ImageIcon(Objects.requireNonNull(loader.getResource("com/owlrobotics/pedropathingvisualizer/pedropathing/images/robot/pedro_robot_red.png"))).getImage();
        };
    }
}
