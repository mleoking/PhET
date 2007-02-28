/**
 * Class: SphereWallCollision
 * Class: edu.colorado.phet.lasers.model.collision
 * User: Ron LeMaster
 * Date: Mar 31, 2003
 * Time: 7:28:13 PM
 */
package edu.colorado.phet.physics.collision;

//import edu.colorado.phet.model.Vector2D;
//import edu.colorado.phet.model.body.Particle;
//import edu.colorado.phet.util.MathUtil;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.model.Particle;

import java.awt.geom.Point2D;

public class SphereWallCollision extends HardsphereCollision {

    private SphericalBody sphere;
//    private Sphere sphere;
    private Wall wall;

    /**
     * Provided so subclasses can create and register prototypes with the
     * CollisionFactory
     */
    protected SphereWallCollision() {
        // NOP
    }

    public SphereWallCollision( SphericalBody sphere, Wall wall ) {
//    public SphereWallCollision( Sphere sphere, Wall wall ) {
        this.sphere = sphere;
        this.wall = wall;
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

        Point2D.Float p1 = new Point2D.Float( sphere.getPosition().getX(), sphere.getPosition().getY() );
        Point2D.Float p2 = new Point2D.Float( sphere.getPosition().getX() + dy, sphere.getPosition().getY() + dx );

        Point2D.Float intersection = MathUtil.getLinesIntersection( p1, p2, wall.getEnd1(), wall.getEnd2() );
        wall.setPosition( intersection );
        wall.setPosition( intersection );

        super.collide( sphere, wall, getLoa( sphere, wall ) );
    }


    /**
     *
     * @param particleA
     * @param particleB
     * @return
     */
    public Collision createIfApplicable( Particle particleA, Particle particleB ) {
        Collision result = null;
        if( particleA instanceof SphericalBody && particleB instanceof Wall ) {
//        if( particleA instanceof Sphere && particleB instanceof Wall ) {
            result = new SphereWallCollision( (SphericalBody)particleA, (Wall)particleB );
//            result = new SphereWallCollision( (Sphere)particleA, (Wall)particleB );
        }
        if( particleB instanceof SphericalBody && particleA instanceof Wall ) {
            result = new SphereWallCollision( (SphericalBody)particleB, (Wall)particleA );
        }
        return result;
    }

    //
    // Static fields and methods
    //
    private static Vector2D loa = new Vector2D.Float( );
//    private static Vector2D loa = new Vector2D( );
    static public void register() {
        CollisionFactory.addPrototype( new SphereWallCollision() );
    }
}
