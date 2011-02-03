// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.BCEColors;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * CH4 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CH4Node extends PComposite {

    public CH4Node() {

        // attributes
        double diameterBig = 30;
        double diameterSmall = 20;

        // atom nodes
        ShadedSphereNode atomBig = new ShadedSphereNode( diameterBig, BCEColors.C );
        ShadedSphereNode atomSmallTopLeft = new ShadedSphereNode( diameterSmall, BCEColors.H );
        ShadedSphereNode atomSmallTopRight = new ShadedSphereNode( diameterSmall, BCEColors.H );
        ShadedSphereNode atomSmallBottomLeft = new ShadedSphereNode( diameterSmall, BCEColors.H );
        ShadedSphereNode atomSmallBottomRight = new ShadedSphereNode( diameterSmall, BCEColors.H );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atomSmallTopRight );
        parentNode.addChild( atomSmallBottomLeft );
        parentNode.addChild( atomBig );
        parentNode.addChild( atomSmallTopLeft );
        parentNode.addChild( atomSmallBottomRight );

        // layout
        final double offsetSmall = diameterSmall / 4;
        double x = 0;
        double y = 0;
        atomBig.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMinX() + offsetSmall;
        y = atomBig.getFullBoundsReference().getMinY() + offsetSmall;
        atomSmallTopLeft.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMaxX() - offsetSmall;
        y = atomBig.getFullBoundsReference().getMinY() + offsetSmall;
        atomSmallTopRight.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMinX() + offsetSmall;
        y = atomBig.getFullBoundsReference().getMaxY() - offsetSmall;
        atomSmallBottomLeft.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMaxX() - offsetSmall;
        y = atomBig.getFullBoundsReference().getMaxY() - offsetSmall;
        atomSmallBottomRight.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
