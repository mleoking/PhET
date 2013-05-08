// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.balancelab.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import edu.colorado.phet.balanceandtorque.common.model.BalanceModel;
import edu.colorado.phet.balanceandtorque.common.view.BasicBalanceCanvas;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;

import static edu.colorado.phet.common.piccolophet.PhetPCanvas.CenteredStage.DEFAULT_STAGE_SIZE;

/**
 * Main view class for the "Balance Lab" module.
 *
 * @author John Blanco
 */
public class BalanceLabCanvas extends BasicBalanceCanvas {

    private static final int GAME_BUTTON_HIDDEN_TIME = 2; // In seconds.

    protected MassKitSelectionNode fullMassKitSelectionNode;
    protected SimpleMassKitSelectionNode simpleMassKitSelectionNode;
    private int gameButtonVizCountdown = GAME_BUTTON_HIDDEN_TIME;
    private final Timer gameButtonVizTimer;
    private final TextButtonNode gameButton;

    public enum MassKitMode {SIMPLE, FULL}

    public Property<MassKitMode> massKitMode = new Property<MassKitMode>( MassKitMode.SIMPLE );

    public BalanceLabCanvas( final BalanceModel model, final BooleanProperty inGame ) {
        super( model );

        // Add the mass kits, which is the place where the user will get the
        // objects that can be placed on the balance.
        fullMassKitSelectionNode = new MassKitSelectionNode( new Property<Integer>( 0 ), model, mvt, this );
        final ControlPanelNode fullMassKit = new ControlPanelNode( fullMassKitSelectionNode );
        nonMassLayer.addChild( fullMassKit );
        simpleMassKitSelectionNode = new SimpleMassKitSelectionNode( new Property<Integer>( 0 ), model, mvt, this );
        final ControlPanelNode simpleMassKit = new ControlPanelNode( simpleMassKitSelectionNode );
        nonMassLayer.addChild( simpleMassKit );

        // Control the mass kit visibility.
        massKitMode.addObserver( new VoidFunction1<MassKitMode>() {
            public void apply( MassKitMode massKitMode ) {
                fullMassKit.setVisible( massKitMode == MassKitMode.FULL );
                simpleMassKit.setVisible( massKitMode == MassKitMode.SIMPLE );
            }
        } );

        // Lay out the control panels.
        double minDistanceToEdge = 20; // Value chosen based on visual appearance.
        double controlPanelCenterX = Math.min( DEFAULT_STAGE_SIZE.getWidth() - fullMassKit.getFullBoundsReference().width / 2 - minDistanceToEdge,
                                               DEFAULT_STAGE_SIZE.getWidth() - controlPanel.getFullBoundsReference().width / 2 - minDistanceToEdge );
        fullMassKit.setOffset( controlPanelCenterX - fullMassKit.getFullBoundsReference().width / 2,
                               mvt.modelToViewY( 0 ) - fullMassKit.getFullBoundsReference().height - 10 );
        controlPanel.setOffset( controlPanelCenterX - controlPanel.getFullBoundsReference().width / 2,
                                fullMassKit.getFullBoundsReference().getMinY() - controlPanel.getFullBoundsReference().height - 10 );
        simpleMassKit.setOffset( controlPanelCenterX - simpleMassKit.getFullBoundsReference().width / 2,
                                 controlPanel.getFullBoundsReference().getMaxY() + 20 );

        // Add button for moving to the game.
        gameButton = new TextButtonNode( "Game", new PhetFont( 24, true ), Color.CYAN );
        gameButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                inGame.set( true );
            }
        } );
        nonMassLayer.addChild( gameButton );

        // Set up the timer used to control visibility of the game button.
        gameButtonVizTimer = new Timer( 1000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                gameButtonVizCountdown--;
                if ( gameButtonVizCountdown <= 0 ) {
                    gameButtonVizTimer.stop();
                    gameButton.setVisible( true );
                }
            }
        } );
    }

    public void restartGameButtonVizCountdown() {
        gameButton.setVisible( false );
        gameButtonVizTimer.restart();
    }

    @Override public void reset() {
        super.reset();
        fullMassKitSelectionNode.reset();
    }
}
