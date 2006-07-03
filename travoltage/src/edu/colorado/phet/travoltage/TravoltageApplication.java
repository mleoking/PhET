/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:20:53 PM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class TravoltageApplication extends PhetApplication {
    private static final String TITLE = "Travoltage";
    private static final String DESCRIPTION = "The John Travoltage Simulation";
    private static final String VERSION = "1.00.01";

    public TravoltageApplication( String[] args ) {
        super( args, TITLE, DESCRIPTION, VERSION, new TravoltageFrameSetup() );
        addModule( new TravoltageModule() );
    }

    public static class TravoltageFrameSetup implements FrameSetup {
        public void initialize( JFrame frame ) {
            new FrameSetup.CenteredWithSize( 800, 700 ).initialize( frame );
        }
    }

    public static void main( String[] args ) {
        new TravoltageLookAndFeel().initLookAndFeel();
        new TravoltageApplication( args ).startApplication();
    }
}
