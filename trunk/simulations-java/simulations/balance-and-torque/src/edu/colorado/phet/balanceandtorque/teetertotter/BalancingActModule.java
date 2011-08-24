// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter;

import edu.colorado.phet.balanceandtorque.teetertotter.model.BalancingActModel;
import edu.colorado.phet.balanceandtorque.teetertotter.view.BalancingActCanvas;
import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * Main module for this tab.
 *
 * @author John Blanco
 */
public class BalancingActModule extends Module {

    BalancingActModel model;

    public BalancingActModule() {
        this( new BalancingActModel() );
        setClockControlPanel( null );
        getModulePanel().setLogoPanel( null );
    }

    private BalancingActModule( BalancingActModel model ) {
        // TODO: i18n
        super( "Balance", model.getClock() );
        this.model = model;
        setSimulationPanel( new BalancingActCanvas( model ) );
        reset();
    }

    @Override public void reset() {
        model.reset();
    }
}
