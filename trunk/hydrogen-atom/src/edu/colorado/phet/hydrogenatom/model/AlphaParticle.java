/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.hydrogenatom.util.DebugUtils;

/**
 * AlphaParticle is the model of an alpha particle.
 * An alpha particle has a position and direction of motion.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AlphaParticle extends DynamicObject {
    
    private static final double DISTANCE_PER_DT = 1;
    
    public AlphaParticle( Point2D position, double orientation ) {
        super( position, orientation );
    }
    
    public void simulationTimeChanged( ClockEvent clockEvent ) {
        double dt = clockEvent.getSimulationTimeChange();
        double distance = DISTANCE_PER_DT * dt;
        double direction = getOrientation();
        double x = Math.cos( direction ) * distance;
        double y = Math.sin( direction ) * distance;
        setPosition( x, y );
    }
    
    public String toString() {
        String s = "Photon ";
        s += ( "position=" + DebugUtils.format( getPositionRef() ) + " " );
        s += ( "orientation=" + DebugUtils.format( Math.toDegrees( getOrientation() ) ) + " " );
        return s;
    }
}
