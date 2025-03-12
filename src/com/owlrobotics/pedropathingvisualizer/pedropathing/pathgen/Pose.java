//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen;

public class Pose {
    private double x;
    private double y;
    private double heading;

    public Pose(double setX, double setY, double setHeading) {
        this.setX(setX);
        this.setY(setY);
        this.setHeading(setHeading);
    }

    public Pose(double setX, double setY) {
        this(setX, setY, 0.0);
    }

    public Pose() {
        this(0.0, 0.0, 0.0);
    }

    public void setX(double set) {
        this.x = set;
    }

    public void setY(double set) {
        this.y = set;
    }

    public void setHeading(double set) {
        this.heading = MathFunctions.normalizeAngle(set);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getHeading() {
        return this.heading;
    }

    public Vector getVector() {
        Vector returnVector = new Vector();
        returnVector.setOrthogonalComponents(this.x, this.y);
        return returnVector;
    }

    public Vector getHeadingVector() {
        return new Vector(1.0, this.heading);
    }

    public void add(Pose pose) {
        this.setX(this.x + pose.getX());
        this.setY(this.y + pose.getY());
        this.setHeading(this.heading + pose.getHeading());
    }

    public void subtract(Pose pose) {
        this.setX(this.x - pose.getX());
        this.setY(this.y - pose.getY());
        this.setHeading(this.heading - pose.getHeading());
    }

    public void scalarMultiply(double scalar) {
        this.setX(this.x * scalar);
        this.setY(this.y * scalar);
        this.setHeading(this.heading * scalar);
    }

    public void scalarDivide(double scalar) {
        this.setX(this.x / scalar);
        this.setY(this.y / scalar);
        this.setHeading(this.heading / scalar);
    }

    public void flipSigns() {
        this.setX(-this.x);
        this.setY(-this.y);
        this.setHeading(-this.heading);
    }

    public boolean roughlyEquals(Pose pose, double accuracy) {
        return MathFunctions.roughlyEquals(this.x, pose.getX(), accuracy) && MathFunctions.roughlyEquals(this.y, pose.getY(), accuracy) && MathFunctions.roughlyEquals(MathFunctions.getSmallestAngleDifference(this.heading, pose.getHeading()), 0.0, accuracy);
    }

    public boolean roughlyEquals(Pose pose) {
        return this.roughlyEquals(pose, 1.0E-4);
    }

    public Pose copy() {
        return new Pose(this.getX(), this.getY(), this.getHeading());
    }

    public String toString() {
        return "(" + this.getX() + ", " + this.getY() + ", " + Math.toDegrees(this.getHeading()) + ")";
    }
}
