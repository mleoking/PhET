// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.numbers;

import fj.F;
import fj.data.List;
import lombok.Data;

import java.awt.Color;

import edu.colorado.phet.fractions.fractionmatcher.view.FilledPattern;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;

import static fj.data.List.nil;

/**
 * Target the user tries to create when creating numbers to match a given picture.
 *
 * @author Sam Reid
 */
public @Data class NumberTarget {
    public final Fraction fraction;
    public final Color color;
    public final List<FilledPattern> filledPattern;

    //Convenience for single pattern
    public static NumberTarget target( int numerator, int denominator, Color color, F<Fraction, FilledPattern> pattern ) {
        return new NumberTarget( new Fraction( numerator, denominator ), color, composite( pattern ).f( new Fraction( numerator, denominator ) ) );
    }

    public static NumberTarget target( Fraction fraction, Color color, F<Fraction, FilledPattern> pattern ) {
        return target( fraction.numerator, fraction.denominator, color, pattern );
    }

    //Create a list of filled patterns from a single pattern type
    public static F<Fraction, List<FilledPattern>> composite( final F<Fraction, FilledPattern> element ) {
        return new F<Fraction, List<FilledPattern>>() {
            @Override public List<FilledPattern> f( final Fraction fraction ) {
                List<FilledPattern> result = nil();
                int numerator = fraction.numerator;
                int denominator = fraction.denominator;
                while ( numerator > denominator ) {
                    result = result.snoc( element.f( new Fraction( denominator, denominator ) ) );
                    numerator = numerator - denominator;
                }
                if ( numerator > 0 ) {
                    result = result.snoc( element.f( new Fraction( numerator, denominator ) ) );
                }
                return result;
            }
        };
    }
}