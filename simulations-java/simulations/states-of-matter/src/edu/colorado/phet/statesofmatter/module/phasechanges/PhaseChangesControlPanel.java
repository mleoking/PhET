// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.module.phasechanges;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import edu.colorado.phet.statesofmatter.module.solidliquidgas.MoleculeImageLabel;


public class PhaseChangesControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    private static final Font BUTTON_LABEL_FONT = new PhetFont( 14 );
    private static final Color ENABLED_TITLE_COLOR = Color.BLACK;
    private static final int INTERACTION_POTENTIAL_DIAGRAM_WIDTH = 200;
    private static final int INTERACTION_POTENTIAL_DIAGRAM_HEIGHT = (int) ( INTERACTION_POTENTIAL_DIAGRAM_WIDTH * 0.8 );

    // Constants used when mapping the model pressure and temperature to the phase diagram.
    private static final double TRIPLE_POINT_TEMPERATURE_IN_MODEL = MultipleParticleModel.TRIPLE_POINT_MONATOMIC_MODEL_TEMPERATURE;
    private static final double TRIPLE_POINT_TEMPERATURE_ON_DIAGRAM = 0.375;
    private static final double CRITICAL_POINT_TEMPERATURE_IN_MODEL = MultipleParticleModel.CRITICAL_POINT_MONATOMIC_MODEL_TEMPERATURE;
    private static final double CRITICAL_POINT_TEMPERATURE_ON_DIAGRAM = 0.8;
    private static final double SLOPE_IN_1ST_REGION = TRIPLE_POINT_TEMPERATURE_ON_DIAGRAM / TRIPLE_POINT_TEMPERATURE_IN_MODEL;
    private static final double SLOPE_IN_2ND_REGION =
            ( CRITICAL_POINT_TEMPERATURE_ON_DIAGRAM - TRIPLE_POINT_TEMPERATURE_ON_DIAGRAM ) /
            ( CRITICAL_POINT_TEMPERATURE_IN_MODEL - TRIPLE_POINT_TEMPERATURE_IN_MODEL );
    private static final double OFFSET_IN_2ND_REGION = TRIPLE_POINT_TEMPERATURE_ON_DIAGRAM -
                                                       ( SLOPE_IN_2ND_REGION * TRIPLE_POINT_TEMPERATURE_IN_MODEL );

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private final MultipleParticleModel m_model;
    private final JPanel m_phaseDiagramPanel;
    private boolean m_phaseDiagramVisible;
    private final JButton m_phaseDiagramCtrlButton;
    private final PhaseDiagram m_phaseDiagram;
    private MoleculeSelectionPanel m_moleculeSelectionPanel;

    private final JPanel m_interactionDiagramPanel;
    private boolean m_interactionDiagramVisible;
    private final JButton m_interactionDiagramCtrlButton;
    private EpsilonControlInteractionPotentialDiagram m_interactionPotentialDiagram;
    private final InteractionStrengthControlPanel m_interactionStrengthControlPanel;

    private final JPanel m_preInteractionButtonSpacer;
    private final JPanel m_postInteractionButtonSpacer;
    private final JPanel m_prePhaseButtonSpacer;
    private final JPanel m_postPhaseButtonSpacer;

    private final boolean m_advanced;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param phaseChangesModule
     * @param advanced           - Flag to indicate whether basic or advanced mode is to
     *                           be used.  Advanced mode shows the interaction potential stuff, basic
     *                           mode does not.
     */
    public PhaseChangesControlPanel( PhaseChangesModule phaseChangesModule, boolean advanced ) {
        super();
        m_model = phaseChangesModule.getMultiParticleModel();
        m_advanced = advanced;
        m_phaseDiagramVisible = advanced;
        m_interactionDiagramVisible = advanced;

        // Register with the model for events that affect the diagrams on this panel.
        m_model.addListener( new MultipleParticleModel.Adapter() {
            public void moleculeTypeChanged() {
                m_interactionPotentialDiagram.setLjPotentialParameters( m_model.getSigma(), m_model.getEpsilon() );
                m_moleculeSelectionPanel.setMolecule( m_model.getMoleculeType() );
            }

            public void temperatureChanged() {
                updatePhaseDiagram();
            }

            public void pressureChanged() {
                updatePhaseDiagram();
            }

            public void containerExplodedStateChanged( boolean containerExploded ) {
                updatePhaseDiagram();
            }

            public void resetOccurred() {
                m_phaseDiagramVisible = m_advanced;
                m_interactionDiagramVisible = m_advanced;
                updateVisibilityStates();
            }
        } );

        // Set the control panel's minimum width.
        int minimumWidth = StatesOfMatterResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );

        // Add the panel that allows the user to select molecule type.
        m_moleculeSelectionPanel = new MoleculeSelectionPanel( advanced );
        addControlFullWidth( m_moleculeSelectionPanel );

        // Create the panel for controlling the interaction strength.
        m_interactionStrengthControlPanel = new InteractionStrengthControlPanel( m_model );
        addControlFullWidth( m_interactionStrengthControlPanel );

        // Add a little spacing.
        addVerticalSpace( 10 );

        // Add additional spacing before the interaction potential control button.
        m_preInteractionButtonSpacer = createVerticalSpacerPanel( 20 );
        addControlFullWidth( m_preInteractionButtonSpacer );
        m_preInteractionButtonSpacer.setVisible( !m_interactionDiagramVisible );

        // Add the button that allows the user to turn the interaction diagram on/off.
        m_interactionDiagramCtrlButton = new JButton();
        m_interactionDiagramCtrlButton.setFont( BUTTON_LABEL_FONT );
        m_interactionDiagramCtrlButton.setText( StatesOfMatterStrings.INTERACTION_POTENTIAL_BUTTON_LABEL );
        addControl( m_interactionDiagramCtrlButton );
        m_interactionDiagramCtrlButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                m_interactionDiagramVisible = true;
                updateVisibilityStates();
            }
        } );
        m_interactionDiagramCtrlButton.setVisible( !m_interactionDiagramVisible );
        double buttonWidth = m_interactionDiagramCtrlButton.getPreferredSize().getWidth();
        double buttonHeight = m_interactionDiagramCtrlButton.getPreferredSize().getHeight();

        // Add additional spacing after the interaction potential diagram control button.
        m_postInteractionButtonSpacer = createVerticalSpacerPanel( 20 );
        addControlFullWidth( m_postInteractionButtonSpacer );
        m_postInteractionButtonSpacer.setVisible( !m_interactionDiagramVisible );

        // Add the interaction potential diagram.
        m_interactionDiagramPanel = new JPanel();
        PhetPCanvas interactionDiagramCanvas = new PhetPCanvas();
        interactionDiagramCanvas.setPreferredSize( new Dimension( INTERACTION_POTENTIAL_DIAGRAM_WIDTH,
                                                                  INTERACTION_POTENTIAL_DIAGRAM_HEIGHT ) );
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
        CloseRequestListener interactionPotentialDiagramCloseListener = new CloseRequestListener() {
            public void closeRequestReceived() {
                // Note that we don't actually make it go away, we just make
                // it invisible.
                m_interactionDiagramVisible = false;
                updateVisibilityStates();
            }
        };
        m_interactionPotentialDiagram.addListener( interactionPotentialDiagramCloseListener );

        // Add additional spacing before the phase diagram control button.
        m_prePhaseButtonSpacer = createVerticalSpacerPanel( 20 );
        addControlFullWidth( m_prePhaseButtonSpacer );
        m_prePhaseButtonSpacer.setVisible( !m_phaseDiagramVisible );

        // Add the button that allows the user to turn the phase diagram on/off.
        m_phaseDiagramCtrlButton = new JButton();
        m_phaseDiagramCtrlButton.setFont( BUTTON_LABEL_FONT );
        m_phaseDiagramCtrlButton.setText( StatesOfMatterStrings.PHASE_DIAGRAM_BUTTON_LABEL );
        addControl( m_phaseDiagramCtrlButton );
        m_phaseDiagramCtrlButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                m_phaseDiagramVisible = true;
                updateVisibilityStates();
            }
        } );
        m_phaseDiagramCtrlButton.setVisible( !m_phaseDiagramVisible );

        // Set the two buttons to be the same size.
        buttonWidth = Math.max( buttonWidth, m_phaseDiagramCtrlButton.getPreferredSize().getWidth() );
        buttonHeight = Math.max( buttonHeight, m_phaseDiagramCtrlButton.getPreferredSize().getHeight() );
        Dimension buttonSize = new Dimension( (int) Math.round( buttonWidth ), (int) Math.round( buttonHeight ) );
        m_phaseDiagramCtrlButton.setPreferredSize( buttonSize );
        m_interactionDiagramCtrlButton.setPreferredSize( buttonSize );

        // Add additional spacing after the phase diagram control button.
        m_postPhaseButtonSpacer = createVerticalSpacerPanel( 20 );
        addControlFullWidth( m_postPhaseButtonSpacer );
        m_postInteractionButtonSpacer.setVisible( !m_phaseDiagramVisible );

        // Add the phase diagram.
        m_phaseDiagramPanel = new JPanel();
        m_phaseDiagram = new PhaseDiagram();
        m_phaseDiagramPanel.add( m_phaseDiagram );
        addControlFullWidth( m_phaseDiagramPanel );
        m_phaseDiagramPanel.setVisible( m_phaseDiagramVisible );

        // Create and register the handler for user requests to close the phase diagram.
        CloseRequestListener phaseDiagramCloseListener = new CloseRequestListener() {
            public void closeRequestReceived() {
                // Note that we don't actually make it go away, we just make
                // it invisible.
                m_phaseDiagramVisible = false;
                updateVisibilityStates();
            }
        };
        m_phaseDiagram.addListener( phaseDiagramCloseListener );

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
    private void updatePhaseDiagram() {

        // If the container has exploded, don't bother showing the dot.
        if ( m_model.getContainerExploded() ) {
            m_phaseDiagram.setStateMarkerVisible( false );
        }
        else {
            m_phaseDiagram.setStateMarkerVisible( true );
            double modelTemperature = m_model.getTemperatureSetPoint();
            double modelPressure = m_model.getModelPressure();
            double mappedTemperature = mapModelTemperatureToPhaseDiagramTemperature( modelTemperature );
            double mappedPressure = mapModelTempAndPressureToPhaseDiagramPressureAlternative1( modelPressure, modelTemperature );
            m_phaseDiagram.setStateMarkerPos( mappedTemperature, mappedPressure );
        }
    }

    private double mapModelTemperatureToPhaseDiagramTemperature( double modelTemperature ) {

        double mappedTemperature;

        if ( modelTemperature < TRIPLE_POINT_TEMPERATURE_IN_MODEL ) {
            mappedTemperature = SLOPE_IN_1ST_REGION * modelTemperature;
        }
        else {
            mappedTemperature = modelTemperature * SLOPE_IN_2ND_REGION + OFFSET_IN_2ND_REGION;
        }

        return Math.min( mappedTemperature, 1 );
    }

    private static final double PRESSURE_FACTOR = 35;

    private double mapModelTempAndPressureToPhaseDiagramPressure( double modelPressure, double modelTemperature ) {
        double mappedTemperature = mapModelTemperatureToPhaseDiagramTemperature( modelTemperature );
        double mappedPressure;

        if ( modelTemperature < TRIPLE_POINT_TEMPERATURE_IN_MODEL ) {
            mappedPressure = 1.4 * ( Math.pow( mappedTemperature, 2 ) ) + PRESSURE_FACTOR * Math.pow( modelPressure, 2 );
        }
        else if ( modelTemperature < CRITICAL_POINT_TEMPERATURE_IN_MODEL ) {
            mappedPressure = 0.19 + 1.2 * ( Math.pow( mappedTemperature - TRIPLE_POINT_TEMPERATURE_ON_DIAGRAM, 2 ) ) +
                             PRESSURE_FACTOR * Math.pow( modelPressure, 2 );
        }
        else {
            mappedPressure = 0.43 + ( 0.43 / 0.81 ) * ( mappedTemperature - 0.81 ) +
                             PRESSURE_FACTOR * Math.pow( modelPressure, 2 );
        }
        return Math.min( mappedPressure, 1 );
    }

    // Version that strictly maps the temperature to a spot on the chart that
    // has been empirically determined to always be right on the phase line.
    private double mapModelTempAndPressureToPhaseDiagramPressureAlternative1( double modelPressure, double modelTemperature ) {
        // This method is a total tweak fest.  All values and equations are
        // made to map the the phase diagram, and are NOT based on any real
        // world equations that define the phase.
        double cutOverTemperature = 0.35;
        double mappedTemperature = mapModelTemperatureToPhaseDiagramTemperature( modelTemperature );
        double mappedPressure;
        if ( mappedTemperature < cutOverTemperature ) {
            mappedPressure = Math.pow( mappedTemperature, 1.5 );
        }
        else {
            mappedPressure = Math.pow( mappedTemperature - cutOverTemperature, 1.8 ) + 0.2;
        }
        return Math.min( mappedPressure, 1 );
    }

    //----------------------------------------------------------------------------
    // Inner Classes
    //----------------------------------------------------------------------------

    private class MoleculeSelectionPanel extends JPanel {

        private final JRadioButton m_neonRadioButton;
        private final JRadioButton m_argonRadioButton;
        private final JRadioButton m_oxygenRadioButton;
        private final JRadioButton m_waterRadioButton;
        private final JRadioButton m_configurableRadioButton;

        MoleculeSelectionPanel( boolean showConfigurableAtom ) {
            BevelBorder baseBorder = (BevelBorder) BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                                                                          StatesOfMatterStrings.MOLECULE_TYPE_SELECT_LABEL,
                                                                          TitledBorder.LEFT,
                                                                          TitledBorder.TOP,
                                                                          new PhetFont( Font.BOLD, 14 ),
                                                                          Color.BLACK );

            setBorder( titledBorder );

            m_oxygenRadioButton = new MoleculeSelectorButton( StatesOfMatterStrings.OXYGEN_SELECTION_LABEL, m_model, StatesOfMatterConstants.DIATOMIC_OXYGEN, true );
            final JLabel oxygenImageLabel = new MoleculeImageLabel( StatesOfMatterConstants.DIATOMIC_OXYGEN, m_model );
            m_neonRadioButton = new MoleculeSelectorButton( StatesOfMatterStrings.NEON_SELECTION_LABEL, m_model, StatesOfMatterConstants.NEON, false );
            final JLabel neonImageLabel = new MoleculeImageLabel( StatesOfMatterConstants.NEON, m_model );
            m_argonRadioButton = new MoleculeSelectorButton( StatesOfMatterStrings.ARGON_SELECTION_LABEL, m_model, StatesOfMatterConstants.ARGON, false );
            final JLabel argonImageLabel = new MoleculeImageLabel( StatesOfMatterConstants.ARGON, m_model );
            m_waterRadioButton = new MoleculeSelectorButton( StatesOfMatterStrings.WATER_SELECTION_LABEL, m_model, StatesOfMatterConstants.WATER, true );
            final JLabel waterImageLabel = new MoleculeImageLabel( StatesOfMatterConstants.WATER, m_model );
            m_configurableRadioButton = new MoleculeSelectorButton( StatesOfMatterStrings.ADJUSTABLE_ATTRACTION_SELECTION_LABEL, m_model, StatesOfMatterConstants.USER_DEFINED_MOLECULE, true );
            final JLabel configurableAtomImageLabel = new MoleculeImageLabel( StatesOfMatterConstants.USER_DEFINED_MOLECULE, m_model );

            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( m_neonRadioButton );
            buttonGroup.add( m_argonRadioButton );
            buttonGroup.add( m_oxygenRadioButton );
            buttonGroup.add( m_waterRadioButton );
            buttonGroup.add( m_configurableRadioButton );
            m_neonRadioButton.setSelected( true );

            // Lay out the panel such that the radio buttons are all the way
            // to the left and the molecule are vertically aligned to the
            // right of the labels.
            // TODO: This layout doesn't look that great when the "adjustable
            // attraction" label is present since it pushes all of the images
            // way out to the right.  Delete when and if we accept the other layout.
//            setLayout( new GridBagLayout() );
//            GridBagConstraints constraints = new GridBagConstraints();
//            constraints.anchor = GridBagConstraints.WEST;
//            constraints.gridx = 0;
//            constraints.gridy = 0;
//            add( m_neonRadioButton, constraints );
//            constraints.gridx++;
//            constraints.anchor = GridBagConstraints.CENTER;
//            add( neonLabel, constraints );
//
//            constraints.gridx = 0;
//            constraints.gridy++;
//            constraints.anchor = GridBagConstraints.WEST;
//            add( m_argonRadioButton, constraints );
//            constraints.gridx++;
//            constraints.anchor = GridBagConstraints.CENTER;
//            add( argonLabel, constraints );
//
//            constraints.gridx = 0;
//            constraints.gridy++;
//            constraints.anchor = GridBagConstraints.WEST;
//            add( m_oxygenRadioButton, constraints );
//            constraints.gridx++;
//            constraints.anchor = GridBagConstraints.CENTER;
//            add( oxygenLabel, constraints );
//
//            constraints.gridx = 0;
//            constraints.gridy++;
//            constraints.anchor = GridBagConstraints.WEST;
//            add( m_waterRadioButton, constraints );
//            constraints.gridx++;
//            constraints.anchor = GridBagConstraints.CENTER;
//            add( waterLabel, constraints );
//
//            if ( showConfigurableAtom ) {
//                constraints.gridx = 0;
//                constraints.gridy++;
//                constraints.anchor = GridBagConstraints.WEST;
//                add( m_configurableRadioButton, constraints );
//                constraints.gridx++;
//                constraints.anchor = GridBagConstraints.CENTER;
//                add( configurableLabel, constraints );
//            }

            // Lay out the controls so that the atom or molecule image is
            // just to the right of the corresponding label.
            setLayout( new GridLayout( 5, 1 ) );
            add( new JPanel( new FlowLayout( FlowLayout.LEFT ) ) {{
                add( m_neonRadioButton );
                add( neonImageLabel );
            }} );
            add( new JPanel( new FlowLayout( FlowLayout.LEFT ) ) {{
                add( m_argonRadioButton );
                add( argonImageLabel );
            }} );
            add( new JPanel( new FlowLayout( FlowLayout.LEFT ) ) {{
                add( m_oxygenRadioButton );
                add( oxygenImageLabel );
            }} );
            add( new JPanel( new FlowLayout( FlowLayout.LEFT ) ) {{
                add( m_waterRadioButton );
                add( waterImageLabel );
            }} );

            if ( showConfigurableAtom ) {
                add( new JPanel( new FlowLayout( FlowLayout.LEFT ) ) {{
                    add( m_configurableRadioButton );
                    add( configurableAtomImageLabel );
                }} );
            }
        }

        public void setMolecule( int molecule ) {
            switch( molecule ) {
                case StatesOfMatterConstants.ARGON:
                    m_argonRadioButton.setSelected( true );
                    break;
                case StatesOfMatterConstants.NEON:
                    m_neonRadioButton.setSelected( true );
                    break;
                case StatesOfMatterConstants.DIATOMIC_OXYGEN:
                    m_oxygenRadioButton.setSelected( true );
                    break;
                case StatesOfMatterConstants.WATER:
                    m_waterRadioButton.setSelected( true );
                    break;
                case StatesOfMatterConstants.USER_DEFINED_MOLECULE:
                    m_configurableRadioButton.setSelected( true );
                    break;
            }
        }
    }

    /**
     * Convenience class that creates the radio button with the label and adds
     * the listener.
     */
    private class MoleculeSelectorButton extends JRadioButton {
        private final Font LABEL_FONT = new PhetFont( Font.PLAIN, 14 );

        private MoleculeSelectorButton( String text, final MultipleParticleModel model, final int moleculeID, final boolean isMolecular ) {
            super( text );
            setFont( LABEL_FONT );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( model.getMoleculeType() != moleculeID ) {
                        model.setMoleculeType( moleculeID );
                        m_interactionPotentialDiagram.setMolecular( isMolecular );
                        m_phaseDiagram.setDepictingWater( moleculeID == StatesOfMatterConstants.WATER );
                        updateVisibilityStates();
                    }
                }
            } );
        }
    }

    /**
     * Layout strategy for slider.
     */
    public class SliderLayoutStrategy implements ILayoutStrategy {

        public SliderLayoutStrategy() {
        }

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

        private final Font LABEL_FONT = new PhetFont( 14, false );

        private final LinearValueControl m_interactionStrengthControl;
        private final MultipleParticleModel m_model;
        private final TitledBorder m_titledBorder;
        private final JLabel m_leftLabel;
        private final JLabel m_rightLabel;

        public InteractionStrengthControlPanel( MultipleParticleModel model ) {


            m_model = model;

            setLayout( new GridLayout( 0, 1 ) );

            // Create the border.
            BevelBorder baseBorder = (BevelBorder) BorderFactory.createRaisedBevelBorder();
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
                public void stateChanged( ChangeEvent e ) {
                    // Set the interaction strength in the model if the molecule is correct.
                    if ( m_model.getMoleculeType() == StatesOfMatterConstants.USER_DEFINED_MOLECULE ) {
                        m_model.setEpsilon( m_interactionStrengthControl.getValue() );
                    }
                }
            } );
            m_interactionStrengthControl.getSlider().addMouseListener( new MouseAdapter() {
                public void mousePressed( MouseEvent e ) {
                    // TODO: Add this back if needed, and when implemented.
                    //m_model.setParticleMotionPaused(true);
                }

                public void mouseReleased( MouseEvent e ) {
                    // TODO: Add this back if needed, and when implemented.
                    //m_model.setParticleMotionPaused(false);
                }
            } );
            Hashtable diameterControlLabelTable = new Hashtable();
            m_leftLabel = new JLabel( StatesOfMatterStrings.INTERACTION_STRENGTH_WEAK );
            m_leftLabel.setFont( LABEL_FONT );
            diameterControlLabelTable.put( m_interactionStrengthControl.getMinimum(), m_leftLabel );
            m_rightLabel = new JLabel( StatesOfMatterStrings.INTERACTION_STRENGTH_STRONG );
            m_rightLabel.setFont( LABEL_FONT );
            diameterControlLabelTable.put( m_interactionStrengthControl.getMaximum(), m_rightLabel );
            m_interactionStrengthControl.setTickLabels( diameterControlLabelTable );

            // Register as a listener with the model so that we know when the
            // settings for potential are changed.
            m_model.addListener( new MultipleParticleModel.Adapter() {
                public void interactionStrengthChanged() {
                    double epsilon = m_model.getEpsilon();
                    epsilon = Math.min( epsilon, MultipleParticleModel.MAX_ADJUSTABLE_EPSILON );
                    epsilon = Math.max( epsilon, MultipleParticleModel.MIN_ADJUSTABLE_EPSILON );
                    m_interactionStrengthControl.setValue( epsilon );
                    updatePhaseDiagram();
                }
            } );

            add( m_interactionStrengthControl );
        }

        public void setEnabled( boolean enabled ) {
            super.setEnabled( enabled );
            m_interactionStrengthControl.setEnabled( enabled );
            m_leftLabel.setEnabled( enabled );
            m_rightLabel.setEnabled( enabled );
            if ( enabled ) {
                m_titledBorder.setTitleColor( ENABLED_TITLE_COLOR );
            }
            else {
                m_titledBorder.setTitleColor( Color.LIGHT_GRAY );
            }
        }
    }

    private JPanel createVerticalSpacerPanel( int space ) {
        if ( space <= 0 ) {
            throw new IllegalArgumentException( "Can't have zero or negative space in spacer panel." );
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
    private void updateVisibilityStates() {

        m_interactionDiagramPanel.setVisible( m_interactionDiagramVisible && m_advanced );
        m_interactionDiagramCtrlButton.setVisible( !m_interactionDiagramVisible && m_advanced );
        m_preInteractionButtonSpacer.setVisible( !m_interactionDiagramVisible );
        m_postInteractionButtonSpacer.setVisible( !m_interactionDiagramVisible );

        boolean userDefinedMoleculeSelected =
                m_model.getMoleculeType() == StatesOfMatterConstants.USER_DEFINED_MOLECULE;

        m_interactionStrengthControlPanel.setVisible( userDefinedMoleculeSelected );

        if ( userDefinedMoleculeSelected ) {
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
        else {
            m_phaseDiagramPanel.setVisible( m_phaseDiagramVisible );
            m_phaseDiagramCtrlButton.setVisible( !m_phaseDiagramVisible );
            m_prePhaseButtonSpacer.setVisible( !m_phaseDiagramVisible );
            m_postPhaseButtonSpacer.setVisible( !m_phaseDiagramVisible );
        }
    }
}
