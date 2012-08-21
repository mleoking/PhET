// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.DynamicCursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.BackgroundColors;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.DownButtonImages;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.InterceptColors;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.PointColors;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.SlopeColors;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.UpButtonImages;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Spinner for a double value, with up and down arrows.
 * Unlike a JSpinner, the value is not directly editable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SpinnerNode extends PNode {

    // Constructor with default button images and default up/down functions.
    public SpinnerNode( IUserComponent userComponent,
                        SpinnerStateIndicator<Color> colors,
                        final Property<Double> value, final Property<DoubleRange> range, PhetFont font, final NumberFormat format ) {
        this( userComponent,
              colors,
              value, range, font, format,
              // default "up" function, increments by 1
              new Function0<Double>() {
                  public Double apply() {
                      return value.get() + 1;
                  }
              },
              // default "down" function, decrements by 1
              new Function0<Double>() {
                  public Double apply() {
                      return value.get() - 1;
                  }
              }
        );
    }

    // Constructor with default button images and custom up/down functions.
    public SpinnerNode( IUserComponent userComponent,
                        SpinnerStateIndicator<Color> colors,
                        final Property<Double> value, final Property<DoubleRange> range, PhetFont font, final NumberFormat format,
                        Function0<Double> upFunction, Function0<Double> downFunction ) {
        this( userComponent,
              new UpButtonImages( colors ), new DownButtonImages( colors ), new BackgroundColors( colors.highlighted, colors.pressed ),
              value, range, font, format,
              upFunction, downFunction );
    }

    /**
     * Fully-specified constructor.
     *
     * @param userComponent
     * @param upButtonImages images for the "up" button states
     * @param downButtonImages images for the "down" button states
     * @param backgroundColors colors for the background state
     * @param value the value property that is being manipulated by the spinner
     * @param range range of the value
     * @param font font used to display the value
     * @param format displayed format of the value
     * @param upFunction function called when the "up" (increment) button is pressed
     * @param downFunction function called when the "down" (decrement) button is pressed
     */
    private SpinnerNode( IUserComponent userComponent,
                         final SpinnerStateIndicator<Image> upButtonImages,
                         final SpinnerStateIndicator<Image> downButtonImages,
                         final SpinnerStateIndicator<Color> backgroundColors,
                         final Property<Double> value, final Property<DoubleRange> range, PhetFont font, final NumberFormat format,
                         Function0<Double> upFunction, Function0<Double> downFunction ) {

        // properties for the "up" (increment) control
        final BooleanProperty upPressed = new BooleanProperty( false );
        final BooleanProperty downPressed = new BooleanProperty( false );
        final CompositeProperty upEnabled = new CompositeProperty<Boolean>(
                new Function0<Boolean>() {
                    public Boolean apply() {
                        return value.get() < range.get().getMax();
                    }
                }, value, range );

        // properties for the "down" (decrement) control
        final BooleanProperty upInside = new BooleanProperty( false );
        final BooleanProperty downInside = new BooleanProperty( false );
        final CompositeProperty downEnabled = new CompositeProperty<Boolean>(
                new Function0<Boolean>() {
                    public Boolean apply() {
                        return value.get() > range.get().getMin();
                    }
                }, value, range );

        final PText textNode = new PhetPText( font );
        textNode.setPickable( false );

        // compute dimensions of the background behind the numeric value
        textNode.setText( "-20" );
        final double xMargin = 3;
        final double yMargin = 3;
        final double backgroundWidth = textNode.getFullBoundsReference().getWidth() + ( 2 * xMargin );
        final double backgroundHeight = textNode.getFullBoundsReference().getHeight() + ( 2 * yMargin );
        final double backgroundOverlap = 0.5;
        final double backgroundCornerRadius = 10;
        final Shape backgroundShape = new RoundRectangle2D.Double( 0, 0, backgroundWidth, backgroundHeight, backgroundCornerRadius, backgroundCornerRadius );

        // top half of the background, for "up"
        final PPath upBackgroundNode = new PPath() {{
            setPathTo( ShapeUtils.subtract( backgroundShape, new Rectangle2D.Double( 0, ( backgroundHeight / 2 ) - backgroundOverlap, backgroundWidth, backgroundHeight ) ) );
            setStroke( null );
            setPaint( backgroundColors.inactive );
            addInputEventListener( new BackgroundMouseHandler( this, new PDimension( backgroundWidth, backgroundHeight ),
                                                               upPressed, upInside, upEnabled,
                                                               backgroundColors ) );
        }};

        // bottom half of the background, for "down"
        final PNode downBackgroundNode = new PPath() {{
            setPathTo( ShapeUtils.subtract( backgroundShape, new Rectangle2D.Double( 0, 0, backgroundWidth, ( backgroundHeight / 2 ) - backgroundOverlap ) ) );
            setStroke( null );
            setPaint( backgroundColors.inactive );
            addInputEventListener( new BackgroundMouseHandler( this, new PDimension( backgroundWidth, backgroundHeight ),
                                                               downPressed, downInside, downEnabled,
                                                               backgroundColors ) );
        }};

        // up (increment) button
        SpinnerButtonNode upButton = new SpinnerButtonNode<Double>( UserComponentChain.chain( userComponent, "up" ),
                                                                    upButtonImages,
                                                                    upPressed, upInside, upEnabled,
                                                                    value,
                                                                    upFunction );

        // down (decrement) button
        SpinnerButtonNode downButton = new SpinnerButtonNode<Double>( UserComponentChain.chain( userComponent, "down" ),
                                                                      downButtonImages,
                                                                      downPressed, downInside, downEnabled,
                                                                      value,
                                                                      downFunction );

        // rendering order
        addChild( upBackgroundNode );
        addChild( downBackgroundNode );
        addChild( upButton );
        addChild( downButton );
        addChild( textNode );

        // layout
        upBackgroundNode.setOffset( 0, 0 );
        downBackgroundNode.setOffset( 0, 0 );
        textNode.setOffset( 0, ( backgroundHeight - textNode.getFullBoundsReference().getHeight() ) / 2 );
        upButton.setOffset( upBackgroundNode.getFullBoundsReference().getCenterX() - ( upButton.getFullBoundsReference().getWidth() / 2 ),
                            upBackgroundNode.getFullBoundsReference().getMinY() - upButton.getFullBoundsReference().getHeight() - 1 );
        downButton.setOffset( downBackgroundNode.getFullBoundsReference().getCenterX() - ( downButton.getFullBoundsReference().getWidth() / 2 ),
                              downBackgroundNode.getFullBoundsReference().getMaxY() );

        /*
         * When the value changes, update the display. Use a SimpleObserver instead of a VoidFunction1 so
         * that we don't get a possibly-stale argument value provided by the VoidFunction1.
         */
        value.addObserver( new SimpleObserver() {
            public void update() {
                // displayed value
                textNode.setText( format.format( value.get() ) );
                // centered
                textNode.setOffset( ( backgroundWidth - textNode.getFullBoundsReference().getWidth() ) / 2, textNode.getYOffset() );
            }
        } );
    }

    /*
     * Handler for a segment of the background that appears behind the spinner value.
     * This segment behaves like a button.
     */
    private static class BackgroundMouseHandler extends PBasicInputEventHandler {

        final PNode backgroundNode;
        final PDimension buttonSize;
        final BooleanProperty pressed, inside;
        final ObservableProperty<Boolean> enabled;
        final SpinnerStateIndicator<Color> stateIndicator;
        final IBackgroundPaintStrategy paintStrategy;
        final DynamicCursorHandler cursorHandler;

        public BackgroundMouseHandler( PNode backgroundNode, PDimension backgroundSize,
                                       BooleanProperty pressed, BooleanProperty inside, ObservableProperty<Boolean> enabled,
                                       SpinnerStateIndicator<Color> stateColors ) {

            this.backgroundNode = backgroundNode;
            this.buttonSize = backgroundSize;

            this.pressed = pressed;
            this.inside = inside;
            this.enabled = enabled;

            this.stateIndicator = stateColors;
            this.paintStrategy = new GradientColorStrategy();

            RichSimpleObserver observer = new RichSimpleObserver() {
                @Override public void update() {
                    updatePaint();
                }
            };
            observer.observe( pressed, inside, enabled );

            // manage the cursor
            backgroundNode.addInputEventListener( cursorHandler = new DynamicCursorHandler() );
            enabled.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean enabled ) {
                    cursorHandler.setCursor( enabled ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR );
                }
            } );
        }

        @Override public void mousePressed( PInputEvent event ) {
            pressed.set( true );
        }

        @Override public void mouseReleased( PInputEvent event ) {
            pressed.set( false );
        }

        @Override public void mouseEntered( PInputEvent event ) {
            inside.set( true );
        }

        @Override public void mouseExited( PInputEvent event ) {
            inside.set( false );
        }

        private void updatePaint() {
            Paint paint;
            if ( !enabled.get() ) {
                paint = stateIndicator.disabled;
            }
            else if ( pressed.get() ) {
                paint = paintStrategy.createPaint( stateIndicator.pressed, buttonSize );
            }
            else if ( inside.get() ) {
                paint = paintStrategy.createPaint( stateIndicator.highlighted, buttonSize );
            }
            else {
                paint = stateIndicator.inactive;
            }
            backgroundNode.setPaint( paint );
        }
    }

    // Strategy for creating the paint for a background.
    private static interface IBackgroundPaintStrategy {
        public Paint createPaint( Color baseColor, PDimension buttonSize );
    }

    // Creates a solid color background paint. Vestigial, kept for historical reasons.
    private static class SolidColorStrategy implements IBackgroundPaintStrategy {
        public Paint createPaint( Color baseColor, PDimension buttonSize ) {
            return baseColor;
        }
    }

    // Creates a gradient background paint, opaque at the top and bottom, transparent in the center.
    private static class GradientColorStrategy implements IBackgroundPaintStrategy {
        public Paint createPaint( Color baseColor, PDimension buttonSize ) {
            return new GradientPaint( 0f, 0f, baseColor, 0f, (float) ( buttonSize.getHeight() / 2 ), ColorUtils.createColor( baseColor, 0 ), true );
        }
    }

    // Base class that is color-coded for slope
    private abstract static class SlopeSpinnerNode extends SpinnerNode {
        public SlopeSpinnerNode( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent, new SlopeColors(), value, range, font, format );
        }
    }

    // Rise spinner
    public static class RiseSpinnerNode extends SlopeSpinnerNode {
        public RiseSpinnerNode( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent, value, range, font, format );
        }
    }

    // Run spinner
    public static class RunSpinnerNode extends SlopeSpinnerNode {
        public RunSpinnerNode( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent, value, range, font, format );
        }
    }

    // Intercept spinner
    public static class InterceptSpinnerNode extends SpinnerNode {
        public InterceptSpinnerNode( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent, new InterceptColors(), value, range, font, format );
        }
    }

    // Base class that is color-coded for point in point-slope form
    private abstract static class PointSpinnerNode extends SpinnerNode {
        public PointSpinnerNode( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent, new PointColors(), value, range, font, format );
        }
    }

    // x1 spinner
    public static class X1SpinnerNode extends PointSpinnerNode {
        public X1SpinnerNode( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent, value, range, font, format );
        }
    }

    // y1 spinner
    public static class Y1SpinnerNode extends PointSpinnerNode {
        public Y1SpinnerNode( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent, value, range, font, format );
        }
    }

    // test
    public static void main( String[] args ) {

        Property<DoubleRange> range = new Property<DoubleRange>( new DoubleRange( -10, 10, 0 ) );
        Property<Double> value = new Property<Double>( range.get().getDefault() );

        PNode node = new InterceptSpinnerNode( UserComponents.interceptSpinner,
                                               value, range, new PhetFont( Font.BOLD, 24 ), new DecimalFormat( "0" ) );
        node.setOffset( 200, 200 );

        // canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 600, 400 ) );
        canvas.getLayer().addChild( node );

        // frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
