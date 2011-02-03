// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.BCEColors;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * O2 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class O2Node extends PComposite {

    public O2Node() {

        // attributes
        double diameter = 30;

        // atom nodes
        ShadedSphereNode atomLeft = new ShadedSphereNode( diameter, BCEColors.O );
        ShadedSphereNode atomRight = new ShadedSphereNode( diameter, BCEColors.O );

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
