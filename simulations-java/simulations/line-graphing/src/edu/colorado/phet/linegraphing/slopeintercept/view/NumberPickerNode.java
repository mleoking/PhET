// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
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
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.DynamicCursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.OutlineTextNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Number picker based on design by Ariel Paul.
 * The number sits on top of a background that is composed of two buttons, referred to herein as top button and bottom button.
 * Pressing the top button increments, pressing the bottom button decrements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NumberPickerNode extends PhetPNode {

    //TODO move colors in this file to LGColors
    private static final Color BUTTON_TOP_COLOR = Color.WHITE;
    private static final Color BUTTON_BOTTOM_COLOR = new Color( 235, 235, 235 );
    private static final Color BUTTON_DISABLED_COLOR = new Color( 210, 210, 210 );
    private static final Color BUTTON_SHADOW_COLOR = new Color( 120, 120, 120 );
    private static final Color BUTTON_STROKE_COLOR = new Color( 135, 135, 135 );
    private static final double NUMBER_OUTLINE_WIDTH = 0.5;
    private static final Color NUMBER_OUTLINE_COLOR = Color.BLACK;

    // Picker for intercept
    public static class InterceptPickerNode extends NumberPickerNode {
        public InterceptPickerNode( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format, boolean abs ) {
            super( userComponent, value, range, 1, abs, font, format,
                   new NumberPickerColorScheme( LGColors.INTERCEPT, BUTTON_TOP_COLOR, BUTTON_BOTTOM_COLOR, new Color( 255, 255, 225 ), BUTTON_DISABLED_COLOR, BUTTON_SHADOW_COLOR ) );
        }
    }

    // Picker for slope (rise or run)
    public static class SlopePickerNode extends NumberPickerNode {
        public SlopePickerNode( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format, boolean abs ) {
            super( userComponent, value, range, 1, abs, font, format,
                   new NumberPickerColorScheme( LGColors.SLOPE, BUTTON_TOP_COLOR, BUTTON_BOTTOM_COLOR, new Color( 224, 255, 211 ), BUTTON_DISABLED_COLOR, BUTTON_SHADOW_COLOR ) );
        }
    }

    // Data structure for picker color scheme
    public static class NumberPickerColorScheme {

        public final Color numberColor;
        private final Color gradientEndsColor, gradientCenterColor;
        public final Color highlightColor, disabledColor, shadowColor;

        public NumberPickerColorScheme( Color numberColor, Color gradientEndsColor, Color gradientCenterColor, Color highlightColor, Color disabledColor, Color shadowColor ) {
            this.numberColor = numberColor;
            this.gradientEndsColor = gradientEndsColor;
            this.gradientCenterColor = gradientCenterColor;
            this.highlightColor = highlightColor;
            this.disabledColor = disabledColor;
            this.shadowColor = shadowColor;
        }

        public Paint getTopButtonPaint( double height ) {
            return new GradientPaint( 0f, 0f, gradientEndsColor, 0f, (float) height, gradientCenterColor );
        }

        public Paint getBottomButtonPaint( double height ) {
            return new GradientPaint( 0f, (float) height, gradientCenterColor, 0f, (float) ( 2 * height ), gradientEndsColor );
        }
    }

    private static final ImmutableVector2D SHADOW_OFFSET = new ImmutableVector2D( 2, 2 ); // offset of drop shadow from buttons

    private final Property<Boolean> topEnabled, bottomEnabled; // enabled state of the top and bottom buttons

    /**
     * Constructor
     *
     * @param userComponent identifier for data-collection messages
     * @param value         the value to display and modify
     * @param range         range of value, may change dynamically
     * @param delta         amount to increment or decrement
     * @param abs           true = display absolute value, false = display actual value, including minus sign
     * @param font          font used to display the value
     * @param format        formatter for the value
     * @param colorScheme   colors to use for the picker buttons
     */
    public NumberPickerNode( IUserComponent userComponent,
                             final Property<Double> value, final Property<DoubleRange> range, final double delta, final boolean abs,
                             PhetFont font, final NumberFormat format, final NumberPickerColorScheme colorScheme ) {

        topEnabled = new Property<Boolean>( true );
        bottomEnabled = new Property<Boolean>( true );

        final OutlineTextNode numberNode = new OutlineTextNode( "?", font, colorScheme.numberColor, NUMBER_OUTLINE_COLOR, NUMBER_OUTLINE_WIDTH );

        // compute max number width, based on range
        numberNode.setText( "20" ); //TODO this assumes 2 digits, better to compute based on range but range is dynamic.
        final double maxWidth = numberNode.getFullBoundsReference().getWidth();
        final double maxHeight = numberNode.getFullBoundsReference().getHeight();

        final double xMargin = 6;
        final double yMargin = 3;
        final double buttonWidth = maxWidth + ( 2 * xMargin );
        final double buttonHeight = ( maxHeight / 2 ) + ( 2 * yMargin );
        final PPath topButtonNode = new PPath( createTopButtonShape( buttonWidth, buttonHeight ) );
        final PPath bottomButtonNode = new PPath( createBottomButtonShape( buttonWidth, buttonHeight ) );
        PPath topShadowNode = new PPath( topButtonNode.getPathReference() );
        PPath bottomShadowNode = new PPath( bottomButtonNode.getPathReference() );

        // fill colors
        topButtonNode.setPaint( colorScheme.getTopButtonPaint( buttonHeight ) );
        bottomButtonNode.setPaint( colorScheme.getBottomButtonPaint( buttonHeight ) );
        topShadowNode.setPaint( colorScheme.shadowColor );
        bottomShadowNode.setPaint( colorScheme.shadowColor );

        // strokes
        topButtonNode.setStroke( new BasicStroke( 0.5f ) );
        topButtonNode.setStrokePaint( BUTTON_STROKE_COLOR );
        bottomButtonNode.setStroke( new BasicStroke( 0.25f ) );
        bottomButtonNode.setStrokePaint( BUTTON_STROKE_COLOR );
        topShadowNode.setStroke( null );
        bottomShadowNode.setStroke( null );

        // non-interactive
        numberNode.setPickable( false );
        topShadowNode.setPickable( false );
        bottomShadowNode.setPickable( false );

        // rendering order
        {
            addChild( topShadowNode );
            addChild( bottomShadowNode );
            addChild( topButtonNode );
            addChild( bottomButtonNode );
            addChild( numberNode );
        }

        // layout
        {
            topButtonNode.setOffset( 0, 0 );
            bottomButtonNode.setOffset( topButtonNode.getOffset() );
            topShadowNode.setOffset( topButtonNode.getXOffset() + SHADOW_OFFSET.getX(), topButtonNode.getYOffset() + SHADOW_OFFSET.getY() );
            bottomShadowNode.setOffset( bottomButtonNode.getXOffset() + SHADOW_OFFSET.getX(), bottomButtonNode.getYOffset() + SHADOW_OFFSET.getY() );
            // numberNode offset is set dynamically, to keep value centered
        }

        // enabled/disabled
        final DynamicCursorHandler topButtonCursorHandler = new DynamicCursorHandler();
        final DynamicCursorHandler bottomButtonCursorHandler = new DynamicCursorHandler();
        topEnabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean enabled ) {
                topButtonNode.setPaint( enabled ? colorScheme.getTopButtonPaint( buttonHeight ) : colorScheme.disabledColor );
                topButtonCursorHandler.setCursor( topEnabled.get() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR );
            }
        } );
        bottomEnabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean enabled ) {
                bottomButtonNode.setPaint( enabled ? colorScheme.getBottomButtonPaint( buttonHeight ) : colorScheme.disabledColor );
                bottomButtonCursorHandler.setCursor( bottomEnabled.get() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR );
            }
        } );

        // sync with value
        final RichSimpleObserver observer = new RichSimpleObserver() {
            @Override public void update() {
                double adjustedValue = abs ? Math.abs( value.get() ) : value.get();
                numberNode.setText( format.format( adjustedValue ) );
                numberNode.setOffset( ( topButtonNode.getFullBoundsReference().getWidth() - numberNode.getFullBoundsReference().getWidth() ) / 2,
                                      ( topButtonNode.getFullBoundsReference().getHeight() - numberNode.getFullBoundsReference().getHeight() / 2 ) );
                topEnabled.set( value.get() < range.get().getMax() );
                bottomEnabled.set( value.get() > range.get().getMin() );
            }
        };
        observer.observe( value, range );

        // button handlers
        topButtonNode.addInputEventListener( topButtonCursorHandler );
        bottomButtonNode.addInputEventListener( bottomButtonCursorHandler );
        topButtonNode.addInputEventListener( new IncrementButtonHandler( userComponent, topButtonNode, colorScheme.getTopButtonPaint( buttonHeight ), colorScheme.highlightColor,
                                                                         topEnabled, value, range, delta ) );
        bottomButtonNode.addInputEventListener( new DecrementButtonHandler( userComponent, bottomButtonNode, colorScheme.getBottomButtonPaint( buttonHeight ), colorScheme.highlightColor,
                                                                            bottomEnabled, value, range, delta ) );
    }

    // Creates the shape for the top button.
    private static final Shape createTopButtonShape( double width, double height ) {
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( 0, height );
        path.lineTo( 0, 0.65 * height );
        path.lineTo( 0.1 * width, 0 );
        path.lineTo( 0.9 * width, 0 );
        path.lineTo( width, 0.65 * height );
        path.lineTo( width, height );
        path.closePath();
        return path.getGeneralPath();
    }

    // Bottom shape is same of the top shape, but facing down. It shares the same coordinate frame with the top button.
    private static final Shape createBottomButtonShape( double width, double height ) {
        AffineTransform transform = AffineTransform.getRotateInstance( Math.PI );
        transform.translate( -width, -2 * height );
        return transform.createTransformedShape( createTopButtonShape( width, height ) );
    }

    // Button handler that increments a value.
    private static class IncrementButtonHandler extends ButtonHandler {
        public IncrementButtonHandler( final IUserComponent userComponent,
                                       PPath buttonNode,
                                       Paint normalColor, Paint highlightPaint,
                                       Property<Boolean> enabled,
                                       final Property<Double> value, final Property<DoubleRange> range, final double delta ) {
            super( buttonNode, normalColor, highlightPaint, enabled,
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

    // Button handler that decrements a value.
    private static class DecrementButtonHandler extends ButtonHandler {
        public DecrementButtonHandler( final IUserComponent userComponent,
                                       PPath buttonNode,
                                       Paint normalColor, Paint highlightPaint,
                                       Property<Boolean> enabled,
                                       final Property<Double> value, final Property<DoubleRange> range, final double delta ) {
            super( buttonNode, normalColor, highlightPaint, enabled,
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

    // Base class for button handlers. Behaves like a spinner if you press and hold.
    private static abstract class ButtonHandler extends PBasicInputEventHandler {

        private final PPath buttonNode;
        private final Paint normalPaint, highlightPaint;
        private final Property<Boolean> enabled;
        private final VoidFunction0 buttonFired;
        private final Point2D buttonNodeNormalOffset;
        private final Timer timer;

        private boolean mouseOver = false;
        private boolean isFiringContinuously = false; // true = is acting like a spinner, repeatedly firing while the mouse is pressed

        public ButtonHandler( final PPath buttonNode,
                              Paint normalPaint, Paint highlightPaint,
                              final Property<Boolean> enabled,
                              final VoidFunction0 buttonFired ) {

            this.buttonNode = buttonNode;
            this.normalPaint = normalPaint;
            this.highlightPaint = highlightPaint;
            this.enabled = enabled;
            this.buttonFired = buttonFired;

            buttonNodeNormalOffset = buttonNode.getOffset();

            // If holding down the button, then fire continuously.
            timer = new Timer( 200, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    isFiringContinuously = true;
                    buttonFired.apply();
                }
            } );
            timer.setInitialDelay( 500 );

            // Stop the timer when the button is disabled.
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
                buttonNode.setPaint( highlightPaint );
            }
        }

        @Override public void mouseExited( PInputEvent event ) {
            super.mouseExited( event );
            mouseOver = false;
            if ( enabled.get() ) {
                buttonNode.setPaint( normalPaint );
            }
        }

        @Override public void mousePressed( PInputEvent event ) {
            super.mousePressed( event );
            if ( enabled.get() ) {
                buttonNode.setOffset( buttonNodeNormalOffset.getX() + SHADOW_OFFSET.getX(), buttonNodeNormalOffset.getY() + SHADOW_OFFSET.getY() );
                timer.start();
            }
        }

        @Override public void mouseReleased( PInputEvent event ) {
            super.mouseReleased( event );
            timer.stop();
            buttonNode.setOffset( buttonNodeNormalOffset );
            if ( mouseOver && enabled.get() && !isFiringContinuously ) {
                buttonFired.apply();
            }
            isFiringContinuously = false;
        }
    }

    // test
    public static void main( String[] args ) {

        NumberPickerColorScheme colorScheme = new NumberPickerColorScheme( LGColors.SLOPE, BUTTON_TOP_COLOR, BUTTON_BOTTOM_COLOR,
                                                                           BUTTON_DISABLED_COLOR, BUTTON_SHADOW_COLOR, Color.DARK_GRAY );
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
