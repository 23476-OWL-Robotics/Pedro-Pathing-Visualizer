package com.owlrobotics.visualizer.ui.titlebar.menu;

import com.owlrobotics.visualizer.ui.titlebar.TBJFrame;
import com.owlrobotics.visualizer.ui.titlebar.win.CustomDecorationParameters;

import javax.swing.*;
import java.awt.*;

public class TBEditMenu extends TBControlMenu {

    public TBEditMenu(TBJFrame frame) {
        super(frame);
        setBorder(BorderFactory.createEmptyBorder(5,2,5,2));
        setBackground(frame.getTheme().getTitleBarColorBackground);
        setForeground(frame.getTheme().getTextColor);
        setFont(new Font ("SansSerif", Font.PLAIN, 14));

        setText("Edit");
    }

    @Override
    public void draw(Graphics2D g, Point p1, Point p2) {
        g.setColor(frame.getTheme().getTextColor);

        g.setStroke(new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setFont(new Font ("SansSerif", Font.PLAIN, 14));
        g.drawString(getText(), 5, (CustomDecorationParameters.getTitleBarHeight() / 2) + 7);
    }
}
