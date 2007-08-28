/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

/**
 * EnzymeB is one type of fictitious enzyme.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EnzymeB extends AbstractEnzyme {
    
    // speed when DNA force=0 and ATP concentration=infinite
    private static final double MAX_DNA_SPEED = 5000; // nm/s
    
    // calibration constants for DNA speed model
    private static final double C1 = 4.79;
    private static final double C2 = 4.7;
    private static final double C3 = 0.09;
    private static final double C4 = 0.82;
    private static final double C5 = 2.1;
    private static final double C6 = 2;
    private static final double C7 = 0.1;
    private static final double C8 = 1.2;
    
    public EnzymeB( Point2D position, double outerDiameter, double innerDiameter, DNAStrand dnaStrand, Fluid fluid, double maxDt ) {
        super( position, outerDiameter, innerDiameter, dnaStrand, fluid, maxDt, MAX_DNA_SPEED, C1, C2, C3, C4, C5, C6, C7, C8 );
    }

}
