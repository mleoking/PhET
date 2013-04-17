// Copyright 2002-2011, University of Colorado

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

    /*
     * Note: The intensity param is poorly named. It does not vary from 0...100,
     * it varies from 0...PhotoelectricModel.MAX_PHOTONS_PER_SECOND.  So it's actually
     * the maximum number of photons for a specific intensity.
     * Discovered while investigating #2989.
     */
    public static double intensityToPhotonRate( double intensity, double wavelength ) {
//        System.out.println( "PhotoelectricModelUtil.intensityToPhotonRate intensity=" + intensity + " wavelength=" + wavelength );
        return intensity * wavelength / PhotoelectricConfig.MAX_WAVELENGTH;
    }

    public static double photonRateToIntensity( double photonRate, double wavelength ) {
        return photonRate * PhotoelectricConfig.MAX_WAVELENGTH / wavelength;
    }
}
