/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.testsolution;

import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.module.ABSModule;

/**
 * "Test Solution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestSolutionModule extends ABSModule {

    public TestSolutionModule() {
        super( ABSStrings.TEST_SOLUTION );
        setSimulationPanel( new TestSolutionCanvas() );
        setControlPanel( new TestSolutionControlPanel() );
    }
}
