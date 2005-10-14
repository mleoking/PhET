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
import edu.colorado.phet.common.math.Vector2D;

/**
 * IonIonCollisionExpert
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IonIonCollisionExpert implements CollisionExpert {
    private SphereSphereContactDetector detector = new SphereSphereContactDetector();

    public boolean detectAndDoCollision( Collidable bodyA, Collidable bodyB ) {
        boolean collisionOccured = false;

        if( bodyA instanceof Ion && bodyB instanceof Ion ) {
            Ion ionA = (Ion)bodyA;
            Ion ionB = (Ion)bodyB;
            if( detector.areInContact( ionA, ionB )
                && ionA.getCharge() * ionB.getCharge() < 0 ) {
                Vector2D netMomentum = null;
                if( ionA.isBound() || ionB.isBound() ) {
                    netMomentum = new Vector2D.Double();
                    ionA.setMomentum( netMomentum );
                    ionB.setMomentum( netMomentum );
                }
                else {
                    netMomentum = new Vector2D.Double( ionA.getMomentum() ).add( ionB.getMomentum() );
                    double netMass = ionA.getMass() + ionB.getMass();
                    ionA.setMomentum( new Vector2D.Double( netMomentum ).scale( ( ionA.getMass() / netMass ) ) );
                    ionB.setMomentum( new Vector2D.Double( netMomentum ).scale( ( ionB.getMass() / netMass ) ) );
                }
//                ionA.setMomentum( new Vector2D.Double( netMomentum ).scale( ( ionA.getMass() / netMass ) ) );
//                ionB.setMomentum( new Vector2D.Double( netMomentum ).scale( ( ionB.getMass() / netMass ) ) );
            }
        }

        return collisionOccured;
    }
}
