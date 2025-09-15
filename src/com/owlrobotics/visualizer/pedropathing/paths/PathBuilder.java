package com.owlrobotics.visualizer.pedropathing.paths;

import com.owlrobotics.visualizer.pedropathing.geometry.BezierCurve;
import com.owlrobotics.visualizer.pedropathing.geometry.Curve;
import com.owlrobotics.visualizer.pedropathing.geometry.Pose;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the PathBuilder class. This class makes it easier to create PathChains, so you don't have
 * to individually create Path instances to create a PathChain. A PathBuilder can be accessed
 * through running the pathBuilder() method on an instance of the Follower class, or just creating
 * an instance of PathBuilder regularly.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @author Havish Sripada - 12808 RevAmped Robotics
 * @version 1.0, 3/11/2024
 */
public class PathBuilder {
    private ArrayList<Path> paths = new ArrayList<>();
    private PathChain.DecelerationType decelerationType = PathChain.DecelerationType.LAST_PATH;
    private PathConstraints constraints;
    private HeadingInterpolator headingInterpolator;

    /**
     * This adds a Path to the PathBuilder.
     *
     * @param path The Path being added.
     * @return This returns itself with the updated data.
     */
    public PathBuilder addPath(Path path) {
        path.setConstraints(constraints);
        this.paths.add(path);
        return this;
    }

    /**
     * This adds a default Path defined by a specified BezierCurve to the PathBuilder.
     *
     * @param curve This curve is turned into a Path and added.
     * @return This returns itself with the updated data.
     */
    public PathBuilder addPath(Curve curve) {
        return addPath(new Path(curve, constraints));
    }

    /**
     * This adds multiple Paths to the PathBuilder
     * @param paths Vararg of Paths being added
     * @return This returns itself with the updated data.
     */
    public PathBuilder addPaths(Path... paths){
        for (Path path: paths) {
            path.setConstraints(constraints);
            this.paths.add(path);
        }
        return this;
    }

    /**
     * This adds multiple curves wrapped in the default Path defined by the curve to the PathBuilder
     * @param curves Vararg of Curves being added
     * @return This returns itself with the updated data.
     */
    public PathBuilder addPaths(Curve... curves){
        for (Curve curve: curves) {
            this.paths.add(new Path(curve, constraints));
        }
        return this;
    }

