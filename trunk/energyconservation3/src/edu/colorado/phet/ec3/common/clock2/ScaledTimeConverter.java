/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.common.clock2;

/**
 * User: Sam Reid
 * Date: Dec 15, 2005
 * Time: 12:38:38 PM
 * Copyright (c) Dec 15, 2005 by Sam Reid
 */
public class ScaledTimeConverter implements TimeConverter {
    private double scale;

    public ScaledTimeConverter( double scale ) {
        this.scale = scale;
    }

    public double getSimulationTimeChange( Clock clock ) {
        return clock.getWallTimeChangeMillis() * scale / 1000.0;
    }
}
