/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;


/**
 * HollywoodCompass is a compass that does not correspond to any real-world
 * physical model.  Its behavior is faked to provide a rough approximation
 * of how a compass should behave.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HollywoodCompass extends AbstractCompass {

    /**
     * Sole constructor.
     * 
     * @param magnetModel the magnet that the compass is observing
     */
    public HollywoodCompass( IMagnet magnetModel ) {
        super( magnetModel );
    }
    
    /**
     * Abruptly changes the compass needle direction any time that there is
     * a change in the magnetic field direction.
     * 
     * @see edu.colorado.phet.faraday.model.AbstractCompass#stepInTime(double)
     */
    public void stepInTime( double dt ) {
        double previousDirection = super.getDirection();
        double newDirection = Math.toDegrees( super.getFieldStrength().getAngle() );
        if ( newDirection != previousDirection ) {
            super.setDirection( newDirection );
        }
    }

}
