// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemistry.nodes;

import java.awt.Color;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

public class LabeledAtomNode extends PNode {

    private final ShadedSphereNode sphericalNode;

    public LabeledAtomNode( final Element element ) {

        double transformedRadius = element.getRadius();
        sphericalNode = new ShadedSphereNode( 2 * transformedRadius, element.getColor() );
        addChild( sphericalNode );

        // Create, scale, and add the label
        PText labelNode = new PText() {{
            setText( element.getSymbol() );
            setFont( new PhetFont( 10, true ) );
            setScale( sphericalNode.getFullBoundsReference().width * 0.65 / getFullBoundsReference().width );
            if ( 0.30 * element.getColor().getRed() + 0.59 * element.getColor().getGreen() + 0.11 * element.getColor().getBlue() < 125 ) {
                setTextPaint( Color.WHITE );
            }
            setOffset( -getFullBoundsReference().width / 2, -getFullBoundsReference().height / 2 );
        }};
        sphericalNode.addChild( labelNode );

        // Add a cursor handler to signal to the user that this is movable.
//        addInputEventListener( new CursorHandler() );
    }
}
