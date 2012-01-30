// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.molarity;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.molarity.molarity.MolarityModule;

/**
 * Main class for the "Molarity" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolarityApplication extends PiccoloPhetApplication {

    public MolarityApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new MolarityModule( getPhetFrame() ) );
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, MolarityResources.PROJECT_NAME, MolarityApplication.class );
    }
}
