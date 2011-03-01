// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.game;

import edu.colorado.phet.balancingchemicalequations.BCEResources;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Indicator that equation is balanced.
 * Smiley face, check mark, and text.
 */
public class BalancedNode extends GameResultNode {

    public BalancedNode() {
        super( true /* smile */ , new Function1<PhetFont, PNode>() {
            public PNode apply( PhetFont phetFont ) {
                PNode parentNode = new PNode();

                PImage iconNode = new PImage( BCEResources.getImage( "Check-Mark-u2713.png" ) );
                parentNode.addChild( iconNode );

                PText textNode = new PText( BCEStrings.BALANCED );
                textNode.setFont( phetFont );
                parentNode.addChild( textNode );

                // layout: icon to left of text
                iconNode.setOffset( 0, 0 );
                double x = iconNode.getFullBoundsReference().getMaxX() + 2;
                double y = iconNode.getFullBoundsReference().getCenterY() - ( textNode.getFullBoundsReference().getHeight() / 2 );
                textNode.setOffset( x, y );

                return parentNode;
            }
        } );
    }
}