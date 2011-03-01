// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import edu.colorado.phet.bendinglight.modules.BendingLightModule;
import edu.colorado.phet.bendinglight.modules.intro.IntroModel;

/**
 * @author Sam Reid
 */
public class MoreToolsModule extends BendingLightModule<IntroModel> {
    public MoreToolsCanvas canvas;

    public MoreToolsModule() {
        super( "More Tools", new IntroModel() );
        canvas = new MoreToolsCanvas( getBendingLightModel(), moduleActive, resetAll );
        setSimulationPanel( canvas );
    }

    @Override
    protected void resetAll() {
        super.resetAll();
        canvas.resetAll();
    }
}