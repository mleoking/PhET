/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.moleculecounts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.text.NumberFormat;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.view.NegligibleValueNode;
import edu.colorado.phet.common.phetcommon.util.ConstantPowerOfTenNumberFormat;
import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.RectangularBackgroundNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for molecule counts, knows nothing about model or how to update itself.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class AbstractMoleculeCountsNode extends PComposite {
    
    private static final int ROWS = 5;

    private static final int REACTANT_INDEX = 0;
    private static final int PRODUCT_INDEX = 1;
    private static final int H3O_INDEX = 2;
    private static final int OH_INDEX = 3;
    private static final int H2O_INDEX = 4;
    
    private static final double X_SPACING = 10;
    private static final double Y_SPACING = 20;
    
    private static final TimesTenNumberFormat VALUE_FORMAT_DEFAULT = new TimesTenNumberFormat( "0.00" );
    private static final ConstantPowerOfTenNumberFormat VALUE_FORMAT_H2O = new ConstantPowerOfTenNumberFormat( "0.00", 25 );
    private static final Color VALUE_BACKGROUND_COLOR = new Color( 255, 255, 255, 128 ); // translucent white
    private static final Insets VALUE_INSETS = new Insets( 4, 4, 4, 4 ); // top, left, bottom, right
    private static final double NEGLIGIBLE_THRESHOLD = 0;
    
    private static final Font LABEL_FONT = new PhetFont( Font.PLAIN, 18 );
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final double ICON_SCALE = 0.25; //TODO: scale image files so that this is 1.0
    
    private final ValueNode[] countNodes;
    private final IconNode[] iconNodes;
    private final LabelNode[] labelNodes;
    
    public AbstractMoleculeCountsNode() {
        super();

        // this node is not interactive
        setPickable( false );
        setChildrenPickable( false );

        // values
        countNodes = new ValueNode[ ROWS ];
        for ( int i = 0; i < countNodes.length; i++ ) {
            countNodes[i] = new ValueNode();
            addChild( countNodes[i] );
        }

        // icons
        iconNodes = new IconNode[ ROWS ];
        for ( int i = 0; i < iconNodes.length; i++ ) {
            iconNodes[i] = new IconNode();
            addChild( iconNodes[i] );
        }

        // labels
        labelNodes = new LabelNode[ ROWS ];
        for ( int i = 0; i < labelNodes.length; i++ ) {
            labelNodes[i] = new LabelNode();
            addChild( labelNodes[i] );
        }
        
        setIconAndLabel( H3O_INDEX, ABSImages.H3O_PLUS_MOLECULE, ABSSymbols.H3O_PLUS );
        setIconAndLabel( OH_INDEX, ABSImages.OH_MINUS_MOLECULE, ABSSymbols.OH_MINUS );
        setIconAndLabel( H2O_INDEX, ABSImages.H2O_MOLECULE, ABSSymbols.H2O );
        setCountFormat( H2O_INDEX, VALUE_FORMAT_H2O );
        
        updateLayout();
    }
    
    private double getMaxIconWidth() {
        double maxIconWidth = 0;
        for ( int i = 0; i < iconNodes.length; i++ ) {
            IconNode iconNode = iconNodes[i];
            double iconWidth = iconNode.getFullBoundsReference().getWidth();
            maxIconWidth = Math.max( iconWidth, maxIconWidth );
        }
        return maxIconWidth;
    }
    
    private void updateLayout() {
        double xOffset = 0;
        double yOffset = 0;
        for ( int i = 0; i < iconNodes.length; i++ ) {
            // vertical column of icons, horizontally centered at origin
            IconNode iconNode = iconNodes[i];
            xOffset = -( iconNode.getFullBoundsReference().getWidth() / 2 );
            iconNode.setOffset( xOffset, yOffset );
            yOffset = yOffset + iconNode.getFullBoundsReference().getHeight() + Y_SPACING;
            // label
            updateLabelLayout( i );
            // count
            updateCountLayout( i );
        }
    }
    
    private void updateLabelLayout( int index ) {
        // to the right of icon, left justified, vertically align centers
        double maxIconWidth = getMaxIconWidth();
        LabelNode labelNode = labelNodes[index];
        IconNode iconNode = iconNodes[index];
        double xOffset = ( maxIconWidth / 2 ) + X_SPACING;
        double yOffset = iconNode.getFullBoundsReference().getCenterY() - ( labelNode.getFullBoundsReference().getHeight() / 2 );
        labelNode.setOffset( xOffset, yOffset );
    }
    
    private void updateCountLayout( int index ) {
        // to left of icon, right justified, vertically centered
        double maxIconWidth = getMaxIconWidth();
        ValueNode countNode = countNodes[index];
        IconNode iconNode = iconNodes[index];
        double xOffset = -( maxIconWidth / 2 ) - X_SPACING - countNode.getFullBoundsReference().getWidth();
        double yOffset = iconNode.getFullBoundsReference().getCenterY() - ( countNode.getFullBoundsReference().getHeight() / 2 );
        countNode.setOffset( xOffset, yOffset );
    }
    
    public void setReactantVisible( boolean visible ) {
        setVisible( REACTANT_INDEX, visible );
    }
    
    public void setProductVisible( boolean visible ) {
        setVisible( PRODUCT_INDEX, visible );
    }
    
    public void setReactantCount( double count ) {
        setCount( REACTANT_INDEX, count );
    }
    
    public void setProductCount( double count ) {
        setCount( PRODUCT_INDEX, count );
    }

    public void setH3OCount( double count ) {
        setCount( H3O_INDEX, count );
    }
    
    public void setOHCount( double count ) {
        setCount( OH_INDEX, count );
    }
    
    public void setH2OCount( double count ) {
        setCount( H2O_INDEX, count );
    }

    private void setCount( int row, double count ) {
        countNodes[row].setValue( count );
        updateCountLayout( row );
    }
    
    private void setCountFormat( int row, NumberFormat format ) {
        countNodes[row].setFormat( format );
        updateCountLayout( row );
    }
    
    public void setReactantNegligibleEnabled( boolean enabled ) {
        setNegligibleEnabled( REACTANT_INDEX, enabled, NEGLIGIBLE_THRESHOLD );
    }
    
    private void setNegligibleEnabled( int row, boolean enabled, double negligibleThreshold ) {
        countNodes[row].setNegligibleEnabled( enabled, negligibleThreshold );
        updateCountLayout( row );
    }
    
    public void setReactantIcon( Image image ) {
        setIcon( REACTANT_INDEX, image );
    }
    
    public void setProductIcon( Image image ) {
        setIcon( PRODUCT_INDEX, image );
    }
    
    private void setIcon( int row, Image image ) {
        iconNodes[row].setImage( image );
        updateLayout();
    }
    
    public void setReactantLabel( String text ) {
        setLabel( REACTANT_INDEX, text );
    }
    
    public void setProductLabel( String text ) {
        setLabel( PRODUCT_INDEX, text );
    }
    
    private void setLabel( int row, String text ) {
        labelNodes[row].setHTML( text );
        updateLabelLayout( row );
    }
    
    private void setIconAndLabel( int row, Image image, String text ) {
        setIcon( row, image );
        setLabel( row, text );
    }
    
    private void setVisible( int row, boolean visible ) {
        countNodes[row].setVisible( visible );
        iconNodes[row].setVisible( visible );
        labelNodes[row].setVisible( visible );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Labels used in this view.
     */
    static class LabelNode extends HTMLNode {
        
        public LabelNode() {
            this( "" );
        }

        public LabelNode( String html ) {
            super( HTMLUtils.toHTMLString( html ) );
            setFont( LABEL_FONT );
            setHTMLColor( LABEL_COLOR );
        }

        public void setHTML( String html ) {
            super.setHTML( HTMLUtils.toHTMLString( html ) );
        }
    }

    /*
     * Icons used in this view.
     */
    static class IconNode extends PComposite {

        private PImage imageNode;
        
        public IconNode() {
            this( null );
        }

        public IconNode( Image image ) {
            super();
            imageNode = new PImage( image );
            addChild( imageNode );
            scale( ICON_SCALE );
        }

        public void setImage( Image image ) {
            imageNode.setImage( image );
        }
    }

    /*
     * Decorates a value with a rectangular background.
     */
    static class ValueNode extends RectangularBackgroundNode {
        
        private final NegligibleValueNode numberNode;
        
        public ValueNode() {
            super( new NegligibleValueNode( 0, VALUE_FORMAT_DEFAULT ), VALUE_INSETS, VALUE_BACKGROUND_COLOR );
            numberNode = (NegligibleValueNode) getForegroundNode();
        }
        
        public void setValue( double value ) {
            numberNode.setValue( value );
        }
        
        public void setFormat( NumberFormat format ) {
            numberNode.setFormat( format );
        }
        
        public void setNegligibleEnabled( boolean enabled, double threshold ) {
            numberNode.setNegligibleEnabled( enabled, threshold );
        }
    }
}