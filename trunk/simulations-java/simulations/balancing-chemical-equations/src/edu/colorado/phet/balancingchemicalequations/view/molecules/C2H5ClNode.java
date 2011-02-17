// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.C;
import edu.colorado.phet.balancingchemicalequations.model.Atom.Cl;
import edu.colorado.phet.balancingchemicalequations.model.Atom.H;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.BigAtomNode;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.SmallAtomNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * C2H5Cl molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class C2H5ClNode extends PComposite {

    public C2H5ClNode() {

        // atom nodes
        AtomNode leftNode = new BigAtomNode( new C() );
        AtomNode centerNode = new BigAtomNode( new C() );
        AtomNode smallTopLeftNode = new SmallAtomNode( new H() );
        AtomNode smallBottomLeftNode = new SmallAtomNode( new H() );
        AtomNode smallLeftNode = new SmallAtomNode( new H() );
        AtomNode smallTopRightNode = new SmallAtomNode( new H() );
        AtomNode smallBottomRightNode = new SmallAtomNode( new H() );
        AtomNode rightNode = new BigAtomNode( new Cl() );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( smallBottomRightNode );
        parentNode.addChild( smallTopRightNode );
        parentNode.addChild( centerNode );
        parentNode.addChild( rightNode );
        parentNode.addChild( smallLeftNode );
        parentNode.addChild( leftNode );
        parentNode.addChild( smallBottomLeftNode );
        parentNode.addChild( smallTopLeftNode );

        // layout
        double x = 0;
        double y = 0;
        leftNode.setOffset( x, y );
        x = leftNode.getFullBoundsReference().getMaxX() + ( 0.25 * centerNode.getFullBoundsReference().getWidth() );
        y = leftNode.getYOffset();
        centerNode.setOffset( x, y );
        x = leftNode.getXOffset();
        y = leftNode.getFullBoundsReference().getMinY();
        smallTopLeftNode.setOffset( x, y );
        x = smallTopLeftNode.getXOffset();
        y = leftNode.getFullBoundsReference().getMaxY();
        smallBottomLeftNode.setOffset( x, y );
        x = leftNode.getFullBoundsReference().getMinX();
        y = leftNode.getYOffset();
        smallLeftNode.setOffset( x, y );
        x = centerNode.getXOffset();
        y = centerNode.getFullBoundsReference().getMinY();
        smallTopRightNode.setOffset( x, y );
        x = centerNode.getXOffset();
        y = centerNode.getFullBoundsReference().getMaxY();
        smallBottomRightNode.setOffset( x, y );
        x = centerNode.getFullBoundsReference().getMaxX();
        y = centerNode.getYOffset();
        rightNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
