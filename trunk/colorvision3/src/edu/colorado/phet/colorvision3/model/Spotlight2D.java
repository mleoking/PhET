/* Spotlight2D.java */

package edu.colorado.phet.colorvision3.model;

import java.awt.Color;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObservable;

/**
 * Spotlight2D is the model for a 2D spotlight.
 * Any changes to the models properties (via its setters or getters)
 * results in the notification of all registered observers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class Spotlight2D extends SimpleObservable
{
  public static final double INTENSITY_MIN = 0.0;
  public static final double INTENSITY_MAX = 100.0;
  public static final double DROP_OFF_RATE_MIN = 0.0;  // constant intensity
  public static final double DROP_OFF_RATE_MAX = 100.0; // sharpest droff-off
  public static final double CUT_OFF_ANGLE_MIN = 0.0;
  public static final double CUT_OFF_ANGLE_MAX = 180.00;
  public static final double CUT_OFF_ANGLE_DEFAULT = 15.0;
  
  private Color _color;
  private double _cutOffAngle;
  private double _direction;
  private double _dropOffRate;
  private double _intensity; // 0.0 - 100.0 %
  private double _x, _y;

  /**
   * Sole constructor.
   * Creates a Spotlight2D with: location at the origin (0,0), a white beam,
   * 20 degree cut off angle, 0 degree direction, 0 drop off rate (contant),
   * 0 intensity.
   */
  public Spotlight2D()
  {
    // Initialize instance data.
    _color       = Color.white;
    _cutOffAngle = CUT_OFF_ANGLE_DEFAULT;
    _direction   = 0.0;
    _dropOffRate = DROP_OFF_RATE_MIN; // constant intensity
    _intensity   = INTENSITY_MIN;
    _x = _y      = 0.0;
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
   * Sets the color.
   * 
   * @param color the color
   */
  public void setColor( Color color )
  {
    _color = color;
    notifyObservers();
  }
  
  /**
   * Gets the cut off angle.
   * The cut off angle is the angle outside of which the light intensity is 0.0.
   *  
   * @return the cut off angle (in degrees)
   */
  public double getCutOffAngle()
  {
    return _cutOffAngle;
  }
  
  /**
   * Sets the cut off angle.
   * Values outside the allowable range are silently clamped.
   * 
   * @param cutOffAngle the cut off angle (in degrees)
   */
  public void setCutOffAngle( double cutOffAngle )
  {
    _cutOffAngle = MathUtil.clamp( CUT_OFF_ANGLE_MIN, cutOffAngle,CUT_OFF_ANGLE_MAX );
    notifyObservers();
  }
  
  /**
   * Gets the direction. 
   * The direction determines where the spotlight points.
   * 
   * @return the direction (in degrees)
   */
  public double getDirection()
  {
    return _direction;
  }
  
  /**
   * Sets the direction.
   * 
   * @param direction the direction (in degrees)
   */
  public void setDirection( double direction )
  {
    _direction = direction;
    notifyObservers();
  }
  
  /**
   * Gets the drop off rate of the light.  The drop off rate is the rate
   * at which light intensity drops off from the primary direction.
   * 
   * @return the drop off rate
   */
  public double getDropOffRate()
  {
    return _dropOffRate;
  }
  
  /**
   * Sets the drop off rate.
   * Values range from 0.0 (constant intensity) to 1.0 (sharpest drop-off) inclusive.
   * Values outside the allowable range are silently clamped.
   * 
   * @param dropOffRate the drop off rate
   */
  public void setDropOffRate( double dropOffRate )
  {
    _dropOffRate = MathUtil.clamp( DROP_OFF_RATE_MIN, dropOffRate, DROP_OFF_RATE_MAX );
    notifyObservers();
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
   * Sets the intensity.
   * Intensity is measured as a percentage.
   * Values range from 0.0 (no intensity) to 100.0 (full intensity) inclusive.
   * Values outside the allowable range are silently clamped.
   * 
   * @param intensity the intensity
   */
  public void setIntensity( double intensity )
  {
    _intensity = MathUtil.clamp( INTENSITY_MIN, intensity, INTENSITY_MAX );
    notifyObservers();
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
   * Sets the location.
   * 
   * @param x the X coordinate
   * @param y the Y coordinate
   */
  public void setLocation( double x, double y )
  {
    _x = x;
    _y = y;
    notifyObservers();
  }

}


/* end of file */