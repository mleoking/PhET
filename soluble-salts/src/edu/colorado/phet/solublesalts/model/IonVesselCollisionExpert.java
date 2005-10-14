/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import edu.colorado.phet.collision.*;

import java.util.Random;

/**
 * IonVesselCollisionExpert
 * <p>
 * Has the same behavior as a SphereBoxExpert, with the additional behavior that
 * on some collisions, the ion may stick to the vessel, based on an "affinity" between
 * the two. The affinity is determined by a strategy plugged into the collision expert.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IonVesselCollisionExpert implements CollisionExpert, ContactDetector {

    private SphereBoxExpert sphereBoxExpert = new SphereBoxExpert();
    private Random random = new Random( System.currentTimeMillis() );
    private Affinity affinity = new RandomAffinity( .2 );


    public boolean detectAndDoCollision( Collidable bodyA, Collidable bodyB ) {
        Ion ion = null;
        Box2D box = null;
        if( applies( bodyA, bodyB ) && areInContact( bodyA, bodyB ) ) {
            ion = (Ion)( bodyA instanceof Ion ? bodyA : bodyB );
            box = (Box2D)( bodyA instanceof Box2D ? bodyA : bodyB );
            if( affinity.stick( ion, box ) &&
                !( ion.getPosition().getY() - ion.getRadius() <= box.getMinY() ) ) {
                ion.setVelocity( 0, 0 );
            }
            else {
                sphereBoxExpert.detectAndDoCollision( ion, box );
            }
        }
        return false;
    }

    public boolean areInContact( Collidable bodyA, Collidable bodyB ) {
        return sphereBoxExpert.areInContact( bodyA, bodyB );
    }

    public boolean applies( Collidable bodyA, Collidable bodyB ) {
        return ( bodyA instanceof Ion && bodyB instanceof Box2D
                 || bodyB instanceof Ion && bodyA instanceof Box2D );
    }

    //----------------------------------------------------------------
    // Affinities
    //----------------------------------------------------------------

    public interface Affinity {
        boolean stick( Ion ion, Box2D box );
    }

    public static class RandomAffinity implements Affinity {
        Random random = new Random( System.currentTimeMillis() );
        double affinityLikelihood;

        public RandomAffinity( double affinityLikelihood ) {
            this.affinityLikelihood = affinityLikelihood;
        }

        public boolean stick( Ion ion, Box2D box ) {
            return random.nextDouble() <= affinityLikelihood;
        }
    }
}
