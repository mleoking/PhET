// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicMappingStrategy;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * SimulationSpeedSlider is a Piccolo-based slider for controlling simulation speed.
 * The slider consists of 3 ranges, all of which are logarithmic.
 * To the left is the "slow" range, to the right is the "fast" range.
 * In between the slow and fast range is the "between" range.
 * If the user releases the knob in the "between" range, it automatically snaps
 * to slow.max or fast.min, whichever is closer.
 * <p>
 * This control is very specific to the Optical Tweezers simulation,
 * and is unlikely to be reusable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimulationSpeedSlider extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Background
    private static final int BACKGROUND_HEIGHT = 20;
    private static final int SLOW_BACKGROUND_WIDTH = 75;
    private static final int BETWEEN_BACKGROUND_WIDTH = 15;
    private static final int FAST_BACKGROUND_WIDTH = 75;
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final int BACKGROUND_STROKE_WIDTH = 1;
    private static final Color SLOW_FILL_COLOR = new Color( 180, 255, 180 ); // light green
    private static final Color FAST_FILL_COLOR = new Color( 50, 180, 50 ); // dark green
    private static final Color BETWEEN_FILL_COLOR = Color.DARK_GRAY;
    
    // Track
    private static final int TRACK_HEIGHT = 2;
    private static final Color TRACK_FILL_COLOR = Color.BLACK;
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final int TRACK_STROKE_WIDTH = 1;
    
    // Knob
    private static final int KNOB_WIDTH = 15;
    private static final int KNOB_HEIGHT = (int)( 0.8 * BACKGROUND_HEIGHT );
    private static final Color KNOB_FILL_COLOR = Color.WHITE;
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;
    private static final int KNOB_STROKE_WIDTH = 1;
    
    // Labels
    private static final int LABEL_FONT_SIZE = 12;
    private static final int LABEL_Y_SPACING = 5;
    private static final Color SLOW_LABEL_FONT_COLOR = Color.BLACK;
    private static final Color FAST_LABEL_FONT_COLOR = Color.BLACK;
    
    // Tick marks
    private static final int TICK_MARK_LENGTH = 10;
    private static final Color TICK_MARK_COLOR = Color.BLACK;
    private static final int TICK_MARK_STROKE_WIDTH = 1;
    
    // Tick labels
    private static final int TICK_LABEL_FONT_SIZE = 10;
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
    private boolean _adjusting;
    
    private LogarithmicMappingStrategy _slowMappingStrategy;
    private LogarithmicMappingStrategy _fastMappingStrategy;
    private LogarithmicMappingStrategy _betweenMappingStrategy;
    
    //----------------------------------------------------------------------------
    // Constructors & initializers
    //----------------------------------------------------------------------------
    
    public SimulationSpeedSlider( DoubleRange slowRange, DoubleRange fastRange, double value ) {
        super();
        
        if ( !( slowRange.getMax() < fastRange.getMin() ) ) {
            throw new IllegalArgumentException( "slowRange and fastRange overlap" );
        }
        if ( !( slowRange.contains( value ) || fastRange.contains( value ) ) ) {
            throw new IllegalArgumentException( "value out of range: " + value );
        }
        
        _slowRange = slowRange;
        _fastRange = fastRange;
        _value = value;
        
        _listenerList = new EventListenerList();
        _adjusting = false;
        
        // strategies for mapping between model and view coordinates
        _slowMappingStrategy = new LogarithmicMappingStrategy( _slowRange.getMin(), _slowRange.getMax(), 0, SLOW_BACKGROUND_WIDTH );
        _fastMappingStrategy = new LogarithmicMappingStrategy( _fastRange.getMin(), _fastRange.getMax(), 0, FAST_BACKGROUND_WIDTH );
        _betweenMappingStrategy = new LogarithmicMappingStrategy( _slowRange.getMax(), _fastRange.getMin(), 0, BETWEEN_BACKGROUND_WIDTH );
        
        initNodes();
        initInteractivity();
        
        updateKnobPosition();
    }
    
    /*
     * Creates and performs layout of all nodes.
     */
    private void initNodes() {
        
        String slowString = OTResources.getString( "label.slow" );
        String fastString = OTResources.getString( "label.fast" );
        
        PText slowTextNode = new PText( slowString );
        slowTextNode.setFont( new PhetFont( LABEL_FONT_SIZE ) );
        slowTextNode.setTextPaint( SLOW_LABEL_FONT_COLOR );
        
        PText fastTextNode = new PText( fastString );
        fastTextNode.setFont( new PhetFont( LABEL_FONT_SIZE ) );
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
            slowTickMarkMaxNode.setOffset( x, y );
            
            x = fastBackgroundNode.getXOffset();
            y = fastBackgroundNode.getFullBounds().getMaxY();
            fastTickMarkMinNode.setOffset( x, y );
            
            x = fastBackgroundNode.getFullBounds().getMaxX() - fastTickMarkMaxNode.getFullBounds().getWidth();
            fastTickMarkMaxNode.setOffset( x, y );
            
            // center aligned with tick mark
            x = slowTickMarkMinNode.getXOffset() - ( slowTickLabelMinNode.getFullBounds().getWidth() / 2 );
            y = slowTickMarkMinNode.getFullBounds().getMaxY() + TICK_LABEL_Y_SPACING;
            slowTickLabelMinNode.setOffset( x, y );
            
            // right aligned with tick mark
            x = slowTickMarkMaxNode.getXOffset() - slowTickLabelMaxNode.getFullBounds().getWidth();
            y = slowTickMarkMaxNode.getFullBounds().getMaxY() + TICK_LABEL_Y_SPACING;
            slowTickLabelMaxNode.setOffset( x, y );
            
            // left aligned with tick mark
            x = fastTickMarkMinNode.getXOffset();
            y = fastTickMarkMinNode.getFullBounds().getMaxY() + TICK_LABEL_Y_SPACING;
            fastTickLabelMinNode.setOffset( x, y );
            
            // center aligned with tick mark
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
    
    /*
     * Creates one of the background sections (slow, fast, in-between).
     */
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
    
    /*
     * Creates the slider track node.
     */
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
    
    /*
     * Creates the slider knob node.
     */
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
    
    /*
     * Create a tick mark node.
     */
    private static PPath createTickMarkNode() {
        Line2D tickPath = new Line2D.Double( 0, 0, 0, TICK_MARK_LENGTH );
        PPath tickNode = new PPath( tickPath );
        tickNode.setStroke( new BasicStroke( TICK_MARK_STROKE_WIDTH ) );
        tickNode.setStrokePaint( TICK_MARK_COLOR );
        return tickNode;
    }
    
    /*
     * Creates a tick label node.
     */
    private static PText createTickLabelNode( double value ) {
        PText textNode = new PText();
        textNode.setText( TICK_LABEL_FORMAT.format( value ) );
        textNode.setFont( new PhetFont( TICK_LABEL_FONT_SIZE ) );
        textNode.setTextPaint( TICK_LABEL_COLOR );
        return textNode;
    }
    
    /*
     * Adds interactivity to the knob.
     */
    private void initInteractivity() {
        
        // Constrain the knob to be dragged horizontally within the track
        double xMin = _trackNode.getFullBounds().getX() - ( _knobNode.getFullBounds().getWidth() / 2 );
        double xMax = _trackNode.getFullBounds().getMaxX() + ( _knobNode.getFullBounds().getWidth() / 2 );
        double yMin = _trackNode.getFullBounds().getY() + ( _trackNode.getFullBounds().getHeight() / 2 ) - ( _knobNode.getFullBounds().getHeight() / 2 );
        double yMax = _trackNode.getFullBounds().getY() + ( _trackNode.getFullBounds().getHeight() / 2 ) + ( _knobNode.getFullBounds().getHeight() / 2 );
        Rectangle2D dragBounds = new Rectangle2D.Double( xMin, yMin, xMax - xMin, yMax - yMin );
        PPath dragBoundsNode = new PPath( dragBounds );
        dragBoundsNode.setStroke( null );
        addChild( dragBoundsNode );
        BoundedDragHandler dragHandler = new BoundedDragHandler( _knobNode, dragBoundsNode );
        _knobNode.addInputEventListener( dragHandler );
        _knobNode.addInputEventListener( new CursorHandler() );
        
        // Update the value when the knob is moved.
        _knobNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PNode.PROPERTY_TRANSFORM ) && _adjusting ) {
                    updateValue();
                }
            }
        } );
        
        // Change the "adjusting" start on mouse press & release.
        _knobNode.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                _adjusting = true;
            }
            public void mouseReleased( PInputEvent event ) {
                _adjusting = false;
                snapToClosest();
                fireChangeEvent( new ChangeEvent( this ) );
            }
        } );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public DoubleRange getSlowRange() {
        return _slowRange;
    }
    
    public DoubleRange getFastRange() {
        return _fastRange;
    }
    
    public boolean isInBetween() {
        return ( _value > _slowRange.getMax() && _value < _fastRange.getMin() );
    }
    
    /**
     * Sets the slider value.
     * 
     * @param value the value, in model coordinates
     */
    public void setValue( double value ) {
        if ( value < _slowRange.getMin() || value > _fastRange.getMax() ) {
            throw new IllegalArgumentException( "value out of range: " + value );
        }
        if ( value != _value ) {
            _value = value;
            updateKnobPosition();
            snapToClosest();
            fireChangeEvent( new ChangeEvent( this ) );
        }
    }
    
    /**
     * Gets the slider value
     * 
     * @return the value, in model coordinates
     */
    public double getValue() { 
        return _value;
    }
    
    /**
     * Is the value currently being adjusted?
     * Returns true while the knob is being dragged. 
     * 
     * @return true or false
     */
    public boolean isAdjusting() {
        return _adjusting;
    }
    
    /*
     * Sets the slider's value in view coordinates.
     * This is x offset of the knob with respect to the track.
     */
    private void setSliderValue( int sliderValue ) {
        _knobNode.setOffset( sliderValue - ( _knobNode.getFullBounds().getWidth() / 2 ), _knobNode.getYOffset() );
    }
    
    /*
     * Gets the slider's value in view coordinates.
     * This is x offset of the knob with respect to the track.
     */
    private int getSliderValue() {
        // Read the knob offset in slider coordinates
        int xOffset = (int)( _knobNode.getXOffset() + ( _knobNode.getFullBounds().getWidth() / 2 ) );
        if ( xOffset < 0 ) {
            xOffset = 0;
        }
        else if ( xOffset > SLOW_BACKGROUND_WIDTH + BETWEEN_BACKGROUND_WIDTH + FAST_BACKGROUND_WIDTH ) {
            xOffset = SLOW_BACKGROUND_WIDTH + BETWEEN_BACKGROUND_WIDTH + FAST_BACKGROUND_WIDTH;
        }
        return xOffset;
    }
    
    /*
     * Is the slider knob in the "slow" range?
     */
    private boolean isKnobInSlowRange( int sliderValue ) {
        return ( sliderValue >= 0 && 
                ( sliderValue <= SLOW_BACKGROUND_WIDTH ) );
    }
    
    /*
     * Is the slider knob in the range between "slow" and "fast"?
     */
    private boolean isKnobInBetweenRange( int sliderValue ) {
        return ( ( sliderValue > SLOW_BACKGROUND_WIDTH ) && 
                ( sliderValue <= SLOW_BACKGROUND_WIDTH + BETWEEN_BACKGROUND_WIDTH ) );
    }
    
    /*
     * Is the slider knob in the "fast" range?
     */
    private boolean isKnobInFastRange( int sliderValue ) {
        return ( ( sliderValue > SLOW_BACKGROUND_WIDTH + BETWEEN_BACKGROUND_WIDTH ) && 
                ( sliderValue <= SLOW_BACKGROUND_WIDTH + BETWEEN_BACKGROUND_WIDTH + FAST_BACKGROUND_WIDTH ) );
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the model value to match the position of the knob.
     */
    private void updateValue() {
        
        // Read the knob offset in slider coordinates
        int sliderValue = getSliderValue();
        
        // Convert to a value in model coordinates
        if ( isKnobInSlowRange( sliderValue ) ) {
            _value = _slowMappingStrategy.sliderToModel( sliderValue );
        }
        else if ( isKnobInBetweenRange( sliderValue ) ) {
            _value = _betweenMappingStrategy.sliderToModel( sliderValue - SLOW_BACKGROUND_WIDTH );
        }
        else if ( isKnobInFastRange( sliderValue ) ) {
            _value = _fastMappingStrategy.sliderToModel( sliderValue - SLOW_BACKGROUND_WIDTH - BETWEEN_BACKGROUND_WIDTH );
        }
        else {
            throw new IllegalStateException( "knob is out of range, programming error?" );
        }
        
        // Notify listeners that the value has changed
        fireChangeEvent( new ChangeEvent( this ) );
    }
    
    /*
     * Updates the knob position to match the current model value.
     */
    private void updateKnobPosition() {
        int sliderValue = 0;
        if ( _value <= _slowRange.getMax() ) {
            sliderValue = _slowMappingStrategy.modelToSlider( _value );
        }
        else if ( _value < _fastRange.getMin() ) {
            sliderValue = SLOW_BACKGROUND_WIDTH + _betweenMappingStrategy.modelToSlider( _value );
        }
        else {
            sliderValue = SLOW_BACKGROUND_WIDTH + BETWEEN_BACKGROUND_WIDTH + _fastMappingStrategy.modelToSlider( _value );
        }
        setSliderValue( sliderValue );
    }
    
    /*
     * If the slider is in the range between "slow" and "fast",
     * snap it to the closest of slow.max or fast.min.
     */
    private void snapToClosest() {
        int sliderValue = getSliderValue();
        if ( isKnobInBetweenRange( sliderValue ) ) {
            if ( ( sliderValue - SLOW_BACKGROUND_WIDTH < ( BETWEEN_BACKGROUND_WIDTH / 2 ) ) ) {
                setValue( _slowRange.getMax() );
            }
            else {
                setValue( _fastRange.getMin() );
            }  
        }
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
