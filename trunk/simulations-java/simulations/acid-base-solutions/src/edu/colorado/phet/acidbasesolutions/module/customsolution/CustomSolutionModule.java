package edu.colorado.phet.acidbasesolutions.module.customsolution;

import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.module.ABSModule;


public class CustomSolutionModule extends ABSModule {

    public CustomSolutionModule() {
        super( ABSStrings.CUSTOM_SOLUTION );
        setSimulationPanel( new CustomSolutionCanvas() );
    }
}
