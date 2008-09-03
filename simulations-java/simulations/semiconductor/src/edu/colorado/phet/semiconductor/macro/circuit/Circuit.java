package edu.colorado.phet.semiconductor.macro.circuit;

import edu.colorado.phet.semiconductor.macro.circuit.battery.Battery;
import edu.colorado.phet.semiconductor.phetcommon.math.PhetVector;

/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 1:03:55 PM
 */
public class Circuit {
    CompositeLinearBranch circuit = new CompositeLinearBranch();
    PhetVector at;
    private double resistorWidth;

    public Circuit( double x, double y, double resistorWidth ) {
        this.resistorWidth = resistorWidth;
        this.at = new PhetVector( x, y );
    }

    public LinearBranch wireAt( int i ) {
        return circuit.branchAt( i );
    }

    public Wire wireTo( double x, double y ) {
        PhetVector to = new PhetVector( x, y );
        Wire branch = new Wire( at, to );
        circuit.addBranch( branch );
        at = to;
        return branch;
    }

    public Resistor resistorTo( double x, double y ) {
        PhetVector to = new PhetVector( x, y );
        Resistor branch = new Resistor( at, to, resistorWidth );
        circuit.addBranch( branch );
        at = to;
        return branch;
    }

    public Battery batteryTo( double x, double y ) {
        PhetVector to = new PhetVector( x, y );
        Battery branch = new Battery( at, to );
        circuit.addBranch( branch );
        at = to;
        return branch;
    }

    public double getLength() {
        return circuit.getLength();
    }

    public int numBranches() {
        return circuit.numBranches();
    }

    public PhetVector getPosition( double dist ) {
        return circuit.getPosition( dist );
    }

    public boolean contains( double newLoc ) {
        return newLoc >= 0 && newLoc <= getLength();
    }
}
