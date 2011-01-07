// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.mna;

import java.util.ArrayList;

/**
 * TimestepSubdivisions updates a state over an interval dt by (potentially) subdividing it into smaller regions, potentially with different lengths.
 * To select the (sub) time step for each iteration, the difference between an update of h and two updates of h/2 are performed.
 * If the error between the h vs. 2x(h/2) states is within the tolerated threshold, the time step is accepted.
 * See #2241
 *
 * @author Sam Reid
 * @param <A> the state type that is being updated
 */
public class TimestepSubdivisions<A> {
    private double errorThreshold;//threshold for determining whether 2 states are similar enough; any error less than errorThreshold will be tolerated.
    private double minDT;//lowest possible value for DT, independent of how the error scales with reduced time step

    public TimestepSubdivisions(double errorThreshold, double minDT) {
        this.errorThreshold = errorThreshold;
        this.minDT = minDT;
    }

    public static interface Steppable<A> {
        double distance(A a, A b);

        A update(A a, double dt);
    }

    public ResultSet<A> stepInTimeWithHistory(A originalState, Steppable<A> steppable, double dt) {
        A state = originalState;
        double elapsed = 0.0;
        ArrayList<ResultSet.State<A>> states = new ArrayList<ResultSet.State<A>>();
        while (elapsed < dt) {
            double seedValue = states.size() > 0 ? states.get(states.size() - 1).dt : dt;//use the last obtained dt as a starting value, if possible

            // try to increase first, in case higher dt has acceptable error
            // but don't try to double dt if it is first state
            int startScale = states.size() > 0 ? 2 : 1;
            double subdivisionDT = getTimestep(state, steppable, seedValue * startScale);
            if (subdivisionDT + elapsed > dt) subdivisionDT = dt - elapsed; // don't exceed max allowed dt
            state = steppable.update(state, subdivisionDT);
            states.add(new ResultSet.State<A>(subdivisionDT, state));
            elapsed = elapsed + subdivisionDT;
        }
        return new ResultSet<A>(states);
    }

    public A stepInTime(A originalState, Steppable<A> steppable, double dt) {
        return stepInTimeWithHistory(originalState, steppable, dt).getFinalState();
    }

    /**
     * Recursively searches for a value of dt that has acceptable error, starting with the value dt
     *
     * @param state     the initial state
     * @param steppable the update algorithm and distance metric
     * @param dt        the initial value to use for dt
     * @return the selected timestep that has acceptable error or meets the minimum allowed
     */
    protected double getTimestep(A state, Steppable<A> steppable, double dt) {
        if (dt < minDT) {
            System.out.println("Time step too small");
            return minDT;
        } else if (errorAcceptable(state, steppable, dt)) {
            return dt;
        } else {
            return getTimestep(state, steppable, dt / 2);
        }
    }

    protected boolean errorAcceptable(A state, Steppable<A> steppable, double dt) {
        A a = steppable.update(state, dt);
        A b1 = steppable.update(state, dt / 2);
        A b2 = steppable.update(b1, dt / 2);
        return steppable.distance(a, b2) < errorThreshold;
    }
}