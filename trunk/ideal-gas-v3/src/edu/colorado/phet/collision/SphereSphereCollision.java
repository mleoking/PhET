/**
 * Class: SphereSphereCollision
 * Class: edu.colorado.phet.physics.collision
 * User: Ron LeMaster
 * Date: Apr 4, 2003
 * Time: 10:28:08 AM
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.SphericalBody;

import java.awt.geom.Point2D;

/**
 * Note: This class is not thread-safe!!!!!
 */
public class SphereSphereCollision extends HardsphereCollision {

    private static Vector2D loa = new Vector2D.Double();

    private SphericalBody sphere1;
    private SphericalBody sphere2;
    private double dt;
    private IdealGasModel model;

    /**
     * Provided so class can register a prototype with the CollisionFactory
     */
    private SphereSphereCollision() {
        //NOP
    }

    protected Vector2D getLoa( Particle particleA, Particle particleB ) {
        Point2D posA = particleA.getPosition();
        Point2D posB = particleB.getPosition();
        loa.setX( posA.getX() - posB.getX() );
        loa.setY( posA.getY() - posB.getY() );
        return loa;
    }

    public void collide() {
        super.collide( sphere1, sphere2, getLoa( sphere1, sphere2 ), dt, model );
    }

    /**
     * @param particleA
     * @param particleB
     * @return
     */
    public Collision createIfApplicable( Particle particleA, Particle particleB,
                                         IdealGasModel model, double dt ) {
        instance.model = model;
        instance.dt = dt;
        Collision result = null;
        if( particleA instanceof SphericalBody && particleB instanceof SphericalBody ) {
            instance.sphere1 = (SphericalBody)particleA;
            instance.sphere2 = (SphericalBody)particleB;
            result = instance;
        }
        return result;
    }

    //
    // Static fields and methods
    //
    private static SphereSphereCollision instance = new SphereSphereCollision();

    static public void register() {
        CollisionFactory.addPrototype( new SphereSphereCollision() );
    }
}
