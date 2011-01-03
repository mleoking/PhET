/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.molecules;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * MOH molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MOHNode extends PComposite {

    public MOHNode() {

        // attributes
        double diameterBig = 24;
        double diameterMedium = 19;
        double diameterSmall = 14;
        Color color = ABSColors.MOH;

        // atom nodes
        ShadedSphereNode atomBig = new ShadedSphereNode( diameterBig, color );
        ShadedSphereNode atomMedium = new ShadedSphereNode( diameterMedium, color );
        ShadedSphereNode atomSmall = new ShadedSphereNode( diameterSmall, color );

        // minus
        PPath minusNode = new PPath( new Line2D.Double( 0, 0, diameterMedium / 4, 0) );
        minusNode.setStroke( new BasicStroke( 1f ) );
        minusNode.setStrokePaint( Color.BLACK );

        // plus
        PComposite plusNode = new PComposite();
        {
            final double length = diameterMedium / 4;
            PPath horizontalNode = new PPath( new Line2D.Double( 0, length / 2, length, length / 2 ) );
            horizontalNode.setStroke( new BasicStroke( 1f ) );
            horizontalNode.setStrokePaint( Color.BLACK );
            plusNode.addChild( horizontalNode );
            PPath verticalNode = new PPath( new Line2D.Double( length / 2, 0, length / 2, length ) );
            verticalNode.setStroke( new BasicStroke( 1f ) );
            verticalNode.setStrokePaint( Color.BLACK );
            plusNode.addChild( verticalNode );
        }

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atomBig );
        parentNode.addChild( atomMedium );
        parentNode.addChild( atomSmall );
        parentNode.addChild( minusNode );
        parentNode.addChild( plusNode );

        // layout
        double x = 0;
        double y = 0;
        atomBig.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getCenterX() - ( 1.2 * atomMedium.getFullBoundsReference().getWidth() );
        y = atomBig.getFullBoundsReference().getCenterY();
        atomMedium.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMaxX();
        y = atomBig.getFullBoundsReference().getCenterY() - ( 0.5 * atomSmall.getFullBoundsReference().getHeight() );
        atomSmall.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getCenterX() - ( minusNode.getFullBoundsReference().getWidth() / 2 );
        y = atomBig.getFullBoundsReference().getMaxY() + ( 0.15 * atomBig.getFullBoundsReference().getHeight() );
        minusNode.setOffset( x, y );
        x = atomMedium.getFullBoundsReference().getCenterX() - ( plusNode.getFullBoundsReference().getWidth() / 2 );
        y = minusNode.getFullBoundsReference().getCenterY() - ( plusNode.getFullBoundsReference().getHeight() / 2 );
        plusNode.setOffset( x, y );
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
