/* SpectrumSlider.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.colorvision3.ColorVisionConfig;
import edu.colorado.phet.colorvision3.view.BellCurve;
import edu.colorado.phet.colorvision3.view.BoundsOutline;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.VisibleColor;

/**
 * SpectrumSlider is a UI component, similar to a JSlider, for selecting a color
 * from the visible spectrum.  In addition to showing the currently selected color,
 * it optional depicts a transmission width. 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class SpectrumSlider extends DefaultInteractiveGraphic implements Translatable
{
	//----------------------------------------------------------------------------
	// Class data
  //----------------------------------------------------------------------------

  // Default label font
  private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 18 );
  // Default label color
  private static final Color LABEL_COLOR = Color.white;
  // Label X offset, relative to upper-left of spectrum graphic.
  private static final int LABEL_X_OFFSET = 0;
  // Label Y offset, relative to upper-left of spectrum graphic.
  private static final int LABEL_Y_OFFSET = -15;
 
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  // The parent Component.
  private Component _parent;
  // The upper left corner of the spectrum graphic.
  private Point _location;
  // The spectrum graphic.
  private PhetImageGraphic _spectrum;
  // The slider knob.
  private SpectrumSliderKnob _knob; 
  // Curve for displaying transmission width.
  private BellCurve _curve;
  //The bounds for dragging the slider knob.
  private Rectangle _dragBounds; 
  // Event listeners.
  private EventListenerList _listenerList; 
  // Minimum and maximum range, inclusive.
  private int _minimum, _maximum; 
  // Textual label, placed above the slider.
  private String _labelString;
  // Font used for the label.
  private Dimension _labelDimension;
  // Textual label, in a form optimized for rendering.
  private AttributedString _attributedString; 
  // Transmission width, in pixels.
  private double _transmissionWidth;
  
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Sole constructor.
   * 
   * @param parent parent Component
   */
  public SpectrumSlider( Component parent )
  {
    super( null );
  
    _parent = parent;
    
    // Listeners
    _listenerList = new EventListenerList();
    
    // Spectrum
    _spectrum = new PhetImageGraphic( parent, ColorVisionConfig.SPECTRUM_IMAGE );
   
    // Slider
    _knob = new SpectrumSliderKnob( parent );
 
    // Drag behavior
    _dragBounds = new Rectangle( 0, 0, 0, 0); // set correctly by setLocation
    super.setBoundedGraphic( _knob );
    super.addCursorHandBehavior();
    super.addTranslationBehavior( this );

    // Initial values.
    setMinimum( 0 );
    setMaximum( 100 );
    setLocation( 0, 0 );
    setValue( 0 );
    setTransmissionWidth( 0 );
    
    translate(0,0);
  }
  
	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------

  /**
   * Sets the slider value.
   *
   * @param value the value, silently clamped to the slider's range
   */
  public void setValue( int value )
  {
    // Silently clamp the value to the allowed range.
    value = (int) MathUtil.clamp( _minimum, value, _maximum );
    
    // Set the knob's location & color.
    {
      double fraction = (value - _minimum)/(double)(_maximum - _minimum);
      int x = (int) (fraction * _dragBounds.width) + _dragBounds.x;
      _knob.setLocation( x, _knob.getPosition().y );
      
      VisibleColor color = new VisibleColor( getValue() );
      _knob.setPaint( color.toColor() );
    }
    
    // Fire a ChangeEvent to notify listeners that the value has changed.
    fireChangeEvent( new ChangeEvent(this) );
    
    repaint();
  }
  
  /**
   * Gets the slider value.
   * 
   * @return the value
   */
  public int getValue()
  {
    // Determine the value based on the knob location.
    return getValue( _knob.getPosition().x, _knob.getPosition().y );
  }
 
  /**
   * Gets the slider value based on an (x,y) location.
   * 
   * @return the value
   */
  private int getValue( int x, int y )
  {
    // Determine the value based on the slider location.
  	double fraction = (x - _dragBounds.x )/(double)(_dragBounds.width);
  	int value = (int) (fraction * (_maximum - _minimum)) + _minimum;
    return value;
  }
  
  /**
   * Sets the minimum value of the slider's range.
   * 
   * @param minimum the minimum
   */
  public void setMinimum( int minimum )
  {
    _minimum = minimum;
  }
  
  /**
   * Gets the minimum value of the slider's range.
   * 
   * @return the minimum
   */
  public int getMinimum()
  {
    return _minimum;
  }
  
  /**
   * Sets the maximum value of the slider's range.
   * 
   * @param maximum the maximum
   */
  public void setMaximum( int maximum )
  {
    _maximum = maximum;
  }
  
  /** 
   * Gets the maximum value of the slider's range.
   * 
   * @return the maximum
   */
  public int getMaximum()
  {
    return _maximum;
  }
  
  /**
   * Sets the label, using a default color and font.
   * The label is positioned above the spectrum graphic.
   * If more precise control over the location of the label is required,
   * create a label separately.
   * 
   * @param label the label
   */
  public void setLabel( String label )
  {
    setLabel( label, LABEL_COLOR, LABEL_FONT );
  }

  /** 
   * Sets the label, using a specified color and font.
   * 
   * @param label the label
   * @param color the color
   * @param font the font
   */
  public void setLabel( String label, Color color, Font font )
  {
    _labelString = label;
    _attributedString = null;
    _labelDimension = null;
    
    if ( _labelString != null )
    {
      // Pre-process the label String for rendering.
      _attributedString = new AttributedString( _labelString );
      _attributedString.addAttribute( TextAttribute.FOREGROUND, color );
      _attributedString.addAttribute( TextAttribute.FONT, font );
      
      // Determine the label dimensions for bounds calculations.
      FontMetrics fontMetrics = _parent.getFontMetrics( font );
      int height = fontMetrics.getHeight();
      int width = fontMetrics.stringWidth( label );
      _labelDimension = new Dimension( width, height );
    }
    
    repaint();
  }
  
  /**
   * Gets the label.
   * 
   * @return the label
   */
  public String getLabel()
  {
    return _labelString;
  }
  
  /**
   * Sets the location.  
   * The location corresponds to the upper-left corner of the spectrum graphic.
   * 
   * @param location the location
   */
  public void setLocation( Point location )
  {
    int value = getValue();
    
    _location = location;
    
    // Move the spectrum graphic.
    _spectrum.setPosition( _location.x, _location.y );

    // Move the slider, positioning it at the minimum value.
    _knob.setLocation( _location.x - (_knob.getBounds().width/2), 
                         _location.y + _spectrum.getBounds().height );
    
    // Set drag bounds.
    _dragBounds = new Rectangle( _knob.getPosition().x, _knob.getPosition().y,
                                          _spectrum.getBounds().width, 0 );
    
    // Restore the value.
    setValue( value ); // calls repaint
  }
  
  /**
   * Convenience method for setting location.
   * 
   * @param x X coordinate
   * @param y Y coordinate
   */
  public void setLocation( int x, int y )
  {
    this.setLocation( new Point(x,y) );
  }
  
  /** 
   * Gets the location.
   * The location corresponds to the upper-left corner of the spectrum graphic.
   * 
   * @return the location
   */
  public Point getLocation()
  {
    return _location;
  }
  
  /**
   * Sets the transmission width.
   * Setting the width to zero effectively disables drawing of the curve.
   * 
   * @param width the width, in pixels
   */
  public void setTransmissionWidth( double width )
  {
    _transmissionWidth = width;
    _curve = null;
    if ( width > 0 )
    {
      _curve = new BellCurve( _parent, (int)width, _spectrum.getBounds().height );
    }
    repaint();
  }

  /**
   * Gets the transmission width.
   *
   * @return width in pixels
   */
  public double getTransmissionWidth()
  {
    return _transmissionWidth;
  }
  
  /**
   * Gets the bounds.
   * 
   * @return the bounds
   */
  protected Rectangle getBounds()
  {   
    // Start with the spectrum graphic's bounds.
    // Make a copy, so we don't accidentally change the graphic's bounds.
    Rectangle bounds = new Rectangle( _spectrum.getBounds() );
    
    // Add the knob's bounds.
    bounds.add( _knob.getBounds() );
    
    // If a label has been set, add it's bounding box.
    if ( _labelDimension != null )
    {
      Rectangle r = new Rectangle(
                         _location.x + LABEL_X_OFFSET, 
                         _location.y + LABEL_Y_OFFSET - _labelDimension.height,
                         _labelDimension.width, 
                         _labelDimension.height );
      bounds.add( r );
    }
    
    return bounds;
  }

  /*
   * Overrides superclass implementation.
   */
  public void setVisible( boolean visible )
  {
    if ( visible != super.isVisible() )
    {
      super.setVisible( visible );
      repaint();
    }
  }
  
	//----------------------------------------------------------------------------
	// Rendering
  //----------------------------------------------------------------------------
  
  /**
   * Repaints the slider.
   */
  public void repaint()
  {
    Rectangle r = getBounds();
    _parent.repaint( r.x, r.y, r.width, r.height );
  }
  
  /**
   * Renders the slider.
   * 
   * @param g2 the graphics context
   */
  public void paint( Graphics2D g2 )
  {
    if ( super.isVisible() )
    {
      // Draw the spectrum graphic.
      _spectrum.paint( g2 );   

      // Draw the slider knob.
      super.paint( g2 );
      
      // Draw the optional transmission width curve.
      if ( _curve != null )
      { 
        // Set its location to line up with the knob.
        // HACK: If this is done in setValue method, anti-aliasing is inconsistent.
        int x = _knob.getPosition().x + _knob.getBounds().width/2;
        int y = _spectrum.getBounds().y;
        _curve.setLocation( x, y );
        
        // Save graphics state.
        Shape oldClip = g2.getClip();

        // Draw the curve, clipped to the spectrum graphic's bounds.
        g2.setClip( new Rectangle(_spectrum.getBounds()) );
        _curve.paint( g2 );
        
        // Restore graphics state.
        g2.setClip( oldClip );
      }
      
      // Draw the optional label.
      if ( _attributedString != null )
      {
        // Save graphics state
        RenderingHints oldHints = g2.getRenderingHints();
        
        // Request antialiasing.
        RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHints( hints );
        g2.drawString( _attributedString.getIterator(), 
            _location.x + LABEL_X_OFFSET, _location.y + LABEL_Y_OFFSET );
        
        // Restore graphics state
        g2.setRenderingHints( oldHints );
      }
      
      BoundsOutline.paint( g2, getBounds(), Color.GREEN, new BasicStroke(1f) ); // DEBUG
    }
  }
  
	//----------------------------------------------------------------------------
	// Drag control
  //----------------------------------------------------------------------------

  /**
   * Implements the Translatable interface, which handles drag operations.
   * The slider is constrained to its drag bounds, and the slider color is
   * changed as the slider is dragged.  Registered listeners are notified.
   * 
   * @param dx X delta
   * @param dy Y delta
   */
  public void translate( double dx, double dy )
  { 
    // Constrain the drag boundaries of the slider.
    int x = (int) Math.max( _dragBounds.x, Math.min( _dragBounds.x + _dragBounds.width, _knob.getPosition().x + dx ) );
    int y = (int) Math.max( _dragBounds.y, Math.min( _dragBounds.y + _dragBounds.height, _knob.getPosition().y + dy ) );

    int value = getValue( x, y );
    setValue( value );
  }
  
	//----------------------------------------------------------------------------
	// Event handling
  //----------------------------------------------------------------------------

  /**
   * Adds a ChangeListener, ala JSlider.
   * 
   * @param listener the listener to add
   */
  public void addChangeListener( ChangeListener listener )
  {
    _listenerList.add( ChangeListener.class, listener );
  }
  
  /**
   * Removes a ChangeListener, ala JSlider.
   * 
   * @param listener the listener to remove
   */
  public void removeChangeListener( ChangeListener listener )
  {
    _listenerList.remove( ChangeListener.class, listener );
  }
  
  /**
   * Fires a ChangeEvent, ala JSlider.
   * This occurs each time the slider is moved.
   * 
   * @param event the event
   */
  private void fireChangeEvent( ChangeEvent event )
  {
    Object[] listeners = _listenerList.getListenerList();
    for ( int i = 0; i < listeners.length; i+=2 )
    {
      if ( listeners[i] == ChangeListener.class )
      {
        ((ChangeListener)listeners[i+1]).stateChanged( event );
      }
    }
  }
  
}


/* end of file */