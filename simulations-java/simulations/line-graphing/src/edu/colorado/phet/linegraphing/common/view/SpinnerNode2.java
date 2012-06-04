// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.Paint;
import java.awt.geom.Area;
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
public class SpinnerNode2 extends PNode {

    private static final PDimension BUTTON_SIZE = new PDimension( 20, 4 );

    private static final Color DISABLED_COLOR = new Color( 190, 190, 190 );

    private static final Color SLOPE_INACTIVE = LGColors.SLOPE.darker();
    private static final Color SLOPE_HIGHLIGHTED = LGColors.SLOPE;
    private static final Color SLOPE_PRESSED = LGColors.SLOPE.darker();
    private static final Color SLOPE_DISABLED = DISABLED_COLOR;

    private static final Color INTERCEPT_INACTIVE = LGColors.INTERCEPT.darker();
    private static final Color INTERCEPT_HIGHLIGHTED = LGColors.INTERCEPT;
    private static final Color INTERCEPT_PRESSED = LGColors.INTERCEPT.darker();
    private static final Color INTERCEPT_DISABLED = DISABLED_COLOR;

    private static final Color POINT_INACTIVE = LGColors.POINT_X1_Y1.darker();
    private static final Color POINT_HIGHLIGHTED = LGColors.POINT_X1_Y1;
    private static final Color POINT_PRESSED = LGColors.POINT_X1_Y1.darker();
    private static final Color POINT_DISABLED = DISABLED_COLOR;

    private static final Color BACKGROUND_INACTIVE = new Color( 245, 245, 245 );

    public SpinnerNode2( IUserComponent userComponent,
                         Color inactiveColor, Color highlightedColor, Color pressedColor, Color disabledColor,
                         final Property<Double> value, final Property<DoubleRange> range, PhetFont font, final NumberFormat format ) {
        this( userComponent,
              new UpArrowNode( inactiveColor ).toImage(),
              new UpArrowNode( highlightedColor ).toImage(),
              new UpArrowNode( pressedColor ).toImage(),
              new UpArrowNode( disabledColor, false ).toImage(),
              new DownArrowNode( inactiveColor ).toImage(),
              new DownArrowNode( highlightedColor ).toImage(),
              new DownArrowNode( pressedColor ).toImage(),
              new DownArrowNode( disabledColor, false ).toImage(),
              BACKGROUND_INACTIVE, highlightedColor, pressedColor, BACKGROUND_INACTIVE,
              value, range, font, format );
    }

