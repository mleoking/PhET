/* PipeGraphic.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

/**
 * PipeGraphic
 *
 * @author cmalley
 * @revision $Id$
 */
public class PipeGraphic extends PhetShapeGraphic
{
  public static final int HORIZONTAL = 0;
  public static final int VERTICAL = 1;
  
  private ArrayList _pipes; // array of Rectangle
  private int _thickness;
  private int _arc;
  private Point _position;
  
  /**
   * Sole constructor.
   */
  public PipeGraphic( Component parent )
  {
    super( parent, null, null );
    
    _pipes = new ArrayList();
    _thickness = 5;
    _arc = 10;
    
    // Request antialiasing.
    RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    super.setRenderingHints( hints );
    
    super.setPaint( Color.GRAY );
    super.setStroke( new BasicStroke( 1f ) );
    super.setBorderColor( Color.GRAY );
    this.setPosition( 0, 0 );
  }
  
  public void setThickness( int thickness )
  {
    _thickness = thickness;
  }
  
  public int getThickness()
  {
    return _thickness;
  }
  
  public void setArc( int arc )
  {
    _arc = arc;
  }
  
  public int getArc()
  {
    return _arc;
  }
  
  public void setPosition( Point position )
  { 
    _position = position;
    updateShape();
  }
   
  public void setPosition( int x, int y )
  {
    this.setPosition( new Point( x, y ) );
  }
  
  public Point getPosition()
  {
    return _position;
  }
  
  public void addSegment( int orientation, int x, int y, int length )
  {
     RoundRectangle2D.Double r;
     if ( orientation == HORIZONTAL )
     {
       r = new RoundRectangle2D.Double( x, y, length, _thickness, _arc, _arc );
     }
     else
     {
       r = new RoundRectangle2D.Double( x, y, _thickness, length, _arc, _arc );
     }
     _pipes.add( r );
     updateShape();
  }
  
  public void clearSegements( )
  {
    _pipes = new ArrayList();
    updateShape();
  }

  private void updateShape()
  {  
    Area area = new Area();
    RoundRectangle2D.Double r;
    for ( int i = 0; i < _pipes.size(); i ++ )
    {
      r = (RoundRectangle2D.Double)_pipes.get(i);
      r.x += _position.x;
      r.y += _position.y;     
      area.add( new Area(r) );
    }
    super.setShape( area );
  }

}


/* end of file */