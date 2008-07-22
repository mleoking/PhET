/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;

/**
 * TracerFlag is the model of a tracer flag.
 * A tracer flag can be planted at a position along the glacier.
 * It will move with the glacier, thus indicating glacier movement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TracerFlag extends AbstractTool {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------    
    
    private static final double MIN_FALLOVER_ANGLE = Math.toRadians( 30 );
    private static final double MAX_FALLOVER_ANGLE = Math.toRadians( 80 );
    private static final Random RANDOM_FALLOVER = new Random();
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Glacier _glacier;
    private final GlacierListener _glacierListener;
    private boolean _onValleyFloor;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public TracerFlag( Point2D position, Glacier glacier ) {
        super( position );
        _glacier = glacier;
        _glacierListener = new GlacierAdapter() {
            public void iceThicknessChanged() {
                checkForDeletion();
            }
        };
        _glacier.addGlacierListener( _glacierListener );
        _onValleyFloor = false;
    }
    
    public void cleanup() {
        _glacier.removeGlacierListener( _glacierListener );
        super.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // AbstractTool overrides
    //----------------------------------------------------------------------------

    /*
     * If the tool is above or below the ice, snap it to the ice.
     * If there is no ice, the tool will snap to the valley floor.
     */
    protected void constrainDrop() {
        
        // constrain x to >= headwall
        final double x = Math.max( getX(), _glacier.getHeadwallX() );
        
        final double surfaceElevation = _glacier.getSurfaceElevation( x );
        final double valleyElevation = _glacier.getValley().getElevation( x );
        
        // dropped where there is no ice?
        _onValleyFloor = ( surfaceElevation == valleyElevation );
        
        if ( getY() > surfaceElevation ) {
            // snap to ice surface
            setPosition( x, surfaceElevation );
        }
        else if ( getY() <= valleyElevation ) {
            if ( _onValleyFloor ) {
                // snap to the valley floor
                setPosition( x, valleyElevation );
            }
            else {
                // snap to slightly above the valley floor
                setPosition( x, valleyElevation + 1 );    
            }
        }
    }
    
    public void clockTicked( ClockEvent clockEvent ) {
        
        if ( !isDragging() && !_onValleyFloor ) {
            
            // distance = velocity * dt
            Vector2D velocity = _glacier.getIceVelocity( getX(), getElevation() );
            final double dt = clockEvent.getSimulationTimeChange();
            double newX = getX() + ( velocity.getX() * dt );
            double newY = getY() + ( velocity.getY() * dt );
            
            // constrain x to 1 meter beyond the terminus
            final double maxX = _glacier.getTerminusX() + 1;
            if ( newX > maxX ) {
                newX = maxX;
            }
            
            // constrain y to the surface of the glacier or valley
            final double newGlacierSurfaceElevation = _glacier.getSurfaceElevation( newX );
            if ( newY > newGlacierSurfaceElevation ) {
                newY = newGlacierSurfaceElevation;
            }
            
            // are we at past the terminus?
            final double newValleyElevation = _glacier.getValley().getElevation( newX );
            if ( newGlacierSurfaceElevation == newValleyElevation ) {
                _onValleyFloor = true;
                // flags "fall over" when they are past the terminus
                setOrientation( calculateRandomFalloverAngle() );
            }
            
            setPosition( newX, newY );
        }
    }
    
    /*
     * Calculates a random angle for the flag to "fall over".
     */
    private static double calculateRandomFalloverAngle() {
        return MIN_FALLOVER_ANGLE + ( RANDOM_FALLOVER.nextDouble() * ( MAX_FALLOVER_ANGLE - MIN_FALLOVER_ANGLE ) );
    }
    
    //----------------------------------------------------------------------------
    // Self deletion
    //----------------------------------------------------------------------------
    
    /*
     * Deletes itself if covered by an advancing glacier.
     */
    private void checkForDeletion() {
        if ( _onValleyFloor && !isDeletedSelf() ) {
            double iceThicknessAtFlag = _glacier.getIceThickness( getX() );
            if ( iceThicknessAtFlag > 0 ) {
                deleteSelf();
            }
        }
    }
}