    public SpinnerNode2( IUserComponent userComponent,
                         final Image upInactiveImage, final Image upHighlightImage, final Image upPressedImage, final Image upDisabledImage,
                         final Image downInactiveImage, final Image downHighlightImage, final Image downPressedImage, final Image downDisabledImage,
                         final Color backgroundInactive, final Color backgroundHighlighted, final Color backgroundPressed, final Color backgroundDisabled,
                         final Property<Double> value, final Property<DoubleRange> range, PhetFont font, final NumberFormat format ) {

        final BooleanProperty upPressed = new BooleanProperty( false );
        final BooleanProperty downPressed = new BooleanProperty( false );
        final CompositeProperty upEnabled = new CompositeProperty<Boolean>(
                new Function0<Boolean>() {
                    public Boolean apply() {
                        return value.get() < range.get().getMax();
                    }
                }, value, range );

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
        textNode.setText( format.format( range.get().getMin() ) );
        double minValueWidth = textNode.getFullBoundsReference().getWidth();
        textNode.setText( format.format( range.get().getMax() ) );
        double maxValueWidth = textNode.getFullBoundsReference().getWidth();
        final double xMargin = 3;
        final double yMargin = 3;
        final double backgroundWidth = Math.max( minValueWidth, maxValueWidth ) + ( 2 * xMargin );
        final double backgroundHeight = textNode.getFullBoundsReference().getHeight() + ( 2 * yMargin );

        final double backgroundOverlap = 0.5;
        final double cornerRadius = 10;
        final Area roundRect = new Area( new RoundRectangle2D.Double( 0, 0, backgroundWidth, backgroundHeight, cornerRadius, cornerRadius ) );

        final PPath incrementBackgroundNode = new PPath() {{
            Area incrementShape = new Area( roundRect );
            incrementShape.subtract( new Area( new Rectangle2D.Double( 0, ( backgroundHeight / 2 ) - backgroundOverlap, backgroundWidth, backgroundHeight ) ) );
            setPathTo( incrementShape );
            setStroke( null );
            setPaint( backgroundInactive );
        }};


        final PNode decrementBackgroundNode = new PPath() {{
            Area decrementShape = new Area( roundRect );
            decrementShape.subtract( new Area( new Rectangle2D.Double( 0, 0, backgroundWidth, ( backgroundHeight / 2 ) - backgroundOverlap ) ) );
            setPathTo( decrementShape );
            setStroke( null );
            setPaint( backgroundInactive );
        }};

        incrementBackgroundNode.addInputEventListener( new DynamicCursorHandler() );
        incrementBackgroundNode.addInputEventListener(
                new BackgroundMouseHandler( incrementBackgroundNode, new PDimension( backgroundWidth, backgroundHeight ),
                                            upPressed, upInside, upEnabled,
                                            backgroundInactive, backgroundHighlighted, backgroundPressed, backgroundDisabled ) );

        decrementBackgroundNode.addInputEventListener( new DynamicCursorHandler() );
        decrementBackgroundNode.addInputEventListener(
                new BackgroundMouseHandler( decrementBackgroundNode, new PDimension( backgroundWidth, backgroundHeight ),
                                            downPressed, downInside, downEnabled,
                                            backgroundInactive, backgroundHighlighted, backgroundPressed, backgroundDisabled ) );

        SpinnerButtonNode2 incrementButton = new SpinnerButtonNode2<Double>( UserComponentChain.chain( userComponent, "up" ),
                                                                             upInactiveImage, upHighlightImage,upPressedImage, upDisabledImage,
                                                                             upPressed, upInside, upEnabled,
                                                                             value,
                                                                             new Function0<Double>() {
                                                                                 public Double apply() {
                                                                                     return value.get() + 1;
                                                                                 }
                                                                             } );

        SpinnerButtonNode2 decrementButton = new SpinnerButtonNode2<Double>( UserComponentChain.chain( userComponent, "down" ),
                                                                             downInactiveImage, downHighlightImage, downPressedImage, downDisabledImage,
                                                                             downPressed, downInside, downEnabled,
                                                                             value,
                                                                             new Function0<Double>() {
                                                                                 public Double apply() {
                                                                                     return value.get() - 1;
                                                                                 }
                                                                             } );

        // rendering order
        addChild( incrementBackgroundNode );
        addChild( decrementBackgroundNode );
        addChild( incrementButton );
        addChild( decrementButton );
        addChild( textNode );

        // layout
        incrementBackgroundNode.setOffset( 0, 0 );
        decrementBackgroundNode.setOffset( 0, 0 );
        textNode.setOffset( 0,
                            ( backgroundHeight - textNode.getFullBoundsReference().getHeight() ) / 2 );
        incrementButton.setOffset( incrementBackgroundNode.getFullBoundsReference().getCenterX() - ( incrementButton.getFullBoundsReference().getWidth() / 2 ),
                                   incrementBackgroundNode.getFullBoundsReference().getMinY() - incrementButton.getFullBoundsReference().getHeight() );
        decrementButton.setOffset( decrementBackgroundNode.getFullBoundsReference().getCenterX() - ( decrementButton.getFullBoundsReference().getWidth() / 2 ),
                                   decrementBackgroundNode.getFullBoundsReference().getMaxY() );

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

    private static class BackgroundMouseHandler extends PBasicInputEventHandler {

        final PNode backgroundNode;
        final PDimension buttonSize;
        final BooleanProperty pressed, inside;
        final ObservableProperty<Boolean> enabled;
        final Color inactiveColor, highlightColor, pressedColor, disabledColor;
        final IBackgroundPaintStrategy paintStrategy;

        public BackgroundMouseHandler( PNode backgroundNode, PDimension buttonSize,
                                       BooleanProperty pressed, BooleanProperty inside, ObservableProperty<Boolean> enabled,
                                       Color inactiveColor, Color highlightColor, Color pressedColor, Color disabledColor ) {

            this.backgroundNode = backgroundNode;
            this.buttonSize = buttonSize;

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

    private static interface IBackgroundPaintStrategy {
        public Paint createPaint( Color baseColor, PDimension buttonSize );
    }

    private static class SolidColorStrategy implements IBackgroundPaintStrategy {
        public Paint createPaint( Color baseColor, PDimension buttonSize ) {
            return baseColor;
        }
    }

    private static class GradientColorStrategy implements IBackgroundPaintStrategy {
        public Paint createPaint( Color baseColor, PDimension buttonSize ) {
            return new GradientPaint( 0f, 0f, baseColor, 0f, (float) ( buttonSize.getHeight() / 2 ), ColorUtils.createColor( baseColor, 0 ), true );
        }
    }

    private static class UpArrowNode extends PadBoundsNode {

        public UpArrowNode( final Color color ) {
            this( color, true );
        }

        public UpArrowNode( final Color color, boolean outlined ) {
            PPath node = new PPath( new DoubleGeneralPath() {{
                moveTo( 0, BUTTON_SIZE.getHeight() );
                lineTo( BUTTON_SIZE.getWidth() / 2, 0 );
                lineTo( BUTTON_SIZE.getWidth(), BUTTON_SIZE.getHeight() );
                closePath();
            }}.getGeneralPath() );
            node.setPaint( color );
            node.setStroke( new BasicStroke( 0.25f ) );
            node.setStrokePaint( outlined ? Color.BLACK : color );
            addChild( node );
        }
    }

    private static class DownArrowNode extends PadBoundsNode {

        public DownArrowNode( final Color color ) {
            this( color, true );
        }

        public DownArrowNode( final Color color, boolean outlined ) {
            PPath node = new PPath( new DoubleGeneralPath() {{
                moveTo( 0, 0 );
                lineTo( BUTTON_SIZE.getWidth() / 2, BUTTON_SIZE.getHeight() );
                lineTo( BUTTON_SIZE.getWidth(), 0 );
                closePath();
            }}.getGeneralPath() );
            node.setPaint( color );
            node.setStroke( new BasicStroke( 0.5f ) );
            node.setStrokePaint( outlined ? Color.BLACK : color );
            addChild( node );
        }
    }

    // Base class that is color-coded for slope
    private abstract static class SlopeSpinnerNode2 extends SpinnerNode2 {
        public SlopeSpinnerNode2( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent, SLOPE_INACTIVE, SLOPE_HIGHLIGHTED, SLOPE_PRESSED, SLOPE_DISABLED, value, range, font, format );
        }
    }

    // Rise spinner
    public static class RiseSpinnerNode2 extends SlopeSpinnerNode2 {
        public RiseSpinnerNode2( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent, value, range, font, format );
        }
    }

    // Run spinner
    public static class RunSpinnerNode2 extends SlopeSpinnerNode2 {
        public RunSpinnerNode2( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent, value, range, font, format );
        }
    }

