/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;


/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:25:13 AM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class EmptyWaveSetup implements WaveSetup {
    public void initialize( Wavefunction wavefunction ) {
        wavefunction.clear();
    }
}
