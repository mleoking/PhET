// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.collision.Collidable;
import edu.colorado.phet.common.collision.CollisionExpert;
import edu.colorado.phet.common.collision.ContactDetector;
import edu.colorado.phet.common.collision.SphereBoxExpert;
import edu.colorado.phet.solublesalts.model.crystal.Crystal;
import edu.colorado.phet.solublesalts.model.ion.Ion;

/**
 * IonVesselCollisionExpert
 * <p/>
 * Has the same behavior as a SphereBoxExpert,
 * with the additional behavior that on some collisions, the ion may stick to
 * the vessel, based on an "affinity" between the two. The affinity is
 * determined by a strategy plugged into the collision expert.
 * <p/> Will not let two ions of the same type bind within one diameter of each other
 *
 * @author Ron LeMaster
 */
public class CrystalVesselCollisionExpert implements CollisionExpert, ContactDetector {

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private SphereBoxExpert sphereBoxExpert = new SphereBoxExpert();

    public CrystalVesselCollisionExpert() {
    }

    public boolean detectAndDoCollision( Collidable bodyA, Collidable bodyB ) {
        boolean collisionOccurred = false;
        Crystal crystal = null;
        Vessel vessel = null;
        if ( applies( bodyA, bodyB ) ) {

            crystal = (Crystal) ( bodyA instanceof Crystal ? bodyA : bodyB );
            vessel = (Vessel) ( bodyA instanceof Vessel ? bodyA : bodyB );

            // Only do something if the crystal is moving
            if ( crystal.getVelocity().magnitudeSquared() != 0 ) {
                double dx = 0;
                double dy = 0;
                Rectangle2D vesselBounds = vessel.getShape();
                // Check the east, west, and south boundaries of the vessel. Note that there is no north wall
                // to the vessel
                Ion eastmostIon = crystal.getExtremeIon( Crystal.EAST );
                if ( eastmostIon.getPosition().getX() + eastmostIon.getRadius() >= vesselBounds.getMaxX() ) {
                    dx = vessel.getWater().getMaxX() - eastmostIon.getPosition().getX() - eastmostIon.getRadius();
                    handleCrystalVesselCollision( eastmostIon );
                    collisionOccurred = true;
                }
                Ion southmostIon = crystal.getExtremeIon( Crystal.SOUTH );
                if ( southmostIon.getPosition().getY() + southmostIon.getRadius() >= vesselBounds.getMaxY() ) {
                    dy = vessel.getWater().getMaxY() - southmostIon.getPosition().getY() - southmostIon.getRadius();
                    handleCrystalVesselCollision( southmostIon );
                    collisionOccurred = true;
                }
                Ion westmostIon = crystal.getExtremeIon( Crystal.WEST );
                if ( westmostIon.getPosition().getX() - westmostIon.getRadius() <= vesselBounds.getMinX() ) {
                    dx = vessel.getWater().getMinX() - westmostIon.getPosition().getX() + westmostIon.getRadius();
                    handleCrystalVesselCollision( westmostIon );
                    collisionOccurred = true;
                }
                if ( collisionOccurred ) {
                    // Set twice so that the previous acceleration and velocity will be zeroed, too
                    crystal.setVelocity( 0, 0 );
                    crystal.setVelocity( 0, 0 );
                    crystal.setAcceleration( 0, 0 );
                    crystal.setAcceleration( 0, 0 );
                    crystal.translate( dx, dy );
                    crystal.setBound( true );
                }
            }
        }
        return collisionOccurred;
    }

    /**
     * Stops the crystal and resets its seed to be the ion that's in contact with the
     * vessel.
     *
     * @param ion
     */
    private void handleCrystalVesselCollision( Ion ion ) {
        // todo: need something here for setting the seed on a crystal that comes out of the shaker. Mayeb
        // have it set the one with maxY as the seed.
        ion.getBindingCrystal().setVelocity( 0, 0 );
        ion.getBindingCrystal().setAcceleration( 0, 0 );
        ion.getBindingCrystal().setSeed( ion );
    }

    public boolean areInContact( Collidable bodyA, Collidable bodyB ) {
        return sphereBoxExpert.areInContact( bodyA, bodyB );
    }

    public boolean applies( Collidable bodyA, Collidable bodyB ) {
        return ( bodyA instanceof Crystal && bodyB instanceof Vessel
                 || bodyB instanceof Crystal && bodyA instanceof Vessel );
    }
}
