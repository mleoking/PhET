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

import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollisionExpert;
import edu.colorado.phet.collision.ContactDetector;
import edu.colorado.phet.collision.SphereBoxExpert;

/**
 * IonVesselCollisionExpert
 * <p/>
 * Has the same behavior as a SphereBoxExpert, with the additional behavior that
 * on some collisions, the ion may stick to the vessel, based on an "affinity" between
 * the two. The affinity is determined by a strategy plugged into the collision expert.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IonVesselCollisionExpert implements CollisionExpert, ContactDetector {

    private SphereBoxExpert sphereBoxExpert = new SphereBoxExpert();

    public boolean detectAndDoCollision( Collidable bodyA, Collidable bodyB ) {
        Ion ion = null;
        Vessel vessel = null;
        if( applies( bodyA, bodyB ) ) {
            ion = (Ion)( bodyA instanceof Ion ? bodyA : bodyB );
            vessel = (Vessel)( bodyA instanceof Vessel ? bodyA : bodyB );
            if( areInContact( ion, vessel.getWater() ) ) {
                if( vessel.getIonStickAffinity().stick( ion, vessel ) ) {
                    vessel.bind( ion );
                }
                else {
                    sphereBoxExpert.detectAndDoCollision( ion, vessel.getWater() );
                }
            }
        }
        return false;
    }

    public boolean areInContact( Collidable bodyA, Collidable bodyB ) {
        return sphereBoxExpert.areInContact( bodyA, bodyB );
    }

    public boolean applies( Collidable bodyA, Collidable bodyB ) {
        return ( bodyA instanceof Ion && bodyB instanceof Vessel
                 || bodyB instanceof Ion && bodyA instanceof Vessel );
    }
}
