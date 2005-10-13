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
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IonVesselCollisionExpert implements CollisionExpert, ContactDetector {

    private SphereBoxExpert sphereBoxExpert = new SphereBoxExpert();
    private Random random = new Random( System.currentTimeMillis() );
    private double affinity = .2;


    public boolean detectAndDoCollision( Collidable bodyA, Collidable bodyB ) {
        Ion ion = null;
        Box2D box = null;
        if( applies( bodyA, bodyB ) && areInContact( bodyA, bodyB ) ) {
            ion = (Ion)( bodyA instanceof Ion ? bodyA : bodyB );
            box = (Box2D)( bodyA instanceof Box2D ? bodyA : bodyB );
            if( random.nextDouble() <= affinity &&
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
}
