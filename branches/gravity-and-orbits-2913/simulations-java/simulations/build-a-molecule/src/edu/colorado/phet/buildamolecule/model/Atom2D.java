// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.util.ArrayList;
import java.util.HashSet;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.IBucketSphere;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * An atom, extended with position/destination information that is animated
 *
 * @author Sam Reid
 * @author John Blanco
 * @author Jonathan Olson
 */
public class Atom2D extends Atom implements IBucketSphere<Atom2D> {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private static final double MOTION_VELOCITY = 800; // In picometers per second of sim time.

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final String name;
    public final Property<ImmutableVector2D> position;
    private final Property<Boolean> userControlled = new Property<Boolean>( false );//True if the particle is being dragged by the user
    private final HashSet<IBucketSphere.Listener<Atom2D>> listeners = new HashSet<IBucketSphere.Listener<Atom2D>>();
    private ImmutableVector2D destination = new ImmutableVector2D();

    public final Property<Boolean> visible = new Property<Boolean>( true ); // invisible for instance when in a collection box

    // Listener to the clock, used for motion.
    private final ClockAdapter clockListener = new ClockAdapter() {
        @Override
        public void clockTicked( ClockEvent clockEvent ) {
            stepInTime( clockEvent.getSimulationTimeChange() );
        }
    };

    // Reference to the clock.
    private final IClock clock;

    public Atom2D( Element element, IClock clock ) {
        super( element );
        this.clock = clock;
        this.name = BuildAMoleculeStrings.getAtomName( element );
        position = new Property<ImmutableVector2D>( new ImmutableVector2D() );
        destination = position.get();
        addedToModel(); // Assume that this is initially an active part of the model.
        userControlled.addObserver( new SimpleObserver() {
            public void update() {
                ArrayList<IBucketSphere.Listener<Atom2D>> copy = new ArrayList<IBucketSphere.Listener<Atom2D>>( listeners );//ConcurrentModificationException if listener removed while iterating, so use a copy
                if ( userControlled.get() ) {
                    for ( IBucketSphere.Listener<Atom2D> listener : copy ) {
                        listener.grabbedByUser( Atom2D.this );
                    }
                }
                else {
                    for ( IBucketSphere.Listener<Atom2D> listener : copy ) {
                        listener.droppedByUser( Atom2D.this );
                    }
                }
            }
        } );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    public void addListener( IBucketSphere.Listener<Atom2D> listener ) {
        listeners.add( listener );
    }

    public void removeListener( IBucketSphere.Listener<Atom2D> listener ) {
        listeners.remove( listener );
    }

    public PBounds getPositionBounds() {
        return new PBounds( position.get().getX() - getRadius(), position.get().getY() - getRadius(), getDiameter(), getDiameter() );
    }

    public PBounds getDestinationBounds() {
        return new PBounds( destination.getX() - getRadius(), destination.getY() - getRadius(), getDiameter(), getDiameter() );
    }

    private void stepInTime( double dt ) {
        if ( getPosition().getDistance( destination ) != 0 ) {
            // Move towards the current destination.
            double distanceToTravel = MOTION_VELOCITY * dt;
            double distanceToTarget = getPosition().getDistance( destination );

            double farDistanceMultiple = 10; // if we are this many times away, we speed up

            // if we are far from the target, let's speed up the velocity
            if ( distanceToTarget > distanceToTravel * farDistanceMultiple ) {
                double extraDistance = distanceToTarget - distanceToTravel * farDistanceMultiple;
                distanceToTravel *= 1 + extraDistance / 300;
            }

            if ( distanceToTravel >= distanceToTarget ) {
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

    public ImmutableVector2D getPosition() {
        return position.get();
    }

    public ImmutableVector2D getDestination() {
        return destination;
    }

    public void setPosition( ImmutableVector2D point ) {
        position.set( point );
    }

    public void setPosition( double x, double y ) {
        position.set( new ImmutableVector2D( x, y ) );
    }

    public void setDestination( ImmutableVector2D point ) {
        destination = point;
    }

    public void translatePositionAndDestination( ImmutableVector2D delta ) {
        setPosition( getPosition().plus( delta ) );
        setDestination( getDestination().plus( delta ) );
    }

    public void setPositionAndDestination( ImmutableVector2D point ) {
        setPosition( point );
        setDestination( point );
    }

    public String getName() {
        return name;
    }

    public boolean isUserControlled() {
        return userControlled.get();
    }

    public void setUserControlled( boolean userControlled ) {
        this.userControlled.set( userControlled );
    }

    public void translate( double dx, double dy ) {
        setPosition( position.get().getX() + dx, position.get().getY() + dy );
    }

    public void translate( ImmutableVector2D vector2D ) {
        translate( vector2D.getX(), vector2D.getY() );
    }

    public void reset() {
        position.reset();
        destination = position.get();
        userControlled.reset();
        visible.reset();
    }

    /**
     * This method should be called when this particle is removed from the
     * model.  It causes the particle to send out notifications of its
     * removal, which the view will need to receive in order to remove the
     * representation.
     */
    public void removedFromModel() {
        if ( clock != null ) {
            clock.removeClockListener( clockListener );
        }
        ArrayList<IBucketSphere.Listener<Atom2D>> copyOfListeners = new ArrayList<IBucketSphere.Listener<Atom2D>>( listeners );
        for ( IBucketSphere.Listener<Atom2D> listener : copyOfListeners ) {
            listener.removedFromModel( this );
        }
    }

    /**
     * Call this when adding this element to the model, or when re-adding
     * after having removed it.
     */
    public void addedToModel() {
        if ( clock != null ) {
            clock.addClockListener( clockListener );
        }
    }

    public void addPositionListener( SimpleObserver listener ) {
        position.addObserver( listener );
    }

    public void removePositionListener( SimpleObserver listener ) {
        position.removeObserver( listener );
    }

    // -----------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    public static interface Listener extends IBucketSphere.Listener<Atom2D> {
    }

    public static class Adapter extends IBucketSphere.Adapter<Atom2D> implements Listener {
    }

}
