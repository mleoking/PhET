// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.model;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Physical model for an Electron shell, which describes the radius, and
 * tracks electrons (one for each available position).  The locations where
 * the electrons can reside are evenly spaced around the radius of the shell.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class ElectronShell extends SimpleObservable {

    private final double radius;
    private final HashMap<Point2D, Electron> shellLocations = new HashMap<Point2D, Electron>();
    private final int electronCapacity;
    private final Point2D centerLocation = new Point2D.Double(0, 0);

    /**
     * Constructor.
     * @param initialLocation TODO
     */
    ElectronShell( double radius, int electronCapacity, Point2D initialLocation ) {
        this.radius = radius;
        this.electronCapacity = electronCapacity;
        centerLocation.setLocation( initialLocation );
        // Initialize the open shell locations.
        double angleBetweenElectrons = 2 * Math.PI / electronCapacity;
        for ( int i = 0; i < electronCapacity; i++ ) {
            double angle = i * angleBetweenElectrons;
            shellLocations.put( new Point2D.Double( this.radius * Math.cos( angle ), this.radius * Math.sin( angle ) ), null );
        }
    }

    // Listener for events where the user grabs the particle, which is interpreted as
    // removal from the shell.
    private final SphericalParticle.Adapter particleRemovalListener = new SphericalParticle.Adapter() {
        @Override
        public void grabbedByUser( SphericalParticle particle ) {
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
    private Point2D getKey( SphericalParticle particle ) {
        assert particle != null;
        for ( Point2D point2D : shellLocations.keySet() ) {
            if ( shellLocations.get( point2D ) == particle ) {
                return point2D;
            }
        }
        return null;
    }

    /**
     * Get the electron that is closest, based on its location within the
     * electron shell, to the supplied point.
     *
     * @param point2d
     * @return - A reference to the closest electron, null if there are no
     * electrons in the shell.
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
     * Get a list of the open shell locations, i.e. locations that are able
     * to hold an electron but currently do not contain one.
     *
     * @return - List of open locations, which will be empty if the shell is
     * completely full.
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

    public void setCenterLocation( double x, double y){
        centerLocation.setLocation( x, y );
        notifyObservers();
    }

    public void setCenterLocation( Point2D newLocation ){
        centerLocation.setLocation( newLocation.getX(), newLocation.getY() );
    }

    public Point2D getCenterLocation(){
        return new Point2D.Double( centerLocation.getX(), centerLocation.getY() );
    }

    protected boolean isFull() {
        return getOpenShellLocations().size() == 0;
    }

    protected boolean isEmpty() {
        return getOpenShellLocations().size() == shellLocations.size();
    }

    /**
     * Remove all electrons from this shell.
     */
    protected void reset() {
        if (getNumElectrons() > 0){
            for ( Electron electron : shellLocations.values() ) {
                if ( electron != null ) {
                    // This prevents memory leaks and incorrect notifications.
                    electron.removeListener( particleRemovalListener );
                }
            }
            for ( Point2D point2D : shellLocations.keySet() ) {
                shellLocations.put( point2D, null );
            }
            notifyObservers();
        }
    }

    /**
     * Add the supplied electron to this electron shell.
     *
     * @param electronToAdd - Reference to the newly added electron.
     * @param moveImmediately - Flag that indicates whether to move the
     * electron right away, or to start moving the electron continuously to
     * the target location, which results in an animation effect.
     */
    protected void addElectron( final Electron electronToAdd, boolean moveImmediately ) {
        Point2D shellLocation = findClosestOpenLocation( electronToAdd.getPosition() );
        if (shellLocation == null){
            System.err.println( getClass().getName() + " - Error: No space in shell." );
        }
        assert shellLocation != null;
        electronToAdd.setDestination( shellLocation );
        shellLocations.put( shellLocation, electronToAdd );
        electronToAdd.addListener( particleRemovalListener );

        // If the particle shouldn't animate, then it should move to its destination immediately.
        if ( moveImmediately ) {
            electronToAdd.moveToDestination();
        }
        notifyObservers();
    }

    /**
     * Remove the specified electron from the shell.
     *
     * @param electronToRemove
     * @return - Returns the removed electron if found, null if not.
     */
    public Electron removeElectron( Electron electronToRemove ) {
        assert shellLocations.containsValue( electronToRemove );
        assert electronToRemove != null;
        if ( shellLocations.containsValue( electronToRemove ) ){
            shellLocations.put( getKey( electronToRemove ), null );
            electronToRemove.removeListener( particleRemovalListener );
            notifyObservers();
            return electronToRemove;
        }
        else{
            return null;
        }
    }

    /**
     * Remove an arbitrary electron, doesn't matter which one.
     */
    public Electron removeElectron() {
        assert getNumElectrons() > 0;
        // Gets the first electron in the map and removes it.
        for ( Electron electron : shellLocations.values() ) {
            if ( electron != null ) {
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

    /**
     * Get a list of all the electrons that currently reside in this shell.
     */
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
