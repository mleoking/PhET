// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.ImageHighlightHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Slider used to control battery voltage.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VoltageSliderNode extends PhetPNode {

    // track properties
    private static final Color TRACK_COLOR = Color.BLACK;
    private static final Stroke TRACK_STROKE = new BasicStroke( 2f );

    // knob properties
    private static final boolean KNOB_SNAP_TO_ZERO_ENABLED = true;

    // tick mark properties
    private static final double TICK_MARK_LENGTH = 13;
    private static final Color TICK_MARK_COLOR = TRACK_COLOR;
    private static final Stroke TICK_MARK_STROKE = TRACK_STROKE;

    // tick label properties
    private static final Color TICK_LABEL_COLOR = TRACK_COLOR;
    private static final DecimalFormat TICK_LABEL_NONZERO_FORMAT = new DecimalFormat( "0.0" );
    private static final DecimalFormat TICK_LABEL_ZERO_FORMAT = new DecimalFormat( "0" );
    private static final Font TICK_LABEL_FONT = new PhetFont( 14 );

    private final TrackNode trackNode;
    private final KnobNode knobNode;

    public VoltageSliderNode( final Battery battery, final DoubleRange voltageRange, double trackLength ) {

        // track
        trackNode = new TrackNode( trackLength );
        addChild( trackNode );

        // ticks
        assert ( voltageRange.getMax() > 0 && voltageRange.getMin() < 0 );
        TickNode maxTickNode = new TickNode( TICK_MARK_LENGTH, voltageRange.getMax() );
        addChild( maxTickNode );
        TickNode zeroTickNode = new TickNode( TICK_MARK_LENGTH, 0 );
        addChild( zeroTickNode );
        TickNode minTickNode = new TickNode( TICK_MARK_LENGTH, voltageRange.getMin() );
        addChild( minTickNode );

        // knob
        knobNode = new KnobNode( this, trackNode, voltageRange, battery );
        addChild( knobNode );

        // layout
        double x = 0;
        double y = 0;
        trackNode.setOffset( x, y );
        maxTickNode.setOffset( x, y );
        y = ( trackNode.getFullBoundsReference().getHeight() / 2 );
        zeroTickNode.setOffset( x, y );
        y = trackNode.getFullBoundsReference().getHeight();
        minTickNode.setOffset( x, y );
        y = 0; // don't care, this will be set by setVoltage
        knobNode.setOffset( x, y );

        battery.addVoltageObserver( new SimpleObserver() {
            public void update() {
                double voltage = battery.getVoltage();
                double xOffset = knobNode.getXOffset();
                double yOffset = trackNode.getFullBoundsReference().getHeight() * ( ( voltageRange.getMax() - voltage ) / voltageRange.getLength() );
                knobNode.setOffset( xOffset, yOffset );
            }
        } );
    }

    /*
     * Slider track, this is what the knob moves in.
     * Origin is a at upper left of bounding rectangle.
     */
    private static class TrackNode extends PPath {

        public TrackNode( double trackLength ) {
            super( new Line2D.Double( 0, 0, 0, trackLength ) );
            setStroke( TRACK_STROKE );
            setStrokePaint( TRACK_COLOR );
        }
    }

    /*
     * Slider knob (aka thumb), highlighted while the mouse is pressed or the mouse is inside the knob.
     * Origin is in the center of the knob's bounding rectangle.
     */
    private static class KnobNode extends PNode {

        public KnobNode( PNode parent, PNode trackNode, DoubleRange range, final Battery battery ) {
            // image, origin moved to center
            PImage imageNode = new PImage( CLImages.SLIDER_KNOB );
            addChild( imageNode );
            double x = -( imageNode.getFullBoundsReference().getWidth() / 2 );
            double y = -( imageNode.getFullBoundsReference().getHeight() / 2 );
            imageNode.setOffset( x, y );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new ImageHighlightHandler( imageNode, CLImages.SLIDER_KNOB, CLImages.SLIDER_KNOB_HIGHLIGHT ) );
            addInputEventListener( new KnobDragHandler( parent, trackNode, this, range, CLConstants.BATTERY_VOLTAGE_SNAP_TO_ZERO_THRESHOLD,
                                                        new VoidFunction1<Double>() {
                                                            public void apply( Double value ) {
                                                                battery.setVoltage( value );
                                                            }
                                                        } ) );
        }
    }

    // Drag handler for the knob, snaps to zero.
    public static class KnobDragHandler extends PDragSequenceEventHandler {

        private final PNode parent;
        private final PNode trackNode, knobNode;
        private final DoubleRange range;
        private final double snapThreshold;
        private final VoidFunction1<Double> updateFunction;
        private double globalClickYOffset; // y offset of mouse click from knob's origin, in global coordinates

        public KnobDragHandler( PNode parent, PNode trackNode, PNode knobNode, DoubleRange range, double snapThreshold, VoidFunction1<Double> updateFunction ) {
            this.parent = parent;
            this.trackNode = trackNode;
            this.knobNode = knobNode;
            this.range = range;
            this.snapThreshold = snapThreshold;
            this.updateFunction = updateFunction;
        }

        @Override protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            // note the offset between the mouse click and the knob's origin
            Point2D pMouseLocal = event.getPositionRelativeTo( parent );
            Point2D pMouseGlobal = parent.localToGlobal( pMouseLocal );
            Point2D pKnobGlobal = parent.localToGlobal( knobNode.getOffset() );
            globalClickYOffset = pMouseGlobal.getY() - pKnobGlobal.getY();
        }

        @Override protected void drag( PInputEvent event ) {
            super.drag( event );
            updateValue( event, true /* isDragging */ );
        }

        @Override protected void endDrag( PInputEvent event ) {
            updateValue( event, false /* isDragging */ );
            super.endDrag( event );
        }

        // snaps to zero if the value is within some threshold.
        protected double adjustValue( double value ) {
            return ( Math.abs( value ) <= snapThreshold ) ? 0 : value;
        }

        private void updateValue( PInputEvent event, boolean isDragging ) {

            // determine the knob's new offset
            Point2D pMouseLocal = event.getPositionRelativeTo( parent );
            Point2D pMouseGlobal = parent.localToGlobal( pMouseLocal );
            Point2D pKnobGlobal = new Point2D.Double( pMouseGlobal.getX(), pMouseGlobal.getY() - globalClickYOffset );
            Point2D pKnobLocal = parent.globalToLocal( pKnobGlobal );

            // convert the offset to a voltage value
            double yOffset = pKnobLocal.getY();
            double trackLength = trackNode.getFullBoundsReference().getHeight();
            double value = range.getMin() + range.getLength() * ( trackLength - yOffset ) / trackLength;
            value = MathUtil.clamp( value, range );

            // snap to zero if knob is release and value is close enough to zero
            if ( !isDragging ) {
                value = adjustValue( value );
            }

            updateFunction.apply( value );
        }
    }

    /*
     * A tick, horizontal line (mark) + value label, use to indicate a specific value on the slider track.
     * Origin is at upper left corner of the horizontal line.
     */
    private static class TickNode extends PComposite {

        public TickNode( double length, double value ) {

            TickMarkNode markNode = new TickMarkNode( length );
            addChild( markNode );

            TickLabelNode labelNode = new TickLabelNode( value );
            addChild( labelNode );

            double x = 0;
            double y = 0;
            markNode.setOffset( x, y );
            x = markNode.getFullBoundsReference().getWidth() + 5;
            y = ( markNode.getFullBoundsReference().getHeight() - labelNode.getFullBoundsReference().getHeight() ) / 2;
            labelNode.setOffset( x, y );
        }
    }

    /*
     * A tick mark.
     * Origin is at upper left of bounding rectangle.
     */
    private static class TickMarkNode extends PPath {

        public TickMarkNode( double length ) {
            super( new Line2D.Double( 0, 0, length, 0 ) );
            setStroke( TICK_MARK_STROKE );
            setStrokePaint( TICK_MARK_COLOR );
        }
    }

    /*
     * A label on a tick mark.
     * Origin is at upper left of bounding rectangle.
     */
    private static class TickLabelNode extends PText {
        public TickLabelNode( double value ) {
            setFont( TICK_LABEL_FONT );
            setTextPaint( TICK_LABEL_COLOR );
            NumberFormat format = ( value == 0 ) ? TICK_LABEL_ZERO_FORMAT : TICK_LABEL_NONZERO_FORMAT;
            setText( MessageFormat.format( CLStrings.PATTERN_VALUE_UNITS, format.format( value ), CLStrings.VOLTS ) );
        }
    }
}
