// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.chemistry.molecules;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

import static edu.colorado.phet.chemistry.model.Atom.*;

/**
 * CH2O molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CH2ONode extends PComposite {

    public CH2ONode() {

        // atom nodes
        AtomNode leftNode = new AtomNode( C );
        AtomNode smallTopNode = new AtomNode( H );
        AtomNode smallBottomNode = new AtomNode( H );
        AtomNode rightNode = new AtomNode( O );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( smallTopNode );
        parentNode.addChild( leftNode );
        parentNode.addChild( rightNode );
        parentNode.addChild( smallBottomNode );

        // layout
        final double offsetSmall = smallTopNode.getFullBoundsReference().getWidth() / 4;
        double x = 0;
        double y = 0;
        leftNode.setOffset( x, y );
        x = leftNode.getFullBoundsReference().getMaxX() + ( 0.25 * rightNode.getFullBoundsReference().getWidth() );
        y = leftNode.getYOffset();
        rightNode.setOffset( x, y );
        x = leftNode.getFullBoundsReference().getMinX() + offsetSmall;
        y = leftNode.getFullBoundsReference().getMinY() + offsetSmall;
        smallTopNode.setOffset( x, y );
        x = smallTopNode.getXOffset();
        y = leftNode.getFullBoundsReference().getMaxY() - offsetSmall;
        smallBottomNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
