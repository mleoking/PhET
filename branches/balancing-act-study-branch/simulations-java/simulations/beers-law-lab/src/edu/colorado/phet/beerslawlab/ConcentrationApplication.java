// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab;

import java.awt.Frame;

import edu.colorado.phet.beerslawlab.beerslaw.BeersLawModule;
import edu.colorado.phet.beerslawlab.common.BLLResources;
import edu.colorado.phet.beerslawlab.concentration.ConcentrationModule;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Main class for the "Concentration" application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationApplication extends PiccoloPhetApplication {

    public ConcentrationApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new ConcentrationModule( getPhetFrame() ) );
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, BLLResources.PROJECT_NAME, "concentration", ConcentrationApplication.class );
    }
}
