// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.mna;

public class FastMNATestCase extends MNATestCase {
    public LinearCircuitSolver getSolver() {
        return new FastMNA();
    }
}