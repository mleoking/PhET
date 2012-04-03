// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.model;

import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.linegraphing.LGResources.Strings;

/**
 * Model of an immutable line, using slope-intercept form, y=mx+b.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptLine {

    public final double rise;
    public final double run;
    public final double intercept;

    public SlopeInterceptLine( double rise, double run, double intercept ) {
        this.rise = rise;
        this.run = run;
        this.intercept = intercept;
    }

    // Line is undefined if it's slope (rise/run) is undefined.
    public boolean isDefined() {
        return ( run != 0 );
    }

    // Gets the slope, m=rise/run.
    private double getSlope() {
        assert( run != 0 );
        return ( (double) rise ) / run;
    }

    // y = mx + b, returns 0 if there is no unique solution
    public double solveY( double x ) {
        return solveY( getSlope(), x, intercept );
    }

    // y = mx + b
    private static double solveY( double m, double x, double b ) {
        return ( m * x ) + b;
    }

    // x = (y-b)/m
    public double solveX( double y ) {
        if ( run == 0 ) {
            return 0;
        }
        else {
            return solveX( y, intercept, getSlope() );
        }
    }

    // x = (y-b)/m
    private static double solveX( double y, double b, double m ) {
        assert( m != 0 );
        return ( y - b ) / m;
    }

    // Gets the equation as it was entered by the user.
    public String getEquation() {
        final String x = Strings.SYMBOL_HORIZONTAL_AXIS;
        final String y = Strings.SYMBOL_VERTICAL_AXIS;
        if ( run == 0 ) {
            return x + " = 0";
        }
        else if ( rise == 0 ) {
            return y + " = " + (int) intercept;
        }
        else if ( intercept == 0 ) {
            return y + " = (" + (int) rise + "/" + (int) run + ")" + x;
        }
        else if ( intercept < 0 ) {
            return y + " = (" + (int) rise + "/" + (int) run + ")" + x + " - " + (int) Math.abs( intercept );
        }
        else {
            return y + " = (" + (int) rise + "/" + (int) run + ")" + x + " + " + (int) Math.abs( intercept );
        }
    }

    // Gets the equation with slope in reduced form.
    public String getReducedEquation() {
        final String x = Strings.SYMBOL_HORIZONTAL_AXIS;
        final String y = Strings.SYMBOL_VERTICAL_AXIS;
        if ( run == 0 ) {
            return x + " = 0";
        }
        else if ( rise == 0 ) {
            return y + " = " + (int) intercept;
        }
        else if ( intercept == 0 ) {
            return y + " = " + getReducedSlope() + x;
        }
        else if ( intercept < 0 ) {
            return y + " = " + getReducedSlope() + x + " - " + (int) Math.abs( intercept );
        }
        else {
            return y + " = " + getReducedSlope() + x + " + " + (int) Math.abs( intercept );
        }
    }

    // Gets the slope as an integer or reduced fraction.
    private String getReducedSlope() {
        if ( rise == run ) {
            return "";
        }
        else if ( rise % run == 0 ) {
            return String.valueOf( (int) ( rise / run ) );
        }
        else {
            int gcd = MathUtil.getGreatestCommonDivisor( (int) Math.abs( rise ), (int) Math.abs( run ) );
            if ( rise * run > 0 ) {
                return MessageFormat.format( "({0}/{1})", (int) Math.abs( rise / gcd ), (int) Math.abs( run / gcd ) );
            }
            else {
                return MessageFormat.format( "-({0}/{1})", (int) Math.abs( rise / gcd ), (int) Math.abs( run / gcd ) );
            }
        }
    }
}
