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
 * Time: 2:27:13 PM
 * Copyright (c) Dec 15, 2005 by Sam Reid
 */

public class ClockEvent {
    private IClock clock;

    public ClockEvent( IClock clock ) {
        this.clock = clock;
    }

    public IClock getClock() {
        return clock;
    }

    public long getWallTimeChangeMillis() {
        return clock.getWallTimeChangeMillis();
    }

    public double getSimulationTimeChange() {
        return clock.getSimulationTimeChange();
    }

    public double getSimulationTime() {
        return clock.getSimulationTime();
    }

    public long getWallTime() {
        return clock.getWallTime();
    }

}