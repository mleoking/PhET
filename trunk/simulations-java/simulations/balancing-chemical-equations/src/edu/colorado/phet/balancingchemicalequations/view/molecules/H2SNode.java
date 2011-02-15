// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.H;
import edu.colorado.phet.balancingchemicalequations.model.Atom.S;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.BigAtomNode;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.SmallAtomNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * H2S molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class H2SNode extends PComposite {

    public H2SNode() {

        // atom nodes
        AtomNode smallLeftNode = new SmallAtomNode( new H() );
        AtomNode smallRightNode = new SmallAtomNode( new H() );
        AtomNode bigNode = new BigAtomNode( new S() );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( bigNode );
        parentNode.addChild( smallLeftNode );
        parentNode.addChild( smallRightNode );

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

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
