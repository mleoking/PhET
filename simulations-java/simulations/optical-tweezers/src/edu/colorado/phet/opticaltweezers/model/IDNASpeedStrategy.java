/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

/**
 * IDNASpeedStrategy is the interface for models that describe the speed with 
 * which an enzyme "pulls in" a bead attached to the end of a DNA strand.
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
    
    /**
     * Gets the force required to make the DNA strand move at a specified speed
     * in the given ATP concentration. This is the identical algorithm as getSpeed,
     * but we're given ATP and speed, and solving for force.
     * 
     * @param atp
     * @param speed
     * @return force (pN)
     */
    public double getForce( final double atp, final double speed );
}
