package com.owlrobotics.visualizer;

import com.owlrobotics.visualizer.pedropathing.entities.BotEntity;
import com.owlrobotics.visualizer.ui.theme.Theme;
import com.owlrobotics.visualizer.util.enums.PlaneOrigin;
import com.owlrobotics.visualizer.util.XYLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.ArrayList;

/*
    This class is the main panel for the visualizer
    FieldPanel and ControlPanel are added to it

    It is also the main loop for the visualizer
 */
public class MainPanel extends JPanel {

    // Variables required for ControlPanel and FieldPanel
    int fieldSize;
    BufferedImage fieldImage;
    JFrame frame;
    ArrayList<BotEntity> entities;
    PlaneOrigin planeOrigin;
    double pixelsPerInch;

    // Variables for managing fps
    int frameTime;

    // FieldPanel
    FieldPanel fieldPanel;

    // MainPanel class constructor
    public MainPanel(BufferedImage fieldImg, double fieldRotation, JFrame frame, ArrayList<BotEntity> entities, PlaneOrigin planeOrigin, double pixelsPerInch, int fieldSize, int targetFPS) {
        // Set the variables
        this.fieldImage = fieldImg;
        this.frame = frame;
        this.entities = entities;
        this.planeOrigin = planeOrigin;
        this.pixelsPerInch = pixelsPerInch;
        this.fieldSize = fieldSize;

        // Set the frame time
        // The fps limiter uses nanoTime which is why the first number is so large
        frameTime = 1000000000 / targetFPS;

        // Panel settings
        this.setPreferredSize(new Dimension(fieldSize + 60 + 800, fieldSize + 40));
        this.setLocation(0, 20);
        this.setLayout(new XYLayout());
        this.setIgnoreRepaint(true);
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

        // Add FieldPanel
        this.add(fieldPanel);

        // Create and add ControlPanel
        this.add(new ControlPanel(
                fieldSize,
                entities,
                fieldPanel));

    }

    @Override
    public void paintComponent(Graphics g) {
        // StartTime for the fps limiter
        long startTime = System.nanoTime();

        // Paint the Background
        g.setColor(Theme.mainPanelBackgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Revalidate
        revalidate();

        // Repaint
        repaint();

        // FPS Limiter
        // ElapsedTime is the time taken to do everything above in paintComponent
        long elapsedTime = System.nanoTime() - startTime;

        // Have the System wait if needed to get the wanted frame rate
        long waitTime = frameTime - elapsedTime;
        try {
            Thread.sleep(Duration.ofNanos(waitTime));
        } catch (InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
