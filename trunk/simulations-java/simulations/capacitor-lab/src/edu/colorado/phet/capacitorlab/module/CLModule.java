// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module;

import edu.colorado.phet.capacitorlab.model.CLClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * Base class for all modules in the "Capacitor Lab" sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class CLModule extends PiccoloModule {

    public CLModule( String title ) {
        super( title, new CLClock(), false /* startsPaused */ );
        setLogoPanel( null );
        setClockControlPanel( null );
    }

    public CLClock getCLClock() {
        return (CLClock)getClock();
    }

    public abstract void setEFieldShapesDebugEnabled( boolean enabled );

    public abstract void setVoltageShapesDebugEnabled( boolean enabled );
}
