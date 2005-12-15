/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.common.clock2;

/**
 * User: Sam Reid
 * Date: Dec 15, 2005
 * Time: 12:38:38 PM
 * Copyright (c) Dec 15, 2005 by Sam Reid
 */
public class IdentityTimeConverter implements TimeConverter {

    public double getSimulationTimeChange( Clock clock ) {
        return clock.getWallTimeChangeMillis() / 1000.0;
    }
}
