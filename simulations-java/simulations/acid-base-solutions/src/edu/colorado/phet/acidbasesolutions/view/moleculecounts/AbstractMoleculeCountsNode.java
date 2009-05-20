/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.moleculecounts;

import java.awt.*;
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
import edu.colorado.phet.common.piccolophet.nodes.layout.PinnedLayoutNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;

public abstract class AbstractMoleculeCountsNode extends PinnedLayoutNode {
    
    private static final int ROWS = 5;

    private static final int H3O_ROW = 2;
    private static final int OH_ROW = 3;
    private static final int H2O_ROW = 4;
    
    private static final TimesTenNumberFormat VALUE_FORMAT_DEFAULT = new TimesTenNumberFormat( "0.00" );
    private static final ConstantPowerOfTenNumberFormat VALUE_FORMAT_H2O = new ConstantPowerOfTenNumberFormat( "0.0", 25 );
    private static final Color VALUE_BACKGROUND_COLOR = new Color( 255, 255, 255, 128 ); // translucent white
    private static final Insets VALUE_INSETS = new Insets( 4, 4, 4, 4 ); // top, left, bottom, right
    
    private static final Font LABEL_FONT = new PhetFont( Font.PLAIN, 16 );
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
        }

        // icons
        iconNodes = new IconNode[ ROWS ];
        for ( int i = 0; i < iconNodes.length; i++ ) {
            iconNodes[i] = new IconNode();
        }

        // labels
        labelNodes = new LabelNode[ ROWS ];
        for ( int i = 0; i < labelNodes.length; i++ ) {
            labelNodes[i] = new LabelNode();
        }

        // layout in a grid
        GridBagLayout layout = new GridBagLayout();
        setLayout( layout );
        // uniform minimum row height
        layout.rowHeights = new int[countNodes.length];
        for ( int i = 0; i < layout.rowHeights.length; i++ ) {
            layout.rowHeights[i] = (int) ( 2 * countNodes[0].getFullBoundsReference().getHeight() + 1 );
        }
        // default constraints
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets( 5, 2, 5, 2 ); // top,left,bottom,right
        constraints.gridy = GridBagConstraints.RELATIVE; // row
        // counts
        {
            constraints.gridx = 0; // next column
            constraints.anchor = GridBagConstraints.EAST;
            for ( int i = 0; i < countNodes.length; i++ ) {
                addChild( countNodes[i], constraints );
            }
        }
        // icons
        {
            constraints.gridx++; // next column
            constraints.anchor = GridBagConstraints.CENTER;
            for ( int i = 0; i < iconNodes.length; i++ ) {
                addChild( iconNodes[i], constraints );
            }
        }
        // labels
        {
            constraints.gridx++; // next column
            constraints.anchor = GridBagConstraints.WEST;
            for ( int i = 0; i < labelNodes.length; i++ ) {
                addChild( labelNodes[i], constraints );
            }
        }
        
        setIconAndLabel( H3O_ROW, ABSImages.H3O_PLUS_MOLECULE, ABSSymbols.H3O_PLUS );
        setIconAndLabel( OH_ROW, ABSImages.OH_MINUS_MOLECULE, ABSSymbols.OH_MINUS );
        setIconAndLabel( H2O_ROW, ABSImages.H2O_MOLECULE, ABSSymbols.H2O );
        setFormat( H2O_ROW, VALUE_FORMAT_H2O );
    }
    
    public void setH3OCount( double count ) {
        setCount( H3O_ROW, count );
    }
    
    public void setOHCount( double count ) {
        setCount( OH_ROW, count );
    }
    
    public void setH2OCount( double count ) {
        setCount( H2O_ROW, count );
    }

    protected void setCount( int row, double count ) {
        countNodes[row].setValue( count );
    }
    
    protected void setCountFormat( int row, NumberFormat format ) {
        countNodes[row].setFormat( format );
    }
    
    protected void setNegligibleEnabled( int row, boolean enabled, double negligibleThreshold ) {
        countNodes[row].setNegligibleEnabled( enabled, negligibleThreshold );
    }
    
    protected void setIcon( int row, Image image ) {
        iconNodes[row].setImage( image );
    }
    
    protected void setLabel( int row, String text ) {
        labelNodes[row].setHTML( text );
    }
    
    protected void setIconAndLabel( int row, Image image, String text ) {
        setIcon( row, image );
        setLabel( row, text );
    }
    
    protected void setVisible( int row, boolean visible ) {
        countNodes[row].setVisible( visible );
        iconNodes[row].setVisible( visible );
        labelNodes[row].setVisible( visible );
    }
    
    protected void setFormat( int row, NumberFormat format ) {
        countNodes[row].setFormat( format );
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