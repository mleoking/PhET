/* FilterGraphic.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.colorvision3.model.Filter;
import edu.colorado.phet.colorvision3.util.ColorUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

/**
 * FilterGraphic
 *
 * @author cmalley
 * @revision $Id$
 */
public class FilterGraphic extends PhetGraphic implements SimpleObserver
{
  private static final int HEIGHT = 150;
  
  private Filter _model;
  private Point _position;
  private Color _filterColor;
  private Shape _exterior, _interior, _lens;
  
  public FilterGraphic( Component parent, Filter model )
  {
    super( parent );
    _model = model;
    _position = new Point(0,0);
    _filterColor = Color.WHITE;
  }

  public void setPosition( Point position )
  { 
    _position = position;
    updateShape();
    super.repaint();
  }
   
  public void setPosition( int x, int y )
  {
    this.setPosition( new Point( x, y ) );
  }
  
  public Point getPosition()
  {
    return _position;
  }
  
  public void setTransmissionPeak( double wavelength )
  {
    _model.setTransmissionPeak( wavelength );
  }
  
  private void updateShape()
  {
    int x = _position.x;
    int y = _position.y;
    int height = 150;
    
    // Use constructive area geomety to create the exterior shape.
    Area area = new Area();
    {
      Ellipse2D.Double e1 = new Ellipse2D.Double( x, y, 20, HEIGHT );
      Rectangle2D.Double r1 = new Rectangle2D.Double( x+10, y, 10, HEIGHT );
      Ellipse2D.Double e2 = new Ellipse2D.Double( x+10, y, 20, HEIGHT );
      Rectangle2D.Double base = new Rectangle2D.Double( x+10, y+HEIGHT, 10, 20 );
      area.add( new Area(e1) );
      area.add( new Area(e2) );
      area.add( new Area(r1) );
      area.add( new Area(base) );
    }
    
    _exterior = area;
    _interior = new Ellipse2D.Double( x, y, 20, HEIGHT );
    _lens = new Ellipse2D.Double( x+4, y+4, 12, HEIGHT-8 );
  }
  
  public void paint( Graphics2D g2 )
  {
    if ( super.isVisible() )
    {
      super.saveGraphicsState( g2 );
      {
        // Request antialiasing.
        RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHints( hints );
        
        // Draw the exterior
        g2.setPaint( Color.DARK_GRAY );
        g2.fill( _exterior );
        
        // Draw the interior
        g2.setPaint( Color.GRAY );
        g2.fill( _interior );
        
        // Draw the filter
        g2.setPaint( _filterColor );
        g2.fill( _lens );
      }
      super.restoreGraphicsState();
    }
  }

  /**
   * Determines the bounds.
   */
  protected Rectangle determineBounds()
  {
    Rectangle bounds = null;
    if ( _exterior != null )
    {
      bounds = _exterior.getBounds();
    }
    return bounds;
  }

  /**
   * @see edu.colorado.phet.common.util.SimpleObserver#update()
   */
  public void update()
  {
    double wavelength = _model.getTransmissionPeak();
    _filterColor = ColorUtil.wavelengthToColor( wavelength );
    repaint();
  }
}


/* end of file */