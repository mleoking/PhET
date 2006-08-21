/**
 * Implements a circular buffer, intended to be used where there is
 * a constant stream of data (such as observations over time).
 */
class TimeGraph implements Solvable {
    /**
     * Creates a new TimeGraph
     *
     * @param    obsCount    The size of the &quot;history&quot; of
     * observations
     */
    public TimeGraph( int obsCount ) {
        observations = new double[obsCount];
        pos = 0;
    }


    /**
     * Adds a value to the list of observations.  If the buffer is full,
     * the oldest observation will be replaced by this one.
     */
    public void addObservation( double value ) {
        if( skip-- != 0 ) {
            return;
        }

        if( value > max ) {
            max = value;
            maxIndex = pos;
        }

        skip = skipRatio;
        observations[pos++] = value;
        if( pos == observations.length ) {
            pos = 0;
        }
        observations[pos] = Double.NaN;
    }


    /**
     * The <code>x0</code> and <code>step</code> parameters are ignored;
     * this method will always return the array of observations.
     *
     * @throws RuntimeException    If the size of the supplied array does
     * not match that of the buffer
     */
    public void solve( double x0, double step, double[] vals ) {
        if( vals.length != observations.length ) {
            throw new RuntimeException( "array sizes don't match:  vals.length!=observations.length" );
        }

        for( int i = 0; i < vals.length; i++ ) {
            vals[i] = observations[i];
        }
    }


    /**
     * Returns the current &quot;time&quot;.
     *
     * @param    x0        The &quot;time&quot; of the first observation
     * @param    step    The &quot;time&quot; between observations
     * @return    <code>x0 + step*pos</code>
     */
    public double getCurrentPos( double x0, double step ) {
        return x0 + step * pos;
    }


    /**
     * Clears all observations, and returns the position marker to the start
     * of the buffer.
     */
    public void reset() {
        max = maxIndex = pos = skip = 0;
        observations = new double[observations.length];
    }


    /**
     * Converts an array index to an <i>x</i> coordinate, given the size of
     * the interval into which all coordinates should fall.
     *
     * @param    index    The array index
     * @param    min        The minimum bound of the interval
     * @param    max        The maximum bound of the interval
     */
    public double indexToX( int index, double min, double max ) {
        return min + index * ( max - min ) / observations.length;
    }


    /**
     * Returns all observations.  The observations will be in sequence, save
     * for a discontinuity at the &quot;now&quot; position.
     */
    public double[] getObservations() {
        return observations;
    }


    /**
     * Returns the number of observations that will be ignored for every one
     * used.  This is useful as a simple control for the sampling rate.
     */
    public int getSkipRatio() {
        return skipRatio;
    }


    /**
     * Sets the number of observations that will be ignored for every one
     * used.  This is useful as a simple control for the sampling rate.
     */
    public void setSkipRatio( int i ) {
        skipRatio = i;
    }


    /**
     * The index of the largest data value seen so far.  This index will
     * still point to the position where the observation occurred, even if
     * the actual data has been replaced by newer observations.
     */
    protected int maxIndex;


    /**
     * The largest data value seen so far.  There is no guarantee that this
     * value is still in the buffer, and has not been replaced by a newer
     * observation.
     */
    protected double max;


    /**
     * The number of observations that will be ignored for every one
     * used.  This is useful as a simple control for the sampling rate.
     */
    protected int skipRatio = 20;


    /**
     * The current position in the buffer.
     */
    protected int pos;

    /**
     * The number of observations to ignore before an observation is used.
     * This value will be decrement as observations are discarded, and reset
     * to <code>skipRatio</code> when a value is used.
     */
    protected int skip;

    /**    The buffer of observations	*/
	protected double[] observations;
}