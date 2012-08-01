// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.shapes;

import fj.F;
import fj.Ord;
import fj.data.List;

import java.awt.Color;

import edu.colorado.phet.fractions.buildafraction.model.Level;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;

/**
 * Level for the build a fraction game.
 *
 * @author Sam Reid
 */
public class ShapeLevel extends Level {

    public final List<ShapeTarget> targets;
    public final List<Integer> pieces;

    public final Color color;

    public final ShapeType shapeType;

    //True if the scoring target cell should blink when the user creates a match.  Disabled on higher levels to make it more difficult.
    public final boolean flashTargetCellOnMatch = false;

    //Cannot be a constructor because has same erasure
    public static ShapeLevel shapeLevel( final List<Integer> pieces, final List<Fraction> targets, Color color, ShapeType shapeType ) {
        return new ShapeLevel( pieces, targets.map( new F<Fraction, ShapeTarget>() {
            @Override public ShapeTarget f( final Fraction fraction ) {
                return new ShapeTarget( fraction );
            }
        } ), color, shapeType );
    }

    public ShapeLevel( final List<Integer> pieces, final List<ShapeTarget> targets, Color color, ShapeType shapeType ) {
        super( targets.length() );
        this.targets = targets;
        this.pieces = pieces.sort( Ord.intOrd );
        this.color = color;
        this.shapeType = shapeType;
    }

    public ShapeTarget getTarget( final int i ) { return targets.index( i ); }

    public boolean hasValuesGreaterThanOne() {
        return targets.exists( new F<ShapeTarget, Boolean>() {
            @Override public Boolean f( final ShapeTarget shapeTarget ) {
                return shapeTarget.fraction.numerator > shapeTarget.fraction.denominator;
            }
        } );
    }
}