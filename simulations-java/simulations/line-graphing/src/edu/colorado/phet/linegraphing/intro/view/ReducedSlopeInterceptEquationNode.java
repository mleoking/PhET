// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.LGResources;
import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Displays a slope-intercept equation in reduced form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ReducedSlopeInterceptEquationNode extends PText {

    public ReducedSlopeInterceptEquationNode( SlopeInterceptLine line, Color color, PhetFont font ) {
        setText( getReducedEquation( line ) );
        setTextPaint( color );
        setFont( font );
        setPickable( false );
    }

    // Gets the equation in reduced form.
    private static String getReducedEquation( SlopeInterceptLine line ) {
        final String x = Strings.SYMBOL_X;
        final String y = Strings.SYMBOL_Y;
        if ( MathUtil.round( line.run ) == 0 ) {
            return Strings.SLOPE_UNDEFINED;
        }
        else if ( MathUtil.round( line.rise ) == 0 ) {
            return y + " = " + Math.round( line.intercept );
        }
        else if ( MathUtil.round( line.intercept ) == 0 ) {
            return y + " = " + getReducedSlope( line ) + x;
        }
        else if ( line.intercept < 0 ) {
            return y + " = " + getReducedSlope( line ) + x + " - " + MathUtil.round( Math.abs( line.intercept ) );
        }
        else {
            return y + " = " + getReducedSlope( line ) + x + " + " + MathUtil.round( Math.abs( line.intercept ) );
        }
    }

    // Reduces the slope to an integer or reduced fraction
    private static String getReducedSlope( SlopeInterceptLine line ) {
        if ( line.rise == line.run ) {
            return "";
        }
        else if ( line.rise % line.run == 0 ) {
            return String.valueOf( (int) ( line.rise / line.run ) );
        }
        else {
            int gcd = MathUtil.getGreatestCommonDivisor( MathUtil.round( Math.abs( line.rise ) ), MathUtil.round( Math.abs( line.run ) ) );
            if ( line.rise * line.run > 0 ) {
                return MessageFormat.format( "({0}/{1})", MathUtil.round( Math.abs( line.rise / gcd ) ), MathUtil.round( Math.abs( line.run / gcd ) ) );
            }
            else {
                return MessageFormat.format( "-({0}/{1})", MathUtil.round( Math.abs( line.rise / gcd ) ), MathUtil.round( Math.abs( line.run / gcd ) ) );
            }
        }
    }
}
