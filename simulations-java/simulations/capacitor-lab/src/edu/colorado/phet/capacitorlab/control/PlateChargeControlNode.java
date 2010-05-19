/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.PaintHighlightHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Meter that displays charge on the capacitor plates. 
 * Origin is at the upper-left corner of the "track" that the bar moves in.
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
    private static final Color BACKGROUND_FILL_COLOR = Color.WHITE;
    
    // knob
    private static final PDimension KNOB_SIZE = new PDimension( 20, 15 );
    private static final Stroke KNOB_STROKE = new BasicStroke( 1f );
    private static final Color KNOB_NORMAL_COLOR = CLPaints.DRAGGABLE_NORMAL;
    private static final Color KNOB_HIGHLIGHT_COLOR = CLPaints.DRAGGABLE_HIGHLIGHT;
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;
    
    // ticks
    private static final double TICK_MARK_LENGTH = 10;
    private static final Color TICK_MARK_COLOR = TRACK_STROKE_COLOR;
    private static final Stroke TICK_MARK_STROKE = TRACK_STROKE;
    
    // range labels
    private static final String RANGE_LABEL_NONE = CLStrings.LABEL_PLATE_CHARGE_NONE;
    private static final String RANGE_LABEL_LOTS = CLStrings.LABEL_PLATE_CHARGE_LOTS;
    private static final Font RANGE_LABEL_FONT = new PhetFont( 14 );
    private static final Color RANGE_LABEL_COLOR = Color.BLACK;
    
    // title
    private static final Font TITLE_FONT = new PhetFont( 16 );
    private static final Color TITLE_COLOR = Color.BLACK;
    
    // value display
    private static final boolean VALUE_VISIBLE = false;
    private static final int VALUE_EXPONENT = -11; //XXX compute based on range.max
    private static final Font VALUE_FONT = new PhetFont( 14 );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final NumberFormat VALUE_MANTISSA_FORMAT = new DecimalFormat( "0.000" );
    private static final String VALUE_PATTERN = "<html>{0}x10<sup>" + String.valueOf( VALUE_EXPONENT ) + "</sup>";
    
    private final BatteryCapacitorCircuit circuit;
    
    private final TrackNode trackNode;
    private final KnobNode knobNode;
    private final TitleNode titleNode;
    private final ValueNode valueNode;
    private final DoubleRange range;
    
    public PlateChargeControlNode( final BatteryCapacitorCircuit circuit ) {
        
        range = new DoubleRange( 0, BatteryCapacitorCircuit.getMaxPlateCharge() );
        
        this.circuit = circuit;
        circuit.addBatteryCapacitorCircuitChangeListener( new  BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void chargeChanged() {
                if ( !circuit.isBatteryConnected() ) {
                    update();
                }
            }
        });
        
        // nodes
        trackNode = new TrackNode();
        knobNode = new KnobNode();
        TickMarkNode lotsTickMarkNode = new TickMarkNode();
        RangeLabelNode lotsLabelNode = new RangeLabelNode( RANGE_LABEL_LOTS );
        TickMarkNode noneTickMarkNode = new TickMarkNode();
        RangeLabelNode noneLabelNode = new RangeLabelNode( RANGE_LABEL_NONE );
        titleNode = new TitleNode( CLStrings.LABEL_PLATE_CHARGE );
        valueNode = new ValueNode( circuit.getTotalPlateCharge() );
        
        // parent for all nodes that are part of the slider, excluding the value
        PNode parentNode = new PNode();
        parentNode.addChild( trackNode );
        parentNode.addChild( lotsTickMarkNode );
        parentNode.addChild( lotsLabelNode );
        parentNode.addChild( noneTickMarkNode );
        parentNode.addChild( noneLabelNode );
        parentNode.addChild( knobNode );
        
        // layout in parentNode
        double x = 0;
        double y = 0;
        trackNode.setOffset( x, y );
        x = -5; // determines the overlap with the track
        y = 0; // don't care, set by update
        knobNode.setOffset( x, y );
        x = -lotsTickMarkNode.getFullBoundsReference().getWidth();
        y = trackNode.getFullBoundsReference().getMinY() + lotsTickMarkNode.getFullBoundsReference().getHeight() / 2;
        lotsTickMarkNode.setOffset( x, y );
        x = lotsTickMarkNode.getFullBoundsReference().getMinX() - lotsLabelNode.getFullBoundsReference().getWidth() - 2;
        y = lotsTickMarkNode.getFullBoundsReference().getCenterY() - ( lotsLabelNode.getFullBoundsReference().getHeight() / 2 );
        lotsLabelNode.setOffset( x, y );
        x = -noneTickMarkNode.getFullBoundsReference().getWidth();
        y = trackNode.getFullBoundsReference().getMaxY() - noneTickMarkNode.getFullBoundsReference().getHeight() / 2;
        noneTickMarkNode.setOffset( x, y );
        x = noneTickMarkNode.getFullBoundsReference().getMinX() - noneLabelNode.getFullBoundsReference().getWidth() - 2;
        y = noneTickMarkNode.getFullBoundsReference().getCenterY() - ( noneLabelNode.getFullBoundsReference().getHeight() / 2 );
        noneLabelNode.setOffset( x, y );
        
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
        if ( VALUE_VISIBLE ) {
            addChild( valueNode );
        }
        
        // layout
        x = 0;
        y = 0;
        backgroundNode.setOffset( x, y );
        x = backgroundNode.getFullBoundsReference().getCenterX() - ( parentNode.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( parentNode );
        y = backgroundNode.getFullBoundsReference().getCenterY() - ( parentNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( parentNode );
        parentNode.setOffset( x, y );
        x = backgroundNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        y = backgroundNode.getFullBoundsReference().getMaxY() + 2;
        titleNode.setOffset( x, y );
        x = titleNode.getFullBoundsReference().getCenterX() - ( valueNode.getFullBoundsReference().getWidth() / 2 );
        y = titleNode.getFullBoundsReference().getMaxY() + 2;
        valueNode.setOffset( x, y );
        
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
        knobNode.addInputEventListener( new PDragEventHandler() {
            
            private double _globalClickYOffset; // y offset of mouse click from knob's origin, in global coordinates
            
            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                // note the offset between the mouse click and the knob's origin
                Point2D pMouseLocal = event.getPositionRelativeTo( PlateChargeControlNode.this );
                Point2D pMouseGlobal = PlateChargeControlNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = PlateChargeControlNode.this.localToGlobal( knobNode.getOffset() );
                _globalClickYOffset = pMouseGlobal.getY() - pKnobGlobal.getY();
            }

            protected void drag(PInputEvent event) {
                
                // determine the knob's new offset
                Point2D pMouseLocal = event.getPositionRelativeTo( PlateChargeControlNode.this );
                Point2D pMouseGlobal = PlateChargeControlNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = new Point2D.Double( pMouseGlobal.getX(), pMouseGlobal.getY() - _globalClickYOffset );
                Point2D pKnobLocal = PlateChargeControlNode.this.globalToLocal( pKnobGlobal );
                
                // convert the offset to a pH value
                double yOffset = pKnobLocal.getY();
                double trackLength = trackNode.getFullBoundsReference().getHeight();
                double value = range.getMin() + range.getLength() * ( trackLength - yOffset ) / trackLength;
                if ( value < range.getMin() ) {
                    value = range.getMin();
                }
                else if ( value > range.getMax() ) {
                    value = range.getMax();
                }
                
                // set the model
                circuit.setDisconnectedPlateCharge( value );
            }
        } );
    }
    
    private void update() {
        
        double plateCharge = circuit.getDisconnectedPlateCharge();
        
        // knob location
        double xOffset = knobNode.getXOffset();
        double yOffset = trackNode.getFullBoundsReference().getHeight() * ( ( range.getMax() - plateCharge ) / range.getLength() );
        knobNode.setOffset( xOffset, yOffset );
        
        // value, centered below title
        valueNode.setValue( plateCharge );
        double x = titleNode.getFullBoundsReference().getCenterX() - ( valueNode.getFullBoundsReference().getWidth() / 2 );
        double y = titleNode.getFullBoundsReference().getMaxY() + 2;
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
     * Origin is at the left end of the line.
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
    
    /*
     * Value that corresponds to the bar height.
     * Origin is at upper-left corner of bounding box.
     */
    private static class ValueNode extends HTMLNode {
        
        public ValueNode( double value ) {
            setFont( VALUE_FONT );
            setHTMLColor( VALUE_COLOR );
            setValue( value );
        }
        
        public void setValue( double value ) {
            String valueString = "0";
            if ( value != 0 ) {
                double mantissaString = value / Math.pow( 10, VALUE_EXPONENT );
                valueString = MessageFormat.format( VALUE_PATTERN, VALUE_MANTISSA_FORMAT.format( mantissaString ) );
            }
            setHTML( MessageFormat.format( CLStrings.PATTERN_VALUE, valueString, CLStrings.UNITS_COULOMBS ) );
        }
    }
}
