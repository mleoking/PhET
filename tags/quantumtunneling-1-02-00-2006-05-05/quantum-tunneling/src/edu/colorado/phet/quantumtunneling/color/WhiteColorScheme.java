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
 * WhiteColorScheme is a color scheme that features a white chart background.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WhiteColorScheme extends QTColorScheme {

    public WhiteColorScheme() {
        super();
        setChartColor( Color.WHITE );
        setTickColor( Color.BLACK );
        setGridlineColor( Color.DARK_GRAY );
        setAnnotationColor( Color.BLACK );
        setRegionMarkerColor( Color.BLACK );
        setTotalEnergyColor( Color.GREEN );
        setPotentialEnergyColor( new Color( 178, 25, 205 ) ); // purple
        setRealColor( Color.RED );
        setImaginaryColor( Color.BLUE );
        setMagnitudeColor( Color.BLACK );
        setProbabilityDensityColor( Color.BLACK );
    }
}
