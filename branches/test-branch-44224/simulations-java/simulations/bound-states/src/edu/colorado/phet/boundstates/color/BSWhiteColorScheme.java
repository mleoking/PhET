/* Copyright 2006, University of Colorado */

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
        
        setPotentialEnergyColor( new Color( 178, 25, 205 ) ); // purple
        setEigenstateNormalColor( new Color( 51, 255, 0 ) ); // bright green
        setEigenstateHiliteColor( new Color( 102, 102, 102 ) ); // gray
        setEigenstateSelectionColor( new Color( 0, 102, 51 ) ); // dark green

        setRealColor( Color.RED );
        setImaginaryColor( Color.BLUE );
        setMagnitudeColor( Color.BLACK );
        
        setMagnifyingGlassBezelColor( Color.GRAY );
        setMagnifyingGlassHandleColor( Color.ORANGE );
        
        setDragHandleColor( new Color( 204, 153, 255 ) ); // lighter purple
        setDragHandleHiliteColor( Color.WHITE );
        setDragHandleValueColor( Color.BLACK );
        setDragHandleMarkersColor( Color.BLACK );
    }
}
