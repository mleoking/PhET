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
 * BSBlackColorScheme is a color scheme that features a black chart background.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSBlackColorScheme extends BSColorScheme {

    public BSBlackColorScheme() {
        super();
        
        setChartColor( Color.BLACK );
        setTickColor( Color.BLACK );
        setGridlineColor( Color.LIGHT_GRAY );
        
        setPotentialEnergyColor( new Color( 178, 25, 205 ) ); // purple
        setEigenstateNormalColor( Color.GREEN );
        setEigenstateHiliteColor( Color.YELLOW );
        setEigenstateSelectionColor( Color.RED );

        setRealColor( new Color( 255, 120, 0 ) ); // pumpkin orange
        setImaginaryColor( new Color( 26, 135, 255 ) ); // bright blue
        setMagnitudeColor( Color.WHITE );
        
        setMagnifyingGlassBezelColor( Color.GRAY );
        setMagnifyingGlassHandleColor( Color.ORANGE );
        
        setDragHandleColor( new Color( 204, 153, 255 ) ); // lighter purple
        setDragHandleHiliteColor( Color.WHITE );
        setDragHandleValueColor( Color.WHITE );
        setDragHandleMarkersColor( Color.WHITE );
    }
}
