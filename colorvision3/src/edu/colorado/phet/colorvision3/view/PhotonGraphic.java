/* PhotonGraphic.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * PhotonGraphic is the visual representation of a single photon.
 * It is used as a component of a PhotonBeamGraphic.
 *
 * @author Chris Malley, cmalley@pixelzoom.com
 * @revision $Id$
 */
public class PhotonGraphic
{
  public static int PHOTON_DS = 10;
  public static int PHOTON_LINE_LENGTH = 3;
  private static int PHOTON_LINE_WIDTH = 1;
  private static Stroke PHOTON_STROKE = new BasicStroke( PHOTON_LINE_WIDTH );
  
  private Color _color;
  private double _direction;
  private double _intensity; // 0.0 - 100.0 %
  private boolean _inUse;
  private double _x, _y;

  private double _deltaX, _deltaY;
  private double _width, _height;

  /**
   * Sole constructor.
   * 
   * @param color the color
   * @param intensity the intensity
   * @param x the X location
   * @param y the Y location
   * @param direction the direction
   */
  public PhotonGraphic( final Color color, double intensity, double x, double y, double direction ) 
  {   
    setColor( color );
    setIntensity( intensity );
    setLocation( x, y );
    setDirection( direction );   
    setInUse( true );
  }

  /**
   * Sets the "in use" state.
   * 
   * @param inUse true or false
   */
  public void setInUse( boolean inUse )
  {
    _inUse = inUse;
  }

  /**
   * Indicates whether the photon is in use.
   * Photons that are not in use are available for reuse.
   * 
   * @return true or false
   */
  public boolean isInUse()
  {
    return _inUse;
  }

  /**
   * Sets the location.
   * 
   * @param x X coordinate
   * @param y Y coordinate
   */
  public void setLocation( double x, double y )
  {
    _x = x;
    _y = y;
  }

  /**
   * Gets the X coordinate of the location.
   * 
   * @return the X coordinate
   */
  public double getX() 
  {
    return _x;
  }
  
  /**
   * Gets the Y coordinate of the location.
   * 
   * @return the Y coordinate
   */
  public double getY()
  {
    return _y;
  }
  
  /**
   * Sets the direction of the photon.
   * 
   * @param direction the direction (in degrees)
   */
  public void setDirection( double direction )
  {
    _direction = direction;
    
    // Pre-compute rendering values that are based on direction.
    double radians = Math.toRadians( direction );
    double cosAngle = Math.cos( radians );
    double sinAngle = Math.sin( radians );
    _deltaX = PHOTON_DS * cosAngle;
    _deltaY = PHOTON_DS * sinAngle;
    _width = PHOTON_LINE_LENGTH * cosAngle;
    _height = PHOTON_LINE_LENGTH * sinAngle;
  }
  
  /**
   * Gets the direction of the photon.
   * 
   * @return the direction (in degrees)
   */
  public double getDirection()
  {
    return _direction;
  }
  
  /**
   * Sets the color.
   * 
   * @param color the color
   */
  public void setColor( final Color color )
  {
    _color = color;
  }
  
  /**
   * Gets the color.
   * 
   * @return the color
   */
  public Color getColor()
  {
    return _color;
  }
  
  /**
   * Sets the intensity.
   * Intensity is measured as a percentage.
   * Values range from 0.0 (no intensity) to 100.0 (full intensity) inclusive.
   * 
   * @param intensity the intensity
   */
  public void setIntensity( double intensity )
  {
    _intensity = intensity;
  }
  
  /**
   * Gets the intensity.
   * 
   * @return the intensity
   */
  public double getIntensity()
  {
    return _intensity;
  }
  
  /**
   * Gets the width of the photon's bounding rectangle.
   * 
   * @return the width
   */
  public double getWidth()
  {
    return _width;
  }
  
  /**
   * Gets the height of the photon's bounding rectangle.
   * 
   * @return the height
   */
  public double getHeight()
  {
    return _height;
  }
  
  /**
   * Advances the location of the photon.
   * 
   * @param dt time delta, currently ignored
   */
  public void stepInTime( double dt )
  {
    // Advance the location of the photon.
    _x += _deltaX;
    _y += _deltaY;
  }
  
  /**
   * Renders the photon.
   * Note that for performance reasons, this call does NOT restore the graphics state.
   * Graphics state must be saved and restored by the caller.
   * 
   * @param g2 the 2D graphics context
   */
  public void paint( Graphics2D g2 )
  {
    g2.setStroke( PHOTON_STROKE );
    g2.setColor( _color );
    g2.drawLine( (int)_x, (int)_y, 
                 (int)(_x - _width), (int)(_y - _height) );
  }
  
}

/* end of file */