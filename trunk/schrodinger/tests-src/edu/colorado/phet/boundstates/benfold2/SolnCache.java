/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.benfold2;

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
