// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.balanceandtorque.stanfordstudy;

import edu.colorado.phet.balanceandtorque.balancelab.model.BalanceLabModel;
import edu.colorado.phet.balanceandtorque.balancelab.view.BalanceLabCanvas;
import edu.colorado.phet.balanceandtorque.game.model.BalanceGameModel;
import edu.colorado.phet.balanceandtorque.game.view.BalanceGameCanvas;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;

import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Strings.BALANCE_LAB;
import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing.UserComponents.balanceLabTab;
import static edu.colorado.phet.balanceandtorque.balancelab.view.BalanceLabCanvas.MassKitMode.FULL;
import static edu.colorado.phet.balanceandtorque.balancelab.view.BalanceLabCanvas.MassKitMode.SIMPLE;

/**
 * A module that acts as a combination of the "Balance Lab" and "Balance Game"
 * modules.  This was created for a study being conducted by Stanford in May
 * of 2013.
 *
 * @author John Blanco
 */
public class BalanceLabGameComboModule extends SimSharingPiccoloModule {

    private final BalanceLabModel balanceLabModel;
    private final BalanceLabCanvas balanceLabCanvas;
    private final BalanceGameModel balanceGameModel;
    private final BalanceGameCanvas balanceGameCanvas;

    // Boolean property that controls whether user is in the game or the lab.
    private final BooleanProperty inGame = new BooleanProperty( false );

    public BalanceLabGameComboModule() {
        this( new BalanceLabModel() );
        setLogoPanel( null );
    }

    private BalanceLabGameComboModule( BalanceLabModel model ) {
        super( balanceLabTab, BALANCE_LAB, model.getClock() );
        this.balanceLabModel = model;
        balanceLabCanvas = new BalanceLabCanvas( model, inGame );
        setSimulationPanel( balanceLabCanvas );
        balanceGameModel = new BalanceGameModel();
        balanceGameCanvas = new BalanceGameCanvas( this.balanceGameModel );
        setClockControlPanel( null );
        reset();

        // Switch canvas when the mode changes.
        inGame.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean inGame ) {
                boolean firstTimeGameCanvasShown = true;
                if ( inGame ) {
                    setSimulationPanel( balanceGameCanvas );
                }
                else {
                    balanceLabCanvas.restartGameButtonVizCountdown();
                    setSimulationPanel( balanceLabCanvas );
                    balanceLabCanvas.massKitMode.set( firstTimeGameCanvasShown ? SIMPLE : FULL );
                    firstTimeGameCanvasShown = false;
                }
            }
        } );
    }

    @Override public void reset() {
        balanceLabModel.reset();
        // TODO: Any need to reset game?
    }

    public enum Mode {LAB, GAME}

    ;
}
