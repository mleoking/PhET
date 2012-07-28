// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;

/**
 * "Fractions Intro" PhET Application
 *
 * @author Sam Reid
 */
public class BuildAFractionApplication extends PiccoloPhetApplication {

    public BuildAFractionApplication( PhetApplicationConfig config ) {
        super( config );

        addModule( new BuildAFractionModule( new BuildAFractionModel(), config.isDev() ) );
        addModule( new BuildAMixedFractionModule( new BuildAFractionModel(), config.isDev() ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", "build-a-fraction", BuildAFractionApplication.class );
    }
}