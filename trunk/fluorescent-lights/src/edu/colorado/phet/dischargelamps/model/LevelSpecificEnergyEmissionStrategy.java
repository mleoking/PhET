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
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import edu.colorado.phet.quantum.model.EnergyEmissionStrategy;
import edu.colorado.phet.quantum.model.AtomicState;
import edu.colorado.phet.quantum.model.Atom;

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

    public LevelSpecificEnergyEmissionStrategy( Object[] states,
                                                double[][] probabilities ) {
        // For each state, build a map of probabilities to target states
        // This will be sorted by the probability, in ascending order
        for( int i = 0; i < states.length; i++ ) {
            double p = 0;
            ProbabilityMap probToTargetState = new ProbabilityMap();
            ProbabilityMap.Entry[] entries = new ProbabilityMap.Entry[states.length];
            for( int j = 0; j < states.length; j++ ) {
                entries[j] = new ProbabilityMap.Entry( states[j], probabilities[i][j] );
            }
            probToTargetState.populate( entries );
            originStateToTargetStates.put( states[i], probToTargetState );
        }
    }

    public AtomicState emitEnergy( Atom atom ) {
        AtomicState newState = (AtomicState)getTargetState( atom.getCurrState() );
        return newState;
    }

    public Object getTargetState( Object originState ) {
        ProbabilityMap targetMap = (ProbabilityMap)originStateToTargetStates.get( originState );
        Object targetState = targetMap.get( random.nextDouble() );
//        Object targetState = targetMap.get( new Double( random.nextDouble() ) );
        return targetState;
    }

    public Object getTargetState( Object originState, double p ) {
        ProbabilityMap targetMap = (ProbabilityMap)originStateToTargetStates.get( originState );
        Object targetState = targetMap.get( p );
        return targetState;
    }

    public void setStates( AtomicState[] states ) {
        for( int i = 0; i < states.length; i++ ) {
            AtomicState state = states[i];
            ProbabilityMap pMap = new ProbabilityMap();
            List mapEntries = new ArrayList();
            for( int j = 0; j < teA.length; j++ ) {
                DischargeLampElementProperties.TransitionEntry te = teA[j];
                if( i == te.getSourceStateIdx() ) {
                    ProbabilityMap.Entry pmEntry = new ProbabilityMap.Entry( states[teA[j].getTargetStateIdx()],
                                                                             te.getTxStrength() );

                    // This shows where the problem is. Both states[0] and states[1] return the same state.
                    asdfasdf
                    if( i == 5) {
                        System.out.println( "teA[j].getTargetStateIdx() = " + teA[j].getTargetStateIdx() );
                        System.out.println( "states[teA[j].getTargetStateIdx() = " + states[teA[j].getTargetStateIdx()] );
                    }
                    mapEntries.add( pmEntry );
                };
            }
            pMap.populate( (ProbabilityMap.Entry[])mapEntries.toArray(new ProbabilityMap.Entry[mapEntries.size()]) );

//            for( Iterator iterator = pMap.keySet().iterator(); iterator.hasNext(); ) {
//                Object o = iterator.next();
//                System.out.println( "o = " + o );
//            }
            originStateToTargetStates.put( state, pMap );
        }
    }

//    public static class ProbabilityMap extends TreeSet {
    public static class ProbabilityMap extends TreeMap {

        public ProbabilityMap() {
            super( new ProbabilityComparator() );
        }

        public void populate( Entry[] entries ) {
            // Get the normalization factor for the probabilities
            double pTotal = 0;
            for( int i = 0; i < entries.length; i++ ) {
                pTotal += entries[i].getP();
            }
            double fNorm = 1 / pTotal;
            double p = 0;
            for( int i = 0; i < entries.length; i++ ) {
                Entry entry = entries[i];
                p += entry.getP() * fNorm;
                put( new Double( p ), entry.getObj() );
//                System.out.println( "entry.getObj() = " + entry.getObj() );
            }
        }

        public Object get( double p ) {
            // 0 is a special case. We cant let entries with 0 probability
            // ever match with anything, so if the specified probability
            // is 0, we need to bump it a bit.
            if( p == 0 ) {
                p += Double.MIN_VALUE;
            }

            Object result = null;
            for( Iterator iterator = keySet().iterator(); iterator.hasNext() && result == null; ) {
                Double pD = (Double)iterator.next();
                double pTest = pD.doubleValue();
                if( pTest >= p ) {
//                    System.out.println( "pTest = " + pTest + "\tp = " + p);
                    result = super.get( pD );
                }
            }

//            for( Iterator iterator = keySet().iterator(); iterator.hasNext(); ) {
//                System.out.println( "get(iterator.next()) = " + get( iterator.next() ) );
//            }

            return result;
        }

        public static class Entry {
            private Object obj;
            private double p;

            public Entry( Object obj, double p ) {
                this.obj = obj;
                this.p = p;
            }

            Object getObj() {
                return obj;
            }

            double getP() {
                return p;
            }
        }

        private static class ProbabilityComparator implements Comparator {
            public int compare( Object o1, Object o2 ) {
                double d1 = ( (Double)o1 ).doubleValue();
                double d2 = ( (Double)o2 ).doubleValue();

                // Default result is not equal
                int result = 1;
                if( d1 <= d2 ) {
                    result = 0;
                }
                return result;
            }

            public boolean equals( Object obj ) {
                return this.getClass() == obj.getClass();
            }
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

            lsees = new LevelSpecificEnergyEmissionStrategy( states, p );
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

            lsees = new LevelSpecificEnergyEmissionStrategy( states, p );
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
