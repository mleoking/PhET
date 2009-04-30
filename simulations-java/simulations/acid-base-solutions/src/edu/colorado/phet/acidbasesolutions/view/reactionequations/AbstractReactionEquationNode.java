package edu.colorado.phet.acidbasesolutions.view.reactionequations;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.view.OutlinedHTMLNode;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

public abstract class AbstractReactionEquationNode extends PComposite {
    
    private static final double X_SPACING = 20;
    private static final double Y_SPACING = 10;
    
    private static final Font SYMBOL_FONT = new PhetFont( Font.PLAIN, 24 );
    private static final Font PLUS_FONT = new PhetFont( Font.PLAIN, 22 );
    private static final Color PLUS_COLOR = Color.BLACK;
    
    private final Term[] terms;
    private final PlusNode plusLHS, plusRHS;
    private PImage arrow;
    
    public AbstractReactionEquationNode() {
        super();
        
        terms = new Term[4];
        for ( int i = 0; i < terms.length; i++ ) {
            terms[i] = new Term( new SymbolNode( "S" + i ) );
            addTerm( terms[i] );
        }
        
        plusLHS = new PlusNode();
        addChild( plusLHS );
        
        plusRHS = new PlusNode();
        addChild( plusRHS );
        
        arrow = new PImage( ABSImages.ARROW_SINGLE );
        addChild( arrow );
        
        updateLayout();
    }
    
    protected void setArrow( BufferedImage arrowImage ) {
        removeChild( arrow );
        arrow = new PImage( arrowImage );
        addChild( arrow );
        updateLayout();
    }
    
    protected void setTerm0Visible( boolean visible ) {
        setTermVisible( 0, visible );
        plusLHS.setVisible( visible );
    }
    
    private void setTermVisible( int index, boolean visible ) {
        terms[index].setVisible( visible );
        updateLayout();
    }
    
    public void setTerm( int index, String text, Color fill, Color outline ) {
        setTerm( index, text, fill, outline, null );
    }
    
    public void setTerm( int index, String text, Color fill, Color outline, BufferedImage structureImage ) {
        Term term = terms[index];
        // symbol
        SymbolNode symbolNode = term.getSymbolNode();
        symbolNode.setHTML( text );
        symbolNode.setFillColor( fill );
        symbolNode.setOutlineColor( outline );
        // structure
        StructureNode structureNode = term.getStructureNode();
        structureNode.setImage( structureImage );
        // layout
        updateLayout();
    }
    
    private void addTerm( Term term ) {
        super.addChild( term.getSymbolNode() );
        super.addChild( term.getStructureNode() );
    }
    
