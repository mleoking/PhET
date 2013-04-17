// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.module;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;


/**
 * OTAbstractModule is the base class for all modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class OTAbstractModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param title the module title
     * @param clock the simulation clock
     */
    public OTAbstractModule( String title, IClock clock ) {
        super( title, clock );
        setLogoPanel( null );
    }
}
