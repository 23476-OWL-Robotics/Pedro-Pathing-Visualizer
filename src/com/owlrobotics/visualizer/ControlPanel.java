package com.owlrobotics.visualizer;

import com.owlrobotics.visualizer.pedropathing.geometry.BezierCurve;
import com.owlrobotics.visualizer.pedropathing.geometry.BezierLine;
import com.owlrobotics.visualizer.pedropathing.geometry.Pose;
import com.owlrobotics.visualizer.pedropathing.paths.Path;
import com.owlrobotics.visualizer.pedropathing.paths.PathChain;
import com.owlrobotics.visualizer.ui.componentUI.CustomComboBoxUI;
import com.owlrobotics.visualizer.ui.componentUI.CustomScrollBarUI;
import com.owlrobotics.visualizer.ui.componentUI.CustomTabbedPaneUI;
import com.owlrobotics.visualizer.pedropathing.entities.BotEntity;
import com.owlrobotics.visualizer.ui.theme.Theme;
import com.owlrobotics.visualizer.util.enums.Interpolation;
import com.owlrobotics.visualizer.util.enums.PlaneOrigin;
import com.owlrobotics.visualizer.util.Point;
import com.owlrobotics.visualizer.util.XYLayout;
import com.owlrobotics.visualizer.ui.componentUI.CustomSliderUI;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

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

        // JPanel settings
        this.setPreferredSize(new Dimension(800, fieldSize));
        this.setLocation(fieldSize + 40, 20);
        this.setLayout(new XYLayout());
        this.setBackground(Theme.mainPanelBackgroundColor);

        initAll();
    }

    public void initAll() {
        this.removeAll();

        // Set fieldPanel and entities
        this.entities = fieldPanel.entities;

        // Font for the NameField
        Font nameFieldFont = new Font("", Font.BOLD, 22);

        // JTextField nameField creation and settings
        JTextField nameField = new JTextField();
        nameField.setBorder(BorderFactory.createEmptyBorder());
        nameField.setText("Pedro Pathing Visualizer");
        nameField.setPreferredSize(new Dimension(300, 40));
        nameField.setLocation(20, 10);
        nameField.setEditable(false);
        nameField.setFocusable(false);
        nameField.setFont(nameFieldFont);
        nameField.setBackground(Theme.controlPanelBackgroundColor);
        nameField.setForeground(Theme.textColor);

        // JTabbedPane creation
        JTabbedPane pane = new JTabbedPane();

        // Set the array sizes
        controlPanels = new JScrollPane[entities.size()];
        entityPanels = new EntityPanel[entities.size()];
        entityControlPanels = new EntityControlPanel[entities.size()];
        entitySlidePanels = new EntitySlidePanel[entities.size()];

        UIManager.put("TabbedPane.contentAreaColor", new Color(15, 15, 15));

        // pane settings
        pane.setUI(new CustomTabbedPaneUI());
        pane.setLocation(10, 60);
        pane.setPreferredSize(new Dimension(780, fieldPanel.fieldSize - 70));

        // for loop for each entity
        for (int botNumber = 0; botNumber < entities.size(); botNumber++) {

            JScrollBar bar = new JScrollBar();
            bar.setUI(new CustomScrollBarUI());
            bar.setBorder(BorderFactory.createEmptyBorder());

            // Create entityControlPanels
            entityControlPanels[botNumber] = new EntityControlPanel(
                    entities.get(botNumber),
                    fieldPanel,
                    botNumber);

            // Create controlPanels
            controlPanels[botNumber] = new JScrollPane(entityControlPanels[botNumber], JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            // controlPanels settings
            controlPanels[botNumber].setVerticalScrollBar(bar);
            controlPanels[botNumber].getVerticalScrollBar().setUnitIncrement(10);
            controlPanels[botNumber].setPreferredSize(new Dimension(756, fieldPanel.fieldSize - 174));
            //controlPanels[botNumber].setBorder(BorderFactory.createLineBorder(Theme.controlPanelBackgroundColor, 1));
            controlPanels[botNumber].setBorder(BorderFactory.createEmptyBorder());
            controlPanels[botNumber].setLocation(10, 6);

            // Create entityPanels
            entityPanels[botNumber] = new EntityPanel(fieldPanel.fieldSize, controlPanels[botNumber], fieldPanel, botNumber);

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

        // Turn on antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw a rounded rectangle as a background
        g2d.setColor(Theme.controlPanelBackgroundColor);
        g2d.fillRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 20, 20);

        // Update the control point x and y locations
        // Update the slider locations
        for (int botNumber = 0; botNumber < entities.size(); botNumber ++) {
            entityControlPanels[botNumber].updateText();
            entityPanels[botNumber].slidePanel.updateSliderLocation();
        }
    }

    // Class EntityPanel is the main panel that holds the components of each tab
    static class EntityPanel extends JPanel {

        // SlidePanel
        EntitySlidePanel slidePanel;
        JScrollPane scrollPane;

        // EntityPanel class constructor
        public EntityPanel(int fieldSize, JScrollPane scrollPane, FieldPanel fieldPanel, int botNumber) {

            // JPanel settings
            this.setLayout(new XYLayout());
            this.setLocation(0, 0);
            this.setBackground(Theme.tabbedPaneBorderColor);

            // Create slidePanel
            slidePanel = new EntitySlidePanel(fieldSize, fieldPanel, botNumber);

            // Add scrollPane and slidePanel
            this.add(scrollPane);
            this.add(slidePanel);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(Theme.controlPanelBackgroundColor);
            g2d.fillRoundRect(2, 0, getWidth() - 3, getHeight() - 62, 18, 18);

            g2d.fillRoundRect(2, getHeight() - 56, getWidth() - 3, 54, 18, 18);
        }
    }

    // Thr EntitySlidePanel class holds the JSlider and the Single Run Button
    static class EntitySlidePanel extends JPanel {

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
            this.setPreferredSize(new Dimension(760, 50));
            this.setLocation(6, fieldSize - 152);
            this.setBackground(Theme.controlPanelBackgroundColor);
            this.setLayout(new XYLayout());

            // Create slider
            JSlider slider = new JSlider();

            // Slider Settings
            slider.setUI(new CustomSliderUI(slider));
            slider.setPreferredSize(new Dimension(700, 20));
            slider.setLocation(50, 16);
            slider.setBackground(Theme.controlPanelBackgroundColor);
            slider.setMinimum(0);
            slider.setMaximum((fieldPanel.chain.get(botNumber).size() * 100));
            slider.setValue(0);

            // Add a changeListener to the JSlider
            slider.addChangeListener(e -> {

                if (slider.getMaximum() != (fieldPanel.chain.get(botNumber).size() * 100)) {
                    slider.setMaximum((fieldPanel.chain.get(botNumber).size() * 100));
                }

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
            this.add(new SingleRunButton(fieldPanel, botNumber));
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
    static class SingleRunButton extends JButton {

        // play boolean
        boolean play = false;

        // FieldPanel
        FieldPanel fieldPanel;

        // BotNumber
        int botNumber;

        // ControlButton class constructor
        public SingleRunButton(FieldPanel fieldPanel, int botNumber) {

            // Set fieldPanel and botNumber
            this.fieldPanel = fieldPanel;
            this.botNumber = botNumber;

            // JButton settings
            this.setPreferredSize(new Dimension(30, 30));
            this.setLocation(10, 10);
            this.setBorder(BorderFactory.createEmptyBorder());

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
            g2d.setColor(Theme.controlPanelBackgroundColor);
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
    static class MainRunButton extends JButton {

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
            this.setBorder(BorderFactory.createEmptyBorder());

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
            g2d.setColor(Theme.controlPanelBackgroundColor);
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
    static class MainResetButton extends JButton {

        // MainResetButton class constructor
        public MainResetButton(FieldPanel fieldPanel) {

            // JButton settings
            this.setPreferredSize(new Dimension(30, 34));
            this.setLocation(756, 12);
            this.setBorder(BorderFactory.createEmptyBorder());

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
            g2d.setColor(Theme.controlPanelBackgroundColor);
            g2d.fillRect(0, 0, 30, 34);

            // Set the color to white
            g2d.setColor(Theme.textColor);

            // Draw the reset button
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval(4, 4, 22, 22);
            g2d.setColor(Theme.controlPanelBackgroundColor);
            g2d.fillRect(0, 15, 15, 15);
            g2d.setColor(Theme.textColor);
            g2d.fillOval(3, 13, 3, 3);

            g2d.fillOval(10, 25, 3, 3);
            g2d.fillOval(14, 22, 3, 3);
            g2d.fillOval(14, 28, 3, 3);

            g2d.drawLine(11, 26, 15, 23);
            g2d.drawLine(11, 26, 15, 29);
            g2d.drawLine(15, 23, 15, 29);
        }
    }

    // The ColorButtons class will create a new JColorChooser when clicked, allowing you to change the line colors
    class ColorButtons extends JButton {

        Color color;
        Path path;

        public ColorButtons(Dimension location, Path path) {
            this.color = path.getPathColor();
            this.path = path;

            this.setPreferredSize(new Dimension(20, 20));
            this.setLocation(location.width, location.height);
            this.setBorder(BorderFactory.createEmptyBorder());

            this.addActionListener(e -> NewColorPicker(path, color));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            color = path.getPathColor();

            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Theme.controlPanelBackgroundColor);
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

            g2d.setColor(color);
            g2d.fillOval(3, 3, this.getWidth() - 5, this.getHeight() - 5);
        }
    }

    // The NewColorPicker creates a ColorChooser to pick a color for the path
    public void NewColorPicker(Path path, Color color) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException |
                 InstantiationException |
                 IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        // Create the Frame and ColorChooser
        JFrame frame = new JFrame("Color Picker");
        JColorChooser colorChooser = new JColorChooser();
        colorChooser.setColor(color);

        // Frame settings
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // Add the colorChooser
        frame.add(colorChooser);

        // Pack the frame to set the size
        frame.pack();

        // Add a change listener to know when a color is picked
        colorChooser.getSelectionModel().addChangeListener(e -> path.setPathColor(colorChooser.getColor()));
    }


    // The EntityControlPanel class contains all the x and y control point values
    class EntityControlPanel extends JPanel {

        // ArrayList of xLocations and yLocations
        ArrayList<JTextField[]> xLocations = new ArrayList<>();
        ArrayList<JTextField[]> yLocations = new ArrayList<>();

        // ArrayList of xLabelsArray and yLabelsArray
        ArrayList<JTextField[]> xLabels = new ArrayList<>();
        ArrayList<JTextField[]> yLabels = new ArrayList<>();

        ArrayList<JTextField[]> controlPointLabels = new ArrayList<>();

        // ArrayList of LineNames
        ArrayList<JTextField> lineNames = new ArrayList<>();
        ArrayList<JComboBox> lineInterpolationBox = new ArrayList<>();
        ArrayList<Interpolation> lineInterpolation = new ArrayList<>();
        ArrayList<LinePanel> linePanels = new ArrayList<>();

        ArrayList<ColorButtons> colorButtons = new ArrayList<>();

        AddLineButton addLineButton;

        // Point array pointLocations
        Point[] pointLocations;

        // PathChain and FieldPAnel
        PathChain chain;
        FieldPanel panel;

        // PixelsPerInch conversion
        double pixelsPerInch;

        // LocationNumber corrects for the end control point on one line being the start control point on another line
        int locationNumber;
        // TextYLocation sets the next line of text's y to the previous lines y + 10
        int textYLocation;

        int botNumber;

        // Font for the Line Text
        Font textFont = new Font("", Font.BOLD, 16);

        public EntityControlPanel(BotEntity entity, FieldPanel panel, int botNumber) {

            // Set the variables created above
            this.chain = entity.chain();
            this.pointLocations = fieldPanel.controlPointLocations.get(botNumber);
            this.pixelsPerInch = fieldPanel.pixelsPerInch;
            this.panel = panel;
            this.botNumber = botNumber;

            // JPanel settings
            this.setBackground(Theme.controlPanelBackgroundColor);
            this.setLayout(new XYLayout());
            this.setFocusable(true);

            initEntityPanel();
        }

        public void initEntityPanel() {
            this.removeAll();

            xLocations.clear();
            yLocations.clear();
            xLabels.clear();
            yLabels.clear();
            lineNames.clear();
            lineInterpolationBox.clear();
            lineInterpolation.clear();
            colorButtons.clear();
            linePanels.clear();
            controlPointLabels.clear();

            pointLocations = fieldPanel.controlPointLocations.get(botNumber);
            addLineButton = new AddLineButton(fieldPanel, this, chain);

            // LocationNumber corrects for the end control point on one line being the start control point on another line
            locationNumber = 0;
            // TextYLocation sets the next line of text's y to the previous lines y + 10
            textYLocation = 10;

            // startLocationName is the starting location text of the bot
            JTextField startLocationName = new JTextField();

            // These arrays are the x and y numbers and labels for the starting location
            JTextField[] startPointX = new JTextField[1];
            JTextField[] startPointY = new JTextField[1];
            JTextField[] startPointXLabel = new JTextField[1];
            JTextField[] startPointYLabel = new JTextField[1];

            // startLocationName settings
            startLocationName.setForeground(Theme.textColor);
            startLocationName.setEditable(false);
            startLocationName.setSize(100, 20);
            startLocationName.setBorder(BorderFactory.createEmptyBorder());
            startLocationName.setBackground(Theme.controlPanelBackgroundColor);
            startLocationName.setFont(textFont);
            startLocationName.setLocation(10, textYLocation);
            startLocationName.setText("Start Point");

            // Add 30 to textYLocation
            textYLocation += 30;

            // Create a new JTextField for all the arrays
            startPointX[0] = new JTextField();
            startPointY[0] = new JTextField();
            startPointXLabel[0] = new JTextField();
            startPointYLabel[0] = new JTextField();

            installTextBoxes(
                    startPointX[0],
                    new Dimension(25, textYLocation),
                    new Dimension(100, 20),
                    BorderFactory.createCompoundBorder(new CustomBorder(), new EmptyBorder(0, 4, 0, 0)),
                    true, true,
                    Theme.editableTextBackgroundColor);
            installTextBoxes(
                    startPointY[0],
                    new Dimension(165, textYLocation),
                    new Dimension(100, 20),
                    BorderFactory.createCompoundBorder(new CustomBorder(), new EmptyBorder(0, 4, 0, 0)),
                    true, true,
                    Theme.editableTextBackgroundColor);
            installTextBoxes(
                    startPointXLabel[0],
                    new Dimension(10, textYLocation),
                    new Dimension(20, 20),
                    BorderFactory.createEmptyBorder(),
                    false, false,
                    Theme.uneditableTextBackgroundColor);
            installTextBoxes(
                    startPointYLabel[0],
                    new Dimension(150, textYLocation),
                    new Dimension(20, 20),
                    BorderFactory.createEmptyBorder(),
                    false, false,
                    Theme.uneditableTextBackgroundColor);

            textYLocation += 30;

            // Set the Text
            startPointX[0].setText(String.valueOf(pointLocations[0].getX()));
            startPointY[0].setText(String.valueOf((pointLocations[0].getY())));
            startPointXLabel[0].setText("X:");
            startPointYLabel[0].setText("Y:");

            // Add keyListeners to the number text boxes to set the start location when a new number is entered
            startPointX[0].addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE) || (c == KeyEvent.VK_PERIOD) || (c == KeyEvent.VK_ENTER)) {
                        if (Objects.equals(startPointX[0].getText(), "")) {
                            pointLocations[0].setCoordinates(
                                    0,
                                    pointLocations[0].getY(),
                                    Point.CARTESIAN
                            );
                        } else {
                            pointLocations[0].setCoordinates(
                                    Double.parseDouble(startPointX[0].getText()),
                                    pointLocations[0].getY(),
                                    Point.CARTESIAN
                            );
                        }
                    } else {
                        e.consume();
                    }
                }
            });
            startPointY[0].addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE) || (c == KeyEvent.VK_PERIOD) || (c == KeyEvent.VK_ENTER)) {
                        if (Objects.equals(startPointY[0].getText(), "")) {
                            pointLocations[0].setCoordinates(
                                    pointLocations[0].getX(),
                                    0,
                                    Point.CARTESIAN
                            );
                        } else {
                            pointLocations[0].setCoordinates(
                                    pointLocations[0].getX(),
                                    Double.parseDouble(startPointY[0].getText()),
                                    Point.CARTESIAN
                            );
                        }
                    } else {
                        e.consume();
                    }
                }
            });

            // Add all the above things to the JPanel
            this.add(startLocationName);
            this.add(startPointX[0]);
            this.add(startPointY[0]);
            this.add(startPointXLabel[0]);
            this.add(startPointYLabel[0]);

            // Add the text arrays to the corresponding arrayLists
            xLocations.add(startPointX);
            yLocations.add(startPointY);
            xLabels.add(startPointXLabel);
            yLabels.add(startPointYLabel);

            // for loop for each bot
            for (int pathNumber = 0; pathNumber < chain.size(); pathNumber ++) {

                // Create new JTextField arrays
                JTextField[] xLocations = new JTextField[chain.getPath(pathNumber).getControlPoints().size()];
                JTextField[] yLocations = new JTextField[chain.getPath(pathNumber).getControlPoints().size()];
                JTextField[] xLabels = new JTextField[chain.getPath(pathNumber).getControlPoints().size()];
                JTextField[] yLabels = new JTextField[chain.getPath(pathNumber).getControlPoints().size()];

                JTextField[] controlPointLabels = new JTextField[chain.getPath(pathNumber).getControlPoints().size()];

                // Create a new textField and add it to lineNames
                lineNames.add(new JTextField());

                // JTextField lineName Settings
                lineNames.get(pathNumber).setForeground(Theme.textColor);
                lineNames.get(pathNumber).setEditable(false);
                lineNames.get(pathNumber).setFocusable(false);
                lineNames.get(pathNumber).setSize(100, 20);
                lineNames.get(pathNumber).setLocation(10, 0);
                lineNames.get(pathNumber).setBorder(BorderFactory.createEmptyBorder());
                lineNames.get(pathNumber).setBackground(Theme.uneditableTextBackgroundColor);
                lineNames.get(pathNumber).setFont(textFont);
                lineNames.get(pathNumber).setText("Line " + (pathNumber + 1));

                // Create a new ColorButton and add it to colorButtons
                colorButtons.add(new ColorButtons(new Dimension(60, 0), chain.getPath(pathNumber)));

                linePanels.add(new LinePanel(textYLocation, chain.getPath(pathNumber)));

                linePanels.get(pathNumber).add(lineNames.get(pathNumber));
                linePanels.get(pathNumber).add(colorButtons.get(pathNumber));

                if (chain.size() == 1) {
                    linePanels.get(pathNumber).add(new AddControlPointButton(fieldPanel, this, chain, 705, pathNumber));
                } else {
                    linePanels.get(pathNumber).add(new AddControlPointButton(fieldPanel, this, chain, 683, pathNumber));
                    linePanels.get(pathNumber).add(new RemoveLineButton(chain,fieldPanel, this, pathNumber));
                }

                // Increase YLocation
                textYLocation += 26 + 10;

                // Add that line name to the JPanel
                this.add(linePanels.get(pathNumber));

                // for loop for each control point
                for (int pointNumber = 0; pointNumber < chain.getPath(pathNumber).getControlPoints().size() - 1; pointNumber++) {

                    // Create new JTextFields for each x and y
                    xLocations[pointNumber] = new JTextField();
                    yLocations[pointNumber] = new JTextField();
                    xLabels[pointNumber] = new JTextField();
                    yLabels[pointNumber] = new JTextField();

                    controlPointLabels[pointNumber] = new JTextField();

                    installTextBoxes(
                            controlPointLabels[pointNumber],
                            new Dimension(10, textYLocation),
                            new Dimension(200, 20),
                            BorderFactory.createEmptyBorder(),
                            false, false,
                            Theme.uneditableTextBackgroundColor
                    );

                    textYLocation += 30;

                    installTextBoxes(
                            xLocations[pointNumber],
                            new Dimension(25, textYLocation),
                            new Dimension(100,20),
                            BorderFactory.createCompoundBorder(new CustomBorder(), new EmptyBorder(0, 4, 0, 0)),
                            true, true,
                            Theme.editableTextBackgroundColor);
                    installTextBoxes(
                            yLocations[pointNumber],
                            new Dimension(165, textYLocation),
                            new Dimension(100,20),
                            BorderFactory.createCompoundBorder(new CustomBorder(), new EmptyBorder(0, 4, 0, 0)),
                            true, true,
                            Theme.editableTextBackgroundColor);
                    installTextBoxes(
                            xLabels[pointNumber],
                            new Dimension(10, textYLocation),
                            new Dimension(20,20),
                            BorderFactory.createEmptyBorder(),
                            false, false,
                            Theme.uneditableTextBackgroundColor);
                    installTextBoxes(
                            yLabels[pointNumber],
                            new Dimension(150, textYLocation),
                            new Dimension(20,20),
                            BorderFactory.createEmptyBorder(),
                            false, false,
                            Theme.uneditableTextBackgroundColor);

                    // Set the text
                    xLocations[pointNumber].setText(String.valueOf(pointLocations[pointNumber + locationNumber + 1].getX()));
                    yLocations[pointNumber].setText(String.valueOf((pointLocations[pointNumber + locationNumber + 1].getY())));
                    xLabels[pointNumber].setText("X:");
                    yLabels[pointNumber].setText("Y:");

                    controlPointLabels[pointNumber].setFont(new Font("", Font.PLAIN, 16));

                    if (pointNumber == chain.getPath(pathNumber).getControlPoints().size() - 2) {
                        controlPointLabels[pointNumber].setText("End Point:");
                        lineInterpolationBox.add(new JComboBox(new String[]{" Linear", " Constant", " Tangential"}));
                        lineInterpolation.add(chain.getPath(pathNumber).getInterpolation());
                        installLineInterpolation(
                                lineInterpolationBox.get(pathNumber),
                                lineInterpolation.get(pathNumber),
                                chain.getPath(pathNumber));
                        this.add(lineInterpolationBox.get(pathNumber));
                    } else {
                        controlPointLabels[pointNumber].setText("Control Point " + (pointNumber + 1) + ":");
                        this.add(new RemoveControlPointButton(fieldPanel, this, chain.getPath(pathNumber), textYLocation, pointNumber));
                    }

                    textYLocation += 30;

                    // Add keyListeners to the number text boxes to set the start location when a new number is entered
                    int finalPointNumber = pointNumber;
                    int finalLocationNumber = locationNumber;
                    xLocations[pointNumber].addKeyListener(new KeyAdapter() {
                        public void keyTyped(KeyEvent e) {
                            char c = e.getKeyChar();
                            if (Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE) || (c == KeyEvent.VK_PERIOD) || (c == KeyEvent.VK_ENTER)) {
                                if (Objects.equals(xLocations[finalPointNumber].getText(), "")) {
                                    pointLocations[finalPointNumber + finalLocationNumber + 1].setCoordinates(
                                            0,
                                            pointLocations[finalPointNumber + finalLocationNumber + 1].getY(),
                                            Point.CARTESIAN
                                    );
                                } else {
                                    pointLocations[finalPointNumber + finalLocationNumber + 1].setCoordinates(
                                            Double.parseDouble(xLocations[finalPointNumber].getText()),
                                            pointLocations[finalPointNumber + finalLocationNumber + 1].getY(),
                                            Point.CARTESIAN
                                    );
                                }
                            } else {
                                e.consume();
                            }
                        }
                    });
                    yLocations[pointNumber].addKeyListener(new KeyAdapter() {
                        public void keyTyped(KeyEvent e) {
                            char c = e.getKeyChar();
                            if (Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE) || (c == KeyEvent.VK_PERIOD) || (c == KeyEvent.VK_ENTER)) {
                                if (Objects.equals(xLocations[finalPointNumber].getText(), "")) {
                                    pointLocations[finalPointNumber + finalLocationNumber + 1].setCoordinates(
                                            pointLocations[finalPointNumber + finalLocationNumber + 1].getX(),
                                            0,
                                            Point.CARTESIAN
                                    );
                                } else {
                                    pointLocations[finalPointNumber + finalLocationNumber + 1].setCoordinates(
                                            pointLocations[finalPointNumber + finalLocationNumber + 1].getX(),
                                            Double.parseDouble(yLocations[finalPointNumber].getText()),
                                            Point.CARTESIAN
                                    );
                                }
                            } else {
                                e.consume();
                            }
                        }
                    });

                    // Add the textFields to the JPanel
                    this.add(xLocations[pointNumber]);
                    this.add(yLocations[pointNumber]);
                    this.add(xLabels[pointNumber]);
                    this.add(yLabels[pointNumber]);
                    this.add(controlPointLabels[pointNumber]);
                }

                // Increase locationNumber
                locationNumber += chain.getPath(pathNumber).getControlPoints().size() - 1;

                // Add the arrays to the corresponding arrayLists
                this.xLocations.add(xLocations);
                this.yLocations.add(yLocations);
                this.xLabels.add(xLabels);
                this.yLabels.add(yLabels);
                this.controlPointLabels.add(controlPointLabels);

                addLineButton.setY(textYLocation);
                this.add(addLineButton);

                textYLocation += 30;

                // Set the JPanel preferredSize
                this.setPreferredSize(new Dimension(700, textYLocation + 10));
            }
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

        public void installTextBoxes(JTextField field, Dimension location, Dimension size, Border border, boolean editable, boolean focusable, Color background) {
            field.setEditable(editable);
            field.setFocusable(focusable);
            field.setLocation(location.width, location.height);
            field.setPreferredSize(size);
            field.setBorder(border);
            field.setBackground(background);
            field.setForeground(Theme.textColor);
            field.setCaretColor(Theme.textColor);
        }

        public void installLineInterpolation(JComboBox box, Interpolation interpolation, Path path) {
            box.setPreferredSize(new Dimension(100, 20));
            box.setLocation(300, textYLocation);
            box.setBorder(new CustomBorder());
            box.setForeground(Theme.textColor);
            box.setUI(new CustomComboBoxUI());

            InterpolationComponents interpolationComponents = new InterpolationComponents(this);

            switch (interpolation) {
                case Interpolation.LINEAR_HEADING_INTERPOLATION ->  {
                    box.setSelectedIndex(0);
                    interpolationComponents.addLinearInterpolation(path, box.getY());}
                case Interpolation.CONSTANT_HEADING_INTERPOLATION -> {
                    box.setSelectedIndex(1);
                    interpolationComponents.addConstantInterpolation(path, box.getY());}
                case Interpolation.TANGENTIAL_HEADING_INTERPOLATION -> {
                    box.setSelectedIndex(2);
                    interpolationComponents.addTangentialInterpolation(path, box.getY());}
                case null -> {
                    box.setSelectedIndex(2);
                    interpolationComponents.addTangentialInterpolation(path, box.getY());}
            }

            box.addActionListener(e -> {
                switch (box.getSelectedIndex()) {
                    case 0 -> {
                        interpolationComponents.removeAll();
                        interpolationComponents.addLinearInterpolation(path, box.getY());}
                    case 1 -> {
                        interpolationComponents.removeAll();
                        interpolationComponents.addConstantInterpolation(path, box.getY());}
                    case 2 -> {
                        interpolationComponents.removeAll();
                        interpolationComponents.addTangentialInterpolation(path, box.getY());}
                }
            });
        }

        static class InterpolationComponents {
            JTextField linearStartHeading;
            JTextField linearEndHeading;
            JTextField constantHeading;
            JTextField tangentialReversedText;
            JCheckBox tangentialReversed;
            JPanel panel;

            public InterpolationComponents(JPanel panel) {
                this.panel = panel;

                linearStartHeading = new JTextField();
                linearEndHeading = new JTextField();
                constantHeading = new JTextField();
                tangentialReversedText = new JTextField();
                tangentialReversed = new JCheckBox();
            }

            public void addLinearInterpolation(Path path, int y) {
                linearStartHeading = new JTextField();
                linearEndHeading = new JTextField();

                installTextBoxes(
                        linearStartHeading,
                        new Dimension(410, y),
                        new Dimension(40, 20),
                        BorderFactory.createCompoundBorder(new CustomBorder(), new EmptyBorder(0, 4, 0, 0)),
                        true, true,
                        Theme.editableTextBackgroundColor
                );

                installTextBoxes(
                        linearEndHeading,
                        new Dimension(460, y),
                        new Dimension(40, 20),
                        BorderFactory.createCompoundBorder(new CustomBorder(), new EmptyBorder(0, 4, 0, 0)),
                        true, true,
                        Theme.editableTextBackgroundColor
                );

                linearStartHeading.setText(String.valueOf(Math.toDegrees(path.startHeading)));
                linearEndHeading.setText(String.valueOf(Math.toDegrees(path.endHeading)));

                linearStartHeading.addActionListener(e ->  {
                    path.setLinearHeadingInterpolation(
                            Math.toRadians(Double.parseDouble(linearStartHeading.getText())),
                            Math.toRadians(Double.parseDouble(linearEndHeading.getText())));
                });
                linearEndHeading.addActionListener(e -> {
                    path.setLinearHeadingInterpolation(
                            Math.toRadians(Double.parseDouble(linearStartHeading.getText())),
                            Math.toRadians(Double.parseDouble(linearEndHeading.getText())));
                });

                panel.add(linearStartHeading);
                panel.add(linearEndHeading);
                path.setLinearHeadingInterpolation(path.startHeading, path.endHeading);
            }

            public void addConstantInterpolation(Path path, int y) {
                constantHeading = new JTextField();

                installTextBoxes(
                        constantHeading,
                        new Dimension(410, y),
                        new Dimension(40, 20),
                        BorderFactory.createCompoundBorder(new CustomBorder(), new EmptyBorder(0, 4, 0, 0)),
                        true, true,
                        Theme.editableTextBackgroundColor
                );

                constantHeading.setText(String.valueOf(Math.toDegrees(path.constantHeading)));

                constantHeading.addActionListener(e -> {
                    path.setConstantHeadingInterpolation(Math.toRadians(Double.parseDouble(constantHeading.getText())));
                });

                panel.add(constantHeading);
                path.setConstantHeadingInterpolation(path.constantHeading);
            }

            public void addTangentialInterpolation(Path path, int y) {
                tangentialReversedText = new JTextField();
                tangentialReversed = new JCheckBox();

                installTextBoxes(
                        tangentialReversedText,
                        new Dimension(410, y),
                        new Dimension(50, 20),
                        BorderFactory.createEmptyBorder(),
                        false, false,
                        Theme.uneditableTextBackgroundColor
                );
                tangentialReversedText.setText("Reverse:");

                tangentialReversed.setFocusable(true);
                tangentialReversed.setForeground(Theme.textColor);
                tangentialReversed.setBackground(Theme.uneditableTextBackgroundColor);
                tangentialReversed.setLocation(460, y);
                tangentialReversed.setPreferredSize(new Dimension(20, 20));

                tangentialReversed.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        path.reverseHeadingInterpolation();
                    } else {
                        path.setTangentHeadingInterpolation();
                    }
                });

                panel.add(tangentialReversedText);
                panel.add(tangentialReversed);
                path.setTangentHeadingInterpolation();
            }

            public void removeAll() {
                panel.remove(linearStartHeading);
                panel.remove(linearEndHeading);
                panel.remove(constantHeading);
                panel.remove(tangentialReversedText);
                panel.remove(tangentialReversed);
            }

            private void installTextBoxes(JTextField field, Dimension location, Dimension size, Border border, boolean editable, boolean focusable, Color background) {
                field.setEditable(editable);
                field.setFocusable(focusable);
                field.setForeground(Theme.textColor);
                field.setCaretColor(Theme.textColor);
                field.setBorder(border);
                field.setBackground(background);
                field.setLocation(location.width, location.height);
                field.setPreferredSize(size);
            }
        }
    }

    static class LinePanel extends JPanel {

        Path path;

        public LinePanel(int y, Path path) {
            this.setLayout(new XYLayout());
            this.setPreferredSize(new Dimension(730, 26));
            this.setLocation(0, y);
            this.setBackground(Color.WHITE);

            this.path = path;
        }

        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(path.getPathColor());

            g2d.drawLine(5, this.getHeight() - 1, this.getWidth() - 5, this.getHeight() - 1);
        }
    }

    static class AddControlPointButton extends JButton {

        PathChain pathChain;

        public AddControlPointButton(FieldPanel fieldPanel, EntityControlPanel entityControlPanel, PathChain chain, int x, int pathNumber) {
            this.pathChain = chain;

            this.setPreferredSize(new Dimension(20, 20));
            this.setLocation(x, 0);
            this.setBorder(BorderFactory.createEmptyBorder());

            this.addActionListener((ActionEvent e) -> {

                if (pathChain.getPath(pathNumber).getControlPoints().size() < 3) {
                    ArrayList<Pose> controlPoints = chain.getPath(pathNumber).getControlPoints();
                    Interpolation interpolation = chain.getPath(pathNumber).getInterpolation();

                    switch (interpolation) {
                        case LINEAR_HEADING_INTERPOLATION -> {
                            double startHeading = pathChain.getPath(pathNumber).startHeading;
                            double endHeading = pathChain.getPath(pathNumber).endHeading;

                            Color color = pathChain.getPath(pathNumber).getPathColor();

                            pathChain.removePath(pathNumber);

                            pathChain.addPath(pathNumber, new Path(
                                    new BezierCurve(
                                            controlPoints.get(0),
                                            randomPose(fieldPanel.origin, controlPoints.getLast()),
                                            controlPoints.get(1)
                                    )
                            ));
                            pathChain.getPath(pathNumber).setLinearHeadingInterpolation(startHeading, endHeading);
                            pathChain.getPath(pathNumber).setPathColor(color);
                        }
                        case CONSTANT_HEADING_INTERPOLATION -> {
                            double constantHeading = pathChain.getPath(pathNumber).constantHeading;

                            Color color = pathChain.getPath(pathNumber).getPathColor();

                            pathChain.removePath(pathNumber);

                            pathChain.addPath(pathNumber, new Path(
                                    new BezierCurve(
                                            controlPoints.get(0),
                                            randomPose(fieldPanel.origin, controlPoints.getLast()),
                                            controlPoints.get(1)
                                    )
                            ));
                            pathChain.getPath(pathNumber).setConstantHeadingInterpolation(constantHeading);
                            pathChain.getPath(pathNumber).setPathColor(color);
                        }
                        case TANGENTIAL_HEADING_INTERPOLATION -> {
                            boolean isReversed = pathChain.getPath(pathNumber).isReversed;

                            Color color = pathChain.getPath(pathNumber).getPathColor();

                            pathChain.removePath(pathNumber);

                            pathChain.addPath(pathNumber, new Path(
                                    new BezierCurve(
                                            controlPoints.get(0),
                                            randomPose(fieldPanel.origin, controlPoints.getLast()),
                                            controlPoints.get(1)
                                    )
                            ));
                            pathChain.getPath(pathNumber).setTangentHeadingInterpolation();
                            pathChain.getPath(pathNumber).setPathColor(color);
                            if (isReversed) {
                                pathChain.getPath(pathNumber).reverseHeadingInterpolation();
                            }
                        }
                    }
                } else {
                    chain.getPath(pathNumber).getControlPoints().add(
                            chain.getPath(pathNumber).getControlPoints().size() - 1,
                            randomPose(fieldPanel.origin, chain.getPath(pathNumber).getLastControlPoint()));
                }

                chain.getPath(pathNumber).init();

                fieldPanel.initAll();
                entityControlPanel.initEntityPanel();
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(Color.GREEN);

            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(getWidth() / 2, 3, getWidth() / 2, getHeight() - 3);
            g2d.drawLine(3, getHeight() / 2, getWidth() - 3, getHeight() / 2);
        }
    }
    
    static class RemoveControlPointButton extends JButton {
        
        Path path;

        public RemoveControlPointButton(FieldPanel fieldPanel, EntityControlPanel entityControlPanel, Path path, int y, int pointNumber) {
            this.path = path;
            this.setLayout(new XYLayout());

            this.setPreferredSize(new Dimension(20, 20));
            this.setLocation(270, y);
            this.setBorder(BorderFactory.createEmptyBorder());

            this.addActionListener((ActionEvent e) -> {
                path.getControlPoints().remove(pointNumber + 1);

                path.init();

                fieldPanel.initAll();
                entityControlPanel.initEntityPanel();
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(Color.RED);

            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(2, 2, getWidth() - 4, getHeight() - 4);
            g2d.drawLine(7, getHeight() / 2, 13, getHeight() / 2);
        }
    }

    static class AddLineButton extends JButton {
        PathChain pathChain;

        public AddLineButton(FieldPanel fieldPanel, EntityControlPanel entityControlPanel, PathChain pathChain) {
            this.pathChain = pathChain;

            this.setPreferredSize(new Dimension(100, 20));
            this.setBorder(BorderFactory.createEmptyBorder());

            this.addActionListener((ActionEvent e) -> {
                pathChain.addPath(new Path(
                        new BezierLine(
                                pathChain.getLast().getLastControlPoint(),
                                randomPose(fieldPanel.origin, pathChain.getLast().getLastControlPoint())
                        )
                ));

                fieldPanel.initAll();
                entityControlPanel.initEntityPanel();
            });
        }

        public void setY(int y) {
            this.setLocation(10, y + 10);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(Color.GREEN);

            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(20 / 2, 3, 20 / 2, 20 - 3);
            g2d.drawLine(3, 20 / 2, 20 - 3, 20 / 2);

            g2d.setStroke(new BasicStroke(1));
            g2d.setFont(new Font("", Font.PLAIN, 16));
            g2d.drawString("Add Line", 24, 16);
        }
    }

    static class RemoveLineButton extends JButton {

        PathChain pathChain;

        public RemoveLineButton(PathChain pathChain, FieldPanel fieldPanel, EntityControlPanel entityControlPanel, int pathNumber) {
            this.pathChain = pathChain;

            this.setPreferredSize(new Dimension(20, 20));
            this.setLocation(705, 0);
            this.setBorder(BorderFactory.createEmptyBorder());

            this.addActionListener((ActionEvent e) -> {
                pathChain.removePath(pathNumber);

                fieldPanel.initAll();
                entityControlPanel.initEntityPanel();
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(Color.RED);

            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(2, 2, getWidth() - 4, getHeight() - 4);
            g2d.drawLine(7, getHeight() / 2, 13, getHeight() / 2);
        }
    }

    public static Pose randomPose(PlaneOrigin origin, Pose withinRange) {

        double r1 = new Random().nextInt(60);
        double r2 = new Random().nextInt(60);

        r1 += withinRange.getX() - 30;
        r2 += withinRange.getY() - 30;

        return new Pose(r1, r2);
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
            
            // Turn on antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Erase background outside the border
            g2d.setColor(Theme.controlPanelBackgroundColor);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(-1, -1, c.getWidth() + 1, c.getHeight() + 1, 9, 9);
            
            // Draw border
            g2d.setColor(Theme.textBoxBorderColor);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 9, 9);
        }
    }
}
