/**
 * This class will measure the width of a gaussian.
 * The supplied data will first be searched for a maximum, then bisection
 * will be used to find the two &quot;half-height&quot; points.  The
 * distance between these two points will be taken to be the width of the
 * gaussian.
 * <p/>
 * No attempt will be made to determine whether the data actually follows
 * a gaussian distribution.  If the data contains more than one
 * &quot;turning&quot; point, the results are undefined.
 */
class GaussianWidthFinder {
    /**
     * Creates a new GaussianWidthFinder, to operate on the specified array
     * of values.
     */
    public GaussianWidthFinder( double[] data ) {
        this.data = data;
    }


    /**
     * Returns the width of the gaussian, assuming the centre is known.
     *
     * @param    centre    The centre of the gaussian
     * @param    rightmost    The highest index for which <code>data[]</code>
     * contains valid data.
     * @return The width of the guassian, measured in array storage
     * locations
     */
    public int getWidth( int centre, int rightMost ) {
        if( --rightMost <= 0 ) {
            return 0;
        }

        double midH = data[centre] / 2;
        int left = bisect( 0, centre, midH );
        int right = bisect( centre, rightMost, midH );

        if( left == 0 || right == rightMost || left == centre || right == centre || ( right - left ) < 0 ) {
            return 0;
        }

        return right - left;
    }


    /**
     * @return The width of the guassian, measured in array storage
     * locations
     */
    public int getWidth() {
        return getWidth( getMaxIndex(), data.length );
    }


    /**
     * Searches for the index of the largest value
     */
    protected int getMaxIndex() {
        int max = 0;
        double maxVal = -1;
        for( int i = 0; i < data.length; i++ ) {
            if( data[i] > maxVal ) {
                maxVal = data[max = i];
            }
        }
        return max;
    }


    /**
     * Searches the specified interval for a value.
     *
     * @param    left    The index corresponding to the minimum bound
     * @param    right    The index corresponding to the maximum bound
     * @param    target    The value to search for
     */
    protected int bisect( int left, int right, double target ) {
        int mid = ( left + right ) / 2;

        while( mid != left && mid != right ) {
            if( data[mid] < target != data[right] < target ) {
                left = mid;
                mid = ( left + right ) / 2;
                continue;
            }
            if( data[mid] < target != data[left] < target ) {
                right = mid;
                mid = ( left + right ) / 2;
                continue;
            }

            return 0;
        }

        return mid;
    }


    /**    Local copy of the gaussian data	*/
    protected double[] data;
}