/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.util.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.collision.Box2D;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.mechanics.Vector3D;
import edu.colorado.phet.molecularreactions.model.Molecule;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;

import java.awt.geom.Point2D;

/**
 * MoleculeBoxCollisionAgent
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeBoxCollisionAgent {

    private Vector2D loa = new Vector2D.Double();
    private Point2D.Double collisionPt = new Point2D.Double();
    private Vector2D n = new Vector2D.Double();

    /**
     * Note: This method is not thread-safe, because we use an instance attribute
     * to avoid allocating a new Vector2D on every invocation.
     *
     * @param bodyA
     * @param bodyB
     * @return
     */
    public boolean detectAndDoCollision( Body bodyA, Body bodyB ) {

        Box2D box = null;
        Molecule molecule;
        if( bodyA instanceof Box2D ) {
            box = (Box2D)bodyA;
            if( bodyB instanceof Molecule ) {
                molecule = (Molecule)bodyB;
            }
            else {
                throw new RuntimeException( "bad args" );
            }
        }
        else if( bodyB instanceof Box2D ) {
            box = (Box2D)bodyB;
            if( bodyA instanceof Molecule ) {
                molecule = (Molecule)bodyA;
            }
            else {
                throw new RuntimeException( "bad args" );
            }
        }
        else {
            throw new RuntimeException( "bad args" );
        }

        // Get the total energy of the two objects, so we can conserve it
        double totalEnergy0 = molecule.getKineticEnergy() + bodyB.getKineticEnergy();

        Molecule[] componentMolecules = molecule.getComponentMolecules();
        for( int i = 0; i < componentMolecules.length; i++ ) {

            if( componentMolecules[i] instanceof SimpleMolecule ) {
                SimpleMolecule rm = (SimpleMolecule)componentMolecules[i];

                // Hitting left wall?
                double dx = rm.getCM().getX() - rm.getRadius() - box.getMinX();
                if( dx <= 0 && rm.getVelocity().getX() < 0 ) {
                    loa.setComponents( 1, 0);
                    collisionPt.setLocation( box.getMinX(), rm.getCM().getY());
                    doCollision( molecule, loa, collisionPt );
//                    rm.setVelocity( -rm.getVelocity().getX(), rm.getVelocity().getY() );
//                    rm.setPosition( rm.getPosition().getX() - dx, rm.getPosition().getY() );
                    break;
                }

                // Hitting right wall?
                dx = rm.getCM().getX() + rm.getRadius() - box.getMaxX();
                if( dx >= 0 && rm.getVelocity().getX() > 0 ) {
                    loa.setComponents( 1, 0);
                    collisionPt.setLocation( box.getMaxX(), rm.getCM().getY());
                    doCollision( molecule, loa, collisionPt );
//                    rm.setVelocity( -rm.getVelocity().getX(), rm.getVelocity().getY() );
//                    rm.setPosition( rm.getPosition().getX() - dx, rm.getPosition().getY() );
                    break;
                }

                // Hitting bottom wall?
                double dy = rm.getCM().getY() - rm.getRadius() - box.getMinY();
                if( dy <= 0 && molecule.getVelocity().getY() < 0 ) {
                    loa.setComponents( 0,1);
                    collisionPt.setLocation( rm.getCM().getX(), box.getMaxY() );
                    doCollision( molecule, loa, collisionPt );
//                    rm.setVelocity( rm.getVelocity().getX(), -rm.getVelocity().getY() );
//                    rm.setPosition( rm.getPosition().getX(), rm.getPosition().getY() - dy );
                    break;
                }

                // Hitting top wall?
                dy = rm.getCM().getY() + rm.getRadius() - box.getMaxY();
                if( dy >= 0 && molecule.getVelocity().getY() > 0 ) {
                    loa.setComponents( 0,1);
                    collisionPt.setLocation( rm.getCM().getX(), box.getMinY() );
                    doCollision( molecule, loa, collisionPt );
//                    rm.setVelocity( rm.getVelocity().getX(), -rm.getVelocity().getY() );
//                    rm.setPosition( rm.getPosition().getX(), rm.getPosition().getY() - dy );
                    break;
                }
            }
        }

        // tweak the energy to be constant
//        double totalEnergy1 = molecule.getKineticEnergy();
//        double de = totalEnergy0 - totalEnergy1;
//        double dv = Math.sqrt( 2 * Math.abs( de ) ) * MathUtil.getSign( de );
//        double ratA = 1;
//        double dvA = dv * ratA;
//        double vMagA = molecule.getVelocity().getMagnitude();
//        double fvA = ( vMagA - dvA ) / vMagA;
//        bodyA.getVelocity().multiply( (float)fvA );

        return false;
    }


    public void doCollision( Molecule molecule, Vector2D loa, Point2D.Double collisionPt ) {

        // Get the total energy of the two objects, so we can conserve it
        double totalEnergy0 = molecule.getKineticEnergy() /*+ bodyB.getKineticEnergy()*/;

        // Get the vectors from the bodies' CMs to the point of contact
        Vector2D r1 = new Vector2D.Double( collisionPt.getX() - molecule.getCM().getX(),
                                           collisionPt.getY() - molecule.getCM().getY() );

        // Get the unit vector along the line of action
        n.setComponents( loa.getX(), loa.getY() );
        n.normalize();

        // Get the magnitude along the line of action of the bodies' relative velocities at the
        // point of contact
        Vector3D omega = new Vector3D( 0, 0, (float)molecule.getOmega() );
        Vector3D ot = omega.crossProduct( new Vector3D( r1 ) ).add( new Vector3D( molecule.getVelocity() ) );
        double vr = ot.dot( new Vector3D( n ) );

        // Assume the coefficient of restitution is 1
        float e = 1;

        // Compute the impulse, j
        double numerator = -vr * ( 1 + e );
        Vector3D n3D = new Vector3D( n );
        Vector3D r13D = new Vector3D( r1 );
        Vector3D t1 = r13D.crossProduct( n3D ).multiply( (float)( 1 / molecule.getMomentOfInertia() ) );
        Vector3D t1A = t1.crossProduct( t1 );
        double t1B = n3D.dot( t1A );
        double denominator = ( 1 / molecule.getMass() ) + t1B;
        denominator = ( 1 / molecule.getMass() ) +
                      ( n3D.dot( r13D.crossProduct( n3D ).multiply( 1 / (float)molecule.getMomentOfInertia() ).crossProduct( r13D ) ) );
        double j = numerator / denominator;

        // Compute the new linear and angular velocities, based on the impulse
        molecule.getVelocity().add( new Vector2D.Double( n ).scale( (float)( j / molecule.getMass() ) ) );

        // Compute the angular velocity of the molecule
        molecule.setOmega( molecule.getOmega() + ( r1.getX() * n.getY() - r1.getY() * n.getX() ) * j / ( molecule.getMomentOfInertia() ) );

        // tweak the energy to be constant
//        double totalEnergy1 = molecule.getKineticEnergy() /*+ bodyB.getKineticEnergy()*/;
//        double de = totalEnergy0 - totalEnergy1;
//        double dv = Math.sqrt( 2 * Math.abs( de ) ) * MathUtil.getSign( de );
//        double ratA = 1;
//        double dvA = dv * ratA;
//        double vMagA = molecule.getVelocity().getMagnitude();
//        double fvA = ( vMagA - dvA ) / vMagA;
//        molecule.getVelocity().multiply( (float)fvA );
    }
}
