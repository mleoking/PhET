package edu.colorado.phet.cck;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.*;

import edu.colorado.phet.cck.controls.OptionsMenu;
import edu.colorado.phet.cck.piccolo_cck.CCKPiccoloModule;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetFrameWorkaround;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PhetApplication;

/**
 * User: Sam Reid
 * Date: Jul 7, 2006
 * Time: 9:17:52 AM
 */

public class CCKApplication extends PhetApplication {
    //version is generated automatically (with ant)
    public static final String localizedStringsPath = "cck/localization/cck-strings";
    private CCKPiccoloModule cckPiccoloModule;
    public static final String AC_OPTION = "-dynamics";

    public CCKApplication( String[] args ) throws IOException {
        super( new PhetApplicationConfig( args, createFrameSetup(), PhetResources.forProject( "cck" ), isDynamic( args ) ? "cck-ac" : "cck-dc" ) );

        boolean debugMode = false;
        if ( Arrays.asList( args ).contains( "debug" ) ) {
            debugMode = true;
            System.out.println( "debugMode = " + debugMode );
        }

        cckPiccoloModule = new CCKPiccoloModule( args );
        cckPiccoloModule.getCckSimulationPanel().addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
            }

            public void keyReleased( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_F1 ) {
                    getPhetFrame().setSize( 1024, 768 );
                    getPhetFrame().invalidate();
                    getPhetFrame().validate();
                }
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        Module[] modules = new Module[]{cckPiccoloModule};
        setModules( modules );
        if ( getPhetFrame().getTabbedModulePane() != null ) {
            getPhetFrame().getTabbedModulePane().setLogoVisible( false );
        }
        getPhetFrame().addMenu( new OptionsMenu( this, cckPiccoloModule ) );//todo options menu
    }

    private static FrameSetup createFrameSetup() {
        if ( Toolkit.getDefaultToolkit().getScreenSize().height <= 768 ) {
            return new FrameSetup.MaxExtent( new FrameSetup.TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, 700 ) );
        }
        else {
            return new FrameSetup.TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height - 100 );
        }
    }

    protected PhetFrame createPhetFrame() {
        return new PhetFrameWorkaround( this );
    }

    private static boolean isDynamic( String[] args ) {
        return Arrays.asList( args ).contains( AC_OPTION );
    }

    public void startApplication() {
        super.startApplication();
        cckPiccoloModule.applicationStarted();
    }

    private static String readVersion() {
        return PhetApplicationConfig.getVersion( "cck" ).formatForTitleBar();
    }

    public static void main( final String[] args ) throws InvocationTargetException, InterruptedException {
        Locale.setDefault( new Locale( "ar" ) );
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
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
