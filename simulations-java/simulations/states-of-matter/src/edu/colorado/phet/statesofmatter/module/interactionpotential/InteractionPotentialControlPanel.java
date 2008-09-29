/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.interactionpotential;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.DualParticleModel;
import edu.colorado.phet.statesofmatter.view.ParticleForceNode;


/**
 * This class implements the control panel for the Interaction Potential
 * portion of this simulation.
 *
 * @author John Blanco
 */
public class InteractionPotentialControlPanel extends ControlPanel {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
    private static final Font LABEL_FONT = new PhetFont( Font.PLAIN, 14 );
    private static final Color ENABLED_TITLE_COLOR = new Color ( 128, 128, 128 );
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private DualParticleModel m_model;
    private InteractionPotentialCanvas m_canvas;
    private AtomSelectionPanel m_moleculeSelectionPanel;
    private AtomDiameterControlPanel m_atomDiameterControlPanel;
    private InteractionStrengthControlPanel m_interactionStrengthControlPanel;
    private ForceControlPanel m_forceControlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public InteractionPotentialControlPanel( InteractionPotentialModule solidLiquidGasModule, Frame parentFrame ) {
        
        super();
        m_model = solidLiquidGasModule.getDualParticleModel();
        m_canvas = solidLiquidGasModule.getCanvas();
        
        m_model.addListener( new DualParticleModel.Adapter(){
            public void moleculeTypeChanged(){
                m_moleculeSelectionPanel.updateMoleculeType();
            };
        });
        
        // Set the control panel's minimum width.
        int minimumWidth = StatesOfMatterResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create the panel for controlling the atom diameter.
        m_atomDiameterControlPanel = new AtomDiameterControlPanel( m_model );
        
        // Create the panel for controlling the interaction strength.
        m_interactionStrengthControlPanel = new InteractionStrengthControlPanel( m_model );
        
        // Create the panel that allows the user to select molecule type.
        m_moleculeSelectionPanel = new AtomSelectionPanel();

        // Create the panel that allows the user to control which forces are
        // shown on the atoms.
        m_forceControlPanel = new ForceControlPanel();
        
        // Add the panels we just created.
        addControlFullWidth( m_moleculeSelectionPanel );
        addControlFullWidth( m_atomDiameterControlPanel );
        addControlFullWidth( m_interactionStrengthControlPanel );
        addControlFullWidth( m_forceControlPanel );
        
        // Add a reset button.
        addVerticalSpace( 10 );
        JButton resetButton = new JButton( StatesOfMatterStrings.RESET );
        resetButton.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent event ){
                m_model.reset();
                m_forceControlPanel.reset();
            }
        });
        addControl( resetButton );
    }
    
    //----------------------------------------------------------------------------
    // Inner Classes
    //----------------------------------------------------------------------------

    /**
     * This class defines the selection panel that allows the user to choose
     * the type of molecule.
     */
    private class AtomSelectionPanel extends JPanel {
        
        private JRadioButton m_neonRadioButton;
        private JRadioButton m_argonRadioButton;
        private JRadioButton m_adjustableAttractionRadioButton;
        private boolean m_adjustableAtomSelected;
        
        AtomSelectionPanel(){
            
            m_adjustableAtomSelected = false;
            
            setLayout( new GridLayout(0, 1) );
            
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    StatesOfMatterStrings.INTERACTION_POTENTIAL_ATOM_SELECT_LABEL,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    Color.GRAY );
            
            setBorder( titledBorder );

            m_neonRadioButton = new JRadioButton( StatesOfMatterStrings.NEON_SELECTION_LABEL );
            m_neonRadioButton.setFont( LABEL_FONT );
            m_neonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if (m_model.getMoleculeType() != StatesOfMatterConstants.NEON){
                        m_model.setMoleculeType( StatesOfMatterConstants.NEON );
                        m_adjustableAtomSelected = false;
                        updateLjControlSliderState();
                    }
                }
            } );
            m_argonRadioButton = new JRadioButton( StatesOfMatterStrings.ARGON_SELECTION_LABEL );
            m_argonRadioButton.setFont( LABEL_FONT );
            m_argonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if (m_model.getMoleculeType() != StatesOfMatterConstants.ARGON){
                        m_model.setMoleculeType( StatesOfMatterConstants.ARGON );
                        m_adjustableAtomSelected = false;
                        updateLjControlSliderState();
                    }
                }
            } );
            m_adjustableAttractionRadioButton = 
                new JRadioButton( StatesOfMatterStrings.ADJUSTABLE_ATTRACTION_SELECTION_LABEL );
            m_adjustableAttractionRadioButton.setFont( LABEL_FONT );
            m_adjustableAttractionRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if (m_model.getMoleculeType() != StatesOfMatterConstants.USER_DEFINED_MOLECULE){
                        m_model.setMoleculeType( StatesOfMatterConstants.USER_DEFINED_MOLECULE );
                        m_adjustableAtomSelected = true;
                        updateLjControlSliderState();
                    }
                }
            } );

            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( m_neonRadioButton );
            buttonGroup.add( m_argonRadioButton );
            buttonGroup.add( m_adjustableAttractionRadioButton );
            m_neonRadioButton.setSelected( true );
            
            add( m_neonRadioButton );
            add( m_argonRadioButton );
            add( m_adjustableAttractionRadioButton );
            
            updateLjControlSliderState();
        }
        
        /**
         * Update the selected molecule to match whatever the model believes
         * it to be.
         */
        public void updateMoleculeType(){
            int moleculeType = m_model.getMoleculeType();
            
            switch (moleculeType){
            case StatesOfMatterConstants.NEON:
                m_neonRadioButton.setSelected( true );
                m_adjustableAtomSelected = false;
                break;
            case StatesOfMatterConstants.ARGON:
                m_argonRadioButton.setSelected( true );
                m_adjustableAtomSelected = false;
                break;
            default:
                m_adjustableAttractionRadioButton.setSelected( true );
                m_adjustableAtomSelected = true;
                break;
            }
            
            updateLjControlSliderState();
        }
        
        /**
         * Update the state (i.e. enabled or disabled) of the sliders that
         * control the Lennard-Jones parameters.
         * 
         */
        private void updateLjControlSliderState(){
            if (m_atomDiameterControlPanel != null){
                m_atomDiameterControlPanel.setEnabled( m_adjustableAtomSelected );
            }
            if (m_interactionStrengthControlPanel != null){
                m_interactionStrengthControlPanel.setEnabled( m_adjustableAtomSelected );
            }
        }
    }
    
    private class AtomDiameterControlPanel extends JPanel {
        
        private final Font LABEL_FONT = new PhetFont(14, false);

        private LinearValueControl m_atomDiameterControl;
        private DualParticleModel m_model;
        private TitledBorder m_titledBorder;
        private JLabel m_leftLabel;
        private JLabel m_rightLabel;
        
        public AtomDiameterControlPanel(DualParticleModel model){

            m_model = model;
            
            setLayout( new GridLayout(0, 1) );

            // Create the border.
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            m_titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    StatesOfMatterStrings.ATOM_DIAMETER_CONTROL_TITLE,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    ENABLED_TITLE_COLOR );
            
            setBorder( m_titledBorder );
            
            // Add the control slider.
            m_atomDiameterControl = new LinearValueControl( StatesOfMatterConstants.MIN_SIGMA,
                    StatesOfMatterConstants.MAX_SIGMA, "", "0", "", new SliderLayoutStrategy() );
            m_atomDiameterControl.setValue( m_model.getSigma() );
            m_atomDiameterControl.setUpDownArrowDelta( 0.01 );
            m_atomDiameterControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    m_model.setSigma( m_atomDiameterControl.getValue() );
                }
            });
            Hashtable diameterControlLabelTable = new Hashtable();
            m_leftLabel = new JLabel(StatesOfMatterStrings.ATOM_DIAMETER_SMALL);
            m_leftLabel.setFont( LABEL_FONT );
            diameterControlLabelTable.put( new Double( m_atomDiameterControl.getMinimum() ), m_leftLabel );
            m_rightLabel = new JLabel(StatesOfMatterStrings.ATOM_DIAMETER_LARGE);
            m_rightLabel.setFont( LABEL_FONT );
            diameterControlLabelTable.put( new Double( m_atomDiameterControl.getMaximum() ), m_rightLabel );
            m_atomDiameterControl.setTickLabels( diameterControlLabelTable );
            m_atomDiameterControl.setValue(m_model.getSigma());
            add(m_atomDiameterControl);

            // Register as a listener with the model for relevant events.
            m_model.addListener( new DualParticleModel.Adapter(){
                public void particleDiameterChanged(){
                    m_atomDiameterControl.setValue(m_model.getSigma());
                }
            });
            
        }

        public void setEnabled( boolean enabled ){
            super.setEnabled( enabled );
            m_atomDiameterControl.setEnabled( enabled );
            m_leftLabel.setEnabled( enabled );
            m_rightLabel.setEnabled( enabled );
            if (enabled) {
                m_titledBorder.setTitleColor( ENABLED_TITLE_COLOR );
            }
            else {
                m_titledBorder.setTitleColor( Color.LIGHT_GRAY );
            }
        }
    }
    
    private class InteractionStrengthControlPanel extends JPanel {
        
        private final Font LABEL_FONT = new PhetFont(14, false);

        private LinearValueControl m_interactionStrengthControl;
        private DualParticleModel m_model;
        private TitledBorder m_titledBorder;
        private JLabel m_leftLabel;
        private JLabel m_rightLabel;

        public InteractionStrengthControlPanel(DualParticleModel model){


            m_model = model;
            
            setLayout( new GridLayout(0, 1) );

            // Create the border.
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            m_titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    StatesOfMatterStrings.INTERACTION_STRENGTH_CONTROL_TITLE,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    ENABLED_TITLE_COLOR );
            
            setBorder( m_titledBorder );
            
            // Add the control slider.
            m_interactionStrengthControl = new LinearValueControl( StatesOfMatterConstants.MIN_EPSILON,
                    StatesOfMatterConstants.MAX_EPSILON, "", "0", "", new SliderLayoutStrategy() );
            m_interactionStrengthControl.setValue( m_model.getEpsilon() );
            m_interactionStrengthControl.setUpDownArrowDelta( 0.01 );
            m_interactionStrengthControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    // Set the interaction strength in the model.
                    m_model.setEpsilon( m_interactionStrengthControl.getValue() );
                }
            });
            Hashtable diameterControlLabelTable = new Hashtable();
            m_leftLabel = new JLabel(StatesOfMatterStrings.INTERACTION_STRENGTH_WEAK);
            m_leftLabel.setFont( LABEL_FONT );
            diameterControlLabelTable.put( new Double( m_interactionStrengthControl.getMinimum() ), m_leftLabel );
            m_rightLabel = new JLabel(StatesOfMatterStrings.INTERACTION_STRENGTH_STRONG);
            m_rightLabel.setFont( LABEL_FONT );
            diameterControlLabelTable.put( new Double( m_interactionStrengthControl.getMaximum() ), m_rightLabel );
            m_interactionStrengthControl.setTickLabels( diameterControlLabelTable );

            // Register as a listener with the model so that we know when the
            // settings for potential are changed.
            m_model.addListener( new DualParticleModel.Adapter(){
                public void interactionPotentialChanged(){
                    m_interactionStrengthControl.setValue( m_model.getEpsilon() );
                }
            });
            
            add(m_interactionStrengthControl);
        }
        
        public void setEnabled( boolean enabled ){
            super.setEnabled( enabled );
            m_interactionStrengthControl.setEnabled( enabled );
            m_leftLabel.setEnabled( enabled );
            m_rightLabel.setEnabled( enabled );
            if (enabled) {
                m_titledBorder.setTitleColor( ENABLED_TITLE_COLOR );
            }
            else {
                m_titledBorder.setTitleColor( Color.LIGHT_GRAY );
            }
        }
    }
    
    private class ForceControlPanel extends VerticalLayoutPanel {

        private JRadioButton m_noForcesButton;
        private JRadioButton m_totalForcesButton;
        private JRadioButton m_componentForceButton;
        private JLabel m_attractiveForceLegendEntry;
        private JLabel m_repulsiveForceLegendEntry;
        
        public ForceControlPanel(){
            
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    StatesOfMatterStrings.INTERACTION_POTENTIAL_SHOW_FORCES,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    Color.GRAY );
            
            setBorder( titledBorder );

            m_noForcesButton = new JRadioButton( StatesOfMatterStrings.INTERACTION_POTENTIAL_HIDE_FORCES );
            m_noForcesButton.setFont( LABEL_FONT );
            m_noForcesButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_canvas.setShowAttractiveForces( false );
                    m_canvas.setShowRepulsiveForces( false );
                    m_canvas.setShowTotalForces( false );
                }
            } );
            
            m_totalForcesButton = new JRadioButton( StatesOfMatterStrings.INTERACTION_POTENTIAL_TOTAL_FORCES);
            m_totalForcesButton.setFont( LABEL_FONT );
            m_totalForcesButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_canvas.setShowAttractiveForces( false );
                    m_canvas.setShowRepulsiveForces( false );
                    m_canvas.setShowTotalForces( true );
                }
            } );
            JPanel totalForceButtonPanel = new JPanel();
            totalForceButtonPanel.setLayout( new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0) );
            totalForceButtonPanel.add( m_totalForcesButton );
            totalForceButtonPanel.add( new JLabel( createArrowIcon( ParticleForceNode.TOTAL_FORCE_COLOR, false ) ) );
            
            m_componentForceButton = 
                new JRadioButton( StatesOfMatterStrings.INTERACTION_POTENTIAL_COMPONENT_FORCES );
            m_componentForceButton.setFont( LABEL_FONT );
            m_componentForceButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_canvas.setShowAttractiveForces( true );
                    m_canvas.setShowRepulsiveForces( true );
                    m_canvas.setShowTotalForces( false );
                }
            } );

            // Group the buttons logically (not physically) together and set initial state.
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( m_noForcesButton );
            buttonGroup.add( m_totalForcesButton );
            buttonGroup.add( m_componentForceButton );
            m_noForcesButton.setSelected( true );
            
            JPanel spacePanel = new JPanel();
            spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.X_AXIS ) );
            spacePanel.add( Box.createHorizontalStrut( 14 ) );

            JPanel attractiveForceLegendEntry = new JPanel();
            attractiveForceLegendEntry.setLayout( new java.awt.FlowLayout(java.awt.FlowLayout.LEFT) );
            m_attractiveForceLegendEntry = new JLabel(StatesOfMatterStrings.ATTRACTIVE_FORCE_KEY, 
                    createArrowIcon( ParticleForceNode.ATTRACTIVE_FORCE_COLOR, false ), JLabel.LEFT);
            attractiveForceLegendEntry.add( spacePanel );
            attractiveForceLegendEntry.add( m_attractiveForceLegendEntry );
            
            spacePanel = new JPanel();
            spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.X_AXIS ) );
            spacePanel.add( Box.createHorizontalStrut( 14 ) );

            JPanel repulsiveForceLegendEntry = new JPanel();
            repulsiveForceLegendEntry.setLayout( new java.awt.FlowLayout(java.awt.FlowLayout.LEFT) );
            m_repulsiveForceLegendEntry = new JLabel(StatesOfMatterStrings.REPULSIVE_FORCE_KEY, 
                    createArrowIcon( ParticleForceNode.REPULSIVE_FORCE_COLOR, true ), JLabel.LEFT);
            repulsiveForceLegendEntry.add( spacePanel );
            repulsiveForceLegendEntry.add( m_repulsiveForceLegendEntry );
            
            // Add the components to the main panel.
            add( m_noForcesButton );
            add( totalForceButtonPanel );
            add( m_componentForceButton );
            add( attractiveForceLegendEntry );
            add( repulsiveForceLegendEntry );
        }
        
        private void reset() {
            m_noForcesButton.setSelected( true );
            m_canvas.setShowAttractiveForces( false );
            m_canvas.setShowRepulsiveForces( false );
            m_canvas.setShowTotalForces( false );
        }
        
        private ImageIcon createArrowIcon( Color color, boolean pointRight ) {
            ArrowNode arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(20, 0), 10, 15, 6 );
            arrowNode.setPaint( color );
            Image arrowImage = arrowNode.toImage();
            if (!pointRight) {
                arrowImage = BufferedImageUtils.flipX( BufferedImageUtils.toBufferedImage( arrowImage ) );
            }
            return( new ImageIcon(arrowImage)) ;
        }
    }

    /**
     * Layout strategy for sliders.
     */
    public class SliderLayoutStrategy implements ILayoutStrategy {

        public SliderLayoutStrategy() {}
        
        public void doLayout( AbstractValueControl valueControl ) {

            // Get the components that will be part of the layout
            JComponent slider = valueControl.getSlider();

            EasyGridBagLayout layout = new EasyGridBagLayout( valueControl );
            valueControl.setLayout( layout );
            layout.addFilledComponent( slider, 1, 0, GridBagConstraints.HORIZONTAL );
        }
    }
}
