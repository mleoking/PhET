/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.Molecule;

import java.awt.geom.Point2D;

/**
 * SpringCollision
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpringCollision {
    private Spring spring;

    public SpringCollision( Spring spring ) {
        this.spring = spring;
    }

    public void collide( Molecule m1, Molecule m2, MoleculeMoleculeCollisionSpec collisionSpec ) {

        // If the edges of the body are closer than the max length of
        // the spring, the magnitude of the force will > 0
        double separation = collisionSpec.getSimpleMoleculeA().getPosition().distance( collisionSpec.getSimpleMoleculeB().getPosition() )
            - collisionSpec.getSimpleMoleculeA().getRadius() - collisionSpec.getSimpleMoleculeB().getRadius();

        if( separation <= spring.getRestingLength() ) {

            double fMag = Math.abs( spring.getRestingLength() - separation ) * spring.getK();

            // The direction of the force will be along the line of action
            Vector2D f = new Vector2D.Double( collisionSpec.getLoa() ).normalize().scale( fMag );

            // Accelerate each of the bodies with the force
            m1.applyForce( f, collisionSpec );
            m2.applyForce( f.scale( -1 ), collisionSpec );
        }
    }

    /**
     * todo: this shouldn't be necessary
     * @return
     */
    public Spring getSpring() {
        return spring;
    }


    public static class Spring {
        double k;
        double restingLength;

        public Spring( double k, double restingLength ) {
            this.k = k;
            this.restingLength = restingLength;
        }

        public double getRestingLength() {
            return restingLength;
        }

        public double getK() {
            return k;
        }
    }

    //--------------------------------------------------------------------------------------------------
    //  Inner classes
    //--------------------------------------------------------------------------------------------------

    public static class CollisionSpec {

        private Vector2D loa;
        private Point2D.Double collisionPt;
        private SimpleMolecule moleculeA;
        private SimpleMolecule moleculeB;

        public CollisionSpec( Vector2D loa,
                              Point2D.Double collisionPt,
                              SimpleMolecule moleculeA,
                              SimpleMolecule moleculeB ) {
            this.moleculeB = moleculeB;
            this.moleculeA = moleculeA;
            this.loa = loa;
            this.collisionPt = collisionPt;
        }

        public Vector2D getLoa() {
            return loa;
        }

        public Point2D.Double getCollisionPt() {
            return collisionPt;
        }

        public SimpleMolecule getMoleculeA() {
            return moleculeA;
        }

        public SimpleMolecule getMoleculeB() {
            return moleculeB;
        }
    }
}
