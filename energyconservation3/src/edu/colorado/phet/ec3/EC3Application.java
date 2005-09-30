/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:07:25 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class EC3Application extends PhetApplication {
    private EC3Module module;

    public EC3Application( String[] args ) {
        super( args, "EC3", "Energy Conservation", "0.1", new SwingTimerClock( 1.0, 25 ), true, new FrameSetup.CenteredWithInsets( 100, 100 ) );
        module = new EC3Module( "Module", getClock() );
        setModules( new Module[]{module} );
    }

    public static void main( String[] args ) {
        new EC3Application( args ).start();
    }

    private void start() {
        super.startApplication();
        module.getPhetPCanvas().requestFocus();
    }
}
