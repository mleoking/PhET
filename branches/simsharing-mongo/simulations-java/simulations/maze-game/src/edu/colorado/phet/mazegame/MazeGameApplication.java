// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.mazegame;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * User: Sam Reid
 * Date: Sep 11, 2006
 * Time: 10:53:32 AM
 */

public class MazeGameApplication extends PiccoloPhetApplication {

    public MazeGameApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new MazeGameModule( config ) );
    }

    public static class MazeGameApplicationConfig extends PhetApplicationConfig {
        public MazeGameApplicationConfig( String[] commandLineArgs ) {
            super( commandLineArgs, "maze-game" );
            setFrameSetup( new FrameSetup.CenteredWithSize( 700, 600 ) );
        }
    }

    private class MazeGameModule extends PiccoloModule {
        private MazeGameSimulationPanel simulationPanel = new MazeGameSimulationPanel();

        public MazeGameModule( PhetApplicationConfig config ) {
            super( config.getName(), new ConstantDtClock( 35, 0.15 ) );
            simulationPanel.init();
            setSimulationPanel( simulationPanel );
            setClockControlPanel( null );
            setLogoPanelVisible( false );
        }
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( new MazeGameApplicationConfig( args ), new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new MazeGameApplication( config );
            }
        } );
    }

}
