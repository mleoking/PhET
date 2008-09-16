package edu.colorado.phet.efield;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

public class EFieldApplication extends PhetApplication {
    private EFieldModule module;

    public EFieldApplication( PhetApplicationConfig config ) {
        super( config );
        module = new EFieldModule( config );
        addModule( module );
        getPhetFrame().addMenu( module.getMenu() );
    }

    public static class EFieldApplicationConfig extends PhetApplicationConfig {
        public EFieldApplicationConfig( String[] commandLineArgs ) {
            super( commandLineArgs, new FrameSetup.CenteredWithSize( 600, 600 ), new PhetResources( "efield" ) );
            super.setApplicationConstructor( new ApplicationConstructor() {
                public PhetApplication getApplication( PhetApplicationConfig config ) {
                    return new EFieldApplication( config );
                }
            } );
            super.setLookAndFeel( new PhetLookAndFeel() );
        }
    }

    private class EFieldModule extends Module {
        private EFieldSimulationPanel simulationPanel = new EFieldSimulationPanel( getClock() );

        public EFieldModule( PhetApplicationConfig config ) {
            super( config.getName(), new ConstantDtClock( 35, 0.15 ) );
            simulationPanel.init();
            setSimulationPanel( simulationPanel );
            setClockControlPanel( null );
            setLogoPanelVisible( false );
        }

        public JMenu getMenu() {
            return simulationPanel.getMenu();
        }
    }

    public static void main( String[] args ) {
        new EFieldApplicationConfig( args ).launchSim();
    }
}
