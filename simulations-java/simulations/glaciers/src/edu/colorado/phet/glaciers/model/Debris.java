/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;

/**
 * Debris is the model of debris that is moving in the ice.
 * Debris could include rocks, trees, dead animals, etc.
 * In this model, motion ignores the mass of the debris.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Debris extends ClockAdapter {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Point3D _position;
    private Glacier _glacier;
    private GlacierListener _glacierListener;
    private boolean _onValleyFloor;
    private ArrayList _listeners;
    private boolean _deletedSelf;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Debris( Point3D position, Glacier glacier ) {
        super();
        
        _position = new Point3D.Double( position );
        
        _glacier = glacier;
        _onValleyFloor = false;
        _glacierListener = new GlacierAdapter() {
            public void iceThicknessChanged() {
                checkForDeletion();
            }
        };
        _glacier.addGlacierListener( _glacierListener );
        _listeners = new ArrayList();
        _deletedSelf = false;
    }
    
    public void cleanup() {
        _glacier.removeGlacierListener( _glacierListener );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public boolean isOnValleyFloor() {
        return _onValleyFloor;
    }
    
    private void setPosition( double x, double y, double z ) {
        if ( x != getX() || y != getY() || z != getZ() ) {
            _position.setLocation( x, y, z );
            notifyPositionChanged();
        }
    }
    
    /**
     * Gets the position in 3D space.  The axes are as follows:
     * x = distance downvalley from valley headwall
     * y = elevation
     * z = distance across the valley (+ into the screen)
     * 
     * @return position (meters)
     */
    public Point3D getPositionReference() {
        return _position;
    }
    
    public double getX() {
        return _position.getX();
    }
    
    public double getY() {
        return _position.getY();
    }
    
    public double getZ() {
        return _position.getZ();
    }
    
    //----------------------------------------------------------------------------
    // Self deletion
    //----------------------------------------------------------------------------
    
    /*
     * Deletes itself if covered by an advancing glacier.
     */
    private void checkForDeletion() {
        if ( _onValleyFloor ) {
            double iceThicknessAtFlag = _glacier.getIceThickness( getX() );
            if ( iceThicknessAtFlag > 0 ) {
                _deletedSelf = true;
                notifyDeleteMe();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // ClockListener interface
    //----------------------------------------------------------------------------
    
    public void simulationTimeChanged( ClockEvent clockEvent ) {

        if ( !_deletedSelf && !_onValleyFloor ) {
            
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

            setPosition( newX, newY, getZ() ); // z doesn't change
        }
    }
    
    //----------------------------------------------------------------------------
    // Listener
    //----------------------------------------------------------------------------
    
    public interface DebrisListener {
        public void positionChanged();
        public void deleteMe( Debris debris );
    }
    
    public static class DebrisAdapter implements DebrisListener {
        public void positionChanged() {}
        public void deleteMe( Debris debris ){}
    }
    
    public void addDebrisListener( DebrisListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeDebrisListener( DebrisListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyPositionChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (DebrisListener) i.next() ).positionChanged();
        }
    }
    
    private void notifyDeleteMe() {
        ArrayList listenersCopy = new ArrayList( _listeners );
        Iterator i = listenersCopy.iterator(); // iterate on a copy to avoid ConcurrentModificationException
        while ( i.hasNext() ) {
            ( (DebrisListener) i.next() ).deleteMe( this );
        }
    }
}
