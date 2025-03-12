package com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen;

public class Point {
    private double r;
    private double theta;
    private double x;
    private double y;
    public static final int POLAR = 0;
    public static final int CARTESIAN = 1;

    public Point(double rOrX, double thetaOrY, int identifier) {
        this.setCoordinates(rOrX, thetaOrY, identifier);
    }

    public Point(Pose pose) {
        this.setCoordinates(pose.getX(), pose.getY(), 1);
    }

    public Point(double setX, double setY) {
        this.setCoordinates(setX, setY, 1);
    }

    public void setCoordinates(double rOrX, double thetaOrY, int identifier) {
        double[] setOtherCoordinates;
        switch (identifier) {
            case 1:
                this.x = rOrX;
                this.y = thetaOrY;
                setOtherCoordinates = cartesianToPolar(this.x, this.y);
                this.r = setOtherCoordinates[0];
                this.theta = setOtherCoordinates[1];
                break;
            default:
                if (rOrX < 0.0) {
                    this.r = -rOrX;
                    this.theta = MathFunctions.normalizeAngle(thetaOrY + Math.PI);
                } else {
                    this.r = rOrX;
                    this.theta = MathFunctions.normalizeAngle(thetaOrY);
                }

                setOtherCoordinates = polarToCartesian(this.r, this.theta);
                this.x = setOtherCoordinates[0];
                this.y = setOtherCoordinates[1];
        }

    }

    public double distanceFrom(Point otherPoint) {
        return Math.sqrt(Math.pow(otherPoint.getX() - this.x, 2.0) + Math.pow(otherPoint.getY() - this.y, 2.0));
    }

    public static double[] polarToCartesian(double r, double theta) {
        return new double[]{r * Math.cos(theta), r * Math.sin(theta)};
    }

    public static double[] cartesianToPolar(double x, double y) {
        if (x == 0.0) {
            return y > 0.0 ? new double[]{Math.abs(y), 1.5707963267948966} : new double[]{Math.abs(y), 4.71238898038469};
        } else {
            double r = Math.sqrt(x * x + y * y);
            if (x < 0.0) {
                return new double[]{r, Math.PI + Math.atan(y / x)};
            } else {
                return y > 0.0 ? new double[]{r, Math.atan(y / x)} : new double[]{r, 6.283185307179586 + Math.atan(y / x)};
            }
        }
    }

    public double getR() {
        return this.r;
    }

    public double getTheta() {
        return this.theta;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public Point copy() {
        return new Point(this.getX(), this.getY(), 1);
    }

    public String toString() {
        return "(" + this.getX() + ", " + this.getY() + ")";
    }
}
