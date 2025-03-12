package com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen;

import java.util.ArrayList;

public class BezierLine extends BezierCurve {
    private Point startPoint;
    private Point endPoint;
    private Vector endTangent;
    private double UNIT_TO_TIME;
    private double length;

    public BezierLine(Point startPoint, Point endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.length = this.approximateLength();
        this.UNIT_TO_TIME = 1.0 / this.length;
        this.endTangent = MathFunctions.normalizeVector(this.getDerivative(1.0));
        super.initializeDashboardDrawingPoints();
    }

    public Vector getEndTangent() {
        return MathFunctions.copyVector(this.endTangent);
    }

    public double approximateLength() {
        return Math.sqrt(Math.pow(this.startPoint.getX() - this.endPoint.getX(), 2.0) + Math.pow(this.startPoint.getY() - this.endPoint.getY(), 2.0));
    }

    public Point getPoint(double t) {
        t = MathFunctions.clamp(t, 0.0, 1.0);
        return new Point((this.endPoint.getX() - this.startPoint.getX()) * t + this.startPoint.getX(), (this.endPoint.getY() - this.startPoint.getY()) * t + this.startPoint.getY(), 1);
    }

    public double getCurvature(double t) {
        return 0.0;
    }

    public Vector getDerivative(double t) {
        Vector returnVector = new Vector();
        returnVector.setOrthogonalComponents(this.endPoint.getX() - this.startPoint.getX(), this.endPoint.getY() - this.startPoint.getY());
        return returnVector;
    }

    public Vector getSecondDerivative(double t) {
        return new Vector();
    }

    public Vector getApproxSecondDerivative(double t) {
        return new Vector();
    }

    public ArrayList<Point> getControlPoints() {
        ArrayList<Point> returnList = new ArrayList();
        returnList.add(this.startPoint);
        returnList.add(this.endPoint);
        return returnList;
    }

    public Point getFirstControlPoint() {
        return this.startPoint;
    }

    public Point getSecondControlPoint() {
        return this.endPoint;
    }

    public Point getSecondToLastControlPoint() {
        return this.startPoint;
    }

    public Point getLastControlPoint() {
        return this.endPoint;
    }

    public double length() {
        return this.length;
    }

    public double UNIT_TO_TIME() {
        return this.UNIT_TO_TIME;
    }

    public String pathType() {
        return "line";
    }
}