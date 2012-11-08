// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing;

import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.linegraphing.common.LGResources;
import edu.colorado.phet.linegraphing.dev.DevTestGameRewardMenuItem;
import edu.colorado.phet.linegraphing.linegame.LineGameModule;
import edu.colorado.phet.linegraphing.pointslope.PointSlopeModule;
import edu.colorado.phet.linegraphing.slope.SlopeModule;
import edu.colorado.phet.linegraphing.slopeintercept.SlopeInterceptModule;

/**
 * The "Graphing: Lines" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GraphingLinesApplication extends PiccoloPhetApplication {

    public GraphingLinesApplication( PhetApplicationConfig config ) {
        super( config );

        addModule( new SlopeModule() );
        addModule( new SlopeInterceptModule() );
        addModule( new PointSlopeModule() );
        addModule( new LineGameModule() );

        // Developer menu
        JMenu developerMenu = getPhetFrame().getDeveloperMenu();
        developerMenu.add( new DevTestGameRewardMenuItem() );
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, LGResources.PROJECT_NAME, "graphing-lines", GraphingLinesApplication.class );
    }
}
