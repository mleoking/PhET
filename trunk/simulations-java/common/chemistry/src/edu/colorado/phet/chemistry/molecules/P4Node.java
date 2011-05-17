// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.chemistry.molecules;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

import static edu.colorado.phet.chemistry.model.Atom.P;

/**
 * P4 molecule.
 * Structure is tetrahedral.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class P4Node extends PComposite {

    public P4Node() {

        // atom nodes
        AtomNode topNode = new AtomNode( P );
        AtomNode bottomLeftNode = new AtomNode( P );
        AtomNode bottomRightNode = new AtomNode( P );
        AtomNode bottomBackNode = new AtomNode( P );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( bottomBackNode );
        parentNode.addChild( bottomRightNode );
        parentNode.addChild( bottomLeftNode );
        parentNode.addChild( topNode );

        // layout
        double x = 0;
        double y = 0;
        topNode.setOffset( x, y );

        x = topNode.getFullBoundsReference().getMinX() + ( 0.3 * topNode.getFullBoundsReference().getWidth() );
        y = topNode.getFullBoundsReference().getMaxY() + ( 0.2 * topNode.getFullBoundsReference().getWidth() );
        bottomLeftNode.setOffset( x, y );

        x = topNode.getFullBoundsReference().getMaxX();
        y = topNode.getFullBoundsReference().getMaxY();
        bottomRightNode.setOffset( x, y );

        x = topNode.getFullBoundsReference().getMinX();
        y = topNode.getFullBoundsReference().getCenterY() + ( 0.2 * topNode.getFullBoundsReference().getHeight() );
        bottomBackNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
