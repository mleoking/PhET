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
    
    private final Glacier _glacier;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public TracerFlag( Point2D position, Glacier glacier ) {
        super( position );
        _glacier = glacier;
    }
    
    //----------------------------------------------------------------------------
    // AbstractTool overrides
    //----------------------------------------------------------------------------

    /*
     * If the tool is above or below the ice, snap it to the ice.
     * If there is no ice, the tool will snap to the valley floor.
     */
    protected void constrainDrop() {
        double surfaceElevation = _glacier.getSurfaceElevation( getX() );
        if ( getY() > surfaceElevation ) {
            setPosition( getX(), surfaceElevation );
        }
        else {
            double valleyElevation = _glacier.getValley().getElevation( getX() );
            if ( getY() < valleyElevation ) {
                setPosition( getX(), valleyElevation );
            }
        }
    }
    
    public void simulationTimeChanged( ClockEvent clockEvent ) {
        if ( !isDragging() ) {
            
            // distance = velocity * dt
            Vector2D velocity = _glacier.getIceVelocity( getX(), getElevation() );
            final double dt = clockEvent.getSimulationTimeChange();
            final double newX = getX() + ( velocity.getX() * dt );
            double newY = getY() + ( velocity.getY() * dt );
            
            // constrain to the surface of the glacier (or valley floor)
            double newGlacierSurfaceElevation = _glacier.getSurfaceElevation( newX );
            if ( newY > newGlacierSurfaceElevation ) {
                newY = newGlacierSurfaceElevation;
            }
            
            setPosition( newX, newY );
        }
    }
}
