// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.balancingchemicalequations.BCEResources;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Indicator that equation is balanced.
 * Smiley face, check mark, and text.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalancedNode extends PComposite {

    public BalancedNode() {
        setPickable( false );
        setChildrenPickable( false );

        FaceNode faceNode = new FaceNode( 100 );
        faceNode.smile();

        PImage checkNode = new PImage( BCEResources.getImage( "Check-Mark-u2713.png" ) );

        PText balancedTextNode = new PText( BCEStrings.BALANCED );
        balancedTextNode.setFont( new PhetFont( 24 ) );

        PNode parentNode = new PComposite();

        // rendering order for parentNode children
        parentNode.addChild( faceNode );
        parentNode.addChild( checkNode );
        parentNode.addChild( balancedTextNode );

        // layout
        checkNode.setOffset( 0, 0 );
        double x = checkNode.getFullBoundsReference().getMaxX() + 2;
        double y = checkNode.getFullBoundsReference().getCenterY() - ( balancedTextNode.getFullBoundsReference().getHeight() / 2 );
        balancedTextNode.setOffset( x, y );
        x = parentNode.getFullBoundsReference().getCenterX() - ( faceNode.getFullBoundsReference().getWidth() / 2 );
        y = parentNode.getFullBoundsReference().getMinY() - faceNode.getFullBoundsReference().getHeight() - 20;
        faceNode.setOffset( x, y );
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );

        // now add a transparent background
        double margin = 20;
        double w = parentNode.getFullBoundsReference().getWidth() + ( 2 * margin );
        double h = parentNode.getFullBoundsReference().getHeight() + ( 2 * margin );
        PPath backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, w, h ) );
        backgroundNode.setPaint( new Color( 180, 205, 255 ) );
        addChild( backgroundNode );
        addChild( parentNode );

        // layout the top-level nodes
        backgroundNode.setOffset( 0, 0 );
        parentNode.setOffset( parentNode.getXOffset() + margin, parentNode.getYOffset() + margin );
    }
}
