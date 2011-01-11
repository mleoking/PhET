// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.balanceequation;

import java.awt.Frame;

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

    public BalanceEquationModule( Frame parentFrame ) {
        super( parentFrame, BCEStrings.BALANCE_EQUATION, new BCEClock(), true /* startsPaused */ );
        model = new BalanceEquationModel();
        setSimulationPanel( new BalanceEquationCanvas( parentFrame, this, model ) );
    }

    @Override
    public void reset() {
        model.reset();
    }
}
