package edu.colorado.phet.circuitconstructionkit.model.analysis;

import java.util.ArrayList;

import edu.colorado.phet.circuitconstructionkit.model.Circuit;

/**
 * User: Sam Reid
 * Date: Jun 12, 2006
 * Time: 9:06:26 AM
 */
public abstract class CircuitSolver {
    private ArrayList listeners = new ArrayList();

    public final void apply( Circuit circuit ) {
        apply( circuit, 1.0 );
    }

    public abstract void apply( Circuit circuit, double dt );

    public void addSolutionListener( CircuitSolutionListener ksl ) {
        listeners.add( ksl );
    }

    public void removeSolutionListener( CircuitSolutionListener circuitSolutionListener ) {
        listeners.remove( circuitSolutionListener );
    }

    protected void fireCircuitSolved() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            CircuitSolutionListener circuitSolutionListener = (CircuitSolutionListener) listeners.get( i );
            circuitSolutionListener.circuitSolverFinished();
        }
    }
}
