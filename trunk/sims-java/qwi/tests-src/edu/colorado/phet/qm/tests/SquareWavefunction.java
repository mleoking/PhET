/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.tests;

import edu.colorado.phet.qm.model.Wavefunction;

/**
 * User: Sam Reid
 * Date: Feb 10, 2006
 * Time: 12:12:36 AM
 * Copyright (c) Feb 10, 2006 by Sam Reid
 */

public class SquareWavefunction {
    public static void setToSquare( Wavefunction wavefunction, int squareSize ) {
        wavefunction.clear();
        for( int i = wavefunction.getWidth() / 2 - squareSize / 2; i <= wavefunction.getWidth() / 2 + squareSize / 2; i++ )
        {
            for( int k = wavefunction.getHeight() / 2 - squareSize / 2; k <= wavefunction.getHeight() / 2 + squareSize / 2; k++ )
            {
                wavefunction.setValue( i, k, 1.0, 0.0 );
            }
        }
    }
}
