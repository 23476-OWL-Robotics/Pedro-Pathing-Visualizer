package com.owlrobotics.visualizer.util.conversion;

import com.owlrobotics.visualizer.ControlPanel;
import com.owlrobotics.visualizer.FieldPanel;
import com.owlrobotics.visualizer.pedropathing.entities.BotEntity;
import com.owlrobotics.visualizer.pedropathing.entities.PedroPathingBotEntity;
import com.owlrobotics.visualizer.pedropathing.geometry.BezierCurve;
import com.owlrobotics.visualizer.pedropathing.geometry.BezierLine;
import com.owlrobotics.visualizer.pedropathing.geometry.Pose;
import com.owlrobotics.visualizer.pedropathing.paths.Path;
import com.owlrobotics.visualizer.pedropathing.paths.PathChain;
import com.owlrobotics.visualizer.util.enums.PlaneOrigin;
import com.owlrobotics.visualizer.util.enums.RobotImages;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImportText {

    public ImportText(FieldPanel fieldPanel, ControlPanel controlPanel) throws IOException {
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        fileChooser.setDialogTitle("Open Text File");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        fileChooser.setFileFilter(filter);
        fileChooser.setPreferredSize(new Dimension(900, 700));
        fileChooser.setVisible(true);
        int save = fileChooser.showOpenDialog(null);

        if (save == JFileChooser.APPROVE_OPTION) {
            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String checkFile;

            checkFile = reader.readLine();
            if (!Objects.equals(checkFile, "generated-paths")) {
                System.out.println("This is Not a Valid Path File");
            } else {
                ArrayList<BotEntity> entities = new ArrayList<>();

                switch (reader.readLine()) {
                    case "origin:BOTTOM_LEFT" -> fieldPanel.setOrigin(PlaneOrigin.BOTTOM_LEFT);
                    case "origin:BOTTOM_RIGHT" -> fieldPanel.setOrigin(PlaneOrigin.BOTTOM_RIGHT);
                    case "origin:TOP_LEFT" -> fieldPanel.setOrigin(PlaneOrigin.TOP_LEFT);
                    case "origin:TOP_RIGHT" -> fieldPanel.setOrigin(PlaneOrigin.TOP_RIGHT);
                    case "origin:CENTER" -> fieldPanel.setOrigin(PlaneOrigin.CENTER);
                }
                switch (reader.readLine()) {
                    case "rotation:0.0" -> fieldPanel.fieldRotation = Math.toRadians(0);
                    case "rotation:90.0" -> fieldPanel.fieldRotation = Math.toRadians(90);
                    case "rotation:180.0" -> fieldPanel.fieldRotation = Math.toRadians(180);
                    case "rotation:270.0" -> fieldPanel.fieldRotation = Math.toRadians(270);
                }
                boolean readingFile = true;
                while (readingFile) {
                    String entityOrEnd = reader.readLine();

                    if (Objects.equals(entityOrEnd, "endFile")) {
                        readingFile = false;
                        System.out.println("Import Complete");
                    } else {

                        int robotWidth;
                        int robotHeight;
                        RobotImages robotImage = RobotImages.Pedro_CLASSIC;
                        PathChain chain = new PathChain();

                        reader.skip(6);
                        robotWidth = Integer.parseInt(readNumbersUntil(reader, 44));

                        reader.skip(1);
                        robotHeight = Integer.parseInt(readNumbersUntil(reader, 41));

                        reader.skip(1);

                        switch (reader.readLine()) {
                            case " image:Pedro_BLUE" -> robotImage = RobotImages.Pedro_BLUE;
                            case " image:Pedro_RED" -> robotImage = RobotImages.Pedro_RED;
                            case " image:Pedro_CLASSIC" -> robotImage = RobotImages.Pedro_CLASSIC;
                        }

                        boolean readingPaths = true;
                        while (readingPaths) {
                            String nextLine = reader.readLine();

                            switch (nextLine) {
                                case " Curve" -> readCurve(reader, chain);
                                case " Line" -> readLine(reader, chain);
                                case " endEntity" -> readingPaths = false;
                            }
                        }

                        PedroPathingBotEntity entity = new PedroPathingBotEntity.Builder()
                                .setRobotSize(robotWidth, robotHeight)
                                .setRobotImage(robotImage)
                                .build();

                        entity.createNewPath(chain);

                        entities.add(entity);
                    }
                }

                fieldPanel.entities = entities;
                fieldPanel.initAll();
                controlPanel.initAll();
            }
            reader.close();
        }
    }

    public String charsToString(char[] chars) {
        StringBuilder value = new StringBuilder();
        for (char aChar : chars) {
            value.append(aChar);
        }
        return value.toString();
    }

    public void readCurve(BufferedReader reader, PathChain chain) throws IOException {
        List<Pose> controlPoints = new ArrayList<>();

        boolean readingPoints = true;
        while (readingPoints) {
            reader.skip(2);
            if (reader.read() == 101) {
                readingPoints = false;
            } else {

                double xPoint;
                double yPoint;

                xPoint = Double.parseDouble(readNumbersUntil(reader, 44));

                reader.skip(1);

                yPoint = Double.parseDouble(readNumbersUntil(reader, 41));

                // Beginning of Next Line
                reader.skip(1);

                controlPoints.add(new Pose(xPoint, yPoint));
            }
        }
        reader.readLine();

        Path path = new Path(new BezierCurve(controlPoints));

        switch (reader.readLine()) {
            case "  LINEAR_HEADING_INTERPOLATION" -> {
                reader.skip(4);

                double startHeading;
                double endHeading;

                startHeading = Double.parseDouble(readNumbersUntil(reader, 44));

                reader.skip(1);

                endHeading = Double.parseDouble(readNumbersUntil(reader, 41));

                reader.skip(1);

                path.setLinearHeadingInterpolation(Math.toRadians(startHeading), Math.toRadians(endHeading));
            }
            case "  CONSTANT_HEADING_INTERPOLATION" -> {
                reader.skip(4);

                double constantHeading;

                constantHeading = Double.parseDouble(readNumbersUntil(reader, 41));

                reader.skip(1);

                path.setConstantHeadingInterpolation(Math.toRadians(constantHeading));
            }
            case "  TANGENTIAL_HEADING_INTERPOLATION" -> {
                if (Objects.equals(reader.readLine(), "   FORWARD")) {
                    path.setTangentHeadingInterpolation();
                } else {
                    path.reverseHeadingInterpolation();
                }
            }
        }

        reader.skip(19);

        int red, green, blue;

        red = Integer.parseInt(readNumbersUntil(reader, 44));

        reader.skip(2);
        green = Integer.parseInt(readNumbersUntil(reader, 44));

        reader.skip(2);
        blue = Integer.parseInt(readNumbersUntil(reader, 93));

        reader.skip(1);
        reader.readLine();

        Color pathColor = new Color(red, green, blue);
        path.setPathColor(pathColor);

        chain.addPath(path);
    }
    public void readLine(BufferedReader reader, PathChain chain) throws IOException {
        Pose startPose;
        Pose endPose;

        double startPoseX, startPoseY;
        double endPoseX, endPoseY;

        reader.skip(3);
        startPoseX = Double.parseDouble(readNumbersUntil(reader, 44));
        reader.skip(1);
        startPoseY = Double.parseDouble(readNumbersUntil(reader, 41));
        reader.skip(1);

        reader.skip(3);
        endPoseX = Double.parseDouble(readNumbersUntil(reader, 44));
        reader.skip(1);
        endPoseY = Double.parseDouble(readNumbersUntil(reader, 41));
        reader.skip(1);

        reader.readLine();

        startPose = new Pose(startPoseX, startPoseY);
        endPose = new Pose(endPoseX, endPoseY);

        Path path = new Path(new BezierLine(startPose, endPose));

        switch (reader.readLine()) {
            case "  LINEAR_HEADING_INTERPOLATION" -> {
                reader.skip(4);

                double startHeading;
                double endHeading;

                startHeading = Double.parseDouble(readNumbersUntil(reader, 44));

                reader.skip(1);

                endHeading = Double.parseDouble(readNumbersUntil(reader, 41));

                reader.skip(1);

                path.setLinearHeadingInterpolation(Math.toRadians(startHeading), Math.toRadians(endHeading));
            }
            case "  CONSTANT_HEADING_INTERPOLATION" -> {
                reader.skip(4);

                double constantHeading;

                constantHeading = Double.parseDouble(readNumbersUntil(reader, 41));

                reader.skip(1);

                path.setConstantHeadingInterpolation(Math.toRadians(constantHeading));
            }
            case "  TANGENTIAL_HEADING_INTERPOLATION" -> {
                if (Objects.equals(reader.readLine(), "   FORWARD")) {
                    path.setTangentHeadingInterpolation();
                } else {
                    path.reverseHeadingInterpolation();
                }
            }
        }

        reader.skip(19);

        int red, green, blue;

        red = Integer.parseInt(readNumbersUntil(reader, 44));

        reader.skip(2);
        green = Integer.parseInt(readNumbersUntil(reader, 44));

        reader.skip(2);
        blue = Integer.parseInt(readNumbersUntil(reader, 93));

        reader.skip(1);
        reader.readLine();

        Color pathColor = new Color(red, green, blue);
        path.setPathColor(pathColor);

        chain.addPath(path);
    }

    public String readNumbersUntil(BufferedReader reader, int stopAt) throws IOException {
        StringBuilder result = new StringBuilder();
        int chars;
        while ((chars = reader.read()) != stopAt) {
            result.append((char) chars);
        }

        return result.toString();
    }
}
