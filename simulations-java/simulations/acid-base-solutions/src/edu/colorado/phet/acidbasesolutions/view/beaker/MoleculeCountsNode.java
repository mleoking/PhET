package edu.colorado.phet.acidbasesolutions.view.beaker;

import java.awt.*;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
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
    
    private static final Font NEGLIGIBLE_FONT = new PhetFont( Font.PLAIN, 16 );
    private static final Font VALUE_FONT = new PhetFont( Font.PLAIN, 16 );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final Color VALUE_BACKGROUND_COLOR = new Color( 255, 255, 255, 128 ); // translucent white
    private static final Insets VALUE_INSETS = new Insets( 4, 4, 4, 4 ); // top, left, bottom, right
    private static final TimesTenNumberFormat VALUE_FORMAT_DEFAULT = new TimesTenNumberFormat( "0.00" );
    private static final ConstantPowerOfTenNumberFormat VALUE_FORMAT_H2O = new ConstantPowerOfTenNumberFormat( "0.0", 25 );
    private static final Font LABEL_FONT = new PhetFont( Font.PLAIN, 16 );
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final double ICON_SCALE = 0.25; //TODO: scale image files so that this is 1.0
    
    private final NegligibleValueNode countLHS;
    private final ValueNode countRHS, countH3OPlus, countOHMinus, countH2O;
    private final IconNode iconLHS, iconRHS, iconH3OPlus, iconOHMinus, iconH2O;
    private final LabelNode labelLHS, labelRHS, labelH3OPlus, labelOHMinus, labelH2O;
    
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
        iconRHS = new IconNode( ABSImages.A_MINUS_MOLECULE );
        iconH3OPlus = new IconNode( ABSImages.H3O_PLUS_MOLECULE );
        iconOHMinus = new IconNode( ABSImages.OH_MINUS_MOLECULE );
        iconH2O = new IconNode( ABSImages.H2O_MOLECULE );
        PNode[] iconNodes = { iconLHS, iconRHS, iconH3OPlus, iconOHMinus, iconH2O };
        
        // labels
        labelLHS = new LabelNode( ABSSymbols.HA );
        labelRHS = new LabelNode( ABSSymbols.A_MINUS );
        labelH3OPlus = new LabelNode( ABSSymbols.H3O_PLUS );
        labelOHMinus = new LabelNode( ABSSymbols.OH_MINUS );
        labelH2O = new LabelNode( ABSSymbols.H2O );
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
        
        setPinnedNode( iconNodes[0] );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setMoleculeLHS( Image image, String label ) {
        iconLHS.setImage( image );
        labelLHS.setHTML( label );
    }
    
    public void setMoleculeRHS( Image image, String label ) {
        iconRHS.setImage( image );
        labelRHS.setHTML( label );
    }
    
    public void setCountLHS( double count ) {
        countLHS.setValue( count );
    }
    
    public void setCountRHS( double count ) {
        countRHS.setValue( count );
    }
    
    public void setCountH3O( double count ) {
        countH3OPlus.setValue( count );
    }
    
    public void setCountOH( double count ) {
        countOHMinus.setValue( count );
    }
    
    public void setCountH2O( double count ) {
        countH2O.setValue( count );
    }
    
    public void setNegligibleEnabled( boolean enabled ) {
        countLHS.setNegligibleEnabled( enabled );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Labels used in this view.
     */
    private static class LabelNode extends HTMLNode {
        
        public LabelNode( String html ) {
            super( HTMLUtils.toHTMLString( html ) );
            setFont( LABEL_FONT );
            setHTMLColor( LABEL_COLOR );
        }
        
        public void setHTML( String html ) {
            setHTML( HTMLUtils.toHTMLString( html ) );
        }
    }
    
    /*
     * Icons used in this view.
     */
    private static class IconNode extends PComposite {
        
        private PImage imageNode;
        
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
     * Displays a formatted number on a background.
     */
    private static class ValueNode extends PComposite {

        private FormattedNumberNode _numberNode;
        private PNode _backgroundNode;
        
        public ValueNode( double value ) {
            this( value, VALUE_FORMAT_DEFAULT );
        }
        
        public ValueNode( double value, NumberFormat format ) {
            _numberNode = new FormattedNumberNode( format, value, VALUE_FONT, VALUE_COLOR );
            _backgroundNode = new RectangularBackgroundNode( _numberNode, VALUE_INSETS, VALUE_BACKGROUND_COLOR );
            addChild( _backgroundNode );
        }
        
        public void setValue( double value ) {
            _numberNode.setValue( value );
        }
        
        public double getValue() {
            return _numberNode.getValue();
        }
        
        protected PNode getBackgroundNode() {
            return _backgroundNode;
        }
    }
    
    /*
     * Displays a formatted number on a background.
     * If that number drops below some threshold, then "NEGLIGIBLE" is displayed.
     * This behavior can also be disabled.
     */
    private static class NegligibleValueNode extends PNode {

        private final double _negligibleValue;
        private final PNode _negligibleBackground;
        private final ValueNode _valueNode;
        private boolean _negligibleEnabled;
        
        public NegligibleValueNode( double value, double negligibleValue ) {
            this( value, negligibleValue, VALUE_FORMAT_DEFAULT );
        }
        
        public NegligibleValueNode( double negligibleValue, double value, NumberFormat format ) {
            super();
            _negligibleValue = negligibleValue;
            // value
            _valueNode = new ValueNode( value, format );
            addChild( _valueNode );
            // negligible
            PText textNode = new PText( ABSStrings.VALUE_NEGLIGIBLE );
            textNode.setFont( NEGLIGIBLE_FONT );
            _negligibleBackground = new RectangularBackgroundNode( textNode, VALUE_INSETS, VALUE_BACKGROUND_COLOR );
            addChild( _negligibleBackground );
            // init
            _negligibleEnabled = true;
            setValue( value );
        }
        
        public void setValue( double value ) {
            _valueNode.setValue( value );
            updateVisibility( value );
        }
        
        public void setNegligibleEnabled( boolean enabled ) {
            if ( enabled != _negligibleEnabled ) {
                _negligibleEnabled = enabled;
                updateVisibility( _valueNode.getValue() );
            }
        }
        
        private void updateVisibility( double value ) {
            if ( _negligibleEnabled ) {
                _valueNode.setVisible( value > _negligibleValue );
                _negligibleBackground.setVisible( value <= _negligibleValue );
            }
            else {
                _valueNode.setVisible( true );
                _negligibleBackground.setVisible( false );
            }
        }
    }
    
    public static void main( String[] args ) {
        
            Dimension canvasSize = new Dimension( 800, 600 );
            PhetPCanvas canvas = new PhetPCanvas( canvasSize );
            canvas.setPreferredSize( canvasSize );
            canvas.setBackground( Color.LIGHT_GRAY );
            
            final MoleculeCountsNode node = new MoleculeCountsNode();
            canvas.getLayer().addChild( node );
            node.setOffset( 100, 100 );
            node.adjustPinnedNode();
            
            JPanel controlPanel = new JPanel();
            final JSlider slider = new JSlider( 0, 100000, 0 );
            slider.setMajorTickSpacing( slider.getMaximum() );
            slider.setPaintTicks( true );
            slider.setPaintLabels( true );
            slider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    node.setCountLHS( slider.getValue() );
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
