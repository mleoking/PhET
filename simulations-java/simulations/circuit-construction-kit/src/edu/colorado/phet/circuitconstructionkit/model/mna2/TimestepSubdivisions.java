package edu.colorado.phet.circuitconstructionkit.model.mna2;

import java.util.ArrayList;

public class TimestepSubdivisions<A> {
    double errorThreshold;

    public TimestepSubdivisions(double errorThreshold) {
        this.errorThreshold = errorThreshold;
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
            if (subdivisionDT + elapsed > dt) subdivisionDT = dt - elapsed; // don't exceed max allowed dt
            state = steppable.update(state, subdivisionDT);
            states.add(new State<A>(subdivisionDT, state));
            elapsed = elapsed + subdivisionDT;
        }
        return new Result<A>(states);
    }

    public A stepInTime(A originalState, Steppable<A> steppable, double dt) {
        return stepInTimeWithHistory(originalState, steppable, dt).getFinalState();//todo: why is this cast needed?
    }

    double getTimestep(A state, Steppable<A> steppable, double dt) {
        //store the previously used DT and try it first, then to increase it when possible.
        if (dt < 1E-8) {
            System.out.println("Time step too small");
            return dt;
        } else if (errorAcceptable(state, steppable, dt * 2))
            return dt * 2; //only increase by one factor if this exceeds the totalDT, it will be cropped later
        else if (errorAcceptable(state, steppable, dt)) return dt * 2;
        else return getTimestep(state, steppable, dt / 2);
    }

    boolean errorAcceptable(A state, Steppable<A> steppable, double dt) {
        if (dt < 1E-6)
            return true;
        else {
            A a = steppable.update(state, dt);
            A b1 = steppable.update(state, dt / 2);
            A b2 = steppable.update(b1, dt / 2);
            return steppable.distance(a, b2) < errorThreshold;
        }
    }

}

