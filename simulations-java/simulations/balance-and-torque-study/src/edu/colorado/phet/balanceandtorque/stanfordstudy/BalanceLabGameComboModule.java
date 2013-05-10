// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.balanceandtorque.stanfordstudy;

import java.awt.Color;

import edu.colorado.phet.balanceandtorque.balancelab.model.BalanceLabModel;
import edu.colorado.phet.balanceandtorque.balancelab.view.BalanceLabCanvas;
import edu.colorado.phet.balanceandtorque.game.model.BalanceGameModel;
import edu.colorado.phet.balanceandtorque.game.view.BalanceGameCanvas;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

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

    // Flag indicating if user has played the game.
    private boolean gamePlayedAtLeastOnce = false;

    // Node that displays missed challenges.
    private PNode missedChallengeNode = new PNode();

    public BalanceLabGameComboModule() {
        this( new BalanceLabModel() );
        setLogoPanel( null );
    }

    private BalanceLabGameComboModule( BalanceLabModel model ) {
        super( balanceLabTab, BALANCE_LAB, model.getClock() );
        this.balanceLabModel = model;
        balanceLabCanvas = new BalanceLabCanvas( model, inGame );
        balanceLabCanvas.addWorldChild( missedChallengeNode );
        setSimulationPanel( balanceLabCanvas );
        balanceGameModel = new BalanceGameModel();
        balanceGameModel.getClock().start(); // This is needed since it won't be started by the framework.
        balanceGameModel.startGame();
        balanceGameCanvas = new BalanceGameCanvas( this.balanceGameModel, inGame );
        setClockControlPanel( null );

        // Switch canvas when the mode changes.
        inGame.addObserver( new VoidFunction1<Boolean>() {
            boolean firstTimeGameCanvasShown = true;

            public void apply( Boolean inGame ) {
                if ( inGame ) {
                    balanceLabModel.reset();
                    balanceGameModel.resumeGame();
                    setSimulationPanel( balanceGameCanvas );
                    gamePlayedAtLeastOnce = true;
                }
                else {
                    if ( gamePlayedAtLeastOnce ) {
                        // Update missed challenge.
                        missedChallengeNode.removeAllChildren();
                        PNode missedChallengeImage = new PImage( balanceGameCanvas.getChallengeLayer().toImage() );
                        missedChallengeImage.scale( 0.55 );
                        missedChallengeNode.addChild( new ControlPanelNode( new VBox( 0,
                                                                                      new PhetPText( "Challenge you missed:", new PhetFont( 24 ) ),
                                                                                      missedChallengeImage ),
                                                                            new Color( 204, 242, 255 )
                        ) );
                        missedChallengeNode.setOffset( PhetPCanvas.CenteredStage.DEFAULT_STAGE_SIZE.getWidth() / 2 - missedChallengeNode.getFullBoundsReference().width / 2, 20 );
                    }
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
