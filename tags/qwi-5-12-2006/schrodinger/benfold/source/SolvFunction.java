/**
 * Simple implementation of {@link Solvable Solvable} for a {@link Function
 * Function}.  This class is intended to be used for the plotting of simple
 * functions.
 */
class SolvFunction implements Solvable {
    /**
     * @param    f    The function to be used when producing results
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