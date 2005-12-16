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
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.model.ion.IonIonCollision;

/**
 * IonIonCollisionExpert
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IonIonCollisionExpert implements CollisionExpert {
    private SphereSphereContactDetector contactDetector = new SphereSphereContactDetector();
    private BaseModel model;

    public IonIonCollisionExpert( BaseModel model ) {
        this.model = model;
    }

    public boolean detectAndDoCollision( Collidable bodyA, Collidable bodyB ) {
        boolean collisionOccured = false;

        if( bodyA instanceof Ion && bodyB instanceof Ion ) {
            Ion ionA = (Ion)bodyA;
            Ion ionB = (Ion)bodyB;

            // Conditions for collision:
            //      ions have opposite charges
            //      one of the ions is bound, but not both
            if( contactDetector.areInContact( ionA, ionB )
                && ionA.getCharge() * ionB.getCharge() < 0
                && ( ionA.isBound() || ionB.isBound() )
                && !( ionA.isBound() && ionB.isBound() ) ) {

                if( ionA.isBound() ) {
                    if( ionA.getBindingCrystal().addIon( ionB ) ) {
                        ionB.bindTo( ionA.getBindingCrystal() );
                    }
//                    ionA.getBindingCrystal().addIon( ionB );
                }
                else if( ionB.isBound() ) {
                    if( ionB.getBindingCrystal().addIon( ionA ) ) {
                        ionA.bindTo( ionB.getBindingCrystal() );
                    }
                }
                collisionOccured = true;
            }
            // If the ions are of like charge, then they should do a
            // hard-sphere collision
//            if( contactDetector.areInContact( ionA, ionB )
//                && ionA.getCharge() * ionB.getCharge() > 0
//                && ( ionA.isBound() || ionB.isBound() )
//                && !( ionA.isBound() && ionB.isBound() ) ) {
//                IonIonCollision collision = new IonIonCollision( (SphericalBody)bodyA,
//                                                                 (SphericalBody)bodyB );
//                collision.collide();
//                collisionOccured = true;
//            }

        }

        return collisionOccured;
    }
}
