// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.H;
import edu.colorado.phet.balancingchemicalequations.model.Atom.O;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.BigAtomNode;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.SmallAtomNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * H2O molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class H2ONode extends PComposite {

    public H2ONode() {

        // atom nodes
        AtomNode atomSmallLeft = new SmallAtomNode( new H() );
        AtomNode atomSmallRight = new SmallAtomNode( new H() );
        AtomNode atomBig = new BigAtomNode( new O() );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atomBig );
        parentNode.addChild( atomSmallLeft );
        parentNode.addChild( atomSmallRight );

        // layout
        double x = 0;
        double y = 0;
        atomBig.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMinX();
        y = atomBig.getFullBoundsReference().getMaxY() - ( 0.25 * atomBig.getFullBoundsReference().getHeight() );
        atomSmallLeft.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMaxX();
        y = atomSmallLeft.getYOffset();
        atomSmallRight.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
