// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.dilutions.dilution.DilutionModule;
import edu.colorado.phet.dilutions.molarity.MolarityModule;

/**
 * Main class for the "Dilutions" application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionsApplication extends PiccoloPhetApplication {

    public DilutionsApplication( PhetApplicationConfig config ) {
        super( config );
        Frame frame = getPhetFrame();
        addModule( new MolarityModule( frame ) );
        addModule( new DilutionModule( frame ) );
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, DilutionsResources.PROJECT_NAME, DilutionsResources.DILUTIONS_FLAVOR, DilutionsApplication.class );
    }
}
