/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.color;

import java.awt.Color;


/**
 * BSWhiteColorScheme is a color scheme that features a white chart background.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSWhiteColorScheme extends BSColorScheme {

    public BSWhiteColorScheme() {
        super();
        setChartColor( Color.WHITE );
        setTickColor( Color.BLACK );
        setGridlineColor( Color.DARK_GRAY );
        setEigenstateNormalColor( new Color( 0, 108, 0 ) ); // dark green
        setEigenstateHiliteColor( Color.LIGHT_GRAY );
        setEigenstateSelectionColor( Color.GREEN );
        setPotentialEnergyColor( new Color( 178, 25, 205 ) ); // purple
        setRealColor( Color.RED );
        setImaginaryColor( Color.BLUE );
        setMagnitudeColor( Color.BLACK );
        setMagnifyingGlassBezelColor( Color.GRAY );
        setMagnifyingGlassHandleColor( Color.ORANGE );
    }
}
