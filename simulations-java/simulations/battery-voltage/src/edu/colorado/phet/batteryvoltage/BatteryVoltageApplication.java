package edu.colorado.phet.batteryvoltage;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

public class BatteryVoltageApplication extends PiccoloPhetApplication {

    public BatteryVoltageApplication( PhetApplicationConfig config ) {
        super( config );
        BatteryVoltageModule module = new BatteryVoltageModule( config );
        addModule( module );
    }

    private class BatteryVoltageModule extends Module {

        public BatteryVoltageModule( PhetApplicationConfig config ) {
            super( config.getName(), new ConstantDtClock( 20, 0.021 ) );
            BatteryVoltageSimulationPanel simulationPanel = new BatteryVoltageSimulationPanel( getClock() );
            setSimulationPanel( simulationPanel );
            setClockControlPanel( null );
            setLogoPanelVisible( false );
        }

    }

    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new BatteryVoltageApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, appConstructor, "battery-voltage" );
        appConfig.setFrameSetup( new FrameSetup.CenteredWithSize( 850, 525 ) );
        appConfig.launchSim();
    }
}
