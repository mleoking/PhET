/* ColumnarBeamGraphic.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.colorvision3.model.Spotlight;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

/**
 * ColumnarBeamGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class ColumnarBeamGraphic extends PhetShapeGraphic
{
  private Spotlight _model;
  private int _distance;
  private int _alphaScale; // percent
  
  public ColumnarBeamGraphic( Component parent, Spotlight model )
  {
    super( parent, null, null );
    
    _model = model;
    _distance = 0;
    _alphaScale = 50;
    
    // Request antialiasing.
    RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    super.setRenderingHints( hints );
    
    super.setPaint( Color.white );
    super.setStroke( null );
    
    update();
  }
  
  public void setDistance( int distance )
  {
    _distance = distance;
    update();
  }
  
  public int getDistance()
  {
    return _distance;
  }
  
  public void setAlphaScale( int alphaScale )
  {
    _alphaScale = alphaScale;
  }
  
  public int getAlphaScale()
  {
    return _alphaScale;
  }
  
  public void setColor( Color color )
  {
    this.setPaint( color );
  }
  
  public void setPaint( Paint paint )
  {
    if ( paint instanceof Color )
    {
      // Scale the alpha component to provide transparency.
      Color c = (Color)paint;
      int r = c.getRed();
      int g = c.getGreen();
      int b = c.getBlue();
      int a = c.getAlpha() * _alphaScale / 100;
      super.setPaint( new Color(r,g,b,a) );
    }
    else
    {
      super.setPaint( paint );
    }
  }
  
  public void update()
  { 
    double direction = _model.getDirection(); // in degrees
    double cutOffAngle = _model.getCutOffAngle(); // in degrees

    // Radius of the beam.
    double radius = _distance / Math.cos( Math.toRadians( cutOffAngle/2 ) );
    
    // First coordinate
    double x0 = _model.getX();
    double y0 = _model.getY();
    
    // Second coordinate
    double theta1 = Math.toRadians( direction - (cutOffAngle/2) );
    double x1 = radius * Math.cos( theta1 );
    double y1 = radius * Math.sin( theta1 );
    x1 += _model.getX();
    y1 += _model.getY();
      
    // Third coordinate
    double theta2 = Math.toRadians( direction + (cutOffAngle/2) );
    double x2 = radius * Math.cos( theta2 );
    double y2 = radius * Math.sin( theta2 );
    x2 += _model.getX();
    y2 += _model.getY();
    
    GeneralPath path = new GeneralPath();
    path.moveTo( (int)x0, (int)y0 );
    path.lineTo( (int)x1, (int)y1 );
    path.lineTo( (int)x2, (int)y2 );
    path.closePath();
    
    super.setShape( path );
  }
  

}


/* end of file */