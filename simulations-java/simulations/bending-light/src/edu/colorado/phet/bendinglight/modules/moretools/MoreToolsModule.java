// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import edu.colorado.phet.bendinglight.modules.BendingLightModule;

/**
 * Module for the "more tools" tab.
 *
 * @author Sam Reid
 */
public class MoreToolsModule extends BendingLightModule<MoreToolsModel> {
    public MoreToolsCanvas canvas;

    public MoreToolsModule() {
        super( "More Tools", new MoreToolsModel() );
        canvas = new MoreToolsCanvas( getBendingLightModel(), moduleActive, resetAll );
        setSimulationPanel( canvas );
    }

    @Override
    protected void resetAll() {
        super.resetAll();
        canvas.resetAll();
    }
}