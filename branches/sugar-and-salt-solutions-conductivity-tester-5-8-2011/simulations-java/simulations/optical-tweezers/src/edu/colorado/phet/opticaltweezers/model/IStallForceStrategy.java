// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.model;

/**
 * IStallForceStrategy is the interface for calculation stall force.
 * <p>
 * The Strategy pattern was used here because we had competing methods of solving
 * this problem.  Only one implementation of this interface is used in the production
 * code; the other implementation(s) are preserved for historical purposes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IStallForceStrategy {

    /**
     * Gets the stall force for a specified ATP concentration.
     * 
     * @param atpConcentration ATP concentration (arbitrary units)
     * @return stall force magnitude (pN)
     */
    public double getStallForceMagnitude( double atpConcentration );
}
