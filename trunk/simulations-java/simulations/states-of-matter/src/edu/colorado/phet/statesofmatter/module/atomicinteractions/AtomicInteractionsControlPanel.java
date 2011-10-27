// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.module.atomicinteractions;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
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
import edu.colorado.phet.statesofmatter.model.AtomType;
import edu.colorado.phet.statesofmatter.model.DualAtomModel;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.view.ParticleForceNode;


/**
 * This class implements the control panel for the Interaction Potential
 * portion of this simulation.
 *
 * @author John Blanco
 */
public class AtomicInteractionsControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final Font LABEL_FONT = new PhetFont( Font.PLAIN, 14 );
    private static final Font BOLD_LABEL_FONT = new PhetFont( Font.BOLD, 14 );
    private static final Color ENABLED_TITLE_COLOR = Color.BLACK;
    private static final int PIN_ICON_WIDTH = 30; // In pixels.
    private static final int PIN_ICON_HEIGHT = 32; // In pixels.

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private final DualAtomModel m_model;
    private final AtomicInteractionsCanvas m_canvas;
    private AtomSelectionPanel m_moleculeSelectionPanel;
    private final AtomDiameterControlPanel m_atomDiameterControlPanel;
    private final InteractionStrengthControlPanel m_interactionStrengthControlPanel;
    private final ForceControlPanel m_forceControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public AtomicInteractionsControlPanel( AtomicInteractionsModule solidLiquidGasModule, Frame parentFrame,
                                           boolean enableHeterogeneousAtoms ) {

        m_model = solidLiquidGasModule.getDualParticleModel();
        m_canvas = solidLiquidGasModule.getCanvas();

        m_model.addListener( new DualAtomModel.Adapter() {
            public void fixedAtomAdded( StatesOfMatterAtom particle ) {
                m_moleculeSelectionPanel.updateMoleculeType();
            }

            public void movableAtomAdded( StatesOfMatterAtom particle ) {
                m_moleculeSelectionPanel.updateMoleculeType();
            }
        } );

        // Set the control panel's minimum width.
        int minimumWidth = StatesOfMatterResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );

        // Create the panel for controlling the atom diameter.
        m_atomDiameterControlPanel = new AtomDiameterControlPanel( m_model );

        // Create the panel for controlling the interaction strength.
        m_interactionStrengthControlPanel = new InteractionStrengthControlPanel( m_model );

        // Create the panel that allows the user to select molecule type.
        if ( enableHeterogeneousAtoms ) {
            m_moleculeSelectionPanel = new HeterogeneousAtomSelectionPanel();
        }
        else {
            m_moleculeSelectionPanel = new HomogeneousAtomSelectionPanel();
        }

        // Create the panel that allows the user to control which forces are
        // shown on the atoms.
        m_forceControlPanel = new ForceControlPanel();

        // Add the panels we just created.
        addControlFullWidth( (JPanel) m_moleculeSelectionPanel );
        addControlFullWidth( m_atomDiameterControlPanel );
        addControlFullWidth( m_interactionStrengthControlPanel );
        addControlFullWidth( m_forceControlPanel );

        // Add a reset button.
        addVerticalSpace( 10 );
        JButton resetButton = new JButton( StatesOfMatterStrings.RESET );
        resetButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                m_model.reset();
                if ( m_model.getClock().isPaused() ) {
                    // If the clock was paused when the reset button was
                    // pressed, it must be explicitly un-paused.
                    m_model.getClock().setPaused( false );
                }
                m_forceControlPanel.reset();
            }
        } );
        addControl( resetButton );
    }

    //----------------------------------------------------------------------------
    // Inner Classes
    //----------------------------------------------------------------------------

    private interface AtomSelectionPanel {
        public void updateMoleculeType();
    }

    /**
     * This class defines the selection panel that allows the user to choose
     * the type of molecule when both are the same.
     */
    private class HomogeneousAtomSelectionPanel extends JPanel implements AtomSelectionPanel {

        private final JRadioButton m_neonRadioButton;
        private final JRadioButton m_argonRadioButton;
        private final JRadioButton m_adjustableAttractionRadioButton;

        HomogeneousAtomSelectionPanel() {

            setLayout( new GridLayout( 0, 1 ) );

            BevelBorder baseBorder = (BevelBorder) BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                                                                          StatesOfMatterStrings.INTERACTION_POTENTIAL_ATOM_SELECT_LABEL,
                                                                          TitledBorder.LEFT,
                                                                          TitledBorder.TOP,
                                                                          new PhetFont( Font.BOLD, 14 ),
                                                                          Color.BLACK );

            setBorder( titledBorder );

            m_neonRadioButton = new JRadioButton( StatesOfMatterStrings.NEON_SELECTION_LABEL );
            m_neonRadioButton.setFont( LABEL_FONT );
            m_neonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( m_model.getFixedAtomType() != AtomType.NEON ) {
                        m_model.setBothAtomTypes( AtomType.NEON );
                        updateLjControlSliderState();
                    }
                }
            } );
            m_argonRadioButton = new JRadioButton( StatesOfMatterStrings.ARGON_SELECTION_LABEL );
            m_argonRadioButton.setFont( LABEL_FONT );
            m_argonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( m_model.getFixedAtomType() != AtomType.ARGON ) {
                        m_model.setBothAtomTypes( AtomType.ARGON );
                        updateLjControlSliderState();
                    }
                }
            } );
            m_adjustableAttractionRadioButton =
                    new JRadioButton( StatesOfMatterStrings.ADJUSTABLE_ATTRACTION_SELECTION_LABEL );
            m_adjustableAttractionRadioButton.setFont( LABEL_FONT );
            m_adjustableAttractionRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( m_model.getFixedAtomType() != AtomType.ADJUSTABLE ) {
                        m_model.setBothAtomTypes( AtomType.ADJUSTABLE );
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
        public void updateMoleculeType() {
            AtomType moleculeType = m_model.getFixedAtomType();

            if ( moleculeType == AtomType.NEON ) {
                m_neonRadioButton.setSelected( true );
            }
            else if ( moleculeType == AtomType.ARGON ) {
                m_argonRadioButton.setSelected( true );
            }
            else {
                m_adjustableAttractionRadioButton.setSelected( true );
            }

            updateLjControlSliderState();
        }

        /**
         * Update the state (i.e. enabled or disabled) of the sliders that
         * control the Lennard-Jones parameters.
         */
        private void updateLjControlSliderState() {
            m_atomDiameterControlPanel.setVisible( m_model.getFixedAtomType() == AtomType.ADJUSTABLE );
            m_interactionStrengthControlPanel.setVisible( m_model.getFixedAtomType() == AtomType.ADJUSTABLE );
        }
    }

    /**
     * Allows user to select from a fixed list of heterogeneous and
     * homogeneous combinations of atoms.
     */
    private class HeterogeneousAtomSelectionPanel extends VerticalLayoutPanel implements AtomSelectionPanel {

        private final JRadioButton m_neonNeonRadioButton;
        private final JRadioButton m_argonArgonRadioButton;
        private final JRadioButton m_oxygenOxygenRadioButton;
        private final JRadioButton m_neonArgonRadioButton;
        private final JRadioButton m_neonOxygenRadioButton;
        private final JRadioButton m_argonOxygenRadioButton;
        private final JRadioButton m_adjustableInteractionRadioButton;

        /**
         * Constructor.
         */
        HeterogeneousAtomSelectionPanel() {

            BevelBorder baseBorder = (BevelBorder) BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                                                                          StatesOfMatterStrings.INTERACTION_POTENTIAL_ATOM_SELECT_LABEL,
                                                                          TitledBorder.LEFT,
                                                                          TitledBorder.TOP,
                                                                          new PhetFont( Font.BOLD, 14 ),
                                                                          Color.BLACK );

            setBorder( titledBorder );

            // Create and group the buttons that select the atom combinations.
            // Each button is labeled with the fixed atom choice and the other
            // atom label must be added to the control panel in the
            // appropriate place.

            m_neonNeonRadioButton = new JRadioButton( StatesOfMatterStrings.NEON_SELECTION_LABEL );
            m_neonNeonRadioButton.setFont( LABEL_FONT );
            m_neonNeonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setFixedAtom( AtomType.NEON );
                    setMovableAtom( AtomType.NEON );
                    updateLjControlSliderState();
                }
            } );
            m_neonArgonRadioButton = new JRadioButton( StatesOfMatterStrings.NEON_SELECTION_LABEL );
            m_neonArgonRadioButton.setFont( LABEL_FONT );
            m_neonArgonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setFixedAtom( AtomType.NEON );
                    setMovableAtom( AtomType.ARGON );
                    updateLjControlSliderState();
                }
            } );
            m_neonOxygenRadioButton = new JRadioButton( StatesOfMatterStrings.NEON_SELECTION_LABEL );
            m_neonOxygenRadioButton.setFont( LABEL_FONT );
            m_neonOxygenRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setFixedAtom( AtomType.NEON );
                    setMovableAtom( AtomType.OXYGEN );
                    updateLjControlSliderState();
                }
            } );
            m_argonArgonRadioButton = new JRadioButton( StatesOfMatterStrings.ARGON_SELECTION_LABEL );
            m_argonArgonRadioButton.setFont( LABEL_FONT );
            m_argonArgonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setBothAtomTypes( AtomType.ARGON );
                    updateLjControlSliderState();
                }
            } );
            m_argonOxygenRadioButton = new JRadioButton( StatesOfMatterStrings.ARGON_SELECTION_LABEL );
            m_argonOxygenRadioButton.setFont( LABEL_FONT );
            m_argonOxygenRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setFixedAtom( AtomType.ARGON );
                    setMovableAtom( AtomType.OXYGEN );
                    updateLjControlSliderState();
                }
            } );
            m_oxygenOxygenRadioButton = new JRadioButton( StatesOfMatterStrings.OXYGEN_SELECTION_LABEL );
            m_oxygenOxygenRadioButton.setFont( LABEL_FONT );
            m_oxygenOxygenRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setBothAtomTypes( AtomType.OXYGEN );
                    updateLjControlSliderState();
                }
            } );
            m_adjustableInteractionRadioButton = new JRadioButton( StatesOfMatterStrings.ADJUSTABLE_ATTRACTION_SELECTION_LABEL );
            m_adjustableInteractionRadioButton.setFont( LABEL_FONT );
            m_adjustableInteractionRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setBothAtomTypes( AtomType.ADJUSTABLE );
                    updateLjControlSliderState();
                }
            } );

            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( m_neonNeonRadioButton );
            buttonGroup.add( m_argonArgonRadioButton );
            buttonGroup.add( m_oxygenOxygenRadioButton );
            buttonGroup.add( m_neonArgonRadioButton );
            buttonGroup.add( m_neonOxygenRadioButton );
            buttonGroup.add( m_argonOxygenRadioButton );
            buttonGroup.add( m_adjustableInteractionRadioButton );
            m_neonNeonRadioButton.setSelected( true );

            // Create the labels for the fixed and movable atom selection.

            Image pinImage = StatesOfMatterResources.getImage( StatesOfMatterConstants.PUSH_PIN_IMAGE );
            Image scaledPinImage = pinImage.getScaledInstance( PIN_ICON_WIDTH, PIN_ICON_HEIGHT, Image.SCALE_SMOOTH );
            Icon pinIcon = new ImageIcon( scaledPinImage );
            JLabel fixedAtomLabel = new JLabel( StatesOfMatterStrings.FIXED_ATOM_LABEL, pinIcon, JLabel.LEFT );
            fixedAtomLabel.setFont( BOLD_LABEL_FONT );
            JLabel movableAtomLabel = new JLabel( StatesOfMatterStrings.MOVABLE_ATOM_LABEL );
            movableAtomLabel.setFont( BOLD_LABEL_FONT );
            JPanel labelPanel = new JPanel();
            labelPanel.setLayout( new GridLayout( 1, 2, 0, 0 ) );
            labelPanel.add( fixedAtomLabel );
            labelPanel.add( movableAtomLabel );

            // Create a panel and add all the buttons and labels for selecting
            // atoms.

            JPanel selectionPanel = new JPanel();
            selectionPanel.setLayout( new GridLayout( 0, 2 ) );
            selectionPanel.add( m_neonNeonRadioButton );
            selectionPanel.add( new PhetJLabel( StatesOfMatterStrings.NEON_SELECTION_LABEL ) );
            selectionPanel.add( m_argonArgonRadioButton );
            selectionPanel.add( new PhetJLabel( StatesOfMatterStrings.ARGON_SELECTION_LABEL ) );
            selectionPanel.add( m_oxygenOxygenRadioButton );
            selectionPanel.add( new PhetJLabel( StatesOfMatterStrings.OXYGEN_SELECTION_LABEL ) );
            selectionPanel.add( m_neonArgonRadioButton );
            selectionPanel.add( new PhetJLabel( StatesOfMatterStrings.ARGON_SELECTION_LABEL ) );
            selectionPanel.add( m_neonOxygenRadioButton );
            selectionPanel.add( new PhetJLabel( StatesOfMatterStrings.OXYGEN_SELECTION_LABEL ) );
            selectionPanel.add( m_argonOxygenRadioButton );
            selectionPanel.add( new PhetJLabel( StatesOfMatterStrings.OXYGEN_SELECTION_LABEL ) );

            // Insert a spacer panel to make space between the combos and the
            // adjustable selection.
            JPanel spacePanel = new JPanel();
            spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
            spacePanel.add( Box.createVerticalStrut( 12 ) );

            // Put the adjustable atom in a panel by itself so that it doesn't
            // mess up the grid layout.
            JPanel adjustableInteractionPanel = new JPanel();
            adjustableInteractionPanel.setLayout( new GridLayout( 0, 1 ) );
            adjustableInteractionPanel.add( m_adjustableInteractionRadioButton );

            // Add everything to the main panel.
            add( labelPanel );
            add( selectionPanel );
            add( spacePanel );
            add( adjustableInteractionPanel );

            // Make some updates.
            updateMoleculeType();
            updateLjControlSliderState();
        }

        /**
         * Update the selected molecule to match whatever the model believes
         * it to be.
         */
        public void updateMoleculeType() {
            AtomType fixedMoleculeType = m_model.getFixedAtomType();
            AtomType movableMoleculeType = m_model.getMovableAtomType();

            if ( fixedMoleculeType == AtomType.NEON && movableMoleculeType == AtomType.NEON ) {
                m_neonNeonRadioButton.setSelected( true );
            }
            else if ( fixedMoleculeType == AtomType.NEON && movableMoleculeType == AtomType.ARGON ) {
                m_neonArgonRadioButton.setSelected( true );
            }
            else if ( fixedMoleculeType == AtomType.NEON && movableMoleculeType == AtomType.OXYGEN ) {
                m_neonOxygenRadioButton.setSelected( true );
            }
            else if ( fixedMoleculeType == AtomType.ARGON && movableMoleculeType == AtomType.ARGON ) {
                m_argonArgonRadioButton.setSelected( true );
            }
            else if ( fixedMoleculeType == AtomType.ARGON && movableMoleculeType == AtomType.OXYGEN ) {
                m_argonOxygenRadioButton.setSelected( true );
            }
            else if ( fixedMoleculeType == AtomType.OXYGEN && movableMoleculeType == AtomType.OXYGEN ) {
                m_oxygenOxygenRadioButton.setSelected( true );
            }
            else if ( fixedMoleculeType == AtomType.ADJUSTABLE && movableMoleculeType == AtomType.ADJUSTABLE ) {
                m_adjustableInteractionRadioButton.setSelected( true );
            }
            else {
                // This will happen since a notification is received for
                // both the fixed and movable atom, so it can safely be
                // ignored.
            }

            updateLjControlSliderState();
        }

        /**
         * Update the state (i.e. enabled or disabled) of the sliders that
         * control the Lennard-Jones parameters.
         */
        private void updateLjControlSliderState() {
            m_atomDiameterControlPanel.setVisible( m_model.getFixedAtomType() == AtomType.ADJUSTABLE );
            m_interactionStrengthControlPanel.setVisible( m_model.getFixedAtomType() == AtomType.ADJUSTABLE );
        }

        private void setFixedAtom( AtomType atomType ) {
            if ( m_model.getMovableAtomType() == AtomType.ADJUSTABLE ) {
                // Can't have one adjustable and the other not, so we need to set both.
                m_model.setBothAtomTypes( atomType );
            }
            else {
                m_model.setFixedAtomType( atomType );
            }
        }

        private void setMovableAtom( AtomType atomType ) {
            if ( m_model.getFixedAtomType() == AtomType.ADJUSTABLE ) {
                // Can't have one adjustable and the other not, so we need to set both.
                m_model.setBothAtomTypes( atomType );
            }
            else {
                m_model.setMovableAtomType( atomType );
            }
        }
    }

    /**
     * This class represents the control slider for the atom diameter.
     */
    private class AtomDiameterControlPanel extends JPanel {

        private final Font LABEL_FONT = new PhetFont( 14, false );

        private final LinearValueControl m_atomDiameterControl;
        private final DualAtomModel m_model;
        private final TitledBorder m_titledBorder;
        private final JLabel m_leftLabel;
        private final JLabel m_rightLabel;

        public AtomDiameterControlPanel( DualAtomModel model ) {

            m_model = model;

            setLayout( new GridLayout( 0, 1 ) );

            // Create the border.
            BevelBorder baseBorder = (BevelBorder) BorderFactory.createRaisedBevelBorder();
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
                    m_model.setAdjustableAtomSigma( m_atomDiameterControl.getValue() );
                }
            } );
            m_atomDiameterControl.getSlider().addMouseListener( new MouseAdapter() {
                public void mousePressed( MouseEvent e ) {
                    m_model.setMotionPaused( true );
                }

                public void mouseReleased( MouseEvent e ) {
                    m_model.setMotionPaused( false );
                }
            } );
            Hashtable diameterControlLabelTable = new Hashtable();
            m_leftLabel = new JLabel( StatesOfMatterStrings.ATOM_DIAMETER_SMALL );
            m_leftLabel.setFont( LABEL_FONT );
            diameterControlLabelTable.put( m_atomDiameterControl.getMinimum(), m_leftLabel );
            m_rightLabel = new JLabel( StatesOfMatterStrings.ATOM_DIAMETER_LARGE );
            m_rightLabel.setFont( LABEL_FONT );
            diameterControlLabelTable.put( m_atomDiameterControl.getMaximum(), m_rightLabel );
            m_atomDiameterControl.setTickLabels( diameterControlLabelTable );
            m_atomDiameterControl.setValue( m_model.getSigma() );
            add( m_atomDiameterControl );

            // Register as a listener with the model for relevant events.
            m_model.addListener( new DualAtomModel.Adapter() {
                public void fixedAtomDiameterChanged() {
                    m_atomDiameterControl.setValue( m_model.getSigma() );
                }

                public void movableAtomDiameterChanged() {
                    m_atomDiameterControl.setValue( m_model.getSigma() );
                }
            } );

        }

        public void setEnabled( boolean enabled ) {
            super.setEnabled( enabled );
            m_atomDiameterControl.setEnabled( enabled );
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

    /**
     * This class represents the control slider for the interaction strength.
     */
    private class InteractionStrengthControlPanel extends JPanel {

        private final Font LABEL_FONT = new PhetFont( 14, false );

        private final LinearValueControl m_interactionStrengthControl;
        private final DualAtomModel m_model;
        private final TitledBorder m_titledBorder;
        private final JLabel m_leftLabel;
        private final JLabel m_rightLabel;

        public InteractionStrengthControlPanel( DualAtomModel model ) {

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
            m_interactionStrengthControl = new LinearValueControl( StatesOfMatterConstants.MIN_EPSILON,
                                                                   StatesOfMatterConstants.MAX_EPSILON, "", "0", "", new SliderLayoutStrategy() );
            m_interactionStrengthControl.setValue( m_model.getEpsilon() );
            m_interactionStrengthControl.setUpDownArrowDelta( 0.01 );
            m_interactionStrengthControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    // Set the interaction strength in the model.
                    m_model.setEpsilon( m_interactionStrengthControl.getValue() );
                }
            } );
            m_interactionStrengthControl.getSlider().addMouseListener( new MouseAdapter() {
                public void mousePressed( MouseEvent e ) {
                    m_model.setMotionPaused( true );
                }

                public void mouseReleased( MouseEvent e ) {
                    m_model.setMotionPaused( false );
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
            m_model.addListener( new DualAtomModel.Adapter() {
                public void interactionPotentialChanged() {
                    m_interactionStrengthControl.setValue( m_model.getEpsilon() );
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

    private class ForceControlPanel extends VerticalLayoutPanel {

        private final JRadioButton m_noForcesButton;
        private final JRadioButton m_totalForcesButton;
        private final JRadioButton m_componentForceButton;
        private final JLabel m_attractiveForceLegendEntry;
        private final JLabel m_repulsiveForceLegendEntry;

        public ForceControlPanel() {

            BevelBorder baseBorder = (BevelBorder) BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                                                                          StatesOfMatterStrings.INTERACTION_POTENTIAL_SHOW_FORCES,
                                                                          TitledBorder.LEFT,
                                                                          TitledBorder.TOP,
                                                                          new PhetFont( Font.BOLD, 14 ),
                                                                          Color.BLACK );

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

            m_totalForcesButton = new JRadioButton( StatesOfMatterStrings.INTERACTION_POTENTIAL_TOTAL_FORCES );
            m_totalForcesButton.setFont( LABEL_FONT );
            m_totalForcesButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_canvas.setShowAttractiveForces( false );
                    m_canvas.setShowRepulsiveForces( false );
                    m_canvas.setShowTotalForces( true );
                }
            } );
            JPanel totalForceButtonPanel = new JPanel();
            totalForceButtonPanel.setLayout( new java.awt.FlowLayout( java.awt.FlowLayout.LEFT, 0, 0 ) );
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
            attractiveForceLegendEntry.setLayout( new java.awt.FlowLayout( java.awt.FlowLayout.LEFT ) );
            m_attractiveForceLegendEntry = new JLabel( StatesOfMatterStrings.ATTRACTIVE_FORCE_KEY,
                                                       createArrowIcon( ParticleForceNode.ATTRACTIVE_FORCE_COLOR, false ), JLabel.LEFT );
            attractiveForceLegendEntry.add( spacePanel );
            attractiveForceLegendEntry.add( m_attractiveForceLegendEntry );

            spacePanel = new JPanel();
            spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.X_AXIS ) );
            spacePanel.add( Box.createHorizontalStrut( 14 ) );

            JPanel repulsiveForceLegendEntry = new JPanel();
            repulsiveForceLegendEntry.setLayout( new java.awt.FlowLayout( java.awt.FlowLayout.LEFT ) );
            m_repulsiveForceLegendEntry = new JLabel( StatesOfMatterStrings.REPULSIVE_FORCE_KEY,
                                                      createArrowIcon( ParticleForceNode.REPULSIVE_FORCE_COLOR, true ), JLabel.LEFT );
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
            ArrowNode arrowNode = new ArrowNode( new Point2D.Double( 0, 0 ), new Point2D.Double( 20, 0 ), 10, 15, 6 );
            arrowNode.setPaint( color );
            Image arrowImage = arrowNode.toImage();
            if ( !pointRight ) {
                arrowImage = BufferedImageUtils.flipX( BufferedImageUtils.toBufferedImage( arrowImage ) );
            }
            return ( new ImageIcon( arrowImage ) );
        }
    }

    /**
     * Layout strategy for sliders.
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

    public class PhetJLabel extends JLabel {

        public PhetJLabel( String text ) {
            super( text );
            setFont( LABEL_FONT );
        }
    }
}
