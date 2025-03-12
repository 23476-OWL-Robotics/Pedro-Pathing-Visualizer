package com.owlrobotics.pedropathingvisualizer;

import com.owlrobotics.pedropathingvisualizer.pedropathing.entities.BotEntity;
import com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen.PathChain;
import com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen.Point;
import com.owlrobotics.pedropathingvisualizer.pedropathing.util.XYLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class FieldPanel extends JPanel {

    // All ArrayLists needed for the visualizer
    ArrayList<JPanel[]> buttons = new ArrayList<>();
    ArrayList<Point[]> controlPointLocations = new ArrayList<>();
    ArrayList<BotEntity> entities;
    ArrayList<PathChain> chain = new ArrayList<>();

    // If someone is actually reading this, I apologize for this next Array list
    // It is confusing and a horrible way to get the dashboardDrawingPoints, but it's the best I could do
    ArrayList<ArrayList<double[][]>> curvePoints = new ArrayList<>();

    // The JFrame will be set to the frame created in com.owlrobotics.pedropathingvisualizer.PathVisualizer, it is required to get the frames location
    JFrame frame;

    // Plane Origin set in com.owlrobotics.pedropathingvisualizer.PathVisualizer
    Dimension planeOrigin;

    // FieldImage
    Image fieldImg;

    // Arrayed Image for the entities
    Image[] robotImg;

    // Animation settings
    boolean runAnimation = false;
    boolean[] runSingleBotAnimation;
    boolean initAnimation = false;
    boolean settingAnimationPoints = false;
    boolean doUpdate = false;

    // Animations variables
    // Animate 1 is the point on a path
    // Animate 2 defines the path
    int[] animate1;
    int[] animate2;

    // Arrayed integer for the botLocations and rotations
    int[] botLocationX;
    int[] botLocationY;
    double[] botRotation;

    // FieldLocations
    int fieldLocationX;
    int fieldLocationY;

    // Field Size and Rotation
    int fieldSize;
    double fieldRotation;

    // Pixels Per Inch for component placement conversions
    double pixelsPerInch;

    // X and Y Origin are the locations that the entities will be set to
    // They are set based on the PlaneOrigin set in Path Visualizer
    int xOrigin;
    int yOrigin;

    // Variables for managing fps
    int targetFPS;
    int frameTime;

    // Arrayed Thread for animating
    // Each robot will get its own thread for animating
    Thread[] threadAnimator;

    // com.owlrobotics.pedropathingvisualizer.FieldPanel class constructor
    public FieldPanel(BufferedImage fieldImg, JFrame frame, ArrayList<BotEntity> entities, double pixelsPerInch, Dimension planeOrigin, double fieldRotation, int fieldSize, int targetFPS) {
        // Set variables defined above
        this.fieldImg = fieldImg;
        this.frame = frame;
        this.entities = entities;
        this.pixelsPerInch = pixelsPerInch;
        this.planeOrigin = planeOrigin;
        this.fieldSize = fieldSize;
        this.fieldRotation = fieldRotation;
        this.targetFPS = targetFPS;

        // Set the frame time
        // The fps limiter uses nanoTime which is why the first number is so large
        frameTime = 1000000000 / targetFPS;

        // Set the botLocation and botRotation array size
        botLocationX = new int[entities.size()];
        botLocationY = new int[entities.size()];
        botRotation = new double[entities.size()];

        // Set the robotImage array size
        robotImg = new Image[entities.size()];

        // Set the animate arrays size
        animate1 = new int[entities.size()];
        animate2 = new int[entities.size()];

        // Set the xOrigin and yOrigin based on planeOrigin
        xOrigin = planeOrigin.width;
        yOrigin = planeOrigin.height - 10;

        // Set the runSingleBotAnimation array size
        runSingleBotAnimation = new boolean[entities.size()];

        // Set the animation threads array size
        threadAnimator = new Thread[entities.size()];

        // Set the Panel size based on the fieldImage size
        Dimension size = new Dimension(fieldImg.getWidth(), fieldImg.getHeight());
        this.setPreferredSize(size);

        // Set the layoutManager to XYLayout
        this.setLayout(new XYLayout());

        // Set the panel location on the com.owlrobotics.pedropathingvisualizer.MainPanel
        this.setLocation(20, 20);

        // First for loop to set values to the arrayLists created above
        // There are several for loops within each other, which I know is a bad thing to do
        // I again apologize for the bad optimization
        for (int botNumber = 0; botNumber < entities.size(); botNumber++) {

            // Add the entity chain to the local chain arrayList
            // The chain is just a list of paths
            chain.add(entities.get(botNumber).chain());

            // NextPath and MinusPath are used in each botNumber for loop
            // The nextPath is for the Last control point on a curve/line
            // The minusPath is needed because we have one less button for each control point
            int nextPath = 0;
            int minusPath = 0;

            // The number of buttons just defines how many JPanels are needed for each bot
            int numberOfButtons = 0;
            for (int pathNumber = 0; pathNumber < chain.get(botNumber).size(); pathNumber++) {
                numberOfButtons += chain.get(botNumber).getPath(pathNumber).getControlPoints().size();
            }

            // Add a new JPanel array of length numberOfButtons to each button arrayList
            buttons.add(new JPanel[numberOfButtons]);
            // Add a new Point array of length numberOfButtons to each pointLocation arrayList
            controlPointLocations.add(new Point[numberOfButtons]);

            // Set each runSingleBotAnimation boolean to false
            runSingleBotAnimation[botNumber] = false;

            // The addToCurvePoints arrayList will be added to the CurvePoints arrayList after each double is set
            ArrayList<double[][]> addToCurvePoints = new ArrayList<>();

            // Second for loop for each pathNumber
            for (int pathNumber = 0; pathNumber < chain.get(botNumber).size(); pathNumber ++) {

                // New random Color for each Path
                Random r = new Random();
                Random g = new Random();
                Random b = new Random();
                chain.get(botNumber).getPath(pathNumber).setPathColor(new Color(
                        r.nextInt(255),
                        g.nextInt(255),
                        b.nextInt(255)
                ));

                // Set each double in the addToCurvePoints arrayList
                addToCurvePoints.add(chain.get(botNumber).getPath(pathNumber).getDashboardDrawingPoints());

                // The if statements check to see if we are setting the control point buttons on the first path
                if (pathNumber == 0) {

                    // Third for loop for each point number
                    for (int pointNumber = 0; pointNumber < chain.get(botNumber).getPath(pathNumber).getControlPoints().size(); pointNumber++) {

                        // Create a new JPanel for each button
                        buttons.get(botNumber)[pointNumber + nextPath] = new JPanel();

                        // Set the background of each button
                        buttons.get(botNumber)[pointNumber + nextPath].setBackground(chain.get(botNumber).getPath(pathNumber).getPathColor());

                        // Set the location of each button
                        buttons.get(botNumber)[pointNumber + nextPath].setLocation(
                                (int) (chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getX() * pixelsPerInch) - 5 + xOrigin,
                                (int) -(chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getY() * pixelsPerInch) + 5 + yOrigin);

                        // Set the controlPointLocations
                        // These are used is com.owlrobotics.pedropathingvisualizer.ControlPanel
                        controlPointLocations.get(botNumber)[pointNumber + nextPath] = new Point(
                                chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getX(),
                                (chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getY()));

                        // Set each button to be visible
                        buttons.get(botNumber)[pointNumber + nextPath].setVisible(true);

                        // Set each button to be un-focusable and not enabled
                        buttons.get(botNumber)[pointNumber + nextPath].setFocusable(false);
                        buttons.get(botNumber)[pointNumber + nextPath].setEnabled(false);

                        // Add a MouseMotionListener to each button
                        // This makes each button draggable
                        buttons.get(botNumber)[pointNumber + nextPath].addMouseMotionListener(new Listener(
                                buttons.get(botNumber)[pointNumber + nextPath],
                                controlPointLocations.get(botNumber)[pointNumber + nextPath]));

                        // Add a MouseListener to each button
                        // This just checks if a mouse is clicked on the button
                        buttons.get(botNumber)[pointNumber + nextPath].addMouseListener(new MListener());

                        // Set the preferred size of each button
                        buttons.get(botNumber)[pointNumber + nextPath].setPreferredSize(new Dimension(10, 10));

                        // Add each button to the com.owlrobotics.pedropathingvisualizer.FieldPanel
                        this.add(buttons.get(botNumber)[pointNumber + nextPath]);
                    }
                } else {

                    // Third for loop for each point number
                    for (int pointNumber = 1; pointNumber < chain.get(botNumber).getPath(pathNumber).getControlPoints().size(); pointNumber++) {

                        // Create a new JPanel for each button
                        buttons.get(botNumber)[pointNumber + nextPath - minusPath] = new JPanel();

                        // Set the background of each button
                        buttons.get(botNumber)[pointNumber + nextPath - minusPath].setBackground(chain.get(botNumber).getPath(pathNumber).getPathColor());

                        // Set the location of each button
                        buttons.get(botNumber)[pointNumber + nextPath - minusPath].setLocation(
                                (int) (chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getX() * pixelsPerInch) - 5 + xOrigin,
                                (int) -(chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getY() * pixelsPerInch) + 5 + yOrigin);

                        // Set the controlPointLocations
                        // These are used is com.owlrobotics.pedropathingvisualizer.ControlPanel
                        controlPointLocations.get(botNumber)[pointNumber + nextPath - minusPath] = new Point(
                                (int) (chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getX()),
                                (int) (chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getY()));

                        // Set each button to be visible
                        buttons.get(botNumber)[pointNumber + nextPath - minusPath].setVisible(true);

                        // Set each button to be un-focusable and not enabled
                        buttons.get(botNumber)[pointNumber + nextPath - minusPath].setFocusable(false);
                        buttons.get(botNumber)[pointNumber + nextPath - minusPath].setEnabled(false);

                        // Add a MouseMotionListener to each button
                        // This makes each button draggable
                        buttons.get(botNumber)[pointNumber + nextPath - minusPath].addMouseMotionListener(new Listener(
                                buttons.get(botNumber)[pointNumber + nextPath - minusPath],
                                controlPointLocations.get(botNumber)[pointNumber + nextPath - minusPath]));

                        // Add a MouseListener to each button
                        // This just checks if a mouse is clicked on the button
                        buttons.get(botNumber)[pointNumber + nextPath - minusPath].addMouseListener(new MListener());

                        // Set the preferred size of each button
                        buttons.get(botNumber)[pointNumber + nextPath - minusPath].setPreferredSize(new Dimension(10, 10));

                        // Add each button to the com.owlrobotics.pedropathingvisualizer.FieldPanel
                        this.add(buttons.get(botNumber)[pointNumber + nextPath - minusPath]);
                    }
                }

                // Up nextPath and minusPath
                nextPath += chain.get(botNumber).getPath(pathNumber).getControlPoints().size();
                minusPath ++;
            }

            // Add each addToCurvePoints to the curvePoints array
            curvePoints.add(addToCurvePoints);

            // Set each animate to 0
            animate1[botNumber] = 0;
            animate2[botNumber] = 0;

            // Set the botLocation and botRotation
            botLocationX[botNumber] = (int) (curvePoints.get(botNumber).getFirst()[0][0] - entities.get(botNumber).robotSize().width * pixelsPerInch / 2);
            botLocationY[botNumber] = (int) (curvePoints.get(botNumber).getFirst()[1][0] - entities.get(botNumber).robotSize().height * pixelsPerInch / 2);
            botRotation[botNumber] = chain.get(botNumber).getPath(0).getHeadingGoal(0);

            // Get the scaled Instance of the botImage
            robotImg[botNumber] = entities.get(botNumber).robotImage().getScaledInstance(
                    (int) (entities.get(botNumber).robotSize().width * pixelsPerInch),
                    (int) (entities.get(botNumber).robotSize().height * pixelsPerInch),
                    Image.SCALE_DEFAULT
            );
        }

        // Set the com.owlrobotics.pedropathingvisualizer.FieldPanel to be focusable and visible
        this.setFocusable(true);
        this.setVisible(true);
    }

    // This is the main loop of the Visualizer
    // Everything in com.owlrobotics.pedropathingvisualizer.FieldPanel is drawn here and the fps limiter si implemented here
    @Override
    public void paintComponent(Graphics g) {

        // StartTime for the fps limiter
        long startTime = System.nanoTime();

        // super.paintComponent(g) tells com.owlrobotics.pedropathingvisualizer.FieldPanel to draw g
        super.paintComponent(g);

        // Graphics2D is more advanced than Graphics, which is why it is used instead
        Graphics2D g2d = (Graphics2D) g;

        // Turn on Antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // DrawImage function draws the fieldImage
        DrawField(g2d);

        // DrawRobot function draws the robotImage
        DrawRobot(g2d);

        // DrawControlPoints draws the control points of each chain path
        DrawControlPoints(g2d);

        // DrawBezierCurves draws the curves of each path
        DrawBezierCurves(g2d);

        // Repaint updates the rendered screen
        repaint();

        // FPS Limiter
        // ElapsedTime is the time taken to do everything above in paintComponent
        long elapsedTime = System.nanoTime() - startTime;

        // Have the System wait if needed to get the wanted frame rate
        long waitTime = frameTime - elapsedTime;
        if (waitTime > 0) {
            try {
                TimeUnit.NANOSECONDS.sleep(waitTime);
            } catch (InterruptedException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
    }

    // The DrawField function draws the fieldImage
    public void DrawField(Graphics2D g2d) {

        // Save the current g2d transform
        AffineTransform saveXForm = g2d.getTransform();

        // Create the needed transform for the field rotation
        AffineTransform toTransform = new AffineTransform();

        // Rotate the toTransform to the needed field rotation
        toTransform.rotate(
                -fieldRotation,
                (double) fieldSize / 2,
                (double) fieldSize / 2
        );

        // Set the g2d transform to the toTransform
        g2d.setTransform(toTransform);

        // Draw the Image
        g2d.drawImage(fieldImg, 0, 0, null);

        // Reset g2d transform
        g2d.setTransform(saveXForm);
    }

    // The DrawRobot function draws the robotImage
    public void DrawRobot(Graphics2D g2d) {

        // For loop for every robot
        for (int botNumber = 0; botNumber < entities.size(); botNumber++) {

            // Get the botLocations and botLocation
            botLocationX[botNumber] = (int) Math.round(
                    (curvePoints.get(botNumber).get(animate2[botNumber])[0][animate1[botNumber]] * pixelsPerInch) -
                            (double) entities.get(botNumber).robotSize().width * pixelsPerInch / 2);
            botLocationY[botNumber] = (int) Math.round(
                    (yOrigin - (curvePoints.get(botNumber).get(animate2[botNumber])[1][animate1[botNumber]] * pixelsPerInch) + 10) -
                            (double) entities.get(botNumber).robotSize().height * pixelsPerInch / 2);
            botRotation[botNumber] = chain.get(botNumber).getPath(animate2[botNumber]).getHeadingGoal(animate1[botNumber] * 0.01);

            // Save the current g2d transform
            AffineTransform saveXForm = g2d.getTransform();

            // Create the needed transform for the robot rotation
            AffineTransform toTransform = new AffineTransform();

            // Rotate the toTransform to the needed robot rotation
            toTransform.rotate(
                    -botRotation[botNumber],
                    botLocationX[botNumber]+ entities.get(botNumber).robotSize().width * pixelsPerInch / 2,
                    botLocationY[botNumber] + entities.get(botNumber).robotSize().height * pixelsPerInch / 2);

            // Set the g2d transform to the toTransform
            g2d.setTransform(toTransform);

            // Draw the robotImage
            g2d.drawImage(
                    robotImg[botNumber],
                    botLocationX[botNumber],
                    botLocationY[botNumber], null);

            // Reset the g2d transform
            g2d.setTransform(saveXForm);
        }
    }

    // The DrawBezierCurves function draws the curves of all the paths
    public void DrawBezierCurves(Graphics2D g2d) {

        // Set the g2d stroke to 4 pixels thick
        g2d.setStroke(new BasicStroke(4));

        // First for loop for each bot
        for (int botNumber = 0; botNumber < entities.size(); botNumber++) {

            // NextPath and MinusPath
            int nextPath = 0;
            int minusPath = 0;

            // Second for loop for each path
            for (int pathNumber = 0; pathNumber < chain.get(botNumber).size(); pathNumber++) {

                // Set the g2d Color to the pathColor
                g2d.setColor(chain.get(botNumber).getPath(pathNumber).getPathColor());

                // If we are on the first path
                if (pathNumber == 0) {

                    // Third for loop for each point
                    for (int pointNumber = 0; pointNumber < chain.get(botNumber).getPath(pathNumber).getControlPoints().size(); pointNumber++) {

                        // Update the chain ControlPoint locations
                        chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).setCoordinates(
                                controlPointLocations.get(botNumber)[pointNumber + nextPath].getX(),
                                controlPointLocations.get(botNumber)[pointNumber + nextPath].getY(),
                                Point.CARTESIAN
                        );

                        // Update the button locations
                        buttons.get(botNumber)[pointNumber + nextPath].setLocation(
                                (int) (chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getX() * pixelsPerInch) - 5 + xOrigin,
                                (int) -(chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getY() * pixelsPerInch) + 5 + yOrigin);
                    }

                } else {

                    // Third for loop for each point
                    for (int pointNumber = 0; pointNumber < chain.get(botNumber).getPath(pathNumber).getControlPoints().size(); pointNumber++) {

                        // Update the chain ControlPoint locations
                        chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).setCoordinates(
                                controlPointLocations.get(botNumber)[pointNumber + nextPath - minusPath].getX(),
                                controlPointLocations.get(botNumber)[pointNumber + nextPath - minusPath].getY(),
                                Point.CARTESIAN
                        );

                        // Update the button locations
                        buttons.get(botNumber)[pointNumber + nextPath - minusPath].setLocation(
                                (int) (chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getX() * pixelsPerInch) - 5 + xOrigin,
                                (int) -(chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getY() * pixelsPerInch) + 5 + yOrigin);
                    }
                }

                // Refresh DashboardDrawingPoints
                chain.get(botNumber).getPath(pathNumber).refreshDashboardDrawingPoints();

                // Get DashboardDrawingPoints
                curvePoints.get(botNumber).set(pathNumber, chain.get(botNumber).getPath(pathNumber).getDashboardDrawingPoints());

                // for loop to draw the Bézier curves of each path
                for (int drawPoint = 0; drawPoint < 100; drawPoint++) {

                    // Draw a line between each drawing point
                    g2d.drawLine(
                            (int) (Math.round((curvePoints.get(botNumber).get(pathNumber)[0][drawPoint] * pixelsPerInch))),
                            (int) (Math.round((yOrigin - (curvePoints.get(botNumber).get(pathNumber)[1][drawPoint] * pixelsPerInch) + 10))),
                            (int) (Math.round((curvePoints.get(botNumber).get(pathNumber)[0][drawPoint+1] * pixelsPerInch))),
                            (int) (Math.round((yOrigin - (curvePoints.get(botNumber).get(pathNumber)[1][drawPoint+1] * pixelsPerInch) + 10))));
                }

                // Up nextPath and MinusPath
                nextPath += chain.get(botNumber).getPath(pathNumber).getControlPoints().size();
                minusPath ++;
            }
        }

        // Set the g2d stroke to 1 pixel thick
        g2d.setStroke(new BasicStroke(1));
    }

    // The DrawControlPoints function draws the control points
    public void DrawControlPoints(Graphics2D g2d) {

        // Set the g2d stroke to be 6 pixels thick
        g2d.setStroke(new BasicStroke(6));

        // First for loop for each bot
        for (int botNumber = 0; botNumber < entities.size(); botNumber++) {

            // NextPath and Minus Path
            int nextPath = 0;
            int minusPath = 0;

            // Second for loop for each path
            for (int pathNumber = 0; pathNumber < chain.get(botNumber).size(); pathNumber++) {

                // Set the g2d Color to the path color
                g2d.setColor(chain.get(botNumber).getPath(pathNumber).getPathColor());

                // If we are on the first path
                if (pathNumber == 0) {

                    // Third for loop for each point
                    for (int pointNumber = 0; pointNumber < chain.get(botNumber).getPath(pathNumber).getControlPoints().size(); pointNumber++) {

                        // Draw an oval on each button location
                        g2d.drawOval(
                                buttons.get(botNumber)[pointNumber + nextPath].getX() - 2,
                                buttons.get(botNumber)[pointNumber + nextPath].getY() - 2,
                                13,
                                13);
                    }
                } else {

                    // Third for loop for each point
                    for (int pointNumber = 1; pointNumber < chain.get(botNumber).getPath(pathNumber).getControlPoints().size(); pointNumber++) {

                        // Draw an oval on each button location
                        g2d.drawOval(
                                buttons.get(botNumber)[pointNumber + nextPath - minusPath].getX() - 2,
                                buttons.get(botNumber)[pointNumber + nextPath - minusPath].getY() - 2,
                                13,
                                13);
                    }
                }

                // Up nextPath and minusPath
                nextPath += chain.get(botNumber).getPath(pathNumber).getControlPoints().size();
                minusPath ++;
            }
        }

        // Set the g2d stroke to 1 pixel thick
        g2d.setStroke(new BasicStroke(1));
    }

    // The resetRobotPose resets all the robots positions
    public void resetRobotPose() {

        // Set runAnimation to false
        runAnimation = false;

        // For loop for each bot
        for (int botNumber = 0; botNumber < entities.size(); botNumber++) {

            // Set both animates to 0
            animate1[botNumber] = 0;
            animate2[botNumber] = 0;
        }
    }

    // The resetSingleRobotPose resets a single robots position
    public void resetSingleRobotPose(int botNumber) {

        // set runSingleBotAnimation of that bot to false
        runSingleBotAnimation[botNumber] = false;

        // Set both animates of that bot to 0
        animate1[botNumber] = 0;
        animate2[botNumber] = 0;
    }

    // MouseMotionListener for each button
    class Listener implements MouseMotionListener {

        // JPanel and Point variables
        JPanel panel;
        Point point;

        // Listener class constructor
        public Listener(JPanel panel, Point point) {
            this.panel = panel;
            this.point = point;
        }

        // mouseDragged only runs if the mouse is dragged on the selected component
        @Override
        public void mouseDragged(MouseEvent e) {
            // Set fieldLocation
            fieldLocationX = frame.getX() + 20;
            fieldLocationY = frame.getY() + 20;

            // Set the button location
            panel.setLocation(e.getXOnScreen() - fieldLocationX - 13, e.getYOnScreen() - fieldLocationY - 35);

            // Set the point location
            point.setCoordinates(
                    (e.getXOnScreen() - fieldLocationX - 13 + 5) / pixelsPerInch,
                    (yOrigin - (e.getYOnScreen() - fieldLocationY - 40)) / pixelsPerInch,
                    Point.CARTESIAN
            );
        }

        // mouseMoved is not used
        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }

    // MouseListener for each button
    class MListener implements MouseListener {

        // mouseClicked is not used
        @Override
        public void mouseClicked(MouseEvent e) {

        }

        // Set doUpdate to true for com.owlrobotics.pedropathingvisualizer.ControlPanel
        @Override
        public void mousePressed(MouseEvent e) {
            doUpdate = true;
        }

        // Set doUpdate to false for com.owlrobotics.pedropathingvisualizer.ControlPanel
        @Override
        public void mouseReleased(MouseEvent e) {
            doUpdate = false;
        }

        // mouseEntered is not used
        @Override
        public void mouseEntered(MouseEvent e) {

        }

        // mouseExited is not used
        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    // The runSingleAnimation function starts an animation thread for a single robot
    public void runSingleAnimation(int botNumber) {
        // Create a new RunAnimation
        RunAnimation animation = new RunAnimation(botNumber);

        // Set the runSingleAnimation of the bot to true
        runSingleBotAnimation[botNumber] = true;

        // Create the threadAnimator for that bot and start it
        threadAnimator[botNumber] = new Thread(animation);
        threadAnimator[botNumber].start();
    }

    // Thr runAnimation function starts all the animation threads
    public void runAnimation() {

        // Check to see if the animation is already running
        if (runAnimation && !initAnimation) {

            // for loop for each bot
            for (int botNumber = 0; botNumber < entities.size(); botNumber++) {
                // Create a new RunaAnimation
                RunAnimation animation = new RunAnimation(botNumber);

                // Create the threadAnimator and start it
                threadAnimator[botNumber] = new Thread(animation);
                threadAnimator[botNumber].start();
            }

            // Set initAnimation to true
            initAnimation = true;
        }
    }

    // The stopSingleAnimation function stops an animation thread for a single robot
    public void stopSingleAnimation(int botNumber) {

        // Set the runSingleAnimation of the bot to false
        runSingleBotAnimation[botNumber] = false;

        // Check if the thread is even running
        if (threadAnimator[botNumber].isAlive()) {

            // Interrupt the thread to stop it
            threadAnimator[botNumber].interrupt();
        }
    }

    // The isSingleAnimationRunning boolean returns that runSingleBotAnimation boolean
    public boolean isSingleAnimationRunning(int botNumber) {
        return runSingleBotAnimation[botNumber];
    }

    // The isAnimationRunning boolean tells if the animation is running
    public boolean isAnimationRunning() {

        // for loop for each bot
        for (int i = 0; i < entities.size(); i++) {

            // Check if the threadAnimator for each bot is not null and is alive
            if (threadAnimator[i] != null && threadAnimator[i].isAlive()) {
                // If so, return true
                return true;
            }
        }
        // Else return false
        return false;
    }

    // Class RunAnimation for animating
    class RunAnimation extends Thread {

        // botNumber variable
        int botNumber;

        // RunAnimation class contractor
        public RunAnimation(int botNumber) {

            // Set the botNumber variable
            this.botNumber = botNumber;
        }

        // On thread.start()
        public void run() {

            // Create integer arrays for distanceToNextBotLocation
            int[] distanceToNextBotLocationX = new int[entities.size()];
            int[] distanceToNextBotLocationY = new int[entities.size()];
            int[] distanceToNextBotLocation = new int[entities.size()];

            // While the animation is running
            while (runAnimation || runSingleBotAnimation[botNumber]) {

                // Setting animation points is true while we are getting the curvePoints
                settingAnimationPoints = true;

                // check if animate 1 is less than 100
                if (animate1[botNumber] < 100) {

                    // Set distanceToNextBotLocations
                    distanceToNextBotLocationX[botNumber] =
                            botLocationX[botNumber] -
                                    (int) Math.round(
                                            curvePoints.get(botNumber).get(animate2[botNumber])[0][animate1[botNumber] + 1] * pixelsPerInch -
                                            (double) entities.get(botNumber).robotSize().width * pixelsPerInch / 2);
                    distanceToNextBotLocationY[botNumber] =
                            botLocationY[botNumber] -
                                    (int) Math.round(
                                            yOrigin - (curvePoints.get(botNumber).get(animate2[botNumber])[1][animate1[botNumber] + 1] * pixelsPerInch) + 10 -
                                            (double) entities.get(botNumber).robotSize().height * pixelsPerInch / 2);

                    // Set the Overall nextBotLocation variable
                    distanceToNextBotLocation[botNumber] = (int) Math.sqrt(
                            (distanceToNextBotLocationX[botNumber] * distanceToNextBotLocationX[botNumber]) +
                                    (distanceToNextBotLocationY[botNumber] * distanceToNextBotLocationY[botNumber]));

                }
                settingAnimationPoints = false;

                // If the thread is interrupted
                if (Thread.currentThread().isInterrupted()) {
                    // RunAnimations set to false
                    runAnimation = false;
                    runSingleBotAnimation[botNumber] = false;

                    // Return
                    return;
                }

                // If animate 1 is equal to 100
                if (animate1[botNumber] == 100) {
                    // Animate 2 add 1
                    animate2[botNumber]++;

                    // Animate 2 is equal to the chain size
                    if (animate2[botNumber] == chain.get(botNumber).size()) {

                        // Animate 2 subtract 1
                        animate2[botNumber] -= 1;

                        // Check if runSingleBotAnimation is true
                        if (runSingleBotAnimation[botNumber]) {

                            // Reset that robots position
                            resetSingleRobotPose(botNumber);

                        }

                        // Return
                        return;
                    } else {
                        // Animate 1 = 0
                        animate1[botNumber] = 0;
                    }
                } else {

                    // Up animate 1 by 1
                    animate1[botNumber]++;
                }

                // Wait distanceToNextBotLocation to have a constant speed path
                try {
                    TimeUnit.MILLISECONDS.sleep(distanceToNextBotLocation[botNumber] * 5L);
                } catch (InterruptedException e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }
    }
}