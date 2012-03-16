// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;

/**
 * PhET's Rotation simulation.
 *
 * @author Sam Reid
 */

public class LadybugRevolutionApplication extends PiccoloPhetApplication {
    private RotationModule rotationModule;

    public LadybugRevolutionApplication( PhetApplicationConfig config ) {
        super( config );

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
        PhetApplicationConfig rotationApplicationConfig = new PhetApplicationConfig( args,"rotation" );
        new PhetApplicationLauncher().launchSim( rotationApplicationConfig, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                MyRepaintManager synchronizedPSwingRepaintManager = new MyRepaintManager();
                synchronizedPSwingRepaintManager.setDoMyCoalesce( true );
                RepaintManager.setCurrentManager( synchronizedPSwingRepaintManager );

                new RotationLookAndFeel().initLookAndFeel();
                return new LadybugRevolutionApplication( config );
            }
        } );
    }

    public RotationModule getRotationModule() {
        return rotationModule;
    }

}
