package com.owlrobotics.visualizer.util.conversion;

import com.owlrobotics.visualizer.FieldPanel;
import com.owlrobotics.visualizer.ui.componentUI.CustomScrollBarUI;
import com.owlrobotics.visualizer.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class ConvertToText extends JFrame {

    public ConvertToText(FieldPanel fieldPanel) {
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

        textArea.append("generated-paths");

        textArea.append("\norigin:" + fieldPanel.origin);
        textArea.append("\nrotation:" + Math.toDegrees(fieldPanel.fieldRotation));

        for(int botNumber = 0; botNumber < fieldPanel.entities.size(); botNumber++) {
            textArea.append("\nnewEntity");
            textArea.append("\n size(" + fieldPanel.entities.get(botNumber).robotSize().width + ", " + fieldPanel.entities.get(botNumber).robotSize().height + ")");
            textArea.append("\n image:" + fieldPanel.entities.get(botNumber).imageType());

            for (int pathNumber = 0; pathNumber < fieldPanel.chain.get(botNumber).size(); pathNumber++) {
                if (fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().size() == 2) {
                    textArea.append("\n Line");
                } else {
                    textArea.append("\n Curve");
                }

                for (int pointNumber = 0; pointNumber < fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().size(); pointNumber++) {
                    textArea.append("\n  (" + fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getX());
                    textArea.append(", " + fieldPanel.chain.get(botNumber).getPath(pathNumber).getControlPoints().get(pointNumber).getY() + ")");
                }
                textArea.append("\n  endPoints");

                textArea.append("\n  " + fieldPanel.chain.get(botNumber).getPath(pathNumber).getInterpolation());
                switch (fieldPanel.chain.get(botNumber).getPath(pathNumber).getInterpolation()) {
                    case LINEAR_HEADING_INTERPOLATION -> textArea.append(
                            "\n   (" + Math.toDegrees(fieldPanel.chain.get(botNumber).getPath(pathNumber).startHeading) +
                                    ", " + Math.toDegrees(fieldPanel.chain.get(botNumber).getPath(pathNumber).endHeading) + ")");
                    case CONSTANT_HEADING_INTERPOLATION -> textArea.append(
                            "\n   (" + Math.toDegrees(fieldPanel.chain.get(botNumber).getPath(pathNumber).constantHeading) + ")");
                    case TANGENTIAL_HEADING_INTERPOLATION -> {
                        if (fieldPanel.chain.get(botNumber).getPath(pathNumber).isReversed) {
                            textArea.append("\n   REVERSED");
                        } else {
                            textArea.append("\n   FORWARD");
                        }
                    }
                }
                textArea.append("\n  " + fieldPanel.chain.get(botNumber).getPath(pathNumber).getPathColor());
                textArea.append("\n  endPath");
            }

            textArea.append("\n endEntity");
        }
        textArea.append("\nendFile");

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