    /**
     * Automagically generate bézier curves through each given point and add to path
     * @param prevPoint the point prior to the start point
     * @param startPoint start point of the curve chain
     * @param tension controls tangents' generation magnitude
     * @param points other points
     * @return This returns itself with the updated data.
     */
    public PathBuilder curveThrough(Pose prevPoint, Pose startPoint, double tension, Pose... points){
        //guard against points being zero length (which means the curve doesn't have an end point)
        if (points.length == 0) {
            try {
                throw new Exception("Points array must contain at least one point to curve through.");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }
        ArrayList<Pose> poses = new ArrayList<>();

        poses.add(prevPoint);
        poses.add(startPoint);

        poses.addAll(Arrays.asList(points));

        // auto calculate new end point to generate a valid tangent
        Pose diff = poses.get(poses.size() - 1).minus(poses.get(poses.size() - 2));
        poses.add(poses.get(poses.size() - 1).plus(diff));

        double scaledTension = tension / 3d;

        List<ArrayList<Pose>> controlPoints = new ArrayList<>();
        for (int i = 1; i < poses.size() - 2; i++) {
            controlPoints.add(catmullToBezier(scaledTension, poses.get(i - 1), poses.get(i), poses.get(i + 1), poses.get(i + 2)));
        }

        BezierCurve[] curves = new BezierCurve[controlPoints.size()];
        for (int i = 0; i < curves.length; i++) {
            curves[i] = new BezierCurve(controlPoints.get(i), this.constraints);
        }

        return addPaths(curves);
    }

    /**
     * Automagically generate bézier curves through each given point and add to path.
     * This method starts the first curve from the last path's end point.
     * @param tension controls tangents' generation magnitude
     * @param points points to curve through
     * @return This returns itself with the updated data.
     */

    /**
     * Converts catmull rom spline points into cubic bezier control points
     * @param scaledTension tension / 3
     * @param p0 previous pose
     * @param p1 current pose
     * @param p2 next pose
     * @param p3 next pose of the next pose
     * @return list of cubic bezier control points
     */
    private ArrayList<Pose> catmullToBezier(double scaledTension, Pose p0, Pose p1, Pose p2, Pose p3){
        ArrayList<Pose> output = new ArrayList<>();
        output.add(p1);
        output.add(p1.plus((p2.minus(p0)).times(scaledTension)));
        output.add(p2.minus((p3.minus(p1)).times(scaledTension)));
        output.add(p2);

        return output;
    }

    /**
     * This sets a linear heading interpolation on the last Path added to the PathBuilder.
     *
     * @param startHeading The start of the linear heading interpolation.
     * @param endHeading The end of the linear heading interpolation.
     *         This will be reached at the end of the Path if no end t-value is specified.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setLinearHeadingInterpolation(double startHeading, double endHeading) {
        this.paths.get(paths.size() - 1).setLinearHeadingInterpolation(startHeading, endHeading);
        return this;
    }

    /**
     * This sets a global linear heading interpolation.
     *
     * @param startHeading The start of the linear heading interpolation.
     * @param endHeading The end of the linear heading interpolation.
     *         This will be reached at the end of the Path if no end t-value is specified.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setGlobalLinearHeadingInterpolation(double startHeading, double endHeading) {
        headingInterpolator = HeadingInterpolator.linear(startHeading, endHeading);
        return this;
    }

    /**
     * This sets a linear heading interpolation on the last Path added to the PathBuilder.
     *
     * @param startHeading The start of the linear heading interpolation.
     * @param endHeading The end of the linear heading interpolation.
     *         This will be reached at the end of the Path if no end t-value is specified.
     * @param endTime The end t-value on the Path that the linear heading interpolation will end.
     *         This value goes from [0, 1] since Bezier curves are parametric functions.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setLinearHeadingInterpolation(double startHeading, double endHeading, double endTime) {
        this.paths.get(paths.size() - 1).setLinearHeadingInterpolation(startHeading, endHeading, endTime);
        return this;
    }

    /**
     * This sets a global linear heading interpolation.
     *
     * @param startHeading The start of the linear heading interpolation.
     * @param endHeading The end of the linear heading interpolation.
     *         This will be reached at the end of the Path if no end t-value is specified.
     * @param endTime The end t-value on the Path that the linear heading interpolation will end.
     *         This value goes from [0, 1] since Bezier curves are parametric functions.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setGlobalLinearHeadingInterpolation(double startHeading, double endHeading, double endTime) {
        headingInterpolator = HeadingInterpolator.linear(startHeading, endHeading, endTime);
        return this;
    }

    /**
     * This sets a linear heading interpolation on the last Path added to the PathBuilder.
     *
     * @param startHeading The start of the linear heading interpolation.
     * @param endHeading The end of the linear heading interpolation.
     *         This will be reached at the end of the Path if no end t-value is specified.
     * @param startTime The start t-value on the Path that the linear heading interpolation will start.
     * @param endTime The end t-value on the Path that the linear heading interpolation will end.
     *         This value goes from [0, 1] since Bezier curves are parametric functions.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setLinearHeadingInterpolation(double startHeading, double endHeading, double endTime, double startTime) {
        HeadingInterpolator interpolator = HeadingInterpolator.piecewise(
                new HeadingInterpolator.PiecewiseNode(0, startTime, HeadingInterpolator.constant(startHeading)),
                HeadingInterpolator.PiecewiseNode.linear(startTime, endTime, startHeading, endHeading)
        );

        this.paths.get(paths.size() - 1).setHeadingInterpolation(interpolator);
        return this;
    }

    /**
     * This sets a global heading interpolation on the last Path added to the PathBuilder.
     *
     * @param startHeading The start of the linear heading interpolation.
     * @param endHeading The end of the linear heading interpolation.
     *         This will be reached at the end of the Path if no end t-value is specified.
     * @param startTime The start t-value on the Path that the linear heading interpolation will start.
     * @param endTime The end t-value on the Path that the linear heading interpolation will end.
     *         This value goes from [0, 1] since Bezier curves are parametric functions.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setGlobalLinearHeadingInterpolation(double startHeading, double endHeading, double endTime, double startTime) {
        headingInterpolator = HeadingInterpolator.piecewise(
                new HeadingInterpolator.PiecewiseNode(0, startTime, HeadingInterpolator.constant(startHeading)),
                HeadingInterpolator.PiecewiseNode.linear(startTime, endTime, startHeading, endHeading)
        );
        return this;
    }

    /**
     * This sets a constant heading interpolation on the last Path added to the PathBuilder.
     *
     * @param setHeading The constant heading specified.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setConstantHeadingInterpolation(double setHeading) {
        this.paths.get(paths.size() - 1).setConstantHeadingInterpolation(setHeading);
        return this;
    }

    /**
     * This sets a global constant heading interpolation.
     *
     * @param setHeading The constant heading specified.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setGlobalConstantHeadingInterpolation(double setHeading) {
        headingInterpolator = HeadingInterpolator.constant(setHeading);
        return this;
    }

    /**
     * This sets a reversed heading interpolation on the last Path added to the PathBuilder.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setReversed() {
        this.paths.get(paths.size() - 1).reverseHeadingInterpolation();
        return this;
    }

    /**
     * This sets a global reversed heading interpolation.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setGlobalReversed() {
        headingInterpolator.reverse();
        return this;
    }

    /**
     * This sets the heading interpolation to tangential on the last Path added to the PathBuilder.
     * There really shouldn't be a reason to use this since the default heading interpolation is
     * tangential but it's here.
     */
    public PathBuilder setTangentHeadingInterpolation() {
        this.paths.get(paths.size() - 1).setTangentHeadingInterpolation();
        return this;
    }

    /**
     * This sets the global heading interpolation to tangential..
     * There really shouldn't be a reason to use this since the default heading interpolation is
     * tangential but it's here.
     */
    public PathBuilder setGlobalTangentHeadingInterpolation() {
        headingInterpolator = HeadingInterpolator.tangent;
        return this;
    }

    /**
     * This sets the heading interpolation to custom on the last Path added to the PathBuilder.
     * @param function A function that describes the target heading as a function of t, the parametric variable. Use a lambda expression here.
     */
    public PathBuilder setHeadingInterpolation(HeadingInterpolator function) {
        this.paths.get(paths.size() - 1).setHeadingInterpolation(function);
        return this;
    }

    /**
     * This sets the global heading interpolation to custom.
     * @param function A function that describes the target heading as a function of t, the parametric variable. Use a lambda expression here.
     */
    public PathBuilder setGlobalHeadingInterpolation(HeadingInterpolator function) {
        this.headingInterpolator = function;
        return this;
    }

    /**
     * This sets the deceleration multiplier on the last Path added to the PathBuilder.
     *
     * @param set This sets the multiplier for the goal for the deceleration of the robot.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setBrakingStrength(double set) {
        this.paths.get(paths.size() - 1).setBrakingStrength(set);
        return this;
    }

    /**
     * This sets the breaking start
     *
     * @param set This sets the multiplier
     * @return This returns itself with the updated data.
     */
    public PathBuilder setBrakingStart(double set) {
        constraints.setBrakingStart(set);
        return this;
    }

    /**
     * This sets the path end velocity constraint on the last Path added to the PathBuilder.
     *
     * @param set This sets the path end velocity constraint.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setVelocityConstraint(double set) {
        this.paths.get(paths.size() - 1).setVelocityConstraint(set);
        return this;
    }

    /**
     * This sets the path end translational constraint on the last Path added to the PathBuilder.
     *
     * @param set This sets the path end translational constraint.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setTranslationalConstraint(double set) {
        this.paths.get(paths.size() - 1).setTranslationalConstraint(set);
        return this;
    }

    /**
     * This sets the path end heading constraint on the last Path added to the PathBuilder.
     *
     * @param set This sets the path end heading constraint.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setHeadingConstraint(double set) {
        this.paths.get(paths.size() - 1).setHeadingConstraint(set);
        return this;
    }

    /**
     * This sets the path end t-value (parametric time) constraint on the last Path added to the PathBuilder.
     *
     * @param set This sets the path end t-value (parametric timee) constraint.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setTValueConstraint(double set) {
        this.paths.get(paths.size() - 1).setTValueConstraint(set);
        return this;
    }

    /**
     * This sets the path end timeout constraint on the last Path added to the PathBuilder.
     *
     * @param set This sets the path end timeout constraint.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setTimeoutConstraint(double set) {
        this.paths.get(paths.size() - 1).setTimeoutConstraint(set);
        return this;
    }

    /**
     * This is a condition for a callback to run. This class is functionally the same as a BooleanSupplier.
     * It is used to check if the callback is ready to run.
     */
    public interface CallbackCondition {
        boolean isReady();
    }

    /**
     * This builds all the Path and callback information together into a PathChain.
     * @return This returns a PathChain made of all the specified paths and callbacks.
     */
    public PathChain build() {
        PathChain returnChain = new PathChain(paths);
        returnChain.setDecelerationType(decelerationType);
        returnChain.setHeadingInterpolator(headingInterpolator);
        return returnChain;
    }

    /**
     * Sets the PathChain to decelerate based on the entire chain and not only the last path (recommended if the final path is short)
     * @return This returns itself with the updated data.
     */
    public PathBuilder setGlobalDeceleration() {
        this.decelerationType = PathChain.DecelerationType.GLOBAL;
        return this;
    }

    /**
     * Makes this decelerate based on the entire chain and not only the last path (recommended if the last path is short)
     * @param brakingStart sets the BrakingStartMultiplier to the PathConstraints. A lower BrakingStartMultiplier will make the PathChain begin decelerating later, and vice-versa.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setGlobalDeceleration(double brakingStart) {
        this.decelerationType = PathChain.DecelerationType.GLOBAL;
        constraints.setBrakingStart(brakingStart);
        return this;
    }

    /**
     * Sets no deceleration to the PathChain
     * @return This returns itself with the updated data.
     */
    public PathBuilder setNoDeceleration() {
        this.decelerationType = PathChain.DecelerationType.NONE;
        return this;
    }

    /**
     * This sets the constraints to be the default PathBuilder.
     *
     * @param constraints The constraints to set.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setConstraints(PathConstraints constraints) {
        this.constraints = constraints;
        return this;
    }

    /**
     * This sets the constraints for all of the paths.
     *
     * @param constraints The constraints to set.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setConstraintsForAll(PathConstraints constraints) {
        this.constraints = constraints;
        for (Path path : paths) {
            path.setConstraints(constraints);
        }
        return this;
    }

    /**
     * This sets the constraints for the last path.
     *
     * @param constraints The constraints to set.
     * @return This returns itself with the updated data.
     */
    public PathBuilder setConstraintsForLast(PathConstraints constraints) {
        this.constraints = constraints;
        paths.get(paths.size() - 1).setConstraints(constraints);
        return this;
    }

    private void setBrakingStartForAll(double start) {
        for (Path path : paths) path.setBrakingStart(start);
    }
}