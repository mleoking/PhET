// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.balancelab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import edu.colorado.phet.balanceandtorque.common.model.BalanceModel;
import edu.colorado.phet.balanceandtorque.common.view.BasicBalanceCanvas;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;

import static edu.colorado.phet.common.piccolophet.PhetPCanvas.CenteredStage.DEFAULT_STAGE_SIZE;

/**
 * Main view class for the "Balance Lab" module.
 *
 * @author John Blanco
 */
public class BalanceLabCanvas extends BasicBalanceCanvas {

    private static final int CHALLENGE_UNAVAILABLE_TIME = 5; // In seconds.

    protected MassKitSelectionNode fullMassKitSelectionNode;
    protected SimpleMassKitSelectionNode simpleMassKitSelectionNode;
    private Property<Integer> challengeAllowedCountdown = new Property<Integer>( CHALLENGE_UNAVAILABLE_TIME );
    private final Timer challengeAllowedTimer;

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

        // Add the star-shaped button that the user will use to move to the
        // challenge.
        final StarButton starButton = new StarButton( 48, 80, false, new PBasicInputEventHandler() {
            @Override public void mouseClicked( PInputEvent event ) {
                inGame.set( true );
            }
        } );
        starButton.setOffset( 100, 100 );
        starButton.addChild( new HTMLNode( "<center>Begin<br>Challenge</center>", Color.WHITE, new PhetFont( 16, true ) ) {{
            setOffset( -getFullBoundsReference().width / 2, -getFullBoundsReference().height / 2 - 4 );
        }} );
        nonMassLayer.addChild( starButton );

        // Add the explanatory text about moving to the challenge.
        final PNode explanationText = new HTMLNode( "The challenge <br> is open, but <br> you can still <br> explore if you <br> want.",
                                                    Color.BLACK,
                                                    new PhetFont( 16, false ) );
        explanationText.setOffset( starButton.getFullBoundsReference().getCenterX() - explanationText.getFullBoundsReference().width / 2,
                                   starButton.getFullBoundsReference().getMaxY() + 5 );
        nonMassLayer.addChild( explanationText );

        // Add the countdown display.
        final PNode countdownDisplay = new MinSecNode( challengeAllowedCountdown );
        nonMassLayer.addChild( countdownDisplay );

        // Set up the timer used to control visibility of the game button.
        challengeAllowedTimer = new Timer( 1000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                challengeAllowedCountdown.set( challengeAllowedCountdown.get() - 1 );
                if ( challengeAllowedCountdown.get() <= 0 ) {
                    challengeAllowedTimer.stop();
                }
            }
        } );

        // Listen to the countdown and set various states and visibility.
        challengeAllowedCountdown.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer seconds ) {
                countdownDisplay.centerFullBoundsOnPoint( starButton.getFullBoundsReference().getCenterX(), starButton.getFullBoundsReference().getCenterY() );
                countdownDisplay.setVisible( seconds > 0 );
                starButton.setEnabled( seconds <= 0 );
                explanationText.setVisible( seconds <= 0 );
            }
        } );
    }

    public void restartGameButtonVizCountdown() {
        challengeAllowedTimer.restart();
        challengeAllowedCountdown.set( CHALLENGE_UNAVAILABLE_TIME );
    }

    @Override public void reset() {
        super.reset();
        fullMassKitSelectionNode.reset();
    }

    private static class StarButton extends PhetPPath {
        private static final Stroke STROKE = new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        private static final Color ENABLED_STROKE_COLOR = Color.BLACK;
        private static final Color DISABLED_STROKE_COLOR = Color.LIGHT_GRAY;
        private static final Color ENABLED_FILL_COLOR = Color.BLUE;
        private static final Color DISABLED_FILL_COLOR = Color.LIGHT_GRAY;
        private static final float DISABLED_OPACITY = 0.5f;
        private static final int NUM_POINTS = 5;

        private CursorHandler myCursorHandler = new CursorHandler();

        private StarButton( double innerRadius, double outerRadius, boolean initialEnabledState, PInputEventListener inputEventHandler ) {
            super( ENABLED_FILL_COLOR, STROKE, Color.BLACK );
            DoubleGeneralPath starPath = new DoubleGeneralPath();
            double angle = -Math.PI / 2;
            starPath.moveTo( Math.cos( angle ) * outerRadius, Math.sin( angle ) * outerRadius );
            for ( int i = 0; i < NUM_POINTS * 2; i++ ) {
                angle += Math.PI / NUM_POINTS;
                double length = i % 2 == 0 ? innerRadius : outerRadius;
                starPath.lineTo( Math.cos( angle ) * length, Math.sin( angle ) * length );
            }
            starPath.closePath();
            setPathTo( starPath.getGeneralPath() );
            setEnabled( initialEnabledState );
            addInputEventListener( inputEventHandler );
            addInputEventListener( new CursorHandler() );
        }

        private void setEnabled( boolean enabled ) {
            setStrokePaint( enabled ? ENABLED_STROKE_COLOR : DISABLED_STROKE_COLOR );
            setPaint( enabled ? ENABLED_FILL_COLOR : DISABLED_FILL_COLOR );
            setTransparency( enabled ? 1.0f : DISABLED_OPACITY );
            setPickable( enabled );
            setChildrenPickable( enabled );
        }
    }
}
