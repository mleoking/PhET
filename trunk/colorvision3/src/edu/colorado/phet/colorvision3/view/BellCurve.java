/* BellCurve.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

/**
 * BellCurve draws a bell curve shape.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$ $Name$
 */
public class BellCurve extends PhetShapeGraphic
{
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  private Point _location;
  
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Sole constructor
   * 
   * @param component the parent Component
   * @param w the width of the curve, in pixels
   * @param h the height of the curve, in pixels
   */
  public BellCurve( Component component, int w, int h )
  {
    super( component, null, null );
    
    //  Request antialiasing.
    RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    super.setRenderingHints( hints );

    // Default stroke attributes.
    super.setBorderColor( Color.BLACK );
    super.setStroke( new BasicStroke(1f) );
    
    // Create the path that describes the curve.
    GeneralPath path = new GeneralPath();
    path.moveTo( -.5f * w, 1f * h );
    path.curveTo( -.25f * w, 1f * h, -.25f * w, 0f * h, 0f * w, 0f * h  ); // left curve
    path.curveTo( .25f * w, 0f * h, .25f *w, 1f * h, .5f * w, 1f * h ); // right curve
    super.setShape( path );
    
    setLocation( 0, 0 );
  }
  
	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------

  /**
   * Sets the location.
   * 
   * @param location the location
   */
  public void setLocation( Point location )
  { 
    if ( _location != null )
    {
      super.translate( -_location.x, -_location.y );
    }
    _location = location;
    super.translate( location.x, location.y );
  }
  
  /**
   * Convenience method for setting the location.
   * 
   * @param x X coordinate
   * @param y Y coordinate
   */
  public void setLocation( int x, int y )
  {
    this.setLocation( new Point( x, y ) );
  }
  
  /**
   * Gets the location.
   */
  public Point getLocation()
  {
    return _location;
  }
  
}


/* end of file */