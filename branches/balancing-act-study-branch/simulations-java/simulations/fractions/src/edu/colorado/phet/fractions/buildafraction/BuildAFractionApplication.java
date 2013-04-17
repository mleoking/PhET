// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.model.MixedNumbersModel;

/**
 * "Build a Fraction" PhET Application
 *
 * @author Sam Reid
 */
public class BuildAFractionApplication extends PiccoloPhetApplication {

    public BuildAFractionApplication( PhetApplicationConfig config ) {
        super( config );

        //Disable the collection boxes unless the user has created a match in that level or collected a match anywhere in the sim (any tab)
        BooleanProperty collectedMatch = new BooleanProperty( false );
        BooleanProperty audioEnabled = new BooleanProperty( true );
        addModule( new BuildAFractionModule( new BuildAFractionModel( collectedMatch, audioEnabled ) ) );
        addModule( new MixedNumbersModule( new MixedNumbersModel( collectedMatch, audioEnabled ) ) );
        addModule( new FractionLabModule( false ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", "build-a-fraction", BuildAFractionApplication.class );
    }
}