/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view.atom;

import java.awt.Color;

import edu.colorado.phet.hydrogenatom.model.DeBroglieModel;

/**
 * DeBroglieBrightnessMagnitudeNode represents the deBroglie model
 * as a standing wave. The magnitude of the amplitude of the standing
 * wave is represented by the brightness of color in a ring that 
 * is positioned at the electron's orbit.
 * <p>
 * Note that this representation is identical to DeBroglieBrightnessNode,
 * except that we use the magnitude of the amplitude (instead of the 
 * actual value of the amplitude) to determine color.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
class DeBroglieBrightnessMagnitudeNode extends DeBroglieBrightnessNode {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeBroglieBrightnessMagnitudeNode( DeBroglieModel atom ) {
        super( atom );
    }
    
    //----------------------------------------------------------------------------
    // DeBroglieBrightnessNode overrides
    //----------------------------------------------------------------------------
    
    /**
     * Maps the magnitude of the specified amplitude to a color.
     * @param amplitude
     * @return Color
     */
    protected Color amplitudeToColor( double amplitude ) {
        assert( amplitude >= -1 && amplitude <= 1 );
        double magnitude = Math.abs( amplitude );
        double f = 0.7;
        int r = (int)( ( f * 255 ) - ( f * 255 * magnitude ) );
        int g = (int)( ( f * 255 ) - ( f * 255 * magnitude ) );
        int b = 255;
        Color color = new Color( r, g, b );
        return color;
    }

}
