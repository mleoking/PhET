package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.text.NumberFormat;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.common.phetcommon.util.ConstantPowerOfTenNumberFormat;
import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.FormattedNumberNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.RectangularBackgroundNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;


public class MoleculeCountsNode extends PhetPNode {
    
    //TODO localize
    private static final String NEGLIGIBLE = "NEGLIGIBLE";

    private static final Font VALUE_FONT = new PhetFont( Font.BOLD, ABSConstants.CONTROL_FONT_SIZE );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final Color VALUE_BACKGROUND_COLOR = new Color( 255, 255, 255, 128 ); // translucent white
    private static final Insets VALUE_INSETS = new Insets( 4, 4, 4, 4 );
    private static final TimesTenNumberFormat VALUE_FORMAT_DEFAULT = new TimesTenNumberFormat( "0.00" );
    private static final ConstantPowerOfTenNumberFormat VALUE_FORMAT_H2O = new ConstantPowerOfTenNumberFormat( "0.0", 25 );
    
    private final PText neglibibleNode;
    private final ValueNode countLHS, countRHS, countH3OPlus, countOHMinus, countH2O;
    private final IconNode iconLHS, iconRHS, iconH3OPlus, iconOHMinus, iconH2O;
    private final HTMLNode labelLHS, labelRHS, labelH3OPlus, labelOHMinus, labelH2O;
    
    public MoleculeCountsNode() {
        super();
        
        // this node is not interactive
        setPickable( false );
        setChildrenPickable( false );
        
        // values
        neglibibleNode = new PText( NEGLIGIBLE );
        neglibibleNode.setFont( VALUE_FONT );
        countLHS = new ValueNode( 0 );
        countRHS = new ValueNode( 0 );
        countH3OPlus = new ValueNode( 0 );
        countOHMinus = new ValueNode( 0 );
        countH2O = new ValueNode( 0, VALUE_FORMAT_H2O );
        
        // icons
        iconLHS = new IconNode( ABSImages.HA_MOLECULE );
        iconRHS = new IconNode( ABSImages.OH_MINUS_MOLECULE );
        iconH3OPlus = new IconNode( ABSImages.H3O_MOLECULE );
        iconOHMinus = new IconNode( ABSImages.OH_MINUS_MOLECULE );
        iconH2O = new IconNode( ABSImages.H2O_MOLECULE );
        
        // labels
        labelLHS = new HTMLNode();
        labelRHS = new HTMLNode();
        labelH3OPlus = new HTMLNode( HTMLUtils.toHTMLString( ABSSymbols.H3O_PLUS ) );
        labelOHMinus = new HTMLNode( HTMLUtils.toHTMLString( ABSSymbols.OH_MINUS ) );
        labelH2O = new HTMLNode( HTMLUtils.toHTMLString( ABSSymbols.H2O ) );
        
        updateLayout();
    }
    
    private void updateLayout() {
        final int iconVerticalSpacing = 10;
        final int iconHorizontalSpacing = 10;
        // layout, origin at center of top-most icon
        // icons
        PNode[] iconNodes = { iconLHS, iconRHS, iconH3OPlus, iconOHMinus, iconH2O };
        PNode previousNode = null;
        for ( int i = 0; i < iconNodes.length; i++ ) {
            PNode currentNode = iconNodes[i];
            double xOffset = -currentNode.getFullBoundsReference().getWidth() / 2;
            double yOffset = ( previousNode == null ? 0 : previousNode.getFullBoundsReference().getMaxY() + iconVerticalSpacing );
            currentNode.setOffset( xOffset, yOffset );
        }
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setLHS( double count, Image image, String label ) {
        countLHS.setValue( count );
        iconLHS.setImage( image );
        iconLHS.setOffset( iconLHS.getFullBoundsReference().getWidth() / 2, 0 ); // center justified
        labelLHS.setHTML( HTMLUtils.toHTMLString( label ) );
        updateLayout();
    }
    
    public void setRHS( double count, Image image, String label ) {
        countRHS.setValue( count );
        iconRHS.setImage( image );
        iconRHS.setOffset( iconRHS.getFullBoundsReference().getWidth() / 2, 0 );// center justified
        labelRHS.setHTML( HTMLUtils.toHTMLString( label ) );
        updateLayout();
    }
    
    public void setH3OPlus( double count ) {
        countH3OPlus.setValue( count );
        updateLayout();
    }
    
    public void setOHMinus( double count ) {
        countOHMinus.setValue( count );
        updateLayout();
    }
    
    public void setH2O( double count ) {
        countH2O.setValue( count );
        updateLayout();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    private static class IconNode extends PComposite {
        
        private PImage imageNode;
        
        public IconNode( Image image ) {
            super();
            imageNode = new PImage( image );
            addChild( imageNode );
            scale( 0.5 );//TODO scale image files
        }
        
        public void setImage( Image image ) {
            imageNode.setImage( image );
        }
    }
    
    /*
     * Displays a formatted number on a background.
     */
    private static class ValueNode extends PComposite {

        private FormattedNumberNode _numberNode;
        
        public ValueNode( double value ) {
            this( value, VALUE_FORMAT_DEFAULT );
        }
        
        public ValueNode( double value, NumberFormat format ) {
            _numberNode = new FormattedNumberNode( format, value, VALUE_FONT, VALUE_COLOR );
            RectangularBackgroundNode backgroundNode = new RectangularBackgroundNode( _numberNode, VALUE_INSETS, VALUE_BACKGROUND_COLOR );
            addChild( backgroundNode );
        }
        
        public void setValue( double value ) {
            _numberNode.setValue( value );
        }
    }
}
