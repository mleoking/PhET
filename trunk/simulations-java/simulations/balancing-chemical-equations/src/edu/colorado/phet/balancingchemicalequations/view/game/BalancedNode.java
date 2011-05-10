// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.game;

import java.awt.Font;

import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.BCEResources;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;


/**
 * Indicator that an equation is balanced.
 * This looks like a dialog, and contains a smiley face, and check mark for balanced
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalancedNode extends GamePopupNode {

    /**
     * Convenience constructor.
     */
    public BalancedNode( int points, BCEGlobalProperties globalProperties ) {
        this( points, globalProperties.popupsCloseButtonVisible.get(), globalProperties.popupsTitleBarVisible.get() );
    }

    /*
     * @param points
     * @param closeButtonVisible
     * @param titleBarVisible
     */
    private BalancedNode( final int points, boolean closeButtonVisible, boolean titleBarVisible ) {
        super( true /* smile */, closeButtonVisible, titleBarVisible, new Function1<PhetFont, PNode>() {
            public PNode apply( PhetFont phetFont ) {
                PNode parentNode = new PNode();

                PImage iconNode = new PImage( BCEResources.getImage( "Check-Mark-u2713.png" ) );
                parentNode.addChild( iconNode );

                PText textNode = new PText( BCEStrings.BALANCED );
                textNode.setFont( phetFont );
                parentNode.addChild( textNode );

                PText pointsNode = new PText( "+" + String.valueOf( points ) );
                pointsNode.setFont( new PhetFont( Font.BOLD, 40 ) );
                parentNode.addChild( pointsNode );

                // layout: icon to left of text, points centered below
                iconNode.setOffset( 0, 0 );
                double x = iconNode.getFullBoundsReference().getMaxX() + 2;
                double y = iconNode.getFullBoundsReference().getCenterY() - ( textNode.getFullBoundsReference().getHeight() / 2 );
                textNode.setOffset( x, y );
                x = iconNode.getFullBoundsReference().getMinX() + ( ( textNode.getFullBoundsReference().getMaxX() - iconNode.getFullBoundsReference().getMinX() ) /  2 ) - ( pointsNode.getFullBoundsReference().getWidth() / 2 );
                y = Math.max( iconNode.getFullBoundsReference().getMaxY(), textNode.getFullBoundsReference().getMaxY() ) + 5;
                pointsNode.setOffset( x, y );

                return parentNode;
            }
        } );
    }
}