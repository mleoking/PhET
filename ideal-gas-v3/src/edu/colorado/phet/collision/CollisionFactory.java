/**
 * Class: CollisionFactory
 * Package: edu.colorado.phet.lasers.physics.collision
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.idealgas.model.IdealGasModel;

import java.util.ArrayList;
import java.util.List;

public class CollisionFactory {

    static private ArrayList collisionPrototypes = new ArrayList();

    static public void addPrototype( Collision collision ) {
        collisionPrototypes.add( collision );
    }

    static public Collision create( CollidableBody particleA, CollidableBody particleB,
                                    //    static public Collision create( Particle particleA, Particle particleB,
                                    IdealGasModel model, double dt ) {
        Collision result = null;
        for( int i = 0; i < collisionPrototypes.size() && result == null; i++ ) {
            Collision collisionPrototype = (Collision)collisionPrototypes.get( i );
            result = collisionPrototype.createIfApplicable( particleA, particleB, model, dt );
        }
        return result;
    }

    static ArrayList resultList = new ArrayList();

    public static List createCollisions( CollidableBody body1, CollidableBody body2, IdealGasModel model, double dt ) {
        resultList.clear();
        for( int i = 0; i < collisionPrototypes.size(); i++ ) {
            Collision collisionPrototype = (Collision)collisionPrototypes.get( i );
            Collision collision = collisionPrototype.createIfApplicable( body1, body2, model, dt );
            if( collision != null ) {
                resultList.add( collision );
            }
        }
        return resultList;
    }
}
