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
        AtomNode bigNode = new BigAtomNode( new P() );
        AtomNode smallBottomLeftNode = new BigAtomNode( new Cl() );
        AtomNode smallBottomRightNode = new BigAtomNode( new Cl() );
        AtomNode smallBottomCenterNode = new BigAtomNode( new Cl() );
        AtomNode smallTopLeftNode = new BigAtomNode( new Cl() );
        AtomNode smallTopRightNode = new BigAtomNode( new Cl() );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( smallBottomLeftNode );
        parentNode.addChild( smallBottomRightNode );
        parentNode.addChild( smallTopRightNode );
        parentNode.addChild( bigNode );
        parentNode.addChild( smallBottomCenterNode );
        parentNode.addChild( smallTopLeftNode );

        // layout
        double x = 0;
        double y = 0;
        bigNode.setOffset( x, y );
        x = bigNode.getFullBoundsReference().getMinX();
        y = bigNode.getFullBoundsReference().getMaxY() - ( 0.25 * bigNode.getFullBoundsReference().getHeight() );
        smallBottomLeftNode.setOffset( x, y );
        x = bigNode.getFullBoundsReference().getMaxX();
        y = smallBottomLeftNode.getYOffset();
        smallBottomRightNode.setOffset( x, y );
        x = bigNode.getXOffset();
        y = bigNode.getFullBoundsReference().getMaxY();
        smallBottomCenterNode.setOffset( x, y );
        x = bigNode.getFullBoundsReference().getMinX();
        y = bigNode.getFullBoundsReference().getCenterY() - ( 0.25 * bigNode.getFullBoundsReference().getHeight() );
        smallTopLeftNode.setOffset( x, y );
        x = bigNode.getFullBoundsReference().getMaxX() - ( 0.25 * bigNode.getFullBoundsReference().getWidth() );
        y = bigNode.getFullBoundsReference().getMinY();
        smallTopRightNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
