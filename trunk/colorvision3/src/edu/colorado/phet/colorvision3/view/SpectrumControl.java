/* SpectrumControl.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.colorvision3.ColorVisionConfig;
import edu.colorado.phet.colorvision3.util.ColorUtil;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

/**
 * SpectrumControl is a UI component for selecting a color from the visible spectrum.
 * It is implemented in the spirit of JSlider.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class SpectrumControl extends DefaultInteractiveGraphic implements Translatable
{
  private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 18 );
  private static final Color LABEL_COLOR = Color.white;
  private static final int LABEL_X_OFFSET = 0;
  private static final int LABEL_Y_OFFSET = -15;
  
  private Point _location;
  private PhetImageGraphic _spectrum;
  private SpectrumSlider _slider;
  private Rectangle _dragBounds;
  private EventListenerList _listenerList;
  private int _minimum, _maximum;
  private String _label;
  private AttributedString _attributedString;
  
  public SpectrumControl( Component parent )
  {
    super( null );
  
    // Listeners
    _listenerList = new EventListenerList();
    
    // Spectrum
    _spectrum = new PhetImageGraphic( parent, ColorVisionConfig.SPECTRUM_IMAGE );
   
    // Slider
    _slider = new SpectrumSlider( parent );
 
    // Drag behavior
    _dragBounds = new Rectangle( 0, 0, 0, 0); // set correctly by setLocation
    this.setBoundedGraphic( _slider );
    this.addCursorHandBehavior();
    this.addTranslationBehavior( this );
    
    // Initial values.
    setMinimum( 0 );
    setMaximum( 100 );
    setLocation( 0, 0 );
    setValue( 0 );
  }
  
  /**
   * Sets the slider value.
   *
   * @param value the value, silently clamped to the allowed range
   */
  public void setValue( int value )
  {
    // Silently clamp the value to the allowed range.
    value = (int) MathUtil.clamp( _minimum, value, _maximum );
    
    // Determine the slider's new X coordinate, based on the value.
    double fraction = (value - _minimum)/(double)(_maximum - _minimum);
    int x = (int) (fraction * _dragBounds.width) + _dragBounds.x;
    
    // Set the new slider location & color.
    _slider.setPosition( x, _slider.getPosition().y );
    
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
  	double fraction = (_slider.getPosition().x - _dragBounds.x )/(double)(_dragBounds.width);
  	int value = (int) (fraction * (_maximum - _minimum)) + _minimum;
    return value;
  }
  
  /**
   * Gets the slider value as a Color.
   * 
   * @return the color represented by the current slider value
   */
  public Color getColor()
  {
    int wavelength = this.getValue();
    Color color = ColorUtil.wavelengthToColor( wavelength );
    return color;
  }
  
  public void setMinimum( int minimum )
  {
    _minimum = minimum;
  }
  
  public int getMinimum()
  {
    return _minimum;
  }
  
  public void setMaximum( int maximum )
  {
    _maximum = maximum;
  }
  
  public int getMaximum()
  {
    return _maximum;
  }
  
  public void setLabel( String label )
  {
    _label = label;
    
    // Pre-process for rendering.
    _attributedString = new AttributedString( _label );
    _attributedString.addAttribute( TextAttribute.FOREGROUND, LABEL_COLOR );
    _attributedString.addAttribute( TextAttribute.FONT, LABEL_FONT );
  }

  public String getLabel()
  {
    return _label;
  }
  
  public void setLocation( int x, int y )
  {
    this.setLocation( new Point(x,y) );
  }
  
  public void setLocation( Point location )
  {
    _location = location;
    
    // Move the spectrum graphic.
    _spectrum.setPosition( _location.x, _location.y );
    
    // Move the slider.
    _slider.setPosition( _location.x - (_slider.getBounds().width/2), 
                         _location.y + _spectrum.getBounds().height );
    
    // Set drag bounds.
    _dragBounds = new Rectangle( _slider.getPosition().x, _slider.getPosition().y,
                                          _spectrum.getBounds().width, 0 );
    
    // Force slider to update.
    translate( 0.0, 0.0 );
  }
  
  public Point getLocation()
  {
    return _location;
  }

  /*
   * Determines the bounds.
   */
  protected Rectangle determineBounds()
  {
    return null; // XXX is this OK?
  }

  /*
   * Renders this control.
   */
  public void paint( Graphics2D g2 )
  {
    if ( super.isVisible() )
    {
      // Save state
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
      
      // Render spectrum.
      _spectrum.paint( g2 );    
      
      // Render slider.
      super.paint( g2 );
      
      // Restore state
      g2.setRenderingHints( oldHints );
    }
  }
  
  /**
   * Implements the Translatable interface, which handles drag operations.
   * The slider is constrained to its drag bounds, and the slider color is
   * changed as the slider is dragged.  Registered listeners are notified.
   */
  public void translate( double dx, double dy )
  { 
    // Constrain the drag boundaries of the slider.
    double x = Math.max( _dragBounds.x, Math.min( _dragBounds.x + _dragBounds.width, _slider.getPosition().x + dx ) );
    double y = Math.max( _dragBounds.y, Math.min( _dragBounds.y + _dragBounds.height, _slider.getPosition().y + dy ) );

    // Set the slider's location.
    _slider.setPosition( (int)x, (int)y );
      
    // Change the slider color.
    _slider.setPaint( getColor() );
      
    // Fire a ChangeEvent to notify listeners that the slider has moved.
    fireChangeEvent( new ChangeEvent(this) );
  }
  
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