    // Intercept spinner
    public static class InterceptSpinnerNode2 extends SpinnerNode2 {
        public InterceptSpinnerNode2( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent, INTERCEPT_INACTIVE, INTERCEPT_HIGHLIGHTED, INTERCEPT_PRESSED, INTERCEPT_DISABLED, value, range, font, format );
        }
    }

    // Base class that is color-coded for point in point-slope form
    private abstract static class PointSpinnerNode2 extends SpinnerNode2 {
        public PointSpinnerNode2( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent, POINT_INACTIVE, POINT_HIGHLIGHTED, POINT_PRESSED, POINT_DISABLED, value, range, font, format );
        }
    }

    // x1 spinner
    public static class X1SpinnerNode2 extends PointSpinnerNode2 {
        public X1SpinnerNode2( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent, value, range, font, format );
        }
    }

    // y1 spinner
    public static class Y1SpinnerNode2 extends PointSpinnerNode2 {
        public Y1SpinnerNode2( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent, value, range, font, format );
        }
    }

    // test
    public static void main( String[] args ) {

        Property<DoubleRange> range = new Property<DoubleRange>( new DoubleRange( -10, 10, 0 ) );
        Property<Double> value = new Property<Double>( range.get().getDefault() );

        PNode node = new InterceptSpinnerNode2( UserComponents.interceptSpinner,
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
