// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLSimSharing;
import edu.colorado.phet.capacitorlab.CLSimSharing.UserComponents;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit.CircuitChangeListener;
import edu.colorado.phet.capacitorlab.model.circuit.SingleCircuit;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.PaintHighlightHandler;
import edu.colorado.phet.common.piccolophet.event.SliderThumbDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Control for applying charge to the capacitor plates.
 * This control is activated when the battery is disconnected.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlateChargeControlNode extends PhetPNode {

    // track
    private static final PDimension TRACK_SIZE = new PDimension( 5, 200 );
    private static final Color TRACK_FILL_COLOR = Color.LIGHT_GRAY;
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final Stroke TRACK_STROKE = new BasicStroke( 1f );

    // background
    private static final double BACKGROUND_X_MARGIN = 10;
    private static final double BACKGROUND_Y_MARGIN = 5;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Color BACKGROUND_FILL_COLOR = new JPanel().getBackground();

    // knob
    private static final PDimension KNOB_SIZE = new PDimension( 20, 15 );
    private static final Stroke KNOB_STROKE = new BasicStroke( 1f );
    private static final Color KNOB_NORMAL_COLOR = CLPaints.DRAGGABLE_NORMAL;
    private static final Color KNOB_HIGHLIGHT_COLOR = CLPaints.DRAGGABLE_HIGHLIGHT;
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;

    // ticks
    private static final double TICK_MARK_LENGTH = 8;
    private static final Color TICK_MARK_COLOR = TRACK_STROKE_COLOR;
    private static final Stroke TICK_MARK_STROKE = TRACK_STROKE;
    private static final double TICK_LABEL_X_SPACING = 3;

    // range labels
    private static final Font RANGE_LABEL_FONT = new PhetFont( 14 );
    private static final Color RANGE_LABEL_COLOR = Color.BLACK;

    // title
    private static final Font TITLE_FONT = new PhetFont( Font.BOLD, 16 );
    private static final Color TITLE_COLOR = Color.BLACK;

    private final SingleCircuit circuit;
    private final SliderNode sliderNode;
    private final DoubleRange range;

    /**
     * Constructor
     *
     * @param circuit the circuit that we're controlling
     * @param range   range of the plate charge
     */
    public PlateChargeControlNode( final SingleCircuit circuit, DoubleRange range ) {

        this.range = range;

        this.circuit = circuit;
        circuit.addCircuitChangeListener( new CircuitChangeListener() {
            public void circuitChanged() {
                if ( !circuit.isBatteryConnected() ) {
                    update();
                }
            }
        } );

        sliderNode = new SliderNode( circuit, range );

        // background, sized to fit around slider
        double bWidth = sliderNode.getFullBoundsReference().getWidth() + ( 2 * BACKGROUND_X_MARGIN );
        double bHeight = sliderNode.getFullBoundsReference().getHeight() + ( 2 * BACKGROUND_Y_MARGIN );
        Rectangle2D backgroundRect = new Rectangle2D.Double( 0, 0, bWidth, bHeight );
        PPath backgroundNode = new PPath( backgroundRect );
        backgroundNode.setStroke( BACKGROUND_STROKE );
        backgroundNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        backgroundNode.setPaint( BACKGROUND_FILL_COLOR );

        TitleNode titleNode = new TitleNode( CLStrings.PLATE_CHARGE_TOP );

        // rendering order
        addChild( backgroundNode );
        addChild( sliderNode );
        addChild( titleNode );

        // layout
        {
            // background at origin
            double x = 0;
            double y = 0;
            backgroundNode.setOffset( x, y );
            // slider centered in background
            x = backgroundNode.getFullBoundsReference().getCenterX() - ( sliderNode.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( sliderNode );
            y = backgroundNode.getFullBoundsReference().getCenterY() - ( sliderNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( sliderNode );
            sliderNode.setOffset( x, y );
            // title centered below background
            x = backgroundNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
            y = backgroundNode.getFullBoundsReference().getMaxY() + 2;
            titleNode.setOffset( x, y );
        }

        update();
    }

    // Updates the control to match the circuit model.
    private void update() {
        final double plateCharge = circuit.getDisconnectedPlateCharge();
        // knob location
        double xOffset = sliderNode.knobNode.getXOffset();
        double yOffset = sliderNode.trackNode.getFullBoundsReference().getHeight() * ( ( range.getMax() - plateCharge ) / range.getLength() );
        sliderNode.knobNode.setOffset( xOffset, yOffset );
    }

    // Slider, knob, ticks marks, tick labels
    private static class SliderNode extends PhetPNode {

        public final TrackNode trackNode;
        public final KnobNode knobNode;

        public SliderNode( final SingleCircuit circuit, DoubleRange range ) {

            // nodes
            trackNode = new TrackNode();
            knobNode = new KnobNode( this, trackNode, range, circuit );
            TickMarkNode maxTickMarkNode = new TickMarkNode();
            RangeLabelNode maxLabelNode = new RangeLabelNode( CLStrings.LOTS_POSITIVE );
            TickMarkNode zeroTickMarkNode = new TickMarkNode();
            RangeLabelNode zeroLabelNode = new RangeLabelNode( CLStrings.NONE );
            TickMarkNode minTickMarkNode = new TickMarkNode();
            RangeLabelNode minLabelNode = new RangeLabelNode( CLStrings.LOTS_NEGATIVE );

            // parent for all nodes that are part of the slider
            addChild( trackNode );
            addChild( maxTickMarkNode );
            addChild( maxLabelNode );
            addChild( zeroTickMarkNode );
            addChild( zeroLabelNode );
            addChild( minTickMarkNode );
            addChild( minLabelNode );
            addChild( knobNode );

            // layout
            double x = 0;
            double y = 0;
            // track
            trackNode.setOffset( x, y );
            // knob
            x = -5; // determines the overlap with the track
            y = 0; // don't care, set by update
            knobNode.setOffset( x, y );
            // max tick & label
            x = -maxTickMarkNode.getFullBoundsReference().getWidth();
            y = trackNode.getFullBoundsReference().getMinY() + ( maxTickMarkNode.getFullBoundsReference().getHeight() / 2 );
            maxTickMarkNode.setOffset( x, y );
            x = maxTickMarkNode.getFullBoundsReference().getMinX() - maxLabelNode.getFullBoundsReference().getWidth() - TICK_LABEL_X_SPACING;
            y = maxTickMarkNode.getFullBoundsReference().getCenterY() - ( maxLabelNode.getFullBoundsReference().getHeight() / 2 );
            maxLabelNode.setOffset( x, y );
            // zero tick & label
            x = -zeroTickMarkNode.getFullBoundsReference().getWidth();
            y = trackNode.getFullBoundsReference().getCenterY() - ( zeroTickMarkNode.getFullBoundsReference().getHeight() / 2 ) + 1;
            zeroTickMarkNode.setOffset( x, y );
            x = zeroTickMarkNode.getFullBoundsReference().getMinX() - zeroLabelNode.getFullBoundsReference().getWidth() - TICK_LABEL_X_SPACING;
            y = zeroTickMarkNode.getFullBoundsReference().getCenterY() - ( zeroLabelNode.getFullBoundsReference().getHeight() / 2 );
            zeroLabelNode.setOffset( x, y );
            // min tick & label
            x = -minTickMarkNode.getFullBoundsReference().getWidth();
            y = trackNode.getFullBoundsReference().getMaxY();
            minTickMarkNode.setOffset( x, y );
            x = minTickMarkNode.getFullBoundsReference().getMinX() - minLabelNode.getFullBoundsReference().getWidth() - TICK_LABEL_X_SPACING;
            y = minTickMarkNode.getFullBoundsReference().getCenterY() - ( minLabelNode.getFullBoundsReference().getHeight() / 2 );
            minLabelNode.setOffset( x, y );
        }
    }

    /*
     * The track that the bar moves in.
     * Origin is at upper-left corner.
     */
    private static class TrackNode extends PPath {

        public TrackNode() {
            setPathTo( new Rectangle2D.Double( 0, 0, TRACK_SIZE.width, TRACK_SIZE.height ) );
            setPaint( TRACK_FILL_COLOR );
            setStrokePaint( TRACK_STROKE_COLOR );
            setStroke( TRACK_STROKE );
        }
    }

    /*
     * The slider knob, points to the left.
     * Origin is at the knob's tip.
     */
    private static class KnobNode extends PPath {

        public KnobNode( PNode relativeNode, PNode trackNode, DoubleRange range, final SingleCircuit circuit ) {

            float w = (float) KNOB_SIZE.getWidth();
            float h = (float) KNOB_SIZE.getHeight();
            GeneralPath path = new GeneralPath();
            path.moveTo( 0f, 0f );
            path.lineTo( 0.35f * w, h / 2f );
            path.lineTo( w, h / 2f );
            path.lineTo( w, -h / 2f );
            path.lineTo( 0.35f * w, -h / 2f );
            path.closePath();

            setPathTo( path );
            setPaint( KNOB_NORMAL_COLOR );
            setStroke( KNOB_STROKE );
            setStrokePaint( KNOB_STROKE_COLOR );

            // hand cursor on knob
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PaintHighlightHandler( this, KNOB_NORMAL_COLOR, KNOB_HIGHLIGHT_COLOR ) );
            addInputEventListener( new KnobDragHandler( relativeNode, trackNode, this, range, CLConstants.PLATE_CHARGE_CONTROL_SNAP_TO_ZERO_THRESHOLD,
                                                        new VoidFunction1<Double>() {
                                                            public void apply( Double value ) {
                                                                circuit.setDisconnectedPlateCharge( value );
                                                            }
                                                        } ) );
        }
    }

    // Drag handler for the knob, snaps to zero.
    private static class KnobDragHandler extends SliderThumbDragHandler {

        private final double snapThreshold; // slider snaps to zero when model value is <= this threshold

        // see superclass for constructor params
        public KnobDragHandler( PNode relativeNode, PNode trackNode, PNode knobNode, DoubleRange range, double snapThreshold, VoidFunction1<Double> updateFunction ) {
            super( UserComponents.plateChargeSlider, false, Orientation.VERTICAL, relativeNode, trackNode, knobNode, range, updateFunction );
            this.snapThreshold = snapThreshold;
        }

        // snaps to zero if the value is within some threshold.
        @Override protected double adjustValue( double value ) {
            return ( Math.abs( value ) <= snapThreshold ) ? 0 : value;
        }
    }

    /*
     * Horizontal tick mark line, with no label.
     * Origin is at the left center of the line.
     */
    private static class TickMarkNode extends PPath {

        public TickMarkNode() {
            super( new Line2D.Double( 0, 0, TICK_MARK_LENGTH, 0 ) );
            setStrokePaint( TICK_MARK_COLOR );
            setStroke( TICK_MARK_STROKE );
        }
    }

    /*
     * Label used to indicate the range.
     * Origin is at upper-left corner of bounding box.
     */
    private static class RangeLabelNode extends HTMLNode {

        public RangeLabelNode( String label ) {
            super( label );
            setHTMLColor( RANGE_LABEL_COLOR );
            setFont( RANGE_LABEL_FONT );
        }
    }

    /*
     * Title used to indicate the purpose of this meter.
     * Origin is at upper-left corner of bounding box.
     */
    private static class TitleNode extends PText {

        public TitleNode( String label ) {
            super( label );
            setTextPaint( TITLE_COLOR );
            setFont( TITLE_FONT );
        }
    }
}
