/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.phasechanges;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
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
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.module.CloseRequestListener;


public class PhaseChangesControlPanel extends ControlPanel {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    private static final Font BUTTON_LABEL_FONT = new PhetFont(14);
    private static final Color ENABLED_TITLE_COLOR = new Color ( 128, 128, 128 );
    private static final int INTERACTION_POTENTIAL_DIAGRAM_WIDTH = 200;
    private static final int INTERACTION_POTENTIAL_DIAGRAM_HEIGHT = (int)(INTERACTION_POTENTIAL_DIAGRAM_WIDTH * 0.8);

    // Constants used when mapping the model pressure and temperature to the phase diagram.
    private static double TRIPLE_POINT_TEMPERATURE_IN_MODEL = MultipleParticleModel.TRIPLE_POINT_MODEL_TEMPERATURE;
    private static double TRIPLE_POINT_TEMPERATURE_ON_DIAGRAM = 0.375;
    private static double CRITICAL_POINT_TEMPERATURE_IN_MODEL = MultipleParticleModel.CRITICAL_POINT_MODEL_TEMPERATURE;
    private static double CRITICAL_POINT_TEMPERATURE_ON_DIAGRAM = 0.8;
    private static double SLOPE_IN_1ST_REGION = TRIPLE_POINT_TEMPERATURE_ON_DIAGRAM / TRIPLE_POINT_TEMPERATURE_IN_MODEL;
    private static double SLOPE_IN_2ND_REGION = 
    	(CRITICAL_POINT_TEMPERATURE_ON_DIAGRAM - TRIPLE_POINT_TEMPERATURE_ON_DIAGRAM) /
        (CRITICAL_POINT_TEMPERATURE_IN_MODEL - TRIPLE_POINT_TEMPERATURE_IN_MODEL);
    private static double OFFSET_IN_2ND_REGION = TRIPLE_POINT_TEMPERATURE_ON_DIAGRAM - 
         (SLOPE_IN_2ND_REGION * TRIPLE_POINT_TEMPERATURE_IN_MODEL);
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private MultipleParticleModel m_model;
    private JPanel m_phaseDiagramPanel;
    private boolean m_phaseDiagramVisible;
    private JButton m_phaseDiagramCtrlButton;
    private PhaseDiagram m_phaseDiagram;
    private MoleculeSelectionPanel m_moleculeSelectionPanel;
    
    private JPanel m_interactionDiagramPanel;
    private boolean m_interactionDiagramVisible;
    private JButton m_interactionDiagramCtrlButton;
    private EpsilonControlInteractionPotentialDiagram m_interactionPotentialDiagram;
    private InteractionStrengthControlPanel m_interactionStrengthControlPanel;
    
    private JPanel m_preInteractionButtonSpacer;
    private JPanel m_postInteractionButtonSpacer;
    private JPanel m_prePhaseButtonSpacer;
    private JPanel m_postPhaseButtonSpacer;
    private CloseRequestListener m_phaseDiagramCloseListener;
    private CloseRequestListener m_interactionPotentialDiagramCloseListener;
    
