// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.numbers;

import fj.F;
import fj.data.List;
import lombok.Data;

import java.awt.Color;
import java.util.ArrayList;

import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevelList;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.common.util.Sampling;
import edu.colorado.phet.fractions.fractionmatcher.view.FilledPattern;

import static edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevelList.shuffle;
import static fj.data.List.iterableList;
import static fj.data.List.nil;

/**
 * Target the user tries to create when creating numbers to match a given picture.
 *
 * @author Sam Reid
 */
public @Data class NumberTarget {
    public final MixedFraction mixedFraction;
    public final Color color;
    public final List<FilledPattern> filledPattern;

    public static final F<NumberTarget, MixedFraction> _mixedFraction = new F<NumberTarget, MixedFraction>() {
        @Override public MixedFraction f( final NumberTarget numberTarget ) {
            return numberTarget.mixedFraction;
        }
    };
    private static F<Integer, Boolean> nonZero = new F<Integer, Boolean>() {
        @Override public Boolean f( final Integer integer ) {
            return integer != 0;
        }
    };

    public static NumberTarget target( int whole, Fraction fractionPart, Color color, F<MixedFraction, FilledPattern> pattern ) {
        return target( whole, fractionPart.numerator, fractionPart.denominator, color, pattern );
    }

    public static NumberTarget target( int whole, int numerator, int denominator, Color color, F<MixedFraction, FilledPattern> pattern ) {
        return target( new MixedFraction( whole, numerator, denominator ), color, pattern );
    }

    public static NumberTarget target( MixedFraction mixedFraction, Color color, F<MixedFraction, FilledPattern> pattern ) {
        return new NumberTarget( mixedFraction, color, composite( pattern ).f( mixedFraction ) );
    }

    public static NumberTarget scatteredTarget( MixedFraction mixedFraction, Color color, F<MixedFraction, FilledPattern> pattern ) {
        return new NumberTarget( mixedFraction, color, scatteredComposite( pattern ).f( mixedFraction ) );
    }

    //same as target above but the shapes may appear in a different order (though it does not randomize across filled/unfilled)
    public static NumberTarget shuffledTarget( MixedFraction mixedFraction, Color color, F<MixedFraction, FilledPattern> pattern ) {
        return new NumberTarget( mixedFraction, color, shuffle( composite( pattern ).f( mixedFraction ) ) );
    }

    //Convenience for single pattern
    public static NumberTarget target( int numerator, int denominator, Color color, F<MixedFraction, FilledPattern> pattern ) {
        return target( 0, numerator, denominator, color, pattern );
    }

    public static NumberTarget target( Fraction fraction, Color color, F<MixedFraction, FilledPattern> pattern ) {
        return target( fraction.numerator, fraction.denominator, color, pattern );
    }

    //Create a list of filled patterns from a single pattern type
    private static F<MixedFraction, List<FilledPattern>> composite( final F<MixedFraction, FilledPattern> element ) {
        return new F<MixedFraction, List<FilledPattern>>() {
            @Override public List<FilledPattern> f( final MixedFraction f ) {
                MixedFraction fraction = new MixedFraction( 0, f.toFraction().numerator, f.toFraction().denominator );
                List<FilledPattern> result = nil();
                int numerator = fraction.numerator;
                int denominator = fraction.denominator;
                while ( numerator > denominator ) {
//                    System.out.println( "making filled pie with denominator = " + denominator );
                    final FilledPattern elm = element.f( new MixedFraction( 0, denominator, denominator ) );
                    assert elm != null;
                    result = result.snoc( elm );
                    numerator = numerator - denominator;
                }
                if ( numerator > 0 ) {
                    final FilledPattern elm = element.f( new MixedFraction( 0, numerator, denominator ) );
                    assert elm != null;
                    result = result.snoc( elm );
                }
                assert result != null;
                return result;
            }
        };
    }

    //Start with solutions as above, and subdivide them, then rearrange them.
    private static F<MixedFraction, List<FilledPattern>> scatteredComposite( final F<MixedFraction, FilledPattern> element ) {
        return new F<MixedFraction, List<FilledPattern>>() {
            @Override public List<FilledPattern> f( final MixedFraction f ) {
                Fraction fraction = f.toFraction();
                List<List<Integer>> coefficientSets = iterableList( ShapeLevelList.getCoefficientSets( fraction ) ).filter( new F<List<Integer>, Boolean>() {
                    @Override public Boolean f( final List<Integer> coefficientSet ) {

                        //Only consider solutions that have 4 or less shapes
                        return toFilledPatterns( coefficientSet, element ).length() <= 4;
                    }
                } );

                //If it couldn't solve, just use the normal composite rule
                if ( coefficientSets.length() == 0 ) {
                    System.out.println( "Couldn't find any solutions for f=" + f );
                    return composite( element ).f( f );
                }
                List<Integer> selectedCoefficientSet = Sampling.chooseOne( coefficientSets );

                return toFilledPatterns( selectedCoefficientSet, element );
            }
        };
    }

    private static List<FilledPattern> toFilledPatterns( final List<Integer> coefficientSet, final F<MixedFraction, FilledPattern> element ) {
        ArrayList<FilledPattern> filledPatterns = new ArrayList<FilledPattern>();
        for ( int i = 0; i < coefficientSet.length(); i++ ) {
            int denominator = i + 1;
            int piecesForThisNumerator = coefficientSet.index( i );
            if ( piecesForThisNumerator > 0 ) {
                FilledPattern filledPattern = element.f( new MixedFraction( 0, piecesForThisNumerator, denominator ) );
                filledPatterns.add( filledPattern );
            }
        }
        return iterableList( filledPatterns );
    }
}