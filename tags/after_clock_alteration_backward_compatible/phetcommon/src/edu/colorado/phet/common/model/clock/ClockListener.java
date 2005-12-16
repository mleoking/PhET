/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.model.clock;

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