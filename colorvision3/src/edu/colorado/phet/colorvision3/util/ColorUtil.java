/* ColorUtil.java */

package edu.colorado.phet.colorvision3.util;

import java.awt.Color;

/**
 * Color Utilities
 */
public class ColorUtil
{
  // Don't change these -- they are hardcoded into some of the methods below!
  public static final double MIN_WAVELENGTH = 380;
  public static final double MAX_WAVELENGTH = 780;
  
  // Wavelength for white.
  public static final double WHITE_WAVELENGTH = 0.0;
  
  // Two colors match if each of their components differ by this amount.
  private static final int COLOR_MATCH_DELTA = 2;
  
  // Lookup table that for mapping wavelength to Color.
  private static Color _colorArray[] = null;

  /** Not intended for instantiation or subclassing. */
  private ColorUtil() {}
  
  /** 
   * Converts a wavelength to a Color.
   * A wavelength of zero corresponds to white.
   * Wavelengths outside the visible spectrum return fully-transparent black.
   * 
   * @param wl the wavelength
   * @return the corresponding Color
   */
  public static Color wavelengthToColor( double wl )
  {
    Color color = null;
    
    if ( wl == WHITE_WAVELENGTH )
    {
      // Special case: wavelength of zero is white light.
      color = new Color( 255, 255, 255, 255 );
    }  
    else if ( wl < MIN_WAVELENGTH || wl > MAX_WAVELENGTH )
    {
      // Special case: wavelength outside the visible spectrum.
      return new Color( 0, 0, 0, 0 );
    }
    else
    {
      // Look up the color.
      if ( _colorArray == null )
      {
        initColorArray();
      }
      // Colors are immuatable, so use the color from the lookup array.
      color = _colorArray[ (int)(wl - MIN_WAVELENGTH) ];
    }
    
    return color;
  }
    
  /**
   * Converts a Color to its corresponding wavelength.
   * Relies on a color lookup table that is initialized the first time
   * that this method is called.
   * 
   * @param color the color
   * @return the wavelength
   */
  public static double colorToWavelength( Color color )
  {
    double wavelength = WHITE_WAVELENGTH;
    
    if ( _colorArray == null )
    {
      initColorArray();
    }

    for ( int i = 0; i < _colorArray.length; i++ )
    {
      if ( Math.abs(color.getRed() - _colorArray[i].getRed()) < COLOR_MATCH_DELTA &&
           Math.abs(color.getGreen() - _colorArray[i].getGreen()) < COLOR_MATCH_DELTA &&
           Math.abs(color.getBlue() - _colorArray[i].getBlue()) < COLOR_MATCH_DELTA )
      {
        wavelength = MIN_WAVELENGTH + i;
        break;   
      }
    }

    return wavelength;
  }
  
  /**
   * Initializes a color lookup array, used to map between Colors and wavelength.
   * This method is called only once.
   * <p>
   * This method originally appeared in Ron Lemaster's Flash version of this 
   * simulation, in the file ColorUtil.as.  The method was called ColorUtil.genCtx.
   * I removed code that was commented out, and fixed bugs as noted in the
   * comments.
   */
  private static void initColorArray()
  {
    // Allocate the color array.
    int numWavelengths = (int) (MAX_WAVELENGTH - MIN_WAVELENGTH + 1);
    _colorArray = new Color[ numWavelengths ];
    
    // Populate the color array.
    double wl;
    double r, g, b;
    for ( int i = 0; i < numWavelengths; i++)
    {
      // Create the RGB component values.
      wl = MIN_WAVELENGTH + i;
      r = g = b = 0.0;
      
      // Determine the RGB component values.
      if ( wl >= 380. && wl <= 440. )
      {
        r = -1. * (wl - 440.) / (440. - 380.);
        g = 0;
        b = 1;
      }
      else if ( wl > 440. && wl <= 490. )
      {
        r = 0;
        g = (wl - 440.) / (490. - 440.);
        b = 1.;
      }
      else if ( wl > 490. && wl <= 510. )
      {
        r = 0;
        g = 1;
        b = -1. * (wl - 510.) / (510. - 490.);
      }
      else if ( wl > 510. && wl <= 580. )
      {
        r = (wl - 510.) / (580. - 510.);
        g = 1.;
        b = 0.;
      }
      else if ( wl > 580. && wl <= 645. )
      {
        r = 1.;
        g = -1. * (wl - 645.) / (645. - 580.);
        b = 0.;
      }
      else if ( wl > 645. && wl <= 780. )
      {
        r = 1.;
        g = 0.;
        b = 0;
      }
      
      // Let the intensity fall off near the vision limits.
      double intensity;
      // BUG FIX: 
      // The value 645 in this block was 700 in the original code.
      // Because all values above 645 have the same RGB components (see above),
      // this resulted in duplicate entries in the color lookup array for wavelengths
      // int the range 645-700 inclusive.  Setting the value to 645 solves this problem.
      if ( wl > 645. )
      {
        intensity = .3 + .7 * (780. - wl) / (780. - 645.);
      }
      else if ( wl < 420. )
      {
        intensity = .3 + .7 * (wl - 380.) / (420. - 380.);
      }
      else
      {
        intensity = 1.;
      }
      int red = (int) Math.round( 255 * (intensity * r) );
      int green = (int) Math.round( 255 * (intensity * g) );
      int blue = (int) Math.round( 255 * (intensity * b) );
      int alpha = 255;
      
      // Add the color to the lookup array.
      _colorArray[i] = new Color( red, green, blue, alpha );
    }
    
    //debug_colorArray();
    
  }  // initColorArray

  
  /**
   * Debugging method for examining the contents of the color lookup array.
   * This partially tests the correctness of the initColorArray method.
   * There should be no duplicate colors, since the presence of duplicates
   * will yeild incorrect results when we map wavelengths to Colors.
   */
  public static final void debug_colorArray()
  {
    // Determines how many duplicate colors are in the lookup array.
    int duplicates = 0;
    for ( int i = 0; i < _colorArray.length-1; i++ )
    {
      if ( _colorArray[i].equals( _colorArray[i+1] ) )
      {
        duplicates++;
      }
    }
    System.out.println( "colorArray duplicates: " + duplicates );
  }
  
}

/* end of file */
