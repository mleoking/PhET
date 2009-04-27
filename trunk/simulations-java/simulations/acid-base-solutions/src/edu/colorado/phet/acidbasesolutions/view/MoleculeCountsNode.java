package edu.colorado.phet.acidbasesolutions.view;

import java.awt.*;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.common.phetcommon.util.ConstantPowerOfTenNumberFormat;
import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.FormattedNumberNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.RectangularBackgroundNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.PinnedLayoutNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;


public class MoleculeCountsNode extends PinnedLayoutNode {
    
    //TODO localize
    private static final String NEGLIGIBLE = "NEGLIGIBLE";

    private static final Font NEGLIGIBLE_FONT = new PhetFont( Font.PLAIN, ABSConstants.CONTROL_FONT_SIZE - 2 );
    private static final Font VALUE_FONT = new PhetFont( Font.BOLD, ABSConstants.CONTROL_FONT_SIZE );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final Color VALUE_BACKGROUND_COLOR = new Color( 255, 255, 255, 128 ); // translucent white
    private static final Insets VALUE_INSETS = new Insets( 4, 4, 4, 4 );
    private static final TimesTenNumberFormat VALUE_FORMAT_DEFAULT = new TimesTenNumberFormat( "0.00" );
    private static final ConstantPowerOfTenNumberFormat VALUE_FORMAT_H2O = new ConstantPowerOfTenNumberFormat( "0.0", 25 );
    
    private final ValueNode countLHS, countRHS, countH3OPlus, countOHMinus, countH2O;
    private final IconNode iconLHS, iconRHS, iconH3OPlus, iconOHMinus, iconH2O;
    private final HTMLNode labelLHS, labelRHS, labelH3OPlus, labelOHMinus, labelH2O;
    
