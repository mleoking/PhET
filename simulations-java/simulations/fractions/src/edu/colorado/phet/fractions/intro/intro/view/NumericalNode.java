// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import edu.colorado.phet.fractions.intro.intro.model.FractionsIntroModel;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class NumericalNode extends PNode {
    public NumericalNode( final FractionsIntroModel model ) {

        //Show the main fraction control.  Wrap in a zero offset node since its internal layout is not normalized
        final FractionControlNode fractionNode = new FractionControlNode( model.numerator, model.denominator );
        addChild( fractionNode );

        final EqualsSignNode equalsSignNode = new EqualsSignNode();
        addChild( equalsSignNode );
        equalsSignNode.setOffset( fractionNode.getFullBounds().getMaxX() + 50, fractionNode.getOffset().getY() );

        addChild( new ReducedFractionNode( model.reducedFraction, model.reducedFractionRepresentation.enabled ) {{
            setOffset( fractionNode.getFullBounds().getMaxX() + 50 + equalsSignNode.getFullBounds().getWidth() + 50, fractionNode.getOffset().getY() );
        }} );
    }
}