package com.owlrobotics.visualizer.util.conversion;

import com.owlrobotics.visualizer.FieldPanel;
import com.owlrobotics.visualizer.util.enums.Interpolation;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportToJava {

    public ExportToJava(FieldPanel fieldPanel) throws IOException {
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        fileChooser.setDialogTitle("Save Java File");
        fileChooser.setPreferredSize(new Dimension(900, 700));
        fileChooser.setVisible(true);
        fileChooser.setSelectedFile(new File("javaPaths.java"));
        int save = fileChooser.showSaveDialog(null);

        if (save == JFileChooser.APPROVE_OPTION) {
            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            if (!file.createNewFile()) {
                System.out.println("That File Already Exists. Please Select a Different File Name");
            } else {
                FileWriter writer = new FileWriter(file);
                String indent = "";

                writer.write("public class GeneratedPaths {");
                indent = "    ";

                writer.write("\n");

                writer.write("\n" + indent +"public static PathBuilder builder = new PathBuilder();");

                writer.write("\n");

                for (int botNumber = 0; botNumber < fieldPanel.entities.size(); botNumber++) {
                    writer.write("\n" + indent + "public static PathChain chain" + (botNumber + 1) + " = builder");
                    indent = "        ";

                    for (int pathNumber = 0; pathNumber < fieldPanel.chain.get(botNumber).size(); pathNumber++) {
                        writer.write("\n" + indent + ".addPath(");
                        indent = "            ";

                        if (fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().size() == 2) {
                            writer.write("\n" + indent + "new BezierLine(");
                        } else {
                            writer.write("\n" + indent + "new BezierCurve");
                        }
                        indent = "                ";

                        for (int pointNumber = 0; pointNumber < fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().size(); pointNumber++) {
                            writer.write("\n" + indent + "new Pose(");
                            writer.write(fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getX() + ", ");
                            writer.write(fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getY() + "");

                            if (pointNumber == fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().size() - 1) {
                                writer.write(")");
                            } else {
                                writer.write("),");
                            }
                        }

                        indent = "            ";

                        writer.write("\n" + indent + ")");
                        indent = "        ";

                        writer.write("\n" + indent + ")");

                        if (fieldPanel.chain.get(botNumber).getPath(pathNumber).getInterpolation() == Interpolation.LINEAR_HEADING_INTERPOLATION) {
                            writer.write("\n" + indent);
                            writer.write(".setLinearHeadingInterpolation(");
                            writer.write("Math.toRadians(" + Math.toDegrees(fieldPanel.chain.get(botNumber).getPath(pathNumber).startHeading) + "), ");
                            writer.write("Math.toRadians(" + Math.toDegrees(fieldPanel.chain.get(botNumber).getPath(pathNumber).endHeading) + "))");
                        } else if (fieldPanel.chain.get(botNumber).getPath(pathNumber).getInterpolation() == Interpolation.CONSTANT_HEADING_INTERPOLATION) {
                            writer.write("\n" + indent);
                            writer.write(".setConstantHeadingInterpolation(");
                            writer.write("Math.toRadians(" + fieldPanel.chain.get(botNumber).getPath(pathNumber).constantHeading + "))");
                        } else if (fieldPanel.chain.get(botNumber).getPath(pathNumber).getInterpolation() == Interpolation.TANGENTIAL_HEADING_INTERPOLATION) {
                            writer.write("\n" + indent);
                            writer.write(".setTangentHeadingInterpolation()");
                            if (fieldPanel.chain.get(botNumber).getPath(pathNumber).isReversed) {
                                writer.write("\n.setReversed(true)");
                            }
                        }
                    }
                    writer.write("\n" + indent + ".build");
                    writer.write("\n");
                    indent = "    ";
                }
                indent = "";
                writer.write("\n" + indent + "}");
                writer.close();
            }
        }
    }
}
