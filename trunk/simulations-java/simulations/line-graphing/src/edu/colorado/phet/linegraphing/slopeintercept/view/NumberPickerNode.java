// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.DynamicCursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Number picker based on design by Ariel Paul.
 * The number sits on top of a background that is composed of two buttons, referred to herein as top button and bottom button.
 * Pressing the top button increments, pressing the bottom button decrements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NumberPickerNode extends PhetPNode {

    // Data structure for picker color scheme
    public static class NumberPickerColorScheme {

        public final Color topNormalColor, topHighlightColor;
        public final Color bottomNormalColor, bottomHighlightColor;
        public final Color disabledColor, shadowColor;

        public NumberPickerColorScheme( Color topNormalColor, Color topHighlightColor,
                                        Color bottomNormalColor, Color bottomHighlightColor,
                                        Color disabledColor, Color shadowColor ) {
            this.topNormalColor = topNormalColor;
            this.topHighlightColor = topHighlightColor;
            this.bottomNormalColor = bottomNormalColor;
            this.bottomHighlightColor = bottomHighlightColor;
            this.disabledColor = disabledColor;
            this.shadowColor = shadowColor;
        }
    }

    private final Property<Boolean> topEnabled, bottomEnabled;

    public NumberPickerNode( IUserComponent userComponent,
                             final Property<Double> value, final Property<DoubleRange> range, final double delta, final boolean abs,
                             PhetFont font, final NumberFormat format, final NumberPickerColorScheme colorScheme ) {

        topEnabled = new Property<Boolean>( true );
        bottomEnabled = new Property<Boolean>( true );

        final PText textNode = new PText();
        textNode.setFont( font );

        // compute max text width, based on range
        textNode.setText( format.format( abs ? Math.abs( range.get().getMin() ) : range.get().getMin() ) );
        double minValueWidth = textNode.getFullBoundsReference().getWidth();
        textNode.setText( format.format( abs ? Math.abs( range.get().getMax() ) : range.get().getMax() ) );
        double maxValueWidth = textNode.getFullBoundsReference().getWidth();
        final double maxWidth = Math.max( minValueWidth, maxValueWidth );

        // compute max text height
        textNode.setText( "X" );
        final double maxHeight = textNode.getFullBoundsReference().getHeight();

        final double xMargin = 6;
        final double yMargin = 6;
        final double buttonWidth = maxWidth + ( 2 * xMargin );
        final double buttonHeight = ( maxHeight / 2 ) + ( 2 * yMargin );
        final PPath topButtonNode = new PPath( createTopButtonShape( buttonWidth, buttonHeight ) );
        final PPath bottomButtonNode = new PPath( createBottomButtonShape( buttonWidth, buttonHeight ) );
        PPath topShadowNode = new PPath( topButtonNode.getPathReference() );
        PPath bottomShadowNode = new PPath( bottomButtonNode.getPathReference() );

        // fill colors
        topButtonNode.setPaint( colorScheme.topNormalColor );
        bottomButtonNode.setPaint( colorScheme.bottomNormalColor );
        topShadowNode.setPaint( colorScheme.shadowColor );
        bottomShadowNode.setPaint( colorScheme.shadowColor );

        // strokes
        topButtonNode.setStroke( null );
        bottomButtonNode.setStroke( null );
        topShadowNode.setStroke( null );
        bottomShadowNode.setStroke( null );

        // non-interactive
        textNode.setPickable( false );
        topShadowNode.setPickable( false );
        bottomShadowNode.setPickable( false );

        // rendering order
        {
            addChild( topShadowNode );
            addChild( bottomShadowNode );
            addChild( topButtonNode );
            addChild( bottomButtonNode );
            addChild( textNode );
        }

        // layout
        {
            final double shadowXOffset = 3;
            final double shadowYOffset = 3;
            topButtonNode.setOffset( 0, 0 );
            topShadowNode.setOffset( topButtonNode.getXOffset() + shadowXOffset, topButtonNode.getYOffset() + shadowYOffset );
            bottomButtonNode.setOffset( topButtonNode.getXOffset(), topButtonNode.getFullBoundsReference().getMaxY() );
            bottomShadowNode.setOffset( bottomButtonNode.getXOffset() + shadowXOffset, bottomButtonNode.getYOffset() + shadowYOffset );
            // textNode offset is set dynamically, to keep value centered
        }

        // enabled/disabled
        final DynamicCursorHandler topButtonCursorHandler = new DynamicCursorHandler();
        final DynamicCursorHandler bottomButtonCursorHandler = new DynamicCursorHandler();
        topEnabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean enabled ) {
                topButtonNode.setPaint( enabled ? colorScheme.topNormalColor : colorScheme.disabledColor );
                topButtonCursorHandler.setCursor( topEnabled.get() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR );
            }
        } );
        bottomEnabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean enabled ) {
                bottomButtonNode.setPaint( enabled ? colorScheme.bottomNormalColor : colorScheme.disabledColor );
                bottomButtonCursorHandler.setCursor( bottomEnabled.get() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR );
            }
        } );

        // sync with value
        value.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                double adjustedValue = abs ? Math.abs( value ) : value;
                textNode.setText( format.format( adjustedValue ) );
                textNode.setOffset( topButtonNode.getFullBoundsReference().getCenterX() - ( textNode.getFullBoundsReference().getWidth() / 2 ),
                                    topButtonNode.getFullBoundsReference().getMaxY() - ( textNode.getFullBoundsReference().getHeight() / 2 ) );
                topEnabled.set( value < range.get().getMax() );
                bottomEnabled.set( value > range.get().getMin() );
            }
        } );

        // button handlers
        topButtonNode.addInputEventListener( topButtonCursorHandler  );
        bottomButtonNode.addInputEventListener( bottomButtonCursorHandler  );
        topButtonNode.addInputEventListener( new IncrementButtonHandler( userComponent, topButtonNode, colorScheme.topNormalColor, colorScheme.topHighlightColor,
                                                                         topEnabled, value, range, delta ) );
        bottomButtonNode.addInputEventListener( new DecrementButtonHandler( userComponent, bottomButtonNode, colorScheme.bottomNormalColor, colorScheme.bottomHighlightColor,
                                                                            bottomEnabled, value, range, delta ) );
    }

    private static final Shape createTopButtonShape( double width, double height ) {
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( 0, height );
        path.lineTo( 0, 0.75 * height );
        path.lineTo( 0.1 * width, 0 );
        path.lineTo( 0.9 * width, 0 );
        path.lineTo( width, 0.75 * height );
        path.lineTo( width, height );
        path.closePath();
        return path.getGeneralPath();
    }

    // Bottom shape is same of the top shape, but facing down.
    private static final Shape createBottomButtonShape( double width, double height ) {
        AffineTransform transform = AffineTransform.getRotateInstance( Math.PI );
        transform.translate( -width, -height );
        return transform.createTransformedShape( createTopButtonShape( width, height ) );
    }

    private static class IncrementButtonHandler extends ButtonHandler {
        public IncrementButtonHandler( final IUserComponent userComponent,
                                       PNode buttonNode,
                                       Color normalColor, Color highlightColor,
                                       Property<Boolean> enabled,
                                       final Property<Double> value, final Property<DoubleRange> range, final double delta ) {
            super( buttonNode, normalColor, highlightColor, enabled,
                   new VoidFunction0() {
                       public void apply() {
                           final double newValue = Math.min( range.get().getMax(), value.get() + delta );
                           SimSharingManager.sendUserMessage( UserComponentChain.chain( userComponent, "incrementButton" ), UserComponentTypes.button, UserActions.pressed,
                                                              ParameterSet.parameterSet( ParameterKeys.value, newValue ) );
                           value.set( newValue );
                       }
                   } );
        }
    }

    private static class DecrementButtonHandler extends ButtonHandler {
        public DecrementButtonHandler( final IUserComponent userComponent,
                                       PNode buttonNode,
                                       Color normalColor, Color highlightColor,
                                       Property<Boolean> enabled,
                                       final Property<Double> value, final Property<DoubleRange> range, final double delta ) {
            super( buttonNode, normalColor, highlightColor, enabled,
                   new VoidFunction0() {
                       public void apply() {
                           final double newValue = Math.min( range.get().getMax(), value.get() - delta );
                           SimSharingManager.sendUserMessage( UserComponentChain.chain( userComponent, "decrementButton" ), UserComponentTypes.button, UserActions.pressed,
                                                              ParameterSet.parameterSet( ParameterKeys.value, newValue ) );
                           value.set( newValue );
                       }
                   } );
        }
    }

    private static abstract class ButtonHandler extends PBasicInputEventHandler {

        private final PNode buttonNode;
        private final Color normalColor, highlightColor;
        private final Property<Boolean> enabled;
        private final VoidFunction0 buttonFired;
        private final Point2D buttonNodeNormalOffset;
        private final Timer timer;

        private boolean mouseOver = false;
        private boolean fireContinuously = false;

        public ButtonHandler( final PNode buttonNode,
                              Color normalColor, Color highlightColor,
                              final Property<Boolean> enabled,
                              final VoidFunction0 buttonFired ) {

            this.buttonNode = buttonNode;
            this.normalColor = normalColor;
            this.highlightColor = highlightColor;
            this.enabled = enabled;
            this.buttonFired = buttonFired;

            buttonNodeNormalOffset = buttonNode.getOffset();

            // If holding down the button, then spin continuously.
            timer = new Timer( 200, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    fireContinuously = true;
                    buttonFired.apply();
                }
            } );
            timer.setInitialDelay( 500 );

            // stop the timer when the button is disabled
            enabled.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean enabled ) {
                    if ( timer.isRunning() ) {
                        timer.stop();
                        buttonNode.setOffset( buttonNodeNormalOffset );
                    }
                }
            } );
        }

        @Override public void mouseEntered( PInputEvent event ) {
            super.mouseEntered( event );
            mouseOver = true;
            if ( enabled.get() ) {
                buttonNode.setPaint( highlightColor );
            }
        }

        @Override public void mouseExited( PInputEvent event ) {
            super.mouseExited( event );
            mouseOver = false;
            if ( enabled.get() ) {
                buttonNode.setPaint( normalColor );
            }
        }

        @Override public void mousePressed( PInputEvent event ) {
            super.mousePressed( event );
            if ( enabled.get() ) {
                buttonNode.setOffset( buttonNodeNormalOffset.getX() + 1, buttonNodeNormalOffset.getY() + 1 );
                timer.start();
            }
        }

        @Override public void mouseReleased( PInputEvent event ) {
            super.mouseReleased( event );
            timer.stop();
            buttonNode.setOffset( buttonNodeNormalOffset );
            if ( mouseOver && enabled.get() && !fireContinuously ) {
                buttonFired.apply();
            }
            fireContinuously = false;
        }
    }

    // test
    public static void main( String[] args ) {

        final Color disabledColor = new Color( 210, 210, 210 );
        NumberPickerColorScheme colorScheme = new NumberPickerColorScheme( new Color( 0, 225, 0 ), new Color( 0, 255, 0 ),
                                                                           new Color( 0, 180, 0 ), new Color( 0, 255, 0 ),
                                                                           disabledColor, Color.DARK_GRAY );
        Property<Double> value = new Property<Double>( 3d );
        Property<DoubleRange> range = new Property<DoubleRange>( new DoubleRange( -10, 10 ) );

        NumberPickerNode pickerNode = new NumberPickerNode( new UserComponent( "myPicker" ),
                                                            value, range, 1, false,
                                                            new PhetFont( Font.BOLD, 40 ), new DefaultDecimalFormat( "0" ), colorScheme );
        pickerNode.setOffset( 100, 100 );

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 800, 600 ) );
        canvas.getLayer().addChild( pickerNode );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
