// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.balancelab;

import edu.colorado.phet.balanceandtorque.balancelab.model.BalanceLabModel;
import edu.colorado.phet.balanceandtorque.balancelab.view.BalanceLabCanvas;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;

import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Strings.BALANCE_LAB;
import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing.UserComponents.balanceLabTab;

/**
 * The "Balance Lab" module.
 *
 * @author John Blanco
 */
public class BalanceLabModule extends SimSharingPiccoloModule {

    private BalanceLabModel model;

    public BalanceLabModule() {
        this( new BalanceLabModel() );
        setLogoPanel( null );
    }

    private BalanceLabModule( BalanceLabModel model ) {
        super( balanceLabTab, BALANCE_LAB, model.getClock() );
        this.model = model;
        setSimulationPanel( new BalanceLabCanvas( model, new BooleanProperty( false ) ) );
        setClockControlPanel( null );
        reset();
    }

    @Override public void reset() {
        model.reset();
    }
}
