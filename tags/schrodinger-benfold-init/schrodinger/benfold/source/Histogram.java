/**
 * Implementation of a histogram as a Sovable object.
 */

class Histogram implements Solvable {
    /**
     * Creates a new histogram.
     *
     * @param    min    The minimum data value to be included
     * @param    max    The maximum data value to be included
     * @param    intervals    The number of intervals into which the range
     * should be divided
     */
    public Histogram( double min, double max, int intervals ) {
        this.min = min;
        this.max = max;
        this.intervals = intervals;
    }


    /**
     * Fills the supplied area with &quot;step function data&quot;.  If
     * plotted, the data will form either a series of horizontal bars, or
     * (if interpolation is enabled on the {@link Plotter Plotter}) the
     * outline of a bar graph.
     */
    public void solve( double x0, double step, double[] solns ) {
        double intSize = ( max - min ) / intervals;
        int[] freqs = getFreqs();
        for( int i = 0; i < solns.length; i++ ) {
            solns[i] = freqs[(int)( ( ( x0 + step * i ) - min ) / intSize )];
        }
    }


    /**
     * Produces a frequency table for the collected data, with intervals
     * as specified in the constructor.
     */
    public int[] getFreqs() {
        double intSize = ( max - min ) / intervals;
        int[] f = new int[intervals];
        for( int i = 0; i < data.length; i++ ) {
            try {
                f[(int)( ( data[i] - min ) / intSize )]++;
            }
            catch( IndexOutOfBoundsException e ) {
            }
        }
        return f;
    }


    /**
     * Returns the raw data
     */
    public double[] getData() {
        return data;
    }

    /**
     * Sets the raw data
     */
    public void setData( double[] d ) {
        data = d;
    }


    /**
     * The raw data to be organized.  This will usually be set via the
     * {@link #setData(double[]) setData()} method.
     */
    protected double[] data;
    protected double min, max;
    protected int intervals;
}