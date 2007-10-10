/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;


/**
 * GlaciersAbstractModule is the base class for all modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GlaciersAbstractModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param title the module title
     * @param clock the simulation clock
     */
    public GlaciersAbstractModule( String title, IClock clock ) {
        super( title, clock );
        setLogoPanel( null );
    }
}
