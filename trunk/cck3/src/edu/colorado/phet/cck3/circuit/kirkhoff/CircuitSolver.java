package edu.colorado.phet.cck3.circuit.kirkhoff;

import edu.colorado.phet.cck3.circuit.Circuit;

/**
 * User: Sam Reid
 * Date: Jun 12, 2006
 * Time: 9:06:26 AM
 * Copyright (c) Jun 12, 2006 by Sam Reid
 */
public interface CircuitSolver {
    void apply( Circuit circuit );

    void addSolutionListener( CircuitSolutionListener circuitSolutionListener );

    void removeSolutionListener( CircuitSolutionListener circuitSolutionListener );
}
