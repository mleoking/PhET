// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import edu.colorado.phet.fractionsintro.intro.model.FractionsIntroModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows the the representation at the bottom of tab 1 like 8/4 = 2
 *
 * @author Sam Reid
 */
public class FractionEqualityPanel extends PNode {

    public static final double DISTANCE_FROM_NUMBER_TO_EQUALS_SIGN = 8;

    public FractionEqualityPanel( final FractionsIntroModel model ) {

        //Show the main fraction control.  Wrap in a zero offset node since its internal layout is not normalized
        final FractionControlNode fractionNode = new FractionControlNode( model.numerator, model.denominator );
        addChild( fractionNode );

//        final ZeroOffsetNode equalsReducedNode = new ZeroOffsetNode( new EqualsReducedNode( model.reducedNumerator, model.reducedDenominator ) ) {{
//            setOffset( fractionNode.getMaxX() + DISTANCE_FROM_NUMBER_TO_EQUALS_SIGN, fractionNode.getOffset().getY() - getFullHeight() / 2 );
//            model.showReduced.addObserver( new VoidFunction1<Boolean>() {
//                public void apply( Boolean visible ) {
//                    setVisible( visible );
//                }
//            } );
//        }};
//        addChild( equalsReducedNode );
//
//        addChild( new ZeroOffsetNode( new EqualsMixedNode( model.mixedInteger, model.mixedNumerator, model.mixedDenominator ) ) {{
//            setOffset( equalsReducedNode.getMaxX() + DISTANCE_FROM_NUMBER_TO_EQUALS_SIGN, fractionNode.getOffset().getY() - getFullHeight() / 2 );
//            model.showMixed.addObserver( new VoidFunction1<Boolean>() {
//                public void apply( Boolean visible ) {
//                    setVisible( visible );
//                }
//            } );
//        }} );
    }
}