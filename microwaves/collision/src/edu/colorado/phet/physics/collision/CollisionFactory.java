/**
 * Class: CollisionFactory
 * Package: edu.colorado.phet.lasers.physics.collision
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.physics.collision;

import edu.colorado.phet.coreadditions.Body;

import java.util.ArrayList;

public class CollisionFactory {

    private CollisionFactory() {

    }

    //
    // Static fields and methods
    //
    static private ArrayList collisionPrototypes = new ArrayList();

    static public void addPrototype( Collision collision ) {
        collisionPrototypes.add( collision );
    }

    static public Collision create( Body bodyA, Body bodyB ) {

        Collision result = null;
        for( int i = 0; i < collisionPrototypes.size() && result == null; i++ ) {
            Collision collisionPrototype = (Collision)collisionPrototypes.get( i );
            result = collisionPrototype.createIfApplicable( bodyA, bodyB );
        }

        return result;
    }
}
