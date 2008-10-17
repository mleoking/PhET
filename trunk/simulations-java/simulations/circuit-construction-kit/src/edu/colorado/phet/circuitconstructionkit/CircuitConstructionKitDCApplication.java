package edu.colorado.phet.circuitconstructionkit;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

import edu.colorado.phet.circuitconstructionkit.controls.OptionsMenu;
import edu.colorado.phet.circuitconstructionkit.util.CCKUtil;
import edu.colorado.phet.circuitconstructionkit.view.CCKPhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetFrameWorkaround;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * User: Sam Reid
 * Date: Jul 7, 2006
 * Time: 9:17:52 AM
 */

public class CircuitConstructionKitDCApplication extends PiccoloPhetApplication {
    public static final String localizedStringsPath = "circuitconstructionkit/localization/cck-strings";
    private CCKModule cckPiccoloModule;
    public static final String AC_OPTION = "-dynamics";

    public CircuitConstructionKitDCApplication( PhetApplicationConfig config ) {
        super( config );

        boolean debugMode = false;
        if ( Arrays.asList( config.getCommandLineArgs() ).contains( "debug" ) ) {
            debugMode = true;
            System.out.println( "debugMode = " + debugMode );
        }

        cckPiccoloModule = new CCKModule( config.getCommandLineArgs() );
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

    public static void main( final String[] args ) {
        
        CCKUtil.setupLanguagesForSwingComponents();
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new CircuitConstructionKitDCApplication( config );
            }
        };
        
        String flavor = isDynamic( args ) ? "circuit-construction-kit-ac" : "circuit-construction-kit-dc";
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, appConstructor, "circuit-construction-kit", flavor );
        appConfig.setLookAndFeel( new CCKPhetLookAndFeel() );
        appConfig.setFrameSetup( createFrameSetup() );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
