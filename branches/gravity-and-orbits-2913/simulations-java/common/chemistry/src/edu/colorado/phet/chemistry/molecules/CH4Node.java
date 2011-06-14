// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.chemistry.molecules;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

import static edu.colorado.phet.chemistry.model.Element.C;
import static edu.colorado.phet.chemistry.model.Element.H;

/**
 * CH4 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CH4Node extends PComposite {

    public CH4Node() {

        // atom nodes
        AtomNode bigNode = new AtomNode( C );
        AtomNode smallTopLeftNode = new AtomNode( H );
        AtomNode smallTopRightNode = new AtomNode( H );
        AtomNode smallBottomLeftNode = new AtomNode( H );
        AtomNode smallBottomRightNode = new AtomNode( H );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( smallTopRightNode );
        parentNode.addChild( smallBottomLeftNode );
        parentNode.addChild( bigNode );
        parentNode.addChild( smallTopLeftNode );
        parentNode.addChild( smallBottomRightNode );

        // layout
        final double offsetSmall = smallTopLeftNode.getFullBoundsReference().getWidth() / 4;
        double x = 0;
        double y = 0;
        bigNode.setOffset( x, y );
        x = bigNode.getFullBoundsReference().getMinX() + offsetSmall;
        y = bigNode.getFullBoundsReference().getMinY() + offsetSmall;
        smallTopLeftNode.setOffset( x, y );
        x = bigNode.getFullBoundsReference().getMaxX() - offsetSmall;
        y = bigNode.getFullBoundsReference().getMinY() + offsetSmall;
        smallTopRightNode.setOffset( x, y );
        x = bigNode.getFullBoundsReference().getMinX() + offsetSmall;
        y = bigNode.getFullBoundsReference().getMaxY() - offsetSmall;
        smallBottomLeftNode.setOffset( x, y );
        x = bigNode.getFullBoundsReference().getMaxX() - offsetSmall;
        y = bigNode.getFullBoundsReference().getMaxY() - offsetSmall;
        smallBottomRightNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
