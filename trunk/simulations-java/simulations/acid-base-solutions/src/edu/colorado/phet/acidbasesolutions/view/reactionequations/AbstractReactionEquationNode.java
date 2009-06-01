package edu.colorado.phet.acidbasesolutions.view.reactionequations;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions.ConcentrationScaleModel;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for all acid/base/water reaction equations.
 * Reaction equations are composed of at most 4 terms, numbered 0-3.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractReactionEquationNode extends PComposite {
    
    private static final int MAX_TERMS = 4; // don't attempt to change this
    
    private static final double X_SPACING = 20;
    private static final double Y_SPACING = 10;
    
    private static final Font SYMBOL_FONT = new PhetFont( Font.BOLD, 24 );
    private static final Font PLUS_FONT = new PhetFont( Font.BOLD, 22 );
    private static final Color PLUS_COLOR = Color.BLACK;
    
    private final Term[] terms;
    private final PlusNode plusLHS, plusRHS;
    private PImage arrow;
    
    /*
     * Sets up a default reaction equation that looks like: S0 + S1 -> S2 + S3
     * and has no Lewis Structure diagrams.
     */
    public AbstractReactionEquationNode() {
        super();
        
        terms = new Term[MAX_TERMS];
        for ( int i = 0; i < terms.length; i++ ) {
            terms[i] = new Term( new SymbolNode( "S" + i ) );
            addTerm( terms[i] );
        }
        
        plusLHS = new PlusNode();
        addChild( plusLHS );
        
        plusRHS = new PlusNode();
        addChild( plusRHS );
        
        arrow = new PImage();
        addChild( arrow );
        
        updateLayout();
    }
    
    public int getNumberOfTerms() {
        return terms.length;
    }
    
    protected void scaleTermToConcentration( int index, double concentration ) {
        double scale = ConcentrationScaleModel.getFontSize( concentration ) / SYMBOL_FONT.getSize();
        setTermScale( index, scale );
    }
    
    // scale about center
    private void setTermScale( int index, double scale ) {
        SymbolNode symbolNode = terms[index].getSymbolNode();
        PBounds boundsBefore = symbolNode.getFullBounds();
        symbolNode.setScale( scale );
        PBounds boundsAfter = symbolNode.getFullBounds();
        double xOffset = symbolNode.getXOffset() - ( boundsAfter.getWidth() - boundsBefore.getWidth() ) / 2;
        double yOffset = symbolNode.getYOffset() - ( boundsAfter.getHeight() - boundsBefore.getHeight() ) / 2;
        symbolNode.setOffset( xOffset, yOffset );
    }
    
    
    /**
     * Sets all scalable nodes to have the same scale.
     */
    protected void scaleAllTerms( double scale ) {
        for ( int i = 0; i < terms.length; i++ ) {
            setTermScale( i, scale );
        }
    }

    protected void setBidirectional( boolean b ) {
        if ( b ) {
            arrow.setImage( ABSImages.ARROW_DOUBLE );
        }
        else {
            arrow.setImage( ABSImages.ARROW_SINGLE );
        }
    }
    
    /*
     * Changes the visibility of term 1.
     * Term 1 is typically H2O, which is not shown in the reaction equation for strong bases.
     */
    protected void setTerm1Visible( boolean visible ) {
        terms[1].setVisible( visible );
        plusLHS.setVisible( visible );
        updateLayout();
    }
    
    /*
     * Sets the mutable properties of a term.
     */
    protected void setTerm( int index, String text, Color color ) {
        setTerm( index, text, color, null );
    }
    
    /*
     * Sets the mutable properties of a Term.
     */
    protected void setTerm( int index, String text, Color color, BufferedImage structureImage ) {
        Term term = terms[index];
        // symbol
        SymbolNode symbolNode = term.getSymbolNode();
        symbolNode.setHTML( text );
        symbolNode.setHTMLColor( color );
        // structure
        StructureNode structureNode = term.getStructureNode();
        structureNode.setImage( structureImage );
        // layout
        updateLayout();
    }
    
    /*
     * Adds a term, by adding its nodes to the scenegraph.
     */
    private void addTerm( Term term ) {
        super.addChild( term.getSymbolNode() );
        super.addChild( term.getStructureNode() );
    }
    
    /*
     * Sets the position of all nodes.
     */
    private void updateLayout() {
        
        final double maxSymbolHeight = getMaxSymbolHeight();
        final double structureYOffset = ( maxSymbolHeight / 2 ) + Y_SPACING;
        double xOffset = 0;
        double yOffset = 0;
        int termIndex = 0;
        
        // term 0
        xOffset = layoutTerm( termIndex++, xOffset, structureYOffset );
        
        // term 1 is optional
        if ( terms[termIndex].isVisible() ) {
            
            // plus sign
            yOffset = -plusLHS.getFullBoundsReference().getHeight() / 2;
            plusLHS.setOffset( xOffset, yOffset );
            xOffset = plusLHS.getFullBoundsReference().getMaxX() + X_SPACING;
            
            // term 1
            xOffset = layoutTerm( termIndex++, xOffset, structureYOffset );
        }
        else {
            termIndex++;
        }
        
        // arrow
        yOffset = -arrow.getFullBoundsReference().getHeight() / 2;
        arrow.setOffset( xOffset, yOffset );
        xOffset = arrow.getFullBoundsReference().getMaxX() + X_SPACING;
        
        // term 2
        xOffset = layoutTerm( termIndex++, xOffset, structureYOffset );
        
        // plus sign
        yOffset = -plusRHS.getFullBoundsReference().getHeight() / 2;
        plusRHS.setOffset( xOffset, yOffset );
        xOffset = plusRHS.getFullBoundsReference().getMaxX() + X_SPACING;
        
        // term 3
        xOffset = layoutTerm( termIndex++, xOffset, structureYOffset );
    }
    
    /*
     * Handles the positioning of a term.
     * @param index the index of the term
     * @param xStart the starting location of the xOffset
     * @param yOffset the y offset of the Lewis Structure diagram
     */
    private double layoutTerm( int index, double xStart, double structureYOffset ) {
        double xOffset, yOffset;
        Term term = terms[index];
        // symbol
        SymbolNode symbolNode = term.getSymbolNode();
        xOffset = xStart;
        yOffset = -symbolNode.getFullBoundsReference().getHeight() / 2;
        symbolNode.setOffset( xOffset, yOffset );
        // structure
        StructureNode structureNode = term.getStructureNode();
        xOffset = symbolNode.getXOffset() + ( symbolNode.getFullBoundsReference().getWidth() - structureNode.getFullBoundsReference().getWidth() ) / 2;
        yOffset = structureYOffset;
        structureNode.setOffset( xOffset, yOffset );
        // next x offset 
        return symbolNode.getFullBoundsReference().getMaxX() + X_SPACING;
    }
    
    /*
     * Gets the maximum height of all the symbols.
     * Used to determine the offset of the Lewis Structure diagrams.
     */
    private double getMaxSymbolHeight() {
        double maxHeight = 0;
        for ( int i = 0; i < terms.length; i++ ) {
            PNode n = terms[i].getSymbolNode();
            if ( n != null ) {
                maxHeight = Math.max( maxHeight, n.getFullBoundsReference().getHeight() );
            }
        }
        return maxHeight;
    }
    
    /*
     * Each term in the equation has a symbol and optional Lewis Structure diagram.
     */
    private static class Term {

        public final SymbolNode symbolNode;
        public final StructureNode structureNode;

        public Term( SymbolNode symbolNode ) {
            this( symbolNode, new StructureNode() );
        }
        
        public Term( SymbolNode symbolNode, StructureNode structureNode ) {
            this.symbolNode = symbolNode;
            this.structureNode = structureNode;
        }

        public SymbolNode getSymbolNode() {
            return symbolNode;
        }

        public StructureNode getStructureNode() {
            return structureNode;
        }
        
        public void setVisible( boolean visible ) {
            symbolNode.setVisible( visible );
            structureNode.setVisible( visible );
        }
        
        public boolean isVisible() {
            return symbolNode.getVisible() || structureNode.getVisible();
        }
    }
    
    /*
     * A symbol is the formula for a molecule.
     */
    private static class SymbolNode extends HTMLNode {
        
        public SymbolNode( String text ) {
            super( HTMLUtils.toHTMLString( text ) );
            setFont( SYMBOL_FONT );
        }
        
        public void setHTML( String text ) {
            super.setHTML( HTMLUtils.toHTMLString( text ) );
        }
    }
    
    /*
     * Lewis Structure diagram.
     */
    private static class StructureNode extends PImage {
        
        public StructureNode() {
            super();
        }
        
        public StructureNode( Image image ) {
            super( image );
        }
    }
    
    /*
     * Plus signs between terms.
     */
    private static class PlusNode extends PText {
        public PlusNode() {
            super( "+" );
            setFont( PLUS_FONT );
            setTextPaint( PLUS_COLOR );
        }
    }
}
