/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.phasechanges;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;


public class PhaseChangesControlPanel extends ControlPanel {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    private static final Font BUTTON_LABEL_FONT = new PhetFont(14);
    private static final int INTERACTION_POTENTIAL_DIAGRAM_WIDTH = 200;
    private static final int INTERACTION_POTENTIAL_DIAGRAM_HEIGHT = (int)(INTERACTION_POTENTIAL_DIAGRAM_WIDTH * 0.8);
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private MultipleParticleModel m_model;
    private JPanel m_phaseDiagramPanel;
    private boolean m_phaseDiagramVisible;
    private JButton m_phaseDiagramCtrlButton;
    private JPanel m_interactionDiagramPanel;
    private boolean m_interactionDiagramVisible;
    private JButton m_interactionDiagramCtrlButton;
    private InteractionPotentialDiagramNode m_interactionPotentialDiagram;
    private PhaseDiagram m_phaseDiagram;
    private MoleculeSelectionPanel m_moleculeSelectionPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param phaseChangesModule
     * @param parentFrame parent frame, for creating dialogs
     */
    public PhaseChangesControlPanel( PhaseChangesModule phaseChangesModule, Frame parentFrame ) {
        
        super();
        m_model = phaseChangesModule.getMultiParticleModel();
        m_phaseDiagramVisible = true;
        m_interactionDiagramVisible = true;
        
        // Register with the model for events that affect the diagrams on this panel.
        m_model.addListener( new MultipleParticleModel.Adapter(){
            public void moleculeTypeChanged(){
                m_interactionPotentialDiagram.setLjPotentialParameters( m_model.getSigma(), m_model.getEpsilon() );
                m_moleculeSelectionPanel.setMolecule(m_model.getMoleculeType());
            }
            public void temperatureChanged(){
                updatePhaseDiagram();
            }
            public void pressureChanged(){
                updatePhaseDiagram();
            }
        });
        
        // Set the control panel's minimum width.
        int minimumWidth = StatesOfMatterResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Add the panel that allows the user to select molecule type.
        m_moleculeSelectionPanel = new MoleculeSelectionPanel();
        addControlFullWidth( m_moleculeSelectionPanel );
        
        // Add a little spacing.
        addVerticalSpace( 10 );
        
        // Add the button that allows the user to turn the phase diagram on/off.
        m_phaseDiagramCtrlButton = new JButton();
        m_phaseDiagramCtrlButton.setFont( BUTTON_LABEL_FONT );
        addControlFullWidth( m_phaseDiagramCtrlButton );
        m_phaseDiagramCtrlButton.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                m_phaseDiagramVisible = !m_phaseDiagramVisible;
                m_phaseDiagramPanel.setVisible( m_phaseDiagramVisible );
                updatePhaseDiagramButtonLabel();
            }
        });
        updatePhaseDiagramButtonLabel();
        
        // Add the phase diagram.
        m_phaseDiagramPanel = new JPanel();
        m_phaseDiagram = new PhaseDiagram();
        m_phaseDiagramPanel.add( m_phaseDiagram );
        addControlFullWidth( m_phaseDiagramPanel );
        m_phaseDiagramPanel.setVisible( m_phaseDiagramVisible );
        
        // Add the button that allows the user to turn the interaction diagram on/off.
        m_interactionDiagramCtrlButton = new JButton();
        m_interactionDiagramCtrlButton.setFont( BUTTON_LABEL_FONT );
        addControlFullWidth( m_interactionDiagramCtrlButton );
        m_interactionDiagramCtrlButton.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                m_interactionDiagramVisible = !m_interactionDiagramVisible;
                m_interactionDiagramPanel.setVisible( m_interactionDiagramVisible );
                updateInteractionDiagramButtonLabel();
            }
        });
        updateInteractionDiagramButtonLabel();
        
        // Add the interaction potential diagram.
        m_interactionDiagramPanel = new JPanel();
        PhetPCanvas interactionDiagramCanvas = new PhetPCanvas();
        interactionDiagramCanvas.setPreferredSize( new Dimension(INTERACTION_POTENTIAL_DIAGRAM_WIDTH, 
                INTERACTION_POTENTIAL_DIAGRAM_HEIGHT) );
        interactionDiagramCanvas.setBackground( StatesOfMatterConstants.CONTROL_PANEL_COLOR );
        interactionDiagramCanvas.setBorder( null );
        m_interactionPotentialDiagram = new InteractionPotentialDiagramNode( m_model.getSigma(), m_model.getEpsilon(),
                false );
        interactionDiagramCanvas.addWorldChild( m_interactionPotentialDiagram );
        m_interactionDiagramPanel.add( interactionDiagramCanvas );
        addControlFullWidth( m_interactionDiagramPanel );
        m_interactionDiagramPanel.setVisible( m_interactionDiagramVisible );
        
        // Add the Reset All button.
        addSeparator();
        addVerticalSpace( 5 );
        addResetAllButton( phaseChangesModule );
    }
    
    //----------------------------------------------------------------------------
    // Inner Classes
    //----------------------------------------------------------------------------
    private class MoleculeSelectionPanel extends JPanel {
        
        private JRadioButton m_neonRadioButton;
        private JRadioButton m_argonRadioButton;
        private JRadioButton m_oxygenRadioButton;
        private JRadioButton m_waterRadioButton;
        
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

            m_oxygenRadioButton = new JRadioButton( StatesOfMatterStrings.OXYGEN_SELECTION_LABEL );
            m_oxygenRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_oxygenRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setMoleculeType( StatesOfMatterConstants.DIATOMIC_OXYGEN );
                    m_interactionPotentialDiagram.setMolecular( true );
                    m_phaseDiagram.setDepictingWater( false );
                }
            } );
            m_neonRadioButton = new JRadioButton( StatesOfMatterStrings.NEON_SELECTION_LABEL );
            m_neonRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_neonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setMoleculeType( StatesOfMatterConstants.NEON );
                    m_interactionPotentialDiagram.setMolecular( false );
                    m_phaseDiagram.setDepictingWater( false );
                }
            } );
            m_argonRadioButton = new JRadioButton( StatesOfMatterStrings.ARGON_SELECTION_LABEL );
            m_argonRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_argonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setMoleculeType( StatesOfMatterConstants.ARGON );
                    m_interactionPotentialDiagram.setMolecular( false );
                    m_phaseDiagram.setDepictingWater( false );
                }
            } );
            m_waterRadioButton = new JRadioButton( StatesOfMatterStrings.WATER_SELECTION_LABEL );
            m_waterRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_waterRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setMoleculeType( StatesOfMatterConstants.WATER );
                    m_interactionPotentialDiagram.setMolecular( true );
                    m_phaseDiagram.setDepictingWater( true );
                }
            } );
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( m_neonRadioButton );
            buttonGroup.add( m_argonRadioButton );
            buttonGroup.add( m_oxygenRadioButton );
            buttonGroup.add( m_waterRadioButton );
            m_neonRadioButton.setSelected( true );
            
            add( m_neonRadioButton );
            add( m_argonRadioButton );
            add( m_oxygenRadioButton );
            add( m_waterRadioButton );
        }
        
        public void setMolecule( int molecule ){
        	switch (molecule){
        	case StatesOfMatterConstants.ARGON:
        		m_argonRadioButton.setSelected(true);
        		break;
        	case StatesOfMatterConstants.NEON:
        		m_neonRadioButton.setSelected(true);
        		break;
        	case StatesOfMatterConstants.DIATOMIC_OXYGEN:
        		m_oxygenRadioButton.setSelected(true);
        		break;
        	case StatesOfMatterConstants.WATER:
        		m_waterRadioButton.setSelected(true);
        		break;
        	}
        }
    }
    
    private void updatePhaseDiagramButtonLabel(){
        if (m_phaseDiagramVisible){
            m_phaseDiagramCtrlButton.setText( StatesOfMatterStrings.PHASE_DIAGRAM_BUTTON_LABEL + " <<" );
        }
        else{
            m_phaseDiagramCtrlButton.setText( StatesOfMatterStrings.PHASE_DIAGRAM_BUTTON_LABEL + " >>" );
        }
    }
    
    private void updateInteractionDiagramButtonLabel(){
        if (m_interactionDiagramVisible){
            m_interactionDiagramCtrlButton.setText( StatesOfMatterStrings.INTERACTION_POTENTIAL_BUTTON_LABEL + " <<" );
        }
        else{
            m_interactionDiagramCtrlButton.setText( StatesOfMatterStrings.INTERACTION_POTENTIAL_BUTTON_LABEL + " >>" );
        }
    }
    
    // Constants used to control the way in which pressure and temperature
    // data from the model are mapped to the phase diagram.  These values are
    // empirically determined and can and should be tweaked as necessary to
    // create the correct behavior.
    private static final int ASSYMTOTIC_MAPPING_ALGORITHM = 0;
    private static final int LINEAR_MAPPING_ALGORITHM = 1;
    private static final int SIGMOID_LOGARITHMIC_MAPPING_ALGORITHM = 2;
    private static final int MAPPING_ALGORITHM = SIGMOID_LOGARITHMIC_MAPPING_ALGORITHM;
    
    private static final double TEMPERATURE_SCALE_FACTOR_LINEAR = 0.6;
    private static final double PRESSURE_SCALE_FACTOR_LINEAR = 2;
    
    private static final double TEMPERATURE_SCALE_FACTOR_ASSYMTOTIC = 10;
    private static final double PRESSURE_SCALE_FACTOR_ASSYMTOTIC = 100;
    
    private static final double TEMPERATURE_SCALE_FACTOR_SIGMOID = 20;
    private static final double TEMPERATURE_SHIFT_FACTOR_SIGMOID = 0.43;
    
    /**
     * Update the position of the marker on the phase diagram based on the
     * temperature and pressure values within the model.
     */
    private void updatePhaseDiagram(){
        
        double modelTemperature = m_model.getModelTemperature();
        double modelPressure = m_model.getModelPressure();
        double normalizedTemperature = 0;
        double normalizedPressure = 0;
        
        if (MAPPING_ALGORITHM == LINEAR_MAPPING_ALGORITHM) {
            normalizedTemperature = modelTemperature * TEMPERATURE_SCALE_FACTOR_LINEAR;
            if (normalizedTemperature > 1.0) {
                normalizedTemperature = 1.0;
            }
            normalizedPressure = modelPressure * PRESSURE_SCALE_FACTOR_LINEAR;
            if (normalizedPressure > 1.0) {
                normalizedPressure = 1.0;
            }
        }
        else if (MAPPING_ALGORITHM == ASSYMTOTIC_MAPPING_ALGORITHM) {
            normalizedTemperature = -1 / ((modelTemperature * TEMPERATURE_SCALE_FACTOR_ASSYMTOTIC) + 1) + 1;
            if (normalizedTemperature > 1.0) {
                normalizedTemperature = 1.0;
            }
            normalizedPressure = -1 / ((modelPressure * PRESSURE_SCALE_FACTOR_ASSYMTOTIC) + 1) + 1;
            if (normalizedPressure > 1.0) {
                normalizedPressure = 1.0;
            }
        }
        else if (MAPPING_ALGORITHM == SIGMOID_LOGARITHMIC_MAPPING_ALGORITHM) {
            normalizedTemperature = 1 / (1 + (Math.exp((-modelTemperature + TEMPERATURE_SHIFT_FACTOR_SIGMOID ) * 
                    TEMPERATURE_SCALE_FACTOR_SIGMOID)));
            normalizedPressure = Math.log((modelPressure * 5) + 1);
            if (normalizedPressure > 1.0) {
                normalizedPressure = 1.0;
            }
            System.out.println("modelPressure = " + modelPressure + ", normalizedPressure = " + normalizedPressure);
        }
        m_phaseDiagram.setStateMarkerPos( normalizedTemperature, normalizedPressure );
    }
}
