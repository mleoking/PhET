package edu.colorado.phet.recordandplayback.model;

/**
 * The DataPoint is the basic data structure in recording, it keeps track of a state T (which should be immutable)
 * and pairs it with a time at which the state occurred.
 *
 * @author Sam Reid
 * @param <T> the type of the state (should be immutable), possibly a memento pattern for recording a model state.
 */
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

    public String toString() {
        return "time = " + time + ", state = " + state;
    }
}
