// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.buildafraction.view.FreePlayCanvas;
import edu.colorado.phet.fractions.fractionsintro.AbstractFractionsModule;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;

/**
 * Main module for "build a fraction with mixed numbers" tab, which is only visible in the standalone "Build a Fraction" sim.
 *
 * @author Sam Reid
 */
public class FreePlayModule extends AbstractFractionsModule implements FreePlayCanvasContext {
    public FreePlayModule() {
        super( Components.freePlayModule, Strings.FREE_PLAY, new ConstantDtClock() );
        setSimulationPanel( new FreePlayCanvas( this ) );
    }

    public void resetFreePlayCanvas() {
        setSimulationPanel( new FreePlayCanvas( this ) );
    }
}