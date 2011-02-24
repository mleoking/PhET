// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.balanceequation;

import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.BCEModule;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.model.BCEClock;

/**
 * The "Balance Equation" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroductionModule extends BCEModule {

    private final IntroductionModel model;
    private final IntroductionCanvas canvas;

    public IntroductionModule( BCEGlobalProperties globalProperties ) {
        super( globalProperties.getFrame(), BCEStrings.INTRODUCTION, new BCEClock(), true /* startsPaused */ );
        model = new IntroductionModel( globalProperties.isDev() );
        canvas = new IntroductionCanvas( model, globalProperties, this );
        setSimulationPanel( canvas );
    }

    @Override
    public void reset() {
        super.reset();
        model.reset();
        canvas.reset();
    }
}
