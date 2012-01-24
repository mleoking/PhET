// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.PrecisionDecimal;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.PaintHighlightHandler;
import edu.colorado.phet.common.piccolophet.event.SliderThumbDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.dilutions.DilutionsConstants;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.common.util.ZeroIntegerDoubleFormat;
import edu.colorado.phet.dilutions.common.view.DualLabelNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Vertical sliders in the Dilutions simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionsSliderNode extends PhetPNode {

    private static final PDimension THUMB_SIZE = new PDimension( 45, 15 );
    private static final ZeroIntegerDoubleFormat RANGE_FORMAT = new ZeroIntegerDoubleFormat( "0.0" );
    private static final int VALUE_DECIMAL_PLACES = 2;
    private static final DecimalFormat VALUE_FORMAT = new DefaultDecimalFormat( "0.00" ); // this should match VALUE_DECIMAL_PLACES;

    private final LinearFunction function; // maps model value to a track position
    private final PNode trackNode;
    private final ThumbNode thumbNode;

    // Slider with a default track fill and background color.
    public DilutionsSliderNode( IUserComponent userComponent, String title, String minLabel, String maxLabel, final PDimension trackSize,
                                final Property<Double> modelValue, DoubleRange range, String units, Property<Boolean> valuesVisible ) {
        this( userComponent, title, minLabel, maxLabel, trackSize, Color.BLACK, new Color( 200, 200, 200, 140 ), modelValue, range, units, valuesVisible );
    }

    public DilutionsSliderNode( IUserComponent userComponent, String title, String minLabel, String maxLabel,
                                final PDimension trackSize, final Paint trackPaint, final Paint trackBackgroundPaint,
                                final Property<Double> modelValue, DoubleRange range, String units, Property<Boolean> valuesVisible ) {

        this.function = new LinearFunction( range.getMin(), range.getMax(), trackSize.getHeight(), 0 );

        // title
        PNode titleNode = new HTMLNode( title, Color.BLACK, DilutionsConstants.SLIDER_LABEL_FONT );

        // track that the thumb moves in, origin at upper left
        trackNode = new PPath() {{
            setPathTo( new Rectangle2D.Double( 0, 0, trackSize.getWidth(), trackSize.getHeight() ) );
            setPaint( trackPaint );
        }};

        // background that surrounds the track
        PNode backgroundNode = new PPath() {{
            final double xMargin = 7;
            final double yMargin = 7;
            setPathTo( new RoundRectangle2D.Double( -xMargin, -yMargin, trackSize.getWidth() + ( 2 * xMargin ), trackSize.getHeight() + ( 2 * yMargin ), 10, 10 ) );
            setPaint( trackBackgroundPaint );
            setStroke( null );

        }};

        // thumb that moves in the track
        thumbNode = new ThumbNode( userComponent, THUMB_SIZE, this, trackNode, range, modelValue, units, valuesVisible );

        // min and max labels
        final PNode minNode = new DualLabelNode( RANGE_FORMAT.format( range.getMin() ), minLabel, valuesVisible, DilutionsConstants.SLIDER_RANGE_FONT );
        final PNode maxNode = new DualLabelNode( RANGE_FORMAT.format( range.getMax() ), maxLabel, valuesVisible, DilutionsConstants.SLIDER_RANGE_FONT );

        // rendering order
        {
            addChild( titleNode );
            addChild( maxNode );
            addChild( minNode );
            addChild( backgroundNode );
            addChild( trackNode );
            addChild( thumbNode );
        }

        // layout
        {
            // max label centered above the bar
            maxNode.setOffset( trackNode.getFullBoundsReference().getCenterX(),
                               trackNode.getFullBoundsReference().getMinY() - ( thumbNode.getFullBoundsReference().getHeight() / 2 ) - maxNode.getFullBoundsReference().getHeight() - 1 );
            // min label centered below the bar
            minNode.setOffset( trackNode.getFullBoundsReference().getCenterX(),
                               trackNode.getFullBoundsReference().getMaxY() + ( thumbNode.getFullBoundsReference().getHeight() / 2 ) + 1 );
            // title centered above max label
            titleNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                                 maxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - 8 );
            // thumb centered in track
            thumbNode.setOffset( trackNode.getFullBoundsReference().getCenterX(),
                                 trackNode.getFullBoundsReference().getCenterY() );
        }

        // adjust the slider to reflect the model value
        modelValue.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                updateNode( value );
            }
        } );
    }

    private void updateNode( double value ) {
        // knob location
        thumbNode.setOffset( thumbNode.getXOffset(), function.evaluate( value ) );
    }

    // The slider thumb, rounded rectangle with a horizontal line through the center. Origin is at the thumb's geometric center.
    private static class ThumbNode extends PComposite {

        private static final Stroke THUMB_STROKE = new BasicStroke( 1f );
        private static final Color THUMB_NORMAL_COLOR = new Color( 89, 156, 212 );
        private static final Color THUMB_HIGHLIGHT_COLOR = THUMB_NORMAL_COLOR.brighter();
        private static final Color THUMB_STROKE_COLOR = Color.BLACK;
        private static final Color THUMB_CENTER_LINE_COLOR = Color.WHITE;

        private final PText valueNode;

        public ThumbNode( IUserComponent userComponent, final PDimension size, PNode relativeNode, PNode trackNode,
                          DoubleRange range, final Property<Double> modelValue, final String units, Property<Boolean> valuesVisible ) {

            PPath bodyNode = new PPath() {{
                final double arcWidth = 0.25 * size.getWidth();
                setPathTo( new RoundRectangle2D.Double( -size.getWidth() / 2, -size.getHeight() / 2, size.getWidth(), size.getHeight(), arcWidth, arcWidth ) );
                setPaint( THUMB_NORMAL_COLOR );
                setStroke( THUMB_STROKE );
                setStrokePaint( THUMB_STROKE_COLOR );
            }};

            PPath centerLineNode = new PPath() {{
                setPathTo( new Line2D.Double( -( size.getWidth() / 2 ) + 3, 0, ( size.getWidth() / 2 ) - 3, 0 ) );
                setStrokePaint( THUMB_CENTER_LINE_COLOR );
            }};

            this.valueNode = new PText( "?" ) {{
                setFont( DilutionsConstants.SLIDER_VALUE_FONT );
            }};
            valueNode.setOffset( bodyNode.getFullBoundsReference().getMaxX() + 5,
                                 bodyNode.getFullBoundsReference().getCenterY() - ( valueNode.getFullBoundsReference().getHeight() / 2 ) );

            // rendering order
            addChild( bodyNode );
            addChild( centerLineNode );
            addChild( valueNode );

            valuesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    valueNode.setVisible( visible );
                }
            } );

            modelValue.addObserver( new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    valueNode.setText( MessageFormat.format( Strings.PATTERN_0VALUE_1UNITS, VALUE_FORMAT.format( value ), units ) );
                }
            } );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PaintHighlightHandler( bodyNode, THUMB_NORMAL_COLOR, THUMB_HIGHLIGHT_COLOR ) );
            addInputEventListener( new ThumbDragHandler( userComponent, relativeNode, trackNode, this, range, modelValue ) );
        }
    }

    // Drag handler for the slider thumb, with data collection support.
    private static class ThumbDragHandler extends SliderThumbDragHandler {

        private final Property<Double> modelValue;

        public ThumbDragHandler( IUserComponent userComponent, PNode relativeNode, PNode trackNode, PNode thumbNode,
                                 DoubleRange range, final Property<Double> modelValue ) {
            super( userComponent, Orientation.VERTICAL, relativeNode, trackNode, thumbNode, range,
                   new VoidFunction1<Double>() {
                       public void apply( Double value ) {
                           modelValue.set( new PrecisionDecimal( value, VALUE_DECIMAL_PLACES ).getValue() ); // limit precision so that student calculations are correct
                       }
                   } );
            this.modelValue = modelValue;
        }

        @Override protected ParameterSet getParametersForAllEvents( PInputEvent event ) {
            return super.getParametersForAllEvents( event ).add( ParameterKeys.value, modelValue.get() );
        }
    }
}
