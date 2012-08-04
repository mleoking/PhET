// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.shapes;

import fj.Ord;
import fj.data.List;

import java.awt.Color;

import edu.colorado.phet.fractions.buildafraction.model.Level;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;

import static edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction._greaterThanOne;

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
    }

    public Fraction getTarget( final int i ) { return targets.index( i ); }

    public boolean hasValuesGreaterThanOne() { return targets.exists( _greaterThanOne ); }
}