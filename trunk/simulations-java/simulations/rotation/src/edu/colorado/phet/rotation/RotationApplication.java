package edu.colorado.phet.rotation;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.QuickProfiler;
import edu.colorado.phet.rotation.RotationFrameSetup;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;
import edu.colorado.phet.rotation.controls.RotationDevMenu;
import edu.colorado.phet.rotation.controls.RotationTestMenu;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

/**
 * PhET's Rotation simulation.
 *
 * @author Sam Reid
 */

public class RotationApplication extends PhetApplication {
    private RotationModule rotationModule;

    public RotationApplication( String[] args ) {
        super( new PhetApplicationConfig( args, new RotationFrameSetup(), RotationResources.getInstance() ) );
        rotationModule = new RotationModule( getPhetFrame() );
        addModule( rotationModule );

        getPhetFrame().addMenu( new RotationTestMenu() );
        getPhetFrame().addMenu( new RotationDevMenu( this ) );

        //trial workaround for getting the window to paint when gray, this is a problem due to performance constraints of this application. 
        getPhetFrame().addWindowFocusListener( new WindowFocusListener() {
            public void windowGainedFocus( WindowEvent e ) {
                System.out.println( "RotationApplication.windowGainedFocus" );
                if( getPhetFrame().getContentPane() instanceof JComponent ) {
                    JComponent jComponent = (JComponent)getPhetFrame().getContentPane();
                    jComponent.paintImmediately( 0, 0, jComponent.getWidth(), jComponent.getHeight() );
                }
            }

            public void windowLostFocus( WindowEvent e ) {
            }
        } );
    }

    public void startApplication() {
        super.startApplication();
        rotationModule.getRotationSimulationPanel().requestFocus();
        rotationModule.startApplication();
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                QuickProfiler appStartTime = new QuickProfiler();
                new RotationLookAndFeel().initLookAndFeel();
                new RotationApplication( args ).startApplication();
                System.out.println( "appStartTime = " + appStartTime );
            }
        } );
    }

    public RotationModule getRotationModule() {
        return rotationModule;
    }
}
