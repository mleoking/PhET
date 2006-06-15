/**
 * Class: SphereSphereExpert
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 21, 2004
 */
package edu.colorado.phet.collision;

public class SphereSphereExpert implements CollisionExpert {
    private SphereSphereContactDetector detector = new SphereSphereContactDetector();

    public boolean detectAndDoCollision( Collidable bodyA, Collidable bodyB ) {
        boolean haveCollided = false;
        if( detector.applies( bodyA, bodyB ) && detector.areInContact( bodyA, bodyB )
            && tweakCheck( bodyA, bodyB ) ) {
            Collision collision = new SphereSphereCollision( (SphericalBody)bodyA,
                                                             (SphericalBody)bodyB );
            collision.collide();
            haveCollided = true;
        }
        return haveCollided;
    }

    /**
     * This check returns false if the two bodies were in contact during the previous
     * time step. Using this check to prevent a collision in such cases makes the
     * performance of the collision system much more natural looking.
     *
     * @param cbA
     * @param cbB
     * @return
     */
    private boolean tweakCheck( Collidable cbA, Collidable cbB ) {

//        if( true ) return true;

        SphericalBody sA = (SphericalBody)cbA;
        SphericalBody sB = (SphericalBody)cbB;

        double dPrev = sA.getPositionPrev().distance( sB.getPositionPrev() );
//        double dCurr = sA.getPosition().distance( sB.getPosition() );
//        return dPrev > dCurr;
        return dPrev > sA.getRadius() + sB.getRadius();
    }
}
