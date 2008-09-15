package edu.colorado.phet.balloons;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

public class BalloonsApplication2 extends PiccoloPhetApplication {
    private BalloonsModule module;

    public BalloonsApplication2( PhetApplicationConfig config ) {
        super( config );
        module = new BalloonsModule( config );
        addModule( module );
        new BalloonsFrameSetup().initialize( getPhetFrame() );//must init after control panel size height is determined
    }

    public static class BalloonsFrameSetup implements FrameSetup {
        public void initialize( JFrame frame ) {
            BalloonsFrame f = (BalloonsFrame) frame;
            frame.setSize( BalloonsSimulationPanel.PANEL_WIDTH, BalloonsSimulationPanel.PANEL_HEIGHT + f.getControlPanelHeight() + 10 );
            SwingUtils.centerWindowOnScreen( frame );
        }
    }

    protected PhetFrame createPhetFrame() {
        return new BalloonsFrame( this );
    }

    static class BalloonsFrame extends PhetFrame {
        private BalloonsApplication2 application;

        public BalloonsFrame( final BalloonsApplication2 application ) throws HeadlessException {
            super( application );
            this.application = application;
        }

        public int getControlPanelHeight() {
            return application.getControlPanelHeight();
        }
    }

    private int getControlPanelHeight() {
        if ( module == null ) {
            return 0;
        }
        else {
            return module.getControlPanelHeight();
        }
    }

    public static class BalloonsApplicationConfig extends PhetApplicationConfig {
        public BalloonsApplicationConfig( String[] commandLineArgs ) {
            super( commandLineArgs, new BalloonsFrameSetup(), new PhetResources( "balloons" ) );
            super.setApplicationConstructor( new ApplicationConstructor() {
                public PhetApplication getApplication( PhetApplicationConfig config ) {
                    return new BalloonsApplication2( config );
                }
            } );
            PhetLookAndFeel laf = new PhetLookAndFeel();
            laf.setBackgroundColor( new Color( 200, 240, 200 ) );
            super.setLookAndFeel( laf );
        }
    }

    public static void main( String[] args ) {
        new BalloonsApplicationConfig( args ).launchSim();
    }

    private class BalloonsModule extends Module {
        private BalloonsSimulationPanel balloonsSimulationPanel;

        public BalloonsModule( PhetApplicationConfig config ) {
            super( config.getName(), new ConstantDtClock( 30, 1 ) );
            balloonsSimulationPanel = new BalloonsSimulationPanel( config.getCommandLineArgs() );
            try {
                balloonsSimulationPanel.init( config.getCommandLineArgs() );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            setSimulationPanel( balloonsSimulationPanel );
            setClockControlPanel( null );
            setLogoPanelVisible( false );
        }

        public int getControlPanelHeight() {
            return balloonsSimulationPanel.getControlPanelHeight();
        }
    }
}
