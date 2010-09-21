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
        
        assert( position.getX() >= glacier.getHeadwallX() ); // downvalley from the headwall
        assert( position.getY() < glacier.getSurfaceElevation( position.getX() ) ); // below glacier's surface
        assert( position.getY() > glacier.getValley().getElevation( position.getX() ) ); // above valley floor
        
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
    
    public void deleteSelf() {
        if ( !_deletedSelf ) {
            _deletedSelf = true;
            notifyDeleteMe();
        }
    }
    
    public boolean isDeletedSelf() {
        return _deletedSelf;
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public boolean isOnValleyFloor() {
        return _onValleyFloor;
    }
    
    private void setOnValleyFloor( boolean b ) {
        if ( b != _onValleyFloor ) {
            _onValleyFloor = b;
            notifyOnValleyFloorChanged();
        }
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
    
    public String toString() {
        return getClass().getName() + "[position=" + _position + ", onValleyFloor=" + _onValleyFloor + ", deletedSelf=" + _deletedSelf + "]";
    }
    
    //----------------------------------------------------------------------------
    // Self deletion
    //----------------------------------------------------------------------------
    
    /*
     * Deletes itself if covered by an advancing glacier.
     */
    private void checkForDeletion() {
        if ( _onValleyFloor && !_deletedSelf ) {
            double iceThickness = _glacier.getIceThickness( getX() );
            if ( iceThickness > 0 ) {
                deleteSelf();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // ClockListener interface
    //----------------------------------------------------------------------------
    
    public void clockTicked( ClockEvent clockEvent ) {

        if ( !_onValleyFloor && !_deletedSelf ) {
            
            // if the debris was on the surface, make sure it's still on the surface
            double currentY = getY();
            final double currentSurfaceElevation = _glacier.getSurfaceElevation( getX() );
            if ( currentY > currentSurfaceElevation) {
                currentY = currentSurfaceElevation;
            }
            
            // distance = velocity * dt
            Vector2D velocity = _glacier.getIceVelocity( getX(), currentY );
            final double dt = clockEvent.getSimulationTimeChange();
            double newX = getX() + ( velocity.getX() * dt );
            double newY = getY() + ( velocity.getY() * dt );

            // constrain x to the terminus
            final double terminusX = _glacier.getTerminusX();
            if ( getX() < terminusX && newX > terminusX ) {
                newX = terminusX;
            }

            // constrain y to the ice
            double newIceThickness = _glacier.getIceThickness( newX );
            double newValleyElevation = _glacier.getValley().getElevation( newX );
            if ( newY > newValleyElevation + newIceThickness ) {
                // constrain to the surface
                newY = newValleyElevation + newIceThickness;
            }
            else if ( newY < newValleyElevation ) {
                if ( newIceThickness == 0 ) {
                    // constrain to the valley floor if there's no ice
                    newY = newValleyElevation;
                }
                else {
                    // constrain to just above the valley floor if there is ice
                    newY = Math.min( newValleyElevation + 1, newValleyElevation + newIceThickness );
                }
            }

            // z doesn't change in this model, not realistic, but acceptable here
            final double newZ = getZ();

            // have we moved to the valley floor?
            if ( newY == _glacier.getValley().getElevation( newX ) ) {
                setOnValleyFloor( true );
            }

            setPosition( newX, newY, newZ );
        }
    }
    
    //----------------------------------------------------------------------------
    // Listener
    //----------------------------------------------------------------------------
    
    public interface DebrisListener {
        public void positionChanged();
        public void onValleyFloorChanged( Debris debris );
        public void deleteMe( Debris debris );
    }
    
    public static class DebrisAdapter implements DebrisListener {
        public void positionChanged() {}
        public void onValleyFloorChanged( Debris debris ) {};
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
    
    private void notifyOnValleyFloorChanged() {
        ArrayList listenersCopy = new ArrayList( _listeners );
        Iterator i = listenersCopy.iterator(); // iterate on a copy to avoid ConcurrentModificationException
        while ( i.hasNext() ) {
            ( (DebrisListener) i.next() ).onValleyFloorChanged( this );
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
