/* Filter.java */

package edu.colorado.phet.colorvision3.model;

import edu.colorado.phet.colorvision3.util.ColorUtil;
import edu.colorado.phet.common.util.SimpleObservable;

/**
 * Filter
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class Filter extends SimpleObservable
{
  private static final double DEFAULT_TRANSMISSION_WIDTH = 50.0;
  
  // Filter location
  private double _x, _y;
  
  // The filter's peak transmission wavelength.
  private double _transmissionPeak;
  
  // Width (in nm) of the filter's transmission curve.
  private double _transmissionWidth;

  public Filter()
  {
    _x = _y = 0.0;
    _transmissionPeak = ColorUtil.WHITE_WAVELENGTH;
    _transmissionWidth = DEFAULT_TRANSMISSION_WIDTH;
  }
  
  public void setLocation( double x, double y )
  {
    _x = x;
    _y = y;
    notifyObservers();
  }
  
  public double getX()
  {
    return _x;
  }
  
  public double getY()
  {
    return _y;
  }
  
  public double getTransmissionPeak()
  {
    return _transmissionPeak;
  }
  
  public void setTransmissionPeak( double transmissionPeak )
  {
    _transmissionPeak = transmissionPeak;
    notifyObservers();
  }
  
  /**
   * @return Returns the transmissionWidth.
   */
  public double getTransmissionWidth()
  {
    return _transmissionWidth;
  }
  
  /**
   * @param transmissionWidth The transmissionWidth to set.
   */
  public void setTransmissionWidth( double transmissionWidth )
  {
    _transmissionWidth = transmissionWidth;
    notifyObservers();
  }
  
  public double calculatePercentPassed( double wavelength )
  {
    double percent = 0.0;
    
    if ( _transmissionPeak == ColorUtil.WHITE_WAVELENGTH )
    {
      // Special case: white filter passes 100% of all colors
      percent = 100.0;
    }
    else if ( wavelength == ColorUtil.WHITE_WAVELENGTH )
    {
      //  Special case: white light passes 100%
      percent = 100.0;
    }
    else if ( wavelength < _transmissionPeak - (_transmissionWidth/2) ||
              wavelength > _transmissionPeak + (_transmissionWidth/2) )
    {
      // If the wavelength is outside the transmission width, no color passes.
      percent = 0.0;
    }
    else
    {
      // Wavelength is within the transmission width, pass a linear percentage.
       percent = 100.0 - ((Math.abs( _transmissionPeak - wavelength )/(_transmissionWidth/2)) * 100.0);
    }
    
    return percent;
  }
}


/* end of file */