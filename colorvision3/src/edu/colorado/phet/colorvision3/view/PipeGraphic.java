/* PipeGraphic.java, Copyright 2004 University of Colorado */

package edu.colorado.phet.colorvision3.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

/**
 * PipeGraphic is a graphic that represents a collection of "pipe" segments.
 * Each segment is either horizontal or vertical.  The pipes can be filled
 * with color if desired.
 *
 * @author cmalley
 * @revision $Id$
 */
public class PipeGraphic extends PhetShapeGraphic
{
	//----------------------------------------------------------------------------
	// Class data
  //----------------------------------------------------------------------------

  /** Horizontal orientation */
  public static final int HORIZONTAL = 0;
  /** Vertical orientation */
  public static final int VERTICAL = 1;
  
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  // Collection of pipe segements (array of Rectangle)
  private ArrayList _pipes;
  // Thickness of a pipe segment, in pixels.
  private int _thickness;
  // Arc on the pipe ends, in degrees.
  private int _arc;
  // Position of the shape, relative to upper-left bounding box.
  private Point _position;
  
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Sole constructor.
   * By default, the pipe is stroked and filled with Color.GRAY.
   * You can change the color by calling methods inherited from the superclass.
   * 
   * @param parent the parent Component
   */
  public PipeGraphic( Component parent )
  {
    super( parent, null, null );
    
    _pipes = new ArrayList();
    _thickness = 5;
    _arc = 10;
    _position = new Point(0,0);
    
    // Request antialiasing.
    RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    super.setRenderingHints( hints );
    
    super.setPaint( Color.GRAY );
    super.setStroke( new BasicStroke( 1f ) );
    super.setBorderColor( Color.GRAY );
  }
  
	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------

  /**
   * Sets the pipe thickness.
   * 
   * @param thickness the thickness, in pixels
   */
  public void setThickness( int thickness )
  {
    _thickness = thickness;
  }
  
  /**
   * Gets the pipe thickness
   * 
   * @return the thickness, in pixels
   */
  public int getThickness()
  {
    return _thickness;
  }
  
  /**
   * Sets the arc on the ends of the pipe segments. 
   * An arc of zero will result in square ends.
   * 
   * @param arc the arc, in degress
   */
  public void setArc( int arc )
  {
    _arc = arc;
  }
  
  /**
   * Gets the arc used on the ends on the pipe segments.
   * @return
   */
  public int getArc()
  {
    return _arc;
  }
  
  /**
   * Sets the position, relative to the upper-left corner of the bounding box
   * that encloses all of the pipe segements.
   * 
   * @param position the position
   */
  public void setPosition( Point position )
  {
    int dx = position.x - _position.x;
    int dy = position.y - _position.y;
    
    _position = position;
    
    RoundRectangle2D.Double r;
    for ( int i = 0; i < _pipes.size(); i ++ )
    {
      r = (RoundRectangle2D.Double)_pipes.get(i);
      r.x += dx;
      r.y += dy;
    }
    
    updateShape();
  }
   
  /**
   * Convenience method for setting position.
   * 
   * @param x X coordinate
   * @param y Y coordinate
   */
  public void setPosition( int x, int y )
  {
    this.setPosition( new Point( x, y ) );
  }
  
  /**
   * Gets the position
   * 
   * @return the position
   */
  public Point getPosition()
  {
    return _position;
  }
  
	//----------------------------------------------------------------------------
	// Pipe assembly
  //----------------------------------------------------------------------------

  /**
   * Adds a pipe segment.
   * 
   * @param orientation the orientation, HORIZONTAL or VERTICAL
   * @param x X coordinate
   * @param y Y coordinate
   * @param length length of the pipe segment
   * @throws IllegalArgumentException if orientation is invalid
   */
  public void addSegment( int orientation, int x, int y, int length )
  {
     RoundRectangle2D.Double r;
     if ( orientation == HORIZONTAL )
     {
       r = new RoundRectangle2D.Double( x + _position.x, y + _position.y, length, _thickness, _arc, _arc );
     }
     else if ( orientation == VERTICAL )
     {
       r = new RoundRectangle2D.Double( x + _position.x, y + _position.y, _thickness, length, _arc, _arc );
     }
     else
     {
       throw new IllegalArgumentException( "invalid orientation: " + orientation );
     }
     _pipes.add( r );
     updateShape();
  }
  
  /**
   * Clears all pipe segments.
   */
  public void clearSegements( )
  {
    _pipes = new ArrayList();
    updateShape();
  }

  /**
   * Updates the Shape that represents the collection of pipe segments.
   */
  private void updateShape()
  {  
    Area area = new Area();
    RoundRectangle2D.Double r;
    for ( int i = 0; i < _pipes.size(); i ++ )
    {
      r = (RoundRectangle2D.Double)_pipes.get(i);
      area.add( new Area(r) );
    }
    super.setShape( area );
  }

}


/* end of file */