package edu.colorado.phet.rotation;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:28:24 AM
 * Copyright (c) Dec 27, 2006 by Sam Reid
 */

public class RotationSimulation extends PhetApplication {
    public static final String TITLE = "Rotational Motion";
    public static final String DESCRIPTION = "Rotational Motion Simulation";
    public static final String VERSION = "0.00.01";

    public RotationSimulation( String[] args ) {
        super( args, TITLE, DESCRIPTION, VERSION, createFrameSetup() );
        addModule( new RotationModule() );
    }

    private static FrameSetup createFrameSetup() {
        if( Toolkit.getDefaultToolkit().getScreenSize().height <= 768 ) {
            return new FrameSetup.MaxExtent( new FrameSetup.TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, 700 ) );
        }
        else {
            return new FrameSetup.TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height - 100 );
        }
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PhetLookAndFeel().initLookAndFeel();
                new RotationSimulation( args ).startApplication();
            }
        } );
    }

}
