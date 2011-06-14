// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.model;

/**
 * IDNASpeedStrategy is the interface for models that describe the speed with 
 * which an enzyme "pulls in" a bead attached to the end of a DNA strand.
 * <p>
 * The Strategy pattern was used here because we had competing methods of solving
 * this problem.  Only one implementation of this interface is used in the production
 * code; the other implementation(s) are preserved for historical purposes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IDNASpeedStrategy {

    /**
     * Gets the speed at which the DNA strand is moving through the enzyme
     * for specific ATP and DNA force values.
     * <p>
     * This is erroneously refered to as velocity in the design document.
     * It is a function of the DNA force magnitude, so it has no orientation
     * and should be referred to as speed (the magnitude component of velocity).
     * 
     * @param atp ATP concentration (arbitrary units)
     * @param force DNA force magnitude (pN)
     * @return speed (nm/sec)
     */
    public double getSpeed( final double atp, final double force );
    
    /**
     * Gets the maximum speed, when DNA force=0 and ATP concentration=infinite.
     */
    public double getMaxSpeed();
}
