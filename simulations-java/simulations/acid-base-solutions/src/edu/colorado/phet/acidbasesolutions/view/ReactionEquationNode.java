/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.model.*;
import edu.colorado.phet.acidbasesolutions.model.Molecule.WaterMolecule;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ChemicalSymbolNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
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
    private static final Color SYMBOL_COLOR = Color.BLACK;
    private static final Image TWO_H2O_IMAGE = create2H2OImage();
    private static final double X_SPACING = 15; // horizontal distance between nodes
    private static final double Y_SPACING = 10; // vertical distance between molecule image and top of capital letters in symbol
    
    private static class SymbolNode extends ChemicalSymbolNode {
        public SymbolNode( String html ) {
            super( html, SYMBOL_FONT, SYMBOL_COLOR );
        }
    }
    
    private static class PlusNode extends PText {
        public PlusNode() {
            super( "+" );
            setFont( SYMBOL_FONT );
        }
    }
    
    private static class ArrowNode extends PImage {
        public ArrowNode( boolean isStrong ) {
            super( isStrong ? ABSImages.ARROW_SINGLE : ABSImages.ARROW_DOUBLE );
        }
    }

    private final ReactionEquation equation;
    
    public ReactionEquationNode( final ReactionEquation equation ) {
        
        // not interactive
        setPickable( false );
        setChildrenPickable( false );
        
        this.equation = equation;
        equation.addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            @Override
            public void solutionChanged() {
                update();
            }
        });
        
        setOffset( equation.getLocationReference() );
        update();
    }
    
    /*
     * Pure water: 2H2O <-> H3O+ + OH-
     * Strong acid: HA + H2O -> H3O+ + A-
     * Weak acid: HA + H2O <-> H3O+ + A-
     * Strong base: MOH -> M+ + OH-
     * Weak base: B + H2O <-> BH+ + OH-
     * 
     * So the 2nd term on the left-hand-side (LHS) may or may not be present.
     */
    private void update() {
        
        removeAllChildren();
        
        AqueousSolution solution = equation.getSolution();
        boolean isPureWater = ( solution instanceof PureWaterSolution );
        boolean isAcid = ( solution instanceof AcidSolution );
        boolean isStrong = ( solution instanceof StrongAcidSolution || solution instanceof StrongBaseSolution );
        
        // molecule images & symbols
        PImage imageLHS1, imageLHS2, imageRHS1, imageRHS2;
        SymbolNode symbolLHS1, symbolLHS2, symbolRHS1, symbolRHS2;
        if ( isPureWater ) {
            imageLHS1 = new PImage( TWO_H2O_IMAGE );
            symbolLHS1 = new SymbolNode( "2" + solution.getWaterMolecule().getSymbol() );
            imageLHS2 = null;
            symbolLHS2 = null;
            imageRHS1 = new PImage( solution.getH3OMolecule().getIcon() );
            symbolRHS1 = new SymbolNode( solution.getH3OMolecule().getSymbol() );
            imageRHS2 = new PImage( solution.getOHMolecule().getIcon() );
            symbolRHS2 = new SymbolNode( solution.getOHMolecule().getSymbol() );
        }
        else {
            imageLHS1 = new PImage( solution.getSolute().getIcon() );
            symbolLHS1 = new SymbolNode( solution.getSolute().getSymbol() );
            if ( isAcid ) {
                imageLHS2 = new PImage( solution.getWaterMolecule().getIcon() );
                symbolLHS2 = new SymbolNode( solution.getWaterMolecule().getSymbol() );
                imageRHS1 = new PImage( solution.getProduct().getIcon() );
                symbolRHS1 = new SymbolNode( solution.getProduct().getSymbol() );
                imageRHS2 = new PImage( solution.getH3OMolecule().getIcon() );
                symbolRHS2 = new SymbolNode( solution.getH3OMolecule().getSymbol() );
            }
            else {
                imageLHS2 = isStrong ? null : new PImage( solution.getWaterMolecule().getIcon() );
                symbolLHS2 = isStrong ? null : new SymbolNode( solution.getWaterMolecule().getSymbol() );
                imageRHS1 = new PImage( solution.getProduct().getIcon() );
                symbolRHS1 = new SymbolNode( solution.getProduct().getSymbol() );
                imageRHS2 = new PImage( solution.getOHMolecule().getIcon() );
                symbolRHS2 = new SymbolNode( solution.getOHMolecule().getSymbol() );
            }
        }
        
        // mathematical symbols
        ArrowNode arrowNode = new ArrowNode( isStrong );
        PlusNode plusLeftNode = new PlusNode();
        PlusNode plusRightNode = new PlusNode();
        
        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( imageLHS1 );
        parentNode.addChild( symbolLHS1 );
        if ( symbolLHS2 != null ) {
            parentNode.addChild( plusLeftNode );
            parentNode.addChild( imageLHS2 );
            parentNode.addChild( symbolLHS2 );
        }
        parentNode.addChild( arrowNode );
        parentNode.addChild( imageRHS1 );
        parentNode.addChild( symbolRHS1 );
        parentNode.addChild( plusRightNode );
        parentNode.addChild( imageRHS2 );
        parentNode.addChild( symbolRHS2 );
        
        // layout
        // LHS1
        double x, y;
        layoutSymbolAndImage( symbolLHS1, imageLHS1, 0 );
        SymbolNode previousSymbolNode = symbolLHS1;
        if ( symbolLHS2 != null ) {
            // +
            x = symbolLHS1.getFullBoundsReference().getMaxX() + X_SPACING;
            y = symbolLHS1.getYOffset() - ( symbolLHS1.getCapHeight() / 2 ) - ( plusLeftNode.getFullBoundsReference().getHeight() / 2 );
            plusLeftNode.setOffset( x, y );
            // LHS2
            layoutSymbolAndImage( symbolLHS2, imageLHS2, plusLeftNode.getFullBoundsReference().getMaxX() + X_SPACING );
            previousSymbolNode = symbolLHS2;
        }
        // <-->
        x = previousSymbolNode.getFullBoundsReference().getMaxX() + X_SPACING;
        y = previousSymbolNode.getYOffset() - ( previousSymbolNode.getCapHeight() / 2 ) - ( arrowNode.getFullBoundsReference().getHeight() / 2 );
        arrowNode.setOffset( x, y );
        // RHS1
        layoutSymbolAndImage( symbolRHS1, imageRHS1, arrowNode.getFullBoundsReference().getMaxX() + X_SPACING );
        // +
        x = symbolRHS1.getFullBoundsReference().getMaxX() + X_SPACING;
        y = symbolRHS1.getYOffset() - ( symbolRHS1.getCapHeight() / 2 ) - ( plusRightNode.getFullBoundsReference().getHeight() / 2 );
        plusRightNode.setOffset( x, y );
        // RHS2
        layoutSymbolAndImage( symbolRHS2, imageRHS2, plusRightNode.getFullBoundsReference().getMaxX() + X_SPACING );
        
        // origin is at the top center of this node
        x = -parentNode.getFullBoundsReference().getWidth() / 2;
        y = -PNodeLayoutUtils.getOriginYOffset( parentNode );
        parentNode.setOffset( x, y );
    }
    
    /*
     * General layout for a molecule's symbol and image.
     */
    private static void layoutSymbolAndImage( SymbolNode symbolNode, PImage imageNode, double symbolXOffset ) {
        double x = symbolXOffset;
        double y = 0;
        symbolNode.setOffset( x, y );
        x = symbolNode.getFullBoundsReference().getCenterX() - ( imageNode.getFullBoundsReference().getWidth() / 2 );
        y = symbolNode.getYOffset() - symbolNode.getCapHeight() - Y_SPACING - imageNode.getFullBoundsReference().getHeight();
        imageNode.setOffset( x, y );
    }
    
    /*
     * For the water reaction equation, the first term is "2H2O",
     * so we need 2 water molecules side by side.
     */
    private static Image create2H2OImage() {
        WaterMolecule waterMolecule = new WaterMolecule();
        // create 2 identical image nodes
        PImage node1 = new PImage( waterMolecule.getIcon() );
        PImage node2 = new PImage( waterMolecule.getIcon() );
        // give them a common parent
        PComposite parent = new PComposite();
        parent.addChild( node1 );
        parent.addChild( node2 );
        // position the nodes side-by-side
        node1.setOffset( 0, 0 );
        final double xSpacing = 2;
        node2.setOffset( node1.getFullBoundsReference().getMaxX() + xSpacing, node1.getYOffset() );
        // convert the parent to an image
        return parent.toImage();
    }
}
