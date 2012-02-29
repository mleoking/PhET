// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.equalitylab;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Main class for testing Equality Lab module
 *
 * @author Sam Reid
 */
public class EqualityLabApplication extends PiccoloPhetApplication {
    public EqualityLabApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new EqualityLabModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", "fractions-intro", EqualityLabApplication.class );
    }
}