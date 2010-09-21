package edu.colorado.phet.batteryresistorcircuit;

import java.awt.*;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

public class BatteryResistorCircuitApplication extends PhetApplication {
    private BatteryResistorCircuitModule module;

    public BatteryResistorCircuitApplication( PhetApplicationConfig config ) {
        super( config );
        module = new BatteryResistorCircuitModule( config );
        addModule( module );
    }

    public static class BatteryResistorCircuitApplicationConfig extends PhetApplicationConfig {
        public BatteryResistorCircuitApplicationConfig( String[] commandLineArgs ) {
            super( commandLineArgs, "battery-resistor-circuit" );
            super.setLookAndFeel( new PhetLookAndFeel() );
            setFrameSetup( new FrameSetup.CenteredWithSize( BatteryResistorCircuitSimulationPanel.BASE_FRAME_WIDTH, 660 ) );
        }
    }

    private class BatteryResistorCircuitModule extends Module {
        public BatteryResistorCircuitModule( PhetApplicationConfig config ) {
            super( config.getName(), new ConstantDtClock( 30, 1 ) );
            BatteryResistorCircuitSimulationPanel simulationPanel = new BatteryResistorCircuitSimulationPanel( getClock() );
            try {
                simulationPanel.startApplication();
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            catch( FontFormatException e ) {
                e.printStackTrace();
            }
            setSimulationPanel( simulationPanel );
            setClockControlPanel( null );
            setLogoPanelVisible( false );
        }
    }

    public static void main( String[] args ) {

        BatteryResistorCircuitApplicationConfig dApplicationConfig = new BatteryResistorCircuitApplicationConfig( args );
        new PhetApplicationLauncher().launchSim( dApplicationConfig, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new BatteryResistorCircuitApplication( config );
            }
        } );
    }
}
