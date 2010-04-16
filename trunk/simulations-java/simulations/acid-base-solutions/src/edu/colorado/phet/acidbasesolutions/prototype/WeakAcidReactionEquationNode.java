/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Reaction equation for a weak acid.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WeakAcidReactionEquationNode extends PComposite {
    
    private static final Font SYMBOL_FONT = new PhetFont( 20 );
    private static final double X_SPACING = 10;
    private static final double Y_SPACING = 5;
    
    private static class SymbolNode extends HTMLNode {
        public SymbolNode( String html ) {
            super( html );
            setFont( SYMBOL_FONT );
        }
    }

    public WeakAcidReactionEquationNode() {
        
        // molecule icons
        PImage imageHA = new PImage( MGPConstants.HA_IMAGE );
        PImage imageH2O = new PImage( MGPConstants.H2O_IMAGE );
        PImage imageH3O = new PImage( MGPConstants.H3O_PLUS_IMAGE );
        PImage imageA = new PImage( MGPConstants.A_MINUS_IMAGE );
        
        // molecule symbols
        SymbolNode symbolHA = new SymbolNode( MGPConstants.HA_FRAGMENT );
        SymbolNode symbolH2O = new SymbolNode( MGPConstants.H2O_FRAGMENT );
        SymbolNode symbolH3O = new SymbolNode( MGPConstants.H3O_PLUS_FRAGMENT );
        SymbolNode symbolA = new SymbolNode( MGPConstants.A_MINUS_FRAGMENT );
        
        // mathematical symbols
        PNode arrowNode = new PImage( MGPConstants.ARROW_DOUBLE_IMAGE );
        PNode plusLeftNode = new PText( "+" );
        PNode plusRightNode = new PText( "+" );
        
        // rendering order
        addChild( imageHA );
        addChild( imageH2O );
        addChild( imageH3O );
        addChild( imageA );
        addChild( symbolHA );
        addChild( symbolH2O );
        addChild( symbolH3O );
        addChild( symbolA );
        addChild( arrowNode );
        addChild( plusLeftNode );
        addChild( plusRightNode );
        
        // layout
        double x = 0;
        double y = 0;
        // HA
        symbolHA.setOffset( x, y );
        // +
        x = symbolHA.getFullBoundsReference().getMaxX() + X_SPACING;
        y = ( symbolHA.getFullBoundsReference().getHeight() - plusLeftNode.getFullBoundsReference().getHeight() ) / 2;
        plusLeftNode.setOffset( x, y );
        // H2O
        x = plusLeftNode.getFullBoundsReference().getMaxX() + X_SPACING;
        y = ( symbolHA.getFullBoundsReference().getHeight() - symbolH2O.getFullBoundsReference().getHeight() ) / 2;
        symbolH2O.setOffset( x, y );
        // <->
        x = symbolH2O.getFullBoundsReference().getMaxX() + X_SPACING;
        y = ( symbolHA.getFullBoundsReference().getHeight() - arrowNode.getFullBoundsReference().getHeight() ) / 2;
        arrowNode.setOffset( x, y );
        // H3O
        x = arrowNode.getFullBoundsReference().getMaxX() + X_SPACING;
        y = ( symbolHA.getFullBoundsReference().getHeight() - symbolH3O.getFullBoundsReference().getHeight() ) / 2;
        symbolH3O.setOffset( x, y );
        // +
        x = symbolH3O.getFullBoundsReference().getMaxX() + X_SPACING;
        y = ( symbolHA.getFullBoundsReference().getHeight() - plusRightNode.getFullBoundsReference().getHeight() ) / 2;
        plusRightNode.setOffset( x, y );
        // A
        x = plusRightNode.getFullBoundsReference().getMaxX() + X_SPACING;
        y = ( symbolHA.getFullBoundsReference().getHeight() - symbolA.getFullBoundsReference().getHeight() ) / 2;
        symbolA.setOffset( x, y );
        // center images above symbols
        centerImage( imageHA, symbolHA );
        centerImage( imageH2O, symbolH2O );
        centerImage( imageH3O, symbolH3O );
        centerImage( imageA, symbolA );
    }
    
    // centers image above symbol
    private static void centerImage( PNode imageNode, SymbolNode symbolNode ) {
        double x = symbolNode.getFullBoundsReference().getCenterX() - ( imageNode.getFullBoundsReference().getWidth() / 2 );
        double y = symbolNode.getFullBoundsReference().getMinY() - imageNode.getFullBoundsReference().getHeight() - Y_SPACING;
        imageNode.setOffset( x, y );
    }
}
