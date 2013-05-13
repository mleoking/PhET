// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.game;

import edu.colorado.phet.balanceandtorquestudy.game.model.BalanceGameModel;
import edu.colorado.phet.balanceandtorquestudy.game.view.BalanceGameCanvas;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;

import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueResources.Strings.GAME;
import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueSimSharing.UserComponents.gameTab;

/**
 * The "Game" module.
 *
 * @author John Blanco
 */
public class BalanceGameModule extends SimSharingPiccoloModule {

    public BalanceGameModule() {
        this( new BalanceGameModel() );
        setLogoPanel( null ); // Do this so that the logo panel won't appear if this module is used alone.
    }

    private BalanceGameModule( BalanceGameModel model ) {
        super( gameTab, GAME, model.getClock() );
        setClockControlPanel( null );
        setSimulationPanel( new BalanceGameCanvas( model, new BooleanProperty( true ), new Property<Integer>( 0 ) ) );
    }
}
