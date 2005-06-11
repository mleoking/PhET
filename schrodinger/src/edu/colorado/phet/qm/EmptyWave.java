/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:25:13 AM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class EmptyWave implements InitialWavefunction {
    public void initialize( Complex[][] wavefunction ) {
        for( int i = 0; i < wavefunction.length; i++ ) {
            Complex[] complexes = wavefunction[i];
            for( int j = 0; j < complexes.length; j++ ) {
                wavefunction[i][j] = new Complex();
            }
        }
    }
}
