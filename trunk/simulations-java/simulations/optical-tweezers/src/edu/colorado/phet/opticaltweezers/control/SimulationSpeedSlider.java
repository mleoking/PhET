/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * SimulationSpeedSlider is a Piccolo-based slider for controlling simulation speed.
 * This control is very specific to the Optical Tweezers simulation, and is unlikely
 * to be reusable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimulationSpeedSlider extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Track
    private static final int TRACK_HEIGHT = 2;
    private static final Color TRACK_FILL_COLOR = Color.BLACK;
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final int TRACK_STROKE_WIDTH = 1;
    
    // Knob
    private static final int KNOB_WIDTH = 15;
    private static final int KNOB_HEIGHT = 40;
    private static final Color KNOB_FILL_COLOR = Color.WHITE;
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;
    private static final int KNOB_STROKE_WIDTH = 1;
    
    // Background
    private static final int BACKGROUND_HEIGHT = 50;
    private static final int SLOW_BACKGROUND_WIDTH = 160;
    private static final int BETWEEN_BACKGROUND_WIDTH = 80;
    private static final int FAST_BACKGROUND_WIDTH = 160;
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final int BACKGROUND_STROKE_WIDTH = 1;
    private static final Color SLOW_FILL_COLOR = Color.BLUE;
    private static final Color FAST_FILL_COLOR = Color.RED;
    private static final Color BETWEEN_FILL_COLOR = Color.DARK_GRAY;
    
    // Labels
    private static final int LABEL_FONT_SIZE = 14;
    private static final int LABEL_Y_SPACING = 5;
    private static final Color SLOW_LABEL_FONT_COLOR = SLOW_FILL_COLOR;
    private static final Color FAST_LABEL_FONT_COLOR = FAST_FILL_COLOR;
    
    // Tick marks
    private static final int TICK_MARK_LENGTH = 10;
    private static final Color TICK_MARK_COLOR = Color.BLACK;
    private static final int TICK_MARK_STROKE_WIDTH = 1;
    
    // Tick labels
    private static final int TICK_LABEL_FONT_SIZE = 14;
    private static final Color TICK_LABEL_COLOR = Color.BLACK;
    private static final DecimalFormat TICK_LABEL_FORMAT = new DecimalFormat( "0E00" );
    private static final int TICK_LABEL_Y_SPACING = 2;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DoubleRange _slowRange;
    private DoubleRange _fastRange;
    private double _value;
    
    private PPath _trackNode;
    private PPath _knobNode;
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors & initializers
    //----------------------------------------------------------------------------
    
    public SimulationSpeedSlider( DoubleRange slowRange, DoubleRange fastRange, double value ) {
        super();
        
        assert( slowRange.getMax() < fastRange.getMin() );
        assert( slowRange.contains( value ) || fastRange.contains( value ) );
        
        _slowRange = slowRange;
        _fastRange = fastRange;
        _value = value;
        
        _listenerList = new EventListenerList();
        
        initNodes();
    }
    
    private void initNodes() {
        
        String slowString = OTResources.getString( "label.slow" );
        String fastString = OTResources.getString( "label.fast" );
        
        PText slowTextNode = new PText( slowString );
        slowTextNode.setFont( new PhetDefaultFont( LABEL_FONT_SIZE ) );
        slowTextNode.setTextPaint( SLOW_LABEL_FONT_COLOR );
        
        PText fastTextNode = new PText( fastString );
        fastTextNode.setFont( new PhetDefaultFont( LABEL_FONT_SIZE ) );
        fastTextNode.setTextPaint( FAST_LABEL_FONT_COLOR );
        
        // Background sections
        PPath slowBackgroundNode = createBackgroundNode( SLOW_BACKGROUND_WIDTH, SLOW_FILL_COLOR );
        PPath fastBackgroundNode = createBackgroundNode( FAST_BACKGROUND_WIDTH, FAST_FILL_COLOR );
        PPath betweenBackgroundNode = createBackgroundNode( BETWEEN_BACKGROUND_WIDTH, BETWEEN_FILL_COLOR );
        
        _trackNode = createTrackNode();
        
        _knobNode = createKnobNode();
        
        // Tick marks
        PNode slowTickMarkMinNode = createTickMarkNode();
        PNode slowTickMarkMaxNode = createTickMarkNode();
        PNode fastTickMarkMinNode = createTickMarkNode();
        PNode fastTickMarkMaxNode = createTickMarkNode();
        
        // Tick labels
        PNode slowTickLabelMinNode = createTickLabelNode( _slowRange.getMin() );
        PNode slowTickLabelMaxNode = createTickLabelNode( _slowRange.getMax() );
        PNode fastTickLabelMinNode = createTickLabelNode( _fastRange.getMin() );
        PNode fastTickLabelMaxNode = createTickLabelNode( _fastRange.getMax() );
        
        // Positioning
        {
            double x = 0;
            double y = 0;
            slowBackgroundNode.setOffset( x, y );
            
            x = SLOW_BACKGROUND_WIDTH;
            y = slowBackgroundNode.getYOffset();
            betweenBackgroundNode.setOffset( x, y );
            
            x = SLOW_BACKGROUND_WIDTH + BETWEEN_BACKGROUND_WIDTH;
            y = betweenBackgroundNode.getYOffset();
            fastBackgroundNode.setOffset( x, y );
            
            x = slowBackgroundNode.getXOffset() + ( ( slowBackgroundNode.getFullBounds().getWidth() - slowTextNode.getFullBounds().getWidth() ) / 2 );
            y = -( slowTextNode.getFullBounds().getHeight() + LABEL_Y_SPACING );
            slowTextNode.setOffset( x, y );
            
            x = fastBackgroundNode.getXOffset() + ( ( fastBackgroundNode.getFullBounds().getWidth() - fastTextNode.getFullBounds().getWidth() ) / 2 );
            y = -( fastTextNode.getFullBounds().getHeight() + LABEL_Y_SPACING );
            fastTextNode.setOffset( x, y );
            
            x = slowBackgroundNode.getXOffset();
            y = slowBackgroundNode.getYOffset() + ( ( BACKGROUND_HEIGHT - _trackNode.getFullBounds().getHeight() ) / 2 );
            _trackNode.setOffset( x, y );
            
            x = slowBackgroundNode.getXOffset() - ( _knobNode.getFullBounds().getWidth() / 2 );
            y = slowBackgroundNode.getYOffset() + ( ( BACKGROUND_HEIGHT - _knobNode.getFullBounds().getHeight() ) / 2 );
            _knobNode.setOffset( x, y );
            
            x = slowBackgroundNode.getXOffset();
            y = slowBackgroundNode.getFullBounds().getMaxY();
            slowTickMarkMinNode.setOffset( x, y );
            
            x = slowBackgroundNode.getFullBounds().getMaxX();
            y = slowTickMarkMinNode.getYOffset() - slowTickMarkMaxNode.getFullBounds().getWidth();
            slowTickMarkMaxNode.setOffset( x, y );
            
            x = fastBackgroundNode.getXOffset();
            y = fastBackgroundNode.getFullBounds().getMaxY();
            fastTickMarkMinNode.setOffset( x, y );
            
            x = fastBackgroundNode.getFullBounds().getMaxX() - fastTickMarkMaxNode.getFullBounds().getWidth();
            y = fastTickMarkMinNode.getYOffset();
            fastTickMarkMaxNode.setOffset( x, y );
            
            x = slowTickMarkMinNode.getXOffset() - ( slowTickLabelMinNode.getFullBounds().getWidth() / 2 );
            y = slowTickMarkMinNode.getFullBounds().getMaxY() + TICK_LABEL_Y_SPACING;
            slowTickLabelMinNode.setOffset( x, y );
            
            x = slowTickMarkMaxNode.getXOffset() - ( slowTickLabelMaxNode.getFullBounds().getWidth() / 2 );
            y = slowTickMarkMaxNode.getFullBounds().getMaxY() + TICK_LABEL_Y_SPACING;
            slowTickLabelMaxNode.setOffset( x, y );
            
            x = fastTickMarkMinNode.getXOffset() - ( fastTickLabelMinNode.getFullBounds().getWidth() / 2 );
            y = fastTickMarkMinNode.getFullBounds().getMaxY() + TICK_LABEL_Y_SPACING;
            fastTickLabelMinNode.setOffset( x, y );
            
            x = fastTickMarkMaxNode.getXOffset() - ( fastTickLabelMaxNode.getFullBounds().getWidth() / 2 );
            y = fastTickMarkMaxNode.getFullBounds().getMaxY() + TICK_LABEL_Y_SPACING;
            fastTickLabelMaxNode.setOffset( x, y );
        }
        
        // Layering
        addChild( slowTickMarkMinNode );
        addChild( slowTickMarkMaxNode );
        addChild( fastTickMarkMinNode );
        addChild( fastTickMarkMaxNode );
        addChild( slowTickLabelMinNode );
        addChild( slowTickLabelMaxNode );
        addChild( fastTickLabelMinNode );
        addChild( fastTickLabelMaxNode );
        addChild( betweenBackgroundNode );
        addChild( slowBackgroundNode );
        addChild( fastBackgroundNode );
        addChild( slowTextNode );
        addChild( fastTextNode );
        addChild( _trackNode );
        addChild( _knobNode );
        
        // Pickability
        slowTextNode.setPickable( false );
        fastTextNode.setPickable( false );
        slowBackgroundNode.setPickable( false );
        fastBackgroundNode.setPickable( false );
        betweenBackgroundNode.setPickable( false );
        _trackNode.setPickable( true );
        _knobNode.setPickable( true );
        slowTickMarkMinNode.setPickable( false );
        slowTickMarkMaxNode.setPickable( false );
        fastTickMarkMinNode.setPickable( false );
        fastTickMarkMaxNode.setPickable( false );
        slowTickLabelMinNode.setPickable( false );
        slowTickLabelMaxNode.setPickable( false );
        fastTickLabelMinNode.setPickable( false );
        fastTickLabelMaxNode.setPickable( false );
    }
    
    private static PPath createBackgroundNode( int width, Paint fillPaint ) {
        int w = width - BACKGROUND_STROKE_WIDTH;
        int h = BACKGROUND_HEIGHT - BACKGROUND_STROKE_WIDTH;
        Rectangle path = new Rectangle( 0, 0, w, h );
        PPath node = new PPath( path);
        node.setPaint( fillPaint );
        node.setStroke( new BasicStroke( BACKGROUND_STROKE_WIDTH ) );
        node.setStrokePaint( BACKGROUND_STROKE_COLOR );
        return node;
    }
    
    private static PPath createTrackNode() {
        int width = ( SLOW_BACKGROUND_WIDTH + BETWEEN_BACKGROUND_WIDTH + FAST_BACKGROUND_WIDTH ) - TRACK_STROKE_WIDTH;
        int height = Math.max( 1, TRACK_HEIGHT - TRACK_STROKE_WIDTH );
        Rectangle trackPath = new Rectangle( 0, 0, width, height );
        PPath trackNode = new PPath( trackPath );
        trackNode.setPaint( TRACK_FILL_COLOR );
        trackNode.setStroke( new BasicStroke( TRACK_STROKE_WIDTH ) );
        trackNode.setStrokePaint( TRACK_STROKE_COLOR );
        return trackNode;
    }
    
    private static PPath createKnobNode() {
        float w = KNOB_WIDTH - KNOB_STROKE_WIDTH;
        float h = KNOB_HEIGHT - KNOB_STROKE_WIDTH;
        GeneralPath knobPath = new GeneralPath();
        knobPath.moveTo( 0f, 0f );
        knobPath.lineTo( w, 0f );
        knobPath.lineTo( w, 0.65f * h );
        knobPath.lineTo( w/2f, h );
        knobPath.lineTo( 0f, 0.65f * h );
        knobPath.closePath();
        
        PPath knob = new PPath( knobPath );
        knob.setPaint( KNOB_FILL_COLOR );
        knob.setStroke( new BasicStroke( KNOB_STROKE_WIDTH ) );
        knob.setStrokePaint( KNOB_STROKE_COLOR );
        return knob;
    }
    
    private static PPath createTickMarkNode() {
        Line2D tickPath = new Line2D.Double( 0, 0, 0, TICK_MARK_LENGTH );
        PPath tickNode = new PPath( tickPath );
        tickNode.setStroke( new BasicStroke( TICK_MARK_STROKE_WIDTH ) );
        tickNode.setStrokePaint( TICK_MARK_COLOR );
        return tickNode;
    }
    
    private static PText createTickLabelNode( double value ) {
        PText textNode = new PText();
        textNode.setText( TICK_LABEL_FORMAT.format( value ) );
        textNode.setFont( new PhetDefaultFont( TICK_LABEL_FONT_SIZE ) );
        textNode.setTextPaint( TICK_LABEL_COLOR );
        return textNode;
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setValue( double value ) {
        if ( !isValidValue( value ) ) {
            throw new IllegalArgumentException( "value out of range: " + value );
        }
        if ( value != _value ) {
            _value = value;
            fireChangeEvent( new ChangeEvent( this ) );
        }
    }
    
    public double getValue() { 
        return _value;
    }
    
    public String getFormattedValue() {
        return TICK_LABEL_FORMAT.format( _value );
    }
    
    private boolean isValidValue( double value ) {
        return ( _slowRange.contains( value ) || _fastRange.contains( value ) );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /**
     * Adds a ChangeListener.
     *
     * @param listener the listener
     */
    public void addChangeListener( ChangeListener listener ) {
        _listenerList.add( ChangeListener.class, listener );
    }

    /**
     * Removes a ChangeListener.
     *
     * @param listener the listener
     */
    public void removeChangeListener( ChangeListener listener ) {
        _listenerList.remove( ChangeListener.class, listener );
    }

    /**
     * Fires a ChangeEvent.
     *
     * @param event the event
     */
    private void fireChangeEvent( ChangeEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ChangeListener.class ) {
                ( (ChangeListener)listeners[i + 1] ).stateChanged( event );
            }
        }
    }
}
