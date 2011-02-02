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
    private final BalanceEquationCanvas canvas;

    public BalanceEquationModule( Frame parentFrame, boolean dev ) {
        super( parentFrame, BCEStrings.BALANCE_EQUATION, new BCEClock(), true /* startsPaused */ );
        model = new BalanceEquationModel();
        canvas = new BalanceEquationCanvas( parentFrame, this, model, dev );
        setSimulationPanel( canvas );
    }

    public void setMoleculesVisible( boolean moleculesVisible ) {
        canvas.setMoleculesVisible( moleculesVisible );
    }

    @Override
    public void reset() {
        model.reset();
        canvas.reset();
    }
}
