package com.owlrobotics.pedropathingvisualizer.pedropathing.pathgen;

import java.util.ArrayList;

public class PathChain {
    private ArrayList<Path> pathChain = new ArrayList();
    private ArrayList<PathCallback> callbacks = new ArrayList();

    public PathChain(Path... paths) {
        Path[] var2 = paths;
        int var3 = paths.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Path path = var2[var4];
            this.pathChain.add(path);
        }

    }

    public PathChain(ArrayList<Path> paths) {
        this.pathChain = paths;
    }

    public Path getPath(int index) {
        return (Path)this.pathChain.get(index);
    }

    public int size() {
        return this.pathChain.size();
    }

    public void setCallbacks(PathCallback... callbacks) {
        PathCallback[] var2 = callbacks;
        int var3 = callbacks.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            PathCallback callback = var2[var4];
            this.callbacks.add(callback);
        }

    }

    public void setCallbacks(ArrayList<PathCallback> callbacks) {
        this.callbacks = callbacks;
    }

    public ArrayList<PathCallback> getCallbacks() {
        return this.callbacks;
    }
}
