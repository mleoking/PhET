// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.semiconductor.macro.circuit;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;


/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 1:12:48 PM
 */
public class Resistor extends LinearBranch {
    private double thickness;

    public Resistor( MutableVector2D start, MutableVector2D end, double thickness ) {
        super( start, end );
        this.thickness = thickness;
    }

    public double getHeight() {
        return thickness;
    }
}
