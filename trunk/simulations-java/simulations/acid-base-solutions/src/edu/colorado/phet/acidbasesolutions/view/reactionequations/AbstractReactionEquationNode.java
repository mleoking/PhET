package edu.colorado.phet.acidbasesolutions.view.reactionequations;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

//TODO make this class abstract
public class AbstractReactionEquationNode extends PComposite {
    
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
    }
    
    private void setTermVisible( int index, boolean visible ) {
        terms[index].setVisible( visible );
        updateLayout();
    }
    
    protected void setTerm0( String symbol, Color fill, Color outline ) {
        setTerm0( symbol, fill, outline, null );
    }
    
    protected void setTerm0( String symbol, Color fill, Color outline, BufferedImage structureImage ) {
        updateTerm( 0, symbol, fill, outline, structureImage );
    }
    
    protected void setTerm1( String symbol, Color fill, Color outline ) {
        setTerm1( symbol, fill, outline, null );
    }
    
    protected void setTerm1( String symbol, Color fill, Color outline, BufferedImage structureImage ) {
        updateTerm( 1, symbol, fill, outline, structureImage );
    }
    
    protected void setTerm2( String symbol, Color fill, Color outline ) {
        setTerm2( symbol, fill, outline, null );
    }
    
    protected void setTerm2( String symbol, Color fill, Color outline, BufferedImage structureImage ) {
        updateTerm( 2, symbol, fill, outline, structureImage );
    }
    
    protected void setTerm3( String symbol, Color fill, Color outline ) {
        setTerm3( symbol, fill, outline, null );
    }
    
    protected void setTerm3( String symbol, Color fill, Color outline, BufferedImage structureImage ) {
        updateTerm( 3, symbol, fill, outline, structureImage );
    }
    
    private void updateTerm( int index, String symbol, Color fill, Color outline, BufferedImage structureImage ) {
        Term term = terms[index];
        // symbol
        SymbolNode symbolNode = term.getSymbolNode();
        symbolNode.setText( symbol );
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
        
        // term 1
        xOffset = layoutTerm( termIndex++, xOffset, structureYOffset );
        
        // arrow
        yOffset = -arrow.getFullBoundsReference().getHeight() / 2;
        arrow.setOffset( xOffset, yOffset );
        xOffset = arrow.getFullBoundsReference().getMaxX() + X_SPACING;
        
        // term 2
        xOffset = layoutTerm( termIndex++, xOffset, structureYOffset );
        
        /* plus sign */
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
    
    //TODO handle outline, OutlineHTMLNode?
    private static class SymbolNode extends HTMLNode {
        
        public SymbolNode( String text ) {
            this( HTMLUtils.toHTMLString( text ), Color.BLACK, Color.RED );
        }
        
        public SymbolNode( String text, Color fill, Color outline ) {
            super( HTMLUtils.toHTMLString( text ) );
            setFont( SYMBOL_FONT );
            setHTMLColor( fill );
        }
        
        public void setText( String text ) {
            setHTML( HTMLUtils.toHTMLString( text ) );
        }
        
        public void setFillColor( Color color ) {
            setHTMLColor( color );
        }
        
        public void setOutlineColor( Color color ) {
            //TODO add support for this
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

    public static void main( String[] args ) {
        
        Dimension canvasSize = new Dimension( 800, 600 );
        PhetPCanvas canvas = new PhetPCanvas( canvasSize );
        canvas.setPreferredSize( canvasSize );
        canvas.setBackground( Color.LIGHT_GRAY );
        
        final AbstractReactionEquationNode node = new AbstractReactionEquationNode();
        node.setTerm0( ABSSymbols.H2O, Color.BLUE, Color.BLACK, ABSImages.H2O_STRUCTURE );
        node.setTerm1( ABSSymbols.H2O, Color.BLUE, Color.BLACK, ABSImages.H2O_STRUCTURE );
        node.setTerm2( ABSSymbols.H3O_PLUS, Color.BLUE, Color.BLACK, ABSImages.H3O_PLUS_STRUCTURE );
        node.setTerm3( ABSSymbols.OH_MINUS, Color.BLUE, Color.BLACK, ABSImages.OH_MINUS_STRUCTURE );
        canvas.getLayer().addChild( node );
        node.setOffset( 100, 100 );
        
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( canvas, BorderLayout.CENTER );
        
        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
}
}
