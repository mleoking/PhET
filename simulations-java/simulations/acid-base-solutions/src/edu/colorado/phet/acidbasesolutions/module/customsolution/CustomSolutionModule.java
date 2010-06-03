/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.customsolution;

import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.module.ABSModule;

/**
 * "Custom Solution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CustomSolutionModule extends ABSModule {

    public CustomSolutionModule() {
        super( ABSStrings.CUSTOM_SOLUTION );
        setSimulationPanel( new CustomSolutionCanvas() );
        setControlPanel( new CustomSolutionControlPanel( this ) );
    }
}
