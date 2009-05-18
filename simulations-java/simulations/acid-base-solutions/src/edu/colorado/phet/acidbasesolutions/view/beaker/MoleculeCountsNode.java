
package edu.colorado.phet.acidbasesolutions.view.beaker;

import java.awt.*;
import java.text.NumberFormat;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.Acid;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.model.Base.CustomBase;
import edu.colorado.phet.acidbasesolutions.model.Base.StrongBase;
import edu.colorado.phet.acidbasesolutions.model.Base.WeakBase;
import edu.colorado.phet.acidbasesolutions.model.concentration.*;
import edu.colorado.phet.common.phetcommon.util.ConstantPowerOfTenNumberFormat;
import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FormattedNumberNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.RectangularBackgroundNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.PinnedLayoutNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;


public class MoleculeCountsNode extends PComposite {

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
    
    private final PureWaterCountsNode waterNode;
    private final AcidMoleculeCountsNode acidNode;
    private final WeakBaseMoleculeCountsNode weakBaseNode;
    private final StrongBaseMoleculeCountsNode strongBaseNode;
    
    public MoleculeCountsNode( AqueousSolution solution ) {
        this();
        solution.addSolutionListener( new ModelViewController( solution, this ) );
    }
    
    public MoleculeCountsNode() {
        super();
        // not interactive
        setPickable( false );
        setChildrenPickable( false );
        
        waterNode = new PureWaterCountsNode();
        addChild( waterNode );
        
        acidNode = new AcidMoleculeCountsNode();
        addChild( acidNode );
        
        weakBaseNode = new WeakBaseMoleculeCountsNode();
        addChild( weakBaseNode );
        
        strongBaseNode = new StrongBaseMoleculeCountsNode();
        addChild( strongBaseNode );
    }
    
    protected PureWaterCountsNode getWaterNode() {
        return waterNode;
    }
    
    protected AcidMoleculeCountsNode getAcidNode() {
        return acidNode;
    }
    
    protected WeakBaseMoleculeCountsNode getWeakBaseNode() {
        return weakBaseNode;
    }
    
    protected StrongBaseMoleculeCountsNode getStrongBaseNode() {
        return strongBaseNode;
    }
    
    private static class ModelViewController implements SolutionListener {

        private final AqueousSolution solution;
        private final MoleculeCountsNode countsNode;
        
        public ModelViewController( AqueousSolution solution, MoleculeCountsNode countsNode ) {
            this.solution = solution;
            this.countsNode = countsNode;
            updateView();
        }
        
        public void soluteChanged() {
            updateView();
        }
        
        public void concentrationChanged() {
            updateView();
        }

        public void strengthChanged() {
            updateView();
        }
        
