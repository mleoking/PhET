// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.P;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.BigAtomNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * P4 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class P4Node extends PComposite {

    public P4Node() {

        // atom nodes
        AtomNode topLeftNode = new BigAtomNode( new P() );
        AtomNode topRightNode = new BigAtomNode( new P() );
        AtomNode bottomLeftNode = new BigAtomNode( new P() );
        AtomNode bottomRightNode = new BigAtomNode( new P() );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( bottomRightNode );
        parentNode.addChild( topRightNode );
        parentNode.addChild( bottomLeftNode );
        parentNode.addChild( topLeftNode );

        // layout
        double x = 0;
        double y = 0;
        topLeftNode.setOffset( x, y );
        x = topLeftNode.getFullBoundsReference().getMaxX();
        y = topLeftNode.getYOffset();
        topRightNode.setOffset( x, y );
        x = topLeftNode.getFullBoundsReference().getMaxX();
        y = topLeftNode.getFullBoundsReference().getMaxY();
        bottomRightNode.setOffset( x, y );
        x = topLeftNode.getXOffset();
        y = bottomRightNode.getYOffset();
        bottomLeftNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
