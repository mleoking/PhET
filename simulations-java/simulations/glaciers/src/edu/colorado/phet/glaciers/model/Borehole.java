package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;

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
    private static final DoubleRange FILL_IN_STARTS_RANGE = new DoubleRange( 1500, 2500 );
    private static final DoubleRange FILL_IN_ENDS_RANGE = new DoubleRange( 500, 1000 );
        
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Glacier _glacier;
    private final ArrayList _points; // points that approximate the borehole
    private double _percentFilledIn;
    private final double _fillInStartsX; // x coordinate where the borehole starts to fill in
    private final double _fillInEndsX; // x coordinate where the borehole is completely filled in
    private final ArrayList _listeners;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Borehole( Glacier glacier, Point2D position ) {
        super();
        _glacier = glacier;
        _points = createPoints( position, glacier );
        _percentFilledIn = 0;
        _fillInStartsX = position.getX() + FILL_IN_STARTS_RANGE.getMin();
        _fillInEndsX = _fillInStartsX + FILL_IN_ENDS_RANGE.getMin();
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
     * Gets the percentage of the borehole that has been filled in by ice.
     * 
     * @return 0 to 1
     */
    public double getPercentFilledIn() {
        return _percentFilledIn;
    }
    
    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------
    
    public void simulationTimeChanged( ClockEvent clockEvent ) {

        if ( _points != null ) {
            
            final double dt = clockEvent.getSimulationTimeChange();
            final double terminusX = _glacier.getTerminusX();

            ArrayList pointsCopy = new ArrayList( _points ); // iterate on a copy, since we may delete points from the original
            Point2D currentPoint = null;
            boolean previousPointIsOnGlacierSurface = false;
            Iterator i = pointsCopy.iterator();
            while ( i.hasNext() ) {

                currentPoint = (Point2D) i.next();

                // distance = velocity * dt
                Vector2D velocity = _glacier.getIceVelocity( currentPoint.getX(), currentPoint.getY() );
                double newX = currentPoint.getX() + ( velocity.getX() * dt );
                double newY = currentPoint.getY() + ( velocity.getY() * dt );

                // constrain x to terminus
                if ( newX > terminusX ) {
                    newX = terminusX;
                }

                // constrain elevation to the surface of the glacier.
                // prune points so that at most one point is on the surface.
                double newGlacierSurfaceElevation = _glacier.getSurfaceElevation( newX );
                if ( newY > newGlacierSurfaceElevation ) {
                    if ( previousPointIsOnGlacierSurface ) {
                        _points.remove( currentPoint );
                    }
                    else {
                        previousPointIsOnGlacierSurface = true;
                        newY = newGlacierSurfaceElevation;
                    }
                }
                else {
                    previousPointIsOnGlacierSurface = false;
                }

                currentPoint.setLocation( newX, newY );
            }

            // evolve or delete?
            Point2D pointOnValleyFloor = (Point2D) pointsCopy.get( 0 );
            if ( _points.size() < 2 || pointOnValleyFloor.getX() >= terminusX ) {
                _percentFilledIn = 1;
                notifyDeleteMe();
            }
            else {
                notifyEvolved();
            }
        }
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
