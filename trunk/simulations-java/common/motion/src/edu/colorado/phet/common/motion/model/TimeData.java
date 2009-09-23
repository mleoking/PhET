package edu.colorado.phet.common.motion.model;

import java.text.DecimalFormat;

public class TimeData {
    private final double value;
    private final double time;

    public TimeData( double value, double time ) {
        if ( Double.isNaN( value ) ) {
            throw new IllegalArgumentException( "value was nan" );
        }
        if ( Double.isNaN( time ) ) {
            throw new IllegalArgumentException( "time was nan" );
        }
        this.value = value;
        this.time = time;
    }

    public double getValue() {
        return value;
    }

    public double getTime() {
        return time;
    }

    public String toString() {
        return "t=" + time + ", value=" + value;
//        return "value=" + value + ", time=" + time;
    }

    public String toString( DecimalFormat decimalFormat ) {
        return "t=" + decimalFormat.format( time ) + ", value=" + decimalFormat.format( value );
    }
}
