package edu.colorado.phet.recordandplayback.model;

public class DataPoint<T> {
    private final double time;
    private final T state;

    public DataPoint(double time, T state) {
        this.time = time;
        this.state = state;
    }

    public double getTime() {
        return time;
    }

    public T getState() {
        return state;
    }
}
