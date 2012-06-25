package edu.colorado.phet.functions.buildafunction;

import lombok.Data;

/**
 * @author Sam Reid
 */
public @Data class LinearFunction {
    public final int m;
    public final int b;

    public LinearFunction times( final int scale ) {
        return new LinearFunction( m * scale, b * scale );
    }

    public String toString() {
        if ( b != 0 ) {
            if ( m == 0 ) {
                return "" + b;
            }
            else if ( m == 1 ) {
                return "x + " + b;
            }
            else {
                return m + "x + " + b;
            }
        }
        else {
            if ( m == 0 ) {
                return "0";
            }
            else if ( m == 1 ) {
                return "x";
            }
            else {
                return m + "x";
            }
        }
    }

    public LinearFunction plus( final int i ) {
        return new LinearFunction( m, b + i );
    }
}