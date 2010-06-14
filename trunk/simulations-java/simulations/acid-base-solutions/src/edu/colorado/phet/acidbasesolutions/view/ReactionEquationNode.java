/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.*;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.ModelChangeAdapter;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.SwingLayoutNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Reaction equation for solutions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ReactionEquationNode extends PComposite {
    
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

    private final ABSModel model;
    
    public ReactionEquationNode( ABSModel model ) {
        
        this.model = model;
        model.addModelChangeListener( new ModelChangeAdapter() {
            public void solutionChanged() {
                update();
            }
        });
        
        update();
    }
    
    /*
     * Pure water: H2O + H2O <-> H3O+ + OH-
     * Strong acid: HA + H2O -> H3O+ + A-
     * Weak acid: HA + H2O <-> H3O+ + A-
     * Strong base: MOH -> M+ + OH-
     * Weak base: B + H2O <-> BH+ + OH-
     */
    private void update() {
        
        removeAllChildren();
        
        AqueousSolution solution = model.getSolution();
        boolean isPureWater = ( solution instanceof PureWaterSolution );
        boolean isAcid = ( solution instanceof StrongAcidSolution || solution instanceof WeakAcidSolution );
        boolean isStrong = ( solution instanceof StrongAcidSolution || solution instanceof StrongBaseSolution );
        
        // molecule images & symbols
        PImage imageLHS1, imageLHS2, imageRHS1, imageRHS2;
        SymbolNode symbolLHS1, symbolLHS2, symbolRHS1, symbolRHS2;
        if ( isPureWater ) {
            imageLHS1 = new PImage( ABSImages.H2O_MOLECULE );
            symbolLHS1 = new SymbolNode( ABSSymbols.H2O );
            imageLHS2 = new PImage( ABSImages.H2O_MOLECULE );
            symbolLHS2 = new SymbolNode( ABSSymbols.H2O );
            imageRHS1 = new PImage( ABSImages.H3O_PLUS_MOLECULE );
            symbolRHS1 = new SymbolNode( ABSSymbols.H3O_PLUS );
            imageRHS2 = new PImage( ABSImages.OH_MINUS_MOLECULE );
            symbolRHS2 = new SymbolNode( ABSSymbols.OH_MINUS );
        }
        else {
            imageLHS1 = new PImage( solution.getSolute().getIcon() );
            symbolLHS1 = new SymbolNode( solution.getSolute().getSymbol() );
            if ( isAcid ) {
                imageLHS2 = isStrong ? null : new PImage( ABSImages.H2O_MOLECULE );
                symbolLHS2 = isStrong ? null : new SymbolNode( ABSSymbols.H2O );
                imageRHS1 = new PImage( ABSImages.H3O_PLUS_MOLECULE );
                symbolRHS1 = new SymbolNode( ABSSymbols.H3O_PLUS );
                imageRHS2 = new PImage( solution.getProduct().getIcon() );
                symbolRHS2 = new SymbolNode( solution.getProduct().getSymbol() );
            }
            else {
                imageLHS2 = new PImage( ABSImages.H2O_MOLECULE );
                symbolLHS2 = new SymbolNode( ABSSymbols.H2O );
                imageRHS1 = new PImage( solution.getProduct().getIcon() );
                symbolRHS1 = new SymbolNode( solution.getProduct().getSymbol() );
                imageRHS2 = new PImage( ABSImages.OH_MINUS_MOLECULE );
                symbolRHS2 = new SymbolNode( ABSSymbols.OH_MINUS );
            }
        }
        
        // mathematical symbols
        PNode arrowNode = new PImage( isStrong ? ABSImages.ARROW_SINGLE : ABSImages.ARROW_DOUBLE );
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
        layoutNode.addChild( imageLHS1, constraints );
        constraints.gridx++; 
        constraints.gridx++; 
        if ( imageLHS2 != null ) {
            layoutNode.addChild( imageLHS2, constraints );
            constraints.gridx++;
            constraints.gridx++;
        }
        layoutNode.addChild( imageRHS1, constraints );
        constraints.gridx++; 
        constraints.gridx++;
        layoutNode.addChild( imageRHS2, constraints );
        constraints.gridx = 0;
        constraints.gridy++;
        layoutNode.addChild( symbolLHS1, constraints );
        constraints.gridx++;
        if ( symbolLHS2 != null ) {
            layoutNode.addChild( plusLeftNode, constraints );
            constraints.gridx++;
            layoutNode.addChild( symbolLHS2, constraints );
            constraints.gridx++;
        }
        layoutNode.addChild( arrowNode, constraints );
        constraints.gridx++;
        layoutNode.addChild( symbolRHS1, constraints );
        constraints.gridx++;
        layoutNode.addChild( plusRightNode, constraints );
        constraints.gridx++;
        layoutNode.addChild( symbolRHS2, constraints );
        constraints.gridx++;
    }
}
