// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro;

import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsModule;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.IntroModel;
import edu.colorado.phet.sugarandsaltsolutions.intro.view.IntroCanvas;

/**
 * Intro module for sugar and salt solutions
 *
 * @author Sam Reid
 */
public class IntroModule extends SugarAndSaltSolutionsModule {
    private final IntroModel model;

    public IntroModule( SugarAndSaltSolutionsColorScheme backgroundColor ) {
        this( new IntroModel(), backgroundColor );
    }

    public IntroModule( IntroModel model, SugarAndSaltSolutionsColorScheme config ) {
        super( "Intro", model.clock );
        this.model = model;
        setSimulationPanel( new IntroCanvas( this.model, config ) );
    }
}
