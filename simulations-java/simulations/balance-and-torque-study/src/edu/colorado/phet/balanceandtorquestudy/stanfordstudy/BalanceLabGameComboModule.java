// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.balanceandtorque.stanfordstudy;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import edu.colorado.phet.balanceandtorque.balancelab.model.BalanceLabModel;
import edu.colorado.phet.balanceandtorque.balancelab.view.BalanceLabCanvas;
import edu.colorado.phet.balanceandtorque.game.model.BalanceGameModel;
import edu.colorado.phet.balanceandtorque.game.view.BalanceGameCanvas;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
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

    public static final int TOTAL_ALLOWED_TIME = 15 * 60;

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

    // Timer and countdown value for overall game timer.
    private Property<Integer> totalTimeCountdown = new Property<Integer>( TOTAL_ALLOWED_TIME );
    private Timer totalTimeTimer = new Timer( 1000, new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
            totalTimeCountdown.set( totalTimeCountdown.get() - 1 );
            if ( totalTimeCountdown.get() == 0 ) {
                totalTimeTimer.stop();
            }
        }
    } );

    public BalanceLabGameComboModule() {
        this( new BalanceLabModel() );
        setLogoPanel( null );
    }

    private BalanceLabGameComboModule( BalanceLabModel model ) {
        super( balanceLabTab, BALANCE_LAB, model.getClock() );
        this.balanceLabModel = model;
        balanceLabCanvas = new BalanceLabCanvas( model, inGame, totalTimeCountdown );
        balanceLabCanvas.addWorldChild( missedChallengeNode );
        setSimulationPanel( balanceLabCanvas );
        balanceGameModel = new BalanceGameModel();
        balanceGameModel.getClock().start(); // This is needed since it won't be started by the framework.
        balanceGameModel.startGame();
        balanceGameCanvas = new BalanceGameCanvas( this.balanceGameModel, inGame, totalTimeCountdown );
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
                    if ( !totalTimeTimer.isRunning() ) {
                        totalTimeTimer.restart();
                    }
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
                    setSimulationPanel( balanceLabCanvas );
                    balanceLabCanvas.massKitMode.set( firstTimeGameCanvasShown ? SIMPLE : FULL );
                    firstTimeGameCanvasShown = false;
                }
            }
        } );
    }

    @Override public void reset() {
        balanceLabModel.reset();
    }

    public enum Mode {LAB, GAME}

    ;
}
