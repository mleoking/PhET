/* SpectrumSlider.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

/**
 * SpectrumSlider
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class SpectrumSlider extends PhetShapeGraphic
{
  private static final Color BORDER = Color.white;
  private static final Stroke STROKE = new BasicStroke( 1f );
  
  private Point _position;
  
  /**
   * Sole constructor.
   */
  public SpectrumSlider( Component parent )
  {    
    super( parent, null, null );
    
    _position = new Point( 0, 0 );
    Shape shape = genShape( _position.x, _position.y );
    setShape( shape );
    setPaint( Color.red );
    setStroke( STROKE );
    setBorderColor( BORDER );
  }
  
  public void setPosition( Point location )
  { 
    _position = location;
    Shape shape = genShape( _position.x, _position.y );
    super.setShape( shape );
  }
  
  public void setPosition( int x, int y )
  {
    this.setPosition( new Point( x, y ) );
  }
  
  public Point getPosition()
  {
    return _position;
  }
  
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