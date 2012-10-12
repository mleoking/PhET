// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.numbers;

import fj.Equal;
import fj.F;
import fj.data.List;

import edu.colorado.phet.fractions.buildafraction.model.Level;
import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.fractionmatcher.view.FilledPattern;
import edu.colorado.phet.fractions.fractionmatcher.view.PatternType;

import static edu.colorado.phet.fractions.buildafraction.model.numbers.NumberTarget._mixedFraction;
import static edu.colorado.phet.fractions.buildafraction.model.numbers.NumberTarget.target;
import static edu.colorado.phet.fractions.common.math.Fraction.*;
import static fj.Ord.intOrd;
import static fj.data.List.list;

/**
 * Level for the build a fraction game in which the user build with numbers.  This applies to both the "Build a Fraction" and "Mixed Numbers" tabs.
 *
 * @author Sam Reid
 */
public class NumberLevel extends Level {

    //The numbers that the user can use, sorted in increasing order
    public final List<Integer> numbers;

    //The targets the user must match.
    public final List<NumberTarget> targets;

    public NumberLevel( final List<Integer> numbers, final List<NumberTarget> targets ) {
        super( targets.map( _mixedFraction ) );
        this.numbers = numbers.sort( intOrd );
        this.targets = targets;
    }

    //Infer the number cards from the list of targets, using exactly what is necessary
    public NumberLevel( final List<NumberTarget> targets ) {
        this( targets.map( _mixedFraction ).map( MixedFraction._numerator ).append(
                targets.map( _mixedFraction ).map( MixedFraction._denominator ).append(
                        targets.map( _mixedFraction ).map( MixedFraction._whole ).filter( new F<Integer, Boolean>() {
                            @Override public Boolean f( final Integer integer ) {
                                return integer > 0;
                            }
                        } ) ) ), targets );
    }

    public static List<Integer> straightforwardNumbers( List<MixedFraction> targets ) {
        return targets.map( MixedFraction._numerator ).append(
                targets.map( MixedFraction._denominator ).append(
                        targets.map( MixedFraction._whole ).filter( new F<Integer, Boolean>() {
                            @Override public Boolean f( final Integer integer ) {
                                return integer > 0;
                            }
                        } ) ) );
    }

    public NumberLevel( final List<MixedFraction> selected, final RandomColors3 colors, final F<MixedFraction, FilledPattern> shape ) {
        this( list( target( selected.index( 0 ), colors.next(), shape ),
                    target( selected.index( 1 ), colors.next(), shape ),
                    target( selected.index( 2 ), colors.next(), shape ) ) );
    }

    //Create a Number Level from the reduced fractions
    public static NumberLevel numberLevelReduced( final List<NumberTarget> targets ) {
        List<Fraction> reduced = targets.map( MixedFraction._fractionPart ).map( _reduce );
        List<Integer> cards = reduced.map( _numerator ).append( reduced.map( _denominator ) );
        return new NumberLevel( cards, targets );
    }

    public boolean hasValuesGreaterThanOne() {
        return targets.exists( new F<NumberTarget, Boolean>() {
            @Override public Boolean f( final NumberTarget numberTarget ) {
                return numberTarget.mixedFraction.toFraction().greaterThanOne();
            }
        } );
    }

    public boolean hasMixedNumbers() {
        return targets.exists( new F<NumberTarget, Boolean>() {
            @Override public Boolean f( final NumberTarget numberTarget ) {
                return numberTarget.mixedFraction.whole > 0;
            }
        } );
    }

    public boolean hasDifferentShapeTypes() {
        return getPatternTypes().group( Equal.<PatternType>anyEqual() ).length() > 1;
    }

    public List<PatternType> getPatternTypes() {
        return targets.map( new F<NumberTarget, PatternType>() {
            @Override public PatternType f( final NumberTarget numberTarget ) {
                return numberTarget.getRepresentation().f( numberTarget.mixedFraction ).type;
            }
        } );
    }

    public List<NumberTarget> getTargetsThatHaveShapesWithDifferentNumerators() {
        return targets.filter( new F<NumberTarget, Boolean>() {
            @Override public Boolean f( final NumberTarget t ) {
                return t.hasMixedDivisions();
            }
        } );
    }
}