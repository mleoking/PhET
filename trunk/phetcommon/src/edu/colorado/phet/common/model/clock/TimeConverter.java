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
 * Time: 12:32:53 PM
 * Copyright (c) Dec 15, 2005 by Sam Reid
 */
public interface TimeConverter {
    double getSimulationTimeChange( Clock clock );

    public static class Constant implements TimeConverter {
        private double simulationTimeChange;

        public Constant( double simulationTimeChange ) {
            this.simulationTimeChange = simulationTimeChange;
        }

        public double getSimulationTimeChange( Clock clock ) {
            return simulationTimeChange;
        }
    }

    public static class Identity implements TimeConverter {
        public double getSimulationTimeChange( Clock clock ) {
            return clock.getWallTimeChangeMillis() / 1000.0;
        }
    }

    public static class Scaled implements TimeConverter {
        private double scale;

        public Scaled( double scale ) {
            this.scale = scale;
        }

        public double getSimulationTimeChange( Clock clock ) {
            return clock.getWallTimeChangeMillis() * scale / 1000.0;
        }
    }
}
