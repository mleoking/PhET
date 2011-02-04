// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.lightreflectionandrefraction.modules.intro.IntroModule;

/**
 * @author Sam Reid
 */
public class LightReflectionAndRefractionApplication extends PiccoloPhetApplication {
    private static final String NAME = "light-reflection-and-refraction";
    public static final PhetResources RESOURCES = new PhetResources( NAME );

    public LightReflectionAndRefractionApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new IntroModule() );
//        addModule( new PrismBreakModule() );
//        addModule( new IntroModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, NAME, LightReflectionAndRefractionApplication.class );
    }
}
