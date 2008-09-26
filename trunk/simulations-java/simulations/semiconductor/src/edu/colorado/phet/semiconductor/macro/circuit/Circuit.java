package edu.colorado.phet.semiconductor.macro.circuit;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.semiconductor.macro.circuit.battery.Battery;


/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 1:03:55 PM
 */
public class Circuit {
    CompositeLinearBranch circuit = new CompositeLinearBranch();
    Vector2D.Double at;
    private double resistorWidth;

    public Circuit( double x, double y, double resistorWidth ) {
        this.resistorWidth = resistorWidth;
        this.at = new Vector2D.Double( x, y );
    }

    public LinearBranch wireAt( int i ) {
        return circuit.branchAt( i );
    }

    public Wire wireTo( double x, double y ) {
        Vector2D.Double to = new Vector2D.Double( x, y );
        Wire branch = new Wire( at, to );
        circuit.addBranch( branch );
        at = to;
        return branch;
    }

    public Resistor resistorTo( double x, double y ) {
        Vector2D.Double to = new Vector2D.Double( x, y );
        Resistor branch = new Resistor( at, to, resistorWidth );
        circuit.addBranch( branch );
        at = to;
        return branch;
    }

    public Battery batteryTo( double x, double y ) {
        Vector2D.Double to = new Vector2D.Double( x, y );
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

    public AbstractVector2D getPosition( double dist ) {
        return circuit.getPosition( dist );
    }

    public boolean contains( double newLoc ) {
        return newLoc >= 0 && newLoc <= getLength();
    }
}
