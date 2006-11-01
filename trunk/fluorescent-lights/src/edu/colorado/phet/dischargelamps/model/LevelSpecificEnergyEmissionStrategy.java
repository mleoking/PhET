/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.model;

import java.util.*;

import junit.framework.TestCase;
import junit.swingui.TestRunner;
import edu.colorado.phet.quantum.model.EnergyEmissionStrategy;
import edu.colorado.phet.quantum.model.AtomicState;
import edu.colorado.phet.quantum.model.Atom;
import edu.colorado.phet.quantum.util.ProbabilisticChooser;

/**
 * LevelSpecificEnergyEmissionStrategy
 * <p/>
 * An energy emission strategy in which the probability of the atom going from
 * one state to another is different for the transition from each level to
 * every lower level.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LevelSpecificEnergyEmissionStrategy implements EnergyEmissionStrategy {
    private static final Random random = new Random();

    private Map originStateToTargetStates = new HashMap();
    private DischargeLampElementProperties.TransitionEntry[] teA;


    public LevelSpecificEnergyEmissionStrategy( DischargeLampElementProperties.TransitionEntry[] teA ) {
        this.teA = teA;
    }

    public AtomicState emitEnergy( Atom atom ) {
        AtomicState newState = (AtomicState)getTargetState( atom.getCurrState() );
        return newState;
    }

    public Object getTargetState( Object originState ) {
        return getTargetState( originState, random.nextDouble() );
    }

    public Object getTargetState( Object originState, double p ) {
        ProbabilisticChooser targetMap = (ProbabilisticChooser)originStateToTargetStates.get( originState );
        Object targetState = targetMap.get( p );
        return targetState;
    }

    /**
     * Sets the states for the strategy. Builds the map that holds the ProbabilityMaps for each
     * state.
     *
     * @param states
     */
    public void setStates( AtomicState[] states ) {
        for( int i = 0; i < states.length; i++ ) {
            AtomicState state = states[i];
            List mapEntries = new ArrayList();
            for( int j = 0; j < teA.length; j++ ) {
                DischargeLampElementProperties.TransitionEntry te = teA[j];
                if( i == te.getSourceStateIdx() ) {
                    ProbabilisticChooser.Entry pmEntry = new ProbabilisticChooser.Entry( states[teA[j].getTargetStateIdx()],
                                                                             te.getTxStrength() );
                    mapEntries.add( pmEntry );
                };
            }
            originStateToTargetStates.put( state, new ProbabilisticChooser( (ProbabilisticChooser.Entry[])mapEntries.toArray(new ProbabilisticChooser.Entry[mapEntries.size()]) ) );
        }
    }

    // Test case
    public static void main( String[] args ) {

        try {
            Test test = new Test();
            test.setUp();
            test.testA1();
            test.testA2();
            test.testA3();
            test.testA4();
            test.testA5();

            test.testB1();
            test.testB2();
            test.testB3();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }

        TestRunner.run( Test.class );
        TestRunner.run( Test2.class );
    }

    public static class Test extends TestCase {
        private Object[] states;
        private double[][] p;
        private LevelSpecificEnergyEmissionStrategy lsees;
        private Object oA;
        private Object oB;
        private Object oC;
        private Object oD;

        public Test() {
            super();
        }

        protected void setUp() throws Exception {
            oA = new String( "A" );
            oB = new String( "B" );
            oC = new String( "C" );
            oD = new String( "D" );
            states = new Object[]{oA, oB, oC};

            double[] pA = new double[]{0, .2, .8};
            double[] pB = new double[]{0, 0, 1};
            double[] pC = new double[]{0, 0, 1};
            p = new double[][]{pA, pB, pC };

//            lsees = new LevelSpecificEnergyEmissionStrategy( states, p );
        }

        public void testA1() {
            Object o = lsees.getTargetState( oA, .9 );
            assertTrue( o == oC );
        }

        public void testA2() {
            Object o = lsees.getTargetState( oA, .8 );
            assertTrue( o == oC );
        }

        public void testA3() {
            Object o = lsees.getTargetState( oA, .3 );
            assertTrue( o == oC );
        }

        public void testA4() {
            Object o = lsees.getTargetState( oA, .1 );
            assertTrue( o == oB );
        }

        public void testA5() {
            Object o = lsees.getTargetState( oA, .0 );
            assertTrue( o == oB );
        }

        public void testB1() {
            Object o = lsees.getTargetState( oB, .0 );
            assertTrue( o == oC );
        }

        public void testB2() {
            Object o = lsees.getTargetState( oB, .5 );
            assertTrue( o == oC );
        }

        public void testB3() {
            Object o = lsees.getTargetState( oB, 1 );
            assertTrue( o == oC );
        }

        public void testC1() {
            Object o = lsees.getTargetState( oC, .0 );
            assertTrue( o == oC );
        }

        public void testC2() {
            Object o = lsees.getTargetState( oC, .5 );
            assertTrue( o == oC );
        }

        public void testC3() {
            Object o = lsees.getTargetState( oC, 1 );
            assertTrue( o == oC );
        }
    }

    /**
     * Tests normalization of probabilities
     */
    public static class Test2 extends TestCase {
        private Object[] states;
        private double[][] p;
        private LevelSpecificEnergyEmissionStrategy lsees;
        private Object oA;
        private Object oB;
        private Object oC;
        private Object oD;

        public Test2() {
            super();
        }

        protected void setUp() throws Exception {
            oA = new String( "A" );
            oB = new String( "B" );
            oC = new String( "C" );
            oD = new String( "D" );
            states = new Object[]{oA, oB, oC};

            double[] pA = new double[]{0, .4, 1.6};
            double[] pB = new double[]{0, 0, 1};
            double[] pC = new double[]{0, 0, 1};
            p = new double[][]{pA, pB, pC };

//            lsees = new LevelSpecificEnergyEmissionStrategy( states, p );
        }

        public void testA1() {
            Object o = lsees.getTargetState( oA, .9 );
            assertTrue( o == oC );
        }

        public void testA2() {
            Object o = lsees.getTargetState( oA, .8 );
            assertTrue( o == oC );
        }

        public void testA3() {
            Object o = lsees.getTargetState( oA, .3 );
            assertTrue( o == oC );
        }

        public void testA4() {
            Object o = lsees.getTargetState( oA, .1 );
            assertTrue( o == oB );
        }

        public void testA5() {
            Object o = lsees.getTargetState( oA, .0 );
            assertTrue( o == oB );
        }

        public void testB1() {
            Object o = lsees.getTargetState( oB, .0 );
            assertTrue( o == oC );
        }

        public void testB2() {
            Object o = lsees.getTargetState( oB, .5 );
            assertTrue( o == oC );
        }

        public void testB3() {
            Object o = lsees.getTargetState( oB, 1 );
            assertTrue( o == oC );
        }

        public void testC1() {
            Object o = lsees.getTargetState( oC, .0 );
            assertTrue( o == oC );
        }

        public void testC2() {
            Object o = lsees.getTargetState( oC, .5 );
            assertTrue( o == oC );
        }

        public void testC3() {
            Object o = lsees.getTargetState( oC, 1 );
            assertTrue( o == oC );
        }
    }
}
