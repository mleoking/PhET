package edu.colorado.phet.ohm1d;

import java.awt.*;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

public class Ohm1DApplication extends PhetApplication {
    private Ohm1DModule module;

    public Ohm1DApplication( PhetApplicationConfig config ) {
        super( config );
        module = new Ohm1DModule( config );
        addModule( module );
    }

    public static class Ohm1DApplicationConfig extends PhetApplicationConfig {
        public Ohm1DApplicationConfig( String[] commandLineArgs ) {
            super( commandLineArgs, new FrameSetup.CenteredWithSize( Ohm1DSimulationPanel.BASE_FRAME_WIDTH, 660 ), new PhetResources( "ohm-1d" ) );
            super.setApplicationConstructor( new ApplicationConstructor() {
                public PhetApplication getApplication( PhetApplicationConfig config ) {
                    return new Ohm1DApplication( config );
                }
            } );
            super.setLookAndFeel( new PhetLookAndFeel() );
        }
    }

    private class Ohm1DModule extends Module {
        public Ohm1DModule( PhetApplicationConfig config ) {
            super( config.getName(), new ConstantDtClock( 30, 1 ) );
            Ohm1DSimulationPanel simulationPanel = new Ohm1DSimulationPanel( getClock() );
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
        new Ohm1DApplicationConfig( args ).launchSim();
    }
}
