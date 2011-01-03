/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.molecules;

import java.awt.Color;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * BH+ molecule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BHPlusNode extends PComposite {

    public BHPlusNode() {

        // attributes
        double diameterBig = 24;
        double diameterSmall = 14;
        Color color = ABSColors.BH_PLUS;

        // atom nodes
        ShadedSphereNode atomBig = new ShadedSphereNode( diameterBig, color );
        ShadedSphereNode atomSmall = new ShadedSphereNode( diameterSmall, color );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atomSmall );
        parentNode.addChild( atomBig );

        // layout
        double x = 0;
        double y = 0;
        atomBig.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMinX() + ( 0.25 * atomSmall.getFullBoundsReference().getWidth() );
        y = atomBig.getFullBoundsReference().getCenterX() - ( 0.75 * atomSmall.getFullBoundsReference().getHeight() );
        atomSmall.setOffset( x, y );
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
