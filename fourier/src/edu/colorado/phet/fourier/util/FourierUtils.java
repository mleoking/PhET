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

import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.fourier.model.FourierSeries;


/**
 * FourierComponentColor
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierUtils {

    private FourierUtils() {}
    
    public static Color calculateColor( FourierSeries fourierSeriesModel, int n ) {
        double wavelengthRange = VisibleColor.MAX_WAVELENGTH - VisibleColor.MIN_WAVELENGTH;
        int numberOfHarmonics = fourierSeriesModel.getNumberOfHarmonics();
        if ( n >= numberOfHarmonics ) {
            throw new IllegalArgumentException( "n is out of range: " + n );
        }
        double deltaWavelength = wavelengthRange / ( numberOfHarmonics - 1 );
        double wavelength = VisibleColor.MAX_WAVELENGTH - ( n * deltaWavelength );
        Color color = VisibleColor.wavelengthToColor( wavelength );
        return color;
    }
}
