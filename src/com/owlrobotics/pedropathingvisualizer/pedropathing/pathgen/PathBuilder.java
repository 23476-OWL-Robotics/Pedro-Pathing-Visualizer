package com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen;

import java.util.ArrayList;

public class PathBuilder {
    private ArrayList<Path> paths = new ArrayList();
    private ArrayList<PathCallback> callbacks = new ArrayList();

    public PathBuilder() {
    }

    public PathBuilder addPath(Path path) {
        this.paths.add(path);
        return this;
    }

    public PathBuilder addPath(BezierCurve curve) {
        this.paths.add(new Path(curve));
        return this;
    }

    public PathBuilder addBezierCurve(Point... controlPoints) {
        return this.addPath(new BezierCurve(controlPoints));
    }

    public PathBuilder addBezierCurve(ArrayList<Point> controlPoints) {
        return this.addPath(new BezierCurve(controlPoints));
    }

    public PathBuilder addBezierLine(Point startPoint, Point endPoint) {
        return this.addPath((BezierCurve)(new BezierLine(startPoint, endPoint)));
    }

    public PathBuilder setLinearHeadingInterpolation(double startHeading, double endHeading) {
        ((Path)this.paths.get(this.paths.size() - 1)).setLinearHeadingInterpolation(startHeading, endHeading);
        return this;
    }

    public PathBuilder setLinearHeadingInterpolation(double startHeading, double endHeading, double endTime) {
        ((Path)this.paths.get(this.paths.size() - 1)).setLinearHeadingInterpolation(startHeading, endHeading, endTime);
        return this;
    }

    public PathBuilder setConstantHeadingInterpolation(double setHeading) {
        ((Path)this.paths.get(this.paths.size() - 1)).setConstantHeadingInterpolation(setHeading);
        return this;
    }

    public PathBuilder setReversed(boolean set) {
        ((Path)this.paths.get(this.paths.size() - 1)).setReversed(set);
        return this;
    }

    public PathBuilder setTangentHeadingInterpolation() {
        ((Path)this.paths.get(this.paths.size() - 1)).setTangentHeadingInterpolation();
        return this;
    }

    public PathBuilder setZeroPowerAccelerationMultiplier(double set) {
        ((Path)this.paths.get(this.paths.size() - 1)).setZeroPowerAccelerationMultiplier(set);
        return this;
    }

    public PathBuilder setPathEndVelocityConstraint(double set) {
        ((Path)this.paths.get(this.paths.size() - 1)).setPathEndVelocityConstraint(set);
        return this;
    }

    public PathBuilder setPathEndTranslationalConstraint(double set) {
        ((Path)this.paths.get(this.paths.size() - 1)).setPathEndTranslationalConstraint(set);
        return this;
    }

    public PathBuilder setPathEndHeadingConstraint(double set) {
        ((Path)this.paths.get(this.paths.size() - 1)).setPathEndHeadingConstraint(set);
        return this;
    }

    public PathBuilder setPathEndTValueConstraint(double set) {
        ((Path)this.paths.get(this.paths.size() - 1)).setPathEndTValueConstraint(set);
        return this;
    }

    public PathBuilder setPathEndTimeoutConstraint(double set) {
        ((Path)this.paths.get(this.paths.size() - 1)).setPathEndTimeoutConstraint(set);
        return this;
    }

    public PathBuilder addTemporalCallback(double time, Runnable runnable) {
        this.callbacks.add(new PathCallback(time, runnable, 0, this.paths.size() - 1));
        return this;
    }

    public PathBuilder addParametricCallback(double t, Runnable runnable) {
        this.callbacks.add(new PathCallback(t, runnable, 1, this.paths.size() - 1));
        return this;
    }

    public PathChain build() {
        PathChain returnChain = new PathChain(this.paths);
        returnChain.setCallbacks(this.callbacks);
        return returnChain;
    }
}
