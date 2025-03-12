package com.owlrobotics.pedropathingvisualizer

import com.owlrobotics.pedropathingvisualizer.componentUI.CustomSliderUI
import com.owlrobotics.pedropathingvisualizer.pedropathing.entities.BotEntity
import com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen.PathChain
import com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen.Point
import com.owlrobotics.pedropathingvisualizer.pedropathing.util.XYLayout
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.geom.RoundRectangle2D
import javax.swing.*
import javax.swing.border.AbstractBorder
import javax.swing.border.EmptyBorder
import javax.swing.event.ChangeEvent
import kotlin.math.abs

class ControlPanel(
    fieldSize: Int, // ArrayList of BotEntities
    var entities: ArrayList<BotEntity>, // FieldPanel

    // Set fieldPanel and entities

    var fieldPanel: FieldPanel
) : JPanel() {
    // Component Arrays for the ControlPanel
    var controlPanels: Array<JScrollPane?>
    var entityPanels: Array<EntityPanel?>
    var entityControlPanels: Array<EntityControlPanel?>
    var entitySlidePanels: Array<EntitySlidePanel?>

    // FieldPanel

    // Set fieldPanel and entities

    // ControlPanel class constructor
    init {
        // JPanel settings
        this.preferredSize = Dimension(800, fieldSize)
        this.setLocation(fieldSize + 40, 20)
        this.layout = XYLayout()
        this.background = Color.BLACK

        // Font for the NameField
        val nameFieldFont = Font("", Font.BOLD, 22)

        // JTextField nameField creation and settings
        val nameField = JTextField()
        nameField.border = BorderFactory.createEmptyBorder()
        nameField.font = nameFieldFont
        nameField.background = Color(25, 25, 25)
        nameField.foreground = Color.WHITE
        nameField.text = "Pedro Pathing Visualizer"
        nameField.preferredSize = Dimension(300, 40)
        nameField.setLocation(20, 10)
        nameField.isEditable = false

        // JTabbedPane creation
        val pane = JTabbedPane()

        // Set the array sizes
        controlPanels = arrayOfNulls(entities.size)
        entityPanels = arrayOfNulls(entities.size)
        entityControlPanels = arrayOfNulls(entities.size)
        entitySlidePanels = arrayOfNulls(entities.size)

        // pane settings
        pane.setLocation(10, 60)
        pane.preferredSize = Dimension(780, fieldSize - 72)

        // for loop for each entity
        for (botNumber in entities.indices) {
            // Create entityControlPanels

            entityControlPanels[botNumber] = EntityControlPanel(
                entities[botNumber],
                fieldPanel.controlPointLocations[botNumber],
                fieldPanel.pixelsPerInch,
                fieldPanel
            )

            // Create controlPanels
            controlPanels[botNumber] = JScrollPane(
                entityControlPanels[botNumber],
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            )

            // controlPanels settings
            controlPanels[botNumber]!!.verticalScrollBar.unitIncrement = 10
            controlPanels[botNumber]!!.preferredSize = Dimension(775, fieldSize - 150)
            controlPanels[botNumber]!!.border = BorderFactory.createLineBorder(Color(25, 25, 25), 5)
            controlPanels[botNumber]!!.setLocation(0, 0)

            // Create entityPanels
            entityPanels[botNumber] = EntityPanel(fieldSize, controlPanels[botNumber], fieldPanel, botNumber)

            // Add a tab for each entityPanel
            pane.addTab("Entity " + (botNumber + 1), entityPanels[botNumber])
        }

        // Add nameField and run/reset buttons
        this.add(nameField)
        this.add(MainRunButton(fieldPanel))
        this.add(MainResetButton(fieldPanel))

        // Add the tabbed pane
        this.add(pane)
    }

    // Paint Component override for the ControlPanel
    public override fun paintComponent(g: Graphics) {
        // Create Graphics2D

        val g2d = g as Graphics2D

        // Have the panel paint the component g
        super.paintComponent(g)

        // Turn on antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Draw a rounded rectangle as a background
        g2d.color = Color(25, 25, 25)
        g2d.fillRoundRect(0, 0, this.width - 1, this.height - 1, 20, 20)

        // Update the control point x and y locations
        // Update the slider locations
        for (botNumber in entities.indices) {
            entityControlPanels[botNumber]!!.updateText()
            entityPanels[botNumber]!!.slidePanel.updateSliderLocation()
        }

        // Render the graphics
        repaint()
    }

    // Class EntityPanel is the main panel that holds the components of each tab
    inner class EntityPanel(fieldSize: Int, scrollPane: JScrollPane?, fieldPanel: FieldPanel, botNumber: Int) :
        JPanel() {
        // SlidePanel
        var slidePanel: EntitySlidePanel

        // EntityPanel class constructor
        init {
            // JPanel settings

            this.layout = XYLayout()
            this.setLocation(0, 0)
            this.background = Color(50, 50, 50)

            // Create slidePanel
            slidePanel = EntitySlidePanel(fieldSize, fieldPanel, botNumber)

            // Add scrollPane and slidePanel
            this.add(scrollPane)
            this.add(slidePanel)
        }
    }

    // Thr EntitySlidePanel class holds the JSlider and the Single Run Button
    inner class EntitySlidePanel(
        fieldSize: Int, // FieldPanel and Slider
        var fieldPanel: FieldPanel, // BotNumber
        var botNumber: Int
    ) : JPanel() {
        var slider: JSlider

        // isUpdating tells when the slider is being manually controlled
        var isUpdating: Boolean = false

        // EntitySlidePanel class constructor
        init {
            // Add fieldPanel and botNumber

            this.botNumber = botNumber

            // JPanel Settings
            this.preferredSize = Dimension(775, 50)
            this.setLocation(0, fieldSize - 150)
            this.background = Color(25, 25, 25)
            this.layout = XYLayout()

            // Create slider
            val slider = JSlider()

            // Slider Settings
            slider.setUI(CustomSliderUI(slider))
            slider.preferredSize = Dimension(710, 20)
            slider.setLocation(50, 15)
            slider.background = Color(25, 25, 25)
            slider.minimum = 0
            slider.maximum = (fieldPanel.chain[botNumber]!!.size() * 100)
            slider.value = 0

            // Add a changeListener to the JSlider
            slider.addChangeListener { e: ChangeEvent? ->

                // Check if the animation is running
                if (!fieldPanel.isSingleAnimationRunning(botNumber)) {
                    // Set isUpdating to true

                    isUpdating = true

                    // Set the two animation variables to the Slider value
                    fieldPanel.animate1[botNumber] =
                        ((slider.value.toDouble()) % 100).toInt()
                    if ((slider.value - slider.value.toDouble() % 100) / 100 < fieldPanel.chain[botNumber]!!.size()) {
                        fieldPanel.animate2[botNumber] =
                            (slider.value - slider.value.toDouble() % 100).toInt() / 100
                    } else if (((slider.value - slider.value.toDouble() % 100) / 100).toInt() == fieldPanel.chain[botNumber]!!.size()) {
                        fieldPanel.animate1[botNumber] = 100
                    }

                    // Set isUpdating to false
                    isUpdating = false
                }
            }

            // Set slider
            this.slider = slider

            // Add the slider and ControlButton
            this.add(slider)
            this.add(ControlButton(fieldPanel, botNumber))
        }

        // updateSliderLocation updates the sliders value(location) when run
        fun updateSliderLocation() {
            // Check if isUpdating is false

            if (!isUpdating) {
                // Set the slider value

                slider.value = fieldPanel.animate1[botNumber] + (fieldPanel.animate2[botNumber] * 100)
            }
        }
    }

    // The ControlButton class is the button that runs a single bots animation
    internal inner class ControlButton(// FieldPanel

        // Set fieldPanel and botNumber

        var fieldPanel: FieldPanel, // BotNumber
        var botNumber: Int
    ) : JButton() {
        // play boolean
        var play: Boolean = false

        // FieldPanel

        // Set fieldPanel and botNumber

        // ControlButton class constructor
        init {
            // JButton settings
            this.preferredSize = Dimension(30, 30)
            this.setLocation(10, 10)
            this.border = BorderFactory.createEmptyBorder()

            // Add an action listener to know when the button is clicked
            this.addActionListener { e: ActionEvent? ->

                // Invert the play boolean
                play = !play

                // If play is true, run that bot animation
                // If play is false, stop the bot animation
                if (play) {
                    fieldPanel.runSingleAnimation(botNumber)
                } else {
                    fieldPanel.stopSingleAnimation(botNumber)
                }
            }
        }

        // paintComponent override
        public override fun paintComponent(g: Graphics) {
            // Have the JButton paint the component g

            super.paintComponent(g)

            // Create Graphics2D g2d
            val g2d = g as Graphics2D

            // Turn on antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            // BackGround Rectangle
            g2d.color = Color(25, 25, 25)
            g2d.fillRect(0, 0, 30, 30)

            // Set color and stroke size
            g2d.color = Color.GREEN
            g2d.stroke = BasicStroke(3f)

            // Set play to false if the single animation is complete
            if (play && !fieldPanel.isSingleAnimationRunning(botNumber)) {
                play = false
            }

            // Draw a play sign or a pause sign based of what the value of play is
            if (play) {
                g2d.fillOval(7, 6, 3, 3)
                g2d.fillOval(7, 24, 3, 3)

                g2d.fillOval(18, 6, 3, 3)
                g2d.fillOval(18, 24, 3, 3)

                g2d.drawLine(8, 9, 8, 23)
                g2d.drawLine(19, 9, 19, 23)
            } else {
                g2d.fillOval(4, 4, 3, 3)
                g2d.fillOval(4, 24, 3, 3)
                g2d.fillOval(21, 14, 3, 3)

                g2d.drawLine(5, 7, 5, 23)
                g2d.drawLine(6, 5, 21, 14)
                g2d.drawLine(21, 16, 6, 25)
            }
        }
    }

    // The MainRunButton class is the pause/play button at the top right of the control panel
    internal inner class MainRunButton(// Field panel

        // Set fieldPanel

        var fieldPanel: FieldPanel
    ) : JButton() {
        // Play boolean
        var play: Boolean = false

        // Field panel

        // Set fieldPanel

        // MainRunButton class constructor
        init {
            // JButton settings
            this.preferredSize = Dimension(30, 30)
            this.setLocation(720, 12)
            this.border = BorderFactory.createEmptyBorder()

            // Add an actionListener to know when the button is clicked
            this.addActionListener { e: ActionEvent? ->

                // Invert play
                play = !play

                // Set runAnimation and initAnimation to play
                fieldPanel.runAnimation = play
                fieldPanel.initAnimation = !play

                // Run Animation
                fieldPanel.runAnimation()
            }
        }

        // paintComponent override
        public override fun paintComponent(g: Graphics) {
            // Have the JButton paint the component g

            super.paintComponent(g)

            // Create Graphics2D g2d
            val g2d = g as Graphics2D

            // Turn on antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            // Draw the background
            g2d.color = Color(25, 25, 25)
            g2d.fillRect(0, 0, 30, 30)

            // Set play to false if the animation is finished
            if (!fieldPanel.isAnimationRunning && play) {
                fieldPanel.resetRobotPose()
                play = false
            }

            // Set the color and stroke size
            g2d.color = Color.GREEN
            g2d.stroke = BasicStroke(3f)

            // Draw a play sign or a pause sign based of what the value of play is
            if (play) {
                g2d.fillOval(7, 6, 3, 3)
                g2d.fillOval(7, 24, 3, 3)

                g2d.fillOval(18, 6, 3, 3)
                g2d.fillOval(18, 24, 3, 3)

                g2d.drawLine(8, 9, 8, 23)
                g2d.drawLine(19, 9, 19, 23)
            } else {
                g2d.fillOval(4, 4, 3, 3)
                g2d.fillOval(4, 24, 3, 3)
                g2d.fillOval(21, 14, 3, 3)

                g2d.drawLine(5, 7, 5, 23)
                g2d.drawLine(6, 5, 21, 14)
                g2d.drawLine(21, 16, 6, 25)
            }
        }
    }

    // The MainResetButton class is the reset button in the top right of the control panel
    internal inner class MainResetButton(fieldPanel: FieldPanel) : JButton() {
        // MainResetButton class constructor
        init {
            // JButton settings

            this.preferredSize = Dimension(30, 34)
            this.setLocation(756, 12)
            this.border = BorderFactory.createEmptyBorder()

            // Add an action listener to know when the button is pressed
            this.addActionListener { e: ActionEvent? -> fieldPanel.resetRobotPose() }
        }

        // paintComponent override
        public override fun paintComponent(g: Graphics) {
            // Have the JButton paint the component g

            super.paintComponent(g)

            // Create Graphics2D g2d
            val g2d = g as Graphics2D

            // Turn on antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            // Draw the Background
            g2d.color = Color(25, 25, 25)
            g2d.fillRect(0, 0, 30, 34)

            // Set the color to white
            g2d.color = Color.WHITE

            // Draw the reset button
            g2d.stroke = BasicStroke(3f)
            g2d.drawOval(4, 4, 22, 22)
            g2d.color = Color(25, 25, 25)
            g2d.fillRect(0, 15, 15, 15)
            g2d.color = Color.WHITE
            g2d.fillOval(3, 13, 3, 3)

            g2d.fillOval(10, 25, 3, 3)
            g2d.fillOval(14, 22, 3, 3)
            g2d.fillOval(14, 28, 3, 3)

            g2d.drawLine(11, 26, 15, 23)
            g2d.drawLine(11, 26, 15, 29)
            g2d.drawLine(15, 23, 15, 29)
        }
    }

    // The EntityControlPanel class contains all the x and y control point values
    inner class EntityControlPanel(
        entity: BotEntity, // Point array pointLocations
        var pointLocations: Array<Point?>, // PixelsPerInch conversion
        var pixelsPerInch: Double, var panel: FieldPanel
    ) :
        JPanel() {
        // ArrayList of xLocations and yLocations
        var xLocations: ArrayList<Array<JTextField?>> = ArrayList()
        var yLocations: ArrayList<Array<JTextField?>> = ArrayList()

        // ArrayList of xLabelsArray and yLabelsArray
        var xLabelsArray: ArrayList<Array<JTextField?>> = ArrayList()
        var yLabelsArray: ArrayList<Array<JTextField?>> = ArrayList()

        // ArrayList of LineNames
        var lineNames: ArrayList<JTextField> = ArrayList()

        // PathChain and FieldPAnel

        // Set the variables created above

        var chain: PathChain = entity.chain()

        // Font for the Line Text
        var textFont: Font = Font("", Font.BOLD, 16)

        init {
            // JPanel settings
            this.background = Color.BLUE
            this.background = Color(25, 25, 25)
            this.layout = XYLayout()
            this.isFocusable = true

            // LocationNumber corrects for the end control point on one line being the start control point on another line
            var locationNumber = 0
            // TextYLocation sets the next line of text's y to the previous lines y + 10
            var textYLocation = 10

            // startLocationName is the starting location text of the bot
            val startLocationName = JTextField()

            // These arrays are the x and y numbers and labels for the starting location
            val addToX0 = arrayOfNulls<JTextField>(1)
            val addToY0 = arrayOfNulls<JTextField>(1)
            val xLabels0 = arrayOfNulls<JTextField>(1)
            val yLabels0 = arrayOfNulls<JTextField>(1)

            // startLocationName settings
            startLocationName.foreground = Color.WHITE
            startLocationName.isEditable = false
            startLocationName.setSize(100, 20)
            startLocationName.border = BorderFactory.createEmptyBorder()
            startLocationName.background = Color(25, 25, 25)
            startLocationName.font = textFont
            startLocationName.setLocation(10, textYLocation)
            startLocationName.text = "Start Point"

            // Add 30 to textYLocation
            textYLocation += 30

            // Create a new JTextField for all the arrays
            addToX0[0] = JTextField()
            addToY0[0] = JTextField()
            xLabels0[0] = JTextField()
            yLabels0[0] = JTextField()

            // Set the text fields to either be editable or non-editable
            addToX0[0]!!.isEditable = true
            addToY0[0]!!.isEditable = true
            xLabels0[0]!!.isEditable = false
            yLabels0[0]!!.isEditable = false

            // Set the x and y numbers to be focusable
            addToX0[0]!!.isFocusable = true
            addToY0[0]!!.isFocusable = true

            // Set the foreground to be white
            addToX0[0]!!.foreground = Color.WHITE
            addToY0[0]!!.foreground = Color.WHITE
            xLabels0[0]!!.foreground = Color.WHITE
            yLabels0[0]!!.foreground = Color.WHITE

            // Set the Border to CustomBorder
            addToX0[0]!!.border = BorderFactory.createCompoundBorder(
                CustomBorder(),
                EmptyBorder(0, 8, 0, 0)
            )
            addToY0[0]!!.border = BorderFactory.createCompoundBorder(
                CustomBorder(),
                EmptyBorder(0, 8, 0, 0)
            )


            // Set the labels border to be empty
            xLabels0[0]!!.border = BorderFactory.createEmptyBorder()
            yLabels0[0]!!.border = BorderFactory.createEmptyBorder()

            // Set the backgrounds
            addToX0[0]!!.background = Color.BLACK
            addToY0[0]!!.background = Color.BLACK
            xLabels0[0]!!.background = Color(25, 25, 25)
            yLabels0[0]!!.background = Color(25, 25, 25)

            // Set the locations
            addToX0[0]!!.setLocation(25, textYLocation)
            addToY0[0]!!.setLocation(165, textYLocation)
            xLabels0[0]!!.setLocation(10, textYLocation)
            yLabels0[0]!!.setLocation(150, textYLocation)
            textYLocation += 30

            // Set the preferredSizes
            addToX0[0]!!.preferredSize = Dimension(100, 20)
            addToY0[0]!!.preferredSize = Dimension(100, 20)
            xLabels0[0]!!.preferredSize = Dimension(20, 20)
            yLabels0[0]!!.preferredSize = Dimension(20, 20)

            // Set the Text
            addToX0[0]!!.text = pointLocations[0]!!.x.toString()
            addToY0[0]!!.text = (pointLocations[0]!!.y).toString()
            xLabels0[0]!!.text = "X:"
            yLabels0[0]!!.text = "Y:"

            // Add action listeners to the number text boxes to set the start location when a new number is entered
            addToX0[0]!!.addActionListener { e: ActionEvent? ->
                pointLocations[0]!!.setCoordinates(
                    addToX0[0]!!.text.toDouble(),
                    pointLocations[0]!!.y,
                    Point.CARTESIAN
                )
            }
            addToY0[0]!!.addActionListener { e: ActionEvent? ->
                pointLocations[0]!!.setCoordinates(
                    pointLocations[0]!!.x,
                    addToY0[0]!!.text.toDouble(),
                    Point.CARTESIAN
                )
            }

            // Add all the above things to the JPanel
            this.add(startLocationName)
            this.add(addToX0[0])
            this.add(addToY0[0])
            this.add(xLabels0[0])
            this.add(yLabels0[0])

            // Add the text arrays to the corresponding arrayLists
            xLocations.add(addToX0)
            yLocations.add(addToY0)
            xLabelsArray.add(xLabels0)
            yLabelsArray.add(yLabels0)

            // for loop for each bot
            for (pathNumber in 0 until chain.size()) {
                // Create new JTextField arrays

                val addToX = arrayOfNulls<JTextField>(chain.getPath(pathNumber).controlPoints.size)
                val addToY = arrayOfNulls<JTextField>(chain.getPath(pathNumber).controlPoints.size)
                val xLabels = arrayOfNulls<JTextField>(chain.getPath(pathNumber).controlPoints.size)
                val yLabels = arrayOfNulls<JTextField>(chain.getPath(pathNumber).controlPoints.size)

                // Create a new textField and add it to lineNames
                lineNames.add(JTextField())


                // JTextField lineName Settings
                lineNames[pathNumber].foreground = Color.WHITE
                lineNames[pathNumber].isEditable = false
                lineNames[pathNumber].setSize(100, 20)
                lineNames[pathNumber].border = BorderFactory.createEmptyBorder()
                lineNames[pathNumber].background = Color(25, 25, 25)
                lineNames[pathNumber].font = textFont
                lineNames[pathNumber].setLocation(10, textYLocation)
                lineNames[pathNumber].text = "Line " + (pathNumber + 1)
                textYLocation += lineNames[pathNumber].height + 10


                // Add that line name to the JPanel
                this.add(lineNames[pathNumber])

                // for loop for each control point
                for (pointNumber in 0 until chain.getPath(pathNumber).controlPoints.size - 1) {
                    // Create new JTextFields for each x and y

                    addToX[pointNumber] = JTextField()
                    addToY[pointNumber] = JTextField()
                    xLabels[pointNumber] = JTextField()
                    yLabels[pointNumber] = JTextField()

                    // Set editable to be either true or false
                    addToX[pointNumber]!!.isEditable = true
                    addToY[pointNumber]!!.isEditable = true
                    xLabels[pointNumber]!!.isEditable = false
                    yLabels[pointNumber]!!.isEditable = false

                    // Set x and y numbers to be focusable
                    addToX[pointNumber]!!.isFocusable = true
                    addToY[pointNumber]!!.isFocusable = true

                    // Set the foreground to be white
                    addToX[pointNumber]!!.foreground = Color.WHITE
                    addToY[pointNumber]!!.foreground = Color.WHITE
                    xLabels[pointNumber]!!.foreground = Color.WHITE
                    yLabels[pointNumber]!!.foreground = Color.WHITE

                    // Set the x and y number borders to a new CustomBorder
                    addToX[pointNumber]!!.border = BorderFactory.createCompoundBorder(
                        CustomBorder(),
                        EmptyBorder(0, 8, 0, 0)
                    )
                    addToY[pointNumber]!!.border = BorderFactory.createCompoundBorder(
                        CustomBorder(),
                        EmptyBorder(0, 8, 0, 0)
                    )


                    // Set the x and y labels to have an empty border
                    xLabels[pointNumber]!!.border = BorderFactory.createEmptyBorder()
                    yLabels[pointNumber]!!.border = BorderFactory.createEmptyBorder()

                    // Set the background colors
                    addToX[pointNumber]!!.background = Color.BLACK
                    addToY[pointNumber]!!.background = Color.BLACK
                    xLabels[pointNumber]!!.background = Color(25, 25, 25)
                    yLabels[pointNumber]!!.background = Color(25, 25, 25)

                    // Set the locations
                    addToX[pointNumber]!!.setLocation(25, textYLocation)
                    addToY[pointNumber]!!.setLocation(165, textYLocation)
                    xLabels[pointNumber]!!.setLocation(10, textYLocation)
                    yLabels[pointNumber]!!.setLocation(150, textYLocation)
                    textYLocation += 30

                    // Set the preferredSizes
                    addToX[pointNumber]!!.preferredSize = Dimension(100, 20)
                    addToY[pointNumber]!!.preferredSize = Dimension(100, 20)
                    xLabels[pointNumber]!!.preferredSize = Dimension(20, 20)
                    yLabels[pointNumber]!!.preferredSize = Dimension(20, 20)

                    // Set the text
                    addToX[pointNumber]!!.text = pointLocations[pointNumber + locationNumber + 1]!!.x.toString()
                    addToY[pointNumber]!!.text = (pointLocations[pointNumber + locationNumber + 1]!!.y).toString()
                    xLabels[pointNumber]!!.text = "X:"
                    yLabels[pointNumber]!!.text = "Y:"

                    // Add action listeners to the number text boxes to set the start location when a new number is entered
                    val finalPointNumber = pointNumber
                    val finalLocationNumber = locationNumber
                    addToX[pointNumber]!!.addActionListener { e: ActionEvent? ->
                        pointLocations[finalPointNumber + finalLocationNumber + 1]!!
                            .setCoordinates(
                                addToX[finalPointNumber]!!.text.toDouble(),
                                pointLocations[finalPointNumber + finalLocationNumber + 1]!!.y,
                                Point.CARTESIAN
                            )
                    }
                    addToY[pointNumber]!!.addActionListener { e: ActionEvent? ->
                        pointLocations[finalPointNumber + finalLocationNumber + 1]!!
                            .setCoordinates(
                                pointLocations[finalPointNumber + finalLocationNumber + 1]!!.x,
                                addToY[finalPointNumber]!!.text.toDouble(),
                                Point.CARTESIAN
                            )
                    }

                    // Add the textFields to the JPanel
                    this.add(addToX[pointNumber])
                    this.add(addToY[pointNumber])
                    this.add(xLabels[pointNumber])
                    this.add(yLabels[pointNumber])
                }


                // Increase locationNumber
                locationNumber += chain.getPath(pathNumber).controlPoints.size - 1

                // Add the arrays to the corresponding arrayLists
                xLocations.add(addToX)
                yLocations.add(addToY)
                xLabelsArray.add(xLabels)
                yLabelsArray.add(yLabels)
            }
            // Set the JPanel preferredSize
            this.preferredSize = Dimension(700, textYLocation + 10)
        }

        // UpdateText updates the x and y texts when called
        fun updateText() {
            // Check if doUpdate is true

            if (panel.doUpdate) {
                // Set the set of each of the text boxes

                xLocations.first()[0]!!.text = pointLocations[0]!!.x.toString()
                yLocations.first()[0]!!.text = pointLocations[0]!!.y.toString()
                var locationNumber = 0
                for (pathNumber in 0 until chain.size()) {
                    for (pointNumber in 0 until chain.getPath(pathNumber).controlPoints.size - 1) {
                        xLocations[pathNumber + 1][pointNumber]!!.text =
                            (pointLocations[pointNumber + locationNumber + 1]!!
                                .x).toString()
                        yLocations[pathNumber + 1][pointNumber]!!.text =
                            (pointLocations[pointNumber + locationNumber + 1]!!
                                .y).toString()
                    }
                    locationNumber += chain.getPath(pathNumber).controlPoints.size - 1
                }
            }
        }
    }

    // The CustomBorder class draws a rounded rectangle as a JTextField border
    class CustomBorder : AbstractBorder() {
        // paintBorder override
        override fun paintBorder(
            c: Component, g: Graphics, x: Int, y: Int,
            width: Int, height: Int
        ) {
            // Have the AbstractBorder paint the border

            super.paintBorder(c, g, x, y, width, height)


            // Create Graphics2D g2d
            val g2d = g as Graphics2D


            // Set the g2d color
            g2d.color = Color.DARK_GRAY


            // Turn on antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)


            // Draw a rounded rectangle
            val shape: Shape = RoundRectangle2D.Float(0f, 0f, (c.width - 1).toFloat(), (c.height - 1).toFloat(), 9f, 9f)
            g2d.draw(shape)
        }
    }
}
