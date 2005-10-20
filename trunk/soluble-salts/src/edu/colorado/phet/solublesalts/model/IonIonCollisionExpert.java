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
import edu.colorado.phet.collision.SphereSphereContactDetector;
import edu.colorado.phet.common.model.BaseModel;

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
                    ionB.bindTo( ionA.getBindingLattice() );
                    ionA.getBindingLattice().addIon( ionB );
                }
                else if( ionB.isBound() ) {
                    ionB.bindTo( ionA.getBindingLattice() );
                    ionB.getBindingLattice().addIon( ionA );
                }
//                Lattice lattice = new SaltLattice( model );
//                lattice.addIon( ionA );
//                lattice.addIon( ionB );
//                model.addModelElement( lattice );
                collisionOccured = true;
            }
        }

        return collisionOccured;
    }
}
