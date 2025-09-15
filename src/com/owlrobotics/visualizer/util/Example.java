package com.owlrobotics.visualizer.util;

import com.owlrobotics.visualizer.PathVisualizer;
import com.owlrobotics.visualizer.pedropathing.entities.PedroPathingBotEntity;
import com.owlrobotics.visualizer.pedropathing.paths.*;
import com.owlrobotics.visualizer.pedropathing.geometry.*;
import com.owlrobotics.visualizer.ui.theme.DarkTheme;
import com.owlrobotics.visualizer.util.enums.Backgrounds;
import com.owlrobotics.visualizer.util.enums.PlaneOrigin;
import com.owlrobotics.visualizer.util.enums.RobotImages;

public class Example {

    public static void main(String[] args) {
        PathVisualizer visualizer = new PathVisualizer(900, 144);

        PedroPathingBotEntity test = new PedroPathingBotEntity.Builder()
                .setRobotImage(RobotImages.Pedro_CLASSIC)
                .setRobotSize(16, 16)
                .build();

        test.createNewPath(new PathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(56, 8),
                                new Pose(56, 36)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
                .build());

        visualizer
                .setBackground(Backgrounds.Decode_DARK)
                .setPlaneOrigin(PlaneOrigin.BOTTOM_LEFT)
                .setTheme(new DarkTheme())
                .addEntity(test)
                .start();
    }
}
