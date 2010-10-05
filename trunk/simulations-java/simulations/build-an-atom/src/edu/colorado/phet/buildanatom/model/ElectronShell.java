package edu.colorado.phet.buildanatom.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
* @author John Blanco
*/
public class ElectronShell extends SimpleObservable {

    private final double radius;
    private final int electronCapacity;
    private final ArrayList<Point2D> openShellLocations = new ArrayList<Point2D>();
    private final HashMap<Electron, Point2D> occupiedShellLocations = new HashMap<Electron, Point2D>();

    // Listener for events where the user grabs the particle, which is interpreted as
    // removal from the bucket.
    private final SubatomicParticle.Adapter particleRemovalListener = new SubatomicParticle.Adapter() {
        @Override
        public void grabbedByUser( SubatomicParticle particle ) {
            // The user has picked up this particle, so we assume
            // that they want to remove it.
            assert occupiedShellLocations.containsKey( particle );
            openShellLocations.add( occupiedShellLocations.get( particle ) );
            occupiedShellLocations.remove( particle );
            particle.removeListener( this );
        }
    };

    ElectronShell( double radius, int electronCapacity ) {
        this.radius = radius;
        this.electronCapacity = electronCapacity;
        // Initialize the open shell locations.
        double angleBetweenElectrons = 2 * Math.PI / electronCapacity;
        for ( int i = 0; i < electronCapacity; i++ ) {
            double angle = i * angleBetweenElectrons;
            openShellLocations.add( new Point2D.Double( this.radius * Math.cos( angle ), this.radius * Math.sin( angle ) ) );
        }
    }

    /**
     * @param point2d
     * @return
     */
    public Electron getClosestElectron( Point2D point2d ) {
        Electron closestElectron = null;
        for ( Electron candidateElectron : occupiedShellLocations.keySet() ) {
            if ( closestElectron == null ) {
                closestElectron = candidateElectron;
            }
            else if ( candidateElectron.getPosition().distance( point2d ) < closestElectron.getPosition().distance( point2d ) ) {
                // This electron is closer.
                closestElectron = candidateElectron;
            }
        }
        return closestElectron;
    }

    /**
     * @return
     */
    public ArrayList<Point2D> getOpenShellLocations() {
        return new ArrayList<Point2D>( openShellLocations );
    }

    protected double getRadius() {
        return radius;
    }

    protected boolean isFull() {
        return occupiedShellLocations.size() == electronCapacity;
    }

    protected boolean isEmpty() {
        return occupiedShellLocations.size() == 0;
    }

    protected void reset() {
        for ( Point2D occupiedLocation : occupiedShellLocations.values() ) {
            assert !openShellLocations.contains( occupiedLocation );
            openShellLocations.add( occupiedLocation );
        }
        for ( Electron electron : occupiedShellLocations.keySet() ){
            electron.removeListener( particleRemovalListener );
        }
        occupiedShellLocations.clear();
    }

    protected void addElectron( final Electron electronToAdd ) {
        Point2D shellLocation = findClosestOpenLocation( electronToAdd.getPosition() );
        assert shellLocation != null;
        electronToAdd.setDestination( shellLocation );
        openShellLocations.remove( shellLocation );
        occupiedShellLocations.put( electronToAdd, shellLocation );
        electronToAdd.addListener( particleRemovalListener );
        notifyObservers();
    }

    public void removeElectron( Electron electronToRemove ) {
        if ( !occupiedShellLocations.containsKey( electronToRemove ) ) {
            System.out.println( "Error!!!" );
        }
        assert occupiedShellLocations.containsKey( electronToRemove );
        Point2D newlyFreedShellLocation = occupiedShellLocations.get( electronToRemove );
        openShellLocations.add( newlyFreedShellLocation );
        occupiedShellLocations.remove( electronToRemove );
    }

    private Point2D findClosestOpenLocation( Point2D comparisonPt ) {
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
