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
import javax.swing.JButton;
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
    private InteractionPotentialCanvas m_canvas;
    private MoleculeSelectionPanel m_moleculeSelectionPanel;
    private AtomDiameterControlPanel m_atomDiameterControlPanel;
    private InteractionStrengthControlPanel m_interactionStrengthControlPanel;
    private JCheckBox m_showAttractiveForcesCheckbox;
    private JCheckBox m_showRepulsiveForcesCheckbox;
    
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
        m_moleculeSelectionPanel = new MoleculeSelectionPanel();
        
        // Add the panels we just created.
        addControlFullWidth( m_moleculeSelectionPanel );
        addControlFullWidth( m_atomDiameterControlPanel );
        addControlFullWidth( m_interactionStrengthControlPanel );
        
        // Add the check box for showing/hiding the the attractive force arrows.
        addVerticalSpace( 10 );
        m_showAttractiveForcesCheckbox = new JCheckBox();
        m_showAttractiveForcesCheckbox.setFont( LABEL_FONT );
        m_showAttractiveForcesCheckbox.setText( StatesOfMatterStrings.SHOW_ATTRACTIVE_FORCES );
        m_showAttractiveForcesCheckbox.setSelected( false );
        addControl( m_showAttractiveForcesCheckbox );
        
        m_showAttractiveForcesCheckbox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                m_canvas.setShowAttractiveForces( m_showAttractiveForcesCheckbox.isSelected() );
            }
        } );

        // Add the check box for showing/hiding the the repulsive force arrows.
        addVerticalSpace( 10 );
        m_showRepulsiveForcesCheckbox = new JCheckBox();
        m_showRepulsiveForcesCheckbox.setFont( LABEL_FONT );
        m_showRepulsiveForcesCheckbox.setText( StatesOfMatterStrings.SHOW_REPULSIVE_FORCES );
        m_showRepulsiveForcesCheckbox.setSelected( false );
        addControl( m_showRepulsiveForcesCheckbox );
        
        m_showRepulsiveForcesCheckbox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                m_canvas.setShowRepulsiveForces( m_showRepulsiveForcesCheckbox.isSelected() );
            }
        } );

        // Add a reset button.
        addVerticalSpace( 10 );
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent event ){
                m_model.reset();
                m_showAttractiveForcesCheckbox.setSelected( false );
                m_showRepulsiveForcesCheckbox.setSelected( false );
                m_canvas.setShowAttractiveForces( m_showAttractiveForcesCheckbox.isSelected() );
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
                    StatesOfMatterStrings.INTERACTION_POTENTIAL_ATOM_SELECT_LABEL,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    Color.GRAY );
            
            setBorder( titledBorder );

            m_monatomicOxygenRadioButton = new JRadioButton( StatesOfMatterStrings.MONATOMIC_OXYGEN_SELECTION_LABEL );
            m_monatomicOxygenRadioButton.setFont( LABEL_FONT );
            m_monatomicOxygenRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if (m_model.getMoleculeType() != StatesOfMatterConstants.MONATOMIC_OXYGEN){
                        m_model.setMoleculeType( StatesOfMatterConstants.MONATOMIC_OXYGEN );
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
         * Update the selected molecule to match whatever the model believes
         * it to be.
         */
        public void updateMoleculeType(){
            int moleculeType = m_model.getMoleculeType();
            
            switch (moleculeType){
            case StatesOfMatterConstants.MONATOMIC_OXYGEN:
                m_monatomicOxygenRadioButton.setSelected( true );
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
