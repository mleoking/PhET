
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
                node.setCountH3O( concentrationModel.getH3OMoleculeCount() );
                node.setCountOH( concentrationModel.getOHMoleculeCount() );
                node.setCountH2O( concentrationModel.getH2OMoleculeCount() );
            }
            else if ( concentrationModel instanceof AcidConcentrationModel ) {
                AcidMoleculeCountsNode node = countsNode.getAcidNode();
                AcidConcentrationModel model = (AcidConcentrationModel) concentrationModel;
                // counts
                node.setAcidCount( model.getAcidMoleculeCount() );
                node.setBaseCount( model.getBaseMoleculeCount() );
                node.setCountH3O( model.getH3OMoleculeCount() );
                node.setCountOH( model.getOHMoleculeCount() );
                node.setCountH2O( model.getH2OMoleculeCount() );
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
                node.setCountH3O( model.getH3OMoleculeCount() );
                node.setCountOH( model.getOHMoleculeCount() );
                node.setCountH2O( model.getH2OMoleculeCount() );
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
                node.setCountH3O( model.getH3OMoleculeCount() );
                node.setCountOH( model.getOHMoleculeCount() );
                node.setCountH2O( model.getH2OMoleculeCount() );
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
    
    private static abstract class AbstractMoleculeCountsNode extends PinnedLayoutNode {

        private final NegligibleValueNode countLHS;
        private final ValueNode countRHS, countH3OPlus, countOHMinus, countH2O;
        private final IconNode iconLHS, iconRHS, iconH3OPlus, iconOHMinus, iconH2O;
        private final LabelNode labelLHS, labelRHS, labelH3OPlus, labelOHMinus, labelH2O;

        public AbstractMoleculeCountsNode( Image imageLHS, String textLHS, Image imageRHS, String textRHS ) {
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
            iconLHS = new IconNode( imageLHS );
            iconRHS = new IconNode( imageRHS );
            iconH3OPlus = new IconNode( ABSImages.H3O_PLUS_MOLECULE );
            iconOHMinus = new IconNode( ABSImages.OH_MINUS_MOLECULE );
            iconH2O = new IconNode( ABSImages.H2O_MOLECULE );
            PNode[] iconNodes = { iconLHS, iconRHS, iconH3OPlus, iconOHMinus, iconH2O };

            // labels
            labelLHS = new LabelNode( textLHS );
            labelRHS = new LabelNode( textRHS );
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

        protected void setCountLHS( double count ) {
            countLHS.setValue( count );
        }

        protected void setCountRHS( double count ) {
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

        protected void setNegligibleEnabled( boolean enabled ) {
            countLHS.setNegligibleEnabled( enabled );
        }
        
        protected void setTextLHS( String text ) {
            labelLHS.setHTML( text );
        }
        
        protected void setTextRHS( String text ) {
            labelRHS.setHTML( text );
        }
        
        protected void setCountLHSVisible( boolean visible ) {
            countLHS.setVisible( visible );
        }
        
        protected void setCountRHSVisible( boolean visible ) {
            countRHS.setVisible( visible );
        }
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
            super.setHTML( HTMLUtils.toHTMLString( html ) );
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
            System.out.println( "ValueNode.setValue value=" + value );//XXX
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
    
    public static class PureWaterCountsNode extends AbstractMoleculeCountsNode {

        public PureWaterCountsNode() {
            super( null, "", null, "" );
            setCountLHSVisible( false );
            setCountRHSVisible( false );
        }
    }

    public static class AcidMoleculeCountsNode extends AbstractMoleculeCountsNode {

        public AcidMoleculeCountsNode() {
            super( ABSImages.HA_MOLECULE, ABSSymbols.HA, ABSImages.A_MINUS_MOLECULE, ABSSymbols.A_MINUS );
        }
        
        public void setAcidLabel( String text ) {
            setTextLHS( text );
        }

        public void setAcidCount( int count ) {
            setCountLHS( count );
        }
        
        public void setBaseLabel( String text ) {
            setTextRHS( text );
        }

        public void setBaseCount( int count ) {
            setCountRHS( count );
        }
    }

    public static class WeakBaseMoleculeCountsNode extends AbstractMoleculeCountsNode {

        public WeakBaseMoleculeCountsNode() {
            super( ABSImages.B_MOLECULE, ABSSymbols.B, ABSImages.BH_PLUS_MOLECULE, ABSSymbols.BH_PLUS );
        }

        public void setBaseLabel( String text ) {
            setTextLHS( text );
        }
        
        public void setBaseCount( int count ) {
            setCountLHS( count );
        }
        
        public void setAcidLabel( String text ) {
            setTextRHS( text );
        }

        public void setAcidCount( int count ) {
            setCountRHS( count );
        }
    }

    public static class StrongBaseMoleculeCountsNode extends AbstractMoleculeCountsNode {

        public StrongBaseMoleculeCountsNode() {
            super( ABSImages.MOH_MOLECULE, ABSSymbols.MOH, ABSImages.M_PLUS_MOLECULE, ABSSymbols.M_PLUS );
        }
        
        public void setBaseLabel( String text ) {
            setTextLHS( text );
        }

        public void setBaseCount( int count ) {
            setCountLHS( count );
        }
        
        public void setMetalLabel( String text ) {
            setTextRHS( text );
        }

        public void setMetalCount( int count ) {
            setCountRHS( count );
        }
    }
}
