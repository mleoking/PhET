// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

/**
 * Borehole is the model of a borehole.
 * A borehole is approximated as a set of points that make up a vertical line where the hole is created.
 * As the borehole evolves, the points move at different speeds, based on their position in the ice.
 * The borehole also "fills in" as it evolves.  The borehole is deleted when it is completely filled in,
 * or when all of its points reach the terminus.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Borehole extends ClockAdapter {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final double DZ = 10; // distance between points (meters)
    private static final double FILL_IN_BEGINS = 40; // when the borehole starts to fill in, relative to when it was created (years)
    private static final double FILL_IN_DURATION = 40; // how long it takes for the borehole to completely fill in (years)

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final Glacier _glacier;
    private ArrayList _points; // points that approximate the borehole
    private double _percentFilledIn; // percentage of the borehole that is filled in (0.0-1.0)
    private double _fillInStartTime; // absolute time at which the borehole will start to fill in (years)
    private double _fillInEndTime; // absolute time at which the borehole will be completely filled in (years)
    private final ArrayList _listeners;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public Borehole( Glacier glacier, Point2D position ) {
        super();
        _glacier = glacier;
        _points = createPoints( position, glacier );
        _percentFilledIn = 0;
        _fillInStartTime = _fillInEndTime = -1; // undefined until first clock tick
        _listeners = new ArrayList();
    }

    public void cleanup() {
        // do nothing
    }

    /*
    * Creates the points that approximate the borehole.
    * This is a straight line, with points ordered from the valley floor to the glacier's surface.
    * NOTE! OTHER PARTS OF THIS MODEL DEPEND ON THIS ORDERING OF POINTS!
    *
    * Returns null if there is no ice at the specified position.
    */
    private static ArrayList createPoints( Point2D position, Glacier glacier ) {

        ArrayList points = null;

        final double x = position.getX();
        final double valleyElevation = glacier.getValley().getElevation( x );
        final double glacierSurfaceElevation = glacier.getSurfaceElevation( x );
        double iceThickness = glacierSurfaceElevation - valleyElevation;

        if ( iceThickness > 0 ) {
            points = new ArrayList();
            Point2D p = null;
            double elevation = valleyElevation;
            while ( elevation <= glacierSurfaceElevation ) {
                p = new Point2D.Double( x, elevation );
                if ( elevation != glacierSurfaceElevation && elevation + DZ > glacierSurfaceElevation ) {
                    elevation = glacierSurfaceElevation;
                }
                else {
                    elevation += DZ;
                }
                points.add( p );
            }
        }

        return points;
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    /**
     * Gets the points that approximate the borehole.
     * The points are ordered from the valley floor to the glacier surface.
     * This returns a reference to the points (not a copy), so callers
     * should NOT modify the points.
     *
     * @return Point2D[]
     */
    public Point2D[] getPoints() {
        return (Point2D[]) _points.toArray( new Point2D[_points.size()] );
    }

    /**
     * Gets the percentage of the borehole that has been filled in.
     *
     * @return 0.0 to 1.0
     */
    public double getPercentFilledIn() {
        return _percentFilledIn;
    }

    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------

    /*
     * Evolves the model when the clock ticks.
     */
    public void clockTicked( ClockEvent clockEvent ) {

        if ( _points != null ) {

            final double t = clockEvent.getSimulationTime();

            // initialize "fill in" model
            if ( _fillInStartTime == -1 ) {
                _fillInStartTime = t + FILL_IN_BEGINS;
                _fillInEndTime = _fillInStartTime + FILL_IN_DURATION;
            }

            if ( t >= _fillInEndTime ) {
                // borehole is completely filled in
                deleteMe();
            }
            else {

                // calculate how much of the borehole is filled in
                if ( t < _fillInStartTime ) {
                    _percentFilledIn = 0;
                }
                else {
                    _percentFilledIn = 1 - ( ( _fillInEndTime - t ) / ( _fillInEndTime - _fillInStartTime ) );
                }

                // evolve the points
                final double dt = clockEvent.getSimulationTimeChange();
                final double terminusX = _glacier.getTerminusX();
                ArrayList pointsCopy = new ArrayList( _points ); // iterate on a copy, since we may delete points from the original
                Point2D currentPoint = null;
                Iterator i = pointsCopy.iterator();
                while ( i.hasNext() ) {

                    currentPoint = (Point2D) i.next();

                    // distance = velocity * dt
                    MutableVector2D velocity = _glacier.getIceVelocity( currentPoint.getX(), currentPoint.getY() );
                    double newX = currentPoint.getX() + ( velocity.getX() * dt );
                    double newY = currentPoint.getY() + ( velocity.getY() * dt );

                    // constrain x to terminus
                    if ( newX > terminusX ) {
                        newX = terminusX;
                    }

                    // prune points that breach the glacier's surface
                    double newGlacierSurfaceElevation = _glacier.getSurfaceElevation( newX );
                    if ( newY > newGlacierSurfaceElevation ) {
                        _points.remove( currentPoint );
                    }

                    currentPoint.setLocation( newX, newY );
                }

                // evolve or delete?
                Point2D pointOnValleyFloor = (Point2D) pointsCopy.get( 0 );
                if ( _points.size() < 2 || pointOnValleyFloor.getX() >= terminusX ) {
                    deleteMe();
                }
                else {
                    notifyEvolved();
                }
            }
        }
    }

    /*
    * Borehole deletes itself.
    */
    private void deleteMe() {
        _percentFilledIn = 1;
        _points = null;
        notifyDeleteMe();
    }

    //----------------------------------------------------------------------------
    // Listener
    //----------------------------------------------------------------------------

    public interface BoreholeListener {
        public void evolved();

        public void deleteMe( Borehole borehole );
    }

    public static class BoreholeAdapter implements BoreholeListener {
        public void evolved() {}

        public void deleteMe( Borehole borehole ) {}
    }

    public void addBoreholeListener( BoreholeListener listener ) {
        _listeners.add( listener );
    }

    public void removeBoreholeListener( BoreholeListener listener ) {
        _listeners.remove( listener );
    }

    private void notifyEvolved() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (BoreholeListener) i.next() ).evolved();
        }
    }

    private void notifyDeleteMe() {
        ArrayList listenersCopy = new ArrayList( _listeners ); // iterate on a copy, deleteMe causes a call to removeBoreholeListener
        Iterator i = listenersCopy.iterator();
        while ( i.hasNext() ) {
            ( (BoreholeListener) i.next() ).deleteMe( this );
        }
    }
}
