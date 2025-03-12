package com.owlrobotics.pedropathingvisualizer

import com.owlrobotics.pedropathingvisualizer.pedropathing.entities.BotEntity
import com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen.PathChain
import com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen.Point
import com.owlrobotics.pedropathingvisualizer.pedropathing.util.XYLayout
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.util.*
import java.util.concurrent.TimeUnit
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.math.sqrt

class FieldPanel(
    fieldImg: BufferedImage, // The JFrame will be set to the frame created in com.owlrobotics.pedropathingvisualizer.PathVisualizer, it is required to get the frames location
    var frame: JFrame,
    var entities: ArrayList<BotEntity>, // Pixels Per Inch for component placement conversions
    var pixelsPerInch: Double, // Plane Origin set in com.owlrobotics.pedropathingvisualizer.PathVisualizer
    var planeOrigin: Dimension, var fieldRotation: Double, // Field Size and Rotation
    var fieldSize: Int, // Variables for managing fps
    var targetFPS: Int
) :
    JPanel() {
    // All ArrayLists needed for the visualizer
    var buttons: ArrayList<Array<JPanel?>> = ArrayList()
    var controlPointLocations: ArrayList<Array<Point?>> = ArrayList()
    var chain: ArrayList<PathChain> = ArrayList()

    // If someone is actually reading this, I apologize for this next Array list
    // It is confusing and a horrible way to get the dashboardDrawingPoints, but it's the best I could do
    var curvePoints: ArrayList<ArrayList<Array<DoubleArray>>> = ArrayList()

    // FieldImage
    // Set variables defined above
    var fieldImg: Image = fieldImg

    // Arrayed Image for the entities

    // Set the robotImage array size
    var robotImg: Array<Image?> = arrayOfNulls(entities.size)

    // Animation settings
    var runAnimation: Boolean = false

    // Set the runSingleBotAnimation array size
    var runSingleBotAnimation: BooleanArray = BooleanArray(entities.size)
    var initAnimation: Boolean = false
    var settingAnimationPoints: Boolean = false
    var doUpdate: Boolean = false

    // Animations variables
    // Animate 1 is the point on a path
    // Animate 2 defines the path

    // Set the animate arrays size
    var animate1: IntArray = IntArray(entities.size)
    var animate2: IntArray = IntArray(entities.size)

    // Arrayed integer for the botLocations and rotations

    // Set the botLocation and botRotation array size
    var botLocationX: IntArray = IntArray(entities.size)
    var botLocationY: IntArray = IntArray(entities.size)
    var botRotation: DoubleArray = DoubleArray(entities.size)

    // FieldLocations
    var fieldLocationX: Int = 0
    var fieldLocationY: Int = 0

    // Set the xOrigin and yOrigin based on planeOrigin
    // X and Y Origin are the locations that the entities will be set to
    // They are set based on the PlaneOrigin set in Path Visualizer
    var xOrigin: Int = planeOrigin.width
    var yOrigin: Int = planeOrigin.height - 10

    // Set the frame time
    // The fps limiter uses nanoTime which is why the first number is so large
    var frameTime: Int = 1000000000 / targetFPS

    // Arrayed Thread for animating
    // Each robot will get its own thread for animating

    // Set the animation threads array size
    var threadAnimator: Array<Thread?> = arrayOfNulls(entities.size)

    // com.owlrobotics.pedropathingvisualizer.FieldPanel class constructor
    init {
        // Set the Panel size based on the fieldImage size
        val size = Dimension(fieldImg.width, fieldImg.height)
        this.preferredSize = size

        // Set the layoutManager to XYLayout
        this.layout = XYLayout()

        // Set the panel location on the com.owlrobotics.pedropathingvisualizer.MainPanel
        this.setLocation(20, 20)

        // First for loop to set values to the arrayLists created above
        // There are several for loops within each other, which I know is a bad thing to do
        // I again apologize for the bad optimization
        for (botNumber in entities.indices) {
            // Add the entity chain to the local chain arrayList
            // The chain is just a list of paths

            chain.add(entities[botNumber].chain())

            // NextPath and MinusPath are used in each botNumber for loop
            // The nextPath is for the Last control point on a curve/line
            // The minusPath is needed because we have one less button for each control point
            var nextPath = 0
            var minusPath = 0

            // The number of buttons just defines how many JPanels are needed for each bot
            var numberOfButtons = 0
            for (pathNumber in 0 until chain[botNumber]!!.size()) {
                numberOfButtons += chain[botNumber]!!.getPath(pathNumber).controlPoints.size
            }

            // Add a new JPanel array of length numberOfButtons to each button arrayList
            buttons.add(arrayOfNulls(numberOfButtons))
            // Add a new Point array of length numberOfButtons to each pointLocation arrayList
            controlPointLocations.add(arrayOfNulls(numberOfButtons))

            // Set each runSingleBotAnimation boolean to false
            runSingleBotAnimation[botNumber] = false

            // The addToCurvePoints arrayList will be added to the CurvePoints arrayList after each double is set
            val addToCurvePoints = ArrayList<Array<DoubleArray>>()

            // Second for loop for each pathNumber
            for (pathNumber in 0 until chain[botNumber]!!.size()) {
                // New random Color for each Path

                val r = Random()
                val g = Random()
                val b = Random()
                chain[botNumber]!!.getPath(pathNumber).pathColor = Color(
                    r.nextInt(255),
                    g.nextInt(255),
                    b.nextInt(255)
                )

                // Set each double in the addToCurvePoints arrayList
                addToCurvePoints.add(chain[botNumber]!!.getPath(pathNumber).dashboardDrawingPoints)

                // The if statements check to see if we are setting the control point buttons on the first path
                if (pathNumber == 0) {
                    // Third for loop for each point number

                    for (pointNumber in chain[botNumber]!!.getPath(pathNumber).controlPoints.indices) {
                        // Create a new JPanel for each button

                        buttons[botNumber][pointNumber + nextPath] = JPanel()

                        // Set the background of each button
                        buttons[botNumber][pointNumber + nextPath]!!.background =
                            chain[botNumber]!!.getPath(pathNumber).pathColor

                        // Set the location of each button
                        buttons[botNumber][pointNumber + nextPath]!!.setLocation(
                            (chain[botNumber]!!.getPath(pathNumber).controlPoints[pointNumber].x * pixelsPerInch).toInt() - 5 + xOrigin,
                            -(chain[botNumber]!!.getPath(pathNumber).controlPoints[pointNumber].y * pixelsPerInch).toInt() + 5 + yOrigin
                        )

                        // Set the controlPointLocations
                        // These are used is com.owlrobotics.pedropathingvisualizer.ControlPanel
                        controlPointLocations[botNumber][pointNumber + nextPath] = Point(
                            chain[botNumber]!!.getPath(pathNumber).controlPoints[pointNumber].x,
                            (chain[botNumber]!!.getPath(pathNumber).controlPoints[pointNumber].y)
                        )

                        // Set each button to be visible
                        buttons[botNumber][pointNumber + nextPath]!!.isVisible = true

                        // Set each button to be un-focusable and not enabled
                        buttons[botNumber][pointNumber + nextPath]!!.isFocusable = false
                        buttons[botNumber][pointNumber + nextPath]!!.isEnabled = false

                        // Add a MouseMotionListener to each button
                        // This makes each button draggable
                        buttons[botNumber][pointNumber + nextPath]!!.addMouseMotionListener(
                            Listener(
                                buttons[botNumber][pointNumber + nextPath],
                                controlPointLocations[botNumber][pointNumber + nextPath]
                            )
                        )

                        // Add a MouseListener to each button
                        // This just checks if a mouse is clicked on the button
                        buttons[botNumber][pointNumber + nextPath]!!.addMouseListener(MListener())

                        // Set the preferred size of each button
                        buttons[botNumber][pointNumber + nextPath]!!.preferredSize = Dimension(10, 10)

                        // Add each button to the com.owlrobotics.pedropathingvisualizer.FieldPanel
                        this.add(buttons[botNumber][pointNumber + nextPath])
                    }
                } else {
                    // Third for loop for each point number

                    for (pointNumber in 1 until chain[botNumber]!!.getPath(pathNumber).controlPoints.size) {
                        // Create a new JPanel for each button

                        buttons[botNumber][pointNumber + nextPath - minusPath] = JPanel()

                        // Set the background of each button
                        buttons[botNumber][pointNumber + nextPath - minusPath]!!.background =
                            chain[botNumber]!!.getPath(pathNumber).pathColor

                        // Set the location of each button
                        buttons[botNumber][pointNumber + nextPath - minusPath]!!.setLocation(
                            (chain[botNumber]!!.getPath(pathNumber).controlPoints[pointNumber].x * pixelsPerInch).toInt() - 5 + xOrigin,
                            -(chain[botNumber]!!.getPath(pathNumber).controlPoints[pointNumber].y * pixelsPerInch).toInt() + 5 + yOrigin
                        )

                        // Set the controlPointLocations
                        // These are used is com.owlrobotics.pedropathingvisualizer.ControlPanel
                        controlPointLocations[botNumber][pointNumber + nextPath - minusPath] = Point(
                            (chain[botNumber]!!.getPath(pathNumber).controlPoints[pointNumber].x).toInt().toDouble(),
                            (chain[botNumber]!!.getPath(pathNumber).controlPoints[pointNumber].y).toInt().toDouble()
                        )

                        // Set each button to be visible
                        buttons[botNumber][pointNumber + nextPath - minusPath]!!.isVisible = true

                        // Set each button to be un-focusable and not enabled
                        buttons[botNumber][pointNumber + nextPath - minusPath]!!.isFocusable = false
                        buttons[botNumber][pointNumber + nextPath - minusPath]!!.isEnabled = false

                        // Add a MouseMotionListener to each button
                        // This makes each button draggable
                        buttons[botNumber][pointNumber + nextPath - minusPath]!!.addMouseMotionListener(
                            Listener(
                                buttons[botNumber][pointNumber + nextPath - minusPath],
                                controlPointLocations[botNumber][pointNumber + nextPath - minusPath]
                            )
                        )

                        // Add a MouseListener to each button
                        // This just checks if a mouse is clicked on the button
                        buttons[botNumber][pointNumber + nextPath - minusPath]!!.addMouseListener(MListener())

                        // Set the preferred size of each button
                        buttons[botNumber][pointNumber + nextPath - minusPath]!!.preferredSize =
                            Dimension(10, 10)

                        // Add each button to the com.owlrobotics.pedropathingvisualizer.FieldPanel
                        this.add(buttons[botNumber][pointNumber + nextPath - minusPath])
                    }
                }

                // Up nextPath and minusPath
                nextPath += chain[botNumber]!!.getPath(pathNumber).controlPoints.size
                minusPath++
            }

            // Add each addToCurvePoints to the curvePoints array
            curvePoints.add(addToCurvePoints)

            // Set each animate to 0
            animate1[botNumber] = 0
            animate2[botNumber] = 0

            // Set the botLocation and botRotation
            botLocationX[botNumber] =
                (curvePoints[botNumber].first()[0][0] - entities[botNumber].robotSize().width * pixelsPerInch / 2).toInt()
            botLocationY[botNumber] =
                (curvePoints[botNumber].first()[1][0] - entities[botNumber].robotSize().height * pixelsPerInch / 2).toInt()
            botRotation[botNumber] = chain[botNumber]!!.getPath(0).getHeadingGoal(0.0)

            // Get the scaled Instance of the botImage
            robotImg[botNumber] = entities[botNumber].robotImage().getScaledInstance(
                (entities[botNumber].robotSize().width * pixelsPerInch).toInt(),
                (entities[botNumber].robotSize().height * pixelsPerInch).toInt(),
                Image.SCALE_DEFAULT
            )
        }

        // Set the com.owlrobotics.pedropathingvisualizer.FieldPanel to be focusable and visible
        this.isFocusable = true
        this.isVisible = true
    }

    // This is the main loop of the Visualizer
    // Everything in com.owlrobotics.pedropathingvisualizer.FieldPanel is drawn here and the fps limiter si implemented here
    public override fun paintComponent(g: Graphics) {
        // StartTime for the fps limiter

        val startTime = System.nanoTime()

        // super.paintComponent(g) tells com.owlrobotics.pedropathingvisualizer.FieldPanel to draw g
        super.paintComponent(g)

        // Graphics2D is more advanced than Graphics, which is why it is used instead
        val g2d = g as Graphics2D

        // Turn on Antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // DrawImage function draws the fieldImage
        DrawField(g2d)

        // DrawRobot function draws the robotImage
        DrawRobot(g2d)

        // DrawControlPoints draws the control points of each chain path
        DrawControlPoints(g2d)

        // DrawBezierCurves draws the curves of each path
        DrawBezierCurves(g2d)

        // Repaint updates the rendered screen
        repaint()

        // FPS Limiter
        // ElapsedTime is the time taken to do everything above in paintComponent
        val elapsedTime = System.nanoTime() - startTime

        // Have the System wait if needed to get the wanted frame rate
        val waitTime = frameTime - elapsedTime
        if (waitTime > 0) {
            try {
                TimeUnit.NANOSECONDS.sleep(waitTime)
            } catch (e: InterruptedException) {
                println(e.localizedMessage)
            }
        }
    }

    // The DrawField function draws the fieldImage
    fun DrawField(g2d: Graphics2D) {
        // Save the current g2d transform

        val saveXForm = g2d.transform

        // Create the needed transform for the field rotation
        val toTransform = AffineTransform()

        // Rotate the toTransform to the needed field rotation
        toTransform.rotate(
            -fieldRotation,
            fieldSize.toDouble() / 2,
            fieldSize.toDouble() / 2
        )

        // Set the g2d transform to the toTransform
        g2d.transform = toTransform

        // Draw the Image
        g2d.drawImage(fieldImg, 0, 0, null)

        // Reset g2d transform
        g2d.transform = saveXForm
    }

    // The DrawRobot function draws the robotImage
    fun DrawRobot(g2d: Graphics2D) {
        // For loop for every robot

        for (botNumber in entities.indices) {
            // Get the botLocations and botLocation

            botLocationX[botNumber] = Math.round(
                (curvePoints[botNumber][animate2[botNumber]][0][animate1[botNumber]] * pixelsPerInch) -
                        entities[botNumber].robotSize().width.toDouble() * pixelsPerInch / 2
            ).toInt()
            botLocationY[botNumber] = Math.round(
                (yOrigin - (curvePoints[botNumber][animate2[botNumber]][1][animate1[botNumber]] * pixelsPerInch) + 10) -
                        entities[botNumber].robotSize().height.toDouble() * pixelsPerInch / 2
            ).toInt()
            botRotation[botNumber] =
                chain[botNumber]!!.getPath(animate2[botNumber]).getHeadingGoal(animate1[botNumber] * 0.01)

            // Save the current g2d transform
            val saveXForm = g2d.transform

            // Create the needed transform for the robot rotation
            val toTransform = AffineTransform()

            // Rotate the toTransform to the needed robot rotation
            toTransform.rotate(
                -botRotation[botNumber],
                botLocationX[botNumber] + entities[botNumber].robotSize().width * pixelsPerInch / 2,
                botLocationY[botNumber] + entities[botNumber].robotSize().height * pixelsPerInch / 2
            )

            // Set the g2d transform to the toTransform
            g2d.transform = toTransform

            // Draw the robotImage
            g2d.drawImage(
                robotImg[botNumber],
                botLocationX[botNumber],
                botLocationY[botNumber], null
            )

            // Reset the g2d transform
            g2d.transform = saveXForm
        }
    }

    // The DrawBezierCurves function draws the curves of all the paths
    fun DrawBezierCurves(g2d: Graphics2D) {
        // Set the g2d stroke to 4 pixels thick

        g2d.stroke = BasicStroke(4f)

        // First for loop for each bot
        for (botNumber in entities.indices) {
            // NextPath and MinusPath

            var nextPath = 0
            var minusPath = 0

            // Second for loop for each path
            for (pathNumber in 0 until chain[botNumber]!!.size()) {
                // Set the g2d Color to the pathColor

                g2d.color = chain[botNumber]!!.getPath(pathNumber).pathColor

                // If we are on the first path
                if (pathNumber == 0) {
                    // Third for loop for each point

                    for (pointNumber in chain[botNumber]!!.getPath(pathNumber).controlPoints.indices) {
                        // Update the chain ControlPoint locations

                        chain[botNumber]!!.getPath(pathNumber).controlPoints[pointNumber].setCoordinates(
                            controlPointLocations[botNumber][pointNumber + nextPath]!!.x,
                            controlPointLocations[botNumber][pointNumber + nextPath]!!.y,
                            Point.CARTESIAN
                        )

                        // Update the button locations
                        buttons[botNumber][pointNumber + nextPath]!!.setLocation(
                            (chain[botNumber]!!.getPath(pathNumber).controlPoints[pointNumber].x * pixelsPerInch).toInt() - 5 + xOrigin,
                            -(chain[botNumber]!!.getPath(pathNumber).controlPoints[pointNumber].y * pixelsPerInch).toInt() + 5 + yOrigin
                        )
                    }
                } else {
                    // Third for loop for each point

                    for (pointNumber in chain[botNumber]!!.getPath(pathNumber).controlPoints.indices) {
                        // Update the chain ControlPoint locations

                        chain[botNumber]!!.getPath(pathNumber).controlPoints[pointNumber].setCoordinates(
                            controlPointLocations[botNumber][pointNumber + nextPath - minusPath]!!.x,
                            controlPointLocations[botNumber][pointNumber + nextPath - minusPath]!!.y,
                            Point.CARTESIAN
                        )

                        // Update the button locations
                        buttons[botNumber][pointNumber + nextPath - minusPath]!!.setLocation(
                            (chain[botNumber]!!.getPath(pathNumber).controlPoints[pointNumber].x * pixelsPerInch).toInt() - 5 + xOrigin,
                            -(chain[botNumber]!!.getPath(pathNumber).controlPoints[pointNumber].y * pixelsPerInch).toInt() + 5 + yOrigin
                        )
                    }
                }

                // Refresh DashboardDrawingPoints
                chain[botNumber]!!.getPath(pathNumber).refreshDashboardDrawingPoints()

                // Get DashboardDrawingPoints
                curvePoints[botNumber][pathNumber] = chain[botNumber]!!.getPath(pathNumber).dashboardDrawingPoints

                // for loop to draw the Bézier curves of each path
                for (drawPoint in 0..99) {
                    // Draw a line between each drawing point

                    g2d.drawLine(
                        (Math.round((curvePoints[botNumber][pathNumber][0][drawPoint] * pixelsPerInch))).toInt(),
                        (Math.round((yOrigin - (curvePoints[botNumber][pathNumber][1][drawPoint] * pixelsPerInch) + 10))).toInt(),
                        (Math.round((curvePoints[botNumber][pathNumber][0][drawPoint + 1] * pixelsPerInch))).toInt(),
                        (Math.round((yOrigin - (curvePoints[botNumber][pathNumber][1][drawPoint + 1] * pixelsPerInch) + 10))).toInt()
                    )
                }

                // Up nextPath and MinusPath
                nextPath += chain[botNumber]!!.getPath(pathNumber).controlPoints.size
                minusPath++
            }
        }

        // Set the g2d stroke to 1 pixel thick
        g2d.stroke = BasicStroke(1f)
    }

    // The DrawControlPoints function draws the control points
    fun DrawControlPoints(g2d: Graphics2D) {
        // Set the g2d stroke to be 6 pixels thick

        g2d.stroke = BasicStroke(6f)

        // First for loop for each bot
        for (botNumber in entities.indices) {
            // NextPath and Minus Path

            var nextPath = 0
            var minusPath = 0

            // Second for loop for each path
            for (pathNumber in 0 until chain[botNumber]!!.size()) {
                // Set the g2d Color to the path color

                g2d.color = chain[botNumber]!!.getPath(pathNumber).pathColor

                // If we are on the first path
                if (pathNumber == 0) {
                    // Third for loop for each point

                    for (pointNumber in chain[botNumber]!!.getPath(pathNumber).controlPoints.indices) {
                        // Draw an oval on each button location

                        g2d.drawOval(
                            buttons[botNumber][pointNumber + nextPath]!!.x - 2,
                            buttons[botNumber][pointNumber + nextPath]!!.y - 2,
                            13,
                            13
                        )
                    }
                } else {
                    // Third for loop for each point

                    for (pointNumber in 1 until chain[botNumber]!!.getPath(pathNumber).controlPoints.size) {
                        // Draw an oval on each button location

                        g2d.drawOval(
                            buttons[botNumber][pointNumber + nextPath - minusPath]!!.x - 2,
                            buttons[botNumber][pointNumber + nextPath - minusPath]!!.y - 2,
                            13,
                            13
                        )
                    }
                }

                // Up nextPath and minusPath
                nextPath += chain[botNumber]!!.getPath(pathNumber).controlPoints.size
                minusPath++
            }
        }

        // Set the g2d stroke to 1 pixel thick
        g2d.stroke = BasicStroke(1f)
    }

    // The resetRobotPose resets all the robots positions
    fun resetRobotPose() {
        // Set runAnimation to false

        runAnimation = false

        // For loop for each bot
        for (botNumber in entities.indices) {
            // Set both animates to 0

            animate1[botNumber] = 0
            animate2[botNumber] = 0
        }
    }

    // The resetSingleRobotPose resets a single robots position
    fun resetSingleRobotPose(botNumber: Int) {
        // set runSingleBotAnimation of that bot to false

        runSingleBotAnimation[botNumber] = false

        // Set both animates of that bot to 0
        animate1[botNumber] = 0
        animate2[botNumber] = 0
    }

    // MouseMotionListener for each button
    internal inner class Listener(// JPanel and Point variables
        var panel: JPanel?, var point: Point?
    ) :
        MouseMotionListener {
        // mouseDragged only runs if the mouse is dragged on the selected component
        override fun mouseDragged(e: MouseEvent) {
            // Set fieldLocation
            fieldLocationX = frame.x + 20
            fieldLocationY = frame.y + 20

            // Set the button location
            panel?.setLocation(e.xOnScreen - fieldLocationX - 13, e.yOnScreen - fieldLocationY - 35)

            // Set the point location
            point?.setCoordinates(
                (e.xOnScreen - fieldLocationX - 13 + 5) / pixelsPerInch,
                (yOrigin - (e.yOnScreen - fieldLocationY - 40)) / pixelsPerInch,
                Point.CARTESIAN
            )
        }

        // mouseMoved is not used
        override fun mouseMoved(e: MouseEvent) {
        }
    }

    // MouseListener for each button
    internal inner class MListener : MouseListener {
        // mouseClicked is not used
        override fun mouseClicked(e: MouseEvent) {
        }

        // Set doUpdate to true for com.owlrobotics.pedropathingvisualizer.ControlPanel
        override fun mousePressed(e: MouseEvent) {
            doUpdate = true
        }

        // Set doUpdate to false for com.owlrobotics.pedropathingvisualizer.ControlPanel
        override fun mouseReleased(e: MouseEvent) {
            doUpdate = false
        }

        // mouseEntered is not used
        override fun mouseEntered(e: MouseEvent) {
        }

        // mouseExited is not used
        override fun mouseExited(e: MouseEvent) {
        }
    }

    // The runSingleAnimation function starts an animation thread for a single robot
    fun runSingleAnimation(botNumber: Int) {
        // Create a new RunAnimation
        val animation = RunAnimation(botNumber)

        // Set the runSingleAnimation of the bot to true
        runSingleBotAnimation[botNumber] = true

        // Create the threadAnimator for that bot and start it
        threadAnimator[botNumber] = Thread(animation)
        threadAnimator[botNumber]!!.start()
    }

    // Thr runAnimation function starts all the animation threads
    fun runAnimation() {
        // Check to see if the animation is already running

        if (runAnimation && !initAnimation) {
            // for loop for each bot

            for (botNumber in entities.indices) {
                // Create a new RunaAnimation
                val animation = RunAnimation(botNumber)

                // Create the threadAnimator and start it
                threadAnimator[botNumber] = Thread(animation)
                threadAnimator[botNumber]!!.start()
            }

            // Set initAnimation to true
            initAnimation = true
        }
    }

    // The stopSingleAnimation function stops an animation thread for a single robot
    fun stopSingleAnimation(botNumber: Int) {
        // Set the runSingleAnimation of the bot to false

        runSingleBotAnimation[botNumber] = false

        // Check if the thread is even running
        if (threadAnimator[botNumber]!!.isAlive) {
            // Interrupt the thread to stop it

            threadAnimator[botNumber]!!.interrupt()
        }
    }

    // The isSingleAnimationRunning boolean returns that runSingleBotAnimation boolean
    fun isSingleAnimationRunning(botNumber: Int): Boolean {
        return runSingleBotAnimation[botNumber]
    }

    val isAnimationRunning: Boolean
        // The isAnimationRunning boolean tells if the animation is running
        get() {
            // for loop for each bot

            for (i in entities.indices) {
                // Check if the threadAnimator for each bot is not null and is alive

                if (threadAnimator[i] != null && threadAnimator[i]!!.isAlive) {
                    // If so, return true
                    return true
                }
            }
            // Else return false
            return false
        }

    // Class RunAnimation for animating
    internal inner class RunAnimation(// botNumber variable

        // Set the botNumber variable

        var botNumber: Int
    ) : Thread() {
        // botNumber variable

        // Set the botNumber variable

        // On thread.start()
        override fun run() {
            // Create integer arrays for distanceToNextBotLocation

            val distanceToNextBotLocationX = IntArray(entities.size)
            val distanceToNextBotLocationY = IntArray(entities.size)
            val distanceToNextBotLocation = IntArray(entities.size)

            // While the animation is running
            while (runAnimation || runSingleBotAnimation[botNumber]) {
                // Setting animation points is true while we are getting the curvePoints

                settingAnimationPoints = true

                // check if animate 1 is less than 100
                if (animate1[botNumber] < 100) {
                    // Set distanceToNextBotLocations

                    distanceToNextBotLocationX[botNumber] =
                        botLocationX[botNumber] - Math.round(
                            curvePoints[botNumber][animate2[botNumber]][0][animate1[botNumber] + 1] * pixelsPerInch -
                                    entities[botNumber].robotSize().width.toDouble() * pixelsPerInch / 2
                        ).toInt()
                    distanceToNextBotLocationY[botNumber] =
                        botLocationY[botNumber] - Math.round(
                            yOrigin - (curvePoints[botNumber][animate2[botNumber]][1][animate1[botNumber] + 1] * pixelsPerInch) + 10 -
                                    entities[botNumber].robotSize().height.toDouble() * pixelsPerInch / 2
                        ).toInt()

                    // Set the Overall nextBotLocation variable
                    distanceToNextBotLocation[botNumber] = sqrt(
                        ((distanceToNextBotLocationX[botNumber] * distanceToNextBotLocationX[botNumber]) +
                                (distanceToNextBotLocationY[botNumber] * distanceToNextBotLocationY[botNumber])).toDouble()
                    ).toInt()
                }
                settingAnimationPoints = false

                // If the thread is interrupted
                if (currentThread().isInterrupted) {
                    // RunAnimations set to false
                    runAnimation = false
                    runSingleBotAnimation[botNumber] = false

                    // Return
                    return
                }

                // If animate 1 is equal to 100
                if (animate1[botNumber] == 100) {
                    // Animate 2 add 1
                    animate2[botNumber]++

                    // Animate 2 is equal to the chain size
                    if (animate2[botNumber] == chain[botNumber]!!.size()) {
                        // Animate 2 subtract 1

                        animate2[botNumber] -= 1

                        // Check if runSingleBotAnimation is true
                        if (runSingleBotAnimation[botNumber]) {
                            // Reset that robots position

                            resetSingleRobotPose(botNumber)
                        }

                        // Return
                        return
                    } else {
                        // Animate 1 = 0
                        animate1[botNumber] = 0
                    }
                } else {
                    // Up animate 1 by 1

                    animate1[botNumber]++
                }

                // Wait distanceToNextBotLocation to have a constant speed path
                try {
                    TimeUnit.MILLISECONDS.sleep(distanceToNextBotLocation[botNumber] * 5L)
                } catch (e: InterruptedException) {
                    println(e.localizedMessage)
                }
            }
        }
    }
}