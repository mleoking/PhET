/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.qm;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:16:39 AM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class CylinderWave implements InitialWavefunction {

    public void initialize( Complex[][] wavefunction ) {
        initGaussian( wavefunction );
    }

    void initGaussian( Complex[][] w ) {
        int XMESH = w.length - 1;
        int YMESH = w[0].length - 1;
        for( int i = 0; i <= XMESH; i++ ) {
            for( int j = 0; j <= YMESH; j++ ) {
                if( ( i - XMESH / 2 ) * ( i - XMESH / 2 ) + ( j - YMESH / 2 ) * ( j - YMESH / 2 ) < XMESH * YMESH / 64 ) {
                    w[i][j] = new Complex( Math.cos( i / XMESH ), Math.sin( ( i / XMESH ) ) );
                }
                else {
                    w[i][j] = new Complex();
                }
            }
        }
    }
}