    private void updateLayout() {
        
        final double maxSymbolHeight = getMaxSymbolHeight();
        final double structureYOffset = ( maxSymbolHeight / 2 ) + Y_SPACING;
        double xOffset = 0;
        double yOffset = 0;
        int termIndex = 0;
        
        // term 0 is optional
        if ( terms[termIndex].isVisible() ) {
            
            // term 0
            xOffset = layoutTerm( termIndex++, xOffset, structureYOffset );
            
            // plus sign
            yOffset = -plusLHS.getFullBoundsReference().getHeight() / 2;
            plusLHS.setOffset( xOffset, yOffset );
            xOffset = plusLHS.getFullBoundsReference().getMaxX() + X_SPACING;
        }
        else {
            termIndex++;
        }
        
        // term 1
        xOffset = layoutTerm( termIndex++, xOffset, structureYOffset );
        
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
    
    private static class SymbolNode extends OutlinedHTMLNode {
        
        public SymbolNode( String text ) {
            this( HTMLUtils.toHTMLString( text ), Color.BLACK, Color.RED );
        }
        
        public SymbolNode( String text, Color fill, Color outline ) {
            super( HTMLUtils.toHTMLString( text ) );
            setFont( SYMBOL_FONT );
            setFillColor( fill );
            setOutlineColor( outline );
        }
        
        public void setHTML( String text ) {
            super.setHTML( HTMLUtils.toHTMLString( text ) );
        }
    }
    
    private static class StructureNode extends PImage {
        
        public StructureNode() {
            super();
        }
        
        public StructureNode( Image image ) {
            super( image );
        }
    }
    
    private static class PlusNode extends PText {
        public PlusNode() {
            super( "+" );
            setFont( PLUS_FONT );
            setTextPaint( PLUS_COLOR );
        }
    }
    
    public static class WaterReactionEquationNode extends AbstractReactionEquationNode {
        public WaterReactionEquationNode() {
            setTerm( 0, ABSSymbols.H2O, ABSConstants.H2O_COLOR, Color.BLACK, ABSImages.H2O_STRUCTURE );
            setTerm( 1, ABSSymbols.H2O, ABSConstants.H2O_COLOR, Color.BLACK, ABSImages.H2O_STRUCTURE );
            setTerm( 2, ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR, Color.BLACK, ABSImages.H3O_PLUS_STRUCTURE );
            setTerm( 3, ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR, Color.BLACK, ABSImages.OH_MINUS_STRUCTURE );
            setArrow( ABSImages.ARROW_DOUBLE );
        }
    }
    
    protected static abstract class AbstractAcidReactionEquationNode extends AbstractReactionEquationNode {
        public AbstractAcidReactionEquationNode() {
            setTerm( 0, ABSSymbols.HA, ABSConstants.HA_COLOR, Color.BLACK, ABSImages.HA_STRUCTURE );
            setTerm( 1, ABSSymbols.H2O, ABSConstants.H2O_COLOR, Color.BLACK, ABSImages.H2O_STRUCTURE );
            setTerm( 2, ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR, Color.BLACK, ABSImages.H3O_PLUS_STRUCTURE );
            setTerm( 3, ABSSymbols.A_MINUS, ABSConstants.A_COLOR, Color.BLACK, ABSImages.A_MINUS_STRUCTURE );
        }
    }
    
    public static class WeakAcidReactionEquationNode extends AbstractAcidReactionEquationNode {
        public WeakAcidReactionEquationNode() {
            setArrow( ABSImages.ARROW_DOUBLE );
        }
    }
    
    public static class StrongAcidReactionEquationNode extends AbstractAcidReactionEquationNode {
        public StrongAcidReactionEquationNode() {
            setArrow( ABSImages.ARROW_SINGLE );
        }
    }
    
    public static class WeakBaseReactionEquationNode extends AbstractReactionEquationNode {
        public WeakBaseReactionEquationNode() {
            setTerm( 0, ABSSymbols.B, ABSConstants.B_COLOR, Color.BLACK, ABSImages.B_STRUCTURE );
            setTerm( 1, ABSSymbols.H2O, ABSConstants.H2O_COLOR, Color.BLACK, ABSImages.H2O_STRUCTURE );
            setTerm( 2, ABSSymbols.BH_PLUS, ABSConstants.BH_COLOR, Color.BLACK, ABSImages.BH_PLUS_STRUCTURE );
            setTerm( 3, ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR, Color.BLACK, ABSImages.OH_MINUS_STRUCTURE );
            setArrow( ABSImages.ARROW_DOUBLE );
        }
    }
    
    public static class StrongBaseReactionEquationNode extends AbstractReactionEquationNode {
        public StrongBaseReactionEquationNode() {
            setTerm0Visible( false );
            setTerm( 1, ABSSymbols.MOH, ABSConstants.MOH_COLOR, Color.BLACK, ABSImages.MOH_STRUCTURE );
            setTerm( 2, ABSSymbols.M_PLUS, ABSConstants.M_COLOR, Color.BLACK, ABSImages.M_PLUS_STRUCTURE );
            setTerm( 3, ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR, Color.BLACK, ABSImages.OH_MINUS_STRUCTURE );
            setArrow( ABSImages.ARROW_SINGLE );
        }
    }

    public static void main( String[] args ) {
        
        Dimension canvasSize = new Dimension( 1024, 768 );
        PCanvas canvas = new PCanvas();
        canvas.setPreferredSize( canvasSize );
        
        // one instance of each type
        PNode[] equations = {
                new WaterReactionEquationNode(),
                new WeakAcidReactionEquationNode(),
                new StrongAcidReactionEquationNode(),
                new WeakBaseReactionEquationNode(),
                new StrongBaseReactionEquationNode()
        };
        
        // layout in a left-justified column
        double xOffset = 50;
        double yOffset = 50;
        for ( int i = 0; i < equations.length; i++ ) {
            canvas.getLayer().addChild( equations[i] );
            equations[i].setOffset( xOffset, yOffset );
            yOffset = equations[i].getFullBoundsReference().getMaxY() + 40;
        }
        
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( canvas, BorderLayout.CENTER );
        
        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
