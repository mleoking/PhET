/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.color;

import java.awt.Color;


/**
 * BlackColorScheme is a color scheme that features a black chart background.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BlackColorScheme extends QTColorScheme {

    public BlackColorScheme() {
        super();
        setChartColor( Color.BLACK );
        setTickColor( Color.BLACK );
        setGridlineColor( new Color( 204, 204, 204 ) ); // lighter gray
        setAnnotationColor( Color.WHITE );
        setRegionMarkerColor( Color.LIGHT_GRAY );
        setTotalEnergyColor( Color.GREEN );
        setPotentialEnergyColor( Color.MAGENTA );
        setRealColor( new Color( 255, 51, 51 ) ); // light red
        setImaginaryColor( new Color( 51, 153, 255 ) ); // bright blue
        setMagnitudeColor( Color.WHITE );
        setProbabilityDensityColor( Color.WHITE );
    }
}
