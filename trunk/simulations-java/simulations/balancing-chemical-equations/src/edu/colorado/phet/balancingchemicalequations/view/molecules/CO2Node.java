// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.C;
import edu.colorado.phet.balancingchemicalequations.model.Atom.O;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.BigAtomNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * CO2 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CO2Node extends PComposite {

    public CO2Node() {

        // atom nodes
        AtomNode atomCenter = new BigAtomNode( new C() );
        AtomNode atomLeft = new BigAtomNode( new O() );
        AtomNode atomRight = new BigAtomNode( new O() );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atomLeft );
        parentNode.addChild( atomCenter );
        parentNode.addChild( atomRight );

        // layout
        double x = 0;
        double y = 0;
        atomCenter.setOffset( x, y );
        x = atomCenter.getFullBoundsReference().getMinX();
        atomLeft.setOffset( x, y );
        x = atomCenter.getFullBoundsReference().getMaxX();
        atomRight.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
