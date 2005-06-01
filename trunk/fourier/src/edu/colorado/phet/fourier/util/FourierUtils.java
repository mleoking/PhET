/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.util;

import java.awt.Color;


/**
 * FourierComponentColor
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierUtils {

    private static final Color[] HARMONIC_COLORS =
    {
            new Color( 1f, 0f, 0f ),
            new Color( 1f, 0.5f, 0f ),
            new Color( 1f, 1f, 0f ),
            new Color( 0f, 1f, 0f ),
            new Color( 0f, 0.790002f, 0.340007f ),
            new Color( 0.392193f, 0.584307f, 0.929395f ),
            new Color( 0f, 0f, 1f ),
            new Color( 0f, 0f, 0.501999f ),
            new Color( 0.569994f, 0.129994f, 0.61999f ),
            new Color( 0.729408f, 0.333293f, 0.827494f ),
            new Color( 1f, 0.411802f, 0.705893f )
    };
    
    private FourierUtils() {}
    
    public static Color calculateHarmonicColor( int n ) {
      if ( n < 0 || n >= HARMONIC_COLORS.length ) {
          throw new IllegalArgumentException( "n is out of range: " + n );
      }
      return HARMONIC_COLORS[ n ];
    }
}
