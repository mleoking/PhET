package edu.colorado.phet.rotation;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.FrameSetup;

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
 * Copyright (c) Dec 27, 2006 by Sam Reid
 */

public class RotationSimulation extends PhetApplication {
    public static final String TITLE = "Rotational Motion";
    public static final String DESCRIPTION = "Rotational Motion Simulation";

    public RotationSimulation( String[] args ) {
        super( args, TITLE, DESCRIPTION, readVersion(), createFrameSetup() );
        addModule( new RotationModule() );
    }

    private static String readVersion() {
        URL url = Thread.currentThread().getContextClassLoader().getResource( "cck.version" );
        try {
            BufferedReader br = new BufferedReader( new InputStreamReader( url.openStream() ) );
            return br.readLine();
        }
        catch( IOException e ) {
            e.printStackTrace();
            return "Version Not Found";
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

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new RotationLookAndFeel().initLookAndFeel();
                new RotationSimulation( args ).startApplication();
            }
        } );
    }

}
