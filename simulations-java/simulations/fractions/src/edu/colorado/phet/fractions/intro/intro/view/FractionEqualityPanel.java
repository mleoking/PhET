// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.intro.intro.model.FractionsIntroModel;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class FractionEqualityPanel extends PNode {
    public FractionEqualityPanel( final FractionsIntroModel model ) {

        //Show the main fraction control.  Wrap in a zero offset node since its internal layout is not normalized
        final FractionControlNode fractionNode = new FractionControlNode( model.numerator, model.denominator );
        addChild( fractionNode );

        final EqualsSignNode equalsSignNode = new EqualsSignNode();
        addChild( equalsSignNode );
        equalsSignNode.setOffset( fractionNode.getFullBounds().getMaxX() + 50, fractionNode.getOffset().getY() - equalsSignNode.getFullBounds().getHeight() / 2 );

        addChild( new FractionNode( model.reducedNumerator, model.reducedDenominator ) {{
            setOffset( fractionNode.getFullBounds().getMaxX() + 50 + equalsSignNode.getFullBounds().getWidth() + 50, fractionNode.getOffset().getY() );
            model.visualization.addObserver( new VoidFunction1<Visualization>() {
                public void apply( Visualization visualization ) {
                    setVisible( visualization == Visualization.FRACTION );
                }
            } );
        }} );

        addChild( new MixedFractionNode( model.mixedInteger, model.mixedNumerator, model.mixedDenominator ) {{
            setOffset( fractionNode.getFullBounds().getMaxX() + 100 + equalsSignNode.getFullBounds().getWidth() + 50, fractionNode.getOffset().getY() );
            model.visualization.addObserver( new VoidFunction1<Visualization>() {
                public void apply( Visualization visualization ) {
                    setVisible( visualization == Visualization.MIXED );
                }
            } );
        }} );
    }
}