package edu.colorado.phet.motion2d;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

public class Motion2DApplication extends PiccoloPhetApplication {

    public Motion2DApplication( PhetApplicationConfig config ) {
        super( config );
        Motion2DModule module = new Motion2DModule( config );
        addModule( module );
    }

    public static class Motion2DApplicationConfig extends PhetApplicationConfig {
        public Motion2DApplicationConfig( String[] commandLineArgs ) {
            super( commandLineArgs, new ApplicationConstructor() {
                public PhetApplication getApplication( PhetApplicationConfig config ) {
                    return new Motion2DApplication( config );
                }
            },"motion-2d");
            setFrameSetup( new FrameSetup.CenteredWithSize( 850, 600 ) );
        }
    }

    private class Motion2DModule extends Module {

        public Motion2DModule( PhetApplicationConfig config ) {
            super( config.getName(), new ConstantDtClock( 20, 0.021 ) );
            Motion2DSimulationPanel simulationPanel = new Motion2DSimulationPanel( (ConstantDtClock) getClock() );
            try {
                simulationPanel.init();
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            setSimulationPanel( simulationPanel );
            setClockControlPanel( null );
            setLogoPanelVisible( false );
        }

    }

    public static void main( String[] args ) {
        new Motion2DApplicationConfig( args ).launchSim();
    }
}
