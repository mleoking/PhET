/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.molecules;

import java.awt.Color;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.common.piccolophet.nodes.AtomNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * OH- molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OHMinusNode extends PComposite {

    public OHMinusNode() {

        // attributes
        double diameterBig = 24;
        double diameterSmall = 14;
        Color color = ABSColors.OH_MINUS;

        // atom nodes
        AtomNode atomBig = new AtomNode( diameterBig, color );
        AtomNode atomSmall = new AtomNode( diameterSmall, color );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atomBig );
        parentNode.addChild( atomSmall );

        // layout
        double x = 0;
        double y = 0;
        atomBig.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMaxX();
        y = atomBig.getFullBoundsReference().getCenterY() - ( 0.45 * atomSmall.getFullBoundsReference().getHeight() );
        atomSmall.setOffset( x, y );
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
