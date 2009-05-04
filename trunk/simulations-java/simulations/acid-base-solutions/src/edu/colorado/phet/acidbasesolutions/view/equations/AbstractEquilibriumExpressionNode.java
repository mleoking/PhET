package edu.colorado.phet.acidbasesolutions.view.equations;

import java.awt.*;
import java.awt.geom.Line2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.view.OutlinedHTMLNode;
import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FormattedNumberNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;


public abstract class AbstractEquilibriumExpressionNode extends PComposite {
    
    //TODO localize
    private static final String LARGE = "Large";
    
    private static final double X_SPACING = 20;
    private static final double Y_SPACING = 10;
    
    private static final Font K_FONT = new PhetFont( Font.PLAIN, 24 );
    private static final Color K_FILL_COLOR = Color.GRAY;
    private static final Color K_OUTLINE_COLOR = Color.BLACK;
    
    private static final Font TERM_FONT = new PhetFont( Font.PLAIN, 24 );
    
    private static final Font VALUE_FONT = new PhetFont( Font.PLAIN, 24 );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final TimesTenNumberFormat VALUE_FORMAT = new TimesTenNumberFormat( "0.00" );
    
    private static final Font EQUALS_FONT = new PhetFont( Font.PLAIN, 22 );
    private static final Color EQUALS_COLOR = Color.BLACK;
    
    private static final Font BRACKET_FONT = new PhetFont( Font.PLAIN, 22 );
    private static final Color BRACKET_COLOR = Color.BLACK;
    private static final double BRACKET_X_MARGIN = 10;
    
    private static final Color DIVIDED_COLOR = Color.BLACK;
    private static final Stroke DIVIDED_STROKE = new BasicStroke( 2f );
    
