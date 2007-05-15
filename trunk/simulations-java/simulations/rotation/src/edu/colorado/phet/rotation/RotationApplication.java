package edu.colorado.phet.rotation;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;

import javax.swing.*;
import java.awt.*;

/**
 * PhET's Rotation simulation.
 * @author Sam Reid
 */

public class RotationApplication extends PhetApplication {
    private RotationModule rotationModule;

    public RotationApplication( String[] args ) {
        super( new PhetApplicationConfig( args, createFrameSetup(), RotationResources.getInstance() ) );
        rotationModule = new RotationModule();
        addModule( rotationModule );

        getPhetFrame().addMenu( new RotationTestMenu() );
    }

    private static FrameSetup createFrameSetup() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if( screenSize.height <= 768 ) {
            return new FrameSetup.MaxExtent( new FrameSetup.TopCenter( screenSize.width, 700 ) );
        }
        else {
            return new FrameSetup.TopCenter( screenSize.width, screenSize.height - 100 );
        }
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
