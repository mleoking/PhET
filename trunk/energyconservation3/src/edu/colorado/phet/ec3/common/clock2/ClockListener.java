/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.common.clock2;

/**
 * User: Sam Reid
 * Date: Dec 15, 2005
 * Time: 12:26:41 PM
 * Copyright (c) Dec 15, 2005 by Sam Reid
 */

public interface ClockListener {
    void clockTicked( Clock clock );

    void clockStarted( Clock clock );

    void clockPaused( Clock clock );

    void simulationTimeChanged( Clock clock );

    void clockReset( Clock clock );
}
