/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module;

import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * Base class for all modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class ABSModule extends PiccoloModule {

    public ABSModule( String name ) {
        super( name, new ABSClock() );
        
        setLogoPanelVisible( false );
        
        // No clock controls
        setClockControlPanel( null );
    }
}
