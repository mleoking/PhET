/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.benfold2;

/**
 * Numerically integrates the Schrodinger equation.
 */
public class ImprovedSchrodinger extends Schrodinger {
    protected SolnCache cache;

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

    /**
     * Maintains a backup of the previous solution, and the <code>solve()</code>
     * parameters used to obtain it.  Subsequent calls will return a copy of the
     * stored solution data, provided the parameters are the same, and that the
     * cache has not been marked invalid.
     */
    class SolnCache implements Solvable {
        /**
         * @param src The object to obtain solution data from
         */
        public SolnCache( Solvable src ) {
            this.src = src;
        }

        public void solve( double x0, double step, double[] vals ) {
            synchronized( this ) {
                if( ( cache == null ) || ( x0 != last_x0 ) || ( step != last_step ) || ( vals.length != cache.length ) ) {
                    cache = new double[vals.length];
                    src.solve( last_x0 = x0, last_step = step, cache );
                }
                System.arraycopy( cache, 0, vals, 0, vals.length );
            }
        }


        /**
         * Requests that the cache be marked invalid.  This will typically be
         * called after a parameter of the original <code>Solvable</code> has
         * changed.
         */
        public void invalidate() {
            cache = null;
            last_x0 = last_step = Double.NaN;
        }


        /**
         * The <code>Solvable</code> object to obtain new data from
         */
        protected Solvable src;

        /**
         * The value of <code>x0</code> for which the cache is valid
         */
        protected double last_x0;

        /**
         * The value of <code>step</code> for which the cache is valid
         */
        protected double last_step;

        /**
         * The cache of solution data
         */
        protected double[] cache;
    }

    /**
     * Simple implementation of {@link edu.colorado.phet.boundstates.benfold2.Solvable Solvable} for a {@link edu.colorado.phet.boundstates.benfold2.Function
     * Function}.  This class is intended to be used for the plotting of simple
     * functions.
     */
    public class SolvFunction implements Solvable {
        /**
         * @param f The function to be used when producing results
         */
        public SolvFunction( Function f ) {
            this.function = f;
        }


        /**
         * Simply evaluates the function at each value of x0.
         */
        public void solve( double x0, double step, double[] vals ) {
            for( int i = 0; i < vals.length; i++ ) {
                vals[i] = function.evaluate( x0 );
                x0 += step;
            }
        }


        protected Function function;
    }

}
