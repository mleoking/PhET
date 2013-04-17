// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.reactionsandrates.test;/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.reactionsandrates.model.CompositeMolecule;
import edu.colorado.phet.reactionsandrates.model.EnergyProfile;
import edu.colorado.phet.reactionsandrates.model.MRModel;
import edu.colorado.phet.reactionsandrates.model.MRModelUtil;
import edu.colorado.phet.reactionsandrates.model.MoleculeA;
import edu.colorado.phet.reactionsandrates.model.MoleculeB;
import edu.colorado.phet.reactionsandrates.model.MoleculeBC;
import edu.colorado.phet.reactionsandrates.model.MoleculeC;
import edu.colorado.phet.reactionsandrates.model.SimpleMolecule;
import edu.colorado.phet.reactionsandrates.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.reactionsandrates.model.reactions.Reaction;

/**
 * MoleculeCollisionTest
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeCollisionTest {

    public static class Test extends TestSuite {
        public Test() {
            this.addTest( new ProfileTest_1() );
            this.addTest( new CollisionEnergyTest() );
        }
    }

    public static class ProfileTest_1 extends TestCase {
        private IClock clock;
        private MRModel model;

        protected void setUp() throws Exception {
            clock = new SwingClock( 40, 1 );
            model = new MRModel( clock );
        }

        // Tests the type of the reaction
        public void testReactionType() {
            Reaction reaction = model.getReaction();
            assertTrue( reaction instanceof A_BC_AB_C_Reaction );
        }

        // Tests getting the slope of the profile right
        public void testProfileSlope() {
            EnergyProfile profile = model.getEnergyProfile();
            double slope = Math.atan2( profile.getPeakLevel() - profile.getRightLevel(), profile.getThresholdWidth() / 2 );
            double testPeakLevel = profile.getRightLevel() + slope * profile.getThresholdWidth() / 2;
            assertTrue( ( testPeakLevel - profile.getPeakLevel() ) < 0.0001 );
        }
    }

    public static class CollisionEnergyTest extends TestCase {

        // Tests getting the collision energy of two molecules movig toward each other
        public void testCE_A() {
            Point2D pCM = new Point2D.Double( 20, 30 );
            MoleculeA mA = new MoleculeA();
            MoleculeB mB = new MoleculeB();
            MoleculeC mC = new MoleculeC();
            CompositeMolecule mBC = new MoleculeBC( new SimpleMolecule[] { mB, mC } );

            Point2D pA = new Point2D.Double( pCM.getX() - 10, pCM.getY() );
            MutableVector2D vA = new MutableVector2D( 1, 0 );
            mA.setVelocity( vA );
            mA.setPosition( pA );

            Point2D pB = new Point2D.Double( pCM.getX() + 10, pCM.getY() );
            MutableVector2D vB = new MutableVector2D( -1, 0 );
            mB.setVelocity( vB );
            mB.setPosition( pB );

            Point2D pC = new Point2D.Double( pB.getX() + mB.getRadius() + mC.getRadius(), pCM.getY() );
            MutableVector2D vC = new MutableVector2D( -1, 0 );
            mC.setVelocity( vC );
            mC.setPosition( pC );

            // Step the model so all the elements get valid kinematic attributes
            mA.stepInTime( 1 );
            mB.stepInTime( 1 );
            mC.stepInTime( 1 );
            mBC.stepInTime( 1 );

            double ke = MRModelUtil.getCollisionEnergy( mA, mBC );
            assertTrue( ke >= 0 );
            System.out.println( "ke = " + ke );
        }

        // Tests getting the collision energy of two molecules moving away from each other
        public void testCE_B() {
            Point2D pCM = new Point2D.Double( 20, 30 );
            MoleculeA mA = new MoleculeA();
            MoleculeB mB = new MoleculeB();
            MoleculeC mC = new MoleculeC();
            CompositeMolecule mBC = new MoleculeBC( new SimpleMolecule[] { mB, mC } );

            Point2D pA = new Point2D.Double( pCM.getX() - 10, pCM.getY() );
            MutableVector2D vA = new MutableVector2D( -1, 0 );
            mA.setVelocity( vA );
            mA.setPosition( pA );

            Point2D pB = new Point2D.Double( pCM.getX() + 10, pCM.getY() );
            MutableVector2D vB = new MutableVector2D( 1, 0 );
            mB.setVelocity( vB );
            mB.setPosition( pB );

            Point2D pC = new Point2D.Double( pB.getX() + mB.getRadius() + mC.getRadius(), pCM.getY() );
            MutableVector2D vC = new MutableVector2D( 1, 0 );
            mC.setVelocity( vC );
            mC.setPosition( pC );

            // Step the model so all the elements get valid kinematic attributes
            mA.stepInTime( 1 );
            mB.stepInTime( 1 );
            mC.stepInTime( 1 );
            mBC.stepInTime( 1 );

            double ke = MRModelUtil.getCollisionEnergy( mA, mBC );
            assertTrue( ke == 0 );
            System.out.println( "ke = " + ke );
        }
    }

    public static class CollisionTest extends TestCase {

        private IClock clock;
        private MRModel model;
        private MoleculeA mA;
        private MoleculeB mB;
        private MoleculeC mC;
        private CompositeMolecule mBC;
        private EnergyProfile profile;
        private Reaction reaction;

        protected void setUp() throws Exception {
            clock = new SwingClock( 40, 1 );
            model = new MRModel( clock );
            profile = model.getEnergyProfile();
            reaction = model.getReaction();

            Point2D pCM = new Point2D.Double( 20, 30 );
            mA = new MoleculeA();
            mB = new MoleculeB();
            mC = new MoleculeC();
            mBC = new MoleculeBC( new SimpleMolecule[] { mB, mC } );

            Point2D pA = new Point2D.Double( pCM.getX() - 10, pCM.getY() );
            MutableVector2D vA = new MutableVector2D( -1, 0 );
            mA.setVelocity( vA );
            mA.setPosition( pA );

            Point2D pB = new Point2D.Double( pCM.getX() + 10, pCM.getY() );
            MutableVector2D vB = new MutableVector2D( 1, 0 );
            mB.setVelocity( vB );
            mB.setPosition( pB );

            Point2D pC = new Point2D.Double( pB.getX() + mB.getRadius() + mC.getRadius(), pCM.getY() );
            MutableVector2D vC = new MutableVector2D( 1, 0 );
            mC.setVelocity( vC );
            mC.setPosition( pC );

            // Step the model so all the elements get valid kinematic attributes
            mA.stepInTime( 1 );
            mB.stepInTime( 1 );
            mC.stepInTime( 1 );
            mBC.stepInTime( 1 );
        }

        private void setVelocitiesAndStepInTime( MutableVector2D vA, MutableVector2D vBC ) {
            mA.setVelocity( vA );
            mB.setVelocity( vBC );
            mC.setVelocity( vBC );
            // Step the model so all the elements get valid kinematic attributes
            mA.stepInTime( 1 );
            mB.stepInTime( 1 );
            mC.stepInTime( 1 );
            mBC.stepInTime( 1 );
        }

        private void setPositions( Point2D pA, Point2D pB ) {
            mA.setPosition( pA );
            mB.setPosition( pB );
            mC.setPosition( new Point2D.Double( pB.getX() + mB.getRadius() + mC.getRadius(), pB.getY() ) );
        }

        public void testCollisionDistanceComputation() {
            CompositeMolecule pc = mB.getParentComposite();
            mB.setParentComposite( null );//step in time has different semantics for composite particles
            setPositions( new Point2D.Double( 0, 0 ), new Point2D.Double( 0 + mA.getRadius() + mB.getRadius() + 2, 0 ) );
            assertEquals( "collision distance is not properly computed", 2.0, model.getReaction().getDistanceToCollision( mA, mBC ), 1E-6 );

            // Check that the collision distance is properly computed
            setVelocitiesAndStepInTime( new MutableVector2D( 1, 0 ), new MutableVector2D( -1, 0 ) );
            assertEquals( "collision distance is not properly computed", 0.0, model.getReaction().getDistanceToCollision( mA, mBC ), 1E-6 );

            setVelocitiesAndStepInTime( new MutableVector2D( 1, 0 ), new MutableVector2D( -1, 0 ) );
            assertEquals( "collision distance is not properly computed", -2.0, model.getReaction().getDistanceToCollision( mA, mBC ), 1E-6 );
            mB.setParentComposite( pc );
        }

        public void testEnergyComputation() {
            double slope = Math.atan2( profile.getPeakLevel() - profile.getRightLevel(), profile.getThresholdWidth() / 2 );
            setPositions( new Point2D.Double( 0, 0 ), new Point2D.Double( 0 + mA.getRadius() + mB.getRadius() - 4, 0 ) );

            // Compute the energy level reached on the threshold hill
            double collisionDistance = model.getReaction().getDistanceToCollision( mA, mBC );
            assertTrue( collisionDistance == -4 );
            double dE = Math.tan( slope ) * Math.abs( collisionDistance );
            double dECheck = ( profile.getPeakLevel() - profile.getRightLevel() ) * ( -collisionDistance / ( profile.getThresholdWidth() / 2 ) );
            assertTrue( dE - dECheck < 0.00005 );
        }

        public void testInsufficientEnergy() {
            double slope = Math.atan2( profile.getPeakLevel() - profile.getRightLevel(), profile.getThresholdWidth() / 2 );
            setPositions( new Point2D.Double( 0, 0 ), new Point2D.Double( 0 + mA.getRadius() + mB.getRadius() + 4, 0 ) );

            // Compute the energy level reached on the threshold hill
            {
                double collisionDistance = model.getReaction().getDistanceToCollision( mA, mBC );
                assertTrue( collisionDistance == 4 );
            }

            {
                setVelocitiesAndStepInTime( new MutableVector2D( 1, 0 ), new MutableVector2D( -1, 0 ) );
                double collisionDistance = model.getReaction().getDistanceToCollision( mA, mBC );
                assertTrue( collisionDistance == 2 );
            }

            {
                setVelocitiesAndStepInTime( new MutableVector2D( 1, 0 ), new MutableVector2D( -1, 0 ) );
                double collisionDistance = model.getReaction().getDistanceToCollision( mA, mBC );
                assertTrue( collisionDistance == 0 );
            }

            {
                boolean outOfEnergy = false;
                for ( int i = 0; i < 100 && !outOfEnergy; i++ ) {
                    setVelocitiesAndStepInTime( new MutableVector2D( 1, 0 ), new MutableVector2D( -1, 0 ) );
                    double collisionDistance = model.getReaction().getDistanceToCollision( mA, mBC );
                    double dE = Math.tan( slope ) * Math.abs( collisionDistance );
                    double dCE = MRModelUtil.getCollisionEnergy( mA, mBC );
                    System.out.println( "dE = " + dE );
                    System.out.println( "dCE = " + dCE );
                    System.out.println( "i = " + i );

                    outOfEnergy = dE > dCE;
                }
                double d = model.getReaction().getDistanceToCollision( mA, mBC );
                System.out.println( "d = " + d );
                assertTrue( Math.abs( d ) < profile.getThresholdWidth() / 2 );
            }
        }

        /**
         * This is getting pretty close. The outstanding problem may be that the last step that is taken
         * before the collision runs out of energy should not be assumed to have been completed, because
         * it should have stopped mid-way through the time step.
         */
        public void testSufficientEnergy() {
            double thresholdWidth = Math.min( mA.getRadius(), mB.getRadius() );
            double slope = Math.atan2( profile.getPeakLevel() - profile.getRightLevel(), thresholdWidth );
            setPositions( new Point2D.Double( 0, 0 ), new Point2D.Double( 0 + mA.getRadius() + mB.getRadius() + 4, 0 ) );

            {
                boolean outOfEnergy = false;
                boolean reactionReached = false;
                for ( int i = 0; i < 100 && !outOfEnergy && !reactionReached; i++ ) {
                    setVelocitiesAndStepInTime( new MutableVector2D( 1.8476, 0 ), new MutableVector2D( -1.8476, 0 ) );
                    double collisionDistance = model.getReaction().getDistanceToCollision( mA, mBC );
                    double dE = Math.tan( slope ) * Math.abs( collisionDistance );
                    double dCE = MRModelUtil.getCollisionEnergy( mA, mBC );
                    if ( dCE <= 0 ) {
                        dCE = MRModelUtil.getCollisionEnergy( mA, mBC );
                    }
                    System.out.println( "dE = " + dE );
                    System.out.println( "dCE = " + dCE );
                    System.out.println( "collisionDistance = " + collisionDistance );

                    outOfEnergy = dE > dCE;
                    reactionReached = -collisionDistance >= thresholdWidth;
                }
                double d = model.getReaction().getDistanceToCollision( mA, mBC );
                System.out.println( "outOfEnergy = " + outOfEnergy );
                assertTrue( reactionReached );
                System.out.println( "d = " + d );
            }
        }
    }
}
