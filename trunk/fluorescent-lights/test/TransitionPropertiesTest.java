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
import edu.colorado.phet.dischargelamps.model.NeonProperties;
import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;
import edu.colorado.phet.dischargelamps.model.LevelSpecificEnergyEmissionStrategy;
import edu.colorado.phet.dischargelamps.model.HydrogenProperties;
import edu.colorado.phet.quantum.model.Atom;
import edu.colorado.phet.quantum.model.AtomicState;
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
            model = new LaserModel();
            atom = new DischargeLampAtom( model, properties, properties.getEnergyEmissionStrategy() );
//            (( LevelSpecificEnergyEmissionStrategy)properties.getEnergyEmissionStrategy()).setStates( atom.getStates() );
        }

        public void testA() {
            atom.setCurrState( atom.getStates()[1] );
            AtomicState stateF = atom.getEnergyStateAfterEmission();
            assertTrue( stateF == atom.getStates()[0] );
        }

        public void testB() {
            for( int i = 0; i < 10; i++ ) {
                atom.setCurrState( atom.getHighestEnergyState() );
                AtomicState stateF = atom.getEnergyStateAfterEmission();
                System.out.println( "stateF = " + stateF );
            }
        }

    }


    public static void main( String[] args ) {
        Test test = new Test();
        try {
            test.setUp();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        test.testB();
    }
}
