/* IntensitySlider.java, Copyright 2004 University of Colorado */

package edu.colorado.phet.colorvision3.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * IntensitySlider is a slider used to control intensity.
 * Intensity is a percentage, with a range of 0-100 inclusive.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class IntensitySlider extends JPanel implements ChangeListener
{
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  private JPanel _containerPanel;
  private JSlider _slider;
  private JLabel _label;
  private Color _color;
  private EventListenerList _listenerList;
  
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Sole constructor.
   * 
   * @param spotlightModel the associated spotlight model
   * @param orientation orientation of the control (JSlider.HORIZONTAL or JSlider.VERTICAL)
   * @param size the dimensions of the control
   * @param color the color whose intensity is being controlled
   */
  public IntensitySlider( Color color, int orientation, Dimension size )
  {  
    _color = color;
    _listenerList = new EventListenerList();
    
    // Container panel, so we can put this component on the Apparatus panel.
    _containerPanel = new JPanel();
    _containerPanel.setBackground( color );
  
    // Slider
    _slider = new JSlider( );
    _slider.setOrientation( orientation );
    _slider.setMinimum( 0 );
    _slider.setMaximum( 100 );
    _slider.setValue( 0 );
    _slider.setPreferredSize( size );
    _slider.addChangeListener( this );
    
    // Layout
    this.add( _containerPanel );
    _containerPanel.add( _slider );
    
    // Make all components transparent so we can draw a custom background.
    this.setOpaque( false );
    _containerPanel.setOpaque( false );
    _slider.setOpaque( false );
      
    // If you don't do this, nothing is drawn.
    revalidate();
    repaint();
  }

	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------

  /** 
   * Sets the location and (as a side effect) the bounds for this component.
   * 
   * @param x the X coordinate
   * @param y the Y coordinate
   */
  public void setLocation( int x, int y )
  {
    super.setLocation( x, y );
    super.setBounds( x, y, super.getPreferredSize().width, super.getPreferredSize().height );
  }
  
  /**
   * Sets the slider value.
   * 
   * @param value the value
   */
  public void setValue( int value )
  {
    _slider.setValue( value );
  }
  
  /**
   * Gets the slider value.
   * 
   * @return the value
   */
  public int getValue()
  {
    return _slider.getValue();
  }
  
	//----------------------------------------------------------------------------
	// Event handling
  //----------------------------------------------------------------------------

  /**
   * Propogates a ChangeEvent, changes the source to this.
   * 
   * @param event the event
   */
  public void stateChanged( ChangeEvent event )
  {
    fireChangeEvent( new ChangeEvent(this) );
  }
  
  /**
   * Adds a ChangeListener.
   * 
   * @param listener the listener
   */
  public void addChangeListener( ChangeListener listener )
  {
    _listenerList.add( ChangeListener.class, listener );
  }
  
  /**
   * Removes a ChangeListener.
   * 
   * @param listener the listener
   */
  public void removeChangeListener( ChangeListener listener )
  {
    _listenerList.remove( ChangeListener.class, listener );
  }

  /**
   * Fires a ChangeEvent.
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
  
	//----------------------------------------------------------------------------
	// Rendering
  //----------------------------------------------------------------------------

  /**
   * Paints the component.
   * A gradient fill, based on the model color, is used for the background.
   * 
   * @param g the graphics context
   */
  public void paintComponent( Graphics g )
  {
    Graphics2D g2 = (Graphics2D)g;
    
    // Save any graphics state that we'll be touching.
    Paint oldPaint = g2.getPaint();
    Stroke oldStroke = g2.getStroke();
    
    // Use local variables to improve code readability.
    Component component = _containerPanel;
    int x = component.getX();
    int y = component.getY();
    int w = component.getWidth();
    int h = component.getHeight();
    
    // Create the background shape.
    Shape shape = new Rectangle2D.Double( x, y, w, h );
    
    // Create the gradient fill.
    Point2D p1, p2;
    if ( _slider.getOrientation() == JSlider.VERTICAL )
    {
      p1 = new Point2D.Double( x + (w/2), y );
      p2 = new Point2D.Double( x + (w/2), y + h );
    }
    else
    {
      p1 = new Point2D.Double( x + w, y + (h/2) );
      p2 = new Point2D.Double( x, y + (h/2) );
    }
    GradientPaint gradient = new GradientPaint( p1, _color, p2, Color.black );
    
    // Render the background.
    g2.setPaint( gradient );
    g2.fill( shape );
    g2.setStroke( new BasicStroke( 1f ) );
    g2.setPaint( Color.white );
    g2.draw( shape );
    
    // Restore the graphics state.
    g2.setPaint( oldPaint );
    g2.setStroke( oldStroke );
    
    // Render the component.
    super.paintComponent( g );
  } // paint

}

/* end of file */