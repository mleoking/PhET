/**
 * Thread to search an interval for a solution to a Schrodinger equation.
 */
class SolnSearcher extends Thread {
    /**
     * Creates a new solution searcher
     *
     * @param    owner    The <code>EnergyListener</code> to be notified when
     * the best estimate changes
     * @param    eqn        The equation to solve
     * @param    min        A lower bound for the solution
     * @param    max        An upper bound for the solution
     * @param    intRange    The range over which the solution should be
     * evaluated
     * @param    steps    The number of steps to be used when evaluating the
     * solution
     */
    public SolnSearcher( EnergyListener owner, Schrodinger eqn, double min, double max, double intRange, int steps ) {
        this.owner = owner;
        this.eqn = eqn;
        this.min = min;
        this.max = max;
        this.intRange = intRange;
        this.steps = steps;
        this.hint = eqn.getEnergy();
    }


    public void run() {
        //System.err.println("Started solving...");
        minValue = tryEnergy( min );
        maxValue = tryEnergy( max );

        while( !killed ) {
            owner.energyChanged( getEstimate() );
            improveEstimate();
            try {
                Thread.sleep( 5 );
            }
            catch( InterruptedException e ) {
                break;
            }
        }

        //System.err.println("...finished");
        finished = true;
        owner.searchFinished();
    }


    /**
     * Performs one step of the search.
     */
    protected void improveEstimate() {
        double energy = ( min + max ) / 2;

        //	Check for fp underflow
        if( energy == min || energy == max ) {
            killed = true;
        }

        double newValue = tryEnergy( energy );
        boolean low, high;


        high = ( maxValue > 0 && newValue < 0 ) || ( maxValue < 0 && newValue > 0 );
        low = ( minValue > 0 && newValue < 0 ) || ( minValue < 0 && newValue > 0 );


        if( high == low ) {
            high = hint > energy;
        }

        if( high ) {
            min = energy;
            minValue = newValue;
            return;
        }
        else {
            max = energy;
            maxValue = newValue;
            return;
        }

    }


    /**
     * Attempts to solve the equation with the specified energy, and returns
     * the value at the end of the <i>x</i> range.  For a solution to the
     * Schrodinger equation, this method should return (approximately) zero.
     */
    public double tryEnergy( double energy ) {
        eqn.setEnergy( energy );
        double[] soln = new double[steps + 1];
        double stepSize = intRange / steps;
        eqn.solve( -intRange / 2, stepSize, soln );
        return soln[steps] - soln[1];
    }


    /**
     * Returns the current &quot;best estimate&quot; of the solution;
     * that is, the midpoint of the bisection interval.
     */
    public double getEstimate() {
        return ( min + max ) / 2;
    }


    /**
     * Returns <code>true</code> if the search is still in process, else
     * <code>false</code>
     */
    public boolean isFinished() {
        return finished && !isAlive();
    }


    /**
     * Requests that the search terminate at the next convenient opportunity.
     */
    public void kill() {
        killed = true;
        interrupt();
    }


    /**
     * If neither half of the interval is guaranteed to contain a solution,
     * the search will move towards this value, which is initialised from the
     * original energy parameter of the equation.
     */
    protected double hint;
    protected double min, max, minValue, maxValue, intRange;
    protected int steps;
    protected boolean finished;
    protected boolean killed;
    protected Schrodinger eqn;
    protected EnergyListener owner;
}
