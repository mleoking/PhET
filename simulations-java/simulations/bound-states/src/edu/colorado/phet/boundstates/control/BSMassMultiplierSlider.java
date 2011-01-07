// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.control;

import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;


/**
 * BSMassMultiplierSlider is the slider used to see the mass multiplier.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSMassMultiplierSlider extends LinearValueControl {

    private static final String MIN_MAX_PATTERN = "<html>{0}m<sub>e</sub></html>";
        
    /**
     * Constructor.
     */
    public BSMassMultiplierSlider( double min, double max, String label, String valuePattern, String units ) 
    {
        super( min, max, label, valuePattern, units );

        // Reformat the min/max tick labels
        DecimalFormat format = new DecimalFormat( valuePattern );
        Object[] minArgs = { format.format( min ) };
        String minTickString = MessageFormat.format( MIN_MAX_PATTERN, minArgs );
        Object[] maxArgs = { format.format( max ) };
        String maxTickString = MessageFormat.format( MIN_MAX_PATTERN, maxArgs );
        addTickLabel( min, minTickString );
        addTickLabel( max, maxTickString );
    }
}
