//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen;

public class Vector {
    private double magnitude;
    private double theta;
    private double xComponent;
    private double yComponent;

    public Vector() {
        this.setComponents(0.0, 0.0);
    }

    public Vector(double magnitude, double theta) {
        this.setComponents(magnitude, theta);
    }

    public void setComponents(double magnitude, double theta) {
        if (magnitude < 0.0) {
            this.magnitude = -magnitude;
            this.theta = MathFunctions.normalizeAngle(theta + Math.PI);
        } else {
            this.magnitude = magnitude;
            this.theta = MathFunctions.normalizeAngle(theta);
        }

        double[] orthogonalComponents = Point.polarToCartesian(magnitude, theta);
        this.xComponent = orthogonalComponents[0];
        this.yComponent = orthogonalComponents[1];
    }

    public void setMagnitude(double magnitude) {
        this.setComponents(magnitude, this.theta);
    }

    public void setTheta(double theta) {
        this.setComponents(this.magnitude, theta);
    }

    public void rotateVector(double theta2) {
        this.setTheta(this.theta + theta2);
    }

    public void setOrthogonalComponents(double xComponent, double yComponent) {
        this.xComponent = xComponent;
        this.yComponent = yComponent;
        double[] polarComponents = Point.cartesianToPolar(xComponent, yComponent);
        this.magnitude = polarComponents[0];
        this.theta = polarComponents[1];
    }

    public double getMagnitude() {
        return this.magnitude;
    }

    public double getTheta() {
        return this.theta;
    }

    public double getXComponent() {
        return this.xComponent;
    }

    public double getYComponent() {
        return this.yComponent;
    }
}
