// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.mna;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * History of results from an mna interval update.
 *
 * @author Sam Reid
 */
public class ResultSet<A> implements Iterable<ResultSet.State<A>> {

    private final ArrayList<State<A>> states;

    public ResultSet(ArrayList<State<A>> states) {
        this.states = states;
    }

    public A getFinalState() {
        return states.get(states.size() - 1).state;
    }

    public double getTotalTime() {
        double sum = 0.0;
        for (State<A> state : states) sum += state.dt;
        return sum;
    }

    public Iterator<State<A>> iterator() {
        return states.iterator();
    }

    public static class State<A> {
        double dt;
        A state;

        public State(double dt, A state) {
            this.state = state;
            this.dt = dt;
        }
    }
}
