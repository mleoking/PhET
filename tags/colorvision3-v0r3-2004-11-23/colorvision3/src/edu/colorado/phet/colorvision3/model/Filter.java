/* Filter.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.model;

import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.view.util.VisibleColor;

/**
 * Filter is a model of a color filter.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$ $Name$
 */
public class Filter extends SimpleObservable
{
	//----------------------------------------------------------------------------
	// Class data
  //----------------------------------------------------------------------------

  // Default transmission width.
  private static final double DEFAULT_TRANSMISSION_WIDTH = 50.0;
  
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  // Filter location in 2D space
  private double _x, _y;
  // Is the filter enabled?
  private boolean _enabled;
  // The filter's peak transmission color.
  private VisibleColor _transmissionPeak;
  // Width (in nm) of the filter's transmission curve.
  private double _transmissionWidth;

	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  public Filter()
  {
    _x = _y = 0.0;
    _transmissionPeak = VisibleColor.WHITE;
    _transmissionWidth = DEFAULT_TRANSMISSION_WIDTH;
  }
  
	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------

  /**
   * Sets the filter location.
   * 
   * @param x X coordinate
   * @param y Y coordinate
   */
  public void setLocation( double x, double y )
  {
    _x = x;
    _y = y;
    _enabled = true;
    notifyObservers();
  }
  
  /**
   * Gets location X coordinate
   * 
   * @return X coordinate
   */
  public double getX()
  {
    return _x;
  }
  
  /**
   * Gets location Y coordinate
   * 
   * @return Y coordinate
   */
  public double getY()
  {
    return _y;
  }
  
  /**
   * Enables the filter.
   * 
   * @param enabled true to enable, false to disable
   */
  public void setEnabled( boolean enabled )
  {
    _enabled = enabled;
    notifyObservers();
  }
  
  /**
   * Is the filter enabled?
   * 
   * @return true or false
   */
  public boolean isEnabled()
  {
    return _enabled;
  }
  
  /**
   * Gets the peak color transmitted.
   * 
   * @return the color
   */
  public VisibleColor getTransmissionPeak()
  {
    return _transmissionPeak;
  }
  
  /**
   * Sets the peak color transmitted.
   * 
   * @param transmissionPeak the color
   */
  public void setTransmissionPeak( VisibleColor transmissionPeak )
  {
    _transmissionPeak = transmissionPeak;
    notifyObservers();
  }
  
  /**
   * Sets the peak color transmitted.
   * 
   * @param wavelength the color's wavelength
   */
  public void setTransmissionPeak( double wavelength )
  {
    setTransmissionPeak( new VisibleColor(wavelength) );
  }
  
  /**
   * Gets the transmission width.
   * 
   * @return the transmission width, in nm
   */
  public double getTransmissionWidth()
  {
    return _transmissionWidth;
  }
  
  /**
   * Sets the transmission width.
   * 
   * @param transmissionWidth the transmission width, in nm
   */
  public void setTransmissionWidth( double transmissionWidth )
  {
    _transmissionWidth = transmissionWidth;
    notifyObservers();
  }
  
	//----------------------------------------------------------------------------
	// Filtering methods
  //----------------------------------------------------------------------------

  /**
   * Determines the color passed by the filter.
   * If the filter is disabled, then the color passes through.
   * White passes as the filter color, while other colors are 
   * attenuated based on their proximity to the transmission peak
   * and the transmission width.
   * 
   * @param color the color entering the filter
   */
  public VisibleColor colorPassed( VisibleColor color )
  {
    VisibleColor passedColor = null;
    
    if ( ! _enabled )
    {
      // If the filter is not enabled, simply pass the color.
      passedColor = color;
    }
    else if ( color.equals( VisibleColor.WHITE) )
    {
      // White passes as the filter color.
      passedColor = _transmissionPeak;
    }
    else
    {
      // With a colored bulb, the filter passes some percentage of the bulb color.
      double percentPassed = percentPassed( color );
      if ( percentPassed == 0 )
      {
        passedColor = VisibleColor.INVISIBLE;
      }
      else
      {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        // Scale alpha by percent passed.
        int a = (int)( percentPassed / 100 * 255 );
        passedColor = new VisibleColor( r, g, b, a );
      }
    }
    return passedColor;
  }
  
  /**
   * Determines the percentage of a color that is passed by the filter.
   * 
   * @param color the color
   * @return the percentage (0-100)
   */
  public double percentPassed( VisibleColor color )
  {
    double wavelength = color.getWavelength();
    double percent = 0.0;
    double peak = _transmissionPeak.getWavelength();
    double halfWidth = _transmissionWidth/2;
    
    if ( wavelength == VisibleColor.WHITE_WAVELENGTH )
    {
      //  Special case: white light passes 100%
      percent = 100.0;
    }
    else if ( wavelength < peak - halfWidth || wavelength > peak + halfWidth )
    {
      // If the wavelength is outside the transmission width, no color passes.
      percent = 0.0;
    }
    else
    {
      // Wavelength is within the transmission width, pass a linear percentage.
       percent = 100.0 - ((Math.abs( peak - wavelength )/halfWidth) * 100.0);
    }
    
    return percent;
  }
}


/* end of file */