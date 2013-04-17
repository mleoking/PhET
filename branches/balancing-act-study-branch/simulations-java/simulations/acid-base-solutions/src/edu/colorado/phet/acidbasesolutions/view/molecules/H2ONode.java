// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.view.molecules;

import java.awt.Color;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * H2O molecule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class H2ONode extends PComposite {

    public H2ONode() {

        // attributes
        double diameterBig = 24;
        double diameterSmall = 14;
        Color color = ABSColors.H2O;

        // atom nodes
        ShadedSphereNode atomBig = new ShadedSphereNode( diameterBig, color );
        ShadedSphereNode atomSmallTop = new ShadedSphereNode( diameterSmall, color );
        ShadedSphereNode atomSmallBottom = new ShadedSphereNode( diameterSmall, color );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atomSmallTop );
        parentNode.addChild( atomBig );
        parentNode.addChild( atomSmallBottom );

        // layout
        double x = 0;
        double y = 0;
        atomBig.setOffset( x, y );
        x = atomBig.getXOffset();
        y = atomBig.getFullBoundsReference().getMinY() - ( 0.25 * atomSmallTop.getFullBoundsReference().getHeight() );
        atomSmallTop.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMinX();
        y = atomBig.getFullBoundsReference().getMaxY() - ( 0.25 * atomBig.getFullBoundsReference().getHeight() );
        atomSmallBottom.setOffset( x, y );
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
