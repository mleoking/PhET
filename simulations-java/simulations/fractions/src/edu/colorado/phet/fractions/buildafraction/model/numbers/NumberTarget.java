// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.numbers;

import fj.Equal;
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
import static fj.data.List.*;

/**
 * Target the user tries to create when creating numbers to match a given picture.
 *
 * @author Sam Reid
 */
public @Data class NumberTarget {
    public final MixedFraction mixedFraction;
    public final Color color;
    public final List<FilledPattern> filledPattern;
    public final F<MixedFraction, FilledPattern> representation;

    public static final F<NumberTarget, MixedFraction> _mixedFraction = new F<NumberTarget, MixedFraction>() {
        @Override public MixedFraction f( final NumberTarget numberTarget ) {
            return numberTarget.mixedFraction;
        }
    };

    public static NumberTarget target( int whole, int numerator, int denominator, Color color, F<MixedFraction, FilledPattern> pattern ) {
        return target( new MixedFraction( whole, numerator, denominator ), color, pattern );
    }

    public static NumberTarget target( MixedFraction mixedFraction, Color color, F<MixedFraction, FilledPattern> pattern ) {
        return new NumberTarget( mixedFraction, color, composite( pattern ).f( mixedFraction ), pattern );
    }

    public static NumberTarget scatteredTarget( MixedFraction mixedFraction, Color color, F<MixedFraction, FilledPattern> pattern ) {
        return new NumberTarget( mixedFraction, color, scatteredComposite( pattern ).f( mixedFraction ), pattern );
    }

    //same as target above but the shapes may appear in a different order (though it does not randomize across filled/unfilled)
    public static NumberTarget shuffledTarget( MixedFraction mixedFraction, Color color, F<MixedFraction, FilledPattern> pattern ) {
        return new NumberTarget( mixedFraction, color, shuffle( composite( pattern ).f( mixedFraction ) ), pattern );
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
    public static F<MixedFraction, List<FilledPattern>> scatteredComposite( final F<MixedFraction, FilledPattern> element ) {
        return new F<MixedFraction, List<FilledPattern>>() {
            @Override public List<FilledPattern> f( final MixedFraction f ) {
                Fraction fraction = f.toFraction();
                List<List<Integer>> coefficientSets = iterableList( ShapeLevelList.getCoefficientSets( fraction ) ).filter( new F<List<Integer>, Boolean>() {
                    @Override public Boolean f( final List<Integer> coefficientSet ) {

                        //Only consider solutions that have 4 or less shapes
                        return !overflows( coefficientSet ) && toFilledPatterns( coefficientSet, element ).length() <= 4 &&
                               compatibleSizes( coefficientSet );
                    }
                } );

                //If it couldn't solve, just use the normal composite rule
                if ( coefficientSets.length() == 0 ) {
                    System.out.println( "Couldn't find any solutions for f=" + f );//Should be rare; I haven't seen it at all after many runs
                    return composite( element ).f( f );
                }
                List<Integer> selectedCoefficientSet = Sampling.chooseOne( coefficientSets );

                return toFilledPatterns( selectedCoefficientSet, element );
            }
        };
    }

    //the pieces that needed to be added together should be of compatible sizes, for instance 1/3's and 1/6's, not 1/5's and 1/4's
    private static boolean compatibleSizes( final List<Integer> coefficientSet ) {
        /*compatible sets:
        1/1, 1/2, 1/4, 1/8
        1/1, 1/3, 1/6
        */
        ArrayList<Integer> usedDenominators = new ArrayList<Integer>();
        for ( int i = 0; i < coefficientSet.length(); i++ ) {
            int denominator = i + 1;
            int piecesForThisNumerator = coefficientSet.index( i );
            if ( piecesForThisNumerator > 0 ) {
                usedDenominators.add( denominator );
            }
        }
        List<Integer> den = iterableList( usedDenominators );
        return containsOnly( den, list( 1, 2, 4, 8 ) ) ||
               containsOnly( den, list( 1, 3, 6 ) ) ||
               containsOnly( den, list( 1, 2, 6 ) );
    }

    //See if all items from the denominator list are also in the list of required matches
    private static boolean containsOnly( final List<Integer> denominators, final List<Integer> match ) {
        return denominators.filter( new F<Integer, Boolean>() {
            @Override public Boolean f( final Integer integer ) {
                return match.elementIndex( Equal.intEqual, integer ).isSome();
            }
        } ).length() == denominators.length();
    }

    //Make sure no shape is going to request more pieces than it can hold.  This is a modification of toFilledPatterns() which ensures it will be a feasible solution.
    //Note: Another way to handle this would be to pop out the overflowed shape into multiple shapes, but we also want to constrain the maximum number of
    //containers and there is probably another good solution anyways.
    private static boolean overflows( final List<Integer> coefficientSet ) {
        for ( int i = 0; i < coefficientSet.length(); i++ ) {
            int denominator = i + 1;
            int piecesForThisNumerator = coefficientSet.index( i );
            if ( piecesForThisNumerator > 0 ) {
                if ( piecesForThisNumerator > denominator ) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<FilledPattern> toFilledPatterns( final List<Integer> coefficientSet, final F<MixedFraction, FilledPattern> element ) {
        ArrayList<FilledPattern> filledPatterns = new ArrayList<FilledPattern>();
        for ( int i = 0; i < coefficientSet.length(); i++ ) {
            int denominator = i + 1;
            int piecesForThisNumerator = coefficientSet.index( i );
            if ( piecesForThisNumerator > 0 ) {
                FilledPattern filledPattern = element.f( new MixedFraction( 0, piecesForThisNumerator, denominator ) );
                if ( piecesForThisNumerator > denominator ) {
                    throw new RuntimeException( "Not going to work, the representation has more pieces than it can hold.  Will need to be filtered out" );
                }
                filledPatterns.add( filledPattern );
            }
        }
        return iterableList( filledPatterns );
    }

    public Boolean hasMixedDivisions() {
        return filledPattern.map( new F<FilledPattern, Integer>() {
            @Override public Integer f( final FilledPattern filledPattern ) {
                return filledPattern.shapes.length();
            }
        } ).group( Equal.intEqual ).length() != 1;
    }

    public boolean hasTwoOrMoreShapesNotCompletelyFilled() {
        return filledPattern.filter( new F<FilledPattern, Boolean>() {
            @Override public Boolean f( final FilledPattern filledPattern ) {
                return !filledPattern.isCompletelyFilled();
            }
        } ).length() >= 2;
    }
}