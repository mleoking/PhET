// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab;

import java.awt.Frame;

import edu.colorado.phet.beerslawlab.beerslaw.BeersLawModule;
import edu.colorado.phet.beerslawlab.common.BLLResources;
import edu.colorado.phet.beerslawlab.concentration.ConcentrationModule;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.umd.cs.piccolo.util.PDebug;

/**
 * Main class for the "Beer's Law Lab" application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeersLawLabApplication extends PiccoloPhetApplication {

    public BeersLawLabApplication( PhetApplicationConfig config ) {
        super( config );

        Frame parentFrame = getPhetFrame();
        addModule( new ConcentrationModule( parentFrame ) );
        addModule( new BeersLawModule( parentFrame ) );

        // Dev: Start with the module number specified on the command line.
        if ( isDeveloperControlsEnabled() ) {
            String startModuleNumber = config.getOptionArg( "-startModule" );
            if ( startModuleNumber != null ) {
                setStartModule( getModule( Integer.valueOf( startModuleNumber ) ) );
            }
        }
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, BLLResources.PROJECT_NAME, "beers-law-lab", BeersLawLabApplication.class );
    }
}
