package edu.colorado.phet.balloons;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
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
            frame.setResizable( false ); // #683
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

    public static void main( final String[] args ) {
        System.out.println( "Testing generics, boxing, unboxing, foreach" );

//        ArrayList<Double>list=new ArrayList<Double>( );
//        list.add(3.0);
//        list.add(4.0);
//        list.add(5.6);
//        for ( double aDouble : list ) {
//            System.out.print(  aDouble+", " );
//        }

        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, "balloons" );
        appConfig.getLookAndFeel().setBackgroundColor( new Color( 200, 240, 200 ) );

        new PhetApplicationLauncher().launchSim( appConfig, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new BalloonsApplication( config );
            }
        } );
    }
}