    //----------------------------------------------------------------------------
    // Constructor(s)
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
            public void containerExploded(){
            	updatePhaseDiagram();
            }
            public void resetOccurred(){
            	m_phaseDiagramVisible = true;
            	m_interactionDiagramVisible = true;
            	updateVisibilityStates();
            }
        });
        
        // Set the control panel's minimum width.
        int minimumWidth = StatesOfMatterResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Add the panel that allows the user to select molecule type.
        m_moleculeSelectionPanel = new MoleculeSelectionPanel();
        addControlFullWidth( m_moleculeSelectionPanel );
        
        // Create the panel for controlling the interaction strength.
        m_interactionStrengthControlPanel = new InteractionStrengthControlPanel( m_model );
        addControlFullWidth(m_interactionStrengthControlPanel);
        
        // Add a little spacing.
        addVerticalSpace( 10 );

        // Add additional spacing before the interaction potential control button.
        m_preInteractionButtonSpacer = createVerticalSpacerPanel(20);
        addControlFullWidth(m_preInteractionButtonSpacer);
        m_preInteractionButtonSpacer.setVisible(!m_interactionDiagramVisible);
        
        // Add the button that allows the user to turn the interaction diagram on/off.
        m_interactionDiagramCtrlButton = new JButton();
        m_interactionDiagramCtrlButton.setFont( BUTTON_LABEL_FONT );
        m_interactionDiagramCtrlButton.setText( StatesOfMatterStrings.INTERACTION_POTENTIAL_BUTTON_LABEL);
        addControl( m_interactionDiagramCtrlButton );
        m_interactionDiagramCtrlButton.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                m_interactionDiagramVisible = true;
                updateVisibilityStates();
            }
        });
        m_interactionDiagramCtrlButton.setVisible( !m_interactionDiagramVisible );
        double buttonWidth = m_interactionDiagramCtrlButton.getPreferredSize().getWidth();
        double buttonHeight = m_interactionDiagramCtrlButton.getPreferredSize().getHeight();
        
        // Add additional spacing after the interaction potential diagram control button.
        m_postInteractionButtonSpacer = createVerticalSpacerPanel(20);
        addControlFullWidth(m_postInteractionButtonSpacer);
        m_postInteractionButtonSpacer.setVisible(!m_interactionDiagramVisible);
        
        // Add the interaction potential diagram.
        m_interactionDiagramPanel = new JPanel();
        PhetPCanvas interactionDiagramCanvas = new PhetPCanvas();
        interactionDiagramCanvas.setPreferredSize( new Dimension(INTERACTION_POTENTIAL_DIAGRAM_WIDTH, 
                INTERACTION_POTENTIAL_DIAGRAM_HEIGHT) );
        interactionDiagramCanvas.setBackground( StatesOfMatterConstants.CONTROL_PANEL_COLOR );
        interactionDiagramCanvas.setBorder( null );
        m_interactionPotentialDiagram = new EpsilonControlInteractionPotentialDiagram( m_model.getSigma(), 
        		m_model.getEpsilon(), false, m_model );
        m_interactionPotentialDiagram.setBackgroundColor( StatesOfMatterConstants.CONTROL_PANEL_COLOR );
        interactionDiagramCanvas.addWorldChild( m_interactionPotentialDiagram );
        m_interactionDiagramPanel.add( interactionDiagramCanvas );
        addControlFullWidth( m_interactionDiagramPanel );
        m_interactionDiagramPanel.setVisible( m_interactionDiagramVisible );
        
        // Create and register the handler for user requests to close the interaction potential diagram.
        m_interactionPotentialDiagramCloseListener = new CloseRequestListener(){
        	public void closeRequestReceived(){
        		// Note that we don't actually make it go away, we just make
        		// it invisible.
        		m_interactionDiagramVisible = false;
                updateVisibilityStates();
        	}
        };
        m_interactionPotentialDiagram.addListener(m_interactionPotentialDiagramCloseListener);
        
        // Add additional spacing before the phase diagram control button.
        m_prePhaseButtonSpacer = createVerticalSpacerPanel(20);
        addControlFullWidth(m_prePhaseButtonSpacer);
        m_prePhaseButtonSpacer.setVisible(!m_phaseDiagramVisible);
        
        // Add the button that allows the user to turn the phase diagram on/off.
        m_phaseDiagramCtrlButton = new JButton();
        m_phaseDiagramCtrlButton.setFont( BUTTON_LABEL_FONT );
        m_phaseDiagramCtrlButton.setText( StatesOfMatterStrings.PHASE_DIAGRAM_BUTTON_LABEL );
        addControl( m_phaseDiagramCtrlButton );
        m_phaseDiagramCtrlButton.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                m_phaseDiagramVisible = true;
                updateVisibilityStates();
            }
        });
        m_phaseDiagramCtrlButton.setVisible( !m_phaseDiagramVisible );
        
        // Set the two buttons to be the same size.
        buttonWidth = Math.max( buttonWidth, m_phaseDiagramCtrlButton.getPreferredSize().getWidth());
        buttonHeight = Math.max( buttonHeight, m_phaseDiagramCtrlButton.getPreferredSize().getHeight());
        Dimension buttonSize = new Dimension((int)Math.round(buttonWidth), (int)Math.round(buttonHeight));
        m_phaseDiagramCtrlButton.setPreferredSize(buttonSize);
        m_interactionDiagramCtrlButton.setPreferredSize(buttonSize);
        
        // Add additional spacing after the phase diagram control button.
        m_postPhaseButtonSpacer = createVerticalSpacerPanel(20);
        addControlFullWidth(m_postPhaseButtonSpacer);
        m_postInteractionButtonSpacer.setVisible(!m_phaseDiagramVisible);
        
        // Add the phase diagram.
        m_phaseDiagramPanel = new JPanel();
        m_phaseDiagram = new PhaseDiagram();
        m_phaseDiagramPanel.add( m_phaseDiagram );
        addControlFullWidth( m_phaseDiagramPanel );
        m_phaseDiagramPanel.setVisible( m_phaseDiagramVisible );
        
        // Create and register the handler for user requests to close the phase diagram.
        m_phaseDiagramCloseListener = new CloseRequestListener(){
        	public void closeRequestReceived(){
        		// Note that we don't actually make it go away, we just make
        		// it invisible.
        		m_phaseDiagramVisible = false;
                updateVisibilityStates();
        	}
        };
        m_phaseDiagram.addListener(m_phaseDiagramCloseListener);
        
        // Add the Reset All button.
        addSeparator();
        addVerticalSpace( 5 );
        addResetAllButton( phaseChangesModule );
        
        // Update the visibility of the controls based on current model state.
        updateVisibilityStates();
    }
    
    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    /**
     * Update the position of the marker on the phase diagram based on the
     * temperature and pressure values within the model.
     */
    private void updatePhaseDiagram(){
    	
    	// If the container has exploded, don't bother showing the dot.
    	if ( m_model.getContainerExploded() ){
    		m_phaseDiagram.setStateMarkerVisible(false);
    	}
    	else{
    		m_phaseDiagram.setStateMarkerVisible(true);
	        double modelTemperature = m_model.getTemperatureSetPoint();
	        double modelPressure = m_model.getModelPressure();
	        double mappedTemperature = mapModelTemperatureToPhaseDiagramTemperature(modelTemperature);
	        double mappedPressure = mapModelTempAndPressureToPhaseDiagramPressure(modelPressure, modelTemperature);
	        m_phaseDiagram.setStateMarkerPos( mappedTemperature, mappedPressure );
    	}
        
    }
    
    private double mapModelTemperatureToPhaseDiagramTemperature(double modelTemperature){
    	
    	double mappedTemperature;
    	
    	if (modelTemperature < TRIPLE_POINT_TEMPERATURE_IN_MODEL){
    		mappedTemperature = SLOPE_IN_1ST_REGION * modelTemperature;
    	}
    	else{
            mappedTemperature = modelTemperature * SLOPE_IN_2ND_REGION + OFFSET_IN_2ND_REGION;    		
    	}

    	return Math.min(mappedTemperature, 1);
    }
    
    private static final double PRESSURE_FACTOR = 35;
    
    private double mapModelTempAndPressureToPhaseDiagramPressure(double modelPressure, double modelTemperature){
    	double mappedTemperature = mapModelTemperatureToPhaseDiagramTemperature(modelTemperature);
    	double mappedPressure;
    	
    	if (modelTemperature  < TRIPLE_POINT_TEMPERATURE_IN_MODEL){
    		mappedPressure = 1.4 * (Math.pow(mappedTemperature, 2)) + PRESSURE_FACTOR * Math.pow(modelPressure, 2);
    	}
    	else if (modelTemperature  < CRITICAL_POINT_TEMPERATURE_IN_MODEL){
            mappedPressure = 0.19 + 1.2 * (Math.pow(mappedTemperature - TRIPLE_POINT_TEMPERATURE_ON_DIAGRAM, 2)) + 
            	PRESSURE_FACTOR * Math.pow(modelPressure, 2);    		
    	}
    	else{
            mappedPressure = 0.43 + (0.43 / 0.81) * (mappedTemperature - 0.81) + 
                PRESSURE_FACTOR * Math.pow(modelPressure, 2);    		
    	}
    	return Math.min(mappedPressure, 1);
    }
    
    //----------------------------------------------------------------------------
    // Inner Classes
    //----------------------------------------------------------------------------
    private class MoleculeSelectionPanel extends JPanel {
        
        private JRadioButton m_neonRadioButton;
        private JRadioButton m_argonRadioButton;
        private JRadioButton m_oxygenRadioButton;
        private JRadioButton m_waterRadioButton;
        private JRadioButton m_configurableRadioButton;
        
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
                    updateVisibilityStates();
                }
            } );
            m_neonRadioButton = new JRadioButton( StatesOfMatterStrings.NEON_SELECTION_LABEL );
            m_neonRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_neonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setMoleculeType( StatesOfMatterConstants.NEON );
                    m_interactionPotentialDiagram.setMolecular( false );
                    m_phaseDiagram.setDepictingWater( false );
                    updateVisibilityStates();
                }
            } );
            m_argonRadioButton = new JRadioButton( StatesOfMatterStrings.ARGON_SELECTION_LABEL );
            m_argonRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_argonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setMoleculeType( StatesOfMatterConstants.ARGON );
                    m_interactionPotentialDiagram.setMolecular( false );
                    m_phaseDiagram.setDepictingWater( false );
                    updateVisibilityStates();
                }
            } );
            m_waterRadioButton = new JRadioButton( StatesOfMatterStrings.WATER_SELECTION_LABEL );
            m_waterRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_waterRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setMoleculeType( StatesOfMatterConstants.WATER );
                    m_interactionPotentialDiagram.setMolecular( true );
                    m_phaseDiagram.setDepictingWater( true );
                    updateVisibilityStates();
                }
            } );
            m_configurableRadioButton = new JRadioButton( StatesOfMatterStrings.ADJUSTABLE_ATTRACTION_SELECTION_LABEL );
            m_configurableRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_configurableRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setMoleculeType( StatesOfMatterConstants.USER_DEFINED_MOLECULE );
                    m_interactionPotentialDiagram.setMolecular( false );
                    m_phaseDiagram.setDepictingWater( false );
                    updateVisibilityStates();
                }
            } );
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( m_neonRadioButton );
            buttonGroup.add( m_argonRadioButton );
            buttonGroup.add( m_oxygenRadioButton );
            buttonGroup.add( m_waterRadioButton );
            buttonGroup.add( m_configurableRadioButton );
            m_neonRadioButton.setSelected( true );
            
            add( m_neonRadioButton );
            add( m_argonRadioButton );
            add( m_oxygenRadioButton );
            add( m_waterRadioButton );
            add( m_configurableRadioButton );
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
    
    /**
     * Layout strategy for slider.
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
    
    /**
     * This class represents the control slider for the interaction strength.
     */
    private class InteractionStrengthControlPanel extends JPanel {
        
        private final Font LABEL_FONT = new PhetFont(14, false);

        private LinearValueControl m_interactionStrengthControl;
        private MultipleParticleModel m_model;
        private TitledBorder m_titledBorder;
        private JLabel m_leftLabel;
        private JLabel m_rightLabel;

        public InteractionStrengthControlPanel(MultipleParticleModel model){


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
            m_interactionStrengthControl = new LinearValueControl( MultipleParticleModel.MIN_ADJUSTABLE_EPSILON, 
            		MultipleParticleModel.MAX_ADJUSTABLE_EPSILON, "", "0", "", new SliderLayoutStrategy() );
            m_interactionStrengthControl.setUpDownArrowDelta( 0.01 );
            m_interactionStrengthControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ){
                    // Set the interaction strength in the model if the molecule is correct.
                	if (m_model.getMoleculeType() == StatesOfMatterConstants.USER_DEFINED_MOLECULE){
                        m_model.setEpsilon( m_interactionStrengthControl.getValue() );
                	}
                }
            });
            m_interactionStrengthControl.getSlider().addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                	// TODO: Add this back if needed, and when implemented.
                	//m_model.setParticleMotionPaused(true);
                }
                public void mouseReleased(MouseEvent e) {
                	// TODO: Add this back if needed, and when implemented.
                	//m_model.setParticleMotionPaused(false);
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
            m_model.addListener( new MultipleParticleModel.Adapter(){
                public void interactionStrengthChanged(){
                	double epsilon = m_model.getEpsilon();
                	epsilon = Math.min(epsilon, MultipleParticleModel.MAX_ADJUSTABLE_EPSILON);
                	epsilon = Math.max(epsilon, MultipleParticleModel.MIN_ADJUSTABLE_EPSILON);
                    m_interactionStrengthControl.setValue( epsilon );
                    updatePhaseDiagram();
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
    
    private JPanel createVerticalSpacerPanel( int space ) {
        if ( space <= 0 ) {
        	throw new IllegalArgumentException("Can't have zero or negative space in spacer panel.");
        }
        
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( space ) );
        return spacePanel;
    }
    
    /**
     * Update the visibility of the various diagrams, buttons, and controls
     * based on the internal state and the state of the model.
     */
    private void updateVisibilityStates(){

    	m_interactionDiagramPanel.setVisible( m_interactionDiagramVisible );
        m_interactionDiagramCtrlButton.setVisible( !m_interactionDiagramVisible );
        m_preInteractionButtonSpacer.setVisible( !m_interactionDiagramVisible );
        m_postInteractionButtonSpacer.setVisible( !m_interactionDiagramVisible );
        
    	boolean userDefinedMoleculeSelected = 
    		m_model.getMoleculeType() == StatesOfMatterConstants.USER_DEFINED_MOLECULE;
    	
    	m_interactionStrengthControlPanel.setVisible( userDefinedMoleculeSelected );
    	
    	if ( userDefinedMoleculeSelected ){
        	// Don't show the phase diagram or the button that enables it if
        	// the user has selected the adjustable atom.  This is done
        	// because the adjustable atom is not a real thing, and it is to
        	// difficult to figure out what to do with the phase diagram in
        	// this case.
    		m_phaseDiagramPanel.setVisible( false );
    		m_phaseDiagramCtrlButton.setVisible( false );
    		m_preInteractionButtonSpacer.setVisible( false );
    		m_postInteractionButtonSpacer.setVisible( false );
        }
        else{
            m_phaseDiagramPanel.setVisible( m_phaseDiagramVisible );
            m_phaseDiagramCtrlButton.setVisible( !m_phaseDiagramVisible );
            m_prePhaseButtonSpacer.setVisible( !m_phaseDiagramVisible );
            m_postPhaseButtonSpacer.setVisible( !m_phaseDiagramVisible );
        }
    }
}
