// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import edu.colorado.phet.bendinglight.BendingLightStrings;
import edu.colorado.phet.bendinglight.modules.BendingLightModule;

/**
 * Module for the "prism break" tab.
 *
 * @author Sam Reid
 */
public class PrismBreakModule extends BendingLightModule<PrismBreakModel> {
    public PrismBreakCanvas canvas;

    public PrismBreakModule() {
        super( BendingLightStrings.PRISM_BREAK, new PrismBreakModel() );

        //Create and add the PrismsCanvas
        canvas = new PrismBreakCanvas( getBendingLightModel(), moduleActive, resetAll );
        setSimulationPanel( canvas );
    }

    @Override
    protected void resetAll() {
        super.resetAll();
        canvas.resetAll();
    }
}
