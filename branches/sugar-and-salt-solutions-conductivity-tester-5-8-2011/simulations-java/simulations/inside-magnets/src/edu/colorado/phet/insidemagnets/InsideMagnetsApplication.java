// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.insidemagnets;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * @author Sam Reid
 */
public class InsideMagnetsApplication extends PiccoloPhetApplication {
    public InsideMagnetsApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new InsideMagnetsModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "inside-magnets", InsideMagnetsApplication.class );
    }
}
