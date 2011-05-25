// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.*;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.view.meters.TimesTenValueNode;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.PaintHighlightHandler;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Control for setting a capacitor's capacitance.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CapacitanceControlNode extends PhetPNode {

    // track
    private static final PDimension TRACK_SIZE = new PDimension( 5, 125 );
    private static final Color TRACK_FILL_COLOR = Color.LIGHT_GRAY;
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final Stroke TRACK_STROKE = new BasicStroke( 1f );

    // knob
    private static final PDimension KNOB_SIZE = new PDimension( 20, 15 );
    private static final Stroke KNOB_STROKE = new BasicStroke( 1f );
    private static final Color KNOB_NORMAL_COLOR = CLPaints.DRAGGABLE_NORMAL;
    private static final Color KNOB_HIGHLIGHT_COLOR = CLPaints.DRAGGABLE_HIGHLIGHT;
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;

    // background
    private static final double BACKGROUND_X_MARGIN = 3;
    private static final double BACKGROUND_Y_MARGIN = ( KNOB_SIZE.getHeight() / 2 ) + 4;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Color BACKGROUND_FILL_COLOR = new JPanel().getBackground();

    // title
    private static final Font TITLE_FONT = new PhetFont( Font.BOLD, 16 );
    private static final Color TITLE_COLOR = Color.BLACK;

    // value
    private static final NumberFormat VALUE_FORMAT = new DecimalFormat( "0.00" );
    private static final Font VALUE_FONT = new PhetFont( 14 );
    private static final Color VALUE_COLOR = Color.BLACK;

    private final Capacitor capacitor;

    private final TrackNode trackNode;
    private final KnobNode knobNode;
    private final TitleNode titleNode;
    private final DoubleRange range;
    private final TimesTenValueNode valueNode;

    public CapacitanceControlNode( final Capacitor capacitor, DoubleRange range, int displayExponent ) {

        this.range = range;

        this.capacitor = capacitor;
        capacitor.addCapacitorChangeListener( new CapacitorChangeListener() {
            public void capacitorChanged() {
                update();
            }
        } );

        // nodes
        trackNode = new TrackNode();
        knobNode = new KnobNode();
        titleNode = new TitleNode( CLStrings.CAPACITANCE );
        valueNode = new TimesTenValueNode( VALUE_FORMAT, displayExponent, CLStrings.FARADS, capacitor.getTotalCapacitance(), VALUE_FONT, VALUE_COLOR );

        // background, sized to fit around parentNode
        Rectangle2D backgroundRect = new Rectangle2D.Double( 0, 0,
                                                             knobNode.getFullBoundsReference().getWidth() + ( 2 * BACKGROUND_X_MARGIN ),
                                                             trackNode.getFullBoundsReference().getHeight() + ( 2 * BACKGROUND_Y_MARGIN ) );
        PPath backgroundNode = new PPath( backgroundRect );
        backgroundNode.setStroke( BACKGROUND_STROKE );
        backgroundNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        backgroundNode.setPaint( BACKGROUND_FILL_COLOR );

        // rendering order
        addChild( backgroundNode );
        addChild( trackNode );
        addChild( knobNode );
        addChild( titleNode );
        addChild( valueNode );

        // layout
        {
            double x = 0;
            double y = 0;
            backgroundNode.setOffset( x, y );
            x = backgroundNode.getFullBoundsReference().getCenterX() - ( trackNode.getFullBoundsReference().getWidth() / 2 );
            y = backgroundNode.getFullBoundsReference().getCenterY() - ( trackNode.getFullBoundsReference().getHeight() / 2 );
            trackNode.setOffset( x, y );
            x = backgroundNode.getFullBoundsReference().getCenterX() + ( knobNode.getFullBoundsReference().getWidth() / 2 );
            y = backgroundNode.getFullBoundsReference().getCenterY();
            knobNode.setOffset( x, y );
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
                Point2D pMouseLocal = event.getPositionRelativeTo( CapacitanceControlNode.this );
                Point2D pMouseGlobal = CapacitanceControlNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = CapacitanceControlNode.this.localToGlobal( knobNode.getOffset() );
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
                Point2D pMouseLocal = event.getPositionRelativeTo( CapacitanceControlNode.this );
                Point2D pMouseGlobal = CapacitanceControlNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = new Point2D.Double( pMouseGlobal.getX(), pMouseGlobal.getY() - _globalClickYOffset );
                Point2D pKnobLocal = CapacitanceControlNode.this.globalToLocal( pKnobGlobal );

                // convert the offset to a charge value
                double yOffset = pKnobLocal.getY();
                double trackLength = trackNode.getFullBoundsReference().getHeight();
                double capacitance = range.getMin() + range.getLength() * ( trackLength - yOffset ) / trackLength;
                if ( capacitance < range.getMin() ) {
                    capacitance = range.getMin();
                }
                else if ( capacitance > range.getMax() ) {
                    capacitance = range.getMax();
                }

                // set the model
                capacitor.setTotalCapacitance( capacitance );
            }
        } );
    }

    private void update() {

        double capacitance = capacitor.getTotalCapacitance();

        // knob location
        double x = knobNode.getXOffset();
        double y = trackNode.getYOffset() + trackNode.getFullBoundsReference().getHeight() * ( ( range.getMax() - capacitance ) / range.getLength() );
        knobNode.setOffset( x, y );

        // value centered below title
        valueNode.setValue( capacitance );
        x = titleNode.getFullBoundsReference().getCenterX() - ( valueNode.getFullBoundsReference().getWidth() / 2 );
        y = titleNode.getFullBoundsReference().getMaxY() + 3;
        valueNode.setOffset( x, y );
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
     * The slider knob, points to the right.
     * Origin is at the knob's tip.
     */
    private static class KnobNode extends PPath {
        public KnobNode() {

            float w = (float) KNOB_SIZE.getWidth();
            float h = (float) KNOB_SIZE.getHeight();
            GeneralPath path = new GeneralPath();
            path.moveTo( 0f, 0f );
            path.lineTo( 0.35f * -w, h / 2f );
            path.lineTo( -w, h / 2f );
            path.lineTo( -w, -h / 2f );
            path.lineTo( 0.35f * -w, -h / 2f );
            path.closePath();

            setPathTo( path );
            setPaint( KNOB_NORMAL_COLOR );
            setStroke( KNOB_STROKE );
            setStrokePaint( KNOB_STROKE_COLOR );
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
