/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanelWithTimeDisplay;
import edu.colorado.phet.glaciers.GlaciersResources;

/**
 * GlaciersClockControlPanel is the clock control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersClockControlPanel extends ClockControlPanelWithTimeDisplay {

    //----------------------------------------------------------------------------
    // Constuctors
    //----------------------------------------------------------------------------
    
    public GlaciersClockControlPanel( ConstantDtClock clock ) {
        super( clock );
        setUnits( GlaciersResources.getString( "units.time" ) );
    }
}
