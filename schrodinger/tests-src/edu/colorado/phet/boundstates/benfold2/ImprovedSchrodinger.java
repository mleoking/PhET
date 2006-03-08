/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.benfold2;

/**
 * Numerically integrates the Schrodinger equation.
 */
public class ImprovedSchrodinger extends Schrodinger {
    /**
     * Creates a new Schrodinger solver
     */
    public ImprovedSchrodinger( Function f ) {
        super( f );
        this.cache = new SolnCache( new SolvFunction( f ) );
    }


    public void solve( double x, double h, double[] vals ) {
        double[] pot = new double[2 * vals.length];
        cache.solve( x, h / 2, pot );

        double y, w;
        w = firstValue;
        y = 0;

        double a = -1;//-_2m_over_h_bar_sq;

        for( int i = 1; i < vals.length; i++ ) {
            double k1, k2, k3, k4;
            double j1, j2, j3, j4;

            k1 = a * ( energy - pot[2 * ( i - 1 )] ) * w;
            j1 = y;

            k2 = a * ( energy - pot[2 * i - 1] ) * ( w + j1 * h / 2 );
            j2 = y + k1 * h / 2;

            k3 = a * ( energy - pot[2 * i - 1] ) * ( w + j2 * h / 2 );
            j3 = y + k2 * h / 2;

            k4 = a * ( energy - pot[2 * i] ) * ( w + j3 * h );
            j4 = y + k3 * h;

            x += h;
            y += ( h / 6 ) * ( k1 + 2 * k2 + 2 * k3 + k4 );
            w += ( h / 6 ) * ( j1 + 2 * j2 + 2 * j3 + j4 );
            vals[i] = w;
        }

    }

    public void setPotentialFunction( Function f ) {
        this.pot = f;
        cache = new SolnCache( new SolvFunction( f ) );
    }


    protected SolnCache cache;
}
