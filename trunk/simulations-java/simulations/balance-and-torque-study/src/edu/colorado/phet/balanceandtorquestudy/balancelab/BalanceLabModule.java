// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.balancelab;

import edu.colorado.phet.balanceandtorquestudy.balancelab.model.BalanceLabModel;
import edu.colorado.phet.balanceandtorquestudy.balancelab.view.BalanceLabCanvas;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;

import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueResources.Strings.BALANCE_LAB;
import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueSimSharing.UserComponents.balanceLabTab;

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
        setSimulationPanel( new BalanceLabCanvas( model, new BooleanProperty( false ), new Property<Integer>( 0 ) ) );
        setClockControlPanel( null );
        reset();
    }

    @Override public void reset() {
        model.reset();
    }
}
