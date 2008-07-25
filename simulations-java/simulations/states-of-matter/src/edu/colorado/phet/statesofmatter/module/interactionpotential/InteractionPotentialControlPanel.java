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
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.control.GravityControlPanel.GravitySliderLayoutStrategy;
import edu.colorado.phet.statesofmatter.model.DualParticleModel;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;


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
        
        // Add the panel that allows the user to select molecule type.
        addControlFullWidth( new MoleculeSelectionPanel() );
        
        // Add the panel for controlling the atom diameter.
        addControlFullWidth( new AtomDiameterControlPanel(m_model) );
        
        // Add the panel for controlling the interaction strength.
        addControlFullWidth( new InteractionStrengthControlPanel(m_model) );
        
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
        
        MoleculeSelectionPanel(){
            
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
                    }
                }
            } );
            m_argonRadioButton = new JRadioButton( StatesOfMatterStrings.ARGON_SELECTION_LABEL );
            m_argonRadioButton.setFont( LABEL_FONT );
            m_argonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if (m_model.getMoleculeType() != DualParticleModel.ARGON){
                        m_model.setMoleculeType( DualParticleModel.ARGON );
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
        }
    }
    
    private class AtomDiameterControlPanel extends JPanel {
        
        private final Font LABEL_FONT = new PhetFont( Font.BOLD, 14 );
        private final double MAX_ATOM_DIAMETER = 500; // JPB TBD - Total guess, tweak as needed.

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
            m_atomDiameterControl = new LinearValueControl( 0, MAX_ATOM_DIAMETER, "", "0", "", new SliderLayoutStrategy() );
            m_atomDiameterControl.setValue( m_model.getCurrentMoleculeDiameter() );
            m_atomDiameterControl.setUpDownArrowDelta( 0.01 );
            m_atomDiameterControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    // TODO: JPB TBD - ability the change this is not implemented yet.
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

            // Register as a listener with the model so that we know when it gets
            // reset.
            m_model.addListener( new DualParticleModel.Adapter(){
                public void resetOccurred(){
                    m_atomDiameterControl.setValue(m_model.getCurrentMoleculeDiameter());
                }
            });
            
            add(m_atomDiameterControl);
        }
    }
    
    private class InteractionStrengthControlPanel extends JPanel {
        
        private final Font LABEL_FONT = new PhetFont( Font.BOLD, 14 );
        private final double MAX_FIELD_STRENGTH = 50; // JPB TBD - Total guess, tweak as needed.

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
            m_interactionStrengthControl = new LinearValueControl( 0, MAX_FIELD_STRENGTH, "", "0", "", new SliderLayoutStrategy() );
            m_interactionStrengthControl.setValue( m_model.getCurrentMoleculeDiameter() );
            m_interactionStrengthControl.setUpDownArrowDelta( 0.01 );
            m_interactionStrengthControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    // TODO: JPB TBD - ability to change this is not implemented yet.
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
                public void resetOccurred(){
                    // TODO: JPB TBD - Need to put this in place once the model supports it.
                }
            });
            
            add(m_interactionStrengthControl);
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
