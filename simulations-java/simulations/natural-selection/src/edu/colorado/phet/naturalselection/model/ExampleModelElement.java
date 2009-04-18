/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.naturalselection.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

/**
 * ExampleModelElement is an example model element.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModelElement extends ClockAdapter {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final double _width, _height; // immutable
    private Point2D _position;
    private double _orientation;

    private ArrayList _listeners;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ExampleModelElement( double width, double height, Point2D position, double orientation ) {
        super();
        _width = width;
        _height = height;
        _position = new Point2D.Double( position.getX(), position.getY() );
        _orientation = orientation;
        _listeners = new ArrayList();
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    public double getWidth() {
        return _width;
    }

    public double getHeight() {
        return _height;
    }

    public Point2D getPosition() {
        return new Point2D.Double( _position.getX(), _position.getY() );
    }

    public Point2D getPositionReference() {
        return _position;
    }

    public void setPosition( Point2D position ) {
        if ( !position.equals( _position ) ) {
            _position.setLocation( position );
            notifyPositionChanged();
        }
    }

    public double getOrientation() {
        return _orientation;
    }

    public void setOrientation( double orientation ) {
        if ( orientation != _orientation ) {
            _orientation = orientation;
            notifyOrientationChanged();
        }
    }

    //----------------------------------------------------------------------------
    // Notification
    //----------------------------------------------------------------------------

    private void notifyPositionChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ExampleModelElementListener) i.next() ).positionChanged();
        }
    }

    private void notifyOrientationChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ExampleModelElementListener) i.next() ).orientationChanged();
        }
    }

    //----------------------------------------------------------------------------
    // Listener
    //----------------------------------------------------------------------------

    public interface ExampleModelElementListener {
        public void positionChanged();

        public void orientationChanged();
    }

    public static class ExampleModelElementAdapter implements ExampleModelElementListener {
        public void positionChanged() {
        }

        public void orientationChanged() {
        }
    }

    public void addExampleModelElementListener( ExampleModelElementListener listener ) {
        _listeners.add( listener );
    }

    public void removeExampleModelElementListener( ExampleModelElementListener listener ) {
        _listeners.remove( listener );
    }

    //----------------------------------------------------------------------------
    // ClockAdapter overrides
    //----------------------------------------------------------------------------

    public void simulationTimeChanged( ClockEvent event ) {
        // move 1 unit of distance in the direction that we're pointing
        double distance = 1;
        double dx = PolarCartesianConverter.getX( distance, _orientation );
        double dy = PolarCartesianConverter.getY( distance, _orientation );
        Point2D p = getPositionReference();
        Point2D pNew = new Point2D.Double( p.getX() + dx, p.getY() + dy );
        setPosition( pNew );
    }
}
