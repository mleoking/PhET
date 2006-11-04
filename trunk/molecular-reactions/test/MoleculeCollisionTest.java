/* Copyright 2003-2004, University of Colorado */

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
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

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
            CompositeMolecule mBC = new MoleculeBC( new SimpleMolecule[]{mB, mC} );

            Point2D pA = new Point2D.Double( pCM.getX() - 10, pCM.getY() );
            Vector2D vA = new Vector2D.Double( 1, 0 );
            mA.setVelocity( vA );
            mA.setPosition( pA );

            Point2D pB = new Point2D.Double( pCM.getX() + 10, pCM.getY() );
            Vector2D vB = new Vector2D.Double( -1, 0 );
            mB.setVelocity( vB );
            mB.setPosition( pB );

            Point2D pC = new Point2D.Double( pB.getX() + mB.getRadius() + mC.getRadius(), pCM.getY() );
            Vector2D vC = new Vector2D.Double( -1, 0 );
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
            CompositeMolecule mBC = new MoleculeBC( new SimpleMolecule[]{mB, mC} );

            Point2D pA = new Point2D.Double( pCM.getX() - 10, pCM.getY() );
            Vector2D vA = new Vector2D.Double( -1, 0 );
            mA.setVelocity( vA );
            mA.setPosition( pA );

            Point2D pB = new Point2D.Double( pCM.getX() + 10, pCM.getY() );
            Vector2D vB = new Vector2D.Double( 1, 0 );
            mB.setVelocity( vB );
            mB.setPosition( pB );

            Point2D pC = new Point2D.Double( pB.getX() + mB.getRadius() + mC.getRadius(), pCM.getY() );
            Vector2D vC = new Vector2D.Double( 1, 0 );
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
            mBC = new MoleculeBC( new SimpleMolecule[]{mB, mC} );

            Point2D pA = new Point2D.Double( pCM.getX() - 10, pCM.getY() );
            Vector2D vA = new Vector2D.Double( -1, 0 );
            mA.setVelocity( vA );
            mA.setPosition( pA );

            Point2D pB = new Point2D.Double( pCM.getX() + 10, pCM.getY() );
            Vector2D vB = new Vector2D.Double( 1, 0 );
            mB.setVelocity( vB );
            mB.setPosition( pB );

            Point2D pC = new Point2D.Double( pB.getX() + mB.getRadius() + mC.getRadius(), pCM.getY() );
            Vector2D vC = new Vector2D.Double( 1, 0 );
            mC.setVelocity( vC );
            mC.setPosition( pC );

            // Step the model so all the elements get valid kinematic attributes
            mA.stepInTime( 1 );
            mB.stepInTime( 1 );
            mC.stepInTime( 1 );
            mBC.stepInTime( 1 );
        }

        private void setVelocities( Vector2D vA, Vector2D vBC ) {
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
            setPositions( new Point2D.Double( 0, 0 ), new Point2D.Double( 0 + mA.getRadius() + mB.getRadius() + 2, 0 ) );

            // Check that the collision distance is properly computed
            {
                double collisionDistance = model.getReaction().getCollisionDistance( mA, mBC );
                assertTrue( collisionDistance == 2 );
            }

            {
                setVelocities( new Vector2D.Double( 1, 0 ), new Vector2D.Double( -1, 0 ) );
                double collisionDistance = model.getReaction().getCollisionDistance( mA, mBC );
                assertTrue( collisionDistance == 0 );
            }

            {
                setVelocities( new Vector2D.Double( 1, 0 ), new Vector2D.Double( -1, 0 ) );
                double collisionDistance = model.getReaction().getCollisionDistance( mA, mBC );
                assertTrue( collisionDistance == -2 );
            }
        }

        public void testEnergyComputation() {
            double slope = Math.atan2( profile.getPeakLevel() - profile.getRightLevel(), profile.getThresholdWidth() / 2 );
            setPositions( new Point2D.Double( 0, 0 ), new Point2D.Double( 0 + mA.getRadius() + mB.getRadius() - 4, 0 ) );

            // Compute the energy level reached on the threshold hill
            double collisionDistance = model.getReaction().getCollisionDistance( mA, mBC );
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
                double collisionDistance = model.getReaction().getCollisionDistance( mA, mBC );
                assertTrue( collisionDistance == 4 );
            }

            {
                setVelocities( new Vector2D.Double( 1, 0 ), new Vector2D.Double( -1, 0 ) );
                double collisionDistance = model.getReaction().getCollisionDistance( mA, mBC );
                assertTrue( collisionDistance == 2 );
            }

            {
                setVelocities( new Vector2D.Double( 1, 0 ), new Vector2D.Double( -1, 0 ) );
                double collisionDistance = model.getReaction().getCollisionDistance( mA, mBC );
                assertTrue( collisionDistance == 0 );
            }

            {
                boolean outOfEnergy = false;
                for( int i = 0; i < 100 && !outOfEnergy; i++ ) {
                    setVelocities( new Vector2D.Double( 1, 0 ), new Vector2D.Double( -1, 0 ) );
                    double collisionDistance = model.getReaction().getCollisionDistance( mA, mBC );
                    double dE = Math.tan( slope ) * Math.abs( collisionDistance );
                    double dCE = MRModelUtil.getCollisionEnergy( mA, mBC );
                    System.out.println( "dE = " + dE );
                    System.out.println( "dCE = " + dCE );
                    System.out.println( "i = " + i );

                    outOfEnergy = dE > dCE;
                }
                double d = model.getReaction().getCollisionDistance( mA, mBC );
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
//            double slope = Math.atan2( profile.getPeakLevel() - profile.getRightLevel(), profile.getThresholdWidth() / 2 );
            setPositions( new Point2D.Double( 0, 0 ), new Point2D.Double( 0 + mA.getRadius() + mB.getRadius() + 4, 0 ) );

            {
                boolean outOfEnergy = false;
                boolean reactionReached = false;
                for( int i = 0; i < 100 && !outOfEnergy && !reactionReached; i++ ) {
                    setVelocities( new Vector2D.Double( 1.7, 0 ), new Vector2D.Double( -1.7, 0 ) );
//                    setVelocities( new Vector2D.Double( 1.8476, 0 ), new Vector2D.Double( -1.8476, 0 ) );
                    double collisionDistance = model.getReaction().getCollisionDistance( mA, mBC );
                    double dE = Math.tan( slope ) * Math.abs( collisionDistance );
                    double dCE = MRModelUtil.getCollisionEnergy( mA, mBC );
                    if( dCE <= 0 ) {
                        dCE = MRModelUtil.getCollisionEnergy( mA, mBC );
                    }
                    System.out.println( "dE = " + dE );
                    System.out.println( "dCE = " + dCE );
                    System.out.println( "collisionDistance = " + collisionDistance );

                    outOfEnergy = dE > dCE;
                    reactionReached = -collisionDistance >= thresholdWidth;
//                    reactionReached = -collisionDistance >= profile.getThresholdWidth() / 2;
                }
                double d = model.getReaction().getCollisionDistance( mA, mBC );
                System.out.println( "outOfEnergy = " + outOfEnergy );
                assertTrue( reactionReached );
                System.out.println( "d = " + d );
//                assertTrue( Math.abs( d ) >= profile.getThresholdWidth() / 2 );
            }
        }
    }
}
