package com.owlrobotics.pedropathingvisualizer.pedropathing.util;

import java.awt.*;

public enum PlaneOrigin {
    CENTER,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT;

    public Dimension getOrigin(int windowSize) {

        return switch (this) {
            case CENTER -> new Dimension(windowSize / 2, windowSize / 2);
            case TOP_LEFT -> new Dimension(0, 0);
            case TOP_RIGHT -> new Dimension(windowSize, 0);
            case BOTTOM_LEFT -> new Dimension(0, windowSize);
            case BOTTOM_RIGHT -> new Dimension(windowSize, windowSize);
        };
    }
}
