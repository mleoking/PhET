// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balancingchemicalequations;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;


/**
 * Base class for all modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class BCEModule extends PiccoloModule {

    public BCEModule( String title, IClock clock, boolean startsPaused ) {
        super( title, clock, startsPaused );
        setLogoPanel( null ); // workaround for #2015
        setControlPanel( null );
        setClockControlPanel( null );
    }
}
