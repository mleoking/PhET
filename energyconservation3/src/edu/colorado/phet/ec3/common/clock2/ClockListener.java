/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.common.clock2;

/**
 * User: Sam Reid
 * Date: Dec 15, 2005
 * Time: 12:26:41 PM
 * Copyright (c) Dec 15, 2005 by Sam Reid
 */

public interface ClockListener {
    void clockTicked( ClockEvent clockEvent );

    void clockStarted( ClockEvent clockEvent );

    void clockPaused( ClockEvent clockEvent );

    void simulationTimeChanged( ClockEvent clockEvent );

    void clockReset( ClockEvent clockEvent );
}
