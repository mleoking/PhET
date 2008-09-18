package edu.colorado.phet.signalcircuit;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

public class SignalCircuitApplication extends PiccoloPhetApplication {
    private SignalCircuitModule module;

    public SignalCircuitApplication( PhetApplicationConfig config ) {
        super( config );
        module = new SignalCircuitModule( config );
        addModule( module );
    }

    public static class SignalCircuitApplicationConfig extends PhetApplicationConfig {
        public SignalCircuitApplicationConfig( String[] commandLineArgs ) {
            super( commandLineArgs, new FrameSetup.CenteredWithSize( 800, 435 ), new PhetResources( "signal-circuit" ) );
            super.setApplicationConstructor( new ApplicationConstructor() {
                public PhetApplication getApplication( PhetApplicationConfig config ) {
                    return new SignalCircuitApplication( config );
                }
            } );
            super.setLookAndFeel( new PhetLookAndFeel() );
        }
    }

    private class SignalCircuitModule extends Module {
        private SignalCircuitSimulationPanel simulationPanel;

        public SignalCircuitModule( PhetApplicationConfig config ) {
            super( config.getName(), new ConstantDtClock( 22, 0.0216) );
            simulationPanel = new SignalCircuitSimulationPanel( getClock() );
            setSimulationPanel( simulationPanel );
            setClockControlPanel( null );
            setLogoPanelVisible( false );
        }

    }

    public static void main( String[] args ) {
        new SignalCircuitApplicationConfig( args ).launchSim();
    }
}
