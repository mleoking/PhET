/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * OTAbstractApplication is the base class for all flavors of this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class OTAbstractApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param args command line arguments
     */
    public OTAbstractApplication( PhetApplicationConfig config )
    {
        super( config );
        initModules();
        initMenubar( config.getCommandLineArgs() );
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the modules.
     */
    protected abstract void initModules();

    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {

        // File->Save/Load
        final PhetFrame frame = getPhetFrame();
        frame.addFileSaveLoadMenuItems();
    }
}
