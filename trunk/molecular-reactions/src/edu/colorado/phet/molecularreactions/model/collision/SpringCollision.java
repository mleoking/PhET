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
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.A_AB_BC_C_Reaction;
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;

/**
 * SpringCollision
 * <p/>
 * Models the collision of two molecules as compressing a spring, unless the molecules get close
 * enough together and are of the correct types for a reaction to occur.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpringCollision implements Collision {
    private MRModel model;
    private Spring spring;

    public SpringCollision( MRModel model, Spring spring ) {
        this.model = model;
        this.spring = spring;
    }

    public void collide( Molecule mA, Molecule mB, MoleculeMoleculeCollisionSpec collisionSpec ) {

        // If the edges of the body are closer than the max length of
        // the spring, the magnitude of the force will > 0
        SimpleMolecule smA = collisionSpec.getSimpleMoleculeA();
        SimpleMolecule smB = collisionSpec.getSimpleMoleculeB();

        // For the separation of the molecules, we use how far apart their edges were HALF-WAY
        // THROUGH THE LAST TIME STEP.
        double dx = ((smA.getPosition().getX() + smA.getPositionPrev().getX() ) / 2 )
                    - ((smB.getPosition().getX() + smB.getPositionPrev().getX() ) / 2 );
        double dy = ((smA.getPosition().getY() + smA.getPositionPrev().getY() ) / 2 )
                    - ((smB.getPosition().getY() + smB.getPositionPrev().getY() ) / 2 );
//        double separation = Math.sqrt(dx * dx + dy * dy ) - smA.getRadius() - smB.getRadius();
        double separation = smA.getPosition().distance( smB.getPosition() )
                            - smA.getRadius() - smB.getRadius();

        // Set the spring constant to correspond the energy threshold when the spring is
        // compressed to 0 length
        double thresholdEnergy = model.getReaction().getThresholdEnergy( mA, mB );
        double dl = spring.getRestingLength();
        double k = 2 * thresholdEnergy / ( dl * dl );
        spring.setK( k );

        Reaction.ReactionCriteria reactionCriteria = model.getReaction().getReactionCriteria();
        if( reactionCriteria.moleculesAreProperTypes( mA, mB )
//        && spring.getEnergy( sep ) >= model.getReaction().getThresholdEnergy( mA, mB )) {
        && spring.getEnergy( separation ) >= model.getReaction().getThresholdEnergy( mA, mB )) {
            doReaction( mA, mB, collisionSpec );
        }
        if( separation <= 0 && !model.getReaction().areCriteriaMet( mA, mB, collisionSpec )
            && ( mA instanceof MoleculeAB || mB instanceof MoleculeAB )) {
            System.out.println( "SpringCollision.collide" );
        }

        // else, if the separation is less than the resting length, do the spring thing
        else if( separation <= spring.getRestingLength() ) {

            System.out.println( "spring.getEnergy( separation ) = " + spring.getEnergy( separation ) );
            model.addToPotentialEnergyStored( spring.getEnergy( separation ));

            if( true ) {
                spring.pushOnMolecule( mA, separation, new Vector2D.Double( mA.getPosition(),
                                                                            mB.getPosition()) );
                spring.pushOnMolecule( mB, separation, new Vector2D.Double( mB.getPosition(),
                                                                            mA.getPosition()) );
                return;
            }

            double fMag = spring.getForce( separation );
//            double fMag = Math.abs( spring.getRestingLength() - separation ) * spring.getK();

            // The direction of the force will be along the line of action
//            if( fMag == Double.POSITIVE_INFINITY ) {
//                double fMagA =
//            }
            Vector2D fA = new Vector2D.Double( collisionSpec.getLoa() ).normalize().scale( fMag );
            Vector2D fB = new Vector2D.Double( fA).scale( -1 );


            // Accelerate each of the bodies with the force
            mA.applyForce( fA, collisionSpec.getCollisionPt() );
            mB.applyForce( fB, collisionSpec.getCollisionPt() );
        }
    }

    private void doReaction( Molecule moleculeA, Molecule moleculeB, MoleculeMoleculeCollisionSpec collisionSpec ) {
        SimpleMolecule simpleMolecule = null;
        CompositeMolecule compositeMolecule = null;

        if( moleculeA instanceof SimpleMolecule && moleculeB instanceof CompositeMolecule ) {
            simpleMolecule = (SimpleMolecule)moleculeA;
            compositeMolecule = (CompositeMolecule)moleculeB;
        }
        else if( moleculeB instanceof SimpleMolecule && moleculeA instanceof CompositeMolecule ) {
            simpleMolecule = (SimpleMolecule)moleculeB;
            compositeMolecule = (CompositeMolecule)moleculeA;
        }
        else {
            throw new RuntimeException( "unexpected situation" );
        }

        A_AB_BC_C_Reaction reaction = (A_AB_BC_C_Reaction)model.getReaction();
        reaction.doReaction( compositeMolecule, simpleMolecule );
    }

    public double getInteractionDistance() {
        return spring.getRestingLength();
    }


    /**
     *
     */
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

        public void setK( double k ) {
            this.k = k;
        }

        public double getK() {
            return k;
        }

        /**
         * Gets the magnitude of the force exerted by the spring.
         * @param length
         * @return
         */
        public double getForce( double length ) {
            int sign = MathUtil.getSign( length - restingLength );
            double f = ( restingLength - length ) * k;
            if( length == 0 ) {
                System.out.println( "SpringCollision$Spring.getForce" );
            }
            return f;
        }

        /**
         * Returns the stored energy in the spring if it is at a specified length
         * @param length
         * @return the energy stored in the spring
         */
        public double getEnergy( double length ) {
            double energy = ( ( getRestingLength() - length ) * getForce( length ) / 2 );
            if( energy < 0 ) {
                System.out.println( "SpringCollision$Spring.getEnergy" );
            }
            return energy;
        }


        public void pushOnMolecule( Molecule molecule, double length, Vector2D loa ) {

            // NOrmalize the line of action vector
            loa.normalize();

            // Determine the amount the molecule has moved in the direction of the
            // spring in its last time step
            Vector2D dl = new Vector2D.Double( molecule.getPositionPrev(), molecule.getPosition() );
            double ds = dl.dot( loa );

            // Compute the change in potential energy in the spring during that last
            // time step
            double dPe = ds * k;

            // Reduce the molecule's velocity in the direction that the spring pushes
            // on it by an amount that corresponds to the change in the spring's potential
            // energy. If the change in potential is greater than the kinetic energy the molecule
            // has in that direction, reverse the molecule's direction of travel.
            double ss = molecule.getVelocity().dot( loa );
            double keS = ss* ss * molecule.getMass() / 2;

//            if( keS > dPe ) {
                keS -= dPe;

            if( keS < 0 ) {
                System.out.println( "SpringCollision$Spring.pushOnMolecule" );
            }
                int sign = MathUtil.getSign( keS );
                double deltaS = sign * Math.sqrt( 2 * ( keS * sign ) /molecule.getMass()) - ss;
                molecule.getVelocity().add( loa.scale( deltaS ));
//            }
        }
    }
}
