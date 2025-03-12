package com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen;

import java.util.ArrayList;

public class BezierCurve {
    private ArrayList<BezierCurveCoefficients> pointCoefficients = new ArrayList();
    private ArrayList<Point> controlPoints = new ArrayList();
    private Vector endTangent = new Vector();
    private final int APPROXIMATION_STEPS;
    private final int DASHBOARD_DRAWING_APPROXIMATION_STEPS;
    private double[][] dashboardDrawingPoints;
    private double[][] visualizerDrawingPoints;
    private double UNIT_TO_TIME;
    private double length;

    public BezierCurve() {
        this.APPROXIMATION_STEPS = 1000;
        this.DASHBOARD_DRAWING_APPROXIMATION_STEPS = 100;
    }

    public BezierCurve(ArrayList<Point> controlPoints) {
        this.APPROXIMATION_STEPS = 1000;
        this.DASHBOARD_DRAWING_APPROXIMATION_STEPS = 100;
        if (controlPoints.size() < 3) {
            try {
                throw new Exception("Too few control points");
            } catch (Exception var3) {
                Exception e = var3;
                e.printStackTrace();
            }
        }

        this.controlPoints = controlPoints;
        this.initialize();
    }

    public BezierCurve(Point... controlPoints) {
        this.APPROXIMATION_STEPS = 1000;
        this.DASHBOARD_DRAWING_APPROXIMATION_STEPS = 100;
        Point[] var2 = controlPoints;
        int var3 = controlPoints.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Point controlPoint = var2[var4];
            this.controlPoints.add(controlPoint);
        }

        if (this.controlPoints.size() < 3) {
            try {
                throw new Exception("Too few control points");
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        }

        this.initialize();
    }

    public void initialize() {
        this.generateBezierCurve();
        this.length = this.approximateLength();
        this.UNIT_TO_TIME = 1.0 / this.length;
        this.endTangent.setOrthogonalComponents(((Point)this.controlPoints.get(this.controlPoints.size() - 1)).getX() - ((Point)this.controlPoints.get(this.controlPoints.size() - 2)).getX(), ((Point)this.controlPoints.get(this.controlPoints.size() - 1)).getY() - ((Point)this.controlPoints.get(this.controlPoints.size() - 2)).getY());
        this.endTangent = MathFunctions.normalizeVector(this.endTangent);
        this.initializeDashboardDrawingPoints();
    }

    public void initializeDashboardDrawingPoints() {
        this.dashboardDrawingPoints = new double[2][101];

        for(int i = 0; i <= 100; ++i) {
            Point currentPoint = this.getPoint((double)i / 100.0);
            this.dashboardDrawingPoints[0][i] = currentPoint.getX();
            this.dashboardDrawingPoints[1][i] = currentPoint.getY();
        }

    }

    public double[][] getDashboardDrawingPoints() {
        return this.dashboardDrawingPoints;
    }

    public void generateBezierCurve() {
        int n = this.controlPoints.size() - 1;

        for(int i = 0; i <= n; ++i) {
            this.pointCoefficients.add(new BezierCurveCoefficients(n, i));
        }

    }

    public Vector getEndTangent() {
        return MathFunctions.copyVector(this.endTangent);
    }

    public double approximateLength() {
        Point previousPoint = this.getPoint(0.0);
        double approxLength = 0.0;

        for(int i = 1; i <= this.APPROXIMATION_STEPS; ++i) {
            Point currentPoint = this.getPoint((double)i / (double)this.APPROXIMATION_STEPS);
            approxLength += previousPoint.distanceFrom(currentPoint);
            previousPoint = currentPoint;
        }

        return approxLength;
    }

    public Point getPoint(double t) {
        t = MathFunctions.clamp(t, 0.0, 1.0);
        double xCoordinate = 0.0;
        double yCoordinate = 0.0;

        int i;
        for(i = 0; i < this.controlPoints.size(); ++i) {
            xCoordinate += ((BezierCurveCoefficients)this.pointCoefficients.get(i)).getValue(t) * ((Point)this.controlPoints.get(i)).getX();
        }

        for(i = 0; i < this.controlPoints.size(); ++i) {
            yCoordinate += ((BezierCurveCoefficients)this.pointCoefficients.get(i)).getValue(t) * ((Point)this.controlPoints.get(i)).getY();
        }

        return new Point(xCoordinate, yCoordinate, 1);
    }

