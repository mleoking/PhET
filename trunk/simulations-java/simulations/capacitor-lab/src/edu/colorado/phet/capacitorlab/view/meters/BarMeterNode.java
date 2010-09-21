/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.activities.ToggleVisibilityActivity;
import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.ImageButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Abstract base class for all bar meters.
 * Origin is at the upper-left corner of the "track" that the bar moves in.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class BarMeterNode extends PhetPNode {

    // track
    private static final PDimension TRACK_SIZE = new PDimension( 50, 200 );
    private static final Color TRACK_FILL_COLOR = Color.WHITE;
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final Stroke TRACK_STROKE = new BasicStroke( 1f );
    
    // bar
    private static final Color BAR_STROKE_COLOR = TRACK_STROKE_COLOR;
    private static final Stroke BAR_STROKE = TRACK_STROKE;
    
    // ticks
    private static final int NUMBER_OF_TICKS = 10;
    private static final double TICK_MARK_LENGTH = 10;
    private static final Color TICK_MARK_COLOR = TRACK_STROKE_COLOR;
    private static final Stroke TICK_MARK_STROKE = TRACK_STROKE;
    
    // range labels
    private static final Font RANGE_LABEL_FONT = new PhetFont( 14 );
    private static final Color RANGE_LABEL_COLOR = Color.BLACK;
    private static final long RANGE_LABEL_FLASH_DURATION = 3000; // ms
    private static final long RANGE_LABEL_FLASH_INTERVAL = 250; // ms
    
    // title
    private static final Font TITLE_FONT = new PhetFont( 16 );
    private static final Color TITLE_COLOR = Color.BLACK;
    
    // value display
    private static final Font VALUE_FONT = new PhetFont( 14 );
    private static final Color VALUE_COLOR = Color.BLACK;
    
    // overload indicator
    private static final double OVERLOAD_INDICATOR_WIDTH = 0.75 * TRACK_SIZE.getWidth();
    private static final double OVERLOAD_INDICATOR_HEIGHT = 15;
    
    private final TrackNode trackNode;
    private final BarNode barNode;
    private final TitleNode titleNode;
    private final ValueNode valueNode;
    private final PowerOfTenRangeLabelNode maxLabelNode;
    private final RangeLabelNode minLabelNode;
    private final OverloadIndicatorNode overloadIndicatorNode;
    private final ScaleButtonNode scaleButton;
    private final PImage closeButton;
    
    private double value;
    private int valueExponent;
    
    /**
     * Constructor.
     * 
     * @param dragBoundsNode constrains the dragging to these bounds
     * @param title title displayed below the meter
     * @param barColor color used to fill the bar
     * @param valueMantissaPattern pattern used to format the mantissa of the value displayed below the meter
     * @param valueExponent exponent of the value shown below the bar
     * @param units units
     */
    public BarMeterNode( PNode dragBoundsNode, Color barColor, String title, String valueMantissaPattern, int valueExponent, String units, double value ) {
        
        if ( value < 0 ) {
            throw new IllegalArgumentException( "value must be >= 0 : " + value );
        }
        
        this.value = value;
        this.valueExponent = valueExponent;
        
        // track
        trackNode = new TrackNode();
        addChild( trackNode );
        
        // bar
        double maxValue = Math.pow( 10, valueExponent + 1 );
        barNode = new BarNode( barColor, maxValue, value );
        addChild( barNode );
        
        // ticks
        double deltaY = TRACK_SIZE.height / NUMBER_OF_TICKS;
        for ( int i = 0; i < NUMBER_OF_TICKS; i++ ) {
            TickMarkNode tickMarkNode = new TickMarkNode();
            addChild( tickMarkNode );
            tickMarkNode.setOffset( 0, ( i + 1 ) * deltaY );
        }
        
        // min range label
        minLabelNode = new RangeLabelNode( "0" );
        addChild( minLabelNode );
        
        // max range label
        maxLabelNode = new PowerOfTenRangeLabelNode( valueExponent + 1 );
        addChild( maxLabelNode );
        
        // title
        titleNode = new TitleNode( title );
        addChild( titleNode );
        
        // overload indicator 
        overloadIndicatorNode = new OverloadIndicatorNode( barColor, maxValue, value );
        addChild( overloadIndicatorNode );
        
        // value
        valueNode = new ValueNode( new DecimalFormat( valueMantissaPattern ), valueExponent, units, value );
        addChild( valueNode );
        
        // close button
        closeButton = new PImage( CLImages.CLOSE_BUTTON );
        addChild( closeButton );
        
        // zoom button
        scaleButton = new ScaleButtonNode();
        scaleButton.scale( 0.2 ); //XXX scale image files
        addChild( scaleButton );
        
        // interactivity
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new BoundedDragHandler( this, dragBoundsNode ) );
        closeButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseReleased( PInputEvent event ) {
                BarMeterNode.this.setVisible( false );
            }
        });
        scaleButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateValueExponent();
            }
        });
        
        // layout
        updateLayout();
        
        updateScaleButtonVisibility();
    }
    
    private void updateLayout() {
        double x = 0;
        double y = 0;
        trackNode.setOffset( x, y );
        // bar inside track
        barNode.setOffset( trackNode.getOffset() );
        // min label at lower left of track
        x = -( minLabelNode.getFullBoundsReference().width + 2 );
        y = trackNode.getFullBoundsReference().getMaxY() - ( minLabelNode.getFullBoundsReference().getHeight() / 2 );
        minLabelNode.setOffset( x, y );
        // max label at upper left of track
        x = -( maxLabelNode.getFullBoundsReference().width + 2 );
        y = trackNode.getFullBoundsReference().getMinX() - ( maxLabelNode.getFullBoundsReference().getHeight() / 2 );
        maxLabelNode.setOffset( x, y );
        // overload indicator centered above track
        x = trackNode.getFullBoundsReference().getCenterX();
        y = trackNode.getFullBoundsReference().getMinY() - overloadIndicatorNode.getFullBoundsReference().getHeight() - 1;
        overloadIndicatorNode.setOffset( x, y );
        // title centered below track
        x = trackNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        y = minLabelNode.getFullBoundsReference().getMaxY() + 2;
        titleNode.setOffset( x, y );
        // value centered below title
        x = titleNode.getFullBoundsReference().getCenterX() - ( valueNode.getFullBoundsReference().getWidth() / 2 );
        y = titleNode.getFullBoundsReference().getMaxY() + 2;
        valueNode.setOffset( x, y );
        // close button at upper right of track
        x = trackNode.getFullBoundsReference().getMaxX() + 2;
        y = trackNode.getFullBoundsReference().getMinY();
        closeButton.setOffset( x, y );
        // zoom button below max label
        x = barNode.getFullBoundsReference().getMinX() - scaleButton.getFullBoundsReference().getWidth() - 8;
        y = maxLabelNode.getFullBoundsReference().getMaxY() + 5;
        scaleButton.setOffset( x, y );
    }
    
    private void updateScaleButtonVisibility() {
        double mantissa = value / Math.pow( 10, valueExponent );
        // if mantissa < 1, we'll be zooming in; > 10, we'll be zooming out.
        scaleButton.setVisible( ( value != 0 ) && ( mantissa < 1 || mantissa > 10 ) );
    }
    
    private void updateValueExponent() {
        if ( value != 0 ) {
            int exponent = 0;
            // look for an exponent that make the mantissa >= 1
            while ( ( value / Math.pow( 10, exponent ) ) < 1 ) {
                exponent--;
            }
            setValueExponent( exponent );
        }
    }
    
    /**
     * Sets the value displayed by the meter.
     * Updates the bar and the value below the meter.
     * @param value
     */
    protected void setValue( double value ) {
        if ( value < 0 ) {
            throw new IllegalArgumentException( "value must be >= 0 : " + value );
        }
        if ( value != this.value ) {
            
            this.value = value;
            
            // update components
            barNode.setValue( value );
            overloadIndicatorNode.setValue( value );
            valueNode.setValue( value );
            
            updateLayout();
            updateScaleButtonVisibility();
        }
    }
    
    /*
     * Sets the exponent of the value displayed under the bar.
     * The max range value becomes 1x10^(valueExponent + 1).
     */
    private void setValueExponent( int valueExponent ) {
        if ( valueExponent != this.valueExponent ) {
            
            this.valueExponent = valueExponent;
            
            // update components
            double maxValue = Math.pow( 10, valueExponent + 1 );
            barNode.setMaxValue( maxValue );
            overloadIndicatorNode.setMaxValue( maxValue );
            maxLabelNode.setExponent( valueExponent + 1 );
            valueNode.setExponent( valueExponent );
            
            updateLayout();
            updateScaleButtonVisibility();
        }
    }
    
    /**
     * Sets the color used to fill the bar.
     * @param color
     */
    protected void setBarColor( Color color ) {
        barNode.setPaint( color );
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
     * The bar which indicates the magnitude of the value being read by the meter.
     * Origin is at upper left of track.
     */
    private static class BarNode extends PPath {
       
        private double value, maxValue;
        private final Rectangle2D rectangle;
        
        public BarNode( Color barColor, double maxValue, double value ) {
            
            this.value = value;
            this.maxValue = maxValue;
            
            rectangle = new Rectangle2D.Double( 0, 0, TRACK_SIZE.width, TRACK_SIZE.height );
            setPathTo( rectangle );
            setPaint( barColor );
            setStrokePaint( BAR_STROKE_COLOR );
            setStroke( BAR_STROKE );
            
            update();
        }
        
        public void setValue( double value ) {
            if ( value != this.value ) {
                this.value = value;
                update();
            }
        }
        
        public void setMaxValue( double maxValue ) {
            if ( maxValue != this.maxValue ) {
                this.maxValue = maxValue;
                update();
            }
        }
        
        private void update() {
            double percent = Math.min( 1, Math.abs( value ) / maxValue );
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
            this();
            setHTML( label );
        }
        
        protected RangeLabelNode() {
            setHTMLColor( RANGE_LABEL_COLOR );
            setFont( RANGE_LABEL_FONT );
        }
    }
    
    /*
     * Label used to indicate a range that is a power of 10.
     * Origin is at upper-left corner of bounding box.
     */
    private static class PowerOfTenRangeLabelNode extends RangeLabelNode {
        
        private static final String PATTERN = "<html>10<sup>{0}</sup></html>";
        
        public PowerOfTenRangeLabelNode( int exponent ) {
            super( MessageFormat.format( PATTERN, exponent ) );
        }
        
        public void setExponent( int exponent ) {
            setHTML( MessageFormat.format( PATTERN, exponent ) );
            doFlash();
        }

        /*
         * Repeatedly toggles this nodes visibility so that it appears to Flash.
         * This is done whenever the exponent is changed. The theory is that
         * students are more likely to notice that the meter's range has changed.
         */
        private void doFlash() {
            PActivity flashActivity = new ToggleVisibilityActivity( this, RANGE_LABEL_FLASH_DURATION, RANGE_LABEL_FLASH_INTERVAL );
            getRoot().addActivity( flashActivity ); // Must schedule the activity with the root for it to run.
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
     * Overload indicator, visible when the value is greater than what the bar
     * is capable of displaying.  The indicator is an arrow that point upward.
     */
    private static class OverloadIndicatorNode extends PComposite {
        
        private double value, maxValue;
        
        public OverloadIndicatorNode( Color fillColor, double maxValue, double value ) {
            
            this.value = value;
            this.maxValue = maxValue;
            
            Point2D tailLocation = new Point2D.Double( 0, OVERLOAD_INDICATOR_HEIGHT );
            Point2D tipLocation = new Point2D.Double( 0, 0 );
            double headHeight = 0.6 * OVERLOAD_INDICATOR_HEIGHT;
            double headWidth = OVERLOAD_INDICATOR_WIDTH;
            double tailWidth = headWidth / 2;
            ArrowNode arrowNode = new ArrowNode( tailLocation, tipLocation, headHeight, headWidth, tailWidth );
            arrowNode.setPaint( fillColor );
            addChild( arrowNode );
            
            update();
        }
        
        public void setValue( double value ) {
            if ( value != this.value ) {
                this.value = value;
                update();
            }
        }
        
        public void setMaxValue( double maxValue ) {
            if ( maxValue != this.maxValue ) {
                this.maxValue = maxValue;
                update();
            }
        }
        
        private void update() {
            setVisible( value > maxValue );
        }
    }
    
    /*
     * Value display that corresponds to the bar height.
     * Origin is at upper-left corner of bounding box.
     */
    private static class ValueNode extends HTMLNode {
        
        private static final String PATTERN_VALUE = "<html>{0}x10<sup>{1}</sup></html>";
        
        private final NumberFormat mantissaFormat;
        private int exponent;
        private final String units;
        private double value;
        
        public ValueNode( NumberFormat mantissaFormat, int exponent, String units, double value ) {
            setFont( VALUE_FONT );
            setHTMLColor( VALUE_COLOR );
            this.mantissaFormat = mantissaFormat;
            this.exponent = exponent;
            this.units = units;
            this.value = value;
            update();
        }
        
        public void setValue( double value ) {
            if ( value != this.value ) {
                this.value = value;
                update();
            }
        }
        
        public void setExponent( int maxExponent ) {
            if ( maxExponent != this.exponent ) {
                this.exponent = maxExponent;
                update();
            }
        }
        
        private void update() {
            String mantissaString = "0";
            if ( value != 0 ) {
                double mantissa = value / Math.pow( 10, exponent );
                mantissaString = MessageFormat.format( PATTERN_VALUE, mantissaFormat.format( mantissa ), exponent );
            }
            setHTML( MessageFormat.format( CLStrings.PATTERN_VALUE_UNITS, mantissaString, units ) );
        }
    }
    
    /*
     * Scale button.
     * Origin at upper-left corner of bounding box.
     */
    private static class ScaleButtonNode extends ImageButtonNode {
        
        public ScaleButtonNode() {
            super(  CLImages.SCALE_BUTTON_UNARMED, CLImages.SCALE_BUTTON_ARMED );
        }
    }
}
