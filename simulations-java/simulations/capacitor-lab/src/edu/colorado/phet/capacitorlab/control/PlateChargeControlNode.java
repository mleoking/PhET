// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit.CircuitChangeListener;
import edu.colorado.phet.capacitorlab.model.circuit.SingleCircuit;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.PaintHighlightHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
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
            knobNode = new KnobNode( trackNode, range, circuit );
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

        public KnobNode( PNode trackNode, DoubleRange range, final SingleCircuit circuit ) {

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
            addInputEventListener( new KnobDragHandler( trackNode, range, CLConstants.PLATE_CHARGE_CONTROL_SNAP_TO_ZERO_THRESHOLD,
                                                        new VoidFunction1<Double>() {
                                                            public void apply( Double value ) {
                                                                circuit.setDisconnectedPlateCharge( value );
                                                            }
                                                        } ) );
        }
    }

    // Drag handler for the knob, updates disconnected plate charge as the knob is dragged.
    private static class KnobDragHandler extends PDragSequenceEventHandler {

        private final PNode trackNode;
        private final DoubleRange range;
        private final double snapThreshold;
        private final VoidFunction1<Double> updateFunction;
        private double globalClickYOffset; // y offset of mouse click from knob's origin, in global coordinates

        public KnobDragHandler( PNode trackNode, DoubleRange range, double snapThreshold, VoidFunction1<Double> updateFunction ) {
            this.trackNode = trackNode;
            this.range = range;
            this.snapThreshold = snapThreshold;
            this.updateFunction = updateFunction;
        }

        @Override
        protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            // note the offset between the mouse click and the knob's origin
            PNode parent = event.getPickedNode().getParent();
            Point2D pMouseLocal = event.getPositionRelativeTo( parent );
            Point2D pMouseGlobal = parent.localToGlobal( pMouseLocal );
            Point2D pKnobGlobal = parent.localToGlobal( event.getPickedNode().getOffset() );
            globalClickYOffset = pMouseGlobal.getY() - pKnobGlobal.getY();
        }

        @Override
        protected void drag( PInputEvent event ) {
            super.drag( event );
            updateValue( event, true /* isDragging */ );
        }

        @Override
        protected void endDrag( PInputEvent event ) {
            updateValue( event, false /* isDragging */ );
            super.endDrag( event );
        }

        private void updateValue( PInputEvent event, boolean isDragging ) {

            // determine the knob's new offset
            PNode parent = event.getPickedNode().getParent();
            Point2D pMouseLocal = event.getPositionRelativeTo( parent );
            Point2D pMouseGlobal = parent.localToGlobal( pMouseLocal );
            Point2D pKnobGlobal = new Point2D.Double( pMouseGlobal.getX(), pMouseGlobal.getY() - globalClickYOffset );
            Point2D pKnobLocal = parent.globalToLocal( pKnobGlobal );

            // convert the offset to a charge value
            double yOffset = pKnobLocal.getY();
            double trackLength = trackNode.getFullBoundsReference().getHeight();
            double value = range.getMin() + range.getLength() * ( trackLength - yOffset ) / trackLength;
            value = MathUtil.clamp( value, range );

            // snap to zero if knob is released and value is close enough to zero
            if ( !isDragging && Math.abs( value ) <= snapThreshold ) {
                value = 0;
            }

            updateFunction.apply( value );
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
