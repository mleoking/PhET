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
import edu.colorado.phet.capacitorlab.model.ICircuit.CircuitChangeListener;
import edu.colorado.phet.capacitorlab.model.SingleCircuit;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
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
    private static final boolean KNOB_SNAP_TO_ZERO_ENABLED = true;

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

    private final TrackNode trackNode;
    private final KnobNode knobNode;
    private final TitleNode titleNode;
    private final DoubleRange range;

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

        // nodes
        trackNode = new TrackNode();
        knobNode = new KnobNode();
        TickMarkNode maxTickMarkNode = new TickMarkNode();
        RangeLabelNode maxLabelNode = new RangeLabelNode( CLStrings.LOTS_POSITIVE );
        TickMarkNode zeroTickMarkNode = new TickMarkNode();
        RangeLabelNode zeroLabelNode = new RangeLabelNode( CLStrings.NONE );
        TickMarkNode minTickMarkNode = new TickMarkNode();
        RangeLabelNode minLabelNode = new RangeLabelNode( CLStrings.LOTS_NEGATIVE );
        titleNode = new TitleNode( CLStrings.PLATE_CHARGE_TOP );

        // parent for all nodes that are part of the slider, excluding the value
        PNode parentNode = new PNode();
        parentNode.addChild( trackNode );
        parentNode.addChild( maxTickMarkNode );
        parentNode.addChild( maxLabelNode );
        parentNode.addChild( zeroTickMarkNode );
        parentNode.addChild( zeroLabelNode );
        parentNode.addChild( minTickMarkNode );
        parentNode.addChild( minLabelNode );
        parentNode.addChild( knobNode );

        // layout in parentNode
        double x = 0;
        double y = 0;
        {
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

        // background, sized to fit around parentNode
        double bWidth = parentNode.getFullBoundsReference().getWidth() + ( 2 * BACKGROUND_X_MARGIN );
        double bHeight = parentNode.getFullBoundsReference().getHeight() + ( 2 * BACKGROUND_Y_MARGIN );
        Rectangle2D backgroundRect = new Rectangle2D.Double( 0, 0, bWidth, bHeight );
        PPath backgroundNode = new PPath( backgroundRect );
        backgroundNode.setStroke( BACKGROUND_STROKE );
        backgroundNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        backgroundNode.setPaint( BACKGROUND_FILL_COLOR );

        // rendering order
        addChild( backgroundNode );
        addChild( parentNode );
        addChild( titleNode );

        // layout
        {
            x = 0;
            y = 0;
            backgroundNode.setOffset( x, y );
            x = backgroundNode.getFullBoundsReference().getCenterX() - ( parentNode.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( parentNode );
            y = backgroundNode.getFullBoundsReference().getCenterY() - ( parentNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( parentNode );
            parentNode.setOffset( x, y );
            x = backgroundNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
            y = backgroundNode.getFullBoundsReference().getMaxY() + 2;
            titleNode.setOffset( x, y );
        }

        // interactivity
        initInteractivity();

        update();
    }

    /*
     * Adds interactivity to the knob.
     */
    private void initInteractivity() {

        // hand cursor on knob
        knobNode.addInputEventListener( new CursorHandler() );

        knobNode.addInputEventListener( new PaintHighlightHandler( knobNode, KNOB_NORMAL_COLOR, KNOB_HIGHLIGHT_COLOR ) );

        // Constrain the knob to be dragged vertically within the track
        knobNode.addInputEventListener( new PDragSequenceEventHandler() {

            private double _globalClickYOffset; // y offset of mouse click from knob's origin, in global coordinates

            @Override
            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                // note the offset between the mouse click and the knob's origin
                Point2D pMouseLocal = event.getPositionRelativeTo( PlateChargeControlNode.this );
                Point2D pMouseGlobal = PlateChargeControlNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = PlateChargeControlNode.this.localToGlobal( knobNode.getOffset() );
                _globalClickYOffset = pMouseGlobal.getY() - pKnobGlobal.getY();
            }

            @Override
            protected void drag( PInputEvent event ) {
                super.drag( event );
                updateVoltage( event, true /* isDragging */ );
            }

            @Override
            protected void endDrag( PInputEvent event ) {
                updateVoltage( event, false /* isDragging */ );
                super.endDrag( event );
            }

            private void updateVoltage( PInputEvent event, boolean isDragging ) {
                // determine the knob's new offset
                Point2D pMouseLocal = event.getPositionRelativeTo( PlateChargeControlNode.this );
                Point2D pMouseGlobal = PlateChargeControlNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = new Point2D.Double( pMouseGlobal.getX(), pMouseGlobal.getY() - _globalClickYOffset );
                Point2D pKnobLocal = PlateChargeControlNode.this.globalToLocal( pKnobGlobal );

                // convert the offset to a charge value
                double yOffset = pKnobLocal.getY();
                double trackLength = trackNode.getFullBoundsReference().getHeight();
                double charge = range.getMin() + range.getLength() * ( trackLength - yOffset ) / trackLength;
                if ( charge < range.getMin() ) {
                    charge = range.getMin();
                }
                else if ( charge > range.getMax() ) {
                    charge = range.getMax();
                }

                // snap to zero if knob is release and value is close enough to zero
                if ( !isDragging && KNOB_SNAP_TO_ZERO_ENABLED && Math.abs( charge ) <= CLConstants.PLATE_CHARGE_CONTROL_SNAP_TO_ZERO_THRESHOLD ) {
                    charge = 0;
                }

                // set the model
                circuit.setDisconnectedPlateCharge( charge );
            }
        } );
    }

    private void update() {

        double plateCharge = circuit.getDisconnectedPlateCharge();

        // knob location
        double xOffset = knobNode.getXOffset();
        double yOffset = trackNode.getFullBoundsReference().getHeight() * ( ( range.getMax() - plateCharge ) / range.getLength() );
        knobNode.setOffset( xOffset, yOffset );
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
        public KnobNode() {

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
