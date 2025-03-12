package com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen;

public class BezierCurveCoefficients {
    private double coefficient;
    private double derivativeCoefficient;
    private double secondDerivativeCoefficient;
    private int n;
    private int i;

    public BezierCurveCoefficients(int n, int i) {
        this.n = n;
        this.i = i;
        this.coefficient = MathFunctions.nCr(n, i);
        this.derivativeCoefficient = MathFunctions.nCr(n - 1, i);
        this.secondDerivativeCoefficient = MathFunctions.nCr(n - 2, i);
    }

    public double getValue(double t) {
        return this.coefficient * Math.pow(1.0 - t, (double)(this.n - this.i)) * Math.pow(t, (double)this.i);
    }

    public double getDerivativeValue(double t) {
        return (double)this.n * this.derivativeCoefficient * Math.pow(t, (double)this.i) * Math.pow(1.0 - t, (double)(this.n - this.i - 1));
    }

    public double getSecondDerivativeValue(double t) {
        return (double)(this.n * (this.n - 1)) * this.secondDerivativeCoefficient * Math.pow(t, (double)this.i) * Math.pow(1.0 - t, (double)(this.n - this.i - 2));
    }
}
