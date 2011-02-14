// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.Cl;
import edu.colorado.phet.balancingchemicalequations.model.Atom.H;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.BigAtomNode;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.SmallAtomNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * HCl molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HClNode extends PComposite {

    public HClNode() {

        // atom nodes
        AtomNode atomLeft = new SmallAtomNode( new H() );
        AtomNode atomRight = new BigAtomNode( new Cl() );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atomRight );
        parentNode.addChild( atomLeft );

        // layout
        double x = 0;
        double y = 0;
        atomLeft.setOffset( x, y );
        x = atomLeft.getXOffset() + ( 0.5 * atomRight.getFullBoundsReference().getWidth() );
        y = atomLeft.getYOffset();
        atomRight.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
