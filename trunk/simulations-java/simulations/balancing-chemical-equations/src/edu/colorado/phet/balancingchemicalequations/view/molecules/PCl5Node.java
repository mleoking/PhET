// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.Cl;
import edu.colorado.phet.balancingchemicalequations.model.Atom.P;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.BigAtomNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * PCl5 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PCl5Node extends PComposite {

    public PCl5Node() {

        // atom nodes
        AtomNode centerNode = new BigAtomNode( new P() );
        AtomNode bottomLeftNode = new BigAtomNode( new Cl() );
        AtomNode bottomRightNode = new BigAtomNode( new Cl() );
        AtomNode bottomCenterNode = new BigAtomNode( new Cl() );
        AtomNode topLeftNode = new BigAtomNode( new Cl() );
        AtomNode topRightNode = new BigAtomNode( new Cl() );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( bottomLeftNode );
        parentNode.addChild( bottomRightNode );
        parentNode.addChild( topRightNode );
        parentNode.addChild( centerNode );
        parentNode.addChild( bottomCenterNode );
        parentNode.addChild( topLeftNode );

        // layout
        double x = 0;
        double y = 0;
        centerNode.setOffset( x, y );
        x = centerNode.getFullBoundsReference().getMinX();
        y = centerNode.getFullBoundsReference().getMaxY() - ( 0.25 * centerNode.getFullBoundsReference().getHeight() );
        bottomLeftNode.setOffset( x, y );
        x = centerNode.getFullBoundsReference().getMaxX();
        y = bottomLeftNode.getYOffset();
        bottomRightNode.setOffset( x, y );
        x = centerNode.getXOffset();
        y = centerNode.getFullBoundsReference().getMaxY();
        bottomCenterNode.setOffset( x, y );
        x = centerNode.getFullBoundsReference().getMinX();
        y = centerNode.getFullBoundsReference().getCenterY() - ( 0.25 * centerNode.getFullBoundsReference().getHeight() );
        topLeftNode.setOffset( x, y );
        x = centerNode.getFullBoundsReference().getMaxX() - ( 0.25 * centerNode.getFullBoundsReference().getWidth() );
        y = centerNode.getFullBoundsReference().getMinY();
        topRightNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
