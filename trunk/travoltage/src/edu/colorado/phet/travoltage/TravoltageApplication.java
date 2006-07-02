/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.application.PhetApplication;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:20:53 PM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class TravoltageApplication extends PhetApplication {
    private static final String TITLE = "Travoltage";
    private static final String DESCRIPTION = "The John Travoltage Simulation";
    private static final String VERSION = "2.00.00";

    public TravoltageApplication( String[] args ) {
        super( args, TITLE, DESCRIPTION, VERSION );
        addModule( new TravoltageModule() );
    }

    public static void main( String[] args ) {
        new TravoltageLookAndFeel().initLookAndFeel();
        new TravoltageApplication( args ).startApplication();
    }
}
