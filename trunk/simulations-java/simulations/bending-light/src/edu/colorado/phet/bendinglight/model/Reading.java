// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import java.text.DecimalFormat;

/**
 * @author Sam Reid
 */
public class Reading {
    private double value;

    public static final Reading MISS = new Reading() {
        public String getString() {
            return "-";
        }

        @Override
        public boolean isHit() {
            return false;
        }
    };
    public static final Reading MULTI = new Reading() {
        public String getString() {
            return "???";
        }

        @Override
        public boolean isHit() {
            return true;
        }
    };

    protected Reading() {
    }

    public Reading( double value ) {
        this.value = value;
    }

    public String getString() {
        return format( value );
    }

    public static String format( double value ) {
        return new DecimalFormat( "0.00" ).format( value * 100 ) + "%";
    }

    public boolean isHit() {
        return true;
    }

    public double getValue() {
        return value;
    }
}
