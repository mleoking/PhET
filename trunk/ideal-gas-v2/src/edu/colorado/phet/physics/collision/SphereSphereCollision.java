/**
 * Class: SphereSphereCollision
 * Class: edu.colorado.phet.physics.collision
 * User: Ron LeMaster
 * Date: Apr 4, 2003
 * Time: 10:28:08 AM
 */
package edu.colorado.phet.physics.collision;

import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.physics.body.Particle;

/**
 * Note: This class is not thread-safe!!!!!
 */
public class SphereSphereCollision extends HardsphereCollision {

    private static Vector2D loa = new Vector2D( );

    private SphericalBody sphere1;
    private SphericalBody sphere2;
//    private Sphere sphere1;
//    private Sphere sphere2;

    /**
     * Provided so class can register a prototype with the CollisionFactory
     */
    private SphereSphereCollision() {
        //NOP
    }

    public SphereSphereCollision( SphericalBody sphere1, SphericalBody sphere2 ) {
//    public SphereSphereCollision( Sphere sphere1, Sphere sphere2 ) {
        this.sphere1 = sphere1;
        this.sphere2 = sphere2;
    }

    protected Vector2D getLoa( Particle particleA, Particle particleB ) {
        Vector2D posA = particleA.getPosition();
        Vector2D posB = particleB.getPosition();
        loa.setX( posA.getX() - posB.getX() );
        loa.setY( posA.getY() - posB.getY() );
        return loa;
//        return new Vector2D( posA.getX() - posB.getX(),
//                             posA.getY() - posB.getY() );
    }

    //
    // Abstract methods
    //
    public void collide() {
        super.collide( sphere1, sphere2, getLoa( sphere1, sphere2 ) );
    }

    /**
     *
     * @param particleA
     * @param particleB
     * @return
     */
    public Collision createIfApplicable( Particle particleA, Particle particleB ) {
        Collision result = null;
        if( particleA instanceof SphericalBody && particleB instanceof SphericalBody ) {
//            result = new SphereSphereCollision( (SphericalBody)particleA, (SphericalBody)particleB );
            instance.sphere1 = (SphericalBody)particleA;
            instance.sphere2 = (SphericalBody)particleB;
            result = instance;
        }
        return result;
    }

    //
    // Static fields and methods
    //
    private static SphereSphereCollision instance = new SphereSphereCollision( );
    static public void register() {
        CollisionFactory.addPrototype( new SphereSphereCollision() );
    }
}
