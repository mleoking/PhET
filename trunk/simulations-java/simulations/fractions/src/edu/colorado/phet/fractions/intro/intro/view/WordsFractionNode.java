// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fractions.intro.intro.model.CellPointer;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;

/**
 * Shows words like "one half"
 *
 * @author Sam Reid
 */
public class WordsFractionNode extends VisibilityNode {
    public WordsFractionNode( final Property<Integer> numerator, final Property<Integer> denominator, Property<Boolean> wordsRepresentation ) {
        super( wordsRepresentation );
        addChild( new PhetPText( "", DecimalFractionNode.FONT ) {{

            new RichSimpleObserver() {
                @Override public void update() {
                    Fraction reduced = Fraction.reduced( numerator.get(), denominator.get() );
                    setText( lookup( reduced.numerator, reduced.denominator ) );
                }
            }.observe( numerator, denominator );
        }} );
    }

    private String lookup( Integer num, Integer den ) {
        if ( den == 1 ) {
            if ( num == 1 ) { return "one"; }
            if ( num == 2 ) { return "two"; }
            if ( num == 3 ) { return "three"; }
            if ( num == 4 ) { return "four"; }
            if ( num == 5 ) { return "five"; }
            if ( num == 6 ) { return "six"; }
            if ( num == 7 ) { return "seven"; }
            if ( num == 8 ) { return "eight"; }
            if ( num == 9 ) { return "nine"; }
            if ( num == 10 ) { return "ten"; }
            if ( num == 11 ) { return "eleven"; }
            if ( num == 12 ) { return "twelve"; }
        }
        if ( den == 2 ) {
            if ( num == 1 ) { return "one half"; }
            if ( num == 3 ) { return "three halves"; }
            if ( num == 5 ) { return "five halves"; }
            if ( num == 7 ) { return "seven halves"; }
            if ( num == 9 ) { return "nine halves"; }
            if ( num == 11 ) { return "eleven halves"; }
        }
        if ( den == 3 ) {
            if ( num == 1 ) { return "one third"; }
            if ( num == 2 ) { return "two thirds"; }
            if ( num == 4 ) { return "four thirds"; }
            if ( num == 5 ) { return "five thirds"; }
            if ( num == 7 ) { return "two and a third"; }
            if ( num == 8 ) { return "two and two thirds"; }
            if ( num == 10 ) { return "three and a third"; }
            if ( num == 11 ) { return "three and two thirds"; }
        }
        if ( den == 4 ) {
            if ( num == 1 ) { return "one fourth"; }
            if ( num == 3 ) { return "three fourths"; }
            if ( num == 5 ) { return "five fourths"; }
            if ( num == 7 ) { return "seven fourths"; }
            if ( num == 9 ) { return "nine fourths"; }
            if ( num == 11 ) { return "eleven fourths"; }
        }
        if ( den == 5 ) {
            if ( num == 1 ) { return "one fifth"; }
            if ( num == 2 ) { return "two fifths"; }
            if ( num == 3 ) { return "three fifths"; }
            if ( num == 4 ) { return "four fifths"; }
            if ( num == 6 ) { return "six fifths"; }
            if ( num == 7 ) { return "seven fifths"; }
            if ( num == 8 ) { return "eight fifths"; }
            if ( num == 9 ) { return "nine fifths"; }
            if ( num == 11 ) { return "eleven fifths"; }
            if ( num == 12 ) { return "twelve fifths"; }
        }
        if ( den == 1 ) {
            if ( num == 1 ) { return "one"; }
            if ( num == 2 ) { return "two"; }
            if ( num == 3 ) { return "three"; }
            if ( num == 4 ) { return "four"; }
            if ( num == 5 ) { return "five"; }
            if ( num == 6 ) { return "six"; }
            if ( num == 7 ) { return "seven"; }
            if ( num == 8 ) { return "eight"; }
            if ( num == 9 ) { return "nine"; }
            if ( num == 10 ) { return "ten"; }
            if ( num == 11 ) { return "eleven"; }
            if ( num == 12 ) { return "twelve"; }
        }
        return "?";
    }

    @Override public CellPointer getClosestOpenCell( Shape globalShape, Point2D center2D ) {
        return null;
    }
}
