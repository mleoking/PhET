// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.BCEColors;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * CO2 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CO2Node extends PComposite {

    public CO2Node() {

        // attributes
        double diameter = 30;

        // atom nodes
        ShadedSphereNode atomCenter = new ShadedSphereNode( diameter, BCEColors.C );
        ShadedSphereNode atomLeft = new ShadedSphereNode( diameter, BCEColors.O );
        ShadedSphereNode atomRight = new ShadedSphereNode( diameter, BCEColors.O );

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
