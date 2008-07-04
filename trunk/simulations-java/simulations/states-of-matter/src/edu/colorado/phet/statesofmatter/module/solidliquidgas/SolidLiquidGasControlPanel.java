/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.solidliquidgas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.StatesOfMatterParticleType;


public class SolidLiquidGasControlPanel extends ControlPanel {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    MultipleParticleModel m_model;
    
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
        
        // Set the control panel's minimum width.
        int minimumWidth = StatesOfMatterResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Add the panel that allows the user to select the phase state.
        addControlFullWidth( new StateSelectionPanel() );
        
        // Add the panel that allows the user to select molecule type.
        addControlFullWidth( new MoleculeSelectionPanel() );
        
        // Add the panel that allows the user to control the system temperature.
        addControlFullWidth( new ParticleSystemControlPanel() );
        
        // Add the Reset All button.
        addVerticalSpace( 10 );
        addResetAllButton( solidLiquidGasModule );
    }
    
    //----------------------------------------------------------------------------
    // Inner Classes
    //----------------------------------------------------------------------------
    
    private class StateSelectionPanel extends JPanel {
        
        private JRadioButton m_solidRadioButton;
        private JRadioButton m_liquidRadioButton;
        private JRadioButton m_gasRadioButton;
        
        StateSelectionPanel(){
            
            setLayout( new GridLayout(0, 1) );
            
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    StatesOfMatterStrings.STATE_TYPE_SELECT_LABEL,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    Color.GRAY );
            
            setBorder( titledBorder );

            m_solidRadioButton = new JRadioButton( StatesOfMatterStrings.PHASE_STATE_SOLID );
            m_solidRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_solidRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setPhase( MultipleParticleModel.PHASE_SOLID );
                }
            } );
            m_liquidRadioButton = new JRadioButton( StatesOfMatterStrings.PHASE_STATE_LIQUID );
            m_liquidRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_liquidRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setPhase( MultipleParticleModel.PHASE_LIQUID );
                }
            } );
            m_gasRadioButton = new JRadioButton( StatesOfMatterStrings.PHASE_STATE_GAS );
            m_gasRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_gasRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setPhase( MultipleParticleModel.PHASE_GAS );
                }
            } );
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( m_solidRadioButton );
            buttonGroup.add( m_liquidRadioButton );
            buttonGroup.add( m_gasRadioButton );
            m_solidRadioButton.setSelected( true );
            
            add( m_solidRadioButton );
            add( m_liquidRadioButton );
            add( m_gasRadioButton );
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
                    m_model.setMolecule( MultipleParticleModel.DIATOMIC_OXYGEN );
                }
            } );
            m_neonRadioButton = new JRadioButton( StatesOfMatterStrings.NEON_SELECTION_LABEL );
            m_neonRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_neonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setMolecule( MultipleParticleModel.NEON );
                }
            } );
            m_argonRadioButton = new JRadioButton( StatesOfMatterStrings.ARGON_SELECTION_LABEL );
            m_argonRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_argonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setMolecule( MultipleParticleModel.ARGON );
                }
            } );
            m_waterRadioButton = new JRadioButton( "Water" ); // TODO: JPB TBD - Make into a string resource if kept.
            m_waterRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_waterRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setMolecule( MultipleParticleModel.WATER );
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
    }
    
    private class ParticleSystemControlPanel extends JPanel {
        
        private LinearValueControl m_gravitationalAccControl;
        
        ParticleSystemControlPanel(){
            
            setLayout( new GridLayout(0, 1) );
            
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    "System Control", // JPB TBD - Make this into a string if we keep it.
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    Color.GRAY );
            
            setBorder( titledBorder );
            
            // Register as a listener with the model so that we know when it gets
            // reset.
            m_model.addListener( new MultipleParticleModel.Adapter(){
                public void resetOccurred(){
                    m_gravitationalAccControl.setValue( m_model.getGravitationalAcceleration() );
                }
            });
            
            // Add the slider that controls the gravitational acceleration of the system.
            m_gravitationalAccControl = new LinearValueControl( 0, 0.4, "Gravity", "##.##", "Control" ); // TODO: JPB TBD - Make this a string if we keep it.
            m_gravitationalAccControl.setUpDownArrowDelta( 0.0025 );
            m_gravitationalAccControl.setTextFieldEditable( true );
            m_gravitationalAccControl.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_gravitationalAccControl.setTickPattern( "0.0" );
            m_gravitationalAccControl.setMajorTickSpacing( 0.1 );
            m_gravitationalAccControl.setMinorTickSpacing( 0.05 );
            m_gravitationalAccControl.setBorder( BorderFactory.createEtchedBorder() );
            m_gravitationalAccControl.setValue( m_model.getTemperature() );
            m_gravitationalAccControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    m_model.setGravitationalAcceleration( m_gravitationalAccControl.getValue() );
                }
            });
            
            add(m_gravitationalAccControl);
        }
    }
}
