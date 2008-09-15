package edu.colorado.phet.balloons;

import java.util.Vector;

public class DoubleSeries {
    TruncatedSeries s;

    public DoubleSeries( int length ) {
        s = new TruncatedSeries( length );
    }

    public void add( double d ) {
        s.add( new Double( d ) );
    }

    public double sum() {
        Vector v = s.get();
        double avg = 0;
        for( int i = 0; i < v.size(); i++ ) {
            avg += ( (Double)v.get( i ) ).doubleValue();
        }
        return avg;
    }

    public double average() {
        return sum() / ( (double)s.length() );
    }
}
