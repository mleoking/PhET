package edu.colorado.phet.buildanatom.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * @author John Blanco
 */
public class ElectronShell extends SimpleObservable {

    private final double radius;
    private final HashMap<Point2D, Electron> occupiedShellLocations = new HashMap<Point2D, Electron>();

    // Listener for events where the user grabs the particle, which is interpreted as
    // removal from the bucket.
    private final SubatomicParticle.Adapter particleRemovalListener = new SubatomicParticle.Adapter() {
        @Override
        public void grabbedByUser( SubatomicParticle particle ) {
            // The user has picked up this particle, so we assume
            // that they want to remove it.
            removeElectron( (Electron)particle );
        }
    };

    /**
     * Return the first Point2D key associated with the specified particle.
     *
     * @param particle the particle value for which to look up the key
     * @return the first Point2D key associated with the specified particle, or null if no such value.
     */
    private Point2D getKey( SubatomicParticle particle ) {
        assert particle != null;
        for ( Point2D point2D : occupiedShellLocations.keySet() ) {
            if ( occupiedShellLocations.get( point2D ) == particle ) {
                return point2D;
            }
        }
        return null;
    }

    ElectronShell( double radius, int electronCapacity ) {
        this.radius = radius;
        // Initialize the open shell locations.
        double angleBetweenElectrons = 2 * Math.PI / electronCapacity;
        for ( int i = 0; i < electronCapacity; i++ ) {
            double angle = i * angleBetweenElectrons;
            occupiedShellLocations.put( new Point2D.Double( this.radius * Math.cos( angle ), this.radius * Math.sin( angle ) ), null );
        }
    }

    /**
     * @param point2d
     * @return
     */
    public Electron getClosestElectron( Point2D point2d ) {//TODO: Consider sorting a list to attain closest electron
        Electron closestElectron = null;
        for ( Electron candidateElectron : occupiedShellLocations.values() ) {
            if ( candidateElectron != null ) {
                if ( closestElectron == null ) {
                    closestElectron = candidateElectron;
                }
                else if ( candidateElectron.getPosition().distance( point2d ) < closestElectron.getPosition().distance( point2d ) ) {
                    // This electron is closer.
                    closestElectron = candidateElectron;
                }
            }
        }
        return closestElectron;
    }

    /**
     * @return
     */
    public ArrayList<Point2D> getOpenShellLocations() {
        ArrayList<Point2D> list = new ArrayList<Point2D>();
        for ( Point2D point2D : occupiedShellLocations.keySet() ) {
            if ( occupiedShellLocations.get( point2D ) == null ) {
                list.add( point2D );
            }
        }
        return list;
    }

    protected double getRadius() {
        return radius;
    }

    protected boolean isFull() {
        return getOpenShellLocations().size() == 0;
    }

    protected boolean isEmpty() {
        return getOpenShellLocations().size() == occupiedShellLocations.size();
    }

    protected void reset() {
        for ( Electron electron : occupiedShellLocations.values() ) {
            if ( electron != null ) {
                electron.removeListener( particleRemovalListener );
            }
        }
        for ( Point2D point2D : occupiedShellLocations.keySet() ) {
            occupiedShellLocations.put( point2D, null );
        }
    }

    protected void addElectron( final Electron electronToAdd ) {
        Point2D shellLocation = findClosestOpenLocation( electronToAdd.getPosition() );
        if (shellLocation == null){
            System.err.println( getClass().getName() + " - Error: No space in shell." );
        }
        assert shellLocation != null;
        electronToAdd.setDestination( shellLocation );
        occupiedShellLocations.put( shellLocation, electronToAdd );
        electronToAdd.addListener( particleRemovalListener );
        notifyObservers();
    }

    public void removeElectron( Electron electronToRemove ) {
        assert occupiedShellLocations.containsValue( electronToRemove );
        occupiedShellLocations.put( getKey( electronToRemove ), null );
        electronToRemove.removeListener( particleRemovalListener );
        notifyObservers();
    }

    private Point2D findClosestOpenLocation( Point2D comparisonPt ) {
        ArrayList<Point2D> openShellLocations = getOpenShellLocations();
        if ( openShellLocations.size() == 0 ) {
            return null;
        }

        Point2D closestPoint = null;
        for ( Point2D candidatePoint : openShellLocations ) {
            if ( closestPoint == null || closestPoint.distance( comparisonPt ) > candidatePoint.distance( comparisonPt ) ) {
                // Candidate point is better.
                closestPoint = candidatePoint;
            }
        }
        return closestPoint;
    }
}
