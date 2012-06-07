// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;


import java.awt.BasicStroke;
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
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.DynamicCursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PadBoundsNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
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

    private static enum ArrowOrientation {UP,DOWN}

    private static final PDimension BUTTON_SIZE = new PDimension( 26, 4 );

    private static final Color BUTTON_DISABLED_COLOR = new Color( 190, 190, 190 );

    private static final Color SLOPE_INACTIVE = LGColors.SLOPE;
    private static final Color SLOPE_HIGHLIGHTED = LGColors.SLOPE;
    private static final Color SLOPE_PRESSED = LGColors.SLOPE.darker();
    private static final Color SLOPE_DISABLED = BUTTON_DISABLED_COLOR;

    private static final Color INTERCEPT_INACTIVE = LGColors.INTERCEPT;
    private static final Color INTERCEPT_HIGHLIGHTED = LGColors.INTERCEPT;
    private static final Color INTERCEPT_PRESSED = LGColors.INTERCEPT.darker();
    private static final Color INTERCEPT_DISABLED = BUTTON_DISABLED_COLOR;

    private static final Color POINT_INACTIVE = LGColors.POINT_X1_Y1;
    private static final Color POINT_HIGHLIGHTED = LGColors.POINT_X1_Y1;
    private static final Color POINT_PRESSED = LGColors.POINT_X1_Y1.darker();
    private static final Color POINT_DISABLED = BUTTON_DISABLED_COLOR;

    private static final Color BACKGROUND_INACTIVE = new Color( 245, 245, 245 );

    // Constructor that creates default up/down arrow images using the specified colors.
    public SpinnerNode( IUserComponent userComponent,
                        Color inactiveColor, Color highlightedColor, Color pressedColor, Color disabledColor,
                        final Property<Double> value, final Property<DoubleRange> range, PhetFont font, final NumberFormat format ) {
        this( userComponent,
              new ArrowButtonNode( inactiveColor, ArrowOrientation.UP, BUTTON_SIZE ).toImage(),
              new ArrowButtonNode( highlightedColor, ArrowOrientation.UP, BUTTON_SIZE ).toImage(),
              new ArrowButtonNode( pressedColor, ArrowOrientation.UP, BUTTON_SIZE ).toImage(),
              new ArrowButtonNode( disabledColor, ArrowOrientation.UP, BUTTON_SIZE, false ).toImage(),
              new ArrowButtonNode( inactiveColor, ArrowOrientation.DOWN, BUTTON_SIZE ).toImage(),
              new ArrowButtonNode( highlightedColor, ArrowOrientation.DOWN, BUTTON_SIZE ).toImage(),
              new ArrowButtonNode( pressedColor, ArrowOrientation.DOWN, BUTTON_SIZE ).toImage(),
              new ArrowButtonNode( disabledColor, ArrowOrientation.DOWN, BUTTON_SIZE, false ).toImage(),
              BACKGROUND_INACTIVE, highlightedColor, pressedColor, BACKGROUND_INACTIVE,
              value, range, font, format );
    }

    public SpinnerNode( IUserComponent userComponent,
                        final Image upInactiveImage, final Image upHighlightImage, final Image upPressedImage, final Image upDisabledImage,
                        final Image downInactiveImage, final Image downHighlightImage, final Image downPressedImage, final Image downDisabledImage,
                        final Color backgroundInactive, final Color backgroundHighlighted, final Color backgroundPressed, final Color backgroundDisabled,
                        final Property<Double> value, final Property<DoubleRange> range, PhetFont font, final NumberFormat format ) {

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
            setPaint( backgroundInactive );
            addInputEventListener( new BackgroundMouseHandler( this, new PDimension( backgroundWidth, backgroundHeight ),
                                                               upPressed, upInside, upEnabled,
                                                               backgroundInactive, backgroundHighlighted, backgroundPressed, backgroundDisabled ) );
        }};

        // bottom half of the background, for "down"
        final PNode downBackgroundNode = new PPath() {{
            setPathTo( ShapeUtils.subtract( backgroundShape, new Rectangle2D.Double( 0, 0, backgroundWidth, ( backgroundHeight / 2 ) - backgroundOverlap ) ) );
            setStroke( null );
            setPaint( backgroundInactive );
            addInputEventListener( new BackgroundMouseHandler( this, new PDimension( backgroundWidth, backgroundHeight ),
                                                               downPressed, downInside, downEnabled,
                                                               backgroundInactive, backgroundHighlighted, backgroundPressed, backgroundDisabled ) );
        }};

        // up (increment) button
        SpinnerButtonNode upButton = new SpinnerButtonNode<Double>( UserComponentChain.chain( userComponent, "up" ),
                                                                      upInactiveImage, upHighlightImage, upPressedImage, upDisabledImage,
                                                                      upPressed, upInside, upEnabled,
                                                                      value,
                                                                      new Function0<Double>() {
                                                                          public Double apply() {
                                                                              return value.get() + 1;
                                                                          }
                                                                      } );

        // down (decrement) button
        SpinnerButtonNode downButton = new SpinnerButtonNode<Double>( UserComponentChain.chain( userComponent, "down" ),
                                                                        downInactiveImage, downHighlightImage, downPressedImage, downDisabledImage,
                                                                        downPressed, downInside, downEnabled,
                                                                        value,
                                                                        new Function0<Double>() {
                                                                            public Double apply() {
                                                                                return value.get() - 1;
                                                                            }
                                                                        } );

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

        // when the value changes, update the display
        value.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                // displayed value
                textNode.setText( format.format( value ) );
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
        final Color inactiveColor, highlightColor, pressedColor, disabledColor;
        final IBackgroundPaintStrategy paintStrategy;
        final DynamicCursorHandler cursorHandler;

        public BackgroundMouseHandler( PNode backgroundNode, PDimension backgroundSize,
                                       BooleanProperty pressed, BooleanProperty inside, ObservableProperty<Boolean> enabled,
                                       Color inactiveColor, Color highlightColor, Color pressedColor, Color disabledColor ) {

            this.backgroundNode = backgroundNode;
            this.buttonSize = backgroundSize;

            this.pressed = pressed;
            this.inside = inside;
            this.enabled = enabled;

            this.inactiveColor = inactiveColor;
            this.highlightColor = highlightColor;
            this.pressedColor = pressedColor;
            this.disabledColor = disabledColor;
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
                paint = disabledColor;
            }
            else if ( pressed.get() ) {
                paint = paintStrategy.createPaint( pressedColor, buttonSize );
            }
            else if ( inside.get() ) {
                paint = paintStrategy.createPaint( highlightColor, buttonSize );
            }
            else {
                paint = inactiveColor;
            }
            backgroundNode.setPaint( paint );
        }
    }

    // Strategy for creating the paint for a background.
    private static interface IBackgroundPaintStrategy {
        public Paint createPaint( Color baseColor, PDimension buttonSize );
    }

    // Creates a solid color background paint.
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

    // Base class for arrow buttons
    private static class ArrowButtonNode extends PPath {

        public ArrowButtonNode( final Color color, ArrowOrientation orientation, PDimension size ) {
            this( color, orientation, size, true );
        }

        public ArrowButtonNode( final Color color, final ArrowOrientation orientation, final PDimension size, boolean outlined ) {
            setPathTo( new DoubleGeneralPath() {{
                if ( orientation == ArrowOrientation.UP ) {
                    moveTo( 0, size.getHeight() );
                    lineTo( size.getWidth() / 2, 0 );
                    lineTo( size.getWidth(), size.getHeight() );
                }
                else {
                    moveTo( 0, 0 );
                    lineTo( size.getWidth() / 2, size.getHeight() );
                    lineTo( size.getWidth(), 0 );
                }
                closePath();
            }}.getGeneralPath() );
            setPaint( color );
            setStroke( new BasicStroke( 0.25f ) );
            setStrokePaint( outlined ? Color.BLACK : color ); // stroke with the fill color, so the arrow doesn't appear to shrink
        }

        // WORKAROUND for #558
        @Override public Image toImage() {
            return new PadBoundsNode( this ).toImage();
        }
    }

    // Base class that is color-coded for slope
    private abstract static class SlopeSpinnerNode extends SpinnerNode {
        public SlopeSpinnerNode( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent, SLOPE_INACTIVE, SLOPE_HIGHLIGHTED, SLOPE_PRESSED, SLOPE_DISABLED, value, range, font, format );
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
            super( userComponent, INTERCEPT_INACTIVE, INTERCEPT_HIGHLIGHTED, INTERCEPT_PRESSED, INTERCEPT_DISABLED, value, range, font, format );
        }
    }

    // Base class that is color-coded for point in point-slope form
    private abstract static class PointSpinnerNode extends SpinnerNode {
        public PointSpinnerNode( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent, POINT_INACTIVE, POINT_HIGHLIGHTED, POINT_PRESSED, POINT_DISABLED, value, range, font, format );
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