    private final KNode kNode;
    private final TermNode leftNumeratorNode, rightNumeratorNode, denominatorNode;
    private final EqualsNode leftEqualsNode, rightEqualsNode;
    private final DividedNode dividedNode;
    private final ValueNode valueNode;
    private final LargeValueNode largeValueNode;
    
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
        setLargeValue( false );
    }
    
    protected void setLargeValue( boolean b ) {
        largeValueNode.setVisible( b );
        valueNode.setVisible( !b );
    }
    
    public void setValue( double value ) {
        valueNode.setValue( value );
    }
    
    protected void setLeftNumeratorProperties( String text, Color fill, Color outline ) {
        setTermProperties( leftNumeratorNode, text, fill, outline );
    }
    
    protected void setRightNumeratorProperties( String text, Color fill, Color outline ) {
        setTermProperties( rightNumeratorNode, text, fill, outline );
    }
    
    protected void setDenominatorProperties( String text, Color fill, Color outline ) {
        setTermProperties( denominatorNode, text, fill, outline );
    }
    
    protected void setTermProperties( TermNode node, String text, Color fill, Color outline ) {
        node.setTermProperties( text, fill, outline );
        updateLayout();
    }
    
    protected void setDenominatorVisible( boolean visible ) {
        denominatorNode.setVisible( visible );
        dividedNode.setVisible( visible );
        updateLayout();
    }
    
    protected void setLeftNumeratorScale( double scale ) {
        setTermScale( leftNumeratorNode, scale );
    }
    
    public void setRightNumeratorScale( double scale ) {
        setTermScale( rightNumeratorNode, scale );
    }
    
    public void setDenominatorScale( double scale ) {
        setTermScale( denominatorNode, scale );
    }
    
    // scale about the center
    private void setTermScale( TermNode node, double scale ) {
        PBounds boundsBefore = node.getFullBounds();
        node.setScale( scale );
        PBounds boundsAfter = node.getFullBounds();
        double xOffset = node.getXOffset() - ( ( boundsAfter.getWidth() - boundsBefore.getWidth() ) / 2 );
        double yOffset = node.getYOffset() - ( ( boundsAfter.getHeight() - boundsBefore.getHeight() ) / 2 );
        node.setOffset( xOffset, yOffset );
    }
    
    public void setKScale( double scale ) {
        kNode.setScale( scale );
    }
    
    public void setAllTermScale( double scale ) {
        setLeftNumeratorScale( scale );
        setRightNumeratorScale( scale );
        setDenominatorScale( scale );
    }
    
    protected void setKLabel( String text ) {
        kNode.setHTML( text );
        updateLayout();
    }
    
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
        double xOffset = 0;
        double yOffset = 0;
        // K
        kNode.setOffset( xOffset, yOffset );
        // left equals
        xOffset = kNode.getFullBoundsReference().getMaxX() + X_SPACING;
        yOffset = kNode.getFullBoundsReference().getCenterY() - ( leftEqualsNode.getFullBoundsReference().getHeight() / 2 );
        leftEqualsNode.setOffset( xOffset, yOffset );
        // terms
        if ( denominatorNode.getVisible() ) {
            // left numerator
            xOffset = leftEqualsNode.getFullBoundsReference().getMaxX() + X_SPACING;
            yOffset = leftEqualsNode.getFullBoundsReference().getCenterY() - leftNumeratorNode.getFullBoundsReference().getHeight() - Y_SPACING;
            leftNumeratorNode.setOffset( xOffset, yOffset );
            // right numerator
            xOffset = leftNumeratorNode.getFullBoundsReference().getMaxX() + X_SPACING;
            yOffset = leftEqualsNode.getFullBoundsReference().getCenterY() - rightNumeratorNode.getFullBoundsReference().getHeight() - Y_SPACING;
            rightNumeratorNode.setOffset( xOffset, yOffset );
            // divided line
            double length = rightNumeratorNode.getFullBoundsReference().getMaxX() - leftNumeratorNode.getFullBoundsReference().getX();
            dividedNode.setLength( length );
            xOffset = leftNumeratorNode.getXOffset();
            yOffset = leftEqualsNode.getFullBoundsReference().getCenterY() - ( dividedNode.getFullBoundsReference().getHeight() / 2 );
            dividedNode.setOffset( xOffset, yOffset );
            // denominator
            xOffset = dividedNode.getFullBoundsReference().getX() + ( ( dividedNode.getFullBoundsReference().getWidth() - denominatorNode.getFullBoundsReference().getWidth() ) / 2 );
            yOffset = dividedNode.getFullBoundsReference().getMaxY() + Y_SPACING;
            denominatorNode.setOffset( xOffset, yOffset );
        }
        else {
            // left numerator
            xOffset = leftEqualsNode.getFullBoundsReference().getMaxX() + X_SPACING;
            yOffset = leftEqualsNode.getFullBoundsReference().getCenterY() - ( leftNumeratorNode.getFullBoundsReference().getHeight() / 2 );
            leftNumeratorNode.setOffset( xOffset, yOffset );
            // right numerator
            xOffset = leftNumeratorNode.getFullBoundsReference().getMaxX() + X_SPACING;
            yOffset = leftEqualsNode.getFullBoundsReference().getCenterY() - ( rightNumeratorNode.getFullBoundsReference().getHeight() / 2 );
            rightNumeratorNode.setOffset( xOffset, yOffset );
        }
        // right equals
        xOffset = rightNumeratorNode.getFullBoundsReference().getMaxX() + X_SPACING;
        yOffset = leftEqualsNode.getYOffset();
        rightEqualsNode.setOffset( xOffset, yOffset );
        // value
        xOffset = rightEqualsNode.getFullBoundsReference().getMaxX() + X_SPACING;
        yOffset = rightEqualsNode.getFullBoundsReference().getCenterY() - ( valueNode.getFullBoundsReference().getHeight() / 2 );
        valueNode.setOffset( xOffset, yOffset );
        // large value
        xOffset = rightEqualsNode.getFullBoundsReference().getMaxX() + X_SPACING;
        yOffset = rightEqualsNode.getFullBoundsReference().getCenterY() - ( largeValueNode.getFullBoundsReference().getHeight() / 2 );
        largeValueNode.setOffset( xOffset, yOffset );
        // restore scales
        setKScale( kScale );
        setLeftNumeratorScale( leftNumeratorScale );
        setRightNumeratorScale( rightNumeratorScale );
        setDenominatorScale( denominatorScale );
    }
    
    private static class KNode extends OutlinedHTMLNode {
        
        public KNode( String text ) {
            super( HTMLUtils.toHTMLString( text ) );
            setFont( K_FONT );
            setFillColor( K_FILL_COLOR );
            setOutlineColor( K_OUTLINE_COLOR );
        }
        
        public void setHTML( String text ) {
            super.setHTML( HTMLUtils.toHTMLString( text ) );
        }
    }
    
    private static class TermNode extends PComposite {
        
        private final OutlinedHTMLNode symbolNode;
        private final BracketNode leftBracketNode, rightBracketNode;
        
        public TermNode( String text ) {
            this( HTMLUtils.toHTMLString( text ), Color.BLACK, Color.RED );
        }
        
        public TermNode( String text, Color fill, Color outline ) {
            super();
            symbolNode= new OutlinedHTMLNode( HTMLUtils.toHTMLString( text ), TERM_FONT, fill, outline );
            addChild( symbolNode );
            leftBracketNode = new LeftBracketNode();
            addChild( leftBracketNode );
            rightBracketNode = new RightBracketNode();
            addChild( rightBracketNode );
        }
        
        public void setTermProperties( String text, Color fill, Color outline ) {
            symbolNode.setHTML( HTMLUtils.toHTMLString( text ) );
            symbolNode.setFillColor( fill );
            symbolNode.setOutlineColor( outline );
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
     * Values displayed on the bars.
     */
    private static class ValueNode extends FormattedNumberNode {

        private static final double DEFAULT_VALUE = 0;

        public ValueNode() {
            super( VALUE_FORMAT, DEFAULT_VALUE, VALUE_FONT, VALUE_COLOR );
        }
    }
    
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
     * Brackets that surround terms.
     */
    private abstract static class BracketNode extends PText {
        public BracketNode( String text ) {
            super( text );
            setFont( BRACKET_FONT );
            setTextPaint( BRACKET_COLOR );
        }
    }
    
    private static class LeftBracketNode extends BracketNode {
        public LeftBracketNode() {
            super( "[" );
        }
    }

    private static class RightBracketNode extends BracketNode {
        public RightBracketNode() {
            super( "]" );
        }
    }
    
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
    
    public static class WaterEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
        
        public WaterEquilibriumExpressionNode() {
            super();
            setKLabel( ABSSymbols.Kw );
            setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR, ABSConstants.H3O_OUTLINE );
            setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR, ABSConstants.OH_OUTLINE );
            setDenominatorVisible( false );
            setValue( ABSConstants.Kw );
        }
    }
    
    public static class AbstractAcidEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
        
        public AbstractAcidEquilibriumExpressionNode() {
            this( ABSSymbols.A_MINUS, ABSSymbols.HA );
        }
        
        public AbstractAcidEquilibriumExpressionNode( String rightNumerator, String denominator ) {
            super();
            setKLabel( ABSSymbols.Ka );
            setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR, ABSConstants.H3O_OUTLINE );
            setRightNumeratorProperties( rightNumerator, ABSConstants.A_COLOR, ABSConstants.A_OUTLINE );
            setDenominatorProperties( denominator, ABSConstants.HA_COLOR, ABSConstants.HA_OUTLINE );
            setValue( 0 );
        }
    }
    
    public static class WeakAcidEquilibriumExpressionNode extends AbstractAcidEquilibriumExpressionNode {
        public WeakAcidEquilibriumExpressionNode() {
            super();
        }
        
        public WeakAcidEquilibriumExpressionNode( String rightNumerator, String denominator ) {
            super( rightNumerator, denominator );
        }
    }
    
    public static class StrongAcidEquilibriumExpressionNode extends AbstractAcidEquilibriumExpressionNode {
        public StrongAcidEquilibriumExpressionNode() {
            super();
            setLargeValue( true );
        }
        public StrongAcidEquilibriumExpressionNode( String rightNumerator, String denominator ) {
            super( rightNumerator, denominator );
            setLargeValue( true );
        }
    }
    
    public static class AbstractBaseEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
        public AbstractBaseEquilibriumExpressionNode() {
            super();
            setKLabel( ABSSymbols.Kb );
            setValue( 0 );
        }
    }
    
    public static class WeakBaseEquilibriumExpressionNode extends AbstractBaseEquilibriumExpressionNode {
        
        public WeakBaseEquilibriumExpressionNode() {
            this( ABSSymbols.BH_PLUS, ABSSymbols.B );
        }
        
        public WeakBaseEquilibriumExpressionNode( String leftNumerator, String denominator ) {
            super();
            setLeftNumeratorProperties( leftNumerator, ABSConstants.BH_COLOR, ABSConstants.BH_OUTLINE );
            setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR, ABSConstants.OH_OUTLINE );
            setDenominatorProperties( denominator, ABSConstants.B_COLOR, ABSConstants.B_OUTLINE );
        }
    }
    
    public static class StrongBaseEquilibriumExpressionNode extends AbstractBaseEquilibriumExpressionNode {
        
        public StrongBaseEquilibriumExpressionNode() {
            this( ABSSymbols.M_PLUS, ABSSymbols.MOH );
        }
        
        public StrongBaseEquilibriumExpressionNode( String leftNumerator, String denominator ) {
            super();
            setLeftNumeratorProperties( leftNumerator, ABSConstants.M_COLOR, ABSConstants.M_OUTLINE );
            setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR, ABSConstants.OH_OUTLINE );
            setDenominatorProperties( denominator, ABSConstants.MOH_COLOR, ABSConstants.MOH_OUTLINE );
            setLargeValue( true );
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
            expressions[i].setOffset( xOffset, yOffset );
            yOffset = expressions[i].getFullBoundsReference().getMaxY() + 40;
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
