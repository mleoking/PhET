// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.linegraphing.common.LGResources;
import edu.colorado.phet.linegraphing.linegame.LineGameModule;
import edu.colorado.phet.linegraphing.pointslope.PointSlopeModule;
import edu.colorado.phet.linegraphing.slopeintercept.SlopeInterceptModule;

/**
 * The "Line Graphing" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGraphingApplication extends PiccoloPhetApplication {

    public LineGraphingApplication( PhetApplicationConfig config ) {
        super( config );

        addModule( new SlopeInterceptModule() );
        addModule( new PointSlopeModule() );
        addModule( new LineGameModule() );

        // Dev: Start with the module number specified on the command line.
        if ( isDeveloperControlsEnabled() ) {
            String startModuleNumber = config.getOptionArg( "-startModule" );
            if ( startModuleNumber != null ) {
                setStartModule( getModule( Integer.valueOf( startModuleNumber ) ) );
            }
        }
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, LGResources.PROJECT_NAME, LineGraphingApplication.class );
    }
}
