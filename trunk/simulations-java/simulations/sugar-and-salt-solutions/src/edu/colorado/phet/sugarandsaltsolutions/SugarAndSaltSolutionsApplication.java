// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsApplication extends PiccoloPhetApplication {
    private static final String NAME = "sugar-and-salt-solutions";
    public static final PhetResources RESOURCES = new PhetResources( NAME );

    public SugarAndSaltSolutionsApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new IntroModule() );
        addModule( new MicroscopicModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, NAME, SugarAndSaltSolutionsApplication.class );
    }
}
