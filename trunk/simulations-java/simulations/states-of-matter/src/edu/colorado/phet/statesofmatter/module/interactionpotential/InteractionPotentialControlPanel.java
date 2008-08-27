/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.interactionpotential;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.DualParticleModel;


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
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private DualParticleModel m_model;
    private MoleculeSelectionPanel m_moleculeSelectionPanel;
    private AtomDiameterControlPanel m_atomDiameterControlPanel;
    private InteractionStrengthControlPanel m_interactionStrengthControlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public InteractionPotentialControlPanel( InteractionPotentialModule solidLiquidGasModule, Frame parentFrame ) {
        
        super();
        m_model = solidLiquidGasModule.getDualParticleModel();
        
        // Set the control panel's minimum width.
        int minimumWidth = StatesOfMatterResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create the panel for controlling the atom diameter.
        m_atomDiameterControlPanel = new AtomDiameterControlPanel( m_model );
        
        // Create the panel for controlling the interaction strength.
        m_interactionStrengthControlPanel = new InteractionStrengthControlPanel( m_model );
        
        // Create the panel that allows the user to select molecule type.
        m_moleculeSelectionPanel = new MoleculeSelectionPanel();
        
        // Add the panels we just created.
        addControlFullWidth( m_moleculeSelectionPanel );
        addControlFullWidth( m_atomDiameterControlPanel );
        addControlFullWidth( m_interactionStrengthControlPanel );
        
        // Add the check box for showing/hiding the force arrows.
        addVerticalSpace( 10 );
        JCheckBox showForcesCheckbox = new JCheckBox();
        showForcesCheckbox.setFont( LABEL_FONT );
        showForcesCheckbox.setText( StatesOfMatterStrings.SHOW_FORCES );
        addControl( showForcesCheckbox );
        
        // Add the Reset All button.
        addVerticalSpace( 10 );
        addResetAllButton( solidLiquidGasModule );
    }
    
    //----------------------------------------------------------------------------
    // Inner Classes
    //----------------------------------------------------------------------------

    /**
     * This class defines the selection panel that allows the user to choose
     * the type of molecule.
     */
    private class MoleculeSelectionPanel extends JPanel {
        
        private JRadioButton m_monatomicOxygenRadioButton;
        private JRadioButton m_argonRadioButton;
        private JRadioButton m_adjustableAttractionRadioButton;
        private boolean m_adjustableAtomSelected;
        
        MoleculeSelectionPanel(){
            
            m_adjustableAtomSelected = false;
            
            setLayout( new GridLayout(0, 1) );
            
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    StatesOfMatterStrings.MOLECULE_TYPE_SELECT_LABEL,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    Color.GRAY );
            
            setBorder( titledBorder );

            m_monatomicOxygenRadioButton = new JRadioButton( StatesOfMatterStrings.MONATOMIC_OXYGEN_SELECTION_LABEL );
            m_monatomicOxygenRadioButton.setFont( LABEL_FONT );
            m_monatomicOxygenRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if (m_model.getMoleculeType() != DualParticleModel.MONATOMIC_OXYGEN){
                        m_model.setMoleculeType( DualParticleModel.MONATOMIC_OXYGEN );
                        m_adjustableAtomSelected = false;
                        updateLjControlSliderState();
                    }
                }
            } );
            m_argonRadioButton = new JRadioButton( StatesOfMatterStrings.ARGON_SELECTION_LABEL );
            m_argonRadioButton.setFont( LABEL_FONT );
            m_argonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if (m_model.getMoleculeType() != DualParticleModel.ARGON){
                        m_model.setMoleculeType( DualParticleModel.ARGON );
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
                    // TODO: JPB TBD - Adjustable attraction not yet implemented in the model, update this when it is.
                    if (m_model.getMoleculeType() != DualParticleModel.NEON){
                        m_model.setMoleculeType( DualParticleModel.NEON );
                        m_adjustableAtomSelected = true;
                        updateLjControlSliderState();
                    }
                }
            } );

            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( m_monatomicOxygenRadioButton );
            buttonGroup.add( m_argonRadioButton );
            buttonGroup.add( m_adjustableAttractionRadioButton );
            m_monatomicOxygenRadioButton.setSelected( true );
            
            add( m_monatomicOxygenRadioButton );
            add( m_argonRadioButton );
            add( m_adjustableAttractionRadioButton );
            
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
        
        private final Font LABEL_FONT = new PhetFont( Font.BOLD, 14 );

        private LinearValueControl m_atomDiameterControl;
        
        private DualParticleModel m_model;
        
        public AtomDiameterControlPanel(DualParticleModel model){

            m_model = model;
            
            setLayout( new GridLayout(0, 1) );

            // Create the border.
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    StatesOfMatterStrings.ATOM_DIAMETER_CONTROL_TITLE,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    Color.GRAY );
            
            setBorder( titledBorder );
            
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
            JLabel leftLabel = new JLabel("Small"); // TODO: JPB TBD - Turn into resource if kept.
            leftLabel.setFont( LABEL_FONT );
            diameterControlLabelTable.put( new Double( m_atomDiameterControl.getMinimum() ), leftLabel );
            JLabel rightLabel = new JLabel("Large"); // TODO: JPB TBD - Turn into resource if kept.
            rightLabel.setFont( LABEL_FONT );
            diameterControlLabelTable.put( new Double( m_atomDiameterControl.getMaximum() ), rightLabel );
            m_atomDiameterControl.setTickLabels( diameterControlLabelTable );
            m_atomDiameterControl.setValue(m_model.getSigma());

            // Register as a listener with the model for relevant events.
            m_model.addListener( new DualParticleModel.Adapter(){
                public void particleDiameterChanged(){
                    m_atomDiameterControl.setValue(m_model.getSigma());
                }
            });
            
            add(m_atomDiameterControl);
        }

        public void setEnabled( boolean enabled ){
            super.setEnabled( enabled );
            m_atomDiameterControl.setEnabled( enabled );
        }
    }
    
    private class InteractionStrengthControlPanel extends JPanel {
        
        private final Font LABEL_FONT = new PhetFont( Font.BOLD, 14 );

        private LinearValueControl m_interactionStrengthControl;
        
        private DualParticleModel m_model;
        
        public InteractionStrengthControlPanel(DualParticleModel model){


            m_model = model;
            
            setLayout( new GridLayout(0, 1) );

            // Create the border.
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    StatesOfMatterStrings.INTERACTION_STRENGTH_CONTROL_TITLE,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    Color.GRAY );
            
            setBorder( titledBorder );
            
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
            JLabel leftLabel = new JLabel("Weak"); // TODO: JPB TBD - Turn into resource if kept.
            leftLabel.setFont( LABEL_FONT );
            diameterControlLabelTable.put( new Double( m_interactionStrengthControl.getMinimum() ), leftLabel );
            JLabel rightLabel = new JLabel("Strong"); // TODO: JPB TBD - Turn into resource if kept.
            rightLabel.setFont( LABEL_FONT );
            diameterControlLabelTable.put( new Double( m_interactionStrengthControl.getMaximum() ), rightLabel );
            m_interactionStrengthControl.setTickLabels( diameterControlLabelTable );

            // Register as a listener with the model so that we know when it gets
            // reset.
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
