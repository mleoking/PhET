package edu.colorado.phet.acidbasesolutions.view.reactionequations;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.model.ConcentrationScaleModel;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for all reaction equations.
 * Reaction equations are composed of at most 4 terms, numbered 0-3.
 * Each term has a symbol and a Lewis structure diagram.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractReactionEquationNode extends PComposite {
    
    private static final int FONT_SIZE = 18;
    
    private static final int MAX_TERMS = 4; // don't attempt to change this
    
    private static final double X_SPACING = 20;
    private static final double Y_SPACING = 10;
    
    private static final Font SYMBOL_FONT = new PhetFont( Font.BOLD, FONT_SIZE );
    private static final Font PLUS_FONT = new PhetFont( Font.BOLD, FONT_SIZE );
    private static final Color PLUS_COLOR = Color.BLACK;
    
    private final Term[] terms;
    private final double[] scales;
    private final PlusNode plusLHS, plusRHS;
    private PImage arrow;
    private boolean scalingEnabled;
    private boolean structuresVisible;
    private boolean structuresEnabled;
    
    /*
     * Sets up a default reaction equation that looks like: ?0 + ?1 -> ?2 + ?3
     * and has no Lewis Structure diagrams.
     */
    public AbstractReactionEquationNode() {
        super();
        
        structuresVisible = false;
        structuresEnabled = true;
        
        terms = new Term[MAX_TERMS];
        for ( int i = 0; i < terms.length; i++ ) {
            terms[i] = new Term( new SymbolNode( "?" + i ) );
            addChild( terms[i].getSymbolNode() );
            // don't add the Lewis structure, it's invisible by default
        }
        
        scalingEnabled = false;
        scales = new double[MAX_TERMS];
        for ( int i = 0; i < scales.length; i++ ) {
            scales[i] = 1.0;
        }
        
        plusLHS = new PlusNode();
        addChild( plusLHS );
        
        plusRHS = new PlusNode();
        addChild( plusRHS );
        
        arrow = new PImage();
        addChild( arrow );
        
        updateLayout();
    }
    
    /**
     * Call this method to update the equation.
     * We use this approach instead of listening to the solution.
     * Listening to the solution introduces problems with order dependencies.
     * And if the solute type changes, then the expression type may no longer be appropriate.
     */
    public abstract void update();
    
    /**
     * H2O color depends on whether scaling is enabled.
     */
    protected abstract void updateH2OColor();
    
    public void setScalingEnabled( boolean enabled ) {
        if ( enabled != scalingEnabled ) {
            scalingEnabled = enabled;
            if ( !enabled ) {
                setUnityScale();
            }
            else {
                for ( int i = 0; i < scales.length; i++ ) {
                    setTermScale( i, scales[i] );
                }
            }
        }
        updateH2OColor();
    }
    
    public boolean isScalingEnabled() {
        return scalingEnabled;
    }
    
    public int getNumberOfTerms() {
        return terms.length;
    }
    
    protected void scaleTermToConcentration( int index, double concentration ) {
        double scale = ConcentrationScaleModel.getFontSize( concentration ) / SYMBOL_FONT.getSize();
        setTermScale( index, scale );
    }
    
    private void setTermScale( int index, double scale ) {
        scales[index] = scale;
        if ( scalingEnabled ) {
            setScaleAboutCenter( terms[index].getSymbolNode(), scale );
        }
    }
    
    /*
     * Scales a node about its center.
     */
    private void setScaleAboutCenter( PNode node, double scale ) {
        PBounds boundsBefore = node.getFullBounds();
        node.setScale( scale );
        PBounds boundsAfter = node.getFullBounds();
        double xOffset = node.getXOffset() - ( ( boundsAfter.getWidth() - boundsBefore.getWidth() ) / 2 );
        double yOffset = node.getYOffset() - ( ( boundsAfter.getHeight() - boundsBefore.getHeight() ) / 2 );
        node.setOffset( xOffset, yOffset );
    }
    
    /*
     * Sets all scalable nodes to have unity scale.
     */
    private void setUnityScale() {
        for ( int i = 0; i < terms.length; i++ ) {
            setScaleAboutCenter( terms[i].getSymbolNode(), 1.0 );
        }
    }

    protected void setBidirectional( boolean b ) {
        if ( b ) {
            arrow.setImage( ABSImages.ARROW_DOUBLE );
        }
        else {
            arrow.setImage( ABSImages.ARROW_SINGLE );
        }
        updateLayout();
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
     * Sets the mutable properties of a Term.
     */
    protected void setTerm( int index, String text, Color color, Image structureImage ) {
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
    
    protected void setTermColor( int index, Color color ) {
        terms[index].getSymbolNode().setHTMLColor( color );
    }
    
    public void setStructuresEnabled( boolean enabled ) {
        if ( enabled != structuresEnabled ) {
            if ( enabled ) {
                structuresEnabled = true;
                setStructuresVisible( structuresVisible );
            }
            else {
                setStructuresVisible( false );
                structuresEnabled = false;
            }
        }
    }
    
    /*
     * Sets the visibility of all Lewis structure diagrams.
     */
    protected void setStructuresVisible( boolean visible ) {
        structuresVisible = visible;
        if ( structuresEnabled ) {
            for ( int i = 0; i < terms.length; i++ ) {
                setStructureVisible( i, visible );
            }
        }
    }
    
    /*
     * Changes the visibility of one term's Lewis structure diagram.
     * Uses addChild/removeChild so that full bounds won't include an invisible structure.
     */
    private void setStructureVisible( int index, boolean visible ) {
        Term term = terms[ index ];
        StructureNode structureNode = term.getStructureNode();
        if ( visible ) {
            addChild( structureNode );
        }
        else if ( indexOfChild( structureNode ) != -1 ) {
            removeChild( structureNode );
        }
        updateLayout();
    }
    
    /*
     * Sets the position of all nodes.
     */
    private void updateLayout() {
        
        // do the layout at unity scale
        setUnityScale();
        
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
        
        // restore scaling of each symbol
        for ( int i = 0; i < scales.length; i++ ) {
            setTermScale( i, scales[i] );
        }
    }
    
    /*
     * Handles the positioning of a term.
     * @param index the index of the term
     * @param xStart the starting location of the xOffset
     * @param structureYOffset the y offset of the Lewis Structure diagram
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
