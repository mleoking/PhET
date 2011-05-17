// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.chemistry.molecules;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

import static edu.colorado.phet.chemistry.model.Atom.C;
import static edu.colorado.phet.chemistry.model.Atom.H;

/**
 * C2H2 molecule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class C2H2Node extends PComposite {

    public C2H2Node() {

        AtomNode bigLeftNode = new AtomNode( C );
        AtomNode bigRightNode = new AtomNode( C );
        AtomNode smallLeftNode = new AtomNode( H );
        AtomNode smallRightNode = new AtomNode( H );

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
