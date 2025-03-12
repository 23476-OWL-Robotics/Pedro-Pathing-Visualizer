package com.owlrobotics.pedropathingvisualizer;

import com.owlrobotics.pedropathingvisualizer.pedropathing.entities.BotEntity;
import com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen.PathChain;
import com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen.Point;
import com.owlrobotics.pedropathingvisualizer.pedropathing.util.XYLayout;
import com.owlrobotics.pedropathingvisualizer.componentUI.CustomSliderUI;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class ControlPanel extends JPanel {

    // Component Arrays for the ControlPanel
    JScrollPane[] controlPanels;
    EntityPanel[] entityPanels;
    EntityControlPanel[] entityControlPanels;
    EntitySlidePanel[] entitySlidePanels;

    // ArrayList of BotEntities
    ArrayList<BotEntity> entities;

    // FieldPanel
    FieldPanel fieldPanel;

    // ControlPanel class constructor
    public ControlPanel(int fieldSize, ArrayList<BotEntity> entities, FieldPanel fieldPanel) {

        // Set fieldPanel and entities
        this.fieldPanel = fieldPanel;
        this.entities = entities;

        // JPanel settings
        this.setPreferredSize(new Dimension(800, fieldSize));
        this.setLocation(fieldSize + 40, 20);
        this.setLayout(new XYLayout());
        this.setBackground(Color.BLACK);

        // Font for the NameField
        Font nameFieldFont = new Font("", Font.BOLD, 22);

        // JTextField nameField creation and settings
        JTextField nameField = new JTextField();
        nameField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        nameField.setFont(nameFieldFont);
        nameField.setBackground(new Color(25, 25, 25));
        nameField.setForeground(Color.WHITE);
        nameField.setText("Pedro Pathing Visualizer");
        nameField.setPreferredSize(new Dimension(300, 40));
        nameField.setLocation(20, 10);
        nameField.setEditable(false);

        // JTabbedPane creation
        JTabbedPane pane = new JTabbedPane();

        // Set the array sizes
        controlPanels = new JScrollPane[entities.size()];
        entityPanels = new EntityPanel[entities.size()];
        entityControlPanels = new EntityControlPanel[entities.size()];
        entitySlidePanels = new EntitySlidePanel[entities.size()];

        // pane settings
        pane.setLocation(10, 60);
        pane.setPreferredSize(new Dimension(780, fieldSize - 72));

        // for loop for each entity
        for (int botNumber = 0; botNumber < entities.size(); botNumber++) {

            // Create entityControlPanels
            entityControlPanels[botNumber] = new EntityControlPanel(
                    entities.get(botNumber),
                    fieldPanel.controlPointLocations.get(botNumber),
                    fieldPanel.pixelsPerInch,
                    fieldPanel);

            // Create controlPanels
            controlPanels[botNumber] = new JScrollPane(entityControlPanels[botNumber], JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            // controlPanels settings
            controlPanels[botNumber].getVerticalScrollBar().setUnitIncrement(10);
            controlPanels[botNumber].setPreferredSize(new Dimension(775, fieldSize - 150));
            controlPanels[botNumber].setBorder(javax.swing.BorderFactory.createLineBorder(new Color(25, 25, 25), 5));
            controlPanels[botNumber].setLocation(0, 0);

            // Create entityPanels
            entityPanels[botNumber] = new EntityPanel(fieldSize, controlPanels[botNumber], fieldPanel, botNumber);

            // Add a tab for each entityPanel
            pane.addTab("Entity " + (botNumber + 1), entityPanels[botNumber]);
        }

        // Add nameField and run/reset buttons
        this.add(nameField);
        this.add(new MainRunButton(fieldPanel));
        this.add(new MainResetButton(fieldPanel));

        // Add the tabbed pane
        this.add(pane);
    }

    // Paint Component override for the ControlPanel
    @Override
    public void paintComponent(Graphics g) {

        // Create Graphics2D
        Graphics2D g2d = (Graphics2D) g;

        // Have the panel paint the component g
        super.paintComponent(g);

        // Turn on antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw a rounded rectangle as a background
        g2d.setColor(new Color(25, 25,25));
        g2d.fillRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 20, 20);

        // Update the control point x and y locations
        // Update the slider locations
        for (int botNumber = 0; botNumber < entities.size(); botNumber ++) {
            entityControlPanels[botNumber].updateText();
            entityPanels[botNumber].slidePanel.updateSliderLocation();
        }

        // Render the graphics
        repaint();
    }

    // Class EntityPanel is the main panel that holds the components of each tab
    class EntityPanel extends JPanel {

        // SlidePanel
        EntitySlidePanel slidePanel;

        // EntityPanel class constructor
        public EntityPanel(int fieldSize, JScrollPane scrollPane, FieldPanel fieldPanel, int botNumber) {

            // JPanel settings
            this.setLayout(new XYLayout());
            this.setLocation(0, 0);
            this.setBackground(new Color(50, 50, 50));

            // Create slidePanel
            slidePanel = new EntitySlidePanel(fieldSize, fieldPanel, botNumber);

            // Add scrollPane and slidePanel
            this.add(scrollPane);
            this.add(slidePanel);
        }
    }

    // Thr EntitySlidePanel class holds the JSlider and the Single Run Button
    class EntitySlidePanel extends JPanel {

        // FieldPanel and Slider
        FieldPanel fieldPanel;
        JSlider slider;

        // isUpdating tells when the slider is being manually controlled
        boolean isUpdating = false;

        // BotNumber
        int botNumber;

        // EntitySlidePanel class constructor
        public EntitySlidePanel(int fieldSize, FieldPanel fieldPanel, int botNumber) {

            // Add fieldPanel and botNumber
            this.fieldPanel = fieldPanel;
            this.botNumber = botNumber;

            // JPanel Settings
            this.setPreferredSize(new Dimension(775, 50));
            this.setLocation(0, fieldSize - 150);
            this.setBackground(new Color(25, 25, 25));
            this.setLayout(new XYLayout());

            // Create slider
            JSlider slider = new JSlider();

            // Slider Settings
            slider.setUI(new CustomSliderUI(slider));
            slider.setPreferredSize(new Dimension(710, 20));
            slider.setLocation(50, 15);
            slider.setBackground(new Color(25, 25, 25));
            slider.setMinimum(0);
            slider.setMaximum((fieldPanel.chain.get(botNumber).size() * 100));
            slider.setValue(0);

            // Add a changeListener to the JSlider
            slider.addChangeListener(e -> {

                // Check if the animation is running
                if (!fieldPanel.isSingleAnimationRunning(botNumber)) {

                    // Set isUpdating to true
                    isUpdating = true;

                    // Set the two animation variables to the Slider value
                    fieldPanel.animate1[botNumber] = Integer.parseInt(String.format("%02d", Math.abs(slider.getValue())%100));
                    if ((slider.getValue() - Integer.parseInt(String.format("%02d", Math.abs(slider.getValue())%100))) / 100 < fieldPanel.chain.get(botNumber).size()) {
                        fieldPanel.animate2[botNumber] = (slider.getValue() - Integer.parseInt(String.format("%02d", Math.abs(slider.getValue())%100))) / 100;
                    } else if ((slider.getValue() - Integer.parseInt(String.format("%02d", Math.abs(slider.getValue())%100))) / 100 == fieldPanel.chain.get(botNumber).size()) {
                        fieldPanel.animate1[botNumber] = 100;
                    }

                    // Set isUpdating to false
                    isUpdating = false;
                }
            });

            // Set slider
            this.slider = slider;

            // Add the slider and ControlButton
            this.add(slider);
            this.add(new ControlButton(fieldPanel, botNumber));
        }

        // updateSliderLocation updates the sliders value(location) when run
        public void updateSliderLocation() {

            // Check if isUpdating is false
            if (!isUpdating) {

                // Set the slider value
                slider.setValue(
                        fieldPanel.animate1[botNumber] + (fieldPanel.animate2[botNumber] * 100)
                );
            }
        }
    }

    // The ControlButton class is the button that runs a single bots animation
    class ControlButton extends JButton {

        // play boolean
        boolean play = false;

        // FieldPanel
        FieldPanel fieldPanel;

        // BotNumber
        int botNumber;

        // ControlButton class constructor
        public ControlButton(FieldPanel fieldPanel, int botNumber) {

            // Set fieldPanel and botNumber
            this.fieldPanel = fieldPanel;
            this.botNumber = botNumber;

            // JButton settings
            this.setPreferredSize(new Dimension(30, 30));
            this.setLocation(10, 10);
            this.setBorder(javax.swing.BorderFactory.createEmptyBorder());

            // Add an action listener to know when the button is clicked
            this.addActionListener(e -> {

                // Invert the play boolean
                play = !play;

                // If play is true, run that bot animation
                // If play is false, stop the bot animation
                if (play) {
                    fieldPanel.runSingleAnimation(botNumber);
                } else {
                    fieldPanel.stopSingleAnimation(botNumber);
                }
            });
        }

        // paintComponent override
        @Override
        public void paintComponent(Graphics g) {

            // Have the JButton paint the component g
            super.paintComponent(g);

            // Create Graphics2D g2d
            Graphics2D g2d = (Graphics2D) g;

            // Turn on antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // BackGround Rectangle
            g2d.setColor(new Color(25, 25, 25));
            g2d.fillRect(0, 0, 30, 30);

            // Set color and stroke size
            g2d.setColor(Color.GREEN);
            g2d.setStroke(new BasicStroke(3));

            // Set play to false if the single animation is complete
            if (play && !fieldPanel.isSingleAnimationRunning(botNumber)) {
                play = false;
            }

            // Draw a play sign or a pause sign based of what the value of play is
            if (play) {
                g2d.fillOval(7, 6, 3, 3);
                g2d.fillOval(7, 24, 3, 3);

                g2d.fillOval(18, 6, 3, 3);
                g2d.fillOval(18, 24,3, 3);

                g2d.drawLine(8, 9, 8, 23);
                g2d.drawLine(19, 9, 19, 23);
            } else {
                g2d.fillOval(4, 4, 3, 3);
                g2d.fillOval(4, 24, 3, 3);
                g2d.fillOval(21, 14, 3, 3);

                g2d.drawLine(5, 7, 5, 23);
                g2d.drawLine(6, 5, 21, 14);
                g2d.drawLine(21, 16, 6, 25);
            }
        }
    }

    // The MainRunButton class is the pause/play button at the top right of the control panel
    class MainRunButton extends JButton {

        // Play boolean
        boolean play = false;

        // Field panel
        FieldPanel fieldPanel;

        // MainRunButton class constructor
        public MainRunButton(FieldPanel fieldPanel) {

            // Set fieldPanel
            this.fieldPanel = fieldPanel;

            // JButton settings
            this.setPreferredSize(new Dimension(30, 30));
            this.setLocation(720, 12);
            this.setBorder(javax.swing.BorderFactory.createEmptyBorder());

            // Add an actionListener to know when the button is clicked
            this.addActionListener(e -> {

                // Invert play
                play = !play;

                // Set runAnimation and initAnimation to play
                fieldPanel.runAnimation = play;
                fieldPanel.initAnimation = !play;

                // Run Animation
                fieldPanel.runAnimation();
            });
        }

        // paintComponent override
        @Override
        public void paintComponent(Graphics g) {

            // Have the JButton paint the component g
            super.paintComponent(g);

            // Create Graphics2D g2d
            Graphics2D g2d = (Graphics2D) g;

            // Turn on antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw the background
            g2d.setColor(new Color(25, 25, 25));
            g2d.fillRect(0, 0, 30, 30);

            // Set play to false if the animation is finished
            if (!fieldPanel.isAnimationRunning() && play) {
                fieldPanel.resetRobotPose();
                play = false;
            }

            // Set the color and stroke size
            g2d.setColor(Color.GREEN);
            g2d.setStroke(new BasicStroke(3));

            // Draw a play sign or a pause sign based of what the value of play is
            if (play) {
                g2d.fillOval(7, 6, 3, 3);
                g2d.fillOval(7, 24, 3, 3);

                g2d.fillOval(18, 6, 3, 3);
                g2d.fillOval(18, 24,3, 3);

                g2d.drawLine(8, 9, 8, 23);
                g2d.drawLine(19, 9, 19, 23);
            } else {
                g2d.fillOval(4, 4, 3, 3);
                g2d.fillOval(4, 24, 3, 3);
                g2d.fillOval(21, 14, 3, 3);

                g2d.drawLine(5, 7, 5, 23);
                g2d.drawLine(6, 5, 21, 14);
                g2d.drawLine(21, 16, 6, 25);
            }
        }
    }

    // The MainResetButton class is the reset button in the top right of the control panel
    class MainResetButton extends JButton {

        // MainResetButton class constructor
        public MainResetButton(FieldPanel fieldPanel) {

            // JButton settings
            this.setPreferredSize(new Dimension(30, 34));
            this.setLocation(756, 12);
            this.setBorder(javax.swing.BorderFactory.createEmptyBorder());

            // Add an action listener to know when the button is pressed
            this.addActionListener(e -> fieldPanel.resetRobotPose());
        }

        // paintComponent override
        @Override
        public void paintComponent(Graphics g) {

            // Have the JButton paint the component g
            super.paintComponent(g);

            // Create Graphics2D g2d
            Graphics2D g2d = (Graphics2D) g;

            // Turn on antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw the Background
            g2d.setColor(new Color(25, 25, 25));
            g2d.fillRect(0, 0, 30, 34);

            // Set the color to white
            g2d.setColor(Color.WHITE);

            // Draw the reset button
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval(4, 4, 22, 22);
            g2d.setColor(new Color(25, 25, 25));
            g2d.fillRect(0, 15, 15, 15);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(3, 13, 3, 3);

            g2d.fillOval(10, 25, 3, 3);
            g2d.fillOval(14, 22, 3, 3);
            g2d.fillOval(14, 28, 3, 3);

            g2d.drawLine(11, 26, 15, 23);
            g2d.drawLine(11, 26, 15, 29);
            g2d.drawLine(15, 23, 15, 29);
        }
    }

    // The EntityControlPanel class contains all the x and y control point values
    class EntityControlPanel extends JPanel {

        // ArrayList of xLocations and yLocations
        ArrayList<JTextField[]> xLocations = new ArrayList<>();
        ArrayList<JTextField[]> yLocations = new ArrayList<>();

        // ArrayList of xLabelsArray and yLabelsArray
        ArrayList<JTextField[]> xLabelsArray = new ArrayList<>();
        ArrayList<JTextField[]> yLabelsArray = new ArrayList<>();

        // ArrayList of LineNames
        ArrayList<JTextField> lineNames = new ArrayList<>();

        // Point array pointLocations
        Point[] pointLocations;

        // PathChain and FieldPAnel
        PathChain chain;
        FieldPanel panel;

        // PixelsPerInch conversion
        double pixelsPerInch;

        // Font for the Line Text
        Font textFont = new Font("", Font.BOLD, 16);

        public EntityControlPanel(BotEntity entity, Point[] pointLocations, double pixelsPerInch, FieldPanel panel) {

            // Set the variables created above
            this.chain = entity.chain();
            this.pointLocations = pointLocations;
            this.pixelsPerInch = pixelsPerInch;
            this.panel = panel;

            // JPanel settings
            this.setBackground(Color.BLUE);
            this.setBackground(new Color(25, 25, 25));
            this.setLayout(new XYLayout());
            this.setFocusable(true);

            // LocationNumber corrects for the end control point on one line being the start control point on another line
            int locationNumber = 0;
            // TextYLocation sets the next line of text's y to the previous lines y + 10
            int textYLocation = 10;

            // startLocationName is the starting location text of the bot
            JTextField startLocationName = new JTextField();

            // These arrays are the x and y numbers and labels for the starting location
            JTextField[] addToX0 = new JTextField[1];
            JTextField[] addToY0 = new JTextField[1];
            JTextField[] xLabels0 = new JTextField[1];
            JTextField[] yLabels0 = new JTextField[1];

            // startLocationName settings
            startLocationName.setForeground(Color.WHITE);
            startLocationName.setEditable(false);
            startLocationName.setSize(100, 20);
            startLocationName.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            startLocationName.setBackground(new Color(25, 25, 25));
            startLocationName.setFont(textFont);
            startLocationName.setLocation(10, textYLocation);
            startLocationName.setText("Start Point");

            // Add 30 to textYLocation
            textYLocation += 30;

            // Create a new JTextField for all the arrays
            addToX0[0] = new JTextField();
            addToY0[0] = new JTextField();
            xLabels0[0] = new JTextField();
            yLabels0[0] = new JTextField();

            // Set the text fields to either be editable or non-editable
            addToX0[0].setEditable(true);
            addToY0[0].setEditable(true);
            xLabels0[0].setEditable(false);
            yLabels0[0].setEditable(false);

            // Set the x and y numbers to be focusable
            addToX0[0].setFocusable(true);
            addToY0[0].setFocusable(true);

            // Set the foreground to be white
            addToX0[0].setForeground(Color.WHITE);
            addToY0[0].setForeground(Color.WHITE);
            xLabels0[0].setForeground(Color.WHITE);
            yLabels0[0].setForeground(Color.WHITE);

            // Set the Border to CustomBorder
            addToX0[0].setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    new CustomBorder(),
                    new EmptyBorder(0, 8, 0, 0)));
            addToY0[0].setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    new CustomBorder(),
                    new EmptyBorder(0, 8, 0, 0)));
            
            // Set the labels border to be empty
            xLabels0[0].setBorder(javax.swing.BorderFactory.createEmptyBorder());
            yLabels0[0].setBorder(javax.swing.BorderFactory.createEmptyBorder());

            // Set the backgrounds
            addToX0[0].setBackground(Color.BLACK);
            addToY0[0].setBackground(Color.BLACK);
            xLabels0[0].setBackground(new Color(25, 25, 25));
            yLabels0[0].setBackground(new Color(25, 25, 25));

            // Set the locations
            addToX0[0].setLocation(25, textYLocation);
            addToY0[0].setLocation(165, textYLocation);
            xLabels0[0].setLocation(10, textYLocation);
            yLabels0[0].setLocation(150, textYLocation);
            textYLocation += 30;

            // Set the preferredSizes
            addToX0[0].setPreferredSize(new Dimension(100, 20));
            addToY0[0].setPreferredSize(new Dimension(100, 20));
            xLabels0[0].setPreferredSize(new Dimension(20, 20));
            yLabels0[0].setPreferredSize(new Dimension(20, 20));

            // Set the Text
            addToX0[0].setText(String.valueOf(pointLocations[0].getX()));
            addToY0[0].setText(String.valueOf((pointLocations[0].getY())));
            xLabels0[0].setText("X:");
            yLabels0[0].setText("Y:");

            // Add action listeners to the number text boxes to set the start location when a new number is entered
            addToX0[0].addActionListener(e -> 
                    pointLocations[0].setCoordinates(
                        Double.parseDouble(addToX0[0].getText()),
                        pointLocations[0].getY(),
                        Point.CARTESIAN
            ));
            addToY0[0].addActionListener(e -> 
                    pointLocations[0].setCoordinates(
                        pointLocations[0].getX(),
                        Double.parseDouble(addToY0[0].getText()),
                        Point.CARTESIAN
            ));

            // Add all the above things to the JPanel
            this.add(startLocationName);
            this.add(addToX0[0]);
            this.add(addToY0[0]);
            this.add(xLabels0[0]);
            this.add(yLabels0[0]);

            // Add the text arrays to the corresponding arrayLists
            xLocations.add(addToX0);
            yLocations.add(addToY0);
            xLabelsArray.add(xLabels0);
            yLabelsArray.add(yLabels0);

            // for loop for each bot
            for (int pathNumber = 0; pathNumber < chain.size(); pathNumber ++) {
                
                // Create new JTextField arrays
                JTextField[] addToX = new JTextField[chain.getPath(pathNumber).getControlPoints().size()];
                JTextField[] addToY = new JTextField[chain.getPath(pathNumber).getControlPoints().size()];
                JTextField[] xLabels = new JTextField[chain.getPath(pathNumber).getControlPoints().size()];
                JTextField[] yLabels = new JTextField[chain.getPath(pathNumber).getControlPoints().size()];

                // Create a new textField and add it to lineNames
                lineNames.add(new JTextField());
                
                // JTextField lineName Settings
                lineNames.get(pathNumber).setForeground(Color.WHITE);
                lineNames.get(pathNumber).setEditable(false);
                lineNames.get(pathNumber).setSize(100, 20);
                lineNames.get(pathNumber).setBorder(javax.swing.BorderFactory.createEmptyBorder());
                lineNames.get(pathNumber).setBackground(new Color(25, 25, 25));
                lineNames.get(pathNumber).setFont(textFont);
                lineNames.get(pathNumber).setLocation(10, textYLocation);
                lineNames.get(pathNumber).setText("Line " + (pathNumber + 1));
                textYLocation += lineNames.get(pathNumber).getHeight() + 10;
                
                // Add that line name to the JPanel
                this.add(lineNames.get(pathNumber));

                // for loop for each control point
                for (int pointNumber = 0; pointNumber < chain.getPath(pathNumber).getControlPoints().size() - 1; pointNumber++) {
                    
                    // Create new JTextFields for each x and y
                    addToX[pointNumber] = new JTextField();
                    addToY[pointNumber] = new JTextField();
                    xLabels[pointNumber] = new JTextField();
                    yLabels[pointNumber] = new JTextField();

                    // Set editable to be either true or false
                    addToX[pointNumber].setEditable(true);
                    addToY[pointNumber].setEditable(true);
                    xLabels[pointNumber].setEditable(false);
                    yLabels[pointNumber].setEditable(false);

                    // Set x and y numbers to be focusable
                    addToX[pointNumber].setFocusable(true);
                    addToY[pointNumber].setFocusable(true);

                    // Set the foreground to be white
                    addToX[pointNumber].setForeground(Color.WHITE);
                    addToY[pointNumber].setForeground(Color.WHITE);
                    xLabels[pointNumber].setForeground(Color.WHITE);
                    yLabels[pointNumber].setForeground(Color.WHITE);

                    // Set the x and y number borders to a new CustomBorder
                    addToX[pointNumber].setBorder(javax.swing.BorderFactory.createCompoundBorder(
                            new CustomBorder(),
                            new EmptyBorder(0, 8, 0, 0)));
                    addToY[pointNumber].setBorder(javax.swing.BorderFactory.createCompoundBorder(
                            new CustomBorder(),
                            new EmptyBorder(0, 8, 0, 0)));
                    
                    // Set the x and y labels to have an empty border
                    xLabels[pointNumber].setBorder(javax.swing.BorderFactory.createEmptyBorder());
                    yLabels[pointNumber].setBorder(javax.swing.BorderFactory.createEmptyBorder());

                    // Set the background colors
                    addToX[pointNumber].setBackground(Color.BLACK);
                    addToY[pointNumber].setBackground(Color.BLACK);
                    xLabels[pointNumber].setBackground(new Color(25, 25, 25));
                    yLabels[pointNumber].setBackground(new Color(25, 25, 25));

                    // Set the locations
                    addToX[pointNumber].setLocation(25, textYLocation);
                    addToY[pointNumber].setLocation(165, textYLocation);
                    xLabels[pointNumber].setLocation(10, textYLocation);
                    yLabels[pointNumber].setLocation(150, textYLocation);
                    textYLocation += 30;

                    // Set the preferredSizes
                    addToX[pointNumber].setPreferredSize(new Dimension(100, 20));
                    addToY[pointNumber].setPreferredSize(new Dimension(100, 20));
                    xLabels[pointNumber].setPreferredSize(new Dimension(20, 20));
                    yLabels[pointNumber].setPreferredSize(new Dimension(20, 20));

                    // Set the text
                    addToX[pointNumber].setText(String.valueOf(pointLocations[pointNumber + locationNumber + 1].getX()));
                    addToY[pointNumber].setText(String.valueOf((pointLocations[pointNumber + locationNumber + 1].getY())));
                    xLabels[pointNumber].setText("X:");
                    yLabels[pointNumber].setText("Y:");

                    // Add action listeners to the number text boxes to set the start location when a new number is entered
                    int finalPointNumber = pointNumber;
                    int finalLocationNumber = locationNumber;
                    addToX[pointNumber].addActionListener(e -> 
                            pointLocations[finalPointNumber + finalLocationNumber + 1].setCoordinates(
                                Double.parseDouble(addToX[finalPointNumber].getText()),
                                pointLocations[finalPointNumber + finalLocationNumber + 1].getY(),
                                Point.CARTESIAN
                    ));
                    addToY[pointNumber].addActionListener(e -> 
                            pointLocations[finalPointNumber + finalLocationNumber + 1].setCoordinates(
                                pointLocations[finalPointNumber + finalLocationNumber + 1].getX(),
                                Double.parseDouble(addToY[finalPointNumber].getText()),
                                Point.CARTESIAN
                    ));

                    // Add the textFields to the JPanel
                    this.add(addToX[pointNumber]);
                    this.add(addToY[pointNumber]);
                    this.add(xLabels[pointNumber]);
                    this.add(yLabels[pointNumber]);
                }
                
                // Increase locationNumber
                locationNumber += chain.getPath(pathNumber).getControlPoints().size() - 1;

                // Add the arrays to the corresponding arrayLists
                xLocations.add(addToX);
                yLocations.add(addToY);
                xLabelsArray.add(xLabels);
                yLabelsArray.add(yLabels);
            }
            // Set the JPanel preferredSize
            this.setPreferredSize(new Dimension(700, textYLocation + 10));
        }

        // UpdateText updates the x and y texts when called
        public void updateText() {
            
            // Check if doUpdate is true
            if (panel.doUpdate) {
                
                // Set the set of each of the text boxes
                xLocations.getFirst()[0].setText(String.valueOf(pointLocations[0].getX()));
                yLocations.getFirst()[0].setText(String.valueOf(pointLocations[0].getY()));
                int locationNumber = 0;
                for (int pathNumber = 0; pathNumber < chain.size(); pathNumber ++) {
                    for (int pointNumber = 0; pointNumber < chain.getPath(pathNumber).getControlPoints().size() - 1; pointNumber ++) {
                        xLocations.get(pathNumber + 1)[pointNumber].setText(String.valueOf((pointLocations[pointNumber + locationNumber + 1].getX())));
                        yLocations.get(pathNumber + 1)[pointNumber].setText(String.valueOf((pointLocations[pointNumber + locationNumber + 1].getY())));
                    }
                    locationNumber += chain.getPath(pathNumber).getControlPoints().size() - 1;
                }
            }
        }
    }

    // The CustomBorder class draws a rounded rectangle as a JTextField border
    public static class CustomBorder extends AbstractBorder {
        
        // paintBorder override
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y,
                                int width, int height) {
            
            // Have the AbstractBorder paint the border
            super.paintBorder(c, g, x, y, width, height);
            
            // Create Graphics2D g2d
            Graphics2D g2d = (Graphics2D)g;
            
            // Set the g2d color
            g2d.setColor(Color.DARK_GRAY);
            
            // Turn on antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw a rounded rectangle
            Shape shape = new RoundRectangle2D.Float(0, 0, c.getWidth()-1, c.getHeight()-1,9, 9);
            g2d.draw(shape);
        }
    }
}
