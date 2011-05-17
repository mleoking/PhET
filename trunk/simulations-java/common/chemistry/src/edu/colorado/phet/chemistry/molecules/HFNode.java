// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.chemistry.molecules;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

import static edu.colorado.phet.chemistry.model.Atom.F;
import static edu.colorado.phet.chemistry.model.Atom.H;

/**
 * HF molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HFNode extends PComposite {

    public HFNode() {

        // atom nodes
        AtomNode leftNode = new AtomNode( H );
        AtomNode rightNode = new AtomNode( F );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( rightNode );
        parentNode.addChild( leftNode );

        // layout
        double x = 0;
        double y = 0;
        leftNode.setOffset( x, y );
        x = leftNode.getXOffset() + ( 0.5 * rightNode.getFullBoundsReference().getWidth() );
        y = leftNode.getYOffset();
        rightNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
