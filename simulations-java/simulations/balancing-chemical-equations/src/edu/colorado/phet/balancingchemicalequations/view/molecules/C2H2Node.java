// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.C;
import edu.colorado.phet.balancingchemicalequations.model.Atom.H;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.BigAtomNode;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.SmallAtomNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;


public class C2H2Node extends PComposite {

    public C2H2Node() {

        AtomNode bigLeftNode = new BigAtomNode( new C() );
        AtomNode bigRightNode = new BigAtomNode( new C() );
        AtomNode smallLeftNode = new SmallAtomNode( new H() );
        AtomNode smallRightNode = new SmallAtomNode( new H() );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( smallLeftNode );
        parentNode.addChild( bigLeftNode );
        parentNode.addChild( bigRightNode );
        parentNode.addChild( smallRightNode );

        // layout
        double x = 0;
        double y = 0;
        bigLeftNode.setOffset( x, y );
        x = bigLeftNode.getFullBoundsReference().getMaxX() + ( 0.25 * bigRightNode.getFullBoundsReference().getWidth() );
        bigRightNode.setOffset( x, y );
        x = bigLeftNode.getFullBoundsReference().getMinX();
        smallLeftNode.setOffset( x, y );
        x = bigRightNode.getFullBoundsReference().getMaxX();
        smallRightNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
