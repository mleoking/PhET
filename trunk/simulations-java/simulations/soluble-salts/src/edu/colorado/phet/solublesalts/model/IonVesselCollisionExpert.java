// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.solublesalts.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import edu.colorado.phet.common.collision.Collidable;
import edu.colorado.phet.common.collision.CollisionExpert;
import edu.colorado.phet.common.collision.ContactDetector;
import edu.colorado.phet.common.collision.SphereBoxExpert;
import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.solublesalts.model.ion.Chlorine;
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
public class IonVesselCollisionExpert implements CollisionExpert, ContactDetector {

    //----------------------------------------------------------------
    // Class fields
    //----------------------------------------------------------------

    private static double DEFAULT_MIN_DIST_TO_LIKE_ION;

    static {
        DEFAULT_MIN_DIST_TO_LIKE_ION = new Chlorine( new Point2D.Double(),
                                                     new MutableVector2D(),
                                                     new MutableVector2D() ).getRadius() * 4;
    }

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private SphereBoxExpert sphereBoxExpert = new SphereBoxExpert();
    private SolubleSaltsModel model;

    private double minDistToLikeIon = DEFAULT_MIN_DIST_TO_LIKE_ION;

    public IonVesselCollisionExpert( SolubleSaltsModel model ) {
        this.model = model;
    }

    public boolean detectAndDoCollision( Collidable bodyA, Collidable bodyB ) {
        boolean collisionOccurred = false;
        Ion ion = null;
        Vessel vessel = null;
        if ( applies( bodyA, bodyB ) ) {
            ion = (Ion) ( bodyA instanceof Ion ? bodyA : bodyB );
            vessel = (Vessel) ( bodyA instanceof Vessel ? bodyA : bodyB );

            if ( !vessel.isOutside( ion.getPosition() )
                 && ( areInContact( ion, vessel.getWater() )
                      || areInContact( ion, vessel ) ) ) {

                // If the ion isn't bound to a crystal, then create a new crystal, if all other
                // conditions are met
                if ( !ion.isBound() ) {
                    collisionOccurred = handleIonVesselCollision( ion, vessel );
                }
            }
        }
        return collisionOccurred;
    }

    /**
     * Performs a collision between an ion and a vessel, giving the ion the proper velocity. If
     * certain conditions are met, the ion sticks to the vessel as the seed of a new crystal.
     *
     * @param ion
     * @param vessel
     * @return If a collision occured between the ion and the vessel
     */
    private boolean handleIonVesselCollision( Ion ion, Vessel vessel ) {
        boolean collisionOccurred;
        // Check that nucleation is enabled in the model
        boolean canBind = model.isNucleationEnabled();

        // Make sure the ion isn't too close to another ion of the same polarity
        double minDist = minDistToLikeIon;
        List otherIons = model.getIons();
        for ( int i = 0; i < otherIons.size() && canBind; i++ ) {
            Ion testIon = (Ion) otherIons.get( i );
            if ( testIon.isBound()
                 && testIon.getPosition().distance( ion.getPosition() ) < minDist ) {
                canBind = false;
            }
        }

        if ( canBind && vessel.getIonStickAffinity().stick( ion, vessel ) ) {
            vessel.bind( ion );
        }

        // Perform the collision, even if we bind, so when the ion is
        // released it moves properly
        collisionOccurred = sphereBoxExpert.detectAndDoCollision( ion, vessel.getWater() );
        return collisionOccurred;
    }

    public boolean areInContact( Collidable bodyA, Collidable bodyB ) {
        return sphereBoxExpert.areInContact( bodyA, bodyB );
    }

    private boolean areInContact( Ion ion, Vessel vessel ) {
        Rectangle2D v = vessel.getShape();
        Point2D i = ion.getPosition();
        double r = ion.getRadius();
        boolean b = i.getX() + r >= v.getMaxX()
                    || i.getX() - r <= v.getMinX()
                    || i.getY() + r >= v.getMaxY();
        return b;
    }

    public boolean applies( Collidable bodyA, Collidable bodyB ) {
        return ( bodyA instanceof Ion && bodyB instanceof Vessel || bodyB instanceof Ion
                                                                    && bodyA instanceof Vessel );
    }
}
