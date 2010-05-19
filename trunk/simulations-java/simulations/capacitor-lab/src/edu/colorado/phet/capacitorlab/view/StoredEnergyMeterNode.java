/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Meter that displays stored energy. 
 * Origin is at the upper-left corner of the "track" that the bar moves in.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StoredEnergyMeterNode extends PhetPNode {

    /*
     * Range of the meter is 0 to 1x10^MAX_EXPONENT Joules.
     * Changing this will adjust label, ticks, value display, etc.
     */
    private static final int MAX_EXPONENT = CLConstants.STORED_ENERGY_METER_MAX_EXPONENT; 
    
    private static final double MAX_VALUE = Math.pow( 10, MAX_EXPONENT );
    private static final String MAX_LABEL = "<html>10<sup>" + String.valueOf( MAX_EXPONENT ) + "</sup>";
    
    // track
    private static final PDimension TRACK_SIZE = new PDimension( 50, 200 );
    private static final Color TRACK_FILL_COLOR = Color.WHITE;
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final Stroke TRACK_STROKE = new BasicStroke( 1f );
    
    // bar
    private static final Color BAR_FILL_COLOR = CLPaints.ENERGY;
    private static final Color BAR_STROKE_COLOR = TRACK_STROKE_COLOR;
    private static final Stroke BAR_STROKE = TRACK_STROKE;
    
    // ticks
    private static final double TICK_SPACING = MAX_VALUE / 10;
    private static final double TICK_MARK_LENGTH = 10;
    private static final Color TICK_MARK_COLOR = TRACK_STROKE_COLOR;
    private static final Stroke TICK_MARK_STROKE = TRACK_STROKE;
    
    // range labels
    private static final Font RANGE_LABEL_FONT = new PhetFont( 14 );
    private static final Color RANGE_LABEL_COLOR = Color.BLACK;
    
    // title
    private static final Font TITLE_FONT = new PhetFont( 16 );
    private static final Color TITLE_COLOR = Color.BLACK;
    
    // value display
    private static final Font VALUE_FONT = new PhetFont( 14 );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final NumberFormat VALUE_MANTISSA_FORMAT = new DecimalFormat( "0.000" );
    private static final String VALUE_PATTERN = "<html>{0}x10<sup>" + String.valueOf( MAX_EXPONENT - 1 ) + "</sup>";
    
    private final BatteryCapacitorCircuit circuit;
    private final BarNode barNode;
    private final TitleNode titleNode;
    private final ValueNode valueNode;
    
    public StoredEnergyMeterNode( BatteryCapacitorCircuit circuit, PNode dragBoundsNode ) {
        
        this.circuit = circuit;
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void energyChanged() {
                update();
            }
        });
        
        double x = 0;
        double y = 0;
        
        // track
        TrackNode trackNode = new TrackNode();
        addChild( trackNode );
        trackNode.setOffset( x, y );
        
        // bar
        barNode = new BarNode();
        addChild( barNode );
        barNode.setOffset( x, y );
        
        // ticks
        int numberOfTicks = (int)( MAX_VALUE / TICK_SPACING ) - 1;
        double deltaY = TRACK_SIZE.height / numberOfTicks;
        for ( int i = 0; i < numberOfTicks; i++ ) {
            TickMarkNode tickMarkNode = new TickMarkNode();
            addChild( tickMarkNode );
            tickMarkNode.setOffset( 0, ( i + 1 ) * deltaY );
        }
        
        // min range label
        RangeLabelNode minLabelNode = new RangeLabelNode( "0" );
        addChild( minLabelNode );
        x = -( minLabelNode.getFullBoundsReference().width + 2 );
        y = trackNode.getFullBoundsReference().getMaxY() - ( minLabelNode.getFullBoundsReference().getHeight() / 2 );
        minLabelNode.setOffset( x, y );
        
        // max range label
        RangeLabelNode maxLabelNode = new RangeLabelNode( MAX_LABEL );
        addChild( maxLabelNode );
        x = -( maxLabelNode.getFullBoundsReference().width + 2 );
        y = trackNode.getFullBoundsReference().getMinX() - ( maxLabelNode.getFullBoundsReference().getHeight() / 2 );
        maxLabelNode.setOffset( x, y );
        
        // title
        titleNode = new TitleNode( CLStrings.METER_STORED_ENERGY );
        addChild( titleNode );
        x = trackNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        y = minLabelNode.getFullBoundsReference().getMaxY() + 2;
        titleNode.setOffset( x, y );
        
        // value
        valueNode = new ValueNode( circuit.getTotalPlateCharge() );
        addChild( valueNode );
        x = titleNode.getFullBoundsReference().getCenterX() - ( valueNode.getFullBoundsReference().getWidth() / 2 );
        y = titleNode.getFullBoundsReference().getMaxY() + 2;
        valueNode.setOffset( x, y );
        
        // close button
        PImage closeButton = new PImage( CLImages.CLOSE_BUTTON );
        x = trackNode.getFullBoundsReference().getMaxX() + 2;
        y = trackNode.getFullBoundsReference().getMinY();
        closeButton.setOffset( x, y );
        closeButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseReleased( PInputEvent event ) {
                StoredEnergyMeterNode.this.setVisible( false );
            }
        });
        addChild( closeButton );
        
        // interactivity
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new BoundedDragHandler( this, dragBoundsNode ) );
        
        update();
    }
    
    private void update() {
        
        double storedEnergy = circuit.getStoredEnergy();
        
        // bar height
        barNode.setValue( storedEnergy );

        // value, centered below title
        valueNode.setValue( storedEnergy );
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
     * The bar which indicates the number of Coulombs being read by the meter.
     * Origin is at upper left of track!
     */
    private static class BarNode extends PPath {
        
        private final Rectangle2D rectangle;
        
        public BarNode() {
            rectangle = new Rectangle2D.Double( 0, 0, TRACK_SIZE.width, TRACK_SIZE.height );
            setPathTo( rectangle );
            setPaint( BAR_FILL_COLOR );
            setStrokePaint( BAR_STROKE_COLOR );
            setStroke( BAR_STROKE );
        }
        
        public void setValue( double value ) {
            double percent = value / MAX_VALUE;
            if ( percent < 0 || percent > 1 ) {
                percent = 1;
                System.err.println( "ChargeMeter.BarNode, value out of range: " + value );
            }
            double y = ( 1 - percent ) * TRACK_SIZE.height;
            double height = TRACK_SIZE.height - y;
            rectangle.setRect( 0, y, TRACK_SIZE.width, height );
            setPathTo( rectangle );
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
            String mantissaString = "0";
            if ( value != 0 ) {
                double mantissa = value / TICK_SPACING;
                mantissaString = MessageFormat.format( VALUE_PATTERN, VALUE_MANTISSA_FORMAT.format( mantissa ) );
            }
            setHTML( MessageFormat.format( CLStrings.PATTERN_VALUE, mantissaString, CLStrings.UNITS_JOULES ) );
        }
    }
}
