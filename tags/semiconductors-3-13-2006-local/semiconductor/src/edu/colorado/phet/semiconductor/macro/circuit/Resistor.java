package edu.colorado.phet.semiconductor.macro.circuit;

import edu.colorado.phet.common.math.PhetVector;

/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 1:12:48 PM
 * Copyright (c) Jan 15, 2004 by Sam Reid
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
