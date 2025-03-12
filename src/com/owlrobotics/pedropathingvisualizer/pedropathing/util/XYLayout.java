package com.owlrobotics.pedropathingvisualizer.pedropathing.util;

import java.awt.*;

public class XYLayout implements LayoutManager {

    public XYLayout() {
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {

    }

    @Override
    public void removeLayoutComponent(Component comp) {

    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return null;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return null;
    }

    @Override
    public void layoutContainer(Container parent) {
        int nComps = parent.getComponentCount();

        for (int i = 0; i < nComps; i++) {
            Component c = parent.getComponent(i);

            c.setSize(c.getPreferredSize());
        }
    }
}
