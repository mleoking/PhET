/**
 * Class: SphereSphereCollision
 * Class: edu.colorado.phet.physics.collision
 * User: Ron LeMaster
 * Date: Apr 4, 2003
 * Time: 10:28:08 AM
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.SphericalBody;

import java.awt.geom.Point2D;

/**
 * Note: This class is not thread-safe!!!!!
 */
public class SphereSphereCollision extends HardsphereCollision {

    private static Vector2D loa = new Vector2D.Double();

    private SphericalBody sphereA;
    private SphericalBody sphereB;
    private double dt;
    private IdealGasModel model;

    public SphereSphereCollision( SphericalBody sphereA, SphericalBody sphereB, 
                                  IdealGasModel model, double dt ) {
        this.sphereA = sphereA;
        this.sphereB = sphereB;
        this.model = model;
        this.dt = dt;
    }

    protected Vector2D getLoa( CollidableBody particleA, CollidableBody particleB ) {
        Point2D posA = particleA.getPosition();
        Point2D posB = particleB.getPosition();
        loa.setX( posA.getX() - posB.getX() );
        loa.setY( posA.getY() - posB.getY() );
        return loa;
    }

    public void collide() {
        super.collide( sphereA, sphereB, getLoa( sphereA, sphereB ), dt, model );
    }
}
