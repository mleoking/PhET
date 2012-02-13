// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.fractionsintro.intro.model.IntClientProperty;

/**
 * Node that shows a fraction (numerator and denominator and dividing line) along with controls to change the values.
 * Layout is not normalized (top left is not 0,0)
 *
 * @author Sam Reid
 */
public class FractionControlNode extends RichPNode {
    public FractionControlNode( final IntClientProperty numerator, final IntClientProperty denominator ) {
        final RoundedDivisorLine line = new RoundedDivisorLine();
        addChild( line );
        addChild( new NumeratorWithSpinner( numerator, denominator ) {{
            setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, line.getFullBounds().getY() - getFullBounds().getHeight() );
        }} );
        addChild( new DenominatorWithSpinner( numerator, denominator ) {{
            setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, line.getFullBounds().getY() );
        }} );
    }
}