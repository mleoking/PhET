package edu.colorado.phet.rotation;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.*;

import edu.colorado.phet.common.motion.model.DefaultTimeSeries;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.QuickProfiler;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.rotation.controls.RotationDevMenu;
import edu.colorado.phet.rotation.view.RotationFrameSetup;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;
import edu.umd.cs.piccolox.pswing.PSwingRepaintManager;

/**
 * PhET's Rotation simulation.
 *
 * @author Sam Reid
 * test comment
 */

public class RotationApplication extends PiccoloPhetApplication {
    private RotationModule rotationModule;

    public RotationApplication( String[] args ) {
        super( new PhetApplicationConfig( args, new RotationFrameSetup(), RotationResources.getInstance() ) );

        RotationIntroModule introRotationModule = new RotationIntroModule( getPhetFrame() );
        addModule( introRotationModule );

        rotationModule = new RotationModule( getPhetFrame() );
        addModule( rotationModule );

//        getPhetFrame().addMenu( new RotationTestMenu() );
//        getPhetFrame().addMenu( new RotationDevMenu( this, rotationModule ) );

        //trial workaround for getting the window to paint when gray, this is a problem due to performance constraints of this application.
        getPhetFrame().addWindowFocusListener( new WindowFocusListener() {
            public void windowGainedFocus( WindowEvent e ) {
                if ( getPhetFrame().getContentPane() instanceof JComponent ) {
                    JComponent jComponent = (JComponent) getPhetFrame().getContentPane();
                    jComponent.paintImmediately( 0, 0, jComponent.getWidth(), jComponent.getHeight() );
                }
            }

            public void windowLostFocus( WindowEvent e ) {
            }
        } );
    }

    public void startApplication() {
        rotationModule.initFinished();
        super.startApplication();
        rotationModule.getRotationSimulationPanel().requestFocus();
        rotationModule.startApplication();
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            /** @noinspection HardCodedStringLiteral*/
            public void run() {
                MyRepaintManager synchronizedPSwingRepaintManager = new MyRepaintManager();
                synchronizedPSwingRepaintManager.setDoMyCoalesce( true );
                RepaintManager.setCurrentManager( synchronizedPSwingRepaintManager );

                QuickProfiler appStartTime = new QuickProfiler();
                new RotationLookAndFeel().initLookAndFeel();
                new RotationApplication( args ).startApplication();
                System.out.println( "Rotation Application started in = " + appStartTime );
            }
        } );
    }

    public RotationModule getRotationModule() {
        return rotationModule;
    }
}
