package edu.colorado.phet.advancedacidbasesolutions.view.reactionequations;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import edu.colorado.phet.advancedacidbasesolutions.AABSImages;
import edu.colorado.phet.advancedacidbasesolutions.model.ConcentrationScaleModel;
import edu.colorado.phet.advancedacidbasesolutions.util.ScalingAnimator;
import edu.colorado.phet.advancedacidbasesolutions.view.ChemicalSymbolNode;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
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
    
    private final CenteredSymbolNode[] symbolNodes;
    private final StructureNode[] structureNodes;
    private final double[] scales;
    private ScalingAnimator[] animators;
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
        
        // symbols
        symbolNodes = new CenteredSymbolNode[MAX_TERMS];
        for ( int i = 0; i < symbolNodes.length; i++ ) {
            symbolNodes[i] = new CenteredSymbolNode( "ABC" + i, SYMBOL_FONT, Color.BLACK );
            addChild( symbolNodes[i] );
        }
        
        // Lewis Structure diagrams
        structureNodes = new StructureNode[MAX_TERMS];
        for ( int i = 0; i < structureNodes.length; i++ ) {
            structureNodes[i] = new StructureNode();
            // don't add the Lewis structure to the scene graph, it's invisible by default
        }
        
        // symbol scaling animators
        animators = new ScalingAnimator[MAX_TERMS];
        
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
    
    public void setScalingEnabled( boolean enabled, boolean animated ) {
        if ( enabled != scalingEnabled ) {
            scalingEnabled = enabled;
            if ( enabled ) {
                scaleToConcentration( animated );
            }
            else {
                scaleToUnity( animated );
            }
        }
        updateH2OColor();
    }
    
    public boolean isScalingEnabled() {
        return scalingEnabled;
    }
    
    private void scaleToConcentration( boolean animated ) {
        cancelScaleAnimation();
        if ( animated ) {
            for ( int i = 0; i < symbolNodes.length; i++ ) {
                animators[i] = new ScalingAnimator( symbolNodes[i], scales[i] );
                animators[i].start();
            }
        }
        else {
            for ( int i = 0; i < scales.length; i++ ) {
                setTermScale( i, scales[i] );
            }
        }
    }

    private void scaleToUnity( boolean animated ) {
        cancelScaleAnimation();
        if ( animated ) {
            for ( int i = 0; i < symbolNodes.length; i++ ) {
                animators[i] = new ScalingAnimator( symbolNodes[i], 1 );
                animators[i].start();
            }
        }
        else {
            for ( int i = 0; i < symbolNodes.length; i++ ) {
                symbolNodes[i].setScale( 1.0 );
            }
        }
    }
    
    private void cancelScaleAnimation() {
        for ( int i = 0; i < animators.length; i++ ) {
            if ( animators[i] != null ) {
                animators[i].stop();
                animators[i] = null;
            }
        }
    }
    
    public int getNumberOfTerms() {
        return symbolNodes.length;
    }
    
    protected void scaleTermToConcentration( int index, double concentration ) {
        cancelScaleAnimation();  // if this method is called, cancel all animation of terms, the user is controlling scale
        double scale = ConcentrationScaleModel.getFontSize( concentration ) / SYMBOL_FONT.getSize();
        setTermScale( index, scale );
    }
    
    private void setTermScale( int index, double scale ) {
        scales[index] = scale;
        if ( scalingEnabled ) {
            symbolNodes[index].setScale( scale );
        }
    }
    
    protected void setBidirectional( boolean b ) {
        if ( b ) {
            arrow.setImage( AABSImages.ARROW_DOUBLE );
        }
        else {
            arrow.setImage( AABSImages.ARROW_SINGLE );
        }
        updateLayout();
    }
    
    /*
     * Changes the visibility of term 1.
     * Term 1 is typically H2O, which is not shown in the reaction equation for strong bases.
     */
    protected void setTerm1Visible( boolean visible ) {
        symbolNodes[1].setVisible( visible );
        structureNodes[1].setVisible( visible );
        plusLHS.setVisible( visible );
        updateLayout();
    }
    
    /*
     * Changes a term (symbol and Lewis Structure diagram).
     */
    protected void setTerm( int index, String text, Color color, Image structureImage ) {
        // remove old symbol & structure
        removeChild( symbolNodes[index] );
        removeChild( structureNodes[index] );
        // add new symbol & structure
        symbolNodes[index] = new CenteredSymbolNode( text, SYMBOL_FONT, color );
        addChild( symbolNodes[index] );
        structureNodes[index] = new StructureNode( structureImage );
        if ( structuresVisible && structuresEnabled ) {
            addChild( structureNodes[index] );
        }
        updateLayout();
    }
    
    /**
     * Override to workaround Piccolo problem.
     */
    @Override
    public PNode removeChild( PNode child ) {
        PNode nodeRemoved = null;
        if ( indexOfChild( child ) != -1 ) {
            nodeRemoved = super.removeChild( child );
        }
        return nodeRemoved;
    }
    
    protected void setTermColor( int index, Color color ) {
        symbolNodes[index].setSymbolColor( color );
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
            for ( int i = 0; i < symbolNodes.length; i++ ) {
                setStructureVisible( i, visible );
            }
        }
    }
    
    /*
     * Changes the visibility of one Lewis structure diagram.
     * Uses addChild/removeChild so that full bounds won't include an invisible structure.
     */
    private void setStructureVisible( int index, boolean visible ) {
        if ( visible ) {
            addChild( structureNodes[index] );
        }
        else {
            removeChild( structureNodes[index] );
        }
        updateLayout();
    }
    
    /*
     * Sets the position of all nodes.
     */
    private void updateLayout() {
        
        // do the layout at unity scale
        scaleToUnity( false /* animate */ );
        
        final double maxSymbolHeight = getMaxSymbolHeight();
        final double structureYOffset = ( maxSymbolHeight / 2 ) + Y_SPACING;
        double xOffset = 0;
        double yOffset = 0;
        int termIndex = 0;
        
        // term 0
        xOffset = layoutTerm( termIndex++, xOffset, structureYOffset );
        
        // term 1 is optional
        if ( symbolNodes[termIndex].getVisible() ) {
            
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
        if ( scalingEnabled ) {
            for ( int i = 0; i < scales.length; i++ ) {
                setTermScale( i, scales[i] );
            }
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
        // symbol
        CenteredSymbolNode symbolNode = symbolNodes[index];
        xOffset = xStart;
        yOffset = -symbolNode.getCapHeight() / 2;
        symbolNode.setOffset( xOffset, yOffset );
        // structure
        StructureNode structureNode = structureNodes[index];
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
        for ( int i = 0; i < symbolNodes.length; i++ ) {
            PNode n = symbolNodes[i];
            if ( n != null ) {
                maxHeight = Math.max( maxHeight, n.getFullBoundsReference().getHeight() );
            }
        }
        return maxHeight;
    }
    
    /*
     * A symbol that scales about its geometric center.
     */
    private static class CenteredSymbolNode extends ChemicalSymbolNode {

        public CenteredSymbolNode( String html, Font font, Color color ) {
            super( html, font, color );
        }
        
        /*
         * Scales about the node's center.
         */
        @Override
        public void setScale( double scale ) {
            PBounds boundsBefore = getFullBounds();
            super.setScale( scale );
            PBounds boundsAfter = getFullBounds();
            double xOffset = getXOffset() - ( ( boundsAfter.getWidth() - boundsBefore.getWidth() ) / 2 );
            double yOffset = getYOffset() - ( ( boundsAfter.getHeight() - boundsBefore.getHeight() ) / 2 );
            setOffset( xOffset, yOffset );
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
