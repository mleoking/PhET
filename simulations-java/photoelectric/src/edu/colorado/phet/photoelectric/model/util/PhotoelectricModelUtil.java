/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.model.util;

import edu.colorado.phet.photoelectric.PhotoelectricConfig;

/**
 * PhotoelectricModelUtil
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricModelUtil {

    public static double intensityToPhotonRate( double intensity, double wavelength ) {
        return intensity * wavelength / PhotoelectricConfig.MAX_WAVELENGTH;
    }

    public static double photonRateToIntensity( double photonRate, double wavelength ) {
        return photonRate * PhotoelectricConfig.MAX_WAVELENGTH / wavelength;
    }
}
