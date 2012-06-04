// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
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
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Spinner for a double value, with up and down arrows.
 * Unlike a JSpinner, the value is not directly editable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SpinnerNode2 extends PNode {

    private static final boolean DEBUG_BOUNDS = false;

    private static final Color BACKGROUND_UNARMED = new Color( 245, 245, 245 );
    private static final Color DISABLED_BUTTON_COLOR = new Color( 190, 190, 190 );
    private static final PDimension BUTTON_SIZE = new PDimension( 20, 4 );

    public SpinnerNode2( IUserComponent userComponent,
                         final Image upUnpressedImage, final Image upPressedImage, final Image upHighlightImage, final Image upDisabledImage,
                         final Image downUnpressedImage, final Image downPressedImage, final Image downHighlightImage, final Image downDisabledImage,
                         final Property<Double> value, final Property<DoubleRange> range, PhetFont font, final NumberFormat format ) {

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
        PPath incrementBackgroundNode = new PPath( new Rectangle2D.Double( 0, 0, backgroundWidth, ( backgroundHeight / 2 ) + backgroundOverlap ) ) {{
            setStroke( null );
            setPaint( BACKGROUND_UNARMED );
        }};
        PNode decrementBackgroundNode = new PPath( new Rectangle2D.Double( 0, 0, backgroundWidth, ( backgroundHeight / 2 ) + backgroundOverlap ) ) {{
            setStroke( null );
            setPaint( BACKGROUND_UNARMED );
        }};

        final BooleanProperty upPressed = new BooleanProperty( false );
        final BooleanProperty downPressed = new BooleanProperty( false );
        final BooleanProperty upInside = new BooleanProperty( false );
        final BooleanProperty downInside = new BooleanProperty( false );

        incrementBackgroundNode.addInputEventListener( new DynamicCursorHandler() );
        incrementBackgroundNode.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                upPressed.set( true );
            }

            @Override public void mouseReleased( PInputEvent event ) {
                upPressed.set( false );
            }

            @Override public void mouseEntered( PInputEvent event ) {
                upInside.set( true );
            }

            @Override public void mouseExited( PInputEvent event ) {
                upInside.set( false );
            }
        } );

        decrementBackgroundNode.addInputEventListener( new DynamicCursorHandler() );
        decrementBackgroundNode.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                downPressed.set( true );
            }

            @Override public void mouseReleased( PInputEvent event ) {
                downPressed.set( false );
            }

            @Override public void mouseEntered( PInputEvent event ) {
                downInside.set( true );
            }

            @Override public void mouseExited( PInputEvent event ) {
                downInside.set( false );
            }
        } );

        SpinnerButtonNode2 incrementButton = new SpinnerButtonNode2<Double>( UserComponentChain.chain( userComponent, "up" ),
                                                                             upUnpressedImage, upPressedImage, upHighlightImage, upDisabledImage,
                                                                             value,
                                                                             // increment the value
                                                                             new Function0<Double>() {
                                                                                 public Double apply() {
                                                                                     return value.get() + 1;
                                                                                 }
                                                                             },
                                                                             // enabled the button if value < range.max
                                                                             new CompositeProperty<Boolean>( new Function0<Boolean>() {
                                                                                 public Boolean apply() {
                                                                                     return value.get() < range.get().getMax();
                                                                                 }
                                                                             }, value, range ),
                                                                             upPressed, upInside );

        SpinnerButtonNode2 decrementButton = new SpinnerButtonNode2<Double>( UserComponentChain.chain( userComponent, "down" ),
                                                                             downUnpressedImage, downPressedImage, downHighlightImage, downDisabledImage,
                                                                             value,
                                                                             // decrement the value
                                                                             new Function0<Double>() {
                                                                                 public Double apply() {
                                                                                     return value.get() - 1;
                                                                                 }
                                                                             },
                                                                             // enabled the button if value > range.min
                                                                             new CompositeProperty<Boolean>( new Function0<Boolean>() {
                                                                                 public Boolean apply() {
                                                                                     return value.get() > range.get().getMin();
                                                                                 }
                                                                             }, value, range ),
                                                                             downPressed, downInside );

        // rendering order
        addChild( incrementBackgroundNode );
        addChild( decrementBackgroundNode );
        addChild( incrementButton );
        addChild( decrementButton );
        addChild( textNode );

        // layout
        incrementBackgroundNode.setOffset( 0, 0 );
        decrementBackgroundNode.setOffset( incrementBackgroundNode.getXOffset(),
                                           incrementBackgroundNode.getFullBoundsReference().getMaxY() - backgroundOverlap );
        textNode.setOffset( 0,
                            ( backgroundHeight - textNode.getFullBoundsReference().getHeight() ) / 2 );
        incrementButton.setOffset( incrementBackgroundNode.getFullBoundsReference().getCenterX() - ( incrementButton.getFullBoundsReference().getWidth() / 2 ),
                                   incrementBackgroundNode.getFullBoundsReference().getMinY() - incrementButton.getFullBoundsReference().getHeight() );
        decrementButton.setOffset( decrementBackgroundNode.getFullBoundsReference().getCenterX() - ( decrementButton.getFullBoundsReference().getWidth() / 2 ),
                                   decrementBackgroundNode.getFullBoundsReference().getMaxY() );

        // show bounds, for debugging
        if ( DEBUG_BOUNDS ) {
            PBounds b = getFullBoundsReference();
            addChild( new PPath( new Rectangle2D.Double( b.getX(), b.getY(), b.getWidth(), b.getHeight() ) ) );
        }

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
            super( userComponent,
                   new UpArrowNode( LGColors.SLOPE.darker() ).toImage(),
                   new UpArrowNode( LGColors.SLOPE ).toImage(),
                   new UpArrowNode( LGColors.SLOPE ).toImage(),
                   new UpArrowNode( DISABLED_BUTTON_COLOR, false ).toImage(),
                   new DownArrowNode( LGColors.SLOPE.darker() ).toImage(),
                   new DownArrowNode( LGColors.SLOPE ).toImage(),
                   new DownArrowNode( LGColors.SLOPE ).toImage(),
                   new DownArrowNode( DISABLED_BUTTON_COLOR, false ).toImage(),
                   value, range, font, format );
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
            super( userComponent,
                   new UpArrowNode( LGColors.INTERCEPT.darker() ).toImage(),
                   new UpArrowNode( LGColors.INTERCEPT ).toImage(),
                   new UpArrowNode( LGColors.INTERCEPT ).toImage(),
                   new UpArrowNode( DISABLED_BUTTON_COLOR, false ).toImage(),
                   new DownArrowNode( LGColors.INTERCEPT.darker() ).toImage(),
                   new DownArrowNode( LGColors.INTERCEPT ).toImage(),
                   new DownArrowNode( LGColors.INTERCEPT ).toImage(),
                   new DownArrowNode( DISABLED_BUTTON_COLOR, false ).toImage(),
                   value, range, font, format );
        }
    }

    // Base class that is color-coded for point in point-slope form
    private abstract static class PointSpinnerNode2 extends SpinnerNode2 {
        public PointSpinnerNode2( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent,
                   new UpArrowNode( LGColors.POINT_X1_Y1.darker() ).toImage(),
                   new UpArrowNode( LGColors.POINT_X1_Y1 ).toImage(),
                   new UpArrowNode( LGColors.POINT_X1_Y1 ).toImage(),
                   new UpArrowNode( DISABLED_BUTTON_COLOR, false ).toImage(),
                   new DownArrowNode( LGColors.POINT_X1_Y1.darker() ).toImage(),
                   new DownArrowNode( LGColors.POINT_X1_Y1 ).toImage(),
                   new DownArrowNode( LGColors.POINT_X1_Y1 ).toImage(),
                   new DownArrowNode( DISABLED_BUTTON_COLOR, false ).toImage(),
                   value, range, font, format );
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
