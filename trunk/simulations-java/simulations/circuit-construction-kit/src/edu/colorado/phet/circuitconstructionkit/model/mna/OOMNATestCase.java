package edu.colorado.phet.circuitconstructionkit.model.mna;

public class OOMNATestCase extends MNATestCase {
    public LinearCircuitSolver getSolver() {
        return new ObjectOrientedMNA();
    }
}
