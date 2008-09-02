/**
 * Class: SphereSphereExpert
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 21, 2004
 */
package edu.colorado.phet.idealgas.collision;

import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.IdealGasModel;

public class SphereSphereExpert implements CollisionExpert {

    private static boolean ignoreGasMoleculeInteractions = false;

    public static void setIgnoreGasMoleculeInteractions( boolean ignoreGasMoleculeInteractions ) {
        SphereSphereExpert.ignoreGasMoleculeInteractions = ignoreGasMoleculeInteractions;
    }


    private SphereSphereContactDetector detector = new SphereSphereContactDetector();
    private IdealGasModel model;
    private double dt;

    public SphereSphereExpert( IdealGasModel model, double dt ) {
        this.model = model;
        this.dt = dt;
    }

    public boolean detectAndDoCollision( CollidableBody bodyA, CollidableBody bodyB ) {
        boolean haveCollided = false;

        // If the bodies are gas molecules and we are to ignore collisions between
        // gas molecules, return false
        if( bodyA instanceof GasMolecule && bodyB instanceof GasMolecule
            && ignoreGasMoleculeInteractions ) {
            return false;
        }

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
    private boolean tweakCheck( CollidableBody cbA, CollidableBody cbB ) {

//        if( true ) return true;

        SphericalBody sA = (SphericalBody)cbA;
        SphericalBody sB = (SphericalBody)cbB;

        double dPrev = sA.getPositionPrev().distance( sB.getPositionPrev() );
//        if( dPrev > sA.getRadius() + sB.getRadius() ) {
//            System.out.println( "tweak check failed" );
//        }
        return dPrev > sA.getRadius() + sB.getRadius();
    }
}
