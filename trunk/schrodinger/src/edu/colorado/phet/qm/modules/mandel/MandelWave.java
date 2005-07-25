/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.qm.model.Complex;
import edu.colorado.phet.qm.model.PlaneWave2D;
import edu.colorado.phet.qm.model.Wave;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 9:33:30 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelWave implements Wave {
    private PlaneWave2D leftWave;
    private MandelDampedWave dampedLeft;
    private PlaneWave2D rightWave;
    private MandelDampedWave dampedRight;
    private double momentum;
    private double phase;
    private double intensity;

    public MandelWave( int distFromLeft, double momentum, double phase, double dPhase, double intensity ) {
        this.momentum = momentum;
        this.phase = phase;
        this.intensity = intensity;

        leftWave = new PlaneWave2D( AbstractVector2D.Double.parseAngleAndMagnitude( momentum, 0 ), 100 );
        dampedLeft = new MandelDampedWave( distFromLeft, leftWave, getIntensity() );
        leftWave.setPhase( phase );

        rightWave = new PlaneWave2D( AbstractVector2D.Double.parseAngleAndMagnitude( momentum, 0 ), 100 );
        rightWave.setPhase( phase + dPhase );
        dampedRight = new MandelDampedWave( 100 - distFromLeft, rightWave, getIntensity() );
    }

    private double getIntensity() {
        return intensity;
    }

    public Complex getValue( int i, int j, double simulationTime ) {
        return dampedLeft.getValue( i, j, simulationTime ).plus( dampedRight.getValue( i, j, simulationTime ) );
    }
}
