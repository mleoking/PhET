// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.advancedacidbasesolutions.module;

import edu.colorado.phet.advancedacidbasesolutions.model.AABSClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * Base class for all modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AABSAbstractModule extends PiccoloModule {

    public AABSAbstractModule( String name, AABSClock clock ) {
        super( name, clock );
        
        setLogoPanelVisible( false );
        
        // No control panel
        setControlPanel( null );
        
        // No clock controls
        setClockControlPanel( null );
    }
}
