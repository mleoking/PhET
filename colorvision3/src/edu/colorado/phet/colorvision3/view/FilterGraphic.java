/* FilterGraphic.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.colorvision3.model.Filter;
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
  private Shape _exterior, _interior, _lens;
  
  public FilterGraphic( Component parent, Filter model )
  {
    super( parent );
    _model = model;
    update();
  }
  
  public void setLocation( int x, int y )
  {
    _model.setLocation( x, y );
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
    int x = (int)_model.getX();
    int y = (int)_model.getY();
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
    
    super.repaint();
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
        g2.setPaint( _model.getTransmissionPeak() );
        g2.fill( _lens );
      }
      super.restoreGraphicsState();
    }
  }

}


/* end of file */