    public double getCurvature(double t) {
        t = MathFunctions.clamp(t, 0.0, 1.0);
        Vector derivative = this.getDerivative(t);
        Vector secondDerivative = this.getSecondDerivative(t);
        return derivative.getMagnitude() == 0.0 ? 0.0 : MathFunctions.crossProduct(derivative, secondDerivative) / Math.pow(derivative.getMagnitude(), 3.0);
    }

    public Vector getDerivative(double t) {
        t = MathFunctions.clamp(t, 0.0, 1.0);
        double xCoordinate = 0.0;
        double yCoordinate = 0.0;
        Vector returnVector = new Vector();

        int i;
        for(i = 0; i < this.controlPoints.size() - 1; ++i) {
            xCoordinate += ((BezierCurveCoefficients)this.pointCoefficients.get(i)).getDerivativeValue(t) * MathFunctions.subtractPoints((Point)this.controlPoints.get(i + 1), (Point)this.controlPoints.get(i)).getX();
        }

        for(i = 0; i < this.controlPoints.size() - 1; ++i) {
            yCoordinate += ((BezierCurveCoefficients)this.pointCoefficients.get(i)).getDerivativeValue(t) * MathFunctions.subtractPoints((Point)this.controlPoints.get(i + 1), (Point)this.controlPoints.get(i)).getY();
        }

        returnVector.setOrthogonalComponents(xCoordinate, yCoordinate);
        return returnVector;
    }

    public Vector getSecondDerivative(double t) {
        t = MathFunctions.clamp(t, 0.0, 1.0);
        double xCoordinate = 0.0;
        double yCoordinate = 0.0;
        Vector returnVector = new Vector();

        int i;
        for(i = 0; i < this.controlPoints.size() - 2; ++i) {
            xCoordinate += ((BezierCurveCoefficients)this.pointCoefficients.get(i)).getSecondDerivativeValue(t) * MathFunctions.addPoints(MathFunctions.subtractPoints((Point)this.controlPoints.get(i + 2), new Point(2.0 * ((Point)this.controlPoints.get(i + 1)).getX(), 2.0 * ((Point)this.controlPoints.get(i + 1)).getY(), 1)), (Point)this.controlPoints.get(i)).getX();
        }

        for(i = 0; i < this.controlPoints.size() - 2; ++i) {
            yCoordinate += ((BezierCurveCoefficients)this.pointCoefficients.get(i)).getSecondDerivativeValue(t) * MathFunctions.addPoints(MathFunctions.subtractPoints((Point)this.controlPoints.get(i + 2), new Point(2.0 * ((Point)this.controlPoints.get(i + 1)).getX(), 2.0 * ((Point)this.controlPoints.get(i + 1)).getY(), 1)), (Point)this.controlPoints.get(i)).getY();
        }

        returnVector.setOrthogonalComponents(xCoordinate, yCoordinate);
        return returnVector;
    }

    public Vector getApproxSecondDerivative(double t) {
        double current = this.getDerivative(t).getTheta();
        double deltaCurrent = this.getDerivative(t + 1.0E-4).getTheta();
        return new Vector(1.0, deltaCurrent - current);
    }

    public ArrayList<Point> getControlPoints() {
        return this.controlPoints;
    }

    public Point getFirstControlPoint() {
        return (Point)this.controlPoints.get(0);
    }

    public Point getSecondControlPoint() {
        return (Point)this.controlPoints.get(1);
    }

    public Point getSecondToLastControlPoint() {
        return (Point)this.controlPoints.get(this.controlPoints.size() - 2);
    }

    public Point getLastControlPoint() {
        return (Point)this.controlPoints.get(this.controlPoints.size() - 1);
    }

    public double length() {
        return this.length;
    }

    public double UNIT_TO_TIME() {
        return this.UNIT_TO_TIME;
    }

    public String pathType() {
        return "curve";
    }
}

