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
import edu.colorado.phet.hydrogenatom.util.ColorUtils;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;

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
public class DeBroglieBrightnessMagnitudeNode extends DeBroglieBrightnessNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // color used when amplitude = 1
    public static Color MAX_COLOR = ElectronNode.getColor();
    // color used when amplitude = 0
    public static Color MIN_COLOR = Color.BLACK;
    
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
        Color color = ColorUtils.interpolateRBGA( MIN_COLOR, MAX_COLOR, magnitude );
        return color;
    }

}
