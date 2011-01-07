// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dischargelamps.test;/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;
import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;
import edu.colorado.phet.dischargelamps.model.HydrogenProperties;
import edu.colorado.phet.lasers.model.LaserModel;

/**
 * TransitionPropertiesTest
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TransitionPropertiesTest {


    public static class Test extends TestCase {
        private HydrogenProperties properties;
        private LaserModel model;
        private DischargeLampAtom atom;

        protected void setUp() throws Exception {
            properties = new HydrogenProperties();
            model = new LaserModel( 1.0 );
            atom = new DischargeLampAtom( model, properties, properties.getEnergyEmissionStrategy() );
//            (( LevelSpecificEnergyEmissionStrategy)properties.getEnergyEmissionStrategy()).setStates( atom.getStates() );
        }

        public void testA() {
            atom.setCurrState( atom.getStates()[1] );
            AtomicState stateF = atom.getEnergyStateAfterEmission();
            assertTrue( stateF == atom.getStates()[0] );
        }

        public void testB() {
            Map results = new HashMap();
            for ( int i = 0; i < 1000; i++ ) {
                atom.setCurrState( atom.getHighestEnergyState() );
                AtomicState stateF = atom.getEnergyStateAfterEmission();

                Integer cnt = (Integer) results.get( stateF );
                if ( cnt == null ) {
                    cnt = new Integer( 0 );
                }
                results.put( stateF, new Integer( cnt.intValue() + 1 ) );
            }

            System.out.println( "Hydrogen tx from level 5" );
            for ( Iterator iterator = results.keySet().iterator(); iterator.hasNext(); ) {
                Object o = iterator.next();
                Integer cnt = (Integer) results.get( o );
                System.out.println( "o = " + o + "\tcnt = " + cnt );
            }
        }

        public void testC() {
            Map results = new HashMap();
            for ( int i = 0; i < 1000; i++ ) {
                atom.setCurrState( atom.getStates()[4] );
                AtomicState stateF = atom.getEnergyStateAfterEmission();

                Integer cnt = (Integer) results.get( stateF );
                if ( cnt == null ) {
                    cnt = new Integer( 0 );
                }
                results.put( stateF, new Integer( cnt.intValue() + 1 ) );
            }

            System.out.println( "Hydrogen tx from level 4" );
            for ( Iterator iterator = results.keySet().iterator(); iterator.hasNext(); ) {
                Object o = iterator.next();
                Integer cnt = (Integer) results.get( o );
                System.out.println( "o = " + o + "\tcnt = " + cnt );
            }
        }

        public void testD() {
            Map results = new HashMap();
            for ( int i = 0; i < 1000; i++ ) {
                atom.setCurrState( atom.getStates()[3] );
                AtomicState stateF = atom.getEnergyStateAfterEmission();

                Integer cnt = (Integer) results.get( stateF );
                if ( cnt == null ) {
                    cnt = new Integer( 0 );
                }
                results.put( stateF, new Integer( cnt.intValue() + 1 ) );
            }

            System.out.println( "Hydrogen tx from level 3" );
            for ( Iterator iterator = results.keySet().iterator(); iterator.hasNext(); ) {
                Object o = iterator.next();
                Integer cnt = (Integer) results.get( o );
                System.out.println( "o = " + o + "\tcnt = " + cnt );
            }
        }

        public void testE() {
            Map results = new HashMap();
            for ( int i = 0; i < 1000; i++ ) {
                atom.setCurrState( atom.getStates()[2] );
                AtomicState stateF = atom.getEnergyStateAfterEmission();

                Integer cnt = (Integer) results.get( stateF );
                if ( cnt == null ) {
                    cnt = new Integer( 0 );
                }
                results.put( stateF, new Integer( cnt.intValue() + 1 ) );
            }

            System.out.println( "Hydrogen tx from level 2" );
            for ( Iterator iterator = results.keySet().iterator(); iterator.hasNext(); ) {
                Object o = iterator.next();
                Integer cnt = (Integer) results.get( o );
                System.out.println( "o = " + o + "\tcnt = " + cnt );
            }
        }

        public void testF() {
            Map results = new HashMap();
            for ( int i = 0; i < 1000; i++ ) {
                atom.setCurrState( atom.getStates()[1] );
                AtomicState stateF = atom.getEnergyStateAfterEmission();

                Integer cnt = (Integer) results.get( stateF );
                if ( cnt == null ) {
                    cnt = new Integer( 0 );
                }
                results.put( stateF, new Integer( cnt.intValue() + 1 ) );
            }

            System.out.println( "Hydrogen tx from level 1" );
            for ( Iterator iterator = results.keySet().iterator(); iterator.hasNext(); ) {
                Object o = iterator.next();
                Integer cnt = (Integer) results.get( o );
                System.out.println( "o = " + o + "\tcnt = " + cnt );
            }
        }

        public void testG() {
            Map results = new HashMap();
            for ( int i = 0; i < 1000; i++ ) {
                atom.setCurrState( atom.getStates()[0] );
                AtomicState stateF = atom.getEnergyStateAfterEmission();

                Integer cnt = (Integer) results.get( stateF );
                if ( cnt == null ) {
                    cnt = new Integer( 0 );
                }
                results.put( stateF, new Integer( cnt.intValue() + 1 ) );
            }

            System.out.println( "Hydrogen tx from level 0" );
            for ( Iterator iterator = results.keySet().iterator(); iterator.hasNext(); ) {
                Object o = iterator.next();
                Integer cnt = (Integer) results.get( o );
                System.out.println( "o = " + o + "\tcnt = " + cnt );
            }
        }

    }


    public static void main( String[] args ) {
        Test test = new Test();
        try {
            test.setUp();
            test.testA();
            test.testB();
            test.testC();
            test.testD();
            test.testE();
            test.testF();
            test.testG();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        test.testB();
    }
}
