/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.waves;

import edu.colorado.phet.qm.model.Wave;
import edu.colorado.phet.qm.model.math.Complex;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 6:35:54 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */

public class MandelDampedWave implements Wave {
    private Wave wave;
    private double intensity;
//    private int x0 = 50;//todo magick.
    private int x0;
//    private double dxLattice = 4.0;
//    private double dxLattice = 10;
//    private double dxLattice = 2;

    private double dxLattice = 4;
//    private int radiusForMax = 3;

    public MandelDampedWave( int x0, Wave wave, double intensity, int waveWidth ) {
        this.x0 = x0;
        this.wave = wave;
        this.intensity = intensity;
        this.dxLattice = 4 * waveWidth / 100.0;
    }

    public Complex getValue( int i, int j, double simulationTime ) {
        double dampTerm = Math.exp( -( i - x0 ) * ( i - x0 ) / 4 / dxLattice / dxLattice );
        return wave.getValue( i, j, simulationTime ).times( dampTerm * intensity );
    }
}
