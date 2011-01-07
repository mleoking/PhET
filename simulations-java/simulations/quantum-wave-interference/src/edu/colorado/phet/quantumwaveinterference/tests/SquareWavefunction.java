// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.tests;

import edu.colorado.phet.quantumwaveinterference.model.Wavefunction;

/**
 * User: Sam Reid
 * Date: Feb 10, 2006
 * Time: 12:12:36 AM
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
