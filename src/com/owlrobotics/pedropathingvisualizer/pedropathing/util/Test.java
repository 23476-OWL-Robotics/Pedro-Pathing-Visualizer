package com.owlrobotics.pedropathingvisualizer.pedropathing.util;

import com.owlrobotics.pedropathingvisualizer.PathVisualizer;
import com.owlrobotics.pedropathingvisualizer.pedropathing.entities.PedroPathingBotEntity;
import com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen.*;

public class Test {

    public static void main(String[] args) {
        PathVisualizer visualizer = new PathVisualizer(900, 100);

        PedroPathingBotEntity blueBot = new PedroPathingBotEntity.Builder()
                .setRobotImage(RobotImages.Pedro_BLUE)
                .setRobotSize(16, 16)
                .build();

        PedroPathingBotEntity redBot = new PedroPathingBotEntity.Builder()
                .setRobotImage(RobotImages.Pedro_RED)
                .setRobotSize(16, 16)
                .build();

        PedroPathingBotEntity test = new PedroPathingBotEntity.Builder()
                .setRobotImage(RobotImages.Pedro_CLASSIC)
                .setRobotSize(16, 16)
                .build();

        blueBot.createNewPath(new PathBuilder()
                .addPath(
                        // Line 1
                        new BezierCurve(
                                new Point(8.000, 88.000, Point.CARTESIAN),
                                new Point(24.000, 90, Point.CARTESIAN),
                                new Point(24.000, 78.000, Point.CARTESIAN),
                                new Point(40.000, 80.000, Point.CARTESIAN)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(0))
                .addPath(
                        // Line 1
                        new BezierLine(
                                new Point(40.000, 80.000, Point.CARTESIAN),
                                new Point(50.000, 90.000, Point.CARTESIAN)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(-90))
                .build());

        redBot.createNewPath(new PathBuilder()
                .addPath(
                        // Line 1
                        new BezierCurve(
                                new Point(136.000, 88.000, Point.CARTESIAN),
                                new Point(120.000, 90.000, Point.CARTESIAN),
                                new Point(120.000, 78.000, Point.CARTESIAN),
                                new Point(104.000, 80.000, Point.CARTESIAN)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(-90), Math.toRadians(180))
                .build());

        test.createNewPath(new PathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(0, 0),
                                new Point(0, 0)
                        )
                )
                .build());

        visualizer
                .setBackground(Backgrounds.IntoTheDeep_DARK)
                .setPlaneOrigin(PlaneOrigin.BOTTOM_LEFT)
                .addEntity(blueBot)
                .addEntity(redBot)
                .start();
    }
}
