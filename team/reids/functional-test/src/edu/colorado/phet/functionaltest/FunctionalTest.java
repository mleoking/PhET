// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * @author Sam Reid
 */
public class FunctionalTest extends PiccoloPhetApplication {
    public FunctionalTest( PhetApplicationConfig config ) {
        super( config );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "functional-test", FunctionalTest.class );
    }
}