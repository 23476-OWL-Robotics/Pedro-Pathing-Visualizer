# Pedro Pathing Visualizer
Open-Source Visualizer for First Tech Challenge

## Table of Contents

- [Installation | Android Studio](#installation--android-studio)
- [Installation | Development](#installation--development)
- [Usage](#usage)
  - [Using the Control Panel](#using-the-control-panel)
  - [Adding a Second Bot](#adding-a-second-bot)
  - [Field Rotation and Origin](#field-rotation-and-origin)
  - [Using Custom Images](#using-custom-images)
- [Issues](#issues)
  - [Bad Class File](#bad-class-file)
  - [Unable to initialize main class](#unable-to-initialize-main-class)
  - [Other Issues not Listed](#other-issues-not-listed)
- [Bugs](#bugs)
  
## Installation | Android Studio

1. Download one of the .jar files from the Latest Release.
2. Open Android Studio and Right Click the FtcRobotController module. Select `New -> Module`
   
   ![Screenshot 2025-03-13 105626](https://github.com/user-attachments/assets/98c9b41b-503e-4168-ab4e-58b3e8d3852c)

4. Select `Java or Kotlin Library` from the list of module options.
  
   Set the Library Name to `PedroPathingVisualizer` and the Package Name to `com.example.visualizer`

   ![Screenshot 2025-03-13 110208](https://github.com/user-attachments/assets/1781216e-039a-4ff6-9bba-615ca6636552)

5. Click `Finish`
6. From the Main Menu at the top of Android Studio, Click `File -> Project Structure`
   
    ![Screenshot 2025-03-13 110536](https://github.com/user-attachments/assets/7f13d5d0-97f0-4d61-8d60-b1ffd441a893)

7. Under the `Dependencies` tab on the far left, select the `PedroPathingVisualizer` Module from the list of modules.
   
    ![Screenshot 2025-03-13 110830](https://github.com/user-attachments/assets/68c393d9-d58d-4f56-9006-2c17d2a3ed15)

8. Click the `+` Button under the `Declared Dependencies` Text and click `JAR/ARR Dependency`
   
   ![Screenshot 2025-03-13 111045](https://github.com/user-attachments/assets/9bf6ef14-82ec-45ac-8a06-b0003f720385)

9. Provide the Path to the .jar file you downloaded. The easiest way to do this is to Right Click the file in File Manager and click `Copy as Path`

   If your on Windows, is will copy the path with quotation marks around it so make sure to delete those.

10. Click `Ok` to add the file as a Dependency then Click `Apply` then `Ok` on the Project Structure screen.

11. Navigate to the `MyClass` file inside of the PedroPathingVisualizer Modual and past the following sample code into it.

    ![Screenshot 2025-03-13 111941](https://github.com/user-attachments/assets/3cb1ff62-f4ae-410a-8c99-2907928b50e7)

    ```java
    package com.example.visualizer;

    import com.owlrobotics.pedropathingvisualizer.PathVisualizer;
    import com.owlrobotics.pedropathingvisualizer.pedropathing.entities.PedroPathingBotEntity;
    import com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen.BezierLine;
    import com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen.PathBuilder;
    import com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen.Point;
    import com.owlrobotics.pedropathingvisualizer.pedropathing.util.Backgrounds;
    import com.owlrobotics.pedropathingvisualizer.pedropathing.util.PlaneOrigin;
    import com.owlrobotics.pedropathingvisualizer.pedropathing.util.RobotImages;

    public class MyClass {
    
        public static void main(String[] args) {
            PathVisualizer visualizer = new PathVisualizer(900, 60);
        
            PedroPathingBotEntity myBot = new PedroPathingBotEntity.Builder()
                    .setRobotImage(RobotImages.Pedro_CLASSIC)
                    .setRobotSize(16, 16)
                    .build();
        
            myBot.createNewPath(new PathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Point(8, 88, Point.CARTESIAN),
                                    new Point(48, 88, Point.CARTESIAN)
                            )
                    )
                    .setTangentHeadingInterpolation()
                    .build());
        
            visualizer
                    .setBackground(Backgrounds.IntoTheDeep_DARK)
                    .setPlaneOrigin(PlaneOrigin.BOTTOM_LEFT)
                    .addEntity(myBot)
                    .start();
        }
    }
    ```

12. Create a Run Configuration for the Visualizer
    1. First, click on the drop down menu on the top bar of Android Studio, where it says "TeamCode" with a little Android logo next to it.
    2. Click `Edit Configurations`
    3. Click on the "+" symbol in the top left of the window, and when it prompts you, select "Application".
    4. Change the name to your liking (ex. VisualizerTest)
    5. Where it says "module not specified", click to open the dropdown, then select your JRE.
    6. Where it says "cp <no module>" click it to open the dropdown, and then select FtcRobotController.PedroPathingVisualizer.main
    7. Where it says "Main Class", click the little "file" icon to the right of the text and then select the name of the main class for your PedroPathingVisualizer module.
    8. From here, in the bottom right of the window, press "Apply" then "Ok".


You can now click the Green run button with your Configuration selected and PedroPathingVisualizer should run!

If it brings up any errors, please look at the [Issues](#issues) section.

## Installation | Development

1. Download and Install [IntelliJ Idea Community Edition](https://www.jetbrains.com/idea/download) (You may need to scroll down to find Community Edition)

2. Download and Install [Java 21](https://www.oracle.com/java/technologies/downloads/#java21) or later.

3. Download and Extract the Zip file from either the `Java` or `Kotlin` branch, whichever language you prefer to code in.

   ![Screenshot 2025-03-13 114318](https://github.com/user-attachments/assets/7d91bd32-7b3f-478e-a58f-83dd9abdc2c5)

4. Open that folder with IntelliJ Idea. All of the vidualizer code is in `src/com/owlrobotics/pedropathingvisualizer`

5. Create an Artifact to download your code as a .jar library
   1. Go to `File -> Project Structure`
   2. Go to the `Artifacts` tab and click the `+` button to create a new artifact.
   3. Select `JAR -> From modules with dependenties`
   4. Leave the main class blank and click `Ok`
   5. Click `Apply` then `Ok` in the Project Structure window.

6. Build that Artifact by going to `Build -> Build Artifacts`, selecting your Artifact, then clicking `Build` or `Rebuild`

## Usage
### Using the Control Panel

The control panel isn't very complicated and is similer to the Web Based visualizer. The tabs at the top under the name each corrispond to an entity(bot) and allow you to swich between them.

The slider and slider play button at thr bottom of each tab control only that robot and no others. The slider allows you to control where that robot is on its path and the play/pause buttons allows you to start/stop that robots animation.

The play/pause button at the top of the Control Panel control all of the added robots animations and will play/pause all of them if pressed. The reset button simply sets all the robots to the start of there respective paths.

### Adding a Second Bot

It's really easy to add more bots to the visualizer. There technically isn't a limit but keep in mind that every bot added required its own CPU Thread to animate.

1. Create a new PedroPathingBotEntity and set the Image and Size:

   ```java
   PedroPathingBotEntity myBot2 = new PedroPathingBotEntity.Builder()
                .setRobotImage(RobotImages.Pedro_BLUE)
                .setRobotSize(16, 16)
                .build();
   ```

2. Create a new path for that entity to follow:

   ```java
   myBot2.createNewPath(new PathBuilder()
                // Robot Path
                .build());
   ```

3. Add that botEntity to PathVisualizer using the `.addEntity();` function.

### Field Rotation and Origin

You can set the Field rotation by using the `.setFieldRotation()` function. It's best to put it directly after `.setBackground()` 
The rotation angles are 90, 180, and 270. Use the FieldRotation enum inside the `.setFieldRotation()` paranthasis. The import is 

`import com.owlrobotics.pedropathingvisualizer.pedropathing.util.FieldRotation;`


You can set the Field origin by using the `.setFieldOrigin()` function. There is an origin enum for each corner of the field as well as the center.

Import: `import com.owlrobotics.pedropathingvisualizer.pedropathing.util.PlaneOrigin;`

### Using Custom Images

You can use a custom image for both the Robots and the Field, to set an image to a robot use `java.awt.Image` and `java.io.file`

```java
File file = new File("pathtofile");
Image cutomBotImage = ImageIO.read(file);
```

You will notice that ImageIO.read() gives an IO Exeption Error. To fix this simply add `throws IOExeption` to the end of `public static void main(String[] args)`


Setting the field image is basically the same thing but with `java.awt.image.BufferedImage` instad

```java
File file = new File("pathtofile");
BufferedImage customFiledImage = ImageIO.read(file);
```

## Issues
### Bad Class File

If you get the following error:

![Screenshot 2025-03-13 182135](https://github.com/user-attachments/assets/ec8d404c-7602-44fe-9eb6-0d16c505374e)

It means that Gradle JDK does not match the Visualizer JDK. To fix the isuue:

1. Download and Install [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)

2. Under the `File` Tab in the Main Menu, Click `Settings`

   ![Screenshot 2025-03-13 182617](https://github.com/user-attachments/assets/dcc6e3fb-17bb-40bf-9c5d-6ac920bf383a)

3. In the Left Hand Menu, navigate to `Build, Exicution, Deployment -> Build Tools -> Gradle`

   ![Screenshot 2025-03-13 182822](https://github.com/user-attachments/assets/a58d27e8-b9e4-446b-9504-3e16566e06c9)

4. From the Gradle JDK Dropdown Menu, Select the `jdk-21` option.

   ![Screenshot 2025-03-13 183009](https://github.com/user-attachments/assets/41cc60d0-30dc-42f7-ba4c-a939e201f64e)

5. Click `Apply` then `Ok` and run a Gradle Sync

The Gradle JDK should now be set to 21 and PedroPathingVisualizer should run.

### Unable to initialize main class

If you get the following error :

![Screenshot 2025-03-13 183454](https://github.com/user-attachments/assets/aa10b7d1-c41f-4584-8209-5b26ac0aed89)

It means that the Configuration Module JDK does not match the PedroPathingVisualizer JDK. To fix the issue:

1. Enter the Configuration Editor by clicking on your Visualizer Configuration, clicking on the the dots on the right, and clicking `Edit`

   ![Screenshot 2025-03-13 183717](https://github.com/user-attachments/assets/33f3107d-9f81-4d74-8de3-29ad37f01ae1)

2. Under the `Build and Run` Section of the configuration, set the First drop down to Java 21

   ![Screenshot 2025-03-13 183956](https://github.com/user-attachments/assets/e659417b-7a1e-4efe-8f3a-4254bd49d325)

   If there is no Java 21 option, please follow the steps in [Bad Class File](#bad-class-file) and return here when finished.

4. Click `Apply` then `Ok`

The Configuration JDK should now be set to the Gradle JDK and PedroPathingVisualizer should run.

### Other Issues not Listed

If you get a different error message than the ones shown or get a gradle sync error after adding the .jar file, please feel free to open an issue at the Github Repo

## Bugs

If you find any bugs when the Visualizer is running, open an issue with the `bug` label at the Github Repo and I will try and fix it in the next release.







