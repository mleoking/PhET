// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.functions;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.functions.buildafunction.BuildAFunctionModule;
import edu.colorado.phet.functions.intro.IntroModule;

/**
 * "Functions" PhET Application
 * <p/>
 * Right now this is just a stub project to help generate some mock-ups
 *
 * @author Sam Reid
 */
public class FunctionsApplication extends PiccoloPhetApplication {

    public FunctionsApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new IntroModule() );
        addModule( new FunctionModule( "Two Inputs" ) );
        addModule( new BuildAFunctionModule() );
        addModule( new FunctionModule( "Game Maker" ) );
    }

    //Utility method for testing a single module
    public static void runModule( String[] args, final Module module ) {
        final ApplicationConstructor constructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig c ) {
                return new PhetApplication( c ) {{addModule( module );}};
            }
        };
        new PhetApplicationLauncher().launchSim( args, "functions", "functions", constructor );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "functions", "functions", FunctionsApplication.class );
    }
}