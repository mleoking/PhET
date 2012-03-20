// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * The "Line Graphing" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGraphingApplication extends PiccoloPhetApplication {

    public LineGraphingApplication( PhetApplicationConfig config ) {
        super( config );
        Frame parentFrame = getPhetFrame();
        addModule( new PiccoloModule( "Intro", new ConstantDtClock( 25 ) ) {{
            setSimulationPanel( new PhetPCanvas() );
        }});
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, LGResources.PROJECT_NAME, LineGraphingApplication.class );
    }
}
