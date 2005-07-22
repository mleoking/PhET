/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.qm.MandelDampedWave;
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

    public MandelWave( double momentum, double phase ) {
        this.momentum = momentum;
        this.phase = phase;

        int distFromLeft = 30;
        leftWave = new PlaneWave2D( AbstractVector2D.Double.parseAngleAndMagnitude( momentum, Math.PI / 4 ), 100 );
        dampedLeft = new MandelDampedWave( distFromLeft, leftWave, getIntensity() );
        leftWave.setPhase( phase );

        rightWave = new PlaneWave2D( AbstractVector2D.Double.parseAngleAndMagnitude( momentum, Math.PI / 8 ), 100 );
//        rightWave.setPhase( phase + Math.PI );
        rightWave.setPhase( phase );
//        rightWave.setPhase( phase );
        dampedRight = new MandelDampedWave( 100 - distFromLeft, rightWave, getIntensity() );
    }

    private double getIntensity() {
        return 0.03;
    }

    public Complex getValue( int i, int j, double simulationTime ) {
        return dampedLeft.getValue( i, j, simulationTime ).plus( dampedRight.getValue( i, j, simulationTime ) );
    }
}
