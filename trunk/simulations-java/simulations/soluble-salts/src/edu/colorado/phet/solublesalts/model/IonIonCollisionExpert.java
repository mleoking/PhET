// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model;

import edu.colorado.phet.common.collision.Collidable;
import edu.colorado.phet.common.collision.CollisionExpert;
import edu.colorado.phet.common.collision.SphereSphereContactDetector;
import edu.colorado.phet.solublesalts.model.ion.Ion;

/**
 * IonIonCollisionExpert
 *
 * @author Ron LeMaster
 */
public class IonIonCollisionExpert implements CollisionExpert {
    private SphereSphereContactDetector contactDetector = new SphereSphereContactDetector();
    private SolubleSaltsModel model;

    public IonIonCollisionExpert( SolubleSaltsModel model ) {
        this.model = model;
    }

    public boolean detectAndDoCollision( Collidable bodyA, Collidable bodyB ) {
        boolean collisionOccured = false;

        if ( bodyA instanceof Ion && bodyB instanceof Ion ) {
            Ion ionA = (Ion) bodyA;
            Ion ionB = (Ion) bodyB;

            // Conditions for collision:
            //      ions have opposite charges
            //      one of the ions is bound, but not both
            if ( contactDetector.areInContact( ionA, ionB )
                 && ionA.getCharge() * ionB.getCharge() < 0
                 && ( ionA.isBound() || ionB.isBound() )
                 && !( ionA.isBound() && ionB.isBound() )
                 && model.isNucleationEnabled() ) {

                if ( ionA.isBound() ) {
                    ionA.getBindingCrystal().addIonNextToIon( ionB, ionA );
                }
                else if ( ionB.isBound() ) {
                    ionB.getBindingCrystal().addIonNextToIon( ionA, ionB );
                }
                collisionOccured = true;
            }
        }

        return collisionOccured;
    }
}
