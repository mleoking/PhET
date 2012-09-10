// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.model.BuildAMixedFractionModel;

/**
 * "Fractions Intro" PhET Application
 *
 * @author Sam Reid
 */
public class BuildAFractionApplication extends PiccoloPhetApplication {

    public BuildAFractionApplication( PhetApplicationConfig config ) {
        super( config );
        BooleanProperty userCreatedMatch = new BooleanProperty( false );
        addModule( new BuildAFractionModule( new BuildAFractionModel( userCreatedMatch ) ) );
        addModule( new BuildAMixedFractionModule( new BuildAMixedFractionModel( userCreatedMatch ) ) );
        addModule( new FreePlayModule( new BuildAMixedFractionModel( userCreatedMatch ) ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", "build-a-fraction", BuildAFractionApplication.class );
    }
}