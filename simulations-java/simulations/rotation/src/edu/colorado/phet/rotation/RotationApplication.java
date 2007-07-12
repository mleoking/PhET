package edu.colorado.phet.rotation;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.QuickProfiler;
import edu.colorado.phet.rotation.torque.RotationFrameSetup;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;

import javax.swing.*;

/**
 * PhET's Rotation simulation.
 *
 * @author Sam Reid
 */

public class RotationApplication extends PhetApplication {
    private AbstractRotationModule rotationModule;

    public RotationApplication( String[] args ) {
        super( new PhetApplicationConfig( args, new RotationFrameSetup(), RotationResources.getInstance() ) );
        rotationModule = new RotationModule( getPhetFrame() );
        addModule( rotationModule );

        getPhetFrame().addMenu( new RotationTestMenu() );
    }

    public void startApplication() {
        super.startApplication();
        rotationModule.getRotationSimulationPanel().requestFocus();
        rotationModule.startApplication();
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                QuickProfiler appStartTime=new QuickProfiler( );
                new RotationLookAndFeel().initLookAndFeel();
                new RotationApplication( args ).startApplication();
                System.out.println( "appStartTime = " + appStartTime );
            }
        } );
    }

}
