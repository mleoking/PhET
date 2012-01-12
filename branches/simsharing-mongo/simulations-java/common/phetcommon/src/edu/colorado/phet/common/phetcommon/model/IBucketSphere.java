//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * This is a spherical object that can be put in a SphereBucket
 *
 * @param <U> U is the concrete class of the particle
 */
public interface IBucketSphere<U extends IBucketSphere> {

    double getRadius();

    /*---------------------------------------------------------------------------*
    * current position
    *----------------------------------------------------------------------------*/

    ImmutableVector2D getPosition();

    void setPosition( ImmutableVector2D position );

    /*---------------------------------------------------------------------------*
    * destination (where the IBucketSphere will end up)
    *----------------------------------------------------------------------------*/

    ImmutableVector2D getDestination();

    void setDestination( ImmutableVector2D destination );

    void setPositionAndDestination( ImmutableVector2D position );

    /*---------------------------------------------------------------------------*
    * position events. should fire if the IBucketSphere moves
    *----------------------------------------------------------------------------*/

    void addPositionListener( SimpleObserver observer );

    void removePositionListener( SimpleObserver observer );

    /*---------------------------------------------------------------------------*
    * general events (grabbed, dropped, removed) that the bucket needs to know
    *----------------------------------------------------------------------------*/

    void addListener( Listener<U> listener );

    void removeListener( Listener<U> listener );

    public static interface Listener<T extends IBucketSphere> {
        void grabbedByUser( T particle );

        void droppedByUser( T particle );

        void removedFromModel( T particle );
    }

    public static class Adapter<T extends IBucketSphere> implements Listener<T> {
        public void grabbedByUser( T particle ) {
        }

        public void droppedByUser( T particle ) {
        }

        public void removedFromModel( T particle ) {
        }
    }

}
