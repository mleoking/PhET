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
    
    private static final Dimension BACKGROUND_SIZE = new Dimension( 400, 50 );
    private static final double SLOW_BACKGROUND_PERCENT = 0.4;
    private static final double FAST_BACKGROUND_PERCENT = 0.4;
    private static final double TRACK_HEIGHT = 5;
    private static final Dimension KNOB_SIZE = new Dimension( 15, 40 );
    private static final double TICK_LENGTH = 10;
    
    private static final int FONT_SIZE = 14;
    private static final int SLOW_FAST_Y_SPACING = 5;
    private static final int TICK_LABEL_Y_SPACING = 2;
    
    private static final Color TRACK_FILL_COLOR = Color.LIGHT_GRAY;
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final Stroke TRACK_STROKE = new BasicStroke( 1f );
    
    private static final Color KNOB_FILL_COLOR = Color.WHITE;
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;
    private static final Stroke KNOB_STROKE = new BasicStroke( 1f );
    
    private static final Font SLOW_FONT = new PhetDefaultFont( FONT_SIZE );
    private static final Color SLOW_FONT_COLOR = Color.BLUE;
    private static final Color SLOW_FILL_COLOR = Color.BLUE;
    private static final Color SLOW_STROKE_COLOR = Color.BLACK;
    private static final Stroke SLOW_STROKE = new BasicStroke( 1f );
    
    private static final Font FAST_FONT = new PhetDefaultFont( FONT_SIZE );
    private static final Color FAST_FONT_COLOR = Color.RED;
    private static final Color FAST_FILL_COLOR = Color.RED;
    private static final Color FAST_STROKE_COLOR = Color.BLACK;
    private static final Stroke FAST_STROKE = new BasicStroke( 1f );
    
    private static final Color BETWEEN_FILL_COLOR = Color.DARK_GRAY;
    private static final Color BETWEEN_STROKE_COLOR = Color.BLACK;
    private static final Stroke BETWEEN_STROKE = new BasicStroke( 1f );
    
    private static final Color TICK_COLOR = Color.BLACK;
    private static final Stroke TICK_STROKE = new BasicStroke( 1f );
    private static final Font TICK_FONT = new PhetDefaultFont( FONT_SIZE );
    private static final DecimalFormat TICK_FORMAT = new DecimalFormat( "0E00" );

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
        slowTextNode.setFont( SLOW_FONT );
        slowTextNode.setTextPaint( SLOW_FONT_COLOR );
        
        PText fastTextNode = new PText( fastString );
        fastTextNode.setFont( FAST_FONT );
        fastTextNode.setTextPaint( FAST_FONT_COLOR );
        
        PPath slowBackgroundNode = new PPath();
        Rectangle2D slowBackgroundPath = new Rectangle2D.Double( 0, 0, BACKGROUND_SIZE.getWidth() * SLOW_BACKGROUND_PERCENT, BACKGROUND_SIZE.getHeight() );
        slowBackgroundNode.setPathTo( slowBackgroundPath );
        slowBackgroundNode.setPaint( SLOW_FILL_COLOR );
        slowBackgroundNode.setStroke( SLOW_STROKE );
        slowBackgroundNode.setStrokePaint( SLOW_STROKE_COLOR );
        
        PPath fastBackgroundNode = new PPath();
        Rectangle2D fastBackgroundPath = new Rectangle2D.Double( 0, 0, BACKGROUND_SIZE.getWidth() * FAST_BACKGROUND_PERCENT, BACKGROUND_SIZE.getHeight() );
        fastBackgroundNode.setPathTo( fastBackgroundPath );
        fastBackgroundNode.setPaint( FAST_FILL_COLOR );
        fastBackgroundNode.setStroke( FAST_STROKE );
        fastBackgroundNode.setStrokePaint( FAST_STROKE_COLOR );
        
        PPath betweenBackgroundNode = new PPath();
        Rectangle2D betweenBackgroundPath = new Rectangle2D.Double( 0, 0, BACKGROUND_SIZE.getWidth() * ( 1 - FAST_BACKGROUND_PERCENT - SLOW_BACKGROUND_PERCENT ), BACKGROUND_SIZE.getHeight() );
        betweenBackgroundNode.setPathTo( betweenBackgroundPath );
        betweenBackgroundNode.setPaint( BETWEEN_FILL_COLOR );
        betweenBackgroundNode.setStroke( BETWEEN_STROKE );
        betweenBackgroundNode.setStrokePaint( BETWEEN_STROKE_COLOR );
        
        _trackNode = new PPath();
        Rectangle2D trackBackgroundPath = new Rectangle2D.Double( 0, 0, BACKGROUND_SIZE.getWidth(), TRACK_HEIGHT );
        _trackNode.setPathTo( trackBackgroundPath );
        _trackNode.setPaint( TRACK_FILL_COLOR );
        _trackNode.setStroke( TRACK_STROKE );
        _trackNode.setStrokePaint( TRACK_STROKE_COLOR );
        
        _knobNode = createKnob();
        
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
        double x = 0;
        double y = 0;
        slowBackgroundNode.setOffset( x, y );
        x = slowBackgroundNode.getFullBounds().getMaxX();
        y = slowBackgroundNode.getYOffset();
        betweenBackgroundNode.setOffset( x, y );
        x = betweenBackgroundNode.getFullBounds().getMaxX();
        y = betweenBackgroundNode.getYOffset();
        fastBackgroundNode.setOffset( x, y );
        x = slowBackgroundNode.getXOffset() + ( ( slowBackgroundNode.getFullBounds().getWidth() - slowTextNode.getFullBounds().getWidth() ) / 2 );
        y = -( slowTextNode.getFullBounds().getHeight() + SLOW_FAST_Y_SPACING );
        slowTextNode.setOffset( x, y );
        x = fastBackgroundNode.getXOffset() + ( ( fastBackgroundNode.getFullBounds().getWidth() - fastTextNode.getFullBounds().getWidth() ) / 2 );
        y = -( fastTextNode.getFullBounds().getHeight() + SLOW_FAST_Y_SPACING );
        fastTextNode.setOffset( x, y );
        x = slowBackgroundNode.getXOffset();
        y = slowBackgroundNode.getYOffset() + ( ( BACKGROUND_SIZE.getHeight() - _trackNode.getFullBounds().getHeight() ) / 2 );
        _trackNode.setOffset( x, y );
        x = slowBackgroundNode.getXOffset() - ( _knobNode.getFullBounds().getWidth() / 2 );
        y = slowBackgroundNode.getYOffset() + ( ( BACKGROUND_SIZE.getHeight() - _knobNode.getFullBounds().getHeight() ) / 2 );
        _knobNode.setOffset( x, y );
        x = slowBackgroundNode.getXOffset();
        y = slowBackgroundNode.getFullBounds().getMaxY();
        slowTickMarkMinNode.setOffset( x, y );
        x = slowBackgroundNode.getFullBounds().getMaxX();
        y = slowTickMarkMinNode.getYOffset();
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
    
    private static PPath createKnob() {
        GeneralPath knobPath = new GeneralPath();
        knobPath.moveTo( 0f, 0f );
        knobPath.lineTo( (float) KNOB_SIZE.getWidth(), 0f );
        knobPath.lineTo( (float) KNOB_SIZE.getWidth(), (float) ( 0.65 * KNOB_SIZE.getHeight() ) );
        knobPath.lineTo( (float) ( KNOB_SIZE.getWidth() / 2 ), (float) KNOB_SIZE.getHeight() );
        knobPath.lineTo( 0f, (float) ( 0.65 * KNOB_SIZE.getHeight() ) );
        knobPath.closePath();
        
        PPath knob = new PPath();
        knob.setPathTo( knobPath );
        knob.setPaint( KNOB_FILL_COLOR );
        knob.setStroke( KNOB_STROKE );
        knob.setStrokePaint( KNOB_STROKE_COLOR );
        return knob;
    }
    
    private static PPath createTickMarkNode() {
        Line2D tickPath = new Line2D.Double( 0, 0, 0, TICK_LENGTH );
        PPath tickNode = new PPath();
        tickNode.setPathTo( tickPath );
        tickNode.setStroke( TICK_STROKE );
        tickNode.setStrokePaint( TICK_COLOR );
        return tickNode;
    }
    
    private static PText createTickLabelNode( double value ) {
        PText textNode = new PText();
        textNode.setText( TICK_FORMAT.format( value ) );
        textNode.setFont( TICK_FONT );
        textNode.setTextPaint( TICK_COLOR );
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
        return TICK_FORMAT.format( _value );
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
