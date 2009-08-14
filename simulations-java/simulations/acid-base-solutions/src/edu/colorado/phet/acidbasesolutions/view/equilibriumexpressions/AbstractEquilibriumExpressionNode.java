package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import edu.colorado.phet.acidbasesolutions.ABSColors;
import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ConcentrationScaleModel;
import edu.colorado.phet.acidbasesolutions.util.PNodeUtils;
import edu.colorado.phet.acidbasesolutions.util.ScalingAnimator;
import edu.colorado.phet.acidbasesolutions.view.SymbolNode;
import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FormattedNumberNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for equilibrium expressions.
 * Equilibrium expressions are composed of at most 3 terms, and have the form:
 * <code>
 * K = [term1][term2] / [term3] = value
 * </code>
 * Value can be displayed as "Large".
 * The denominator can be hidden.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractEquilibriumExpressionNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int FONT_SIZE = 18;
    
    private static final double X_SPACING = 10;
    private static final double Y_SPACING = 5;
    
    private static final Font K_FONT = new PhetFont( Font.BOLD, FONT_SIZE );
    
    private static final Font SYMBOL_FONT = new PhetFont( Font.BOLD, FONT_SIZE );
    
    private static final Font VALUE_FONT = new PhetFont( Font.PLAIN, FONT_SIZE );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final TimesTenNumberFormat VALUE_FORMAT = new TimesTenNumberFormat( "0.00" );
    
    private static final Font EQUALS_FONT = new PhetFont( Font.BOLD, FONT_SIZE - 2 );
    private static final Color EQUALS_COLOR = Color.BLACK;
    
    private static final Font GREATER_THAN_FONT = new PhetFont( Font.BOLD, FONT_SIZE );
    private static final Color GREATER_THAN_COLOR = EQUALS_COLOR;
    
    private static final Font BRACKET_FONT = new PhetFont( Font.PLAIN, FONT_SIZE );
    private static final Color BRACKET_COLOR = Color.BLACK;
    private static final double BRACKET_X_MARGIN = 2;
    
    private static final Color DIVIDED_COLOR = Color.BLACK;
    private static final Stroke DIVIDED_STROKE = new BasicStroke( 2f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final KNode kNode;
    private TermNode leftNumeratorNode, rightNumeratorNode, denominatorNode;
    private final EqualsNode leftEqualsNode, rightEqualsNode;
    private final GreaterThanOneNode greaterThanOneNode;
    private final DividedNode dividedNode;
    private final ValueNode valueNode;
    private final LargeValueNode largeValueNode;
    private final boolean hasDenominator;
    private boolean scalingEnabled;
    private double leftNumeratorScale, rightNumeratorScale, denominatorScale;
    private final boolean kRepresentationVaries;
    private ScalingAnimator leftNumeratorAnimator, rightNumeratorAnimator, denominatorAnimator;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    protected AbstractEquilibriumExpressionNode( boolean hasDenominator, boolean kRepresentationVaries ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        this.kRepresentationVaries = kRepresentationVaries;
        
        kNode = new KNode( "?" );
        addChild( kNode );
        
        leftNumeratorNode = new TermNode( "ABC", Color.BLACK );
        addChild( leftNumeratorNode );
        rightNumeratorNode = new TermNode( "ABC", Color.BLACK );
        addChild( rightNumeratorNode );
        
        leftEqualsNode = new EqualsNode();
        addChild( leftEqualsNode );
        rightEqualsNode = new EqualsNode();
        addChild( rightEqualsNode );
        
        this.hasDenominator = hasDenominator;
        denominatorNode = new TermNode( "ABC", Color.BLACK );
        dividedNode = new DividedNode( 1 );
        if ( hasDenominator ) {
            addChild( denominatorNode );
            addChild( dividedNode );
        }
        
        valueNode = new ValueNode();
        addChild( valueNode );
        
        greaterThanOneNode = new GreaterThanOneNode();
        greaterThanOneNode.setVisible( false );
        addChild( greaterThanOneNode );
        
        largeValueNode = new LargeValueNode();
        largeValueNode.setVisible( false );
        addChild( largeValueNode );
        
        // default state
        scalingEnabled = false;
        leftNumeratorScale = rightNumeratorScale = denominatorScale = 1.0;
        
        updateKRepresentation();
        updateLayout();
    }
    
    /**
     * Call this method to update the expression.
     * We use this approach instead of listening to the solution.
     * Listening to the solution introduces problems with order dependencies.
     * And if the solute type changes, then the expression type may no longer be appropriate.
     */
    public abstract void update();
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setScalingEnabled( boolean enabled ) {
        if ( enabled != scalingEnabled ) {
            scalingEnabled = enabled;
            if ( !enabled ) {
                animateToUnityScale();
            }
            else {
                animateToConcentrationScale();
            }
        }
    }
    
    private void animateToConcentrationScale() {
        cancelScaleAnimation();
        leftNumeratorAnimator = new ScalingAnimator( leftNumeratorNode, leftNumeratorScale );
        rightNumeratorAnimator = new ScalingAnimator( rightNumeratorNode, rightNumeratorScale );
        denominatorAnimator = new ScalingAnimator( denominatorNode, denominatorScale );
        leftNumeratorAnimator.start();
        rightNumeratorAnimator.start();
        denominatorAnimator.start();
    }
    
    private void animateToUnityScale() {
        cancelScaleAnimation();
        leftNumeratorAnimator = new ScalingAnimator( leftNumeratorNode, 1 );
        rightNumeratorAnimator = new ScalingAnimator( rightNumeratorNode, 1 );
        denominatorAnimator = new ScalingAnimator( denominatorNode, 1 );
        leftNumeratorAnimator.start();
        rightNumeratorAnimator.start();
        denominatorAnimator.start();
    }
    
    private void cancelScaleAnimation() {
        if ( leftNumeratorAnimator != null ) {
            leftNumeratorAnimator.stop();
            leftNumeratorAnimator = null;
        }
        if ( rightNumeratorAnimator != null ) {
            rightNumeratorAnimator.stop();
            rightNumeratorAnimator = null;
        }
        if ( denominatorAnimator != null ) {
            denominatorAnimator.stop();
            denominatorAnimator = null;
        }
    }
    
    public boolean isScalingEnabled() {
        return scalingEnabled;
    }
    
    protected void scaleLeftNumeratorToConcentration( double concentration ) {
        leftNumeratorScale = scaleTermToConcentration( leftNumeratorNode, concentration );
    }
    
    protected void scaleRightNumeratorToConcentration( double concentration ) {
        rightNumeratorScale = scaleTermToConcentration( rightNumeratorNode, concentration );
    }
    
    protected void scaleDenominatorToConcentration( double concentration ) {
        denominatorScale = scaleTermToConcentration( denominatorNode, concentration );
    }
    
    private double scaleTermToConcentration( TermNode termNode, double concentration ) {
        double scale = ConcentrationScaleModel.getFontSize( concentration ) / SYMBOL_FONT.getSize();
        if ( scalingEnabled ) {
            cancelScaleAnimation();  // if this method is called, cancel all animation of terms, the user is controlling scale
            termNode.setScale( scale );
        }
        return scale;
    }
    
    /*
     * Sets all scalable nodes to have unity scale.
     */
    private void setUnityScale() {
        leftNumeratorNode.setScale( 1.0 );
        rightNumeratorNode.setScale( 1.0 );
        denominatorNode.setScale( 1.0 );
    }
    
    /**
     * Sets the K value.
     */
    public void setKValue( double value ) {
        valueNode.setValue( value );
        updateKRepresentation();
        updateLayout();
    }
    
    /*
     * The value of K displayed may vary depending on what range K is in.
     * 
     * weak: = 2.04 x 10^-3
     * strong: = Large
     * intermediate: > 1
     */
    private void updateKRepresentation() {
        if ( kRepresentationVaries ) {
            final double k = valueNode.getValue();
            boolean isWeak = ABSConstants.WEAK_STRENGTH_RANGE.contains( k );
            boolean isStrong = ABSConstants.STRONG_STRENGTH_RANGE.contains( k );
            valueNode.setVisible( isWeak );
            largeValueNode.setVisible( isStrong );
            rightEqualsNode.setVisible( isStrong || isWeak );
            greaterThanOneNode.setVisible( !isStrong && !isWeak );
        }
    }
    
    protected void setLeftNumeratorProperties( String text, Color color ) {
        removeChild( leftNumeratorNode );
        leftNumeratorNode = new TermNode( text, color );
        addChild( leftNumeratorNode );
        updateLayout();
    }
    
    protected void setRightNumeratorProperties( String text, Color color ) {
        removeChild( rightNumeratorNode );
        rightNumeratorNode = new TermNode( text, color );
        addChild( rightNumeratorNode );
        updateLayout();
    }
    
    protected void setDenominatorProperties( String text, Color color ) {
        removeChild( denominatorNode );
        denominatorNode = new TermNode( text, color );
        addChild( denominatorNode );
        updateLayout();
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
        final double leftNumeratorScale = leftNumeratorNode.getScale();
        final double rightNumeratorScale = rightNumeratorNode.getScale();
        final double denominatorScale = denominatorNode.getScale();
        // set all nodes to unity scale
        setUnityScale();
        // offsets
        double xOffset, yOffset;
        // K
        xOffset = PNodeUtils.getOriginXOffset( kNode );
        yOffset = PNodeUtils.getOriginYOffset( kNode );
        kNode.setOffset( xOffset, yOffset );
        // left equals
        xOffset = kNode.getFullBoundsReference().getMaxX() + X_SPACING - PNodeUtils.getOriginXOffset( leftEqualsNode );
        yOffset = kNode.getFullBoundsReference().getCenterY() - ( leftEqualsNode.getFullBoundsReference().getHeight() / 2 ) - PNodeUtils.getOriginYOffset( leftEqualsNode );
        leftEqualsNode.setOffset( xOffset, yOffset );
        // terms
        if ( hasDenominator ) {
            // left numerator
            xOffset = leftEqualsNode.getFullBoundsReference().getMaxX() + X_SPACING - PNodeUtils.getOriginXOffset( leftNumeratorNode );
            yOffset = leftEqualsNode.getFullBoundsReference().getCenterY() - leftNumeratorNode.getFullBoundsReference().getHeight() - Y_SPACING - PNodeUtils.getOriginYOffset( leftNumeratorNode );
            leftNumeratorNode.setOffset( xOffset, yOffset );
            // right numerator
            xOffset = leftNumeratorNode.getFullBoundsReference().getMaxX() + X_SPACING - PNodeUtils.getOriginXOffset( rightNumeratorNode );
            yOffset = leftNumeratorNode.getYOffset(); 
            rightNumeratorNode.setOffset( xOffset, yOffset );
            // divided line
            double length = rightNumeratorNode.getFullBoundsReference().getMaxX() - leftNumeratorNode.getFullBoundsReference().getX();
            dividedNode.setLength( length );
            xOffset = leftNumeratorNode.getXOffset() - PNodeUtils.getOriginXOffset( dividedNode );
            yOffset = leftEqualsNode.getFullBoundsReference().getCenterY() - ( dividedNode.getFullBoundsReference().getHeight() / 2 ) - PNodeUtils.getOriginYOffset( dividedNode );
            dividedNode.setOffset( xOffset, yOffset );
            // denominator
            xOffset = dividedNode.getFullBoundsReference().getCenterX() - ( denominatorNode.getFullBoundsReference().getWidth() / 2 ) - PNodeUtils.getOriginXOffset( denominatorNode );
            yOffset = dividedNode.getFullBoundsReference().getMaxY() + Y_SPACING - PNodeUtils.getOriginYOffset( denominatorNode );
            denominatorNode.setOffset( xOffset, yOffset );
        }
        else {
            // left numerator
            xOffset = leftEqualsNode.getFullBoundsReference().getMaxX() + X_SPACING - PNodeUtils.getOriginXOffset( leftNumeratorNode );
            yOffset = leftEqualsNode.getFullBoundsReference().getCenterY() - ( leftNumeratorNode.getFullBoundsReference().getHeight() / 2 ) - PNodeUtils.getOriginYOffset( leftNumeratorNode );
            leftNumeratorNode.setOffset( xOffset, yOffset );
            // right numerator
            xOffset = leftNumeratorNode.getFullBoundsReference().getMaxX() + X_SPACING - PNodeUtils.getOriginXOffset( rightNumeratorNode );
            yOffset = leftNumeratorNode.getYOffset(); 
            rightNumeratorNode.setOffset( xOffset, yOffset );
        }
        // right equals
        xOffset = rightNumeratorNode.getFullBoundsReference().getMaxX() + X_SPACING - PNodeUtils.getOriginXOffset( rightEqualsNode );
        yOffset = leftEqualsNode.getYOffset() - PNodeUtils.getOriginYOffset( rightEqualsNode );
        rightEqualsNode.setOffset( xOffset, yOffset );
        // greater than
        greaterThanOneNode.setOffset( xOffset, yOffset );
        // value
        final double fudgeFactor = -4;
        xOffset = rightEqualsNode.getFullBoundsReference().getMaxX() + X_SPACING - PNodeUtils.getOriginXOffset( valueNode );
        yOffset = rightEqualsNode.getFullBoundsReference().getCenterY() - ( valueNode.getFullBoundsReference().getHeight() / 2 ) - PNodeUtils.getOriginYOffset( valueNode ) + fudgeFactor;
        valueNode.setOffset( xOffset, yOffset );
        // large value
        xOffset = rightEqualsNode.getFullBoundsReference().getMaxX() + X_SPACING - PNodeUtils.getOriginXOffset( largeValueNode );
        yOffset = rightEqualsNode.getFullBoundsReference().getCenterY() - ( largeValueNode.getFullBoundsReference().getHeight() / 2 ) - PNodeUtils.getOriginYOffset( largeValueNode );
        largeValueNode.setOffset( xOffset, yOffset );
        // restore scales
        leftNumeratorNode.setScale( leftNumeratorScale );
        rightNumeratorNode.setScale( rightNumeratorScale );
        denominatorNode.setScale( denominatorScale );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Symbol for K.
     */
    private static class KNode extends HTMLNode {
        
        public KNode( String text ) {
            super( HTMLUtils.toHTMLString( text ) );
            setFont( K_FONT );
            setHTMLColor( ABSColors.K );
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
        
        public TermNode( String text, Color color ) {
            super();
            symbolNode= new SymbolNode( text, SYMBOL_FONT, color );
            addChild( symbolNode );
            leftBracketNode = new LeftBracketNode();
            addChild( leftBracketNode );
            rightBracketNode = new RightBracketNode();
            addChild( rightBracketNode );
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
            yOffset = leftBracketNode.getYOffset();
            symbolNode.setOffset( xOffset, yOffset );
            // right bracket
            xOffset = symbolNode.getFullBoundsReference().getMaxX() + BRACKET_X_MARGIN;
            yOffset = leftBracketNode.getYOffset();
            rightBracketNode.setOffset( xOffset, yOffset );
        }
        
        /**
         * Terms scale about their center.
         */
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
            super( ABSStrings.VALUE_LARGE );
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
     * Greater than sign.
     */
    private static class GreaterThanOneNode extends PText {
        public GreaterThanOneNode() {
            super( "> 1" );
            setFont( GREATER_THAN_FONT );
            setTextPaint( GREATER_THAN_COLOR );
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
}
