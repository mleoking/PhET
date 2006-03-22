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
        setGridlineColor( Color.DARK_GRAY );
        setAnnotationColor( Color.WHITE );
        setRegionMarkerColor( Color.LIGHT_GRAY );
        setTotalEnergyColor( Color.GREEN );
        setPotentialEnergyColor( new Color( 178, 25, 205 ) ); // purple
        setRealColor( Color.RED );
        setImaginaryColor( new Color( 26, 135, 255 ) ); // bright blue
        setMagnitudeColor( Color.WHITE );
        setProbabilityDensityColor( Color.WHITE );
    }
}
