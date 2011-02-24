// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.game;

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
 * Base class for all indicators used in the Game to tell the user
 * whether their guess is balanced or unbalanced.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GameResultNode extends PComposite {

    private static final PhetFont FONT = new PhetFont( 24 );
    private static final Color BACKGROUND = new Color( 180, 205, 255 );
    private static final double MARGIN = 20;
    private static final double FACE_DIAMETER = 100;

    public GameResultNode( boolean smile ) {
        setPickable( false );
        setChildrenPickable( false );

        FaceNode faceNode = new FaceNode( FACE_DIAMETER );
        if ( !smile ) {
            faceNode.frown();
        }

        PNode parentNode = new PComposite();

        PNode iconsAndTextNode = createIconsAndText( FONT );

        // rendering order for parentNode children
        parentNode.addChild( faceNode );
        parentNode.addChild( iconsAndTextNode );

        // layout
        double maxWidth = Math.max( faceNode.getFullBoundsReference().getWidth(), iconsAndTextNode.getFullBoundsReference().getWidth() );
        double x = ( maxWidth / 2 ) - ( faceNode.getFullBoundsReference().getWidth() / 2 );
        double y = 0;
        faceNode.setOffset( x, y );
        x = ( maxWidth / 2 ) - ( iconsAndTextNode.getFullBoundsReference().getWidth() / 2 );
        y = faceNode.getFullBoundsReference().getMaxY() + 20;
        iconsAndTextNode.setOffset( x, y );
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );

        // now add a transparent background
        double w = parentNode.getFullBoundsReference().getWidth() + ( 2 * MARGIN );
        double h = parentNode.getFullBoundsReference().getHeight() + ( 2 * MARGIN );
        PPath backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, w, h ) );
        backgroundNode.setPaint( BACKGROUND );
        addChild( backgroundNode );
        addChild( parentNode );

        // layout the top-level nodes
        backgroundNode.setOffset( 0, 0 );
        parentNode.setOffset( parentNode.getXOffset() + MARGIN, parentNode.getYOffset() + MARGIN );
    }

    protected abstract PNode createIconsAndText( PhetFont font );

    /**
     * Indicator that equation is balanced.
     * Smiley face, check mark, and text.
     */
    public static class BalancedNode extends GameResultNode {

        public BalancedNode() {
            super( true /* smile */ );
        }

        protected PNode createIconsAndText( PhetFont font ) {
            PNode parentNode = new PNode();

            PImage iconNode = new PImage( BCEResources.getImage( "Check-Mark-u2713.png" ) );
            parentNode.addChild( iconNode );

            PText textNode = new PText( BCEStrings.BALANCED );
            textNode.setFont( font );
            parentNode.addChild( textNode );

            // layout
            iconNode.setOffset( 0, 0 );
            double x = iconNode.getFullBoundsReference().getMaxX() + 2;
            double y = iconNode.getFullBoundsReference().getCenterY() - ( textNode.getFullBoundsReference().getHeight() / 2 );
            textNode.setOffset( x, y );

            return parentNode;
        }
    }

    /**
     * Indicator that equation is not balanced, by any definition of balanced.
     * Frowny face, big "X" and text.
     */
    public static class NotBalancedNode extends GameResultNode {

        public NotBalancedNode() {
            super( false /* smile */ );
        }

        protected PNode createIconsAndText( PhetFont font ) {
            PNode parentNode = new PNode();

            PImage iconNode = new PImage( BCEResources.getImage( "Heavy-Ballot-X-u2718.png" ) );
            parentNode.addChild( iconNode );

            PText textNode = new PText( BCEStrings.NOT_BALANCED );
            textNode.setFont( font );
            parentNode.addChild( textNode );

            // layout
            iconNode.setOffset( 0, 0 );
            double x = iconNode.getFullBoundsReference().getMaxX() + 2;
            double y = iconNode.getFullBoundsReference().getCenterY() - ( textNode.getFullBoundsReference().getHeight() / 2 );
            textNode.setOffset( x, y );

            return parentNode;
        }
    }

    /**
     * Indicator that equation is balanced, but not simplified (not lowest coefficients).
     * Frowny face, check mark for balanced, big "X" for not simplified.
     */
    public static class BalancedNotSimplifiedNode extends GameResultNode {

        public BalancedNotSimplifiedNode() {
            super( false /* smile */ );
        }

        @Override
        protected PNode createIconsAndText( PhetFont font ) {

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

            // layout
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
    }
}
