// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.quantumwaveinterference.view.colormaps;

import edu.colorado.phet.quantumwaveinterference.model.Wavefunction;

/**
 * User: Sam Reid
 * Date: Jul 27, 2005
 * Time: 9:07:38 AM
 */
public interface WaveValueAccessor {
    public double getValue( Wavefunction wavefunction, int i, int j );

    public static class Magnitude implements WaveValueAccessor {
        public double getValue( Wavefunction wavefunction, int i, int j ) {
            return wavefunction.valueAt( i, j ).abs();
        }
    }

    public static class Real implements WaveValueAccessor {
        public double getValue( Wavefunction wavefunction, int i, int j ) {
            return Math.abs( wavefunction.valueAt( i, j ).getReal() );
        }
    }

    public static class Imag implements WaveValueAccessor {
        public double getValue( Wavefunction wavefunction, int i, int j ) {
            return Math.abs( wavefunction.valueAt( i, j ).getImaginary() );
        }
    }

    public class Empty implements WaveValueAccessor {
        public double getValue( Wavefunction wavefunction, int i, int j ) {
            return 0;
        }
    }
}
