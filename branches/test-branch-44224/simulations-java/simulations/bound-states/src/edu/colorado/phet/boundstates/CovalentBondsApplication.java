/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;

/**
 * BSCovalentBondsApplication is the simulation titled "Double Wells and Covalent Bonds".
 * It has only the "Two Wells" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CovalentBondsApplication extends BSAbstractApplication {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public CovalentBondsApplication( PhetApplicationConfig config ) {
        super( config );
    }
    
    //----------------------------------------------------------------------------
    // BSAbstractApplication implementation
    //----------------------------------------------------------------------------
    
    /*
     * Initializes modules.
     */
    protected void initModules() {
        addTwoWellsModule();
        getTwoWellsModule().setHasWiggleMe( true );
    }
    
    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new CovalentBondsApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, BSConstants.PROJECT_NAME, BSConstants.FLAVOR_COVALENT_BONDS );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
