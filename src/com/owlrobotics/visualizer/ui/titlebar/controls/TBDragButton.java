package com.owlrobotics.visualizer.ui.titlebar.controls;

import com.owlrobotics.visualizer.ui.titlebar.TBJFrame;
import com.owlrobotics.visualizer.ui.titlebar.win.CustomDecorationParameters;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class TBDragButton extends TBControlButton implements MouseMotionListener {

    int mouseX;
    int mouseY;

    public TBDragButton(TBJFrame frame) {
        super(frame);
        setPreferredSize(new Dimension(0, CustomDecorationParameters.getTitleBarHeight()));
        addMouseMotionListener(this);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        frame.setLocation(
                e.getXOnScreen() - mouseX,
                e.getYOnScreen() - mouseY);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void draw(Graphics2D g, Point p1, Point p2) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        setBackground(frame.getTheme().getControlButtonBackground);
        mouseX = e.getX() + this.getX();
        mouseY = e.getY() + this.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setBackground(frame.getTheme().getControlButtonBackground);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setBackground(frame.getTheme().getControlButtonBackground);
    }
}
