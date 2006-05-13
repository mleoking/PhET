/**
 * A piecewise-constant function with a number of equally spaced square
 * wells, each of equal width and depth.
 */
class MultipleWells extends SquareWell {
    /**
     * Creates a new set of wells
     *
     * @param    depth    The depth of the wells
     * @param    width    The width of each well
     * @param    shift    The <i>x</i> coordinate of the start of the first well
     * @param    period    The distance between the left edges of two adjacent
     * wells
     * @param    nWells    The number of wells
     */
    public MultipleWells( double depth, double width, double shift, double period, int nWells ) {
        super( depth, width, shift );
        this.period = period;
        this.nWells = nWells;
    }


    /**
     * Creates a new set of wells, centred on the origin.
     *
     * @param    depth    The depth of the wells
     * @param    width    The width of each well
     * @param    period    The distance between the left edges of two adjacent
     * wells
     * @param    nWells    The number of wells
     */
    public MultipleWells( double depth, double width, double period, int nWells ) {
        super( depth, width );
        this.period = period;
        this.nWells = nWells;
        reCentre();
    }


    /**
     * Creates a new set of wells with default parameters.
     *
     * @param    nWells    The number of wells
     */
    public MultipleWells( int nWells ) {
        this( DEFAULT_DEPTH, DEFAULT_PERIODIC_WIDTH, DEFAULT_PERIOD, nWells );
    }


    /**
     * Creates a new set of wells with default parameters.
     */
    public MultipleWells() {
        this( DEFAULT_DEPTH, DEFAULT_PERIODIC_WIDTH, DEFAULT_PERIOD, DEFAULT_NWELLS );
    }


    /**
     * @return    <code>depth</code> if x is in a well, else zero.
     */
    public double evaluate( double x ) {
        x += shift;

        int index = (int)( 0.5 + x / period );

        //	Don't apply period for a single well
        if( nWells == 1 ) {
            index = 0;
        }

        if( index < 0 || index >= nWells ) {
            return 0;
        }

        x -= index * period;

        return ( Math.abs( x ) < width / 2 ) ? depth : 0;
    }


    /**
     * Returns an array containing <i>x</i> coordinates of all
     * discontinuities within the specified interval.
     */
    public double[] getMarkers( double minX, double maxX ) {
        // Just return all markers
        double[] d = new double[nWells * 2];

        for( int i = 0; i < nWells; i++ ) {
            d[2 * i] = -shift + i * period - width / 2;
            d[2 * i + 1] = -shift + i * period + width / 2;
        }

        return d;
    }


    /**
     * Adjusts the <code>shift</code> parameter so that the wells are
     * centered on the origin.  In addition, if the width exceeds the period,
     * it will be set to half the period (unless there is only one well, of
     * course).
     * <p/>
     * This method is intended to be used after a change to the period or
     * number of wells.
     */
    public void reCentre() {
        shift = ( nWells - 1 ) * period / 2;
        if( nWells > 1 && width >= period ) {
            width = period / 2;
        }
    }


    /**
     * Returns the number of wells
     */
    public int getWellCount() {
        return nWells;
    }


    /**
     * Sets the number of wells, and calls {@link #reCentre() reCentre()} if
     * the number jas changed.
     */
    public void setWellCount( int i ) {
        if( nWells == i ) {
            return;
        }
        nWells = i;
        reCentre();
    }


    /**
     * Returns the period
     */
    public double getPeriod() {
        return period;
    }

    /**
     * Sets the period
     */
    public void setPeriod( double d ) {
        period = d;
    }

    /**
     * Returns the minimum value this function can take
     */
    public double getLow() {
        return Math.min( depth, 0 );
    }

    /**
     * Returns the maximum value this function can take
     */
    public double getHigh() {
        return Math.max( depth, 0 );
    }


    /**
     * The period
     */
    protected double period;

    /**
     * The number of wells
     */
    protected int nWells;

    /**
     * The default width when there is more than one well
     */
    public static double DEFAULT_PERIODIC_WIDTH = 1;

    /**
     * The default period
     */
    public static double DEFAULT_PERIOD = 2;

    /**    The default number of wells	*/
	public static int DEFAULT_NWELLS=2;
}
