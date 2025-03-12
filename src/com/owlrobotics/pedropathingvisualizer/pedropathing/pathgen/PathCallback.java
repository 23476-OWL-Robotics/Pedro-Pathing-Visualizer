package com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen;

import com.owlrobotics.pedropathingvisualizer.pedropathing.util.SingleRunAction;

public class PathCallback extends SingleRunAction {
    private double startCondition;
    private int type;
    private int index;
    public static final int TIME = 0;
    public static final int PARAMETRIC = 1;

    public PathCallback(double startCondition, Runnable runnable, int type, int index) {
        super(runnable);
        this.startCondition = startCondition;
        this.type = type;
        if (this.type != 0 || this.type != 1) {
            this.type = 1;
        }

        if (this.type == 0 && this.startCondition < 0.0) {
            this.startCondition = 0.0;
        }

        if (this.type == 1) {
            this.startCondition = MathFunctions.clamp(this.startCondition, 0.0, 1.0);
        }

        this.index = index;
    }

    public int getType() {
        return this.type;
    }

    public double getStartCondition() {
        return this.startCondition;
    }

    public int getIndex() {
        return this.index;
    }
}