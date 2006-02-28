/**
 * Performs numerical integration of the time-dependend Schrodinger equation.
 */
class TimeDepSchrodinger implements Solvable, TimeDependent {
    /**
     * @param    fn    The potential function to use
     */
    public TimeDepSchrodinger( Function fn ) {
        this.potFn = fn;
        this.energy = 1;
        this.x0 = -8;
        this.width = 10;
    }


    /**
     * Creates a new instance, using a single square well for the potential
     * function.
     */
    public TimeDepSchrodinger() {
        this( new SquareWell() );
    }


    /**
     * Resets the model, replacing the function data with a new gaussian.
     *
     * @param    x    The smallest <i>x</i>-value to consider
     * @param    h    The gap between <i>x</i>-values
     * @param    steps    The number of <i>x</i>-values to consider
     */
    public void initFunction( double x, double h, int steps ) {
        this.h = h;
        psi = new Complex[steps];
        pot = new double[steps];
        for( int i = 0; i < steps; i++ ) {
            psi[i] = Complex.e( new Complex( -( x - x0 ) * ( x - x0 ) / width, ( x - x0 ) * energy ) );
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
    //	Performs one step of rk4 (for every x)
    //
    //	d(psi)/dt = alpha * d(psi)^2/dx^2 + beta * v(x)*psi
    //	Note that d(psi)/dt has no direct t dependency
    //
    //	alpha = i*hbar/(2*mu);	beta = -i/hbar
    protected void advanceOnce( double dt ) {
        int len = psi.length;

        //System.err.println("t = "+t);
        //System.err.println("h = "+h);

        Complex alpha = new Complex( 0, 1 );
        Complex beta = new Complex( 0, -1 );

        //	Precalculate beta*pot[i]
        Complex[] betaPot = new Complex[psi.length];
        for( int i = 0; i < betaPot.length; i++ ) {
            betaPot[i] = beta.multiply( pot[i] );
        }
        //System.err.println("beta*pot at i=len/2 is:  "+betaPot[len/2]);

        Complex[] newPsi = new Complex[len];
        Complex[] k1 = new Complex[len];
        Complex[] k2 = new Complex[len];
        Complex[] k3 = new Complex[len];
        Complex[] k4 = new Complex[len];

        //	First step
        for( int i = 0; i < len; i++ ) {
            k1[i] = alpha.multiply( xderiv( psi, i ) ).add( betaPot[i].multiply( psi[i] ) );
        }
        for( int i = 0; i < len; i++ ) {
            newPsi[i] = psi[i].add( k1[i].multiply( dt / 2 ) );
        }

        //	Second step
        for( int i = 0; i < len; i++ ) {
            k2[i] = alpha.multiply( xderiv( newPsi, i ) ).add( betaPot[i].multiply( newPsi[i] ) );
        }
        for( int i = 0; i < len; i++ ) {
            newPsi[i] = psi[i].add( k2[i].multiply( dt / 2 ) );
        }

        //	Third step
        for( int i = 0; i < len; i++ ) {
            k3[i] = alpha.multiply( xderiv( newPsi, i ) ).add( betaPot[i].multiply( newPsi[i] ) );
            //if(i==len/4)
            //	System.err.println("At i=len/4,\n   newPsi[i-1] = "+newPsi[i-1]+"\n   newPsi[i] = "+newPsi[i]+"\n   newPsi[i+1] = "+newPsi[i+1]+"\n   xderiv = "+xderiv(newPsi,i));
        }
        for( int i = 0; i < len; i++ ) {
            newPsi[i] = psi[i].add( k3[i].multiply( dt ) );
        }

        //	Final step
        for( int i = 0; i < len; i++ ) {
            k4[i] = alpha.multiply( xderiv( newPsi, i ) ).add( betaPot[i].multiply( newPsi[i] ) );
        }
        for( int i = 0; i < len; i++ ) {
            newPsi[i] = k1[i].add( k2[i].multiply( 2 ) ).add( k3[i].multiply( 2 ) ).add( k4[i] ).multiply( dt / 6 ).add( psi[i] );
        }

        //int i = len/4;
        //System.err.println("at 1/4 of x-range,\n   k1="+k1[i]+"\n   k2="+k2[i]+"\n   k3="+k3[i]+"\n   k4="+k4[i]+"\n  chg="+newPsi[i].subtract(psi[i]));
        psi = newPsi;
        //System.err.println(psi[i]);
        t += dt;
    }


    /**
     * Measures the <em>second derivative</em> with respect to <i>x</i>
     * using a	finite difference method.  If the region considered touches
     * the edge of the	function data (so <code>i &lt;= 1</code> or
     * <code>i &gt;= c.length-2</code>, zero will be returned.
     *
     * @param    c    The function data on which to operate
     * @param    i    The index at which the derivative is required
     * @return (c[i+1]+c[i-1]-2c[i])/(h*h)
     */
    protected Complex xderiv( Complex[] c, int i ) {
        if( i <= 1 ) {
            return Complex.ZERO;
        }
        if( i >= c.length - 2 ) {
            return Complex.ZERO;
        }

        return c[i].multiply( -2 ).add( c[i - 1] ).add( c[i + 1] ).divide( h * h );
    }


    /**
     * Performs one step of the Runge-Kutta method, using the member
     * variable {@link #dt dt} to determine the size of the step in
     * the time direction.
     */
    public void advanceOnce() {
        advanceOnce( this.dt );
    }


    /**
     * Since all the work is done in {@link #advanceOnce(double)
     * advanceOnce()}, this method need only return the (modulus squared of
     * the) function data. 	Since it is not practical to adjust the size
     * or <i>x</i>-spacing	of the data in the array while the simulation is
     * running, the <code>x</code> and <code>h</code> parameters are ignored.
     *
     * @throws RuntimeException    if <code>vals</code> is not the same
     * size as the array of function data
     */
    public void solve( double x, double h, double[] vals ) {
        if( vals.length != psi.length ) {
            throw new RuntimeException( "vals.length != psi.length" );
        }

        //	Now get soln data to return
        for( int i = 0; i < vals.length; i++ ) {
            vals[i] = psi[i].modulusSquared();
        }
    }


    /**
     * Approximates the integral of the function, by summing the (modulus
     * squared of the) function data and dividing by <code>h</code>.
     */
    public double integralCheck() {
        double total = 0;
        for( int i = 0; i < psi.length; i++ ) {
            total += psi[i].modulusSquared();
        }
        total *= h;
        return total;
    }


    /**
     * Returns the simulation time
     */
    public double getT() {
        return t;
    }

    /**
     * Sets the simulation time.  This will not affect the simulation data
     * in any way, just as setting your watch to midnight won't make it dark
     * outside.
     */
    public void setT( double t ) {
        this.t = t;
    }

    /**
     * Returns the value of the energy parameter
     */
    public double getEnergy() {
        return energy;
    }

    /**
     * Sets the value of the energy parameter
     */
    public void setEnergy( double d ) {
        this.energy = d;
    }

    /**
     * Returns the position of the initial gaussian
     */
    public double getX0() {
        return x0;
    }

    /**
     * Sets the position of the initial gaussian
     */
    public void setX0( double d ) {
        x0 = d;
    }

    /**
     * Returns the width of the initial gaussian
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets the width of the initial gaussian
     */
    public void setWidth( double d ) {
        width = d;
    }


    /**
     * The function data
     */
    protected Complex[] psi;


    protected double energy, x0, width;
    protected double t;
    protected double dt = 0.01;
    protected double h;

    /**
     * The potential function
     */
    protected Function potFn;

    /**    A cache of the values of the potential function	*/
	protected double[] pot;
}