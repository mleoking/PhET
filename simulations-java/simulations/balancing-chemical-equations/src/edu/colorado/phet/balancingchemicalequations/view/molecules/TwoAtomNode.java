// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.BigAtomNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for molecules with 2 atoms of the same size.
 * Origin is at geometric center of bounding rectangle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class TwoAtomNode extends PComposite {

    /**
     * Use this constructor when the 2 atoms are different.
     * @param leftAtom
     * @param rightAtom
     */
    public TwoAtomNode( Atom leftAtom, Atom rightAtom ) {

        // atom nodes
        AtomNode atomLeft = new BigAtomNode( leftAtom );
        AtomNode atomRight = new BigAtomNode( rightAtom );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atomRight );
        parentNode.addChild( atomLeft );

        // layout
        double x = 0;
        double y = 0;
        atomLeft.setOffset( x, y );
        x = atomLeft.getFullBoundsReference().getMaxX() + ( 0.25 * atomRight.getFullBoundsReference().getWidth() );
        y = atomLeft.getYOffset();
        atomRight.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
