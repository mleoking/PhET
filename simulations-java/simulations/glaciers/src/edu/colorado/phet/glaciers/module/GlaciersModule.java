/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.module;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * GlaciersModule is the base class for all modules in the Glaciers sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GlaciersModule extends PiccoloModule {

    public GlaciersModule( String title, IClock clock ) {
        super( title, clock );
        
        // we won't be using any of the standard subpanels
        setMonitorPanel( null );
        setSimulationPanel( null );
        setClockControlPanel( null );
        setLogoPanel( null );
        setControlPanel( null );
        setHelpPanel( null );
    }
}
