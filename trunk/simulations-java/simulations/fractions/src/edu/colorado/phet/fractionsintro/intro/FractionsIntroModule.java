// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro;

import edu.colorado.phet.fractionsintro.common.AbstractFractionsModule;
import edu.colorado.phet.fractionsintro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractionsintro.intro.view.FractionsIntroCanvas;

/**
 * Module for "Fractions Intro" sim
 *
 * @author Sam Reid
 */
public class FractionsIntroModule extends AbstractFractionsModule {
    public FractionsIntroModule() {
        this( new FractionsIntroModel( IntroState.IntroState ) );
    }

    private FractionsIntroModule( FractionsIntroModel model ) {
        super( "Intro", model.getClock() );
        setSimulationPanel( new FractionsIntroCanvas( model ) );
    }
}