package edu.colorado.phet.cck;

import edu.colorado.phet.cck.controls.LookAndFeelMenu;
import edu.colorado.phet.cck.controls.OptionsMenu;
import edu.colorado.phet.cck.model.components.Bulb;
import edu.colorado.phet.cck.phetgraphics_cck.CCKPhetgraphicsModule;
import edu.colorado.phet.cck.piccolo_cck.CCKPiccoloModule;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetAboutDialog;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.PhetFrameWorkaround;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common_cck.view.components.AspectRatioPanel;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Jul 7, 2006
 * Time: 9:17:52 AM
 * Copyright (c) Jul 7, 2006 by Sam Reid
 */

public class CCKApplication extends PiccoloPhetApplication {
    //version is generated automatically (with ant)
    public static final String localizedStringsPath = "localization/CCKStrings";
    private CCKPhetGraphicModuleAdapter phetGraphicsCCKModule;
    private CCKPiccoloModule cckPiccoloModule;

    public CCKApplication( String[] args ) throws IOException {
        super( args, SimStrings.get( "CCKApplication.title" ) + getSubTitle( args ),
               SimStrings.get( "CCKApplication.description" ),
               readVersion(), createFrameSetup() );

        //new FrameSetup.CenteredWithSize( 1024,768)

        boolean debugMode = false;
        if( Arrays.asList( args ).contains( "debug" ) ) {
            debugMode = true;
            System.out.println( "debugMode = " + debugMode );
        }

        phetGraphicsCCKModule = new CCKPhetGraphicModuleAdapter( args );
        cckPiccoloModule = new CCKPiccoloModule( args );
        Module[] modules = new Module[]{cckPiccoloModule, phetGraphicsCCKModule};
        setModules( modules );
        getPhetFrame().getTabbedModulePane().setLogoVisible( false );
        getPhetFrame().addMenu( new LookAndFeelMenu() );
        getPhetFrame().addMenu( new OptionsMenu( this, cckPiccoloModule ) );//todo options menu
    }

    private static FrameSetup createFrameSetup() {
        if( Toolkit.getDefaultToolkit().getScreenSize().height <= 768 ) {
            return new FrameSetup.MaxExtent( new TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, 700 ) );
        }
        else {
            return new TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height - 100 );
        }
    }

    public static class TopCenter implements FrameSetup {
        private int width;
        private int height;

        public TopCenter( int width, int height ) {
            this.width = width;
            this.height = height;
        }

        // todo: add test to see that the requested dimensions aren't bigger than the screen
        public void initialize( JFrame frame ) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension d = tk.getScreenSize();
            int x = ( d.width - width ) / 2;
            int y = 0;
            frame.setLocation( x, y );
            frame.setSize( width, height );
        }
    }

    protected PhetFrame createPhetFrame() {
        return new PhetFrameWorkaround( this );
    }

    public static String getSubTitle( String[] args ) {
        return Arrays.asList( args ).contains( "-dynamics" ) ? ": DC + AC" : ": DC Only";
    }

    public void startApplication() {
        super.startApplication();
        cckPiccoloModule.applicationStarted();
    }

    protected PhetAboutDialog createPhetAboutDialog() {
        return new CCKAboutDialog( this );
    }

    private static String readVersion() {
        URL url = Thread.currentThread().getContextClassLoader().getResource( "cck.version" );
        try {
            BufferedReader br = new BufferedReader( new InputStreamReader( url.openStream() ) );
            return br.readLine();
        }
        catch( IOException e ) {
            e.printStackTrace();
            return "Version Not Found";
        }
    }

    static class CCKPhetGraphicModuleAdapter extends Module {
        private CCKPhetgraphicsModule cckModule;

        /* Aspect ratio panel used in single-module setups*/
        private AspectRatioPanel aspectRatioPanel;

        public CCKPhetGraphicModuleAdapter( String[] args ) throws IOException {
            super( "CCK-phetgraphics", new SwingClock( 30, 1 ) );

            cckModule = new CCKPhetgraphicsModule( args );
            aspectRatioPanel = new AspectRatioPanel( cckModule.getCCKApparatusPanel(), 5, 5, 1.2 );
            cckModule.initControlPanel( this );
//            setSimulationPanel( cckModule.getCCKApparatusPanel() );
            setSimulationPanel( aspectRatioPanel );
            setControlPanel( cckModule.getControlPanel() );
            addModelElement( new ModelElement() {
                public void stepInTime( double dt ) {
                    cckModule.getModel().stepInTime( dt );
                    cckModule.getCCKApparatusPanel().synchronizeImmediately();
                }
            } );
            setLogoPanel( null );
        }

        public void setHelpEnabled( boolean enabled ) {
            super.setHelpEnabled( enabled );
            cckModule.setHelpEnabled( enabled );
        }

        public void activate() {
            super.activate();
            Bulb.setHeightScale( 0.25 );
        }
    }

    public static void main( final String[] args ) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
                SimStrings.init( args, CCKApplication.localizedStringsPath );
                new CCKPhetLookAndFeel().initLookAndFeel();
                try {
                    CCKApplication cckApplication = new CCKApplication( args );
                    cckApplication.startApplication();
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

}
