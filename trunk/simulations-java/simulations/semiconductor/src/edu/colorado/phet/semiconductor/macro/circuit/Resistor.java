package edu.colorado.phet.semiconductor.macro.circuit;

import edu.colorado.phet.common.phetcommon.math.Vector2D;


/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 1:12:48 PM
 */
public class Resistor extends LinearBranch {
    private double thickness;

    public Resistor( Vector2D.Double start, Vector2D.Double end, double thickness ) {
        super( start, end );
        this.thickness = thickness;
    }

    public double getHeight() {
        return thickness;
    }
}
