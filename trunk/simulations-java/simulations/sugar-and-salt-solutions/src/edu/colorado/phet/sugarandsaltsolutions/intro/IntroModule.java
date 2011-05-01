// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro;

import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsModule;

/**
 * Intro module for sugar and salt solutions
 *
 * @author Sam Reid
 */
public class IntroModule extends SugarAndSaltSolutionsModule {
    public IntroModule() {
        super( "Intro" );
        setSimulationPanel( new IntroCanvas() );
    }
}
