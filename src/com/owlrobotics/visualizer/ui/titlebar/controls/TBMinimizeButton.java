package com.owlrobotics.visualizer.ui.titlebar.controls;

import com.owlrobotics.visualizer.ui.titlebar.TBJFrame;
import com.owlrobotics.visualizer.ui.titlebar.win.CustomDecorationParameters;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class TBMinimizeButton extends TBControlButton {
    
    public TBMinimizeButton(TBJFrame frame) {
        super(frame);
        setPreferredSize(new Dimension(50, CustomDecorationParameters.getTitleBarHeight()));
    }

    @Override
    public void draw(Graphics2D g, Point p1, Point p2) {
        g.setColor(frame.getTheme().getTextColor);
        p1 = new Point(getWidth() / 2 - 5,getHeight() / 2);
        p2 = new Point(getWidth() / 2 + 5,getHeight() / 2);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        frame.setExtendedState(JFrame.ICONIFIED);
    }
}
