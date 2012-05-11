// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.functions;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

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
        addModule( new FunctionModule( "Intro" ) );
        addModule( new FunctionModule( "Multiple Inputs" ) );
        addModule( new FunctionModule( "Build a Function" ) );
        addModule( new FunctionModule( "Game Maker" ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "functions", "functions", FunctionsApplication.class );
    }
}