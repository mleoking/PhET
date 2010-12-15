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
 * H3O+ molecule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class H3OPlusNode extends AbstractMoleculeNode {
    
    public H3OPlusNode() {
        
        // attributes
        double diameterBig = 24;
        double diameterSmall = 14;
        Color color = ABSColors.H3O_PLUS.darker();
        Color hiliteColor = Color.WHITE;
        Stroke stroke = new BasicStroke( 0.5f );
        Color strokeColor = color.darker();

        // atom nodes
        SphericalNode atomBig = new SphericalNode( diameterBig, createPaint( diameterBig, color, hiliteColor ), stroke, strokeColor, false );
        SphericalNode atomSmallLeft = new SphericalNode( diameterSmall, createPaint( diameterSmall, color, hiliteColor ), stroke, strokeColor, false );
        SphericalNode atomSmallTopRight = new SphericalNode( diameterSmall, createPaint( diameterSmall, color, hiliteColor ), stroke, strokeColor, false );
        SphericalNode atomSmallBottomRight = new SphericalNode( diameterSmall, createPaint( diameterSmall, color, hiliteColor ), stroke, strokeColor, false );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atomSmallTopRight );
        parentNode.addChild( atomSmallBottomRight );
        parentNode.addChild( atomBig );
        parentNode.addChild( atomSmallLeft );

        // layout
        double x = 0;
        double y = 0;
        atomBig.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMinX();
        y = atomBig.getFullBoundsReference().getCenterX();
        atomSmallLeft.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMaxX() - ( 0.5 * atomSmallTopRight.getFullBoundsReference().getWidth() );
        y = atomBig.getFullBoundsReference().getMinY();
        atomSmallTopRight.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMaxX() - ( 0.5 * atomSmallBottomRight.getFullBoundsReference().getWidth() );
        y = atomBig.getFullBoundsReference().getMaxY();
        atomSmallBottomRight.setOffset( x, y );
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
