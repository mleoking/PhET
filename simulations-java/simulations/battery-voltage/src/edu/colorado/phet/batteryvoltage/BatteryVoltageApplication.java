package edu.colorado.phet.batteryvoltage;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

public class BatteryVoltageApplication extends PiccoloPhetApplication {

    public BatteryVoltageApplication( PhetApplicationConfig config ) {
        super( config );
        BatteryVoltageModule module = new BatteryVoltageModule( config );
        addModule( module );
    }

    public static class BatteryVoltageApplicationConfig extends PhetApplicationConfig {
        public BatteryVoltageApplicationConfig( String[] commandLineArgs ) {
            super( commandLineArgs, new FrameSetup.CenteredWithSize( 850, 525 ), new PhetResources( "battery-voltage" ) );
            super.setApplicationConstructor( new ApplicationConstructor() {
                public PhetApplication getApplication( PhetApplicationConfig config ) {
                    return new BatteryVoltageApplication( config );
                }
            } );
            super.setLookAndFeel( new PhetLookAndFeel() );
        }
    }

    private class BatteryVoltageModule extends Module {

        public BatteryVoltageModule( PhetApplicationConfig config ) {
            super( config.getName(), new ConstantDtClock( 22, 0.0216 ) );
            BatteryVoltageSimulationPanel simulationPanel = new BatteryVoltageSimulationPanel( getClock() );
            setSimulationPanel( simulationPanel );
            setClockControlPanel( null );
            setLogoPanelVisible( false );
        }

    }

    public static void main( String[] args ) {
        new BatteryVoltageApplicationConfig( args ).launchSim();
    }
}
