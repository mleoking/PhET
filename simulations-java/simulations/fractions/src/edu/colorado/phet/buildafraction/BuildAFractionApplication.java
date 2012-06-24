// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildafraction;

import edu.colorado.phet.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * "Fractions Intro" PhET Application
 *
 * @author Sam Reid
 */
public class BuildAFractionApplication extends PiccoloPhetApplication {

    public BuildAFractionApplication( PhetApplicationConfig config ) {
        super( config );

        addModule( new BuildAFractionModule( new BuildAFractionModel( true ), config.isDev() ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", "build-a-fraction", BuildAFractionApplication.class );
    }
}