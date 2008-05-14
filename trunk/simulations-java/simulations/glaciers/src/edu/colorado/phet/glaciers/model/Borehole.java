package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

/**
 * Borehole is the model of a borehole.
 * A borehole is approximated as a set of points that make up a vertical line where the hole is created.
 * As the borehole evolves, the points move at different speeds, based on their position in the ice.
 * When all points have moved past the terminus, the borehole is deleted.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Borehole extends ClockAdapter {

    private static final double DZ = 10; // meters
        
    private Glacier _glacier;
    private ArrayList _points;
    private ArrayList _listeners;
    
    public Borehole( Glacier glacier, Point2D position ) {
        super();
        _glacier = glacier;
        _points = createPoints( position, glacier );
        _listeners = new ArrayList();
    }
    
    public void cleanup() {
        // do nothing
    }
    
    private static ArrayList createPoints( Point2D position, Glacier glacier ) {
        
        ArrayList points = new ArrayList();
        
        final double x = position.getX();
        final double valleyElevation = glacier.getValley().getElevation( x );
        final double glacierSurfaceElevation = glacier.getSurfaceElevation( x );
        double iceThickness = glacierSurfaceElevation - valleyElevation;
        assert( iceThickness >= 0 );
        
        double elevation = valleyElevation;
        Point2D p = null;
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
        
        return points;
    }

    public Point2D[] getPoints() {
        return (Point2D[]) _points.toArray( new Point2D[_points.size()] );
    }
    
    public void simulationTimeChanged( ClockEvent clockEvent ) {
        
        final double dt = clockEvent.getSimulationTimeChange();
        final double terminusX = _glacier.getTerminusX();
        
        ArrayList pointsCopy = new ArrayList( _points ); // iterate on a copy, since we may delete points from the original
        Iterator i = pointsCopy.iterator();
        while ( i.hasNext() ) {
            
            Point2D p = (Point2D) i.next();
            
            // distance = velocity * dt
            Vector2D velocity = _glacier.getIceVelocity( p.getX(), p.getY() );
            double newX = p.getX() + ( velocity.getX() * dt );
            double newY = p.getY() + ( velocity.getY() * dt );
            
            // constrain x to terminus
            if ( newX > terminusX ) {
                newX = terminusX;
            }

            // constrain elevation to the surface of the glacier
            double newGlacierSurfaceElevation = _glacier.getSurfaceElevation( newX );
            if ( newY > newGlacierSurfaceElevation ) {
                newY = newGlacierSurfaceElevation;
            }

            p.setLocation( newX, newY );
        }
        
        Point2D pointOnValleyFloor = (Point2D) pointsCopy.get( 0 );
        if ( pointOnValleyFloor.getX() < terminusX ) {
            notifyEvolved();
        }
        else {
            notifyDeleteMe();
        }
    }
    
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
