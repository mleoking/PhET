// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.functionaltest.functional2.Functional2Module;

/**
 * @author Sam Reid
 */
public class FunctionalTest extends PiccoloPhetApplication {
    public FunctionalTest( PhetApplicationConfig config ) {
        super( config );
//        addModule( new FunctionalModule() );
//        addModule( new ImperativeModule() );
        addModule( new Functional2Module() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "functional-test", FunctionalTest.class );
    }
}