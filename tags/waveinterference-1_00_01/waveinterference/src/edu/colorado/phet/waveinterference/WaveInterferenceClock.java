/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.model.clock.SwingClock;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 11:09:48 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class WaveInterferenceClock extends SwingClock {
    public WaveInterferenceClock() {
        super( 30, 1.0 / 30.0 );
    }
}
