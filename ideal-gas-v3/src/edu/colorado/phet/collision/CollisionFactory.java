/**
 * Class: CollisionFactory
 * Package: edu.colorado.phet.lasers.physics.collision
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.model.Particle;

import java.util.ArrayList;

public class CollisionFactory {

    //
    // Static fields and methods
    //
    static private ArrayList collisionPrototypes = new ArrayList();

    static public void addPrototype( Collision collision ) {
        collisionPrototypes.add( collision );
    }

    static public Collision create( Particle particleA, Particle particleB ) {
        Collision result = null;
        for( int i = 0; i < collisionPrototypes.size() && result == null; i++ ) {
            Collision collisionPrototype = (Collision)collisionPrototypes.get( i );
            result = collisionPrototype.createIfApplicable( particleA, particleB );
        }
        return result;
    }
}
