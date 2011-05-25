// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsModule;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.IntroModel;
import edu.colorado.phet.sugarandsaltsolutions.intro.view.IntroCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Intro module for sugar and salt solutions
 *
 * @author Sam Reid
 */
public class IntroModule extends SugarAndSaltSolutionsModule {
    private final IntroModel model;

    public IntroModule( SugarAndSaltSolutionsColorScheme backgroundColor, Function1<IntroModel, PNode> removeSolutesControl ) {
        this( new IntroModel(), backgroundColor, removeSolutesControl );
    }

    private IntroModule( IntroModel model, SugarAndSaltSolutionsColorScheme config, Function1<IntroModel, PNode> removeSolutesControl ) {
        super( "Intro", model.clock );
        this.model = model;
        setSimulationPanel( new IntroCanvas( this.model, config, removeSolutesControl ) );
    }
}