        private void updateView() {
            
            ConcentrationModel concentrationModel = solution.getConcentrationModel();
            
            // visibility
            countsNode.getWaterNode().setVisible( concentrationModel instanceof PureWaterConcentrationModel );
            countsNode.getAcidNode().setVisible( concentrationModel instanceof AcidConcentrationModel );
            countsNode.getWeakBaseNode().setVisible( concentrationModel instanceof WeakBaseConcentrationModel );
            countsNode.getStrongBaseNode().setVisible( concentrationModel instanceof StrongBaseConcentrationModel );
            
            // counts & labels
            if ( concentrationModel instanceof PureWaterConcentrationModel ) {
                PureWaterCountsNode node = countsNode.getWaterNode();
                node.setH3OCount( concentrationModel.getH3OMoleculeCount() );
                node.setOHCount( concentrationModel.getOHMoleculeCount() );
                node.setH2OCount( concentrationModel.getH2OMoleculeCount() );
            }
            else if ( concentrationModel instanceof AcidConcentrationModel ) {
                AcidMoleculeCountsNode node = countsNode.getAcidNode();
                AcidConcentrationModel model = (AcidConcentrationModel) concentrationModel;
                // counts
                node.setAcidCount( model.getAcidMoleculeCount() );
                node.setBaseCount( model.getBaseMoleculeCount() );
                node.setH3OCount( model.getH3OMoleculeCount() );
                node.setOHCount( model.getOHMoleculeCount() );
                node.setH2OCount( model.getH2OMoleculeCount() );
                // labels
                node.setAcidLabel( solution.getSolute().getSymbol() );
                Solute solute = solution.getSolute();
                if ( solute instanceof Acid ) {
                    node.setBaseLabel( ((Acid)solution.getSolute()).getConjugateSymbol() );
                }
                else {
                    throw new IllegalStateException( "unexpected solute type: " + solute.getClass().getName() );
                }
            }
            else if ( concentrationModel instanceof WeakBaseConcentrationModel ) {
                WeakBaseMoleculeCountsNode node = countsNode.getWeakBaseNode();
                WeakBaseConcentrationModel model = (WeakBaseConcentrationModel) concentrationModel;
                // counts
                node.setAcidCount( model.getAcidMoleculeCount() );
                node.setBaseCount( model.getBaseMoleculeCount() );
                node.setH3OCount( model.getH3OMoleculeCount() );
                node.setOHCount( model.getOHMoleculeCount() );
                node.setH2OCount( model.getH2OMoleculeCount() );
                // labels
                node.setBaseLabel( solution.getSolute().getSymbol() );
                Solute solute = solution.getSolute();
                if ( solute instanceof WeakBase ) {
                    node.setAcidLabel( ((WeakBase)solution.getSolute()).getConjugateSymbol() );
                }
                else if ( solute instanceof CustomBase ) {
                    node.setAcidLabel( ((CustomBase)solution.getSolute()).getConjugateSymbol() );
                }
                else {
                    throw new IllegalStateException( "unexpected solute type: " + solute.getClass().getName() );
                }
            }
            else if ( concentrationModel instanceof StrongBaseConcentrationModel ) {
                StrongBaseMoleculeCountsNode node = countsNode.getStrongBaseNode();
                StrongBaseConcentrationModel model = (StrongBaseConcentrationModel) concentrationModel;
                // counts
                node.setBaseCount( model.getBaseMoleculeCount() );
                node.setMetalCount( model.getMetalMoleculeCount() );
                node.setH3OCount( model.getH3OMoleculeCount() );
                node.setOHCount( model.getOHMoleculeCount() );
                node.setH2OCount( model.getH2OMoleculeCount() );
                // labels
                node.setBaseLabel( solution.getSolute().getSymbol() );
                Solute solute = solution.getSolute();
                if ( solute instanceof StrongBase ) {
                    node.setMetalLabel( ((StrongBase)solution.getSolute()).getMetalSymbol() );
                }
                else if ( solute instanceof CustomBase ) {
                    node.setMetalLabel( ((CustomBase)solution.getSolute()).getMetalSymbol() );
                }
                else {
                    throw new IllegalStateException( "unexpected solute type: " + solute.getClass().getName() );
                }
            }
            else { 
                throw new UnsupportedOperationException( "unsupported concentration model type: " + concentrationModel.getClass().getName() );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    private static abstract class AbstractMoleculeCountsNode extends PinnedLayoutNode {
        
        private static final int ROWS = 5;

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
    }

    /*
     * Labels used in this view.
     */
    private static class LabelNode extends HTMLNode {
        
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
    private static class IconNode extends PComposite {

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
     * Displays a formatted number on a background.
     * If that number drops below some threshold, then "NEGLIGIBLE" is displayed.
     * This behavior can also be disabled.
     */
    private static class ValueNode extends PComposite {

        private final FormattedNumberNode numberNode;
        private final PNode numberBackgroundNode;
        private final PNode negligibleBackground;
        private boolean negligibleEnabled;
        private double negligibleThreshold;
        
        public ValueNode() {
            this( 0, VALUE_FORMAT_DEFAULT );
        }
        
        public ValueNode( double value ) {
            this( value, VALUE_FORMAT_DEFAULT );
        }

        public ValueNode( double value, NumberFormat format ) {
            // displays the count
            numberNode = new FormattedNumberNode( format, value, VALUE_FONT, VALUE_COLOR );
            numberBackgroundNode = new RectangularBackgroundNode( numberNode, VALUE_INSETS, VALUE_BACKGROUND_COLOR );
            addChild( numberBackgroundNode );
            // displays "NEGLIGIBLE"
            PText textNode = new PText( ABSStrings.VALUE_NEGLIGIBLE );
            textNode.setFont( NEGLIGIBLE_FONT );
            negligibleBackground = new RectangularBackgroundNode( textNode, VALUE_INSETS, VALUE_BACKGROUND_COLOR );
            addChild( negligibleBackground );
            // negligible mode is off by default
            negligibleEnabled = false;
            negligibleThreshold = 0;
        }
        
        public void setFormat( NumberFormat format ) {
            numberNode.setFormat( format );
        }

        public double getValue() {
            return numberNode.getValue();
        }
        
        public void setValue( double value ) {
            numberNode.setValue( value );
            updateVisibility();
        }

        public void setNegligibleEnabled( boolean enabled, double threshold ) {
            if ( enabled != negligibleEnabled || threshold != negligibleThreshold ) {
                negligibleEnabled = enabled;
                negligibleThreshold = threshold;
                updateVisibility();
            }
        }

        private void updateVisibility() {
            if ( negligibleEnabled ) {
                final double value = numberNode.getValue();
                numberBackgroundNode.setVisible( value > negligibleThreshold );
                negligibleBackground.setVisible( value <= negligibleThreshold );
            }
            else {
                numberBackgroundNode.setVisible( true );
                negligibleBackground.setVisible( false );
            }
        }
    }

    private abstract static class BaseMoleculeCountsNode extends AbstractMoleculeCountsNode {
        
        private static final int H3O_ROW = 2;
        private static final int OH_ROW = 3;
        private static final int H2O_ROW = 4;
        
        public BaseMoleculeCountsNode() {
            super();
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
    }
    
    public static class PureWaterCountsNode extends BaseMoleculeCountsNode {

        public PureWaterCountsNode() {
            super();
            setVisible( 0, false );
            setVisible( 1, false );
        }
    }

    public static class AcidMoleculeCountsNode extends BaseMoleculeCountsNode {
        
        private static int ACID_ROW = 0;
        private static int BASE_ROW = 1;

        public AcidMoleculeCountsNode() {
            super();
            setIconAndLabel( ACID_ROW, ABSImages.HA_MOLECULE, ABSSymbols.HA );
            setIconAndLabel( BASE_ROW, ABSImages.A_MINUS_MOLECULE, ABSSymbols.A );
            setNegligibleEnabled( ACID_ROW, true, 0 /* negligibleThreshold */ );
        }
        
        public void setAcidLabel( String text ) {
            setLabel( ACID_ROW, text );
        }

        public void setAcidCount( double count ) {
            setCount( ACID_ROW, count );
        }
        
        public void setBaseLabel( String text ) {
            setLabel( BASE_ROW, text );
        }

        public void setBaseCount( double count ) {
            setCount( BASE_ROW, count );
        }
    }

    public static class WeakBaseMoleculeCountsNode extends BaseMoleculeCountsNode {

        private static int BASE_ROW = 0;
        private static int ACID_ROW = 1;
        
        public WeakBaseMoleculeCountsNode() {
            super();
            setIconAndLabel( BASE_ROW, ABSImages.B_MOLECULE, ABSSymbols.B );
            setIconAndLabel( ACID_ROW, ABSImages.BH_PLUS_MOLECULE, ABSSymbols.BH_PLUS );
        }

        public void setBaseLabel( String text ) {
            setLabel( BASE_ROW, text );
        }
        
        public void setBaseCount( double count ) {
            setCount( BASE_ROW, count );
        }
        
        public void setAcidLabel( String text ) {
            setLabel( ACID_ROW, text );
        }

        public void setAcidCount( double count ) {
            setCount( ACID_ROW, count );
        }
    }

    public static class StrongBaseMoleculeCountsNode extends BaseMoleculeCountsNode {

        private static int BASE_ROW = 0;
        private static int METAL_ROW = 1;
        
        public StrongBaseMoleculeCountsNode() {
            super();
            setIconAndLabel( BASE_ROW, ABSImages.MOH_MOLECULE, ABSSymbols.MOH );
            setIconAndLabel( METAL_ROW, ABSImages.M_PLUS_MOLECULE, ABSSymbols.M_PLUS );
        }
        
        public void setBaseLabel( String text ) {
            setLabel( BASE_ROW, text );
        }

        public void setBaseCount( double count ) {
            setCount( BASE_ROW, count );
        }
        
        public void setMetalLabel( String text ) {
            setLabel( METAL_ROW, text );
        }

        public void setMetalCount( double count ) {
            setCount( METAL_ROW, count );
        }
    }
}
