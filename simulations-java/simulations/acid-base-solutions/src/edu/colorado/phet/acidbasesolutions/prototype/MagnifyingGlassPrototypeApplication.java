/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Main application for the Magnifying Glass prototype.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MagnifyingGlassPrototypeApplication extends PiccoloPhetApplication {

    public MagnifyingGlassPrototypeApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new MGPModule( getPhetFrame() ) );
    }

    public static void main( final String[] args ) throws ClassNotFoundException {
        new PhetApplicationLauncher().launchSim( args, ABSConstants.PROJECT_NAME, ABSConstants.FLAVOR_MAGNIFYING_GLASS_PROTOTYPE, MagnifyingGlassPrototypeApplication.class );
    }
    
}
