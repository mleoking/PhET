/* IntensityControl.java, Copyright 2004 University of Colorado */

package edu.colorado.phet.colorvision3.view;

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

import edu.colorado.phet.colorvision3.model.Spotlight;

/**
 * IntensityControl is the user interface component used to
 * control a spotlight's intensity.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class IntensityControl extends JPanel implements ChangeListener
{
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  private Spotlight _spotlightModel;
  private JPanel _containerPanel;
  private JSlider _slider;
  private JLabel _label;
  
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Sole constructor.
   * 
   * @param spotlightModel the associated spotlight model
   * @param orientation orientation of the control (JSlider.HORIZONTAL or JSlider.VERTICAL)
   */
  public IntensityControl( Spotlight spotlightModel, int orientation, Dimension size )
  {
    _spotlightModel = spotlightModel;
     
    // Container panel, so we can put this component on the Apparatus panel.
    _containerPanel = new JPanel();
    _containerPanel.setBackground( _spotlightModel.getColor() );
  
    // Slider
    _slider = new JSlider( );
    _slider.setOrientation( orientation );
    _slider.setMinimum( 0 );
    _slider.setMaximum( 100 );
    _slider.setValue( 0 );
    _slider.setPreferredSize( size );

    // Layout
    this.add( _containerPanel );
    _containerPanel.add( _slider );
    
    // Listener for slider changes.
    _slider.addChangeListener( this );
    
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
  
	//----------------------------------------------------------------------------
	// Event handling
  //----------------------------------------------------------------------------

  /**
   * Handles ChangeEvents that occur when the slider is moved.
   * The spotlight model is notified of the intensity change.
   * 
   * @param event the event
   */
  public void stateChanged( ChangeEvent event )
  {
    if ( event.getSource() == _slider )
    {
      _spotlightModel.setIntensity( _slider.getValue() );
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
    GradientPaint gradient = new GradientPaint( p1, _spotlightModel.getColor(), p2, Color.black );
    
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