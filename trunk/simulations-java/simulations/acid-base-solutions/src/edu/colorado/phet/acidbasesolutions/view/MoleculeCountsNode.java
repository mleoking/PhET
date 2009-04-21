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
    private final SwingLayoutNode layoutNode;
    
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
        iconH3OPlus = new IconNode( ABSImages.H3O_PLUS_MOLECULE );
        iconOHMinus = new IconNode( ABSImages.OH_MINUS_MOLECULE );
        iconH2O = new IconNode( ABSImages.H2O_MOLECULE );
        
        // labels
        labelLHS = new HTMLNode( "?" );
        labelRHS = new HTMLNode( "?" );
        labelH3OPlus = new HTMLNode( HTMLUtils.toHTMLString( ABSSymbols.H3O_PLUS ) );
        labelOHMinus = new HTMLNode( HTMLUtils.toHTMLString( ABSSymbols.OH_MINUS ) );
        labelH2O = new HTMLNode( HTMLUtils.toHTMLString( ABSSymbols.H2O ) );
        
        // layout in a grid
        layoutNode = new SwingLayoutNode( new GridBagLayout() );
        addChild( layoutNode );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets( 10, 10, 10, 10 );
        constraints.gridy = 0; // row
        constraints.gridx = 0; // column
        constraints.anchor = GridBagConstraints.EAST;
        PNode[] countNodes = { countLHS, countRHS, countH3OPlus, countOHMinus, countH2O };
        for ( int i = 0; i < countNodes.length; i++ ) {
            layoutNode.addChild( countNodes[i], constraints );
            constraints.gridy++;
        }
        constraints.gridy = 0; // row
        constraints.gridx++; // column
        constraints.anchor = GridBagConstraints.CENTER;
        PNode[] iconNodes = { iconLHS, iconRHS, iconH3OPlus, iconOHMinus, iconH2O };
        for ( int i = 0; i < iconNodes.length; i++ ) {
            layoutNode.addChild( iconNodes[i], constraints );
            constraints.gridy++;
        }
        constraints.gridy = 0; // row
        constraints.gridx++; // column
        constraints.anchor = GridBagConstraints.WEST;
        PNode[] labelNodes = { labelLHS, labelRHS, labelH3OPlus, labelOHMinus, labelH2O };
        for ( int i = 0; i < labelNodes.length; i++ ) {
            layoutNode.addChild( labelNodes[i], constraints );
            constraints.gridy++;
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
    }
    
    public void setRHS( double count, Image image, String label ) {
        countRHS.setValue( count );
        iconRHS.setImage( image );
        iconRHS.setOffset( iconRHS.getFullBoundsReference().getWidth() / 2, 0 );// center justified
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
    }
    
    public static void main( String[] args ) {
        
            Dimension canvasSize = new Dimension( 800, 600 );
            PhetPCanvas canvas = new PhetPCanvas( canvasSize );
            canvas.setPreferredSize( canvasSize );
            
            final MoleculeCountsNode node = new MoleculeCountsNode();
            canvas.getLayer().addChild( node );
            node.setOffset( 100, 100 );
            
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
