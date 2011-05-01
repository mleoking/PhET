// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Base class for sugar and salt solutions modules.
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsModule extends Module {
    public SugarAndSaltSolutionsModule( String name ) {
        super( name, new ConstantDtClock() );

        //Clock control panel will be shown floating in the simulation panel, so don't show the top level swing component for the clock control panel
        setClockControlPanel( null );

        //Don't show the logo panel--since the sim is multi-tab, the logo should be shown in the tab tray at the far right
        getModulePanel().setLogoPanel( null );
    }
}
