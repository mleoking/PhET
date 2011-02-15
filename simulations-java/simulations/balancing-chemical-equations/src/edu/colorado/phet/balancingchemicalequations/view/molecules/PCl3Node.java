// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.Cl;
import edu.colorado.phet.balancingchemicalequations.model.Atom.P;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.BigAtomNode;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.SmallAtomNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * PCl3 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PCl3Node extends PComposite {

    public PCl3Node() {

        // atom nodes
        AtomNode bigNode = new BigAtomNode( new P() );
        AtomNode smallLeftNode = new SmallAtomNode( new Cl() );
        AtomNode smallRightNode = new SmallAtomNode( new Cl() );
        AtomNode smallBottomNode = new SmallAtomNode( new Cl() );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( smallLeftNode );
        parentNode.addChild( smallRightNode );
        parentNode.addChild( bigNode );
        parentNode.addChild( smallBottomNode );

        // layout
        double x = 0;
        double y = 0;
        bigNode.setOffset( x, y );
        x = bigNode.getFullBoundsReference().getMinX();
        y = bigNode.getFullBoundsReference().getMaxY() - ( 0.25 * bigNode.getFullBoundsReference().getHeight() );
        smallLeftNode.setOffset( x, y );
        x = bigNode.getFullBoundsReference().getMaxX();
        y = smallLeftNode.getYOffset();
        smallRightNode.setOffset( x, y );
        x = bigNode.getXOffset();
        y = bigNode.getFullBoundsReference().getMaxY();
        smallBottomNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
