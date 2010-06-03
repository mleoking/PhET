package edu.colorado.phet.acidbasesolutions.module.testsolution;

import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.module.ABSModule;


public class TestSolutionModule extends ABSModule {

    public TestSolutionModule() {
        super( ABSStrings.TEST_SOLUTION );
        setSimulationPanel( new TestSolutionCanvas() );
    }
}
