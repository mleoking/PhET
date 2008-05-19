/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;

/**
 * Debris is the model of debris that is moving in the ice.
 * Debris could include rocks, trees, dead animals, etc.
 * In this model, motion ignores the mass of the debris.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Debris extends Movable implements ClockListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Glacier _glacier;
    private GlacierListener _glacierListener;
    private boolean _onValleyFloor;
    private ArrayList _listeners;
    private boolean _enabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Debris( Point2D position, Glacier glacier ) {
        super( position );
        _glacier = glacier;
        _onValleyFloor = false;
        _glacierListener = new GlacierAdapter() {
            public void iceThicknessChanged() {
                checkForDeletion();
            }
        };
        _glacier.addGlacierListener( _glacierListener );
        _listeners = new ArrayList();
        _enabled = true;
    }
    
    public void cleanup() {
        _glacier.removeGlacierListener( _glacierListener );
    }
    
    private void checkForDeletion() {
        if ( _onValleyFloor ) {
            double iceThicknessAtFlag = _glacier.getIceThickness( getX() );
            if ( iceThicknessAtFlag > 0 ) {
                _enabled = false;
                notifyDeleteMe();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // ClockListener interface
    //----------------------------------------------------------------------------
    
    public void simulationTimeChanged( ClockEvent clockEvent ) {

        if ( _enabled && !_onValleyFloor ) {
            
            // distance = velocity * dt
            Vector2D velocity = _glacier.getIceVelocity( getX(), getY() );
            final double dt = clockEvent.getSimulationTimeChange();
            final double newX = getX() + ( velocity.getX() * dt );
            double newY = getY() + ( velocity.getY() * dt );

            // constrain to the surface of the glacier (or valley floor)
            double newGlacierSurfaceElevation = _glacier.getSurfaceElevation( newX );
            if ( newY > newGlacierSurfaceElevation ) {
                newY = newGlacierSurfaceElevation;
            }

            if ( newY == _glacier.getValley().getElevation( newX ) ) {
                _onValleyFloor = true;
            }

            setPosition( newX, newY );
        }
    }
    
    public void clockPaused( ClockEvent clockEvent ) {}

    public void clockStarted( ClockEvent clockEvent ) {}

    public void clockTicked( ClockEvent clockEvent ) {}

    public void simulationTimeReset( ClockEvent clockEvent ) {}
    
    //----------------------------------------------------------------------------
    // Listener
    //----------------------------------------------------------------------------
    
    public interface DebrisListener {
        public void deleteMe( Debris debris );
    }
    
    public void addDebrisListener( DebrisListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeDebrisListener( DebrisListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyDeleteMe() {
        ArrayList listenersCopy = new ArrayList( _listeners );
        Iterator i = listenersCopy.iterator(); // iterate on a copy to avoid ConcurrentModificationException
        while ( i.hasNext() ) {
            ( (DebrisListener) i.next() ).deleteMe( this );
        }
    }
}
