// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.intro.intro.model.FractionsIntroModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows the the representation at the bottom of tab 1 like 8/4 = 2
 *
 * @author Sam Reid
 */
public class FractionEqualityPanel extends PNode {
    public FractionEqualityPanel( final FractionsIntroModel model ) {

        //Show the main fraction control.  Wrap in a zero offset node since its internal layout is not normalized
        final FractionControlNode fractionNode = new FractionControlNode( model.numerator, model.denominator );
        addChild( fractionNode );

        final EqualsSignNode equalsSignNode = new EqualsSignNode() {{
            setOffset( fractionNode.getMaxX() + 50, fractionNode.getOffset().getY() - getFullHeight() / 2 );
            new RichSimpleObserver() {
                @Override public void update() {
                    setVisible( model.mixedShowing.get() || model.reducedShowing.get() );
                }
            }.observe( model.mixedShowing, model.reducedShowing );
        }};
        addChild( equalsSignNode );

        addChild( new FractionNode( model.reducedNumerator, model.reducedDenominator ) {{
            setOffset( fractionNode.getFullBounds().getMaxX() + 50 + equalsSignNode.getFullBounds().getWidth() + 50, fractionNode.getOffset().getY() );
            model.reducedShowing.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    setVisible( visible );
                }
            } );
        }} );

        addChild( new MixedFractionNode( model.mixedInteger, model.mixedNumerator, model.mixedDenominator ) {{
            setOffset( fractionNode.getFullBounds().getMaxX() + 100 + equalsSignNode.getFullBounds().getWidth() + 50, fractionNode.getOffset().getY() );
            model.mixedShowing.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    setVisible( visible );
                }
            } );
        }} );
    }
}