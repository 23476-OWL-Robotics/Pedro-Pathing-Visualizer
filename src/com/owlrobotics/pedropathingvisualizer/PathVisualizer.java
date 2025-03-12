package com.owlrobotics.pedropathingvisualizer;

import com.owlrobotics.pedropathingvisualizer.pedropathing.entities.BotEntity;
import com.owlrobotics.pedropathingvisualizer.pedropathing.util.Backgrounds;
import com.owlrobotics.pedropathingvisualizer.pedropathing.util.FieldRotation;
import com.owlrobotics.pedropathingvisualizer.pedropathing.util.PlaneOrigin;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PathVisualizer {

    // All the required parameters for com.owlrobotics.pedropathingvisualizer.FieldPanel
    int fieldSize;
    double pixelsPerInch;
    int targetFPS;
    ArrayList<BotEntity> entities;
    private PlaneOrigin planeOrigin;
    private double fieldRotation = 0;

    // BufferedImages
    // fieldImage is the raw image file
    // resizedFieldImaged is a resized instance of fieldImage given to the Field Panel
    private BufferedImage fieldImage;
    private BufferedImage resizedFieldImage;

    // Path Visualizer class constructors
    public PathVisualizer(int fieldSize) {
        this.entities = new ArrayList<>();

        this.fieldSize = fieldSize;
        this.targetFPS = 60;
        this.pixelsPerInch = (double) fieldSize / 144;
    }
    public PathVisualizer(int fieldSize, int targetFPS) {
        this.entities = new ArrayList<>();

        this.fieldSize = fieldSize;
        this.targetFPS = targetFPS;
        this.pixelsPerInch = (double) fieldSize / 144;
    }

    // Path Visualizer Settings
    // Some are optional but most are required to start the visualizer

    // setBackground Setting
    // Option to either use a custom image or a Backgrounds image
    public PathVisualizer setBackground(Backgrounds background) {
        try {
            this.fieldImage = background.getImage();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

        return this;
    }
    public PathVisualizer setBackground(BufferedImage image) {
        this.fieldImage = image;
        return this;
    }

    // Sets the planeOrigin
    public PathVisualizer setPlaneOrigin(PlaneOrigin planeOrigin) {
        this.planeOrigin = planeOrigin;
        return this;
    }

    // Sets the fieldRotation
    public PathVisualizer setFieldRotation(FieldRotation fieldRotation) {
        this.fieldRotation = fieldRotation.getRotation();
        return this;
    }

    // Adds entities
    public PathVisualizer addEntity(BotEntity entity) {
        this.entities.add(entity);

        return this;
    }

    // Starts the visualizer
    public void start() {
        runVisualizer();
    }

    public void runVisualizer() {

        // Create a new JFrame
        JFrame frame = new JFrame("Pedro Pathing Visualizer");

        // Set the resizedFieldImage size and type
        // The graphics are required otherwise the image will be null when called in com.owlrobotics.pedropathingvisualizer.FieldPanel
        resizedFieldImage = new BufferedImage(fieldSize, fieldSize, BufferedImage.TYPE_INT_RGB);
        Graphics g = resizedFieldImage.createGraphics();
        g.drawImage(fieldImage, 0, 0, fieldSize, fieldSize, null);

        EventQueue.invokeLater(() -> {
            // Set the frame to Exit when closed
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Set the frame to be non-resizable
            frame.setResizable(false);


            // Add the com.owlrobotics.pedropathingvisualizer.MainPanel class to the frame
            frame.add(new MainPanel(
                    resizedFieldImage,
                    fieldRotation,
                    frame,
                    entities,
                    planeOrigin.getOrigin(fieldSize),
                    pixelsPerInch,
                    fieldSize,
                    targetFPS));

            // Pack the frame to set the correct size
            frame.pack();

            // Set the frame to be visible
            frame.setVisible(true);

            // Set the frame to be in the middle of the screen
            frame.setLocationRelativeTo(null);
        });
    }
}
