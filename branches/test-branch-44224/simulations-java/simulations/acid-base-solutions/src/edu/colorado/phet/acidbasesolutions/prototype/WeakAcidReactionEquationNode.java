/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.SwingLayoutNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Reaction equation for a weak acid.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class WeakAcidReactionEquationNode extends PComposite {
    
    private static final Font SYMBOL_FONT = new PhetFont( 20 );
    
    private static class SymbolNode extends HTMLNode {
        public SymbolNode( String html ) {
            super( html );
            setFont( SYMBOL_FONT );
        }
    }
    
    private static class PlusNode extends PText {
        public PlusNode() {
            super( "+" );
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
        PlusNode plusLeftNode = new PlusNode();
        PlusNode plusRightNode = new PlusNode();

        // layout
        GridBagLayout layout = new GridBagLayout();
        SwingLayoutNode layoutNode = new SwingLayoutNode( layout );
        addChild( layoutNode );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets( 5, 15, 0, 0 ); // top, left, bottom, right
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        constraints.gridy = 0;
        layoutNode.addChild( imageHA, constraints );
        constraints.gridx++; 
        constraints.gridx++; 
        layoutNode.addChild( imageH2O, constraints );
        constraints.gridx++; 
        constraints.gridx++;
        layoutNode.addChild( imageH3O, constraints );
        constraints.gridx++; 
        constraints.gridx++;
        layoutNode.addChild( imageA, constraints );
        constraints.gridx = 0;
        constraints.gridy++;
        layoutNode.addChild( symbolHA, constraints );
        constraints.gridx++;
        layoutNode.addChild( plusLeftNode, constraints );
        constraints.gridx++;
        layoutNode.addChild( symbolH2O, constraints );
        constraints.gridx++;
        layoutNode.addChild( arrowNode, constraints );
        constraints.gridx++;
        layoutNode.addChild( symbolH3O, constraints );
        constraints.gridx++;
        layoutNode.addChild( plusRightNode, constraints );
        constraints.gridx++;
        layoutNode.addChild( symbolA, constraints );
        constraints.gridx++;
    }
}
