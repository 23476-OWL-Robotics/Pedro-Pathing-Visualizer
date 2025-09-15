package com.owlrobotics.visualizer;

import com.owlrobotics.visualizer.pedropathing.entities.BotEntity;
import com.owlrobotics.visualizer.ui.theme.Theme;
import com.owlrobotics.visualizer.util.enums.Backgrounds;
import com.owlrobotics.visualizer.util.enums.FieldRotation;
import com.owlrobotics.visualizer.util.enums.PlaneOrigin;
import com.owlrobotics.visualizer.ui.titlebar.TBJFrame;
import com.owlrobotics.visualizer.ui.titlebar.win.WindowFrameType;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/*
    PathVisualizer is the main class in the library
    When run, It creates the JFrame and adds MainPanel to the Frame
 */
public class PathVisualizer {

    // All the required parameters for MainPanel
    int fieldSize;
    double pixelsPerInch;
    int targetFPS;
    ArrayList<BotEntity> entities;
    private PlaneOrigin planeOrigin;
    private double fieldRotation = 0;
    Theme theme;

    // BufferedImages
    // fieldImage is the raw image file
    // imageIcon is the icon for the Frame Bar and the TaskBar
    private BufferedImage fieldImage;
    private BufferedImage iconImage;

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

    // sets the Visualizer Theme to either Dark of Light
    public PathVisualizer setTheme(Theme theme) {
        this.theme = theme;
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

        // If you try to add more than 4 entities, this message will send and the visualizer will not run.
        if (this.entities.size() > 4) {
            System.out.println("Too Many Bot Entities!! A Maximum of four Bot Entities are allowed.");
            System.exit(0);
        }

        return this;
    }

    // Starts the visualizer
    public void start() {
        runVisualizer();
    }

    public void runVisualizer() {

        // Create a new TBFrame
        TBJFrame frame = new TBJFrame("PedroPathingVisualizer", WindowFrameType.NORMAL, theme, 20, false);

        // Set the iconImage to the Themes iconImage
        iconImage = Theme.iconImage;

        // When it is safe to, configure the frame and add MainPanel
        EventQueue.invokeLater(() -> {

            // Set the frame to Exit when closed
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Set the Title Bar Icon and Task Bar Icon to iconImage
            frame.setTitleBarIcon(iconImage);
            frame.setTaskBarIcon(iconImage);

            // Add the MainPanel class to the frame
            frame.add(new MainPanel(
                    fieldImage,
                    fieldRotation,
                    frame,
                    entities,
                    planeOrigin,
                    pixelsPerInch,
                    fieldSize,
                    targetFPS));

            // Pack the frame to set the correct size
            frame.pack();

            // Round the frame
            frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 20, 20));

            // Set the frame to be visible
            frame.setVisible(true);

            // Set the frame to be in the middle of the screen
            frame.setLocationRelativeTo(null);
        });
    }
}
