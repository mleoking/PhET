/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.common.model.clock.Clock;
import edu.colorado.phet.common.model.clock.TimingStrategy;

/**
 * User: Sam Reid
 * Date: Jan 5, 2006
 * Time: 10:33:53 PM
 * Copyright (c) Jan 5, 2006 by Sam Reid
 */

public class SchrodingerClock extends Clock {
    public SchrodingerClock() {
        super( new TimingStrategy.Constant( 1.0 ) );
    }

    public void start() {
    }

    public void pause() {
    }

    public boolean isPaused() {
        return false;
    }

    public boolean isRunning() {
        return false;
    }
}
