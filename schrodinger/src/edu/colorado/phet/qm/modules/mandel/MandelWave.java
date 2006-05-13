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
//    private PlaneWave2D leftWave;
    private MandelWaveDamp leftDamp;
//    private PlaneWave2D rightWave;
    private MandelWaveDamp rightDamp;
    private double momentum;
    private double phase;
    private int waveWidth;

    public MandelWave( int distFromLeft, double momentum, double phase, double dPhase, double intensity, int waveWidth ) {
        this( distFromLeft, momentum, momentum, phase, dPhase, intensity, intensity, waveWidth );
    }

    public MandelWave( int distFromLeft, double momentumLeft, double momentumRight, double phase, double dPhase, double leftIntensity, double rightIntensity, int waveWidth ) {
        this.momentum = momentumLeft;
        this.phase = phase;
        this.waveWidth = waveWidth;

        PlaneWave2D leftWave = new PlaneWave2D( AbstractVector2D.Double.parseAngleAndMagnitude( momentumLeft, 0 ), 100 );
        leftWave.setPhase( phase );
        leftDamp = new MandelWaveDamp( distFromLeft, leftWave, leftIntensity, waveWidth );


        PlaneWave2D rightWave = new PlaneWave2D( AbstractVector2D.Double.parseAngleAndMagnitude( momentumRight, 0 ), 100 );
        rightWave.setPhase( phase + dPhase );
        rightDamp = new MandelWaveDamp( waveWidth - distFromLeft, rightWave, rightIntensity, waveWidth );
    }

    public Complex getValue( int i, int j, double simulationTime ) {
        return leftDamp.getValue( i, j, simulationTime ).plus( rightDamp.getValue( i, j, simulationTime ) );
    }

}