    public MoleculeCountsNode() {
        super();
        
        // this node is not interactive
        setPickable( false );
        setChildrenPickable( false );
        
        // values
        countLHS = new NegligibleValueNode( 0, 0 );
        countRHS = new ValueNode( 0 );
        countH3OPlus = new ValueNode( 0 );
        countOHMinus = new ValueNode( 0 );
        countH2O = new ValueNode( 0, VALUE_FORMAT_H2O );
        PNode[] countNodes = { countLHS, countRHS, countH3OPlus, countOHMinus, countH2O };
        
        // icons
        iconLHS = new IconNode( ABSImages.HA_MOLECULE );
        iconRHS = new IconNode( ABSImages.OH_MINUS_MOLECULE );
        iconH3OPlus = new IconNode( ABSImages.H3O_PLUS_MOLECULE );
        iconOHMinus = new IconNode( ABSImages.OH_MINUS_MOLECULE );
        iconH2O = new IconNode( ABSImages.H2O_MOLECULE );
        PNode[] iconNodes = { iconLHS, iconRHS, iconH3OPlus, iconOHMinus, iconH2O };
        
        // labels
        labelLHS = new HTMLNode( "?" );
        labelRHS = new HTMLNode( "?" );
        labelH3OPlus = new HTMLNode( HTMLUtils.toHTMLString( ABSSymbols.H3O_PLUS ) );
        labelOHMinus = new HTMLNode( HTMLUtils.toHTMLString( ABSSymbols.OH_MINUS ) );
        labelH2O = new HTMLNode( HTMLUtils.toHTMLString( ABSSymbols.H2O ) );
        PNode[] labelNodes = { labelLHS, labelRHS, labelH3OPlus, labelOHMinus, labelH2O };
        
        // layout in a grid
        GridBagLayout layout = new GridBagLayout();
        setLayout( layout );
        // uniform minimum row height
        layout.rowHeights = new int[countNodes.length];
        for ( int i = 0; i < layout.rowHeights.length; i++ ) {
            layout.rowHeights[i] = (int) ( 2 * countLHS.getFullBoundsReference().getHeight() + 1 );
        }
        // default constraints
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets( 5, 5, 5, 5 );
        constraints.gridy = GridBagConstraints.RELATIVE; // row
        // counts
        {
            constraints.gridx = 0; // column
            constraints.anchor = GridBagConstraints.EAST;
            for ( int i = 0; i < countNodes.length; i++ ) {
                addChild( countNodes[i], constraints );
            }
        }
        // icons
        {
            constraints.gridx++; // column
            constraints.anchor = GridBagConstraints.CENTER;
            for ( int i = 0; i < iconNodes.length; i++ ) {
                addChild( iconNodes[i], constraints );
            }
        }
        // labels
        {
            constraints.gridx++; // column
            constraints.anchor = GridBagConstraints.WEST;
            for ( int i = 0; i < labelNodes.length; i++ ) {
                addChild( labelNodes[i], constraints );
            }
        }
        
        setPinnedNode( iconNodes[0] );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setLHS( double count, Image image, String label ) {
        countLHS.setValue( count );
        iconLHS.setImage( image );
        labelLHS.setHTML( HTMLUtils.toHTMLString( label ) );
    }
    
    public void setRHS( double count, Image image, String label ) {
        countRHS.setValue( count );
        iconRHS.setImage( image );
        labelRHS.setHTML( HTMLUtils.toHTMLString( label ) );
    }
    
    public void setLHS( double count ) {
        countLHS.setValue( count );
    }
    
    public void setRHS( double count ) {
        countRHS.setValue( count );
    }
    
    public void setH3OPlus( double count ) {
        countH3OPlus.setValue( count );
    }
    
    public void setOHMinus( double count ) {
        countOHMinus.setValue( count );
    }
    
    public void setH2O( double count ) {
        countH2O.setValue( count );
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
            scale( 0.25 );//TODO scale image files
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
        
        protected PNode getNumberNode() {
            return _numberNode;
        }
    }
    
    private static class NegligibleValueNode extends ValueNode {

        private final PText _textNode;
        private final double _negligibleValue;
        
        public NegligibleValueNode( double value, double negligibleValue ) {
            this( value, negligibleValue, VALUE_FORMAT_DEFAULT );
        }
        
        public NegligibleValueNode( double value, double minValue, NumberFormat format ) {
            super( value, format );
            _negligibleValue = minValue;
            _textNode = new PText( NEGLIGIBLE );
            _textNode.setFont( NEGLIGIBLE_FONT );
            addChild( new RectangularBackgroundNode( _textNode, VALUE_INSETS, VALUE_BACKGROUND_COLOR ) );
            setValue( value );
        }
        
        public void setValue( double value ) {
            super.setValue( value );
            getNumberNode().setVisible( value > _negligibleValue );
            _textNode.setVisible( value <= _negligibleValue );
        }
    }
    
    public static void main( String[] args ) {
        
            Dimension canvasSize = new Dimension( 800, 600 );
            PhetPCanvas canvas = new PhetPCanvas( canvasSize );
            canvas.setPreferredSize( canvasSize );
            
            final MoleculeCountsNode node = new MoleculeCountsNode();
            node.setLHS( 0, ABSImages.HA_MOLECULE, ABSSymbols.HA );
            node.setRHS( 0, ABSImages.A_MINUS_MOLECULE, ABSSymbols.A_MINUS );
            canvas.getLayer().addChild( node );
            node.setOffset( 100, 100 );
            node.adjustPinnedNode();
            
            JPanel controlPanel = new JPanel();
            final JSlider slider = new JSlider( 0, 100000 );
            slider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    node.setLHS( slider.getValue() );
                }
            });
            controlPanel.add( slider );
            
            JPanel panel = new JPanel( new BorderLayout() );
            panel.add( canvas, BorderLayout.CENTER );
            panel.add( controlPanel, BorderLayout.EAST );
            
            JFrame frame = new JFrame();
            frame.setContentPane( panel );
            frame.pack();
            frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            frame.setVisible( true );
    }
}
