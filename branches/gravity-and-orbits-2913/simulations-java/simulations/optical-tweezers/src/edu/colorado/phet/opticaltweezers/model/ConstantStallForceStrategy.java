// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.model;

/**
 * ConstantStallForceStrategy uses a constant stall force, regardless of ATP concentration.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConstantStallForceStrategy implements IStallForceStrategy {

    private final double _stallForceMagnitude; // pN
    
    /**
     * Constructor.
     * 
     * @param stallForceMagnitude
     */
    public ConstantStallForceStrategy( double stallForceMagnitude ) {
        _stallForceMagnitude = stallForceMagnitude;
    }
    
    /**
     * @see IDStallForceStrategy.getStallForceMagnitude
     */
    public double getStallForceMagnitude( double atpConcentration ) {
        return _stallForceMagnitude;
    }
}
