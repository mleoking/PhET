/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.Lattice2D;

/**
 * User: Sam Reid
 * Date: Apr 9, 2006
 * Time: 3:47:45 PM
 * Copyright (c) Apr 9, 2006 by Sam Reid
 */

public interface WaveValueReader {
    double getValue( Lattice2D lattice, int i, int k );

    public class Displacement implements WaveValueReader {
        public double getValue( Lattice2D lattice, int i, int k ) {
//            float value = ( lattice.wavefunction[i][k] + 1.0f ) / 2.0f;
            float value = ( lattice.getValue( i, k ) + 1.0f ) / 2.0f;
            if( value > 1 ) {
                value = 1;
            }
            else if( value < 0 ) {
                value = 0;
            }
            return value;
        }
    }

    public class AverageAbs implements WaveValueReader {
        int numNeighbors;

        public AverageAbs() {
            this( 1 );
        }

        public AverageAbs( int numNeighbors ) {
            this.numNeighbors = numNeighbors;
        }

        public double getValue( Lattice2D lattice, int i, int k ) {
            double value = Math.abs( lattice.getAverageValue( i, k, numNeighbors ) );
            if( value > 1 ) {
                value = 1;
            }
            return value;
        }
    }

    public class Abs implements WaveValueReader {
//        Displacement displacement = new Displacement();

        public double getValue( Lattice2D lattice, int i, int k ) {
            float value = Math.abs( lattice.getValue( i, k ) );
            if( value > 1 ) {
                value = 1;
            }
            else if( value < 0 ) {
                value = 0;
            }
            return value;
        }
    }
}
