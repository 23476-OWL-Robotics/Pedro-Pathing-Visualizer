package com.owlrobotics.pedropathingvisualizer.pedropathing.util;

public enum FieldRotation {
    ROTATION_90,
    ROTATION_180,
    ROTATION_270;

    public double getRotation() {
        return switch (this) {
            case ROTATION_90 -> Math.toRadians(90);
            case ROTATION_180 -> Math.toRadians(180);
            case ROTATION_270 -> Math.toRadians(270);
        };
    }
}
