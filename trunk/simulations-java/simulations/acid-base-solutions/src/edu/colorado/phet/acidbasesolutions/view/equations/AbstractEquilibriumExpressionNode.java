package edu.colorado.phet.acidbasesolutions.view.equations;

import java.awt.*;
import java.awt.geom.Line2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FormattedNumberNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for all acid/base/water equilibrium expressions.
 * Equilibrium expressions are composed of at most 3 terms, and have the form:
 * <code>
 * K = [term1][term2] / [term3] = value
 * </code>
 * K is Ka, Kb, or Kw for acid, base, and water respectively.
 * For strong acids and bases, value is displayed as "Large".
 * For water, the expression has no denominator.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractEquilibriumExpressionNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    //TODO localize
    private static final String LARGE = "Large";
    
    private static final double X_SPACING = 10;
    private static final double Y_SPACING = 5;
    
    private static final Font K_FONT = new PhetFont( Font.BOLD, 24 );
    
    private static final Font SYMBOL_FONT = new PhetFont( Font.BOLD, 24 );
    
    private static final Font VALUE_FONT = new PhetFont( Font.PLAIN, 24 );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final TimesTenNumberFormat VALUE_FORMAT = new TimesTenNumberFormat( "0.00" );
    
    private static final Font EQUALS_FONT = new PhetFont( Font.BOLD, 22 );
    private static final Color EQUALS_COLOR = Color.BLACK;
    
    private static final Font BRACKET_FONT = new PhetFont( Font.BOLD, 22 );
    private static final Color BRACKET_COLOR = Color.BLACK;
    private static final double BRACKET_X_MARGIN = 10;
    
    private static final Color DIVIDED_COLOR = Color.BLACK;
    private static final Stroke DIVIDED_STROKE = new BasicStroke( 2f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final KNode kNode;
    private final TermNode leftNumeratorNode, rightNumeratorNode, denominatorNode;
    private final EqualsNode leftEqualsNode, rightEqualsNode;
    private final DividedNode dividedNode;
    private final ValueNode valueNode;
    private final LargeValueNode largeValueNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractEquilibriumExpressionNode() {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        kNode = new KNode( ABSSymbols.Ka );
        addChild( kNode );
        leftNumeratorNode = new TermNode( "?" );
        addChild( leftNumeratorNode );
        rightNumeratorNode = new TermNode( "?" );
        addChild( rightNumeratorNode );
        denominatorNode = new TermNode( "?" );
        addChild( denominatorNode );
        leftEqualsNode = new EqualsNode();
        addChild( leftEqualsNode );
        rightEqualsNode = new EqualsNode();
        addChild( rightEqualsNode );
        dividedNode = new DividedNode( 1 );
        addChild( dividedNode );
        valueNode = new ValueNode();
        addChild( valueNode );
        largeValueNode = new LargeValueNode();
        addChild( largeValueNode );
        
        // default state
        setLargeValueVisible( false );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Sets the scale for the left numerator.
     * @param scale
     */
    public void setLeftNumeratorScale( double scale ) {
        setScaleAboutCenter( leftNumeratorNode, scale );
    }
    
    /**
     * Sets the scale of the right numerator.
     * @param scale
     */
    public void setRightNumeratorScale( double scale ) {
        setScaleAboutCenter( rightNumeratorNode, scale );
    }
    
    /**
     * Sets the scale for the denominator.
     * @param scale
     */
    public void setDenominatorScale( double scale ) {
        setScaleAboutCenter( denominatorNode, scale );
    }
    
    /**
     * Sets the scale for K.
     * @param scale
     */
    public void setKScale( double scale ) {
        setScaleAboutCenter( kNode, scale );
    }
    
    /**
     * Sets all scalable nodes to have the same scale.
     */
    public void setScaleAll( double scale ) {
        setKScale( scale );
        setLeftNumeratorScale( scale );
        setRightNumeratorScale( scale );
        setDenominatorScale( scale );
    }
    
    /**
     * Sets the K value.
     */
    public void setKValue( double value ) {
        valueNode.setValue( value );
        updateLayout();
    }
    
    /*
     * Sets the visibility of the value "Large".
     */
    protected void setLargeValueVisible( boolean b ) {
        largeValueNode.setVisible( b );
        valueNode.setVisible( !b );
    }
    
    //----------------------------------------------------------------------------
    // Non-public interface
    //----------------------------------------------------------------------------
    
    protected void setLeftNumeratorProperties( String text, Color color ) {
        setTermProperties( leftNumeratorNode, text, color );
    }
    
    protected void setRightNumeratorProperties( String text, Color color ) {
        setTermProperties( rightNumeratorNode, text, color );
    }
    
    protected void setDenominatorProperties( String text, Color color ) {
        setTermProperties( denominatorNode, text, color );
    }
    
    protected void setTermProperties( TermNode node, String text, Color fill ) {
        node.setTermProperties( text, fill );
        updateLayout();
    }
    
    /*
     * Sets the visibility of the denominator, and the line that 
     * separates the numerator and denominator.
     */
    protected void setDenominatorVisible( boolean visible ) {
        denominatorNode.setVisible( visible );
        dividedNode.setVisible( visible );
        updateLayout();
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
     * Set the label used for K.
     */
    protected void setKLabel( String text ) {
        kNode.setHTML( text );
        updateLayout();
    }
    
    /*
     * Updates the layout based on unity scale.
     */
    private void updateLayout() {
        // save scale values
        final double kScale = kNode.getScale();
        final double leftNumeratorScale = leftNumeratorNode.getScale();
        final double rightNumeratorScale = rightNumeratorNode.getScale();
        final double denominatorScale = denominatorNode.getScale();
        // set all nodes to unity scale
        kNode.setScale( 1 );
        leftNumeratorNode.setScale( 1 );
        rightNumeratorNode.setScale( 1 );
        denominatorNode.setScale( 1 );
        // offsets
        double xOffset, yOffset;
        // K
        xOffset = getOriginXOffset( kNode );
        yOffset = getOriginYOffset( kNode );
        kNode.setOffset( xOffset, yOffset );
        // left equals
        xOffset = kNode.getFullBoundsReference().getMaxX() + X_SPACING - getOriginXOffset( leftEqualsNode );
        yOffset = kNode.getFullBoundsReference().getCenterY() - ( leftEqualsNode.getFullBoundsReference().getHeight() / 2 ) - getOriginYOffset( leftEqualsNode );
        leftEqualsNode.setOffset( xOffset, yOffset );
        // terms
        if ( denominatorNode.getVisible() ) {
            // left numerator
            xOffset = leftEqualsNode.getFullBoundsReference().getMaxX() + X_SPACING - getOriginXOffset( leftNumeratorNode );
            yOffset = leftEqualsNode.getFullBoundsReference().getCenterY() - leftNumeratorNode.getFullBoundsReference().getHeight() - Y_SPACING - getOriginYOffset( leftNumeratorNode );
            leftNumeratorNode.setOffset( xOffset, yOffset );
            // right numerator
            xOffset = leftNumeratorNode.getFullBoundsReference().getMaxX() + X_SPACING - getOriginXOffset( rightNumeratorNode );
            yOffset = leftEqualsNode.getFullBoundsReference().getCenterY() - rightNumeratorNode.getFullBoundsReference().getHeight() - Y_SPACING - getOriginYOffset( rightNumeratorNode );
            rightNumeratorNode.setOffset( xOffset, yOffset );
            // divided line
            double length = rightNumeratorNode.getFullBoundsReference().getMaxX() - leftNumeratorNode.getFullBoundsReference().getX();
            dividedNode.setLength( length );
            xOffset = leftNumeratorNode.getXOffset() - getOriginXOffset( dividedNode );
            yOffset = leftEqualsNode.getFullBoundsReference().getCenterY() - ( dividedNode.getFullBoundsReference().getHeight() / 2 ) - getOriginYOffset( dividedNode );
            dividedNode.setOffset( xOffset, yOffset );
            // denominator
            xOffset = dividedNode.getFullBoundsReference().getCenterX() - ( denominatorNode.getFullBoundsReference().getWidth() / 2 ) - getOriginXOffset( denominatorNode );
            yOffset = dividedNode.getFullBoundsReference().getMaxY() + Y_SPACING - getOriginYOffset( denominatorNode );
            denominatorNode.setOffset( xOffset, yOffset );
        }
        else {
            // left numerator
            xOffset = leftEqualsNode.getFullBoundsReference().getMaxX() + X_SPACING - getOriginXOffset( leftNumeratorNode );
            yOffset = leftEqualsNode.getFullBoundsReference().getCenterY() - ( leftNumeratorNode.getFullBoundsReference().getHeight() / 2 ) - getOriginYOffset( leftNumeratorNode );
            leftNumeratorNode.setOffset( xOffset, yOffset );
            // right numerator
            xOffset = leftNumeratorNode.getFullBoundsReference().getMaxX() + X_SPACING - getOriginXOffset( rightNumeratorNode );
            yOffset = leftEqualsNode.getFullBoundsReference().getCenterY() - ( rightNumeratorNode.getFullBoundsReference().getHeight() / 2 ) - getOriginYOffset( rightNumeratorNode );
            rightNumeratorNode.setOffset( xOffset, yOffset );
        }
        // right equals
        xOffset = rightNumeratorNode.getFullBoundsReference().getMaxX() + X_SPACING - getOriginXOffset( rightEqualsNode );
        yOffset = leftEqualsNode.getYOffset() - getOriginYOffset( rightEqualsNode );
        rightEqualsNode.setOffset( xOffset, yOffset );
        // value
        xOffset = rightEqualsNode.getFullBoundsReference().getMaxX() + X_SPACING - getOriginXOffset( valueNode );
        yOffset = rightEqualsNode.getFullBoundsReference().getCenterY() - ( valueNode.getFullBoundsReference().getHeight() / 2 ) - getOriginYOffset( valueNode );
        valueNode.setOffset( xOffset, yOffset );
        // large value
        xOffset = rightEqualsNode.getFullBoundsReference().getMaxX() + X_SPACING - getOriginXOffset( largeValueNode );
        yOffset = rightEqualsNode.getFullBoundsReference().getCenterY() - ( largeValueNode.getFullBoundsReference().getHeight() / 2 ) - getOriginYOffset( largeValueNode );
        largeValueNode.setOffset( xOffset, yOffset );
        // restore scales
        setKScale( kScale );
        setLeftNumeratorScale( leftNumeratorScale );
        setRightNumeratorScale( rightNumeratorScale );
        setDenominatorScale( denominatorScale );
    }
    
    /*
     * Difference in X between a node's X offset and the min X coordinate of its bounds.
     */
    private static double getOriginXOffset( PNode node ) {
        return node.getFullBoundsReference().getX() - node.getXOffset();
    }
    
    /*
     * Difference in Y between a node's Y offset and the min Y coordinate of its bounds.
     */
    private static double getOriginYOffset( PNode node ) {
        return node.getFullBoundsReference().getY() - node.getYOffset();
    }
    
    /*
     * Symbol for K.
     */
    private static class KNode extends HTMLNode {
        
        public KNode( String text ) {
            super( HTMLUtils.toHTMLString( text ) );
            setFont( K_FONT );
            setHTMLColor( ABSConstants.K_COLOR );
        }
        
        public void setHTML( String text ) {
            super.setHTML( HTMLUtils.toHTMLString( text ) );
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
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
     * A term in the expression denoting concentration.
     * This is a symbol surrounded by square brackets.
     */
    private static class TermNode extends PComposite {
        
        private final SymbolNode symbolNode;
        private final BracketNode leftBracketNode, rightBracketNode;
        
        public TermNode( String text ) {
            super();
            symbolNode= new SymbolNode( text );
            addChild( symbolNode );
            leftBracketNode = new LeftBracketNode();
            addChild( leftBracketNode );
            rightBracketNode = new RightBracketNode();
            addChild( rightBracketNode );
        }
        
        public void setTermProperties( String text, Color color ) {
            symbolNode.setHTML( HTMLUtils.toHTMLString( text ) );
            symbolNode.setHTMLColor( color );
            updateLayout();
        }
        
        private void updateLayout() {
            double xOffset, yOffset;
            // left bracket
            xOffset = 0;
            yOffset = 0;
            leftBracketNode.setOffset( xOffset, yOffset );
            // symbol
            xOffset = leftBracketNode.getFullBoundsReference().getMaxX() + BRACKET_X_MARGIN;
            yOffset = leftBracketNode.getFullBoundsReference().getCenterY() - ( symbolNode.getFullBoundsReference().getHeight() / 2 );
            symbolNode.setOffset( xOffset, yOffset );
            // right bracket
            xOffset = symbolNode.getFullBoundsReference().getMaxX() + BRACKET_X_MARGIN;
            yOffset = leftBracketNode.getYOffset();
            rightBracketNode.setOffset( xOffset, yOffset );
        }
    }
    
    /*
     * Value displayed in the format N x 10^M 
     */
    private static class ValueNode extends FormattedNumberNode {

        private static final double DEFAULT_VALUE = 0;

        public ValueNode() {
            super( VALUE_FORMAT, DEFAULT_VALUE, VALUE_FONT, VALUE_COLOR );
        }
    }
    
    /*
     * Value that displays as "Large".
     */
    private static class LargeValueNode extends PText {
        public LargeValueNode() {
            super( LARGE );
            setFont( VALUE_FONT );
            setTextPaint( VALUE_COLOR );
        }
    }
    
    /*
     * Equals signs between terms.
     */
    private static class EqualsNode extends PText {
        public EqualsNode() {
            super( "=" );
            setFont( EQUALS_FONT );
            setTextPaint( EQUALS_COLOR );
        }
    }
    
    /*
     * Base class for brackets used to denote concentration.
     */
    private abstract static class BracketNode extends PText {
        public BracketNode( String text ) {
            super( text );
            setFont( BRACKET_FONT );
            setTextPaint( BRACKET_COLOR );
        }
    }
    
    /*
     * Left bracket that denotes concentration.
     */
    private static class LeftBracketNode extends BracketNode {
        public LeftBracketNode() {
            super( "[" );
        }
    }

    /*
     * Right bracket that denotes concentration.
     */
    private static class RightBracketNode extends BracketNode {
        public RightBracketNode() {
            super( "]" );
        }
    }
    
    /*
     * The horizontal line that separate numerator and denominator.
     */
    private class DividedNode extends PPath {
        public DividedNode( double length ) {
            super();
            setStroke( DIVIDED_STROKE );
            setStrokePaint( DIVIDED_COLOR );
            setLength( length );
        }
        
        public void setLength( double length ) {
            setPathTo( new Line2D.Double( 0, 0, length, 0 ) );
        }
    }
    
    /**
     * Water equilibrium expression: Kw = [H3O+][OH-] = 1.0 x 10^-14
     */
    public static class WaterEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
        
        public WaterEquilibriumExpressionNode() {
            super();
            setKLabel( ABSSymbols.Kw );
            setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR );
            setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
            setDenominatorVisible( false );
            setKValue( ABSConstants.Kw );
        }
    }
    
    /*
     * Base class for acid equilibrium expressions.
     */
    public static class AbstractAcidEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
        
        public AbstractAcidEquilibriumExpressionNode() {
            this( ABSSymbols.A_MINUS, ABSSymbols.HA );
        }
        
        public AbstractAcidEquilibriumExpressionNode( String rightNumerator, String denominator ) {
            super();
            setKLabel( ABSSymbols.Ka );
            setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR );
            setRightNumeratorProperties( rightNumerator, ABSConstants.A_COLOR );
            setDenominatorProperties( denominator, ABSConstants.HA_COLOR );
            setKValue( 0 );
        }
    }
    
    /**
     * Weak acid equilibrium expression: Ka = [H3O+][A-] / [HA] = value
     */
    public static class WeakAcidEquilibriumExpressionNode extends AbstractAcidEquilibriumExpressionNode {
        public WeakAcidEquilibriumExpressionNode() {
            super();
        }
        
        public WeakAcidEquilibriumExpressionNode( String rightNumerator, String denominator ) {
            super( rightNumerator, denominator );
        }
    }
    
    /**
     * Strong acid equilibrium expression: Ka = [H3O+][A-] / [HA] = Large
     */
    public static class StrongAcidEquilibriumExpressionNode extends AbstractAcidEquilibriumExpressionNode {
        public StrongAcidEquilibriumExpressionNode() {
            super();
            setLargeValueVisible( true );
        }
        public StrongAcidEquilibriumExpressionNode( String rightNumerator, String denominator ) {
            super( rightNumerator, denominator );
            setLargeValueVisible( true );
        }
    }
    
    /*
     * Base class for base equilibrium expressions.
     */
    public static class AbstractBaseEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
        public AbstractBaseEquilibriumExpressionNode() {
            super();
            setKLabel( ABSSymbols.Kb );
            setKValue( 0 );
        }
    }
    
    /**
     * Weak base equilibrium expression: Kb = [BH+][OH-] / [B] = value
     */
    public static class WeakBaseEquilibriumExpressionNode extends AbstractBaseEquilibriumExpressionNode {
        
        public WeakBaseEquilibriumExpressionNode() {
            this( ABSSymbols.BH_PLUS, ABSSymbols.B );
        }
        
        public WeakBaseEquilibriumExpressionNode( String leftNumerator, String denominator ) {
            super();
            setLeftNumeratorProperties( leftNumerator, ABSConstants.BH_COLOR );
            setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
            setDenominatorProperties( denominator, ABSConstants.B_COLOR );
        }
    }
    
    /**
     * Strong base equilibrium expression: Kb = [M+][OH-] / [MOH] = Large
     */
    public static class StrongBaseEquilibriumExpressionNode extends AbstractBaseEquilibriumExpressionNode {
        
        public StrongBaseEquilibriumExpressionNode() {
            this( ABSSymbols.M_PLUS, ABSSymbols.MOH );
        }
        
        public StrongBaseEquilibriumExpressionNode( String leftNumerator, String denominator ) {
            super();
            setLeftNumeratorProperties( leftNumerator, ABSConstants.M_COLOR );
            setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
            setDenominatorProperties( denominator, ABSConstants.MOH_COLOR );
            setLargeValueVisible( true );
        }
    }
    
    
    /* tests */
    public static void main( String[] args ) {
        
        Dimension canvasSize = new Dimension( 1024, 768 );
        PCanvas canvas = new PCanvas();
        canvas.setPreferredSize( canvasSize );
        
        // one instance of each type
        PNode[] expressions = {
                // water
                new WaterEquilibriumExpressionNode(),
                // generics
                new WeakAcidEquilibriumExpressionNode(),
                new StrongAcidEquilibriumExpressionNode(),
                new WeakBaseEquilibriumExpressionNode(),
                new StrongBaseEquilibriumExpressionNode(),
                // specifics
                new WeakAcidEquilibriumExpressionNode( "ClO<sub>2</sub><sup>-</sup>", "HClO<sub>2</sub>" ),
                new StrongAcidEquilibriumExpressionNode( "Cl<sup>-</sup>", "HCl" ),
                new WeakBaseEquilibriumExpressionNode( "NH<sub>4</sub><sup>+</sup>", "NH<sub>3</sub>" ),
                new StrongBaseEquilibriumExpressionNode( "Na<sup>+</sup>", "NaOH" ),
        };
        
        // layout in a left-justified column
        double xOffset = 50;
        double yOffset = 50;
        for ( int i = 0; i < expressions.length; i++ ) {
            canvas.getLayer().addChild( expressions[i] );
            expressions[i].setOffset( xOffset, yOffset - getOriginYOffset( expressions[i] ) );
            yOffset = expressions[i].getFullBoundsReference().getMaxY() + 10;
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
