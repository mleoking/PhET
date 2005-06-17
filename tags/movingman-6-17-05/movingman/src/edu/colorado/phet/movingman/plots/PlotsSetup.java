/** Sam Reid*/
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.movingman.MovingManModule;

/**
 * User: Sam Reid
 * Date: Sep 6, 2004
 * Time: 3:25:38 PM
 * Copyright (c) Sep 6, 2004 by Sam Reid
 */
public class PlotsSetup {
    private double positionMagnitude;
    private double velocityMagnitude;
    private double accelerationMagnitude;

    private double[] positionLines;
    private double[] velocityLines;
    private double[] accelerationLines;

    public PlotsSetup( double positionMagnitude, double positionGridMax,
                       double velocityMagnitude, double velocityGridMax,
                       double accelerationMagnitude, double accelerationGridMax ) {
        this( positionMagnitude, velocityMagnitude, accelerationMagnitude,
              new double[]{-positionGridMax, -positionGridMax / 2, 0, positionGridMax / 2, positionGridMax},
              new double[]{-velocityGridMax, -velocityGridMax / 2, 0, velocityGridMax / 2, velocityGridMax},
              new double[]{-accelerationGridMax, -accelerationGridMax / 2, 0, accelerationGridMax / 2, accelerationGridMax} );
    }

    public PlotsSetup( double positionMagnitude, double velocityMagnitude, double accelerationMagnitude, double[] positionLines, double[] velocityLines, double[] accelerationLines ) {
        this.positionMagnitude = positionMagnitude;
        this.accelerationMagnitude = accelerationMagnitude;
        this.velocityMagnitude = velocityMagnitude;
        this.positionLines = positionLines;
        this.velocityLines = velocityLines;
        this.accelerationLines = accelerationLines;
    }

    public void setup( MovingManModule module ) {
//        module.getPositionPlot().setMagnitude( positionMagnitude );
//        module.getVelocityPlot().setMagnitude( velocityMagnitude );
//        module.getAccelerationPlot().setMagnitude( accelerationMagnitude );
        module.getPositionPlot().setPaintYLines( positionLines );
        module.getVelocityPlot().setPaintYLines( velocityLines );
        module.getAccelerationPlot().setPaintYLines( accelerationLines );
        module.repaintBackground();
    }
}
