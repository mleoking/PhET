// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.PaintHighlightHandler;
import edu.colorado.phet.common.piccolophet.event.SliderThumbDragHandler;
import edu.colorado.phet.common.piccolophet.event.SliderThumbDragHandler.Orientation;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Base class for vertical sliders in the Dilutions simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class DilutionsSliderNode extends PhetPNode {

    // Slider for controlling amount of solute
    public static class SoluteAmountSliderNode extends DilutionsSliderNode {
        public SoluteAmountSliderNode( PDimension size, final Property<Double> soluteAmount, DoubleRange range, Property<Boolean> valuesVisible ) {
            super( Strings.SOLUTE_AMOUNT, size, soluteAmount, new DecimalFormat( "0.00" ), Strings.UNITS_MOLES,
                   range, Strings.LOTS, Strings.NONE, valuesVisible );
        }
    }

    // Slider for controlling volume of solution
    public static class SolutionVolumeSliderNode extends DilutionsSliderNode {
        public SolutionVolumeSliderNode( PDimension size, final Property<Double> solutionVolume, DoubleRange range, Property<Boolean> valuesVisible ) {
            super( Strings.SOLUTION_VOLUME, size, solutionVolume, new DecimalFormat( "0.00" ), Strings.UNITS_LITERS,
                   range, Strings.FULL, Strings.LOW, valuesVisible );
        }
    }

    private static final PhetFont TITLE_FONT = new PhetFont( Font.BOLD, 16 );
    private static final PhetFont VALUE_FONT = new PhetFont( 16 );
    private static final PhetFont LABEL_FONT = new PhetFont( 14 );

    // track
    private static final Color TRACK_FILL_COLOR = Color.BLACK;

    // knob
    private static final PDimension KNOB_SIZE = new PDimension( 35, 10 );
    private static final Stroke KNOB_STROKE = new BasicStroke( 1f );
    private static final Color KNOB_NORMAL_COLOR = new Color( 89, 156, 212 );
    private static final Color KNOB_HIGHLIGHT_COLOR = new Color( 214, 255, 255 );
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;

    private final LinearFunction function;
    private final ValueNode valueNode;
    private final TrackNode trackNode;
    private final ThumbNode thumbNode;

    public DilutionsSliderNode( String title, PDimension trackSize, final Property<Double> modelValue, NumberFormat valueFormat, String units,
                                DoubleRange range, String maxQualityText, String minQualityText, Property<Boolean> valuesVisible ) {

        this.function = new LinearFunction( range.getMin(), range.getMax(), trackSize.getHeight(), 0 );

        // nodes
        TitleNode titleNode = new TitleNode( title );
        valueNode = new ValueNode( modelValue, range, valueFormat, units );
        trackNode = new TrackNode( trackSize, TRACK_FILL_COLOR );
        thumbNode = new ThumbNode( this, trackNode, range, new VoidFunction1<Double>() {
            public void apply( Double value ) {
                modelValue.set( value );
            }
        } );
        final RangeValueNode maxValueNode = new RangeValueNode( range.getMax(), valueFormat, units );
        final RangeValueNode minValueNode = new RangeValueNode( range.getMin(), valueFormat, units );
        final QualityNode maxQualityNode = new QualityNode( maxQualityText );
        final QualityNode minQualityNode = new QualityNode( minQualityText );

        // rendering order
        {
            addChild( titleNode );
            addChild( valueNode );
            addChild( trackNode );
            addChild( thumbNode );
            addChild( maxValueNode );
            addChild( minValueNode );
            addChild( maxQualityNode );
            addChild( minQualityNode );
        }

        // layout
        {
            // max labels centered above track
            maxValueNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( maxValueNode.getFullBoundsReference().getWidth() / 2 ),
                                    trackNode.getFullBoundsReference().getMinY() - maxValueNode.getFullBoundsReference().getHeight() - ( thumbNode.getFullBoundsReference().getHeight() / 2 ) - 2 );
            maxQualityNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( maxQualityNode.getFullBoundsReference().getWidth() / 2 ),
                                      trackNode.getFullBoundsReference().getMinY() - maxQualityNode.getFullBoundsReference().getHeight() - ( thumbNode.getFullBoundsReference().getHeight() / 2 ) - 2 );
            // min labels centered below track
            minValueNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( minValueNode.getFullBoundsReference().getWidth() / 2 ),
                                    trackNode.getFullBoundsReference().getMaxY() + ( thumbNode.getFullBoundsReference().getHeight() / 2 ) + 2 );
            minQualityNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( minQualityNode.getFullBoundsReference().getWidth() / 2 ),
                                      trackNode.getFullBoundsReference().getMaxY() + ( thumbNode.getFullBoundsReference().getHeight() / 2 ) + 2 );
            // value centered above max label
            valueNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( valueNode.getFullBoundsReference().getWidth() / 2 ),
                                 Math.min( maxValueNode.getFullBoundsReference().getMinY(), maxQualityNode.getFullBoundsReference().getMinY() ) - valueNode.getFullBoundsReference().getHeight() - 2 );
            // title centered above value
            titleNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                                 valueNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - 2 );
            // thumb centered in track
            thumbNode.setOffset( trackNode.getFullBoundsReference().getCenterX(),
                                 trackNode.getFullBoundsReference().getCenterY() );

        }

        modelValue.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                updateNode( value );
            }
        } );

        valuesVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                valueNode.setVisible( visible );
                maxValueNode.setVisible( visible );
                minValueNode.setVisible( visible );
                maxQualityNode.setVisible( !visible );
                minQualityNode.setVisible( !visible );
            }
        } );
    }

    private void updateNode( double value ) {
        // knob location
        thumbNode.setOffset( thumbNode.getXOffset(), function.evaluate( value ) );
    }

    // Title above the slider
    private static class TitleNode extends HTMLNode {
        public TitleNode( String html ) {
            super( html );
            setFont( TITLE_FONT );
        }
    }

    // Value above the slider, an editable text field
    private static class ValueNode extends PhetPNode {

        private final Property<Double> modelValue;
        private final DoubleRange range;
        private final NumberFormat format;
        private final JTextField textField;

        public ValueNode( final Property<Double> modelValue, final DoubleRange range, final NumberFormat format, final String units ) {

            this.modelValue = modelValue;
            this.range = range;
            this.format = format;

            // text field
            textField = new JTextField( 3 ) {{
                setFont( VALUE_FONT );
                setBorder( new CompoundBorder( new LineBorder( Color.BLACK, 1 ), new EmptyBorder( 3, 3, 3, 3 ) ) );
                setHorizontalAlignment( JTextField.RIGHT );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        // update the model when the user presses Enter in the text field
                        updateModelValue();
                    }
                } );
                addFocusListener( new FocusListener() {
                    public void focusGained( FocusEvent e ) {
                        // select the entire contents of the text field when it gains focus
                        textField.selectAll();
                    }

                    public void focusLost( FocusEvent e ) {
                        // update the model when the text field loses focus
                        updateModelValue();
                    }
                } );
            }};

            // text field node
            PNode textFieldNode = new PSwing( textField );
            addChild( textFieldNode );

            // units
            PText unitsNode = new PText( units ) {{
                setFont( VALUE_FONT );
            }};
            addChild( unitsNode );
            unitsNode.setOffset( textFieldNode.getFullBoundsReference().getMaxX() + 2,
                                 ( textFieldNode.getFullBoundsReference().getHeight() - unitsNode.getFullBoundsReference().getHeight() ) / 2 );

            modelValue.addObserver( new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    setValue( value );
                }
            } );
        }

        // Reads the value from the text field and (if it's valid) writes it to the model.
        // If the value is bogus, reverts to the model value.
        private void updateModelValue() {
            double value = getValue();
            if ( range.contains( value ) ) {
                modelValue.set( value );
            }
            else {
                inputErrorAlert();
                setValue( modelValue.get() );
            }
        }

        // Sets the value displayed in the text field.
        private void setValue( double value ) {
            textField.setText( format.format( value ) );
        }

        // Gets the value that is in the text field. If the value is bogus, resets the text field to the model value, and returns that value.
        private double getValue() {
            double value;
            try {
                value = Double.parseDouble( textField.getText() );
            }
            catch ( NumberFormatException nfe ) {
                inputErrorAlert();
                value = modelValue.get();
                setValue( value );
            }
            return value;
        }

        private void inputErrorAlert() {
            Toolkit.getDefaultToolkit().beep(); //TODO do we want to do this?
        }
    }

    // A qualitative label
    private static class QualityNode extends PText {
        public QualityNode( String text ) {
            super( text );
            setFont( LABEL_FONT );
        }
    }

    // The track that the thumb moves in. Origin is at upper-left corner.
    private static class TrackNode extends PPath {
        public TrackNode( PDimension size, Color fillColor ) {
            setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
            setPaint( fillColor );
        }
    }

    // The slider thumb, a simple rectangle. Origin is at the thumb's geometric center.
    private static class ThumbNode extends PPath {

        public ThumbNode( PNode relativeNode, PNode trackNode, DoubleRange range, VoidFunction1<Double> updateFunction ) {

            final double arcWidth = 0.6 * KNOB_SIZE.getWidth();
            setPathTo( new RoundRectangle2D.Double( -KNOB_SIZE.getWidth() / 2, -KNOB_SIZE.getHeight() / 2,
                                                    KNOB_SIZE.getWidth(), KNOB_SIZE.getHeight(),
                                                    arcWidth, arcWidth ) );
            setPaint( KNOB_NORMAL_COLOR );
            setStroke( KNOB_STROKE );
            setStrokePaint( KNOB_STROKE_COLOR );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PaintHighlightHandler( this, KNOB_NORMAL_COLOR, KNOB_HIGHLIGHT_COLOR ) );
            addInputEventListener( new SliderThumbDragHandler( Orientation.VERTICAL, relativeNode, trackNode, this, range, updateFunction ) );
        }
    }

    // Range values
    private static class RangeValueNode extends PComposite {

        private final NumberFormat format;
        private final String units;
        private final PText textNode;

        public RangeValueNode( double initialValue, NumberFormat format, String units ) {
            this.format = format;
            this.units = units;
            textNode = new PText() {{
                setFont( LABEL_FONT );
            }};
            addChild( textNode );
            setValue( initialValue );
        }

        // Converts concentration value to a string with units, with special treatment of zero.
        public void setValue( double value ) {
            String valueString = ( value == 0 ) ? "0" : format.format( value );
            textNode.setText( MessageFormat.format( Strings.PATTERN_0VALUE_1UNITS, valueString, units ) );
        }
    }
}
