package edu.colorado.phet.efield;

import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

public class EFieldApplication extends PhetApplication {
    private EFieldModule module;

    public EFieldApplication( PhetApplicationConfig config ) {
        super( config );
        module = new EFieldModule( config );
        addModule( module );
        getPhetFrame().addMenu( module.getMenu() );
    }

    private class EFieldModule extends Module {
        private EFieldSimulationPanel simulationPanel = new EFieldSimulationPanel( getClock() );

        public EFieldModule( PhetApplicationConfig config ) {
            super( config.getTitle(), new ConstantDtClock( 35, 0.15 ) );
            simulationPanel.init();
            setSimulationPanel( simulationPanel );
            setClockControlPanel( null );
            setLogoPanelVisible( false );
        }

        public JMenu getMenu() {
            return simulationPanel.getMenu();
        }
    }

    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new EFieldApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, appConstructor, "efield" );
        appConfig.setFrameSetup( new FrameSetup.CenteredWithSize( 600, 600 ) );
        appConfig.launchSim();
    }
}
