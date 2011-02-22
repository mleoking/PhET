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
public class BalanceEquationModule extends BCEModule {

    private final BalanceEquationModel model;
    private final BalanceEquationCanvas canvas;

    public BalanceEquationModule( BCEGlobalProperties globalProperties ) {
        super( globalProperties.getFrame(), BCEStrings.BALANCE_EQUATION, new BCEClock(), true /* startsPaused */ );
        model = new BalanceEquationModel( globalProperties.isDev() );
        canvas = new BalanceEquationCanvas( model, globalProperties, this );
        setSimulationPanel( canvas );
    }

    @Override
    public void reset() {
        super.reset();
        model.reset();
        canvas.reset();
    }
}
