// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Physical model for an Electron shell, which describes the radius, and tracks electrons (one for each available position).
 * @author John Blanco
 * @author Sam Reid
 */
public class ElectronShell extends SimpleObservable {

    private final double radius;
    private final HashMap<Point2D, Electron> shellLocations = new HashMap<Point2D, Electron>();
    private final int electronCapacity;

    /**
     * Constructor.
     */
    ElectronShell( double radius, int electronCapacity ) {
        this.radius = radius;
        this.electronCapacity = electronCapacity;
        // Initialize the open shell locations.
        double angleBetweenElectrons = 2 * Math.PI / electronCapacity;
        for ( int i = 0; i < electronCapacity; i++ ) {
            double angle = i * angleBetweenElectrons;
            shellLocations.put( new Point2D.Double( this.radius * Math.cos( angle ), this.radius * Math.sin( angle ) ), null );
        }
    }

    // Listener for events where the user grabs the particle, which is interpreted as
    // removal from the shell.
    private final SubatomicParticle.Adapter particleRemovalListener = new SubatomicParticle.Adapter() {
        @Override
        public void grabbedByUser( SubatomicParticle particle ) {
            // The user has picked up this particle, so we assume
            // that they want to remove it.
            removeElectron( (Electron) particle );
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
        for ( Point2D point2D : shellLocations.keySet() ) {
            if ( shellLocations.get( point2D ) == particle ) {
                return point2D;
            }
        }
        return null;
    }

    /**
     * @param point2d
     * @return
     */
    public Electron getClosestElectron( Point2D point2d ) {
        Electron closestElectron = null;
        for ( Electron candidateElectron : shellLocations.values() ) {
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
        for ( Point2D point2D : shellLocations.keySet() ) {
            if ( shellLocations.get( point2D ) == null ) {
                list.add( point2D );
            }
        }
        return list;
    }

    public int getNumElectrons(){
        return shellLocations.size() - getOpenShellLocations().size();
    }

    public int getNumOpenLocations(){
        return getOpenShellLocations().size();
    }

    public int getElectronCapacity(){
        return electronCapacity;
    }

    public double getRadius() {
        return radius;
    }

    protected boolean isFull() {
        return getOpenShellLocations().size() == 0;
    }

    protected boolean isEmpty() {
        return getOpenShellLocations().size() == shellLocations.size();
    }

    protected void reset() {
        if (getNumElectrons() > 0){
            for ( Electron electron : shellLocations.values() ) {
                if ( electron != null ) {
                    electron.removeListener( particleRemovalListener );
                }
            }
            for ( Point2D point2D : shellLocations.keySet() ) {
                shellLocations.put( point2D, null );
            }
            notifyObservers();
        }
    }

    protected void addElectron( final Electron electronToAdd, boolean moveImmediately ) {
        Point2D shellLocation = findClosestOpenLocation( electronToAdd.getPosition() );
        if (shellLocation == null){
            System.err.println( getClass().getName() + " - Error: No space in shell." );
        }
        assert shellLocation != null;
        electronToAdd.setDestination( shellLocation );
        shellLocations.put( shellLocation, electronToAdd );
        electronToAdd.addListener( particleRemovalListener );

        //If the particle shouldn't animate, then it should move to its destination immediately.
        if ( moveImmediately ) {
            electronToAdd.moveToDestination();
        }
        notifyObservers();
    }

    public Electron removeElectron( Electron electronToRemove ) {
        assert shellLocations.containsValue( electronToRemove );
        assert electronToRemove != null;
        Electron removedElectron = null;
        if ( shellLocations.containsValue( electronToRemove ) ){
            shellLocations.put( getKey( electronToRemove ), null );
            electronToRemove.removeListener( particleRemovalListener );
            removedElectron = electronToRemove;
            notifyObservers();
        }
        return removedElectron;
    }

    /**
     * Remove an arbitrary electron, doesn't matter which one.
     */
    public Electron removeElectron() {
        assert getNumElectrons() > 0;
        // Gets the first electron in the map and removes it.
        for (Electron electron : shellLocations.values()){
            if (electron != null){
                return removeElectron( electron );
            }
        }
        return null;
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

    public boolean containsElectron( Electron electron ) {
        return shellLocations.containsValue( electron );
    }

    /**
     * Automatically synchronize added observers with our state.
     * @param observer
     */
    @Override
    public void addObserver( SimpleObserver observer ) {
        super.addObserver( observer );
        observer.update();
    }

    public ArrayList<Electron> getElectrons() {
        ArrayList<Electron> electrons = new ArrayList<Electron>();
        for ( Electron electron : shellLocations.values() ) {
            if ( electron != null ) {
                electrons.add( electron );
            }
        }
        return electrons;
    }
}
