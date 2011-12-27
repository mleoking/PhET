// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro;

import edu.colorado.phet.fractions.intro.common.AbstractFractionsModule;
import edu.colorado.phet.fractions.intro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractions.intro.intro.view.FractionsIntroCanvas;

/**
 * Module for "Fractions Intro" sim
 *
 * @author Sam Reid
 */
public class FractionsIntroModule extends AbstractFractionsModule {
    public FractionsIntroModule() {
        super( "Intro" );
        setSimulationPanel( new FractionsIntroCanvas( new FractionsIntroModel() ) );
    }
}