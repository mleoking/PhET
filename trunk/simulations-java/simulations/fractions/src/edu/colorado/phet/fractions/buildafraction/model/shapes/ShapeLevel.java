// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.shapes;

import fj.F;
import fj.Ord;
import fj.data.List;
import fj.function.Doubles;

import java.awt.Color;

import edu.colorado.phet.fractions.buildafraction.model.Level;
import edu.colorado.phet.fractions.common.math.Fraction;

import static edu.colorado.phet.fractions.common.math.Fraction._greaterThanOne;
import static edu.colorado.phet.fractions.common.math.Fraction._toDouble;

/**
 * Level for the build a fraction game.
 *
 * @author Sam Reid
 */
public class ShapeLevel extends Level {

    public final List<Fraction> targets;
    public final List<Integer> pieces;
    public final Color color;
    public final ShapeType shapeType;

    //Cannot be a constructor because has same erasure
    public static ShapeLevel shapeLevel( final List<Integer> pieces, final List<Fraction> targets, Color color, ShapeType shapeType ) {
        return new ShapeLevel( pieces, targets, color, shapeType );
    }

    public ShapeLevel( final List<Integer> pieces, final List<Fraction> targets, Color color, ShapeType shapeType ) {
        super( targets.length() );
        this.targets = targets;
        this.pieces = pieces.sort( Ord.intOrd );
        this.color = color;
        this.shapeType = shapeType;

        //make sure it can be solved
        double totalFractionValue = targets.map( _toDouble ).foldLeft( Doubles.add, 0.0 );
        double totalPiecesValue = pieces.map( new F<Integer, Double>() {
            @Override public Double f( final Integer integer ) {
                return 1.0 / integer;
            }
        } ).foldLeft( Doubles.add, 0.0 );
        assert ( totalPiecesValue >= totalFractionValue - 1E-6 );
    }

    public Fraction getTarget( final int i ) { return targets.index( i ); }

    public boolean hasValuesGreaterThanOne() { return targets.exists( _greaterThanOne ); }
}