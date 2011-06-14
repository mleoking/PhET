// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.chemistry.molecules;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

import static edu.colorado.phet.chemistry.model.Element.Cl;
import static edu.colorado.phet.chemistry.model.Element.P;

/**
 * PCl5 molecule.
 * Structure has 2 H's on the vertical axis, and 3 H's arranged in a triangle in the horizontal plane.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PCl5Node extends PComposite {

    public PCl5Node() {

        // atom nodes
        AtomNode centerNode = new AtomNode( P );
        AtomNode topNode = new AtomNode( Cl );
        AtomNode bottomNode = new AtomNode( Cl );
        AtomNode rightNode = new AtomNode( Cl );
        AtomNode topLeftNode = new AtomNode( Cl );
        AtomNode bottomLeftNode = new AtomNode( Cl );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( rightNode );
        parentNode.addChild( bottomNode );
        parentNode.addChild( topLeftNode );
        parentNode.addChild( centerNode );
        parentNode.addChild( topNode );
        parentNode.addChild( bottomLeftNode );

        // layout
        double x = 0;
        double y = 0;
        centerNode.setOffset( x, y );
        x = centerNode.getFullBoundsReference().getCenterX();
        y = centerNode.getFullBoundsReference().getMinY();
        topNode.setOffset( x, y );
        x = centerNode.getFullBoundsReference().getCenterX();
        y = centerNode.getFullBoundsReference().getMaxY();
        bottomNode.setOffset( x, y );
        x = centerNode.getFullBoundsReference().getMaxX();
        y = centerNode.getFullBoundsReference().getCenterY();
        rightNode.setOffset( x, y );
        x = centerNode.getFullBoundsReference().getMinX() + ( 0.25 * centerNode.getFullBoundsReference().getWidth() );
        y = centerNode.getFullBoundsReference().getMinY() + ( 0.25 * centerNode.getFullBoundsReference().getHeight() );
        topLeftNode.setOffset( x, y );
        x = centerNode.getFullBoundsReference().getMinX() + ( 0.1 * centerNode.getFullBoundsReference().getWidth() );
        y = centerNode.getFullBoundsReference().getMaxY() - ( 0.1 * centerNode.getFullBoundsReference().getHeight() );
        bottomLeftNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
