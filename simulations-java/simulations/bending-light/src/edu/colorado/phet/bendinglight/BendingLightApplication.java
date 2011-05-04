// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight;

import edu.colorado.phet.bendinglight.modules.intro.IntroModule;
import edu.colorado.phet.bendinglight.modules.moretools.MoreToolsModule;
import edu.colorado.phet.bendinglight.modules.prisms.PrismsModule;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Application for "bending light"
 *
 * @author Sam Reid
 */
public class BendingLightApplication extends PiccoloPhetApplication {
    private static final String NAME = "bending-light";//Internal name used for loading the sim and its resources
    public static final PhetResources RESOURCES = new PhetResources( NAME );

    public BendingLightApplication( PhetApplicationConfig config ) {
        super( config );

        //Add the modules
        addModule( new IntroModule() );
        addModule( new PrismsModule() );
        addModule( new MoreToolsModule() );
    }

    //Launch main for Bending Light
    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, NAME, BendingLightApplication.class );
    }
}
