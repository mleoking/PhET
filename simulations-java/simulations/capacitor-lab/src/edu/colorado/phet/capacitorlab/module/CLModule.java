/* Copyright 2010, University of Colorado */


package edu.colorado.phet.capacitorlab.module;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * Base class for all modules in the "Capacitor Lab" sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class CLModule extends PiccoloModule {
    
    public CLModule( String title, IClock clock ) {
        super( title, clock, false /* startsPaused */ );
        setLogoPanel( null );
    }
}
