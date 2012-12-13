// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.FractionLabModule;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.fractionsintro.intro.FractionsIntroModule;

/**
 * Research application, requested by Karina.
 *
 * @author Sam Reid
 */
public class ResearchStudy2013Application extends PiccoloPhetApplication {

    //Global flag for whether this functionality should be enabled
    public static boolean recordRegressionData;

    public ResearchStudy2013Application( PhetApplicationConfig config ) {
        super( config );

        //Another way to do this would be to pass a FunctionInvoker to all the modules
        recordRegressionData = config.hasCommandLineArg( "-recordRegressionData" );
        addModule( new FractionsIntroModule() );
        final BooleanProperty audioEnabled = new BooleanProperty( true );
        addModule( new BuildAFractionModule( new BuildAFractionModel( new BooleanProperty( false ), audioEnabled ) ) );
        addModule( new FractionLabModule( true ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", "fractions-intro", ResearchStudy2013Application.class );
    }
}