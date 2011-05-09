// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.analysis;

import edu.colorado.phet.circuitconstructionkit.model.Circuit;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 12, 2006
 * Time: 9:06:26 AM
 */
public abstract class CircuitSolver {
    private ArrayList<CircuitSolutionListener> listeners = new ArrayList<CircuitSolutionListener>();

    public final void apply(Circuit circuit) {
        apply(circuit, 1.0);
    }

    public abstract void apply(Circuit circuit, double dt);

    public void addSolutionListener(CircuitSolutionListener ksl) {
        listeners.add(ksl);
    }

    public void removeSolutionListener(CircuitSolutionListener circuitSolutionListener) {
        listeners.remove(circuitSolutionListener);
    }

    protected void fireCircuitSolved() {
        for (CircuitSolutionListener listener : listeners)
            listener.circuitSolverFinished();
    }
}
