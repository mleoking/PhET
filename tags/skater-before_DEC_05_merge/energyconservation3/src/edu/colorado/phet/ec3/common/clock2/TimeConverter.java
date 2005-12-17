package edu.colorado.phet.ec3.common.clock2;

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
