// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.chemistry.molecules;

import edu.colorado.phet.chemistry.model.Atom.C;
import edu.colorado.phet.chemistry.model.Atom.Cl;
import edu.colorado.phet.chemistry.model.Atom.H;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * C2H5Cl molecule.
 * Structure is similar to C2H6, but with Cl replacing one of the H's.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class C2H5ClNode extends PComposite {

    public C2H5ClNode() {

        // atom nodes
        AtomNode leftNode = new AtomNode( new C() );
        AtomNode centerNode = new AtomNode( new C() );
        AtomNode smallTopLeftNode = new AtomNode( new H() );
        AtomNode smallBottomLeftNode = new AtomNode( new H() );
        AtomNode smallLeftNode = new AtomNode( new H() );
        AtomNode smallTopRightNode = new AtomNode( new H() );
        AtomNode smallBottomRightNode = new AtomNode( new H() );
        AtomNode rightNode = new AtomNode( new Cl() );

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
        x = centerNode.getFullBoundsReference().getMaxX() + ( 0.125 * rightNode.getFullBoundsReference().getWidth() );
        y = centerNode.getYOffset();
        rightNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
