// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;

/**
 * Base class for sugar and salt solutions modules.
 *
 * @author Sam Reid
 */
public abstract class SugarAndSaltSolutionsModule extends Module {

    public SugarAndSaltSolutionsModule( String name, Clock clock, BooleanProperty moduleActive ) {
        super( name, clock );

        //Clock control panel will be shown floating in the simulation panel, so don't show the top level swing component for the clock control panel
        setClockControlPanel( null );

        //Don't show the logo panel--since the sim is multi-tab, the logo should be shown in the tab tray at the far right
        setLogoPanel( null );

        //When the module becomes activated/deactivated, update the flag in the model for purposes of starting and stopping the clock
        listenForModuleActivated( moduleActive );
    }

    public void listenForModuleActivated( final BooleanProperty moduleActive ) {

        //Keep a boolean flag for whether this module is active so subclasses can pause their clocks when not enabled (for performance reasons and so it is in the same place you left it)
        addListener( new Listener() {
            public void activated() {
                moduleActive.set( true );
            }

            public void deactivated() {
                moduleActive.set( false );
            }
        } );
    }
}