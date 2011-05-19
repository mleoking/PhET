// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro.model;

import java.awt.geom.Line2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * A single solid crystal (sugar or salt) that comes from a shaker and gets dissolved in the water.
 *
 * @author Sam Reid
 */
public class MacroCrystal {
    public final double mass = 1E-6;//kg
    public Property<ImmutableVector2D> position;
    public Property<ImmutableVector2D> velocity = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
    public Property<ImmutableVector2D> acceleration = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
    private ArrayList<VoidFunction0> removalListeners = new ArrayList<VoidFunction0>();

    private double moles;//The number of moles of the crystal.  We couldn't just count the number of atoms since it would overflow Long

    //True of the salt has landed on the floor of the beaker.  In this case it won't move anymore and will dissolve when liquid hits
    private boolean landed = false;

    //Length in m^3 of one side of the crystal, assuming it is perfectly cubic
    public final double length;

    public MacroCrystal( ImmutableVector2D position, double moles, double volumePerMole ) {
        this.position = new Property<ImmutableVector2D>( position );
        this.moles = moles;

        //Compute the length of a side
        double volume = volumePerMole * moles;
        length = Math.pow( volume, 1.0 / 3.0 );
    }

    //propagate the crystal according to the specified applied forces, using euler integration
    public void stepInTime( ImmutableVector2D appliedForce, double dt, Line2D.Double leftBeakerWall, Line2D.Double rightBeakerWall, Line2D.Double beakerFloor, Line2D.Double topOfSolid ) {
        if ( !landed ) {
            ImmutableVector2D originalPosition = position.get();

            acceleration.set( appliedForce.times( 1.0 / mass ) );
            velocity.set( velocity.get().plus( acceleration.get().times( dt ) ) );
            position.set( position.get().plus( velocity.get().times( dt ) ) );

            //Path that the particle took from previous time to current time, for purpose of collision detection with walls
            Line2D.Double path = new Line2D.Double( originalPosition.toPoint2D(), position.get().toPoint2D() );

            //if the particle bounced off a wall, then reverse its velocity
            if ( path.intersectsLine( leftBeakerWall ) ||
                 path.intersectsLine( rightBeakerWall ) ) {
                velocity.set( new ImmutableVector2D( Math.abs( velocity.get().getX() ), velocity.get().getY() ) );

                //Rollback the previous update, and go the other way
                position.set( originalPosition );
                position.set( position.get().plus( velocity.get().times( dt ) ) );
            }

            //Compute the new path after accounting for bouncing off walls
            Line2D.Double newPath = new Line2D.Double( originalPosition.toPoint2D(), position.get().toPoint2D() );

            //See if it should land on the floor of the beaker
            if ( newPath.intersectsLine( beakerFloor ) ) {
                position.set( new ImmutableVector2D( position.get().getX(), 0 ) );
                landed = true;
            }

            //See if it should land on top of any precipitated solid in the beaker
            else if ( newPath.intersectsLine( topOfSolid ) ) {
                //Move the crystal down a tiny bit so that it will be intercepted by the water on top of the solid precipitate when water is added
                position.set( new ImmutableVector2D( position.get().getX(), topOfSolid.getY1() - 1E-6 ) );
                landed = true;
            }
        }
    }

    //Add a listener which will be notified when this crystal is removed from the model
    public void addRemovalListener( VoidFunction0 removalListener ) {
        removalListeners.add( removalListener );
    }

    //Remove a removal listener
    public void removeRemovalListener( VoidFunction0 removalListener ) {
        removalListeners.remove( removalListener );
    }

    //Notify all removal listeners that this crystal is being removed from the model
    public void remove() {
        for ( VoidFunction0 removalListener : removalListeners ) {
            removalListener.apply();
        }
    }

    public double getMoles() {
        return moles;
    }
}
