/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

/**
 * EnzymeA is one type of fictitious enzyme.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EnzymeA extends AbstractEnzyme {
    
    // speed when DNA force=0 and ATP concentration=infinite
    private static final double MAX_DNA_SPEED = 5000; // nm/s
    
    // calibration constants for DNA speed model
    private static final double[] CALIBRATION_CONSTANTS = { 4.79, 4.7, 0.09, 0.82, 2.1, 2, 0.1, 1.2 };

    public EnzymeA( Point2D position, double outerDiameter, double innerDiameter, 
            DNAStrand dnaStrandBead, DNAStrand dnaStrandFree, Fluid fluid, double maxDt ) {
        super( position, outerDiameter, innerDiameter, 
                dnaStrandBead, dnaStrandFree, fluid, maxDt, MAX_DNA_SPEED, CALIBRATION_CONSTANTS );
    }
}
