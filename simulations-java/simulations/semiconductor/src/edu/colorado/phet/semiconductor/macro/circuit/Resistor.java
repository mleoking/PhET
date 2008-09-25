package edu.colorado.phet.semiconductor.macro.circuit;

import edu.colorado.phet.semiconductor.util.math.PhetVector;

/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 1:12:48 PM
 */
public class Resistor extends LinearBranch {
    private double thickness;

    public Resistor( PhetVector start, PhetVector end, double thickness ) {
        super( start, end );
        this.thickness = thickness;
    }

    public double getHeight() {
        return thickness;
    }
}
