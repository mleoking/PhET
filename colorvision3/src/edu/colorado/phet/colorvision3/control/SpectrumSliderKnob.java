/* SpectrumSliderKnob.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.control;

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
 * @version $Id$
 */
public class SpectrumSliderKnob extends PhetShapeGraphic
{
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  // Location, upper-left corner of the bounding box.
  private Point _location;
  
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
    _location = new Point( 0, 0 );
    Shape shape = genShape( _location.x, _location.y );
    setShape( shape );
    setPaint( Color.red );
    setStroke( new BasicStroke( 1f ) );
    setBorderColor( Color.white );
  }
  
	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------

  /**
   * Sets the knob's location.
   * 
   * @param location the location
   */
  public void setLocation( Point location )
  { 
    _location = location;
    Shape shape = genShape( _location.x, _location.y );
    super.setShape( shape );
  }
  
  /**
   * Convenience method for setting the knob's location.
   * 
   * @param x X coordinate
   * @param y Y coordinate
   */
  public void setLocation( int x, int y )
  {
    this.setLocation( new Point( x, y ) );
  }
  
  /**
   * Gets the knob's location.
   */
  public Point getLocation()
  {
    return _location;
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