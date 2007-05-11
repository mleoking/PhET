package edu.colorado.phet.rotation;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:28:24 AM
 */

public class RotationApplication extends PhetApplication {
    private RotationModule rotationModule;

    public RotationApplication( String[] args ) {
        super( new RotationApplicationConfig( args ) );
        rotationModule = new RotationModule();
        addModule( rotationModule );

        getPhetFrame().addMenu( new RotationTestMenu() );
    }

    static class RotationApplicationConfig extends PhetApplicationConfig {
        public RotationApplicationConfig( String[] commandLineArgs ) {
            super( commandLineArgs, createFrameSetup(), RotationResources.getInstance() );
        }
    }

    private static FrameSetup createFrameSetup() {
        if( Toolkit.getDefaultToolkit().getScreenSize().height <= 768 ) {
            return new FrameSetup.MaxExtent( new FrameSetup.TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, 700 ) );
        }
        else {
            return new FrameSetup.TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height - 100 );
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
