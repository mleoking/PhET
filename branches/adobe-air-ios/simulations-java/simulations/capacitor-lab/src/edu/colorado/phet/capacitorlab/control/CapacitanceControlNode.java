// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JPanel;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLSimSharing;
import edu.colorado.phet.capacitorlab.CLSimSharing.UserComponents;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.view.meters.TimesTenValueNode;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.PaintHighlightHandler;
import edu.colorado.phet.common.piccolophet.event.SliderThumbDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Control for setting a capacitor's capacitance.
 * Origin at upper-left of bounding rectangle.
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
    private static final PhetFont VALUE_FONT = new PhetFont( 16 );
    private static final Color VALUE_COLOR = Color.BLACK;

    private final Capacitor capacitor;
    private final TrackNode trackNode;
    private final KnobNode knobNode;
    private final TitleNode titleNode;
    private final DoubleRange range;
    private final TimesTenValueNode valueNode;
    private final double snapInterval;

    /**
     * Constructor
     *
     * @param capacitor       the capacitor we're controlling
     * @param range           range of capacitance, in Farads
     * @param displayExponent power of 10 exponent used in displaying the value
     */
    public CapacitanceControlNode( final Capacitor capacitor, DoubleRange range, int displayExponent ) {

        this.range = range;
        this.snapInterval = Math.pow( 10, displayExponent - 1 ); // snap to 0.1 of the mantissa

        this.capacitor = capacitor;
        capacitor.addCapacitorChangeListener( new CapacitorChangeListener() {
            public void capacitorChanged() {
                update();
            }
        } );

        // nodes
        trackNode = new TrackNode();
        knobNode = new KnobNode( this, trackNode, range, snapInterval, capacitor );
        titleNode = new TitleNode( CLStrings.CAPACITANCE );
        valueNode = new TimesTenValueNode( VALUE_FORMAT, displayExponent, CLStrings.FARADS, capacitor.getTotalCapacitance(), VALUE_FONT, VALUE_COLOR );

        // background, sized to fit around track and knob.
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
            // background at origin
            double x = 0;
            double y = 0;
            backgroundNode.setOffset( x, y );
            // track centered in background
            x = backgroundNode.getFullBoundsReference().getCenterX() - ( trackNode.getFullBoundsReference().getWidth() / 2 );
            y = backgroundNode.getFullBoundsReference().getCenterY() - ( trackNode.getFullBoundsReference().getHeight() / 2 );
            trackNode.setOffset( x, y );
            // knob centered in track
            x = backgroundNode.getFullBoundsReference().getCenterX() + ( knobNode.getFullBoundsReference().getWidth() / 2 );
            y = backgroundNode.getFullBoundsReference().getCenterY();
            knobNode.setOffset( x, y );
            // title centered below background
            x = backgroundNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
            y = backgroundNode.getFullBoundsReference().getMaxY() + 2;
            titleNode.setOffset( x, y );
        }

        update();
    }

    // Updates the control to match the capacitor model.
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

        public KnobNode( PNode relativeNode, PNode trackNode, DoubleRange range, double snapInterval, final Capacitor capacitor ) {

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

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PaintHighlightHandler( this, KNOB_NORMAL_COLOR, KNOB_HIGHLIGHT_COLOR ) );
            addInputEventListener( new KnobDragHandler( relativeNode, trackNode, this, range, snapInterval,
                                                        new VoidFunction1<Double>() {
                                                            public void apply( Double value ) {
                                                                capacitor.setTotalCapacitance( value );
                                                            }
                                                        } ) );
        }
    }

    // Drag handler for the knob, snaps to closet value.
    private static class KnobDragHandler extends SliderThumbDragHandler {

        private final double snapInterval; // slider snaps to closet model value in this interval

        // see superclass for constructor params
        public KnobDragHandler( PNode relativeNode, PNode trackNode, PNode knobNode, DoubleRange range, double snapInterval, VoidFunction1<Double> updateFunction ) {
            super( UserComponents.capacitanceSlider, false, Orientation.VERTICAL, relativeNode, trackNode, knobNode, range, updateFunction );
            this.snapInterval = snapInterval;
        }

        // snaps to the closest value
        @Override protected double adjustValue( double value ) {
            return Math.floor( ( value / snapInterval ) + 0.5d ) * snapInterval;
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
