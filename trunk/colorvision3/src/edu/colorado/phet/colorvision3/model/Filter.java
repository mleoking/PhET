/* Filter.java */

package edu.colorado.phet.colorvision3.model;

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
  
  private boolean _enabled;
  
  // The filter's peak transmission color.
  private VisibleColor _transmissionPeak;
  
  // Width (in nm) of the filter's transmission curve.
  private double _transmissionWidth;

  public Filter()
  {
    _x = _y = 0.0;
    _transmissionPeak = VisibleColor.WHITE;
    _transmissionWidth = DEFAULT_TRANSMISSION_WIDTH;
  }
  
  public void setLocation( double x, double y )
  {
    _x = x;
    _y = y;
    _enabled = true;
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
  
  public void setEnabled( boolean enabled )
  {
    _enabled = enabled;
  }
  
  public boolean isEnabled()
  {
    return _enabled;
  }
  
  public VisibleColor getTransmissionPeak()
  {
    return _transmissionPeak;
  }
  
  public void setTransmissionPeak( VisibleColor transmissionPeak )
  {
    _transmissionPeak = transmissionPeak;
    notifyObservers();
  }
  
  public void setTransmissionPeak( double wavelength )
  {
    setTransmissionPeak( new VisibleColor(wavelength) );
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
  
  public VisibleColor colorPassed( VisibleColor color )
  {
    VisibleColor passedColor = null;
    
    if ( color.equals( VisibleColor.WHITE) )
    {
      // White light passes as filter color.
      passedColor = _transmissionPeak;
    }
    else
    {
      // With a colored bulb, the filter passes some percentage of the bulb color.
      double percentPassed = 
        calculatePercentPassed( color.getWavelength(), _transmissionPeak.getWavelength(), _transmissionWidth );
      if ( percentPassed == 0 )
      {
        passedColor = VisibleColor.INVISIBLE;
      }
      else
      {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = (int)( percentPassed / 100 * 255 );
        passedColor = new VisibleColor( r, g, b, a );
      }
    }
    return passedColor;
  }
  
  private static double calculatePercentPassed( 
      double wavelength, double transmissionPeak, double transmissionWidth )
  {
    double percent = 0.0;
    
    if ( wavelength == VisibleColor.WHITE_WAVELENGTH )
    {
      //  Special case: white light passes 100%
      percent = 100.0;
    }
    else if ( wavelength < transmissionPeak - (transmissionWidth/2) ||
              wavelength > transmissionPeak + (transmissionWidth/2) )
    {
      // If the wavelength is outside the transmission width, no color passes.
      percent = 0.0;
    }
    else
    {
      // Wavelength is within the transmission width, pass a linear percentage.
       percent = 100.0 - ((Math.abs( transmissionPeak - wavelength )/(transmissionWidth/2)) * 100.0);
    }
    
    return percent;
  }
}


/* end of file */