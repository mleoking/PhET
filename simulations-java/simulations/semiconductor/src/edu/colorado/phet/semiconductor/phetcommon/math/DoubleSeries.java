package edu.colorado.phet.semiconductor.phetcommon.math;


/**
 * A tool for keeping track of the last n data points, and averaging over them.
 */
public class DoubleSeries {
    TimeSeries s;

    public DoubleSeries( int length ) {
        s = new TimeSeries( length );
    }

    public void add( double d ) {
        s.add( new Double( d ) );
    }

    public double sum() {
        Double[] d = (Double[])this.s.toArray( new Double[0] );
        double avg = 0;
        for( int i = 0; i < d.length; i++ ) {
            avg += d[i].doubleValue();
        }
        return avg;
    }

    public double average() {
        return sum() / s.length();
    }
}
