// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.piccolophet.RichPNode;

/**
 * Node that shows a fraction (numerator and denominator and dividing line) along with controls to change the values.
 * Layout is not normalized (top left is not 0,0)
 *
 * @author Sam Reid
 */
public class FractionControlNode extends RichPNode {
    public FractionControlNode( final IntegerProperty numerator, final IntegerProperty denominator ) {
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