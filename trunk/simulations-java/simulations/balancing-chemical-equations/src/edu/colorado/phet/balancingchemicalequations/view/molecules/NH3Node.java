// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.BCEColors;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * NH3 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NH3Node extends PComposite {

    public NH3Node() {

        // attributes
        double diameterBig = 30;
        double diameterSmall = 20;

        // atom nodes
        ShadedSphereNode atomBig = new ShadedSphereNode( diameterBig, BCEColors.N );
        ShadedSphereNode atomSmallLeft = new ShadedSphereNode( diameterSmall, BCEColors.H );
        ShadedSphereNode atomSmallRight = new ShadedSphereNode( diameterSmall, BCEColors.H );
        ShadedSphereNode atomSmallBottom = new ShadedSphereNode( diameterSmall, BCEColors.H );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atomSmallLeft );
        parentNode.addChild( atomSmallRight );
        parentNode.addChild( atomBig );
        parentNode.addChild( atomSmallBottom );

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
        x = atomBig.getXOffset();
        y = atomBig.getFullBoundsReference().getMaxY();
        atomSmallBottom.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
