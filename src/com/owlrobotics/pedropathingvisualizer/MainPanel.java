package com.owlrobotics.pedropathingvisualizer;

import com.owlrobotics.pedropathingvisualizer.pedropathing.entities.BotEntity;
import com.owlrobotics.pedropathingvisualizer.pedropathing.util.XYLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MainPanel extends JPanel {

    // Variables required for com.owlrobotics.pedropathingvisualizer.ControlPanel and com.owlrobotics.pedropathingvisualizer.FieldPanel
    int fieldSize;
    BufferedImage fieldImage;
    JFrame frame;
    ArrayList<BotEntity> entities;
    Dimension planeOrigin;
    double pixelsPerInch;


    FieldPanel fieldPanel;
    ControlPanel controlPanel;

    // com.owlrobotics.pedropathingvisualizer.MainPanel class constructor
    public MainPanel(BufferedImage fieldImg, double fieldRotation, JFrame frame, ArrayList<BotEntity> entities, Dimension planeOrigin, double pixelsPerInch, int fieldSize, int targetFPS) {
        // Set the variables
        this.fieldImage = fieldImg;
        this.frame = frame;
        this.entities = entities;
        this.planeOrigin = planeOrigin;
        this.pixelsPerInch = pixelsPerInch;
        this.fieldSize = fieldSize;

        // Panel settings
        this.setBackground(Color.BLACK);
        this.setPreferredSize(new Dimension(fieldSize + 60 + 800, fieldSize + 40));
        this.setLayout(new XYLayout());
        this.setVisible(true);

        // Create fieldPanel
        fieldPanel = new FieldPanel(
                fieldImg,
                frame,
                entities,
                pixelsPerInch,
                planeOrigin,
                fieldRotation,
                fieldSize,
                targetFPS);

        // Add com.owlrobotics.pedropathingvisualizer.FieldPanel
        this.add(fieldPanel);

        // Create and add com.owlrobotics.pedropathingvisualizer.ControlPanel
        this.add(new ControlPanel(
                fieldSize,
                entities,
                fieldPanel));

    }
}
