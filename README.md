# Pedro Pathing Visualizer
Open-Source Visualizer for First Tech Challenge

## Table of Contents

- [Installation | Android Studio](#installation--android-studio)
- [Installation | Development](#installation--development)
- [Usage](#usage)
  - [Adding a Second Bot](#adding-a-second-bot)
  - [Field Rotation and Origin](#field-rotation-and-origin)
  - [Using Custom Images](#using-custom-images)
- [Issues](#issues)
  
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
                    .setPlaneOrigin(PlaneOrigin.BOTTOM_LEFT)
                    .setBackground(Backgrounds.IntoTheDeep_DARK)
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
### Adding a Second Bot

### Field Rotation and Origin

### Using Custom Images

## Issues
