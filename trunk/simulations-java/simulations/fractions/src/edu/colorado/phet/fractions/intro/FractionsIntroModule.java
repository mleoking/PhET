// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.fractions.intro.model.FractionsIntroModel;

/**
 * Module for "Fractions Intro" sim
 *
 * @author Sam Reid
 */
public class FractionsIntroModule extends Module {
    public FractionsIntroModule() {
        super( "Fractions Intro", new ConstantDtClock( 30 ) );
        FractionsIntroModel model = new FractionsIntroModel();
        setSimulationPanel( new FractionsIntroCanvas( model ) );

        getModulePanel().setLogoPanel( null );
        setClockControlPanel( null );
    }
}