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

public class BalloonsApplication extends PiccoloPhetApplication {
    private BalloonsModule module;
    public static final double DT = 1.2;

    public BalloonsApplication( PhetApplicationConfig config ) {
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
        private BalloonsApplication application;

        public BalloonsFrame( final BalloonsApplication application ) throws HeadlessException {
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
            super( commandLineArgs, new FrameSetup.NoOp(), new PhetResources( "balloons" ) );
            super.setApplicationConstructor( new ApplicationConstructor() {
                public PhetApplication getApplication( PhetApplicationConfig config ) {
                    return new BalloonsApplication( config );
                }
            } );
            PhetLookAndFeel laf = new PhetLookAndFeel();
            laf.setBackgroundColor( new Color( 200, 240, 200 ) );
            super.setLookAndFeel( laf );
        }
    }

    public class BalloonsModule extends Module {
        private BalloonsSimulationPanel balloonsSimulationPanel;

        public BalloonsModule( PhetApplicationConfig config ) {
            super( config.getName(), new ConstantDtClock( 30, DT ) );
            balloonsSimulationPanel = new BalloonsSimulationPanel();
            try {
                balloonsSimulationPanel.init( this );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            setSimulationPanel( balloonsSimulationPanel );
            setClockControlPanel( null );
            setLogoPanelVisible( false );
            setControlPanel( null );
            setHelpPanel( null );
        }

        public boolean hasMegaHelp() {
            return true;
        }

        public boolean hasHelp() {
            return true;
        }

        public void setHelpEnabled( boolean enabled ) {
            super.setHelpEnabled( enabled );
            balloonsSimulationPanel.setHelpEnabled( enabled );
        }

        public void showMegaHelp() {
            super.showMegaHelp();
            balloonsSimulationPanel.showMegaHelp();
        }

        public int getControlPanelHeight() {
            return balloonsSimulationPanel.getControlPanelHeight();
        }
    }

    public static void main( String[] args ) {
        new BalloonsApplicationConfig( args ).launchSim();
    }
}
