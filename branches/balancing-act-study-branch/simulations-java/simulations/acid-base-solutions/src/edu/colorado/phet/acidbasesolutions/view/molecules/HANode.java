// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.view.molecules;

import java.awt.Color;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * HA molecule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HANode extends PComposite {

    public HANode() {

        // attributes
        double diameterBig = 24;
        double diameterSmall = 14;
        Color color = ABSColors.HA;

        // atom nodes
        ShadedSphereNode atomBig = new ShadedSphereNode( diameterBig, color );
        ShadedSphereNode atomSmall = new ShadedSphereNode( diameterSmall, color );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atomBig );
        parentNode.addChild( atomSmall );

        // layout
        double x = 0;
        double y = 0;
        atomBig.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMinX();
        y = atomBig.getFullBoundsReference().getCenterX() - ( 0.15 * atomSmall.getFullBoundsReference().getHeight() );
        atomSmall.setOffset( x, y );
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
