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

    public static class State<A> {
        double dt;
        A state;

        public State(double dt, A state) {
            this.state = state;
            this.dt = dt;
        }
    }

    public static class Result<A> {
        ArrayList<State<A>> states;

        public Result(ArrayList<State<A>> states) {
            this.states = states;
        }

        public A getFinalState() {
            return states.get(states.size() - 1).state;
        }
    }

    public Result<A> stepInTimeWithHistory(A originalState, Steppable<A> steppable, double dt) {
        A state = originalState;
        double elapsed = 0.0;
        ArrayList<State<A>> states = new ArrayList<State<A>>();
        while (elapsed < dt) {
            double seedValue = states.size() > 0 ? states.get(states.size() - 1).dt : dt;//use the last obtained dt as a starting value, if possible
            double subdivisionDT = getTimestep(state, steppable, seedValue);
            System.out.println("selected subdivisionDT = " + subdivisionDT);
            if (subdivisionDT + elapsed > dt) subdivisionDT = dt - elapsed; // don't exceed max allowed dt
            state = steppable.update(state, subdivisionDT);
            states.add(new State<A>(subdivisionDT, state));
            elapsed = elapsed + subdivisionDT;
        }
        return new Result<A>(states);
    }

    public A stepInTime(A originalState, Steppable<A> steppable, double dt) {
        return stepInTimeWithHistory(originalState, steppable, dt).getFinalState();
    }

    protected double getTimestep(A state, Steppable<A> steppable, double dt) {
        //store the previously used DT and try it first, then to increase it when possible.
        if (dt < minDT) {
            System.out.println("Time step too small");
            return minDT;
        } else if (errorAcceptable(state, steppable, dt * 2))
            return dt * 2; //only increase by one factor if this exceeds the totalDT, it will be cropped later
        else if (errorAcceptable(state, steppable, dt)) return dt * 2;
        else return getTimestep(state, steppable, dt / 2);
    }

    protected boolean errorAcceptable(A state, Steppable<A> steppable, double dt) {
        A a = steppable.update(state, dt);
        A b1 = steppable.update(state, dt / 2);
        A b2 = steppable.update(b1, dt / 2);
        return steppable.distance(a, b2) < errorThreshold;
    }
}

