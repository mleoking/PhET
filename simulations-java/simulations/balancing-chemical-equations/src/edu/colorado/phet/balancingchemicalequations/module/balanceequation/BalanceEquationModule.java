// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.balanceequation;

import java.awt.Frame;

import edu.colorado.phet.balancingchemicalequations.BCEModule;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.model.BCEClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * The "Balance Equation" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalanceEquationModule extends BCEModule {

    public BalanceEquationModule( Frame parentFrame ) {
        super( parentFrame, BCEStrings.TITLE_BALANCE_EQUATION, new BCEClock(), true /* startsPaused */ );
        setSimulationPanel( new PhetPCanvas() );//XXX
    }
}
