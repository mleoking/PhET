/* ThoughtBubbleGraphic.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

/**
 * ThoughtBubbleGraphic displays a "thought bubble", used to show perceived color.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class ThoughtBubbleGraphic extends PhetGraphic
{
  private static Stroke STROKE = new BasicStroke( 1f );
  
  private Point _position;
  private Paint _fill;
  private Paint _border;
  private Area _area;

  /**
   * Sole constructor.
   * 
   * @param component the parent component
   */
  public ThoughtBubbleGraphic( Component component )
  {
    super( component );
    
    // Initialize member data.
    _position = new Point(0,0);
    _border = Color.white;
    _fill = new Color( 0,0,0,0 );
    _area = new Area();
    
    // Use constructive area geometry to describe the "thought bubble".
    // The "thought bubble" is an Area, composed by adding a set of ellipses.
    {
      // Bulges on left end, top to bottom.
      _area.add( new Area( new Ellipse2D.Double(  25,  10, 100,  50 ) ) );
      _area.add( new Area( new Ellipse2D.Double(   0,    35, 100,  50 ) ) );
      _area.add( new Area( new Ellipse2D.Double(  15,   75, 100,  30 ) ) );
      // Bulges on right end, top to bottom.
      _area.add( new Area( new Ellipse2D.Double( 125,  10, 100,  50 ) ) );
      _area.add( new Area( new Ellipse2D.Double( 150,  25, 100,  50 ) ) );
      _area.add( new Area( new Ellipse2D.Double( 125,   50, 100,  50 ) ) );
      // Bulge in top center.
      _area.add( new Area( new Ellipse2D.Double(  50,    0, 150, 100 ) ) );
      // Bulge in bottom center.
      _area.add( new Area( new Ellipse2D.Double(  60,  65, 100, 50 ) ) );
      // 3 blips, top to bottom.
      _area.add( new Area( new Ellipse2D.Double( 150, 115, 40, 20 )) );
      _area.add( new Area( new Ellipse2D.Double( 165, 145, 30, 15 )) );
      _area.add( new Area( new Ellipse2D.Double( 175, 170, 20, 10 )) );
    }
  }

  /**
   * Gets the bounds of the thought bubble.
   * 
   * @return the bounds
   */
  protected Rectangle determineBounds()
  {
    return _area.getBounds();
  }

  /**
   * Gets the paint used to draw the border.
   * 
   * @return the border paint
   */
  public Paint getBorder()
  {
    return _border;
  }
  
  /**
   * Sets the paint used to draw the border.
   * 
   * @param border the border paint
   */
  public void setBorder( Paint border )
  {
    _border = border;
  }
  
  /**
   * Gets the paint used to fill the area.
   * 
   * @return the fill paint
   */
  public Paint getFill()
  {
    return _fill;
  }
  
  /**
   * Sets the paint used to fill the area.
   * 
   * @param fill the fill paint
   */
  public void setFill( Paint fill )
  {
    _fill = fill;
  }
  
  /**
   * Gets the position.
   * 
   * @return the position
   */
  public Point getPosition()
  {
    return _position;
  }

  /** 
   * Sets the position, relative to the upper left corner of the 
   * area's bounding rectangle.
   * 
   * @param position the position
   */
  public void setPosition( Point position )
  {
    _position = position;
  }
  
  /** 
   * Sets the position, relative to the upper left corner of the 
   * area's bounding rectangle.
   * 
   * @param x the X coordinate
   * @param y the Y coordinate
   */
  public void setPosition( int x, int y )
  {
    setPosition( new Point(x,y) );
  }
  
  /*
   * Renders the thought bubble.
   * Graphics state is saved and restored.
   * 
   * @param g2 the grpahics context
   */
  public void paint( Graphics2D g2 )
  {
    if ( isVisible() )
    {
      super.saveGraphicsState( g2 );
      {
        // Request antialiasing.
        RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHints( hints );
        // Translate to location.
        g2.transform( AffineTransform.getTranslateInstance(_position.x, _position.y) );
        // Fill the area.
        g2.setPaint( _fill );
        g2.fill( _area );
        // Draw the border.
        g2.setStroke( STROKE );
        g2.setPaint( _border );
        g2.draw( _area );
      }
      super.restoreGraphicsState();
    }
  }

}


/* end of file */