/**
 * Class to model the Haynes-Shockley experiment.  Uses the fourth-order
 * Runge-Kutta method to interpolate across time.
 */

class HaynesShockley extends TimeDepSchrodinger {
    public HaynesShockley() {
        x0 = 0;
        width = 2;
        field = 0;
        lifetime = 1;
    }


    /**
     * Resets the model, replacing the function data with a new square wave.
     *
     * @param    x    The smallest <i>x</i>-value to consider
     * @param    h    The gap between <i>x</i>-values
     * @param    steps    The number of <i>x</i>-values to consider
     */
    public void initFunction( double x, double h, int steps ) {
        this.h = h;
        p = new double[steps];
        pot = new double[steps];
        for( int i = 0; i < steps; i++ ) {
            /*
                   p[i] = Math.exp(-(x-x0)*(x-x0)/width);
                   Old formula is for Gaussian.  We now want a square wave.
               */
            double d = 2 * ( x - x0 );
            p[i] = ( d >= -width && d <= width ) ? 1 : 0;
            pot[i] = potFn.evaluate( x );
            x += h;
        }
        t = 0;
    }


    /**
     * Performs one step of the Runge-Kutta method.
     *
     * @param    dt    The size of the advancement in time
     */
    protected void advanceOnce( double dt ) {
        int len = p.length;

        double mu = 1;
        double dp = 1;
        double pno = 0;
        double tp = lifetime;
        double e = field;

        double[] newP = new double[len];
        double[] k1 = new double[len];
        double[] k2 = new double[len];
        double[] k3 = new double[len];
        double[] k4 = new double[len];

        //	First step
        for( int i = 0; i < len; i++ ) {
            k1[i] = -2 * mu * e * xderiv( p, i ) + dp * xderiv2( p, i ) - ( p[i] - pno ) / tp;
        }
        for( int i = 0; i < len; i++ ) {
            newP[i] = p[i] + k1[i] * dt / 2;
        }

        //	Second step
        for( int i = 0; i < len; i++ ) {
            k2[i] = -2 * mu * e * xderiv( newP, i ) + dp * xderiv2( newP, i ) - ( newP[i] - pno ) / tp;
        }
        for( int i = 0; i < len; i++ ) {
            newP[i] = p[i] + k2[i] * dt / 2;
        }

        //	Third step
        for( int i = 0; i < len; i++ ) {
            k3[i] = -2 * mu * e * xderiv( newP, i ) + dp * xderiv2( newP, i ) - ( newP[i] - pno ) / tp;
        }
        for( int i = 0; i < len; i++ ) {
            newP[i] = p[i] + k3[i] * dt;
        }

        //	Final step
        for( int i = 0; i < len; i++ ) {
            k4[i] = -2 * mu * e * xderiv( newP, i ) + dp * xderiv2( newP, i ) - ( newP[i] - pno ) / tp;
        }
        for( int i = 0; i < len; i++ ) {
            newP[i] = p[i] + ( k1[i] + k2[i] * 2 + k3[i] * 2 + k4[i] ) * dt / 6;
        }

        //int i = len/4;
        //System.err.println("at 1/4 of x-range,\n   k1="+k1[i]+"\n   k2="+k2[i]+"\n   k3="+k3[i]+"\n   k4="+k4[i]+"\n  chg="+newPsi[i].subtract(psi[i]));
        p = newP;
        //System.err.println(psi[i]);
        t += dt;
    }


    /**
     * Measures the derivative with respect to <i>x</i> using a finite
     * difference method.  If the region considered touches the edge of the
     * function data (so <code>i &lt;= 1</code> or
     * <code>i &gt;= d.length-2</code>, zero will be returned.
     *
     * @param    d    The function data on which to operate
     * @param    i    The index at which the derivative is required
     * @return (d[i+1]-d[i-1])/2h
     */
    protected double xderiv( double[] d, int i ) {
        if( i <= 1 || i >= d.length - 2 ) {
            return 0;
        }
        return ( d[i + 1] - d[i - 1] ) / ( 2 * h );
    }


    /**
     * Measures the second derivative with respect to <i>x</i> using a
     * finite difference method.  If the region considered touches the edge
     * of the	function data (so <code>i &lt;= 1</code> or
     * <code>i &gt;= d.length-2</code>, zero will be returned.
     *
     * @param    d    The function data on which to operate
     * @param    i    The index at which the derivative is required
     * @return (d[i+1]+d[i-1]-2d[i])/(h*h)
     */
    protected double xderiv2( double[] d, int i ) {
        if( i <= 1 || i >= d.length - 2 ) {
            return 0;
        }
        return ( -2 * d[i] + d[i - 1] + d[i + 1] ) / ( h * h );
    }


    /**
     * Since all the work is done in {@link #advanceOnce(double)
     * advanceOnce()}, this method need only return the function data.
     * Since it is not practical to adjust the size or <i>x</i>-spacing
     * of the data in the array while the simulation is running, the
     * <code>x</code> and <code>h</code> parameters are ignored.
     *
     * @throws RuntimeException    if <code>vals</code> is not the same
     * size as the array of function data
     */
    public void solve( double x, double h, double[] vals ) {
        if( vals.length != p.length ) {
            throw new RuntimeException( "vals.length != p.length" );
        }

        //	Now get soln data to return
        for( int i = 0; i < vals.length; i++ ) {
            vals[i] = p[i];
        }
    }


    /**
     * Approximates the integral of the function, by summing the function
     * data and dividing by <code>h</code>.
     */
    public double integralCheck() {
        double total = 0;
        for( int i = 0; i < p.length; i++ ) {
            total += p[i];
        }
        total *= h;
        return total;
    }


    /**
     * Returns the selected field strength
     */
    public double getField() {
        return field;
    }

    /**
     * Sets the selected field strength
     */
    public void setField( double d ) {
        field = d;
    }


    /**
     * Returns the selected carrier lifetime
     */
    public double getLifetime() {
        return lifetime;
    }

    /**
     * Sets the selected carrier lifetime
     */
    public void setLifetime( double d ) {
        lifetime = d;
    }


    /**
     * The carrier lifetime
     */
    protected double lifetime;
    /**
     * The field strength
     */
    protected double field;
    /**    The function data	*/
	protected double[] p;
}
