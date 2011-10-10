// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.module.solidliquidgas;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;

/**
 * Control panel for the Solid, Liquid, and Gas module.
 *
 * @author John Blanco
 */
public class SolidLiquidGasControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final Font BUTTON_FONT = new PhetFont( Font.PLAIN, 16 );
    private static final int MATTER_STATE_ICON_HEIGHT = 32;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    MultipleParticleModel m_model;
    ChangeStateControlPanel m_stateSelectionPanel;
    MoleculeSelectionPanel m_moleculeSelectionPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param solidLiquidGasModule
     * @param parentFrame          parent frame, for creating dialogs
     */
    public SolidLiquidGasControlPanel( SolidLiquidGasModule solidLiquidGasModule, Frame parentFrame ) {

        super();
        m_model = solidLiquidGasModule.getMultiParticleModel();

        // Register for model events that may affect us.
        m_model.addListener( new MultipleParticleModel.Adapter() {
            public void moleculeTypeChanged() {
                m_moleculeSelectionPanel.setMolecule( m_model.getMoleculeType() );
            }
        } );

        // Set the control panel's minimum width.
        int minimumWidth = StatesOfMatterResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );

        // Add the panel that allows the user to select molecule type.
        m_moleculeSelectionPanel = new MoleculeSelectionPanel( m_model );
        addControlFullWidth( m_moleculeSelectionPanel );

        // Add the panel that allows the user to select the phase state.
        m_stateSelectionPanel = new ChangeStateControlPanel();
        addControlFullWidth( m_stateSelectionPanel );
    }

    //----------------------------------------------------------------------------
    // Inner Classes
    //----------------------------------------------------------------------------

    /**
     * This class defines the panel that allows the user to immediately change
     * the state of the current molecules.
     */
    private class ChangeStateControlPanel extends VerticalLayoutPanel {

        private JButton m_solidButton;
        private JButton m_liquidButton;
        private JButton m_gasButton;

        ChangeStateControlPanel() {
            BevelBorder baseBorder = (BevelBorder) BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                                                                          StatesOfMatterStrings.FORCE_STATE_CHANGE,
                                                                          TitledBorder.LEFT,
                                                                          TitledBorder.TOP,
                                                                          new PhetFont( Font.BOLD, 14 ),
                                                                          Color.GRAY );

            setBorder( titledBorder );

            // Create the images used to depict the various states.

            BufferedImage image = StatesOfMatterResources.getImage( StatesOfMatterConstants.ICE_CUBE_IMAGE );
            double scaleFactor = ( (double) MATTER_STATE_ICON_HEIGHT / (double) ( image.getHeight() ) );
            image = BufferedImageUtils.rescaleFractional( image, scaleFactor, scaleFactor );
            ImageIcon solidIcon = new ImageIcon( image );

            image = StatesOfMatterResources.getImage( StatesOfMatterConstants.LIQUID_IMAGE );
            scaleFactor = ( (double) MATTER_STATE_ICON_HEIGHT / (double) ( image.getHeight() ) );
            image = BufferedImageUtils.rescaleFractional( image, scaleFactor, scaleFactor );
            ImageIcon liquidIcon = new ImageIcon( image );

            image = StatesOfMatterResources.getImage( StatesOfMatterConstants.GAS_IMAGE );
            scaleFactor = ( (double) MATTER_STATE_ICON_HEIGHT / (double) ( image.getHeight() ) );
            image = BufferedImageUtils.rescaleFractional( image, scaleFactor, scaleFactor );
            ImageIcon gasIcon = new ImageIcon( image );

            // Create and set up the buttons which the user will press to
            // initiate a state change.

            m_solidButton = new JButton( StatesOfMatterStrings.PHASE_STATE_SOLID, solidIcon );
            m_solidButton.setFont( BUTTON_FONT );
            m_solidButton.setAlignmentX( Component.CENTER_ALIGNMENT );
            m_solidButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setPhase( MultipleParticleModel.PHASE_SOLID );
                }
            } );

            m_liquidButton = new JButton( StatesOfMatterStrings.PHASE_STATE_LIQUID, liquidIcon );
            m_liquidButton.setFont( BUTTON_FONT );
            m_liquidButton.setAlignmentX( Component.CENTER_ALIGNMENT );
            m_liquidButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setPhase( MultipleParticleModel.PHASE_LIQUID );
                }
            } );

            m_gasButton = new JButton( StatesOfMatterStrings.PHASE_STATE_GAS, gasIcon );
            m_gasButton.setFont( BUTTON_FONT );
            m_gasButton.setAlignmentX( Component.CENTER_ALIGNMENT );
            m_gasButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setPhase( MultipleParticleModel.PHASE_GAS );
                }
            } );

            // Add the buttons to the panel.
            add( m_solidButton );
            add( m_liquidButton );
            add( m_gasButton );
        }
    }

    /**
     * This class defines the selection panel that allows the user to choose
     * the type of molecule.
     */
    private static class MoleculeSelectionPanel extends JPanel {

        private JRadioButton m_neonRadioButton;
        private JRadioButton m_argonRadioButton;
        private JRadioButton m_oxygenRadioButton;
        private JRadioButton m_waterRadioButton;

        MoleculeSelectionPanel( final MultipleParticleModel model ) {
            BevelBorder baseBorder = (BevelBorder) BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                                                                          StatesOfMatterStrings.MOLECULE_TYPE_SELECT_LABEL,
                                                                          TitledBorder.LEFT,
                                                                          TitledBorder.TOP,
                                                                          new PhetFont( Font.BOLD, 14 ),
                                                                          Color.GRAY );

            setBorder( titledBorder );

            m_oxygenRadioButton = new MoleculeSelectorButton( StatesOfMatterStrings.OXYGEN_SELECTION_LABEL, model, StatesOfMatterConstants.DIATOMIC_OXYGEN );
            final JLabel oxygenLabel = new MoleculeImageLabel( StatesOfMatterConstants.DIATOMIC_OXYGEN );
            m_neonRadioButton = new MoleculeSelectorButton( StatesOfMatterStrings.NEON_SELECTION_LABEL, model, StatesOfMatterConstants.NEON );
            final JLabel neonLabel = new MoleculeImageLabel( StatesOfMatterConstants.NEON );
            m_argonRadioButton = new MoleculeSelectorButton( StatesOfMatterStrings.ARGON_SELECTION_LABEL, model, StatesOfMatterConstants.ARGON );
            final JLabel argonLabel = new MoleculeImageLabel( StatesOfMatterConstants.ARGON );
            m_waterRadioButton = new MoleculeSelectorButton( StatesOfMatterStrings.WATER_SELECTION_LABEL, model, StatesOfMatterConstants.WATER );
            final JLabel waterLabel = new MoleculeImageLabel( StatesOfMatterConstants.WATER );

            // Put the buttons into a button group.
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( m_neonRadioButton );
            buttonGroup.add( m_argonRadioButton );
            buttonGroup.add( m_oxygenRadioButton );
            buttonGroup.add( m_waterRadioButton );
            m_neonRadioButton.setSelected( true );

            // Add the buttons and their icons.
            setLayout( new GridLayout( 4, 1 ) );
            add( new JPanel( new FlowLayout( FlowLayout.LEFT ) ) {{
                add( m_neonRadioButton );
                add( neonLabel );
            }} );
            add( new JPanel( new FlowLayout( FlowLayout.LEFT ) ) {{
                add( m_argonRadioButton );
                add( argonLabel );
            }} );
            add( new JPanel( new FlowLayout( FlowLayout.LEFT ) ) {{
                add( m_oxygenRadioButton );
                add( oxygenLabel );
            }} );
            add( new JPanel( new FlowLayout( FlowLayout.LEFT ) ) {{
                add( m_waterRadioButton );
                add( waterLabel );
            }} );
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
            }
        }
    }

    /**
     * Convenience class that creates the radio button with the label and adds
     * the listener.
     */
    private static class MoleculeSelectorButton extends JRadioButton {
        private static final Font LABEL_FONT = new PhetFont( Font.PLAIN, 14 );

        private MoleculeSelectorButton( String text, final MultipleParticleModel model, final int moleculeID ) {
            super( text );
            setFont( LABEL_FONT );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( model.getMoleculeType() != moleculeID ) {
                        model.setMoleculeType( moleculeID );
                    }
                }
            } );
        }
    }

    /**
     * This class defines the selection panel that allows the user to choose
     * the type of molecule.
     */
    private class MoleculeSelectionPanelOld extends JPanel {

        private JRadioButton m_neonRadioButton;
        private JRadioButton m_argonRadioButton;
        private JRadioButton m_oxygenRadioButton;
        private JRadioButton m_waterRadioButton;

        MoleculeSelectionPanelOld() {

            setLayout( new GridLayout( 0, 1 ) );

            BevelBorder baseBorder = (BevelBorder) BorderFactory.createRaisedBevelBorder();
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
                    if ( m_model.getMoleculeType() != StatesOfMatterConstants.DIATOMIC_OXYGEN ) {
                        m_model.setMoleculeType( StatesOfMatterConstants.DIATOMIC_OXYGEN );
                    }
                }
            } );
            m_neonRadioButton = new JRadioButton( StatesOfMatterStrings.NEON_SELECTION_LABEL );
            m_neonRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_neonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( m_model.getMoleculeType() != StatesOfMatterConstants.NEON ) {
                        m_model.setMoleculeType( StatesOfMatterConstants.NEON );
                    }
                }
            } );
            m_argonRadioButton = new JRadioButton( StatesOfMatterStrings.ARGON_SELECTION_LABEL );
            m_argonRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_argonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( m_model.getMoleculeType() != StatesOfMatterConstants.ARGON ) {
                        m_model.setMoleculeType( StatesOfMatterConstants.ARGON );
                    }
                }
            } );
            m_waterRadioButton = new JRadioButton( StatesOfMatterStrings.WATER_SELECTION_LABEL );
            m_waterRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_waterRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( m_model.getMoleculeType() != StatesOfMatterConstants.WATER ) {
                        m_model.setMoleculeType( StatesOfMatterConstants.WATER );
                    }
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
            }
        }
    }
}
