package com.owlrobotics.visualizer.util.conversion;

import com.owlrobotics.visualizer.FieldPanel;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportToText {

    public ExportToText(FieldPanel fieldPanel) throws IOException {
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        fileChooser.setDialogTitle("Save Text File");
        fileChooser.setPreferredSize(new Dimension(900, 700));
        fileChooser.setVisible(true);
        fileChooser.setSelectedFile(new File("textPaths.txt"));
        int save = fileChooser.showSaveDialog(null);

        if (save == JFileChooser.APPROVE_OPTION) {
            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            if (!file.createNewFile()) {
                System.out.println("That File Already Exists. Please Select a Different File Name");
            } else {
                FileWriter writer = new FileWriter(file);

                writer.write("generated-paths");

                writer.write("\norigin:" + fieldPanel.origin);
                writer.write("\nrotation:" + Math.toDegrees(fieldPanel.fieldRotation));

                for(int botNumber = 0; botNumber < fieldPanel.entities.size(); botNumber++) {
                    writer.write("\nnewEntity");
                    writer.write("\n size(" + fieldPanel.entities.get(botNumber).robotSize().width + ", " + fieldPanel.entities.get(botNumber).robotSize().height + ")");
                    writer.write("\n image:" + fieldPanel.entities.get(botNumber).imageType());

                    for (int pathNumber = 0; pathNumber < fieldPanel.chain.get(botNumber).size(); pathNumber++) {
                        if (fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().size() == 2) {
                            writer.write("\n Line");
                        } else {
                            writer.write("\n Curve");
                        }

                        for (int pointNumber = 0; pointNumber < fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().size(); pointNumber++) {
                            writer.write("\n  (" + fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getX());
                            writer.write(", " + fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getY() + ")");
                        }
                        writer.write("\n  endPoints");

                        writer.write("\n  " + fieldPanel.chain.get(botNumber).getPath(pathNumber).getInterpolation());
                        switch (fieldPanel.chain.get(botNumber).getPath(pathNumber).getInterpolation()) {
                            case LINEAR_HEADING_INTERPOLATION -> writer.write(
                                    "\n   (" + Math.toDegrees(fieldPanel.chain.get(botNumber).getPath(pathNumber).startHeading) +
                                            ", " + Math.toDegrees(fieldPanel.chain.get(botNumber).getPath(pathNumber).endHeading) + ")");
                            case CONSTANT_HEADING_INTERPOLATION -> writer.write(
                                    "\n   (" + Math.toDegrees(fieldPanel.chain.get(botNumber).getPath(pathNumber).constantHeading) + ")");
                            case TANGENTIAL_HEADING_INTERPOLATION -> {
                                if (fieldPanel.chain.get(botNumber).getPath(pathNumber).isReversed) {
                                    writer.write("\n   REVERSED");
                                } else {
                                    writer.write("\n   FORWARD");
                                }
                            }
                        }
                        writer.write("\n  " + fieldPanel.chain.get(botNumber).getPath(pathNumber).getPathColor());
                        writer.write("\n  endPath");
                    }

                    writer.write("\n endEntity");
                }
                writer.write("\nendFile");
                writer.close();
            }
        }
    }
}
