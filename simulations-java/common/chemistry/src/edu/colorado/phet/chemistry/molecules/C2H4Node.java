// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.chemistry.molecules;

import edu.colorado.phet.chemistry.model.Atom.C;
import edu.colorado.phet.chemistry.model.Atom.H;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * C2H4 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class C2H4Node extends PComposite {

    public C2H4Node() {

        AtomNode bigLeftNode = new AtomNode( new C() );
        AtomNode bigRightNode = new AtomNode( new C() );
        AtomNode smallTopLeftNode = new AtomNode( new H() );
        AtomNode smallTopRightNode = new AtomNode( new H() );
        AtomNode smallBottomLeftNode = new AtomNode( new H() );
        AtomNode smallBottomRightNode = new AtomNode( new H() );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( smallTopRightNode );
        parentNode.addChild( smallTopLeftNode );
        parentNode.addChild( bigLeftNode );
        parentNode.addChild( bigRightNode );
        parentNode.addChild( smallBottomLeftNode );
        parentNode.addChild( smallBottomRightNode );

        // layout
        final double offsetSmall = smallTopLeftNode.getFullBoundsReference().getWidth() / 4;
        double x = 0;
        double y = 0;
        bigLeftNode.setOffset( x, y );
        x = bigLeftNode.getFullBoundsReference().getMaxX() + ( 0.25 * bigRightNode.getFullBoundsReference().getWidth() );
        y = bigLeftNode.getYOffset();
        bigRightNode.setOffset( x, y );
        x = bigLeftNode.getFullBoundsReference().getMinX() + offsetSmall;
        y = bigLeftNode.getFullBoundsReference().getMinY() + offsetSmall;
        smallTopLeftNode.setOffset( x, y );
        x = bigRightNode.getFullBoundsReference().getMaxX() - offsetSmall;
        y = bigRightNode.getFullBoundsReference().getMinY() + offsetSmall;
        smallTopRightNode.setOffset( x, y );
        x = bigLeftNode.getFullBoundsReference().getMinX() + offsetSmall;
        y = bigLeftNode.getFullBoundsReference().getMaxY() - offsetSmall;
        smallBottomLeftNode.setOffset( x, y );
        x = bigRightNode.getFullBoundsReference().getMaxX() - offsetSmall;
        y = bigRightNode.getFullBoundsReference().getMaxY() - offsetSmall;
        smallBottomRightNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
