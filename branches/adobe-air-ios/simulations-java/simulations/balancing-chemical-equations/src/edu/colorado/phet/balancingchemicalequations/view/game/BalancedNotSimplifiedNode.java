// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.game;

import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.BCEResources;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Indicator that an equation is balanced, but not simplified (not lowest coefficients).
 * This looks like a dialog, and contains:
 * <ul>
 * <li>frowny face
 * <li>check mark for "balanced"
 * <li>big "X" for "not simplified"
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalancedNotSimplifiedNode extends GamePopupNode {

    /**
     * Convenience constructor.
     */
    public BalancedNotSimplifiedNode( BCEGlobalProperties globalProperties ) {
        this( globalProperties.popupsCloseButtonVisible.get(), globalProperties.popupsTitleBarVisible.get() );
    }

    /*
     * @param closeButtonVisible
     * @param titleBarVisible
     */
    private BalancedNotSimplifiedNode( boolean closeButtonVisible, boolean titleBarVisible ) {
        super( false /* smile */, closeButtonVisible, titleBarVisible, new Function1<PhetFont, PNode>() {
            public PNode apply( PhetFont font ) {

                PNode parentNode = new PNode();

                PImage checkNode = new PImage( BCEResources.getImage( "Check-Mark-u2713.png" ) );
                parentNode.addChild( checkNode );

                PText balancedTextNode = new PText( BCEStrings.BALANCED );
                balancedTextNode.setFont( font );
                parentNode.addChild( balancedTextNode );

                PImage xNode = new PImage( BCEResources.getImage( "Heavy-Ballot-X-u2718.png" ) );
                parentNode.addChild( xNode );

                PText notSimplifiedTextNode = new PText( BCEStrings.NOT_SIMPLIFIED );
                notSimplifiedTextNode.setFont( font );
                parentNode.addChild( notSimplifiedTextNode );

                // layout: icons on left, text on right
                final double maxImageWidth = Math.max( checkNode.getFullBoundsReference().getWidth(), xNode.getFullBoundsReference().getWidth() );
                double x = maxImageWidth - checkNode.getFullBoundsReference().getWidth();
                double y = 0;
                checkNode.setOffset( x, y );
                x = checkNode.getFullBoundsReference().getMaxX() + 2;
                y = checkNode.getFullBoundsReference().getCenterY() - ( balancedTextNode.getFullBoundsReference().getHeight() / 2 );
                balancedTextNode.setOffset( x, y );
                x = maxImageWidth - xNode.getFullBoundsReference().getWidth();
                y = Math.max( checkNode.getFullBoundsReference().getMaxY(), balancedTextNode.getFullBoundsReference().getMaxY() ) + 4;
                xNode.setOffset( x, y );
                x = balancedTextNode.getXOffset();
                y = xNode.getFullBoundsReference().getCenterY() - ( notSimplifiedTextNode.getFullBoundsReference().getHeight() / 2 );
                notSimplifiedTextNode.setOffset( x, y );

                return parentNode;
            }
        } );
    }
}