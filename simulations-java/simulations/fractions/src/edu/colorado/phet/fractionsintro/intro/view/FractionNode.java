// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that shows a fraction (numerator and denominator and dividing line) along with controls to change the values.
 * Layout is not normalized (top left is not 0,0)
 *
 * @author Sam Reid
 */
public class FractionNode extends PNode {
    public FractionNode( final Property<Integer> numerator, final Property<Integer> denominator ) {
        final RoundedDivisorLine line = new RoundedDivisorLine();
        addChild( line );
        final ZeroOffsetNode num = new ZeroOffsetNode( new FractionNumberNode( numerator ) ) {{
            setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, line.getFullBounds().getY() - getFullBounds().getHeight() );
        }};
        addChild( num );
        final ZeroOffsetNode den = new ZeroOffsetNode( new FractionNumberNode( denominator ) ) {{
            setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, line.getFullBounds().getY() );
        }};
        addChild( den );

//        final ZeroOffsetNode numberNode = new ZeroOffsetNode( new FractionNumberNode( numerator ) ) {{
//            setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, line.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
//        }};
//        addChild( numberNode );

//        denominator.addObserver( new VoidFunction1<Integer>() {
//            public void apply( Integer integer ) {
//                line.setVisible( integer != 1 );
//                den.setVisible( integer != 1 );
//                num.setVisible( integer != 1 );
//
//                numberNode.setVisible( integer == 1 );
//            }
//        } );
    }
}