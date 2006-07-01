/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.piccolo.PiccoloModule;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:22:45 PM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class TravoltageModule extends PiccoloModule {

    public TravoltageModule() {
        super( "Travoltage", createClock() );
        setSimulationPanel( new TravoltagePanel() );
    }

    private static IClock createClock() {
        return new SwingClock( 30, 1.0 );
    }
}
