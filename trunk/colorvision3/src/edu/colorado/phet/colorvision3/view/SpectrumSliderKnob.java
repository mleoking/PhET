/* SpectrumSliderKnob.java, Copyright 2004 University of Colorado */

package edu.colorado.phet.colorvision3.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

/**
 * SpectrumSliderKnob is the knob on a SpectrumSlider.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class SpectrumSliderKnob extends PhetShapeGraphic
{
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  // Position, upper-left corner of the bounding box.
  private Point _position;
  
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Sole constructor.
   * 
   * @param parent the parent Component
   */
  public SpectrumSliderKnob( Component parent )
  {    
    super( parent, null, null );
    
    // Initialize
    _position = new Point( 0, 0 );
    Shape shape = genShape( _position.x, _position.y );
    setShape( shape );
    setPaint( Color.red );
    setStroke( new BasicStroke( 1f ) );
    setBorderColor( Color.white );
  }
  
	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------

  /**
   * Sets the knob's position.
   * 
   * @param position the position
   */
  public void setPosition( Point position )
  { 
    _position = position;
    Shape shape = genShape( _position.x, _position.y );
    super.setShape( shape );
  }
  
  /**
   * Convenience method for setting the knob's position.
   * 
   * @param x X coordinate
   * @param y Y coordinate
   */
  public void setPosition( int x, int y )
  {
    this.setPosition( new Point( x, y ) );
  }
  
  /**
   * Gets the knob's position.
   */
  public Point getPosition()
  {
    return _position;
  }
  
	//----------------------------------------------------------------------------
	// Rendering
  //----------------------------------------------------------------------------

  /**
   * Generates the shape used to draw the slider knob.
   * The coordinate provided identify the upper-left corner
   * of the shape's bounds.
   * 
   * @param x X coordinate
   * @param y Y coordinate
   */
  private Shape genShape( int x, int y )
  {
    GeneralPath path = new GeneralPath();
    path.moveTo(  x + 10, y );
    path.lineTo(  x,      y + 10 );
    path.lineTo(  x,      y + 30 );
    path.lineTo(  x + 20, y + 30 );
    path.lineTo(  x + 20, y + 10 );
    path.closePath();
    return path;
  }

}


/* end of file */