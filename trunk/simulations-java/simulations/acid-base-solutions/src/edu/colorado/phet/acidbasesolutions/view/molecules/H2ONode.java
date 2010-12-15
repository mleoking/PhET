/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.molecules;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * H2O molecule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class H2ONode extends AbstractMoleculeNode {

    public H2ONode() {

        // attributes
        double diameterBig = 24;
        double diameterSmall = 14;
        Color color = ABSColors.H2O;
        Color hiliteColor = Color.WHITE;
        Stroke stroke = new BasicStroke( 0.5f );
        Color strokeColor = color.darker();

        // atom nodes
        SphericalNode atomBig = new SphericalNode( diameterBig, createPaint( diameterBig, color, hiliteColor ), stroke, strokeColor, false );
        SphericalNode atomSmallTop = new SphericalNode( diameterSmall, createPaint( diameterSmall, color, hiliteColor ), stroke, strokeColor, false );
        SphericalNode atomSmallBottom = new SphericalNode( diameterSmall, createPaint( diameterSmall, color, hiliteColor ), stroke, strokeColor, false );

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
