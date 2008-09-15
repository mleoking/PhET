package edu.colorado.phet.electrichockey;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

public class ElectricHockeyApplication2 extends PhetApplication {
    private ElectricHockeyModule module;

    public ElectricHockeyApplication2( PhetApplicationConfig config ) {
        super( config );
        module = new ElectricHockeyModule( config );
        addModule( module );
    }

    public static class ElectricHockeyApplicationConfig extends PhetApplicationConfig {
        public ElectricHockeyApplicationConfig( String[] commandLineArgs ) {
            super( commandLineArgs, new FrameSetup.CenteredWithSize( 800, 750 ), new PhetResources( "electric-hockey" ) );
            super.setApplicationConstructor( new ApplicationConstructor() {
                public PhetApplication getApplication( PhetApplicationConfig config ) {
                    return new ElectricHockeyApplication2( config );
                }
            } );
            super.setLookAndFeel( new PhetLookAndFeel() );
        }
    }

    private class ElectricHockeyModule extends Module {

        public ElectricHockeyModule( PhetApplicationConfig config ) {
            super( config.getName(), new ConstantDtClock( 30, 1 ) );
            ElectricHockeyApplication balloonsSimulationPanel = new ElectricHockeyApplication();
            balloonsSimulationPanel.init();
            setSimulationPanel( balloonsSimulationPanel );
            setClockControlPanel( null );
            setLogoPanelVisible( false );
        }
    }

    public static void main( String[] args ) {
        new ElectricHockeyApplicationConfig( args ).launchSim();
    }
}
