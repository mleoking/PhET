package edu.colorado.phet.rotation;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Sam Reid
 * May 29, 2007, 12:56:31 AM
 */
public class TorqueApplication extends PiccoloPhetApplication {
    private TorqueModule rotationModule;

    public TorqueApplication( String[] args ) {
        super( new PhetApplicationConfig( args, createFrameSetup(), RotationResources.getInstance() ) );
        rotationModule = new TorqueModule();
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

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new RotationLookAndFeel().initLookAndFeel();
                new TorqueApplication( args ).startApplication();
            }
        } );
    }
}
