/**
 * Class: SphereWallCollision
 * Class: edu.colorado.phet.lasers.physics.collision
 * User: Ron LeMaster
 * Date: Mar 31, 2003
 * Time: 7:28:13 PM
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.SphericalBody;
import edu.colorado.phet.idealgas.model.Wall;

import java.awt.geom.Point2D;

public class SphereWallCollision extends HardsphereCollision {

    private SphericalBody sphere;
    private Wall wall;
    private IdealGasModel model;
    private double dt;

    /**
     * Provided so subclasses can create and register prototypes with the
     * CollisionFactory
     */
    protected SphereWallCollision() {
        // NOP
    }

    public SphereWallCollision( SphericalBody sphere, Wall wall,
                                IdealGasModel model, double dt ) {
//    public SphereWallCollision( Sphere sphere, Wall wall ) {
        this.sphere = sphere;
        this.wall = wall;
        this.model = model;
        this.dt = dt;
    }

    //
    // Interfaces implemented
    //
    protected Vector2D getLoa( Particle particleA, Particle particleB ) {
        Wall wall = particleA instanceof Wall ? (Wall)particleA : (Wall)particleB;
        loa.setX( (float)( wall.getEnd1().getY() - wall.getEnd2().getY() ));
        loa.setY( (float)( wall.getEnd1().getX() - wall.getEnd2().getX() ));
//        Vector2D loa = new Vector2D( (float)( wall.getEnd1().getY() - wall.getEnd2().getY() ),
//                                     (float)( wall.getEnd1().getX() - wall.getEnd2().getX() ));
        return loa;
    }

    public void collide() {

        // Set the "position" of the wall to be the point where the LOA goes through the wall.
        float dx = (float)( wall.getEnd1().getX() - wall.getEnd2().getX() );
        float dy = (float)( wall.getEnd1().getY() - wall.getEnd2().getY() );

        Point2D.Double p1 = new Point2D.Double( sphere.getPosition().getX(), sphere.getPosition().getY() );
        Point2D.Double p2 = new Point2D.Double( sphere.getPosition().getX() + dy, sphere.getPosition().getY() + dx );

        Point2D.Double intersection = MathUtil.getLinesIntersection( p1, p2, wall.getEnd1(), wall.getEnd2() );
        wall.setPosition( intersection );
        wall.setPosition( intersection );

        super.collide( sphere, wall, getLoa( sphere, wall ), dt, model  );
    }


    /**
     *
     * @param particleA
     * @param particleB
     * @return
     */
    public Collision createIfApplicable( Particle particleA, Particle particleB,
                                         IdealGasModel model, double dt) {
        Collision result = null;
        if( particleA instanceof SphericalBody && particleB instanceof Wall ) {
//        if( particleA instanceof Sphere && particleB instanceof Wall ) {
            result = new SphereWallCollision( (SphericalBody)particleA, (Wall)particleB,
                                              model, dt );
//            result = new SphereWallCollision( (Sphere)particleA, (Wall)particleB );
        }
        if( particleB instanceof SphericalBody && particleA instanceof Wall ) {
            result = new SphereWallCollision( (SphericalBody)particleB, (Wall)particleA,
                                              model, dt);
        }
        return result;
    }

    //
    // Static fields and methods
    //
    private static Vector2D loa = new Vector2D.Double();
    static public void register() {
        CollisionFactory.addPrototype( new SphereWallCollision() );
    }
}
