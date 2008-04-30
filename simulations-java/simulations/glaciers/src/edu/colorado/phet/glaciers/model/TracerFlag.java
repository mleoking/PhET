/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

/**
 * TracerFlag is the model of a tracer flag.
 * A tracer flag can be planted at a position along the glacier.
 * It will move with the glacier, thus indicating glacier movement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TracerFlag extends AbstractTool {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Glacier _glacier;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public TracerFlag( Point2D position, Glacier glacier ) {
        super( position );
        _glacier = glacier;
    }
    
    public void cleanup() {
        super.cleanup();
    }

    //----------------------------------------------------------------------------
    // AbstractTool overrides
    //----------------------------------------------------------------------------
    
    public void simulationTimeChanged( ClockEvent clockEvent ) {
        if ( isDragging() ) {
            // distance = velocity * dt
            Vector2D velocity = _glacier.getIceVelocity( getX(), getElevation() );
            final double dt = clockEvent.getSimulationTimeChange();
            final double dx = velocity.getX() * dt;
            final double dy = velocity.getY() * dt;
            if ( dx != 0 || dy != 0 ) {
                double newX = getX() + dx;
                double newY = getY() + dy;
                // constrain to the surface of the glacier
                double newGlacierSurfaceElevation = _glacier.getSurfaceElevation( newX );
                if ( newY > newGlacierSurfaceElevation ) {
                    newY = newGlacierSurfaceElevation;
                }
                setPosition( newX, newY );
            }
        }
    }
}
