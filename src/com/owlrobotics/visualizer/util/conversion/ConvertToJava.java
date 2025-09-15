package com.owlrobotics.visualizer.util.conversion;

import com.owlrobotics.visualizer.FieldPanel;
import com.owlrobotics.visualizer.ui.componentUI.CustomScrollBarUI;
import com.owlrobotics.visualizer.ui.theme.Theme;
import com.owlrobotics.visualizer.util.enums.Interpolation;

import javax.swing.*;
import java.awt.*;

public class ConvertToJava extends JFrame {

    public ConvertToJava(FieldPanel fieldPanel) {
        this.setVisible(true);
        this.setResizable(false);

        JTextArea textArea = new JTextArea();
        textArea.setBackground(Theme.uneditableTextBackgroundColor);
        textArea.setForeground(Theme.textColor);
        textArea.setCaretColor(Theme.textColor);
        textArea.setEditable(false);
        textArea.setFocusable(true);

        Font font = new Font ("SansSerif", Font.PLAIN, 14);
        textArea.setFont(font);

        String indent = "";

        textArea.append("public class GeneratedPaths {");
        indent = "    ";

        textArea.append("\n");

        textArea.append("\n" + indent +"public static PathBuilder builder = new PathBuilder();");

        textArea.append("\n");

        for (int botNumber = 0; botNumber < fieldPanel.entities.size(); botNumber++) {
            textArea.append("\n" + indent + "public static PathChain chain" + (botNumber + 1) + " = builder");
            indent = "        ";

            for (int pathNumber = 0; pathNumber < fieldPanel.chain.get(botNumber).size(); pathNumber++) {
                textArea.append("\n" + indent + ".addPath(");
                indent = "            ";

                if (fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().size() == 2) {
                    textArea.append("\n" + indent + "new BezierLine(");
                } else {
                    textArea.append("\n" + indent + "new BezierCurve");
                }
                indent = "                ";

                for (int pointNumber = 0; pointNumber < fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().size(); pointNumber++) {
                    textArea.append("\n" + indent + "new Pose(");
                    textArea.append(fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getX() + ", ");
                    textArea.append(fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getY() + "");

                    if (pointNumber == fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().size() - 1) {
                        textArea.append(")");
                    } else {
                        textArea.append("),");
                    }
                }

                indent = "            ";

                textArea.append("\n" + indent + ")");
                indent = "        ";

                textArea.append("\n" + indent + ")");

                if (fieldPanel.chain.get(botNumber).getPath(pathNumber).getInterpolation() == Interpolation.LINEAR_HEADING_INTERPOLATION) {
                    textArea.append("\n" + indent);
                    textArea.append(".setLinearHeadingInterpolation(");
                    textArea.append("Math.toRadians(" + Math.toDegrees(fieldPanel.chain.get(botNumber).getPath(pathNumber).startHeading) + "), ");
                    textArea.append("Math.toRadians(" + Math.toDegrees(fieldPanel.chain.get(botNumber).getPath(pathNumber).endHeading) + "))");
                } else if (fieldPanel.chain.get(botNumber).getPath(pathNumber).getInterpolation() == Interpolation.CONSTANT_HEADING_INTERPOLATION) {
                    textArea.append("\n" + indent);
                    textArea.append(".setConstantHeadingInterpolation(");
                    textArea.append("Math.toRadians(" + fieldPanel.chain.get(botNumber).getPath(pathNumber).constantHeading + "))");
                } else if (fieldPanel.chain.get(botNumber).getPath(pathNumber).getInterpolation() == Interpolation.TANGENTIAL_HEADING_INTERPOLATION) {
                    textArea.append("\n" + indent);
                    textArea.append(".setTangentHeadingInterpolation()");
                    if (fieldPanel.chain.get(botNumber).getPath(pathNumber).isReversed) {
                        textArea.append("\n.setReversed(true)");
                    }
                }
            }
            textArea.append("\n" + indent + ".build();");
            textArea.append("\n");
            indent = "    ";
        }
        indent = "";
        textArea.append("\n" + indent + "}");

        textArea.setCaretPosition(0);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(900, 700));
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.uneditableTextBackgroundColor, 10, true));
        scrollPane.setBackground(Theme.uneditableTextBackgroundColor);

        this.add(scrollPane);
        this.pack();
    }
}
