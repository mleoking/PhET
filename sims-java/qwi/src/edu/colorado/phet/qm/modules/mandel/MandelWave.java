/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.qm.model.Wave;
import edu.colorado.phet.qm.model.math.Complex;
import edu.colorado.phet.qm.model.waves.PlaneWave2D;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 9:33:30 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelWave implements Wave {
    private MandelWaveDamp leftWave;
    private MandelWaveDamp rightWave;

    public MandelWave( int leftWaveX, double momentumLeft, double momentumRight, double phase, double dPhase, double leftIntensity, double rightIntensity, int waveWidth ) {
        PlaneWave2D leftWave = new PlaneWave2D( AbstractVector2D.Double.parseAngleAndMagnitude( momentumLeft, 0 ), 100 );
        leftWave.setPhase( phase );
        leftWave.setPhaseOffset( 0 );
        this.leftWave = new MandelWaveDamp( leftWaveX, leftWave, leftIntensity, waveWidth );

        PlaneWave2D rightWave = new PlaneWave2D( AbstractVector2D.Double.parseAngleAndMagnitude( momentumRight, 0 ), 100 );
        rightWave.setPhaseOffset( -dPhase );
        int rightWaveX = waveWidth - leftWaveX - 1;
        this.rightWave = new MandelWaveDamp( rightWaveX, rightWave, rightIntensity, waveWidth );
    }

    public Complex getValue( int i, int j, double simulationTime ) {
        double t = simulationTime * 1000 * 3 * 1.2;//todo magic number
        return leftWave.getValue( i, j, t ).plus( rightWave.getValue( i, j, t ) );
    }

}
