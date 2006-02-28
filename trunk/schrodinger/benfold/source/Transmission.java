/**
 * Uses a matrix method to calculate transmission for a finite number of
 * wells.  Despite the name, this class not only calculates transmission,
 * but also reflection.
 */
class Transmission implements Solvable {
    /**
     * Create a new transmission calculator
     *
     * @param    eqn    The potential function
     */
    public Transmission( MultipleWells eqn ) {
        this.eqn = eqn;
    }


    /**
     * Constructs a matrix to represent the change in boundary conditions at
     * an interface (discontinuous change in potential).  The matrices for
     * the two interfaces of a potential well may be multiplied to produce a
     * single matrix for the well.  Similarly, the matrices for a number of
     * wells may be multiplied.
     *
     * @param    v1    The potential before the interface
     * @param    v2    The potential after the interface
     * @param    energy    The energy
     * @param    x    The <i>x</i> coordinate of the interface
     */
    protected ComplexMatrix getInterfaceMatrix( double v1, double v2, double energy, double x ) {
        double a = 1;

        Complex k1 = new Complex( a * ( energy - v1 ) ).sqrt();
        Complex k2 = new Complex( a * ( energy - v2 ) ).sqrt();

        Complex ratio = k1.divide( k2 );
        Complex sum = k1.add( k2 );
        Complex diff = k1.subtract( k2 );

        ComplexMatrix m = new ComplexMatrix(
                ratio.add( 1 ), ratio.negate().add( 1 ),
                ratio.negate().add( 1 ), ratio.add( 1 )
        );

        Complex ix = Complex.I.multiply( x );

        m.a = m.a.multiply( Complex.e( ix.multiply( diff ) ) );
        m.b = m.b.multiply( Complex.e( ix.multiply( sum.negate() ) ) );
        m.c = m.c.multiply( Complex.e( ix.multiply( sum ) ) );
        m.d = m.d.multiply( Complex.e( ix.multiply( diff.negate() ) ) );

        return m.multiply( 0.5 );
    }


    /**
     * Computes the matrix to represent the change in boundary conditions
     * for all the wells in the potential function.
     *
     * @param    energy    The energy to use
     */
    protected ComplexMatrix getMatrix( double energy ) {
        int nWells = eqn.getWellCount();
        double period = eqn.getPeriod();
        double width = eqn.getWidth();

        ComplexMatrix m = new ComplexMatrix();

        for( int i = 0; i < nWells && !abort; i++ ) {
            ComplexMatrix rising = getInterfaceMatrix( eqn.getLow(), eqn.getHigh(), energy, i * period );
            ComplexMatrix falling = getInterfaceMatrix( eqn.getHigh(), eqn.getLow(), energy, i * period + width );
            ComplexMatrix barrier = falling.multiply( rising );//rising.multiply(falling);

            m = barrier.multiply( m );
        }

        return m;
    }


    /**
     * Returns the transmission coefficient for the specified energy
     */
    public double getTransmission( double energy ) {
        return getTransmission( getMatrix( energy ) );
    }

    /**
     * Returns the reflection coefficient for the specified energy
     */
    public double getReflection( double energy ) {
        return getReflection( getMatrix( energy ) );
    }

    public void solve( double x0, double step, double[] solns ) {
        abort = false;
        synchronized( this ) {
            if( cache == null ) {
                cache = new ComplexMatrix[solns.length];
                for( int i = 0; i < solns.length && !abort; i++, x0 += step ) {
                    cache[i] = getMatrix( x0 );
                    if( i % 16 == 0 ) {
                        try {Thread.sleep( 2 );}catch( InterruptedException e ) {}
                    }
                    Thread.yield();
                }
                if( abort ) {
                    cache = null;
                    return;
                }
            }

            for( int i = 0; i < solns.length; i++ ) {
                solns[i] = wantTransmission ? getTransmission( cache[i] ) : getReflection( cache[i] );
            }
        }
    }


    /**
     * Emptys the cache of solutions.  Usually called after a change in the
     * parameters of the potential function.
     */
    public synchronized void flush() {
        cache = null;
    }


    /**
     * Aborts a call to {@link #solve(double,double,double[]) solve()}
     * currently in progress; that method will return at the next convenient
     * opportunity, without supplying any solution data, and discarding any
     * partial results.
     */
    public void abortSolve() {
        abort = true;
        //System.err.println("aborted");
    }

    //	Test harness
    /*
     public static void main(String[] args)
     {
         int nWells = 1;
         MultipleWells eqn = new MultipleWells(nWells);
         eqn.setDepth(5);
         double energy = 1;
         Transmission t = new Transmission(eqn);
         System.err.println("Wells:  "+nWells);

         ComplexMatrix rising = t.getInterfaceMatrix(eqn.getLow(), eqn.getHigh(), energy, 1);
         ComplexMatrix falling = t.getInterfaceMatrix(eqn.getHigh(), eqn.getLow(), energy, 2);
         ComplexMatrix barrier = falling.multiply(rising);//rising.multiply(falling);

         System.err.println("rising matrix :  "+rising);
         System.err.println("falling matrix:  "+falling);
         System.err.println("barrier matrix:  "+barrier);
         System.err.println("reflection:	"+getReflection(barrier));
         System.err.println("transmission:	"+getTransmission(barrier));
     }*/


    /**
     * Extracts the transmission coefficient from a matrix
     */
    protected static double getTransmission( ComplexMatrix m ) {
        return m.a.reciprocal().modulusSquared();
    }

    /**
     * Extracts the reflection coefficient from a matrix
     */
    protected static double getReflection( ComplexMatrix m ) {
        return m.c.divide( m.a ).modulusSquared();
    }


    /**
     * A cache of the solution data
     */
    protected ComplexMatrix[] cache;

    /**
     * Set this to <code>true</code> if {@link #solve(double,double,double[])
     * solve()} should return transmission coefficients, <code>false</code>
     * for reflection.
     */
    public boolean wantTransmission = true;

    /**
     * Flag used internally to indicate that {@link
     * #solve(double,double,double[]) solve()} should abort at the next
     * available opportunity.
     */
    protected boolean abort = false;

    /**    The potential function	*/
	protected MultipleWells eqn;
}