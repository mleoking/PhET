/* SpectrumSlider.java, Copyright 2004 University of Colorado */

package edu.colorado.phet.colorvision3.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.text.AttributedString;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.colorvision3.ColorVisionConfig;
import edu.colorado.phet.colorvision3.model.VisibleColor;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

/**
 * SpectrumSlider is a UI component, similar to a JSlider, for selecting a color
 * from the visible spectrum.  In addition to showing the currently selected color,
 * it optional depicts a transmission width. 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
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

  // The upper left corner of the spectrum graphic.
  private Point _location;
  // The spectrum graphic.
  private PhetImageGraphic _spectrum;
  // The slider knob.
  private SpectrumSliderKnob _knob; 
  // The bounds for dragging the slider knob.
  private Rectangle _dragBounds; 
  // Event listeners.
  private EventListenerList _listenerList; 
  // Minimum and maximum range, inclusive.
  private int _minimum, _maximum; 
  // Textual label, placed above the slider.
  private String _label;  
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
  
    // Listeners
    _listenerList = new EventListenerList();
    
    // Spectrum
    _spectrum = new PhetImageGraphic( parent, ColorVisionConfig.SPECTRUM_IMAGE );
   
    // Slider
    _knob = new SpectrumSliderKnob( parent );
 
    // Drag behavior
    _dragBounds = new Rectangle( 0, 0, 0, 0); // set correctly by setLocation
    this.setBoundedGraphic( _knob );
    this.addCursorHandBehavior();
    this.addTranslationBehavior( this );
    
    // Initial values.
    setMinimum( 0 );
    setMaximum( 100 );
    setLocation( 0, 0 );
    setValue( 0 );
    setTransmissionWidth( 0 );
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
    
    // Determine the slider's new X coordinate, based on the value.
    double fraction = (value - _minimum)/(double)(_maximum - _minimum);
    int x = (int) (fraction * _dragBounds.width) + _dragBounds.x;
    
    // Set the new slider location & color.
    _knob.setPosition( x, _knob.getPosition().y );
    
    // Force slider to update.
    translate( 0.0, 0.0 );
  }
  
  /**
   * Gets the slider value.
   * 
   * @return the value
   */
  public int getValue()
  {
    // Determine the value based on the slider location.
  	double fraction = (_knob.getPosition().x - _dragBounds.x )/(double)(_dragBounds.width);
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
    _label = label;
    
    // Pre-process for rendering.
    _attributedString = new AttributedString( _label );
    _attributedString.addAttribute( TextAttribute.FOREGROUND, color );
    _attributedString.addAttribute( TextAttribute.FONT, font );
  }
  
  /**
   * Gets the label.
   * 
   * @return the label
   */
  public String getLabel()
  {
    return _label;
  }
  
  /**
   * Sets the location.  
   * The location corresponds to the upper-left corner of the spectrum graphic.
   * 
   * @param location the location
   */
  public void setLocation( Point location )
  {
    _location = location;
    
    // Move the spectrum graphic.
    _spectrum.setPosition( _location.x, _location.y );
    
    // Move the slider.
    _knob.setPosition( _location.x - (_knob.getBounds().width/2), 
                         _location.y + _spectrum.getBounds().height );
    
    // Set drag bounds.
    _dragBounds = new Rectangle( _knob.getPosition().x, _knob.getPosition().y,
                                          _spectrum.getBounds().width, 0 );
    
    // Force slider to update.
    translate( 0.0, 0.0 );
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
   * 
   * @param width the width, in pixels
   */
  public void setTransmissionWidth( double width )
  {
    _transmissionWidth = width;
    translate( 0.0, 0.0 );
  }

  /**
   * Determines the bounds.
   * Note that this algorithm currently ignores the optional label,
   * since it has no bounds prior to being passed to the graphics 
   * context for rendering.
   * 
   * @return the bounds
   */
  protected Rectangle determineBounds()
  {
    // TODO: add the bounds for the label
    Rectangle r1 = _spectrum.getBounds();
    Rectangle r2 = _knob.getBounds();
    r1.add( r2 );
    return r1;
  }

	//----------------------------------------------------------------------------
	// Rendering
  //----------------------------------------------------------------------------

  /**
   * Renders the slider.
   * 
   * @param g2 the graphics context
   */
  public void paint( Graphics2D g2 )
  {
    if ( super.isVisible() )
    {
      // Save graphics state
      RenderingHints oldHints = g2.getRenderingHints();
   
      // Request antialiasing.
      RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
      g2.setRenderingHints( hints );

      // Render label.
      if ( _attributedString != null )
      {
        g2.drawString( _attributedString.getIterator(), 
            _location.x + LABEL_X_OFFSET, _location.y + LABEL_Y_OFFSET );
      }
      
      // Render spectrum graphic.
      _spectrum.paint( g2 );    
   
      // Render the transmission width curve.
      if ( _transmissionWidth > 0 )
      {
        renderBellCurve( g2, (int)_transmissionWidth, _spectrum.getBounds().height );
      }
  
      // Render slider knob.
      super.paint( g2 );
      
      // Restore graphics state
      g2.setRenderingHints( oldHints );
    }
  }
  
  /**
   * Renders the filter transmission width as a bell curve.
   * With unity scale values, the bounds of the rendered shape are 1x1 pixel.
   * 
   * @param g2 graphics context
   * @param sx X axis scale
   * @param sy Y axis scale
   */
  private void renderBellCurve( Graphics2D g2, int sx, int sy )
  { 
    // Save graohics state.
    Shape oldClip = g2.getClip();
    AffineTransform oldTransform = g2.getTransform();
    Paint oldPaint = g2.getPaint();
    
    // Clip to the spectrum graphic bounds.
    Rectangle r = _spectrum.getBounds();
   //XXX g2.setClip( r.x, r.y, r.width, r.height );
    
    // Move to slider location.
    g2.translate( _knob.getPosition().x + _knob.getBounds().width/2, _spectrum.getBounds().y );

    // Create the path that describes the curve.
    GeneralPath path = new GeneralPath();
    path.moveTo( -.5f * sx, 1f * sy );
    path.curveTo( -.25f * sx, 1f * sy, -.25f * sx, 0f * sy, 0f * sx, 0f * sy  ); // left curve
    path.curveTo( .25f * sx, 0f * sy, .25f *sx, 1f * sy, .5f * sx, 1f * sy ); // right curve
    
    // Draw the curve.
    g2.setPaint( Color.BLACK );
    g2.draw( path );
    
    // Restore graphic state.
    /*
     * WORKAROUND: 
     * Calling g2.setClip(oldClip) here causes the clipping to be messed up,
     * apparently a Java2D bug. This was observed on Macintosh, but not 
     * investigated on other platforms.  The workaround is to clear the
     * clipping area by calling g2.setClip(null).
     */
    //XXX g2.setClip( null );
    g2.setTransform( oldTransform );
    g2.setPaint( oldPaint );
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
    double x = Math.max( _dragBounds.x, Math.min( _dragBounds.x + _dragBounds.width, _knob.getPosition().x + dx ) );
    double y = Math.max( _dragBounds.y, Math.min( _dragBounds.y + _dragBounds.height, _knob.getPosition().y + dy ) );

    // Set the slider's location.
    _knob.setPosition( (int)x, (int)y );
      
    // Change the slider color.
    VisibleColor color = new VisibleColor( getValue() );
    _knob.setPaint( color );
    
    // Fire a ChangeEvent to notify listeners that the slider has moved.
    fireChangeEvent( new ChangeEvent(this) );
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