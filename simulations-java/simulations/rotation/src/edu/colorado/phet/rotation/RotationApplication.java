package edu.colorado.phet.rotation;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;
import edu.colorado.phet.rotation.torque.RotationFrameSetup;

import javax.swing.*;
import java.awt.*;

/**
 * PhET's Rotation simulation.
 *
 * @author Sam Reid
 */

public class RotationApplication extends PhetApplication {
    private AbstractRotationModule rotationModule;

    public RotationApplication( String[] args ) {
        super( new PhetApplicationConfig( args, new RotationFrameSetup(), RotationResources.getInstance() ) );
        rotationModule = new RotationModule();
        addModule( rotationModule );

        getPhetFrame().addMenu( new RotationTestMenu() );
    }

    public void startApplication() {
        super.startApplication();
        rotationModule.getRotationSimulationPanel().requestFocus();
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new RotationLookAndFeel().initLookAndFeel();
                new RotationApplication( args ).startApplication();
            }
        } );
    }

}
