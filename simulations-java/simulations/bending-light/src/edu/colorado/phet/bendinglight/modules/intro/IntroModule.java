// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import edu.colorado.phet.bendinglight.modules.BendingLightModule;

/**
 * @author Sam Reid
 */
public class IntroModule extends BendingLightModule<IntroModel> {
    public IntroCanvas canvas;

    public IntroModule() {
        super( "Intro", new IntroModel() );
        canvas = new IntroCanvas( getLRRModel(), moduleActive, resetAll );
        setSimulationPanel( canvas );
    }

    @Override
    protected void resetAll() {
        super.resetAll();
        canvas.resetAll();
    }
}
