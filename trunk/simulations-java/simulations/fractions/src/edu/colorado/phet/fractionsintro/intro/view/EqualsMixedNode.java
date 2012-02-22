//// Copyright 2002-2011, University of Colorado
//package edu.colorado.phet.fractionsintro.intro.view;
//
//import edu.colorado.phet.common.phetcommon.model.property.Property;
//import edu.colorado.phet.common.piccolophet.RichPNode;
//import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
//
//import static edu.colorado.phet.fractionsintro.intro.view.FractionEqualityPanel.DISTANCE_FROM_NUMBER_TO_EQUALS_SIGN;
//
///**
// * Shows a node like " = 1/2"
// *
// * @author Sam Reid
// */
//public class EqualsMixedNode extends RichPNode {
//    public EqualsMixedNode( Property<Integer> integer, Property<Integer> mixedNumerator, Property<Integer> mixedDenominator ) {
//        final EqualsSignNode equalsSignNode = new EqualsSignNode();
//        addChild( equalsSignNode );
//
//        addChild( new ZeroOffsetNode( new MixedFractionNode( integer, mixedNumerator, mixedDenominator ) ) {{
//            setOffset( equalsSignNode.getFullBounds().getMaxX() + DISTANCE_FROM_NUMBER_TO_EQUALS_SIGN, equalsSignNode.getCenterY() - getFullHeight() / 2 );
//        }} );
//    }
//}
