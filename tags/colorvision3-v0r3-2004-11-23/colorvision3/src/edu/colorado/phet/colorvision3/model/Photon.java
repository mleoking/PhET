/* Photon.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.model;

import edu.colorado.phet.colorvision3.view.PhotonBeamGraphic;
import edu.colorado.phet.common.view.util.VisibleColor;


/**
 * Photon is the model of a single photon.
 *
 * @author Chris Malley, cmalley@pixelzoom.com
 * @version $Id$ $Name$
 */
public class Photon
{
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  // Photon color
  private VisibleColor _color;
  // Direction in degrees
  private double _direction;
  // Intensity of the light source when the photon was emitted, in percent (0-100)
  private double _intensity;
  // False indicates the photon is available for reuse
  private boolean _inUse;
  // Location in 2D space
  private double _x, _y;
  // True if the photon has been previously filtered
  private boolean _isFiltered;

  // These things are related to how a photon is displayed, but
  // are included here so that we can optimize by pre-computing
  // some values.  See the setDirection method.
  private double _deltaX, _deltaY, _width, _height;
  
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Sole constructor.
   * 
   * @param color the color
   * @param intensity the intensity
   * @param x the X location
   * @param y the Y location
   * @param direction the direction
   */
  public Photon( VisibleColor color, double intensity, double x, double y, double direction ) 
  {   
    _inUse = true;
    _color = color;
    _intensity = intensity;
    _x = x;
    _y = y;
    _isFiltered = false;
    setDirection( direction );  // pre-calculates deltas  
  }

	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------

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
   * Sets the filter status of the photon.
   * 
   * @param isFiltered true if the photon has been filtered, false otherwise
   */
  public void setFiltered( boolean isFiltered )
  {
    _isFiltered = isFiltered;
  }
  
  /**
   * Has the photon been filtered?
   * 
   * @return true if the photon has been filtered, false otherwise
   */
  public boolean isFiltered()
  {
    return _isFiltered;
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
    _deltaX = PhotonBeam.PHOTON_DS * cosAngle;
    _deltaY = PhotonBeam.PHOTON_DS * sinAngle;
    _width  = PhotonBeamGraphic.PHOTON_LINE_LENGTH * cosAngle;
    _height = PhotonBeamGraphic.PHOTON_LINE_LENGTH * sinAngle;
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
  public void setColor( VisibleColor color )
  {
    _color = color;
  }
  
  /**
   * Gets the color.
   * 
   * @return the color
   */
  public VisibleColor getColor()
  {
    return _color;
  }
  
  /**
   * Sets the intensity. of the light source at the time the 
   * photon was emitted.  This value is a percentage.
   * <p>
   * Values range from 0.0 (no intensity) to 100.0 (full intensity) inclusive.
   * 
   * @param intensity the intensity
   * @throws IllegalArgumentException if intensity is out of range
   */
  public void setIntensity( double intensity )
  {
    if ( intensity < 0 || intensity > 100 )
    {
      throw new IllegalArgumentException( "intensity out of range: " + intensity );
    }
    _intensity = intensity;
  }
  
  /**
   * Gets the intensity of the light source at the time the 
   * photon was emitted.
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
  
}

/* end of file */