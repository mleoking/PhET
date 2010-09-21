/**
 * Class: BandpassFilter
 * Class: edu.colorado.phet.filter
 * User: Ron LeMaster
 * Date: Oct 12, 2003
 * Time: 4:52:57 PM
 */
package edu.colorado.phet.greenhouse.filter;

public class BandpassFilter extends Filter1D {
    private double low;
    private double high;

    public BandpassFilter( double low, double high ) {
        this.low = low;
        this.high = high;
    }

    public boolean passes( double value ) {
        return value >= low && value <= high;
    }
}
