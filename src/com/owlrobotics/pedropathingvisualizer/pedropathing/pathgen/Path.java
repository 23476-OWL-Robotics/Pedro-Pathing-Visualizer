package com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen;

import java.awt.*;
import java.util.ArrayList;

public class Path {
    private BezierCurve curve;
    private double startHeading;
    private double endHeading;
    private double closestPointCurvature;
    private double closestPointTValue;
    private double linearInterpolationEndTime;
    private Vector closestPointTangentVector;
    private Vector closestPointNormalVector;
    private boolean isTangentHeadingInterpolation = true;
    private boolean followTangentReversed;
    private double zeroPowerAccelerationMultiplier;
    private double pathEndVelocityConstraint;
    private double pathEndTranslationalConstraint;
    private double pathEndHeadingConstraint;
    private double pathEndTValueConstraint;
    private double pathEndTimeoutConstraint;
    private Color color;

    public Path(BezierCurve curve) {
        this.curve = curve;
    }

    public void setLinearHeadingInterpolation(double startHeading, double endHeading) {
        this.linearInterpolationEndTime = 1.0;
        this.isTangentHeadingInterpolation = false;
        this.startHeading = startHeading;
        this.endHeading = endHeading;
    }

    public void setLinearHeadingInterpolation(double startHeading, double endHeading, double endTime) {
        this.linearInterpolationEndTime = MathFunctions.clamp(endTime, 1.0E-9, 1.0);
        this.isTangentHeadingInterpolation = false;
        this.startHeading = startHeading;
        this.endHeading = endHeading;
    }

    public void setConstantHeadingInterpolation(double setHeading) {
        this.linearInterpolationEndTime = 1.0;
        this.isTangentHeadingInterpolation = false;
        this.startHeading = setHeading;
        this.endHeading = setHeading;
    }

    public Pose getClosestPoint(Pose pose, int searchStepLimit) {
        double lower = 0.0;
        double upper = 1.0;

        for(int i = 0; i < searchStepLimit; ++i) {
            if (MathFunctions.distance(pose, this.getPoint(lower + 0.25 * (upper - lower))) > MathFunctions.distance(pose, this.getPoint(lower + 0.75 * (upper - lower)))) {
                lower += (upper - lower) / 2.0;
            } else {
                upper -= (upper - lower) / 2.0;
            }
        }

        this.closestPointTValue = lower + 0.5 * (upper - lower);
        Point returnPoint = this.getPoint(this.closestPointTValue);
        this.closestPointTangentVector = this.curve.getDerivative(this.closestPointTValue);
        this.closestPointNormalVector = this.curve.getApproxSecondDerivative(this.closestPointTValue);
        this.closestPointCurvature = this.curve.getCurvature(this.closestPointTValue);
        return new Pose(returnPoint.getX(), returnPoint.getY(), this.getClosestPointHeadingGoal());
    }

    public void setReversed(boolean set) {
        this.isTangentHeadingInterpolation = true;
        this.followTangentReversed = set;
    }

    public boolean isReversed() {
        return this.followTangentReversed;
    }

    public void setTangentHeadingInterpolation() {
        this.isTangentHeadingInterpolation = true;
        this.followTangentReversed = false;
    }

    public Vector getEndTangent() {
        return this.curve.getEndTangent();
    }

    public Point getPoint(double t) {
        return this.curve.getPoint(t);
    }

    public double getClosestPointTValue() {
        return this.closestPointTValue;
    }

    public double length() {
        return this.curve.length();
    }

    public double getCurvature(double t) {
        return this.curve.getCurvature(t);
    }

    public double getClosestPointCurvature() {
        return this.closestPointCurvature;
    }

    public Vector getClosestPointNormalVector() {
        return MathFunctions.copyVector(this.closestPointNormalVector);
    }

    public Vector getClosestPointTangentVector() {
        return MathFunctions.copyVector(this.closestPointTangentVector);
    }

    public double getClosestPointHeadingGoal() {
        if (this.isTangentHeadingInterpolation) {
            return this.followTangentReversed ? MathFunctions.normalizeAngle(this.closestPointTangentVector.getTheta() + Math.PI) : this.closestPointTangentVector.getTheta();
        } else {
            return this.getHeadingGoal(this.closestPointTValue);
        }
    }

    public double getHeadingGoal(double t) {
        if (this.isTangentHeadingInterpolation) {
            return this.followTangentReversed ? MathFunctions.normalizeAngle(this.curve.getDerivative(t).getTheta() + Math.PI) : this.curve.getDerivative(t).getTheta();
        } else {
            return t > this.linearInterpolationEndTime ? MathFunctions.normalizeAngle(this.endHeading) : MathFunctions.normalizeAngle(this.startHeading + MathFunctions.getTurnDirection(this.startHeading, this.endHeading) * MathFunctions.getSmallestAngleDifference(this.endHeading, this.startHeading) * (t / this.linearInterpolationEndTime));
        }
    }

    public boolean isAtParametricEnd() {
        return this.closestPointTValue >= this.pathEndTValueConstraint;
    }

    public boolean isAtParametricStart() {
        return this.closestPointTValue <= 1.0 - this.pathEndTValueConstraint;
    }

    public ArrayList<Point> getControlPoints() {
        return this.curve.getControlPoints();
    }

    public Point getFirstControlPoint() {
        return this.curve.getFirstControlPoint();
    }

    public Point getSecondControlPoint() {
        return this.curve.getSecondControlPoint();
    }

    public Point getSecondToLastControlPoint() {
        return this.curve.getSecondToLastControlPoint();
    }

    public Point getLastControlPoint() {
        return this.curve.getLastControlPoint();
    }

    public void setZeroPowerAccelerationMultiplier(double set) {
        this.zeroPowerAccelerationMultiplier = set;
    }

    public void setPathEndVelocityConstraint(double set) {
        this.pathEndVelocityConstraint = set;
    }

    public void setPathEndTranslationalConstraint(double set) {
        this.pathEndTranslationalConstraint = set;
    }

    public void setPathEndHeadingConstraint(double set) {
        this.pathEndHeadingConstraint = set;
    }

    public void setPathEndTValueConstraint(double set) {
        this.pathEndTValueConstraint = set;
    }

    public void setPathEndTimeoutConstraint(double set) {
        this.pathEndTimeoutConstraint = set;
    }

    public double getZeroPowerAccelerationMultiplier() {
        return this.zeroPowerAccelerationMultiplier;
    }

    public double getPathEndVelocityConstraint() {
        return this.pathEndVelocityConstraint;
    }

    public double getPathEndTranslationalConstraint() {
        return this.pathEndTranslationalConstraint;
    }

    public double getPathEndHeadingConstraint() {
        return this.pathEndHeadingConstraint;
    }

    public double getPathEndTValueConstraint() {
        return this.pathEndTValueConstraint;
    }

    public double getPathEndTimeoutConstraint() {
        return this.pathEndTimeoutConstraint;
    }

    public String pathType() {
        return this.curve.pathType();
    }

    public double[][] getDashboardDrawingPoints() {
        return this.curve.getDashboardDrawingPoints();
    }
    public void refreshDashboardDrawingPoints() {
        this.curve.initializeDashboardDrawingPoints();
    }

    public void setPathColor(Color color) {
        this.color = color;
    }
    public Color getPathColor() {
        return this.color;
    }
}
