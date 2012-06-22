// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildafraction;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fractionsintro.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;

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
        new PhetApplicationLauncher().launchSim( args, "fractions", "fraction-matcher", BuildAFractionApplication.class );
    }
}