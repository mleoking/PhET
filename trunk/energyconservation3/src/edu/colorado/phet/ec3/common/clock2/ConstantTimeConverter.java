/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.common.clock2;

/**
 * User: Sam Reid
 * Date: Dec 15, 2005
 * Time: 12:38:52 PM
 * Copyright (c) Dec 15, 2005 by Sam Reid
 */
public class ConstantTimeConverter implements TimeConverter {
    private double simulationTimeChange;

    public ConstantTimeConverter( double simulationTimeChange ) {
        this.simulationTimeChange = simulationTimeChange;
    }

    public double getSimulationTimeChange( Clock clock ) {
        return simulationTimeChange;
    }
}
