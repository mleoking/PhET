/* ThoughtBubbleGraphic.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

/**
 * ThoughtBubbleGraphic displays a "thought bubble", used to show perceived color.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class ThoughtBubbleGraphic extends PhetShapeGraphic
{
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------
  
  // Location, relative to upper-left corner of bounding box.
  private Point _location;

	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Sole constructor.
   * 
   * @param parent the parent Component
   */
  public ThoughtBubbleGraphic( Component parent )
  {
    super( parent, null, null );

    // Outline
    super.setBorderColor( Color.WHITE );
    super.setStroke( new BasicStroke(1f) );
    
    // Fill color
    super.setPaint( new Color(0,0,0,0) );
    
    // Use constructive area geometry to describe the "thought bubble" shape.
    // The "thought bubble" is an Area, composed by adding a set of ellipses.
    // Origin is the upper left corner of the area's bounding box.
    Area area = new Area();
    {
      // Bulges on left end, top to bottom.
      area.add( new Area( new Ellipse2D.Double(  25,  10, 100,  50 ) ) );
      area.add( new Area( new Ellipse2D.Double(   0,  35, 100,  50 ) ) );
      area.add( new Area( new Ellipse2D.Double(  15,  75, 100,  30 ) ) );
      // Bulges on right end, top to bottom.
      area.add( new Area( new Ellipse2D.Double( 125,  10, 100,  50 ) ) );
      area.add( new Area( new Ellipse2D.Double( 150,  25, 100,  50 ) ) );
      area.add( new Area( new Ellipse2D.Double( 125,  50, 100,  50 ) ) );
      // Bulge in top center.
      area.add( new Area( new Ellipse2D.Double(  50,   0, 150, 100 ) ) );
      // Bulge in bottom center.
      area.add( new Area( new Ellipse2D.Double(  60,  65, 100,  50 ) ) );
      // 3 blips, top to bottom.
      area.add( new Area( new Ellipse2D.Double( 150, 115,  40,  20 )) );
      area.add( new Area( new Ellipse2D.Double( 165, 145,  30,  15 )) );
      area.add( new Area( new Ellipse2D.Double( 175, 170,  20,  10 )) );
    }
    super.setShape( area );
    
    // Request antialiasing.
    RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    super.setRenderingHints( hints );
  }

	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------

  /**
   * Gets the position.
   * 
   * @return the position
   */
  public Point getLocation()
  {
    return _location;
  }

  /** 
   * Sets the location, relative to the upper left corner of the 
   * area's bounding rectangle.
   * 
   * @param location the position
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
   * Convenience method for setting location.
   * 
   * @param x the X coordinate
   * @param y the Y coordinate
   */
  public void setLocation( int x, int y )
  {
    setLocation( new Point(x,y) );
  }
  
	//----------------------------------------------------------------------------
	// Rendering
  //----------------------------------------------------------------------------

  /*
   * Renders the thought bubble.
   * 
   * @param g2 the grpahics context
   */
  public void paint( Graphics2D g2 )
  {
    if ( super.isVisible() )
    {
      super.paint( g2 );
      BoundsOutline.paint( g2, this ); // DEBUG
    }
  }

}


/* end of file */