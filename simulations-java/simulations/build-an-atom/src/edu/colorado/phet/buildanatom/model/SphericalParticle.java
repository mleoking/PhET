// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Abstract base class from which spherical particles, which can be anything
 * from a proton to an atom (or a round rock, for that matter) extend.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public abstract class SphericalParticle {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private static final double MOTION_VELOCITY = 120; // In picometers per second of sim time.

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final double radius;
    private final Property<Point2D.Double> position;
    private final Property<Boolean> userControlled=new Property<Boolean>( false );//True if the particle is being dragged by the user
    private final HashSet<Listener> listeners =new HashSet<Listener>( );
    private final Point2D destination = new Point2D.Double();

    // Listener to the clock, used for motion.
    private final ClockAdapter clockListener = new ClockAdapter() {
        @Override
        public void clockTicked( ClockEvent clockEvent ) {
            stepInTime( clockEvent.getSimulationTimeChange() );
        }
    };

    // Reference to the clock.
    private final ConstantDtClock clock;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public SphericalParticle( double radius, double x, double y, ConstantDtClock clock ) {
        this.clock = clock;
        this.radius = radius;
        position = new Property<Point2D.Double>( new Point2D.Double( x, y ) );
        this.destination.setLocation( x, y );
        addedToModel(); // Assume that this is initially an active part of the model.
        userControlled.addObserver( new SimpleObserver() {
            public void update() {
                ArrayList<Listener> copy = new ArrayList<Listener>( listeners );//ConcurrentModificationException if listener removed while iterating, so use a copy
                if (userControlled.getValue()){
                    for ( Listener listener : copy ) {
                        listener.grabbedByUser( SphericalParticle.this );
                    }
                }else{
                    for ( Listener listener : copy ) {
                        listener.droppedByUser( SphericalParticle.this );
                    }
                }
            }
        } );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    public void addListener(Listener listener) {
        listeners.add( listener );
    }
    public void removeListener(Listener listener){
        listeners.remove( listener );
    }

    /**
     * @param dt
     */
    private void stepInTime( double dt ) {
        if ( getPosition().distance( destination ) != 0 ) {
            // Move towards the current destination.
            double distanceToTravel = MOTION_VELOCITY * dt;
            if ( distanceToTravel >= getPosition().distance( destination ) ) {
                // Closer than one step, so just go there.
                setPosition( destination );
            }
            else {
                // Move towards the destination.
                double angle = Math.atan2( destination.getY() - getPosition().getY(),
                        destination.getX() - getPosition().getX() );
                translate( distanceToTravel * Math.cos( angle ), distanceToTravel * Math.sin( angle ) );
            }
        }
    }

    public Point2D getPosition() {
        return position.getValue();
    }

    public Point2D getDestination() {
        return new Point2D.Double( destination.getX(), destination.getY() );
    }

    public void setPosition( Point2D position ) {
        setPosition( position.getX(), position.getY() );
    }

    public void setPosition( double x, double y ) {
        position.setValue( new Point2D.Double( x, y ) );
    }

    public void setDestination( Point2D position ) {
        setDestination( position.getX(), position.getY() );
    }

    public void setDestination( double x, double y ) {
        destination.setLocation( x, y );
    }

    public void setPositionAndDestination( double x, double y ) {
        setPosition( x, y );
        setDestination( x, y );
    }

    public void setPositionAndDestination( Point2D p ) {
        setPosition( p );
        setDestination( p );
    }

    public double getDiameter() {
        return getRadius() * 2;
    }

    public double getRadius() {
        return radius;
    }

    public boolean isUserControlled() {
        return userControlled.getValue();
    }

    public void setUserControlled( boolean userControlled ) {
        this.userControlled.setValue( userControlled );
    }

    public void translate( double dx, double dy ) {
        setPosition( position.getValue().getX() + dx, position.getValue().getY() + dy );
    }

    public void translate( ImmutableVector2D vector2D ) {
        translate( vector2D.getX(), vector2D.getY() );
    }

    /**
     * This method should be called when this particle is removed from the
     * model.  It causes the particle to send out notifications of its
     * removal, which the view will need to receive in order to remove the
     * representation.
     */
    public void removedFromModel() {
        if ( clock != null ){
            clock.removeClockListener( clockListener );
        }
        ArrayList<Listener> copyOfListeners = new ArrayList<Listener>( listeners );
        for ( Listener listener : copyOfListeners ) {
            listener.removedFromModel( this );
        }
    }

    /**
     * Call this when adding this element to the model, or when re-adding
     * after having removed it.
     */
    public void addedToModel(){
        if ( clock != null ){
            clock.addClockListener( clockListener );
        }
    }

    public void addPositionListener( SimpleObserver listener ) {
        position.addObserver( listener );
    }

    public void removePositionListener( SimpleObserver listener ) {
        position.removeObserver( listener );
    }

    public void moveToDestination() {
        setPosition( getDestination() );
    }

    // -----------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    public static interface Listener {
        void grabbedByUser( SphericalParticle particle );
        void droppedByUser( SphericalParticle particle );
        void removedFromModel( SphericalParticle particle );
    }

    public static class Adapter implements Listener{
        public void grabbedByUser( SphericalParticle particle ) {}
        public void droppedByUser( SphericalParticle particle ) {}
        public void removedFromModel( SphericalParticle particle ) {}
    }
}
