package edu.colorado.phet.mazegame;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * User: Sam Reid
 * Date: Sep 11, 2006
 * Time: 10:53:32 AM
 */

public class MazeGameApplication extends PiccoloPhetApplication {

    private MazeGameModule module;

    public MazeGameApplication( PhetApplicationConfig config ) {
        super( config );
        module = new MazeGameModule( config );
        addModule( module );
    }

    public static class MazeGameApplicationConfig extends PhetApplicationConfig {
        public MazeGameApplicationConfig( String[] commandLineArgs ) {
            super( commandLineArgs, new ApplicationConstructor() {
                public PhetApplication getApplication( PhetApplicationConfig config ) {
                    return new MazeGameApplication( config );
                }
            }, "maze-game"  );
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
        new MazeGameApplicationConfig( args ).launchSim();
    }

}
