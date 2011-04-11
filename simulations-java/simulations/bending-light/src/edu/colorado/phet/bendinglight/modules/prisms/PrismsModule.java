// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import edu.colorado.phet.bendinglight.BendingLightStrings;
import edu.colorado.phet.bendinglight.modules.BendingLightModule;

/**
 * Module for the "prism break" tab.
 *
 * @author Sam Reid
 */
public class PrismsModule extends BendingLightModule<PrismsModel> {
    public PrismsCanvas canvas;

    public PrismsModule() {
        super( BendingLightStrings.PRISM_BREAK, new PrismsModel() );
        canvas = new PrismsCanvas( getBendingLightModel(), moduleActive, resetAll );
        setSimulationPanel( canvas );
    }

    @Override
    protected void resetAll() {
        super.resetAll();
        canvas.resetAll();
    }
}
