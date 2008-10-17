package edu.colorado.phet.signalcircuit;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

public class SignalCircuitApplication extends PiccoloPhetApplication {

    public SignalCircuitApplication( PhetApplicationConfig config ) {
        super( config );
        SignalCircuitModule module = new SignalCircuitModule( config );
        addModule( module );
    }

    public static class SignalCircuitApplicationConfig extends PhetApplicationConfig {
        public SignalCircuitApplicationConfig( String[] commandLineArgs ) {
            super( commandLineArgs, "signal-circuit" );
        }
    }

    private class SignalCircuitModule extends Module {

        public SignalCircuitModule( PhetApplicationConfig config ) {
            super( config.getName(), new ConstantDtClock( 22, 0.0216 ) );
            SignalCircuitSimulationPanel simulationPanel = new SignalCircuitSimulationPanel( getClock() );
            setSimulationPanel( simulationPanel );
            setClockControlPanel( null );
            setLogoPanelVisible( false );
        }

    }

    public static void main( String[] args ) {
        SignalCircuitApplicationConfig applicationConfig = new SignalCircuitApplicationConfig( args );
        new PhetApplicationLauncher().launchSim( applicationConfig, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new SignalCircuitApplication( config );
            }
        } );
    }
}
