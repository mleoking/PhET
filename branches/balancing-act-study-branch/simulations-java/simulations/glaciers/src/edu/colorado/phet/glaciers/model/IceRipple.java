// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.glaciers.model;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;

/**
 * IceRipple is the model of a "ripple" on the surface of the ice.
 * Ripples are used to show that the ice is moving.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IceRipple extends ClockAdapter {

    //----------------------------------------------------------------------------
    //  Instance data
    //----------------------------------------------------------------------------

    private Point2D _position; // point on the glacier's surface
    private final Dimension _size;
    private final double _zOffset; // z offset from surface cross section at z=0

    private final Glacier _glacier;
    private final GlacierListener _glacierListener;
    private boolean _deletedSelf;
    private ArrayList _listeners;

    //----------------------------------------------------------------------------
    //  Constructors
    //----------------------------------------------------------------------------

    public IceRipple( double x, Dimension size, double zOffset, Glacier glacier ) {
        super();

        _position = new Point2D.Double( x, glacier.getSurfaceElevation( x ) );
        _size = new Dimension( size );
        _zOffset = zOffset;

        _glacier = glacier;
        _glacierListener = new GlacierAdapter() {
            public void iceThicknessChanged() {
                checkForDeletion();
            }
        };
        _glacier.addGlacierListener( _glacierListener );

        _deletedSelf = false;
        _listeners = new ArrayList();
    }

    public void cleanup() {
        _glacier.removeGlacierListener( _glacierListener );
    }

    private void deleteSelf() {
        if ( !_deletedSelf ) {
            _deletedSelf = true;
            notifyDeleteMe();
        }
    }

    //----------------------------------------------------------------------------
    //  Setters and getters
    //----------------------------------------------------------------------------

    private void setPosition( double x, double y ) {
        if ( x != _position.getX() || y != _position.getY() ) {
            _position.setLocation( x, y );
            notifyPositionChanged();
        }
    }

    public Point2D getPositionReference() {
        return _position;
    }

    public double getX() {
        return _position.getX();
    }

    public double getY() {
        return _position.getY();
    }

    public Dimension getSize() {
        return new Dimension( _size );
    }

    public double getZOffset() {
        return _zOffset;
    }

    //----------------------------------------------------------------------------
    // Self deletion
    //----------------------------------------------------------------------------

    /*
    * Deletes itself when it hits the terminus.
    */
    private void checkForDeletion() {
        if ( !_deletedSelf ) {
            final double iceThickness = _glacier.getIceThickness( getX() );
            if ( iceThickness == 0 ) {
                deleteSelf();
            }
        }
    }

    //----------------------------------------------------------------------------
    // ClockListener interface
    //----------------------------------------------------------------------------

    public void clockTicked( ClockEvent clockEvent ) {

        if ( !_deletedSelf ) {

            final double surfaceElevation = _glacier.getSurfaceElevation( getX() );

            // distance = velocity * dt
            MutableVector2D velocity = _glacier.getIceVelocity( getX(), surfaceElevation );
            final double dt = clockEvent.getSimulationTimeChange();
            final double newX = getX() + ( velocity.getX() * dt );

            // y is always on the surface
            final double newY = _glacier.getSurfaceElevation( newX );

            // delete or move?
            if ( newX >= _glacier.getTerminusX() ) {
                deleteSelf();
            }
            else {
                setPosition( newX, newY );
            }
        }
    }

    //----------------------------------------------------------------------------
    // Listener
    //----------------------------------------------------------------------------

    public interface IceRippleListener {
        public void positionChanged();

        public void deleteMe( IceRipple ripple );
    }

    public static class IceRippleAdapter implements IceRippleListener {
        public void positionChanged() {}

        public void deleteMe( IceRipple ripple ) {}
    }

    public void addIceRippleListener( IceRippleListener listener ) {
        _listeners.add( listener );
    }

    public void removeIceRippleListener( IceRippleListener listener ) {
        _listeners.remove( listener );
    }

    private void notifyPositionChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (IceRippleListener) i.next() ).positionChanged();
        }
    }

    private void notifyDeleteMe() {
        ArrayList listenersCopy = new ArrayList( _listeners );
        Iterator i = listenersCopy.iterator(); // iterate on a copy to avoid ConcurrentModificationException
        while ( i.hasNext() ) {
            ( (IceRippleListener) i.next() ).deleteMe( this );
        }
    }
}
