/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.solidliquidgas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
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
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;


public class SolidLiquidGasControlPanel extends ControlPanel {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final Font BUTTON_FONT = new PhetFont( Font.PLAIN, 14 );
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
     * @param parentFrame parent frame, for creating dialogs
     */
    public SolidLiquidGasControlPanel( SolidLiquidGasModule solidLiquidGasModule, Frame parentFrame ) {
        
        super();
        m_model = solidLiquidGasModule.getMultiParticleModel();
     
        // Register for model events that may affect us.
        m_model.addListener( new MultipleParticleModel.Adapter(){
        	public void moleculeTypeChanged(){
        		m_moleculeSelectionPanel.setMolecule(m_model.getMoleculeType());
        	}
        });
        
        // Set the control panel's minimum width.
        int minimumWidth = StatesOfMatterResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Add the panel that allows the user to select molecule type.
        m_moleculeSelectionPanel = new MoleculeSelectionPanel();
        addControlFullWidth( m_moleculeSelectionPanel );
        
        // Add the panel that allows the user to select the phase state.
        m_stateSelectionPanel = new ChangeStateControlPanel();
        addControlFullWidth( m_stateSelectionPanel );
        
        // Add the Reset All button.
        addVerticalSpace( 10 );
        addResetAllButton( solidLiquidGasModule );
    }
    
    //----------------------------------------------------------------------------
    // Inner Classes
    //----------------------------------------------------------------------------

    /**
     * This class defines the panel that allows the user to immediately change
     * the state of the current molecules.
     */
    private class ChangeStateControlPanel extends JPanel {
        
        private JButton m_solidButton;
        private JButton m_liquidButton;
        private JButton m_gasButton;
        
        ChangeStateControlPanel(){
            
            setLayout( new GridLayout(3, 2) );
            
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    StatesOfMatterStrings.FORCE_STATE_CHANGE,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    Color.GRAY );
            
            setBorder( titledBorder );

            // Create the images used to depict the various states.
            
            BufferedImage image = StatesOfMatterResources.getImage( StatesOfMatterConstants.ICE_CUBE_IMAGE );
            double scaleFactor = (double)((double)MATTER_STATE_ICON_HEIGHT / (double)(image.getHeight()));
            image = BufferedImageUtils.rescaleFractional( image, scaleFactor, scaleFactor );
            ImageIcon icon = new ImageIcon( image );
            JLabel solidIcon = new JLabel( icon );

            image = StatesOfMatterResources.getImage( StatesOfMatterConstants.LIQUID_IMAGE );
            scaleFactor = (double)((double)MATTER_STATE_ICON_HEIGHT / (double)(image.getHeight()));
            image = BufferedImageUtils.rescaleFractional( image, scaleFactor, scaleFactor );
            icon = new ImageIcon( image );
            JLabel liquidIcon = new JLabel( icon );

            image = StatesOfMatterResources.getImage( StatesOfMatterConstants.GAS_IMAGE );
            scaleFactor = (double)((double)MATTER_STATE_ICON_HEIGHT / (double)(image.getHeight()));
            image = BufferedImageUtils.rescaleFractional( image, scaleFactor, scaleFactor );
            icon = new ImageIcon( image );
            JLabel gasIcon = new JLabel( icon );

            // Create and set up the buttons which the user will press to
            // initiate a state change.
            
            m_solidButton = new JButton( StatesOfMatterStrings.PHASE_STATE_SOLID );
            m_solidButton.setFont( BUTTON_FONT );
            m_solidButton.setAlignmentX( Component.CENTER_ALIGNMENT );
            m_solidButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setPhase( MultipleParticleModel.PHASE_SOLID );
                }
            } );

            m_liquidButton = new JButton( StatesOfMatterStrings.PHASE_STATE_LIQUID );
            m_liquidButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_liquidButton.setAlignmentX( Component.CENTER_ALIGNMENT );
            m_liquidButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setPhase( MultipleParticleModel.PHASE_LIQUID );
                }
            } );
            
            m_gasButton = new JButton( StatesOfMatterStrings.PHASE_STATE_GAS );
            m_gasButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_gasButton.setAlignmentX( Component.CENTER_ALIGNMENT );
            m_gasButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setPhase( MultipleParticleModel.PHASE_GAS );
                }
            } );
            
            // Add the various components to the panel.
            add( solidIcon );
            add( m_solidButton );
            add( liquidIcon );
            add( m_liquidButton );
            add( gasIcon );
            add( m_gasButton );
        }
    }

    /**
     * This class defines the selection panel that allows the user to choose
     * the type of molecule.
     */
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
                    if (m_model.getMoleculeType() != StatesOfMatterConstants.DIATOMIC_OXYGEN){
                        m_model.setMoleculeType( StatesOfMatterConstants.DIATOMIC_OXYGEN );
                    }
                }
            } );
            m_neonRadioButton = new JRadioButton( StatesOfMatterStrings.NEON_SELECTION_LABEL );
            m_neonRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_neonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if (m_model.getMoleculeType() != StatesOfMatterConstants.NEON){
                        m_model.setMoleculeType( StatesOfMatterConstants.NEON );
                    }
                }
            } );
            m_argonRadioButton = new JRadioButton( StatesOfMatterStrings.ARGON_SELECTION_LABEL );
            m_argonRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_argonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if (m_model.getMoleculeType() != StatesOfMatterConstants.ARGON){
                        m_model.setMoleculeType( StatesOfMatterConstants.ARGON );
                    }
                }
            } );
            m_waterRadioButton = new JRadioButton( StatesOfMatterStrings.WATER_SELECTION_LABEL );
            m_waterRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_waterRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if (m_model.getMoleculeType() != StatesOfMatterConstants.WATER){
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
}
