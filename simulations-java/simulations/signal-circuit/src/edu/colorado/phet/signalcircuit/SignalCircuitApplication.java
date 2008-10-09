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

    public SignalCircuitApplication( PhetApplicationConfig config ) {
        super( config );
        SignalCircuitModule module = new SignalCircuitModule( config );
        addModule( module );
    }

    public static class SignalCircuitApplicationConfig extends PhetApplicationConfig {
        public SignalCircuitApplicationConfig( String[] commandLineArgs ) {
            super(commandLineArgs, new ApplicationConstructor() {
                public PhetApplication getApplication( PhetApplicationConfig config ) {
                    return new SignalCircuitApplication( config );
                }
            },"signal-circuit");
        }
    }

    private class SignalCircuitModule extends Module {

        public SignalCircuitModule( PhetApplicationConfig config ) {
            super( config.getName(), new ConstantDtClock( 22, 0.0216) );
            SignalCircuitSimulationPanel simulationPanel = new SignalCircuitSimulationPanel( getClock() );
            setSimulationPanel( simulationPanel );
            setClockControlPanel( null );
            setLogoPanelVisible( false );
        }

    }

    public static void main( String[] args ) {
        new SignalCircuitApplicationConfig( args ).launchSim();
    }
}
