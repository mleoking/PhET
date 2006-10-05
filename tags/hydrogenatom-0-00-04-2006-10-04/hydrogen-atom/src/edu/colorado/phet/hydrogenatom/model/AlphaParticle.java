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
public class AlphaParticle extends DynamicObject implements IModelObject {
    
    private static final double DISTANCE_PER_DT = 5;
    
    public AlphaParticle( Point2D position, double orientation ) {
        super( position, orientation );
    }
    
    public void simulationTimeChanged( ClockEvent event ) {
        double dt = event.getSimulationTimeChange();
        double distance = DISTANCE_PER_DT * dt;
        move( distance );
    }
    
    private void move( double distance ) {
        double direction = getOrientation();
        double dx = Math.cos( direction ) * distance;
        double dy = Math.sin( direction ) * distance;
        double x = getX() + dx;
        double y = getY() + dy;
        setPosition( x, y );
//        System.out.println( "AlphaParticle.move distance=" + distance + " dx=" + dx + " dy=" + dy + " " + this );
    }
    
    public String toString() {
        String s = "Photon ";
        s += ( "id=" + getId() + " " );
        s += ( "position=" + DebugUtils.format( getPositionRef() ) + " " );
        s += ( "orientation=" + DebugUtils.format( Math.toDegrees( getOrientation() ) ) + " " );
        return s;
    }
}
