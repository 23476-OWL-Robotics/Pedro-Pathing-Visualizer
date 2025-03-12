package com.owlrobotics.pedropathingvisualizer.pedropathing.util;

public class SingleRunAction {
    private boolean hasBeenRun;
    private Runnable runnable;

    public SingleRunAction(Runnable runnable) {
        this.runnable = runnable;
    }

    public boolean hasBeenRun() {
        return this.hasBeenRun;
    }

    public boolean run() {
        if (!this.hasBeenRun) {
            this.hasBeenRun = true;
            this.runnable.run();
            return true;
        } else {
            return false;
        }
    }

    public void reset() {
        this.hasBeenRun = false;
    }
}
