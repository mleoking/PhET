/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.exp2;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;


public class Exp2SolidLiquidGasControlPanel extends ControlPanel {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    MultipleParticleModel m_model;
    StateSelectionPanel m_stateSelectionPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param solidLiquidGasModule
     * @param parentFrame parent frame, for creating dialogs
     */
    public Exp2SolidLiquidGasControlPanel( Exp2SolidLiquidGasModule solidLiquidGasModule, Frame parentFrame ) {
        
        super();
        m_model = solidLiquidGasModule.getMultiParticleModel();
        
        // Set the control panel's minimum width.
        int minimumWidth = StatesOfMatterResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Add the panel that allows the user to select the phase state.
        m_stateSelectionPanel = new StateSelectionPanel();
        addControlFullWidth( m_stateSelectionPanel );
        
        // Add the panel that allows the user to select molecule type.
        addControlFullWidth( new MoleculeSelectionPanel() );
        
        // Add the panel that allows the user to control the system temperature.
        addControlFullWidth( new GravityControlPanel() );
        
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
                    StatesOfMatterStrings.FORCE_STATE_CHANGE,
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
        
        public void reset(){
            m_solidRadioButton.setSelected( true );
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
                    if (m_model.getMolecule() != MultipleParticleModel.DIATOMIC_OXYGEN){
                        Exp2SolidLiquidGasControlPanel.this.m_stateSelectionPanel.reset();
                        m_model.setMolecule( MultipleParticleModel.DIATOMIC_OXYGEN );
                    }
                }
            } );
            m_neonRadioButton = new JRadioButton( StatesOfMatterStrings.NEON_SELECTION_LABEL );
            m_neonRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_neonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if (m_model.getMolecule() != MultipleParticleModel.NEON){
                        Exp2SolidLiquidGasControlPanel.this.m_stateSelectionPanel.reset();
                        m_model.setMolecule( MultipleParticleModel.NEON );
                    }
                }
            } );
            m_argonRadioButton = new JRadioButton( StatesOfMatterStrings.ARGON_SELECTION_LABEL );
            m_argonRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_argonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if (m_model.getMolecule() != MultipleParticleModel.ARGON){
                        Exp2SolidLiquidGasControlPanel.this.m_stateSelectionPanel.reset();
                        m_model.setMolecule( MultipleParticleModel.ARGON );
                    }
                }
            } );
            m_waterRadioButton = new JRadioButton( "Water" ); // TODO: JPB TBD - Make into a string resource if kept.
            m_waterRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_waterRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if (m_model.getMolecule() != MultipleParticleModel.WATER){
                        Exp2SolidLiquidGasControlPanel.this.m_stateSelectionPanel.reset();
                        m_model.setMolecule( MultipleParticleModel.WATER );
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
    }
    
    private class GravityControlPanel extends JPanel {
        
        private static final int GRAV_SLIDER_RANGE = 100;
        private JSlider m_gravitationalAccControl;
        private Font labelFont = new PhetFont(14, true);
        
        GravityControlPanel(){
            
            setLayout( new GridLayout(0, 1) );
            
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    "Gravity Control", // JPB TBD - Make this into a string if we keep it.
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    Color.GRAY );
            
            setBorder( titledBorder );
            
            // Register as a listener with the model so that we know when it gets
            // reset.
            m_model.addListener( new MultipleParticleModel.Adapter(){
                public void resetOccurred(){
                    m_gravitationalAccControl.setValue((int)((m_model.getGravitationalAcceleration() / 
                            MultipleParticleModel.MAX_GRAVITATIONAL_ACCEL) * GRAV_SLIDER_RANGE));
                }
            });
            
            // Add the labels.
            JPanel labelPanel = new JPanel();
            labelPanel.setLayout( new GridLayout(1,4) );
            JLabel leftLabel = new JLabel("None    ");
            leftLabel.setFont( labelFont );
            labelPanel.add( leftLabel );
            labelPanel.add(new JLabel(""));
            labelPanel.add(new JLabel(""));
            JLabel rightLabel = new JLabel("    Lots");
            rightLabel.setFont( labelFont );
            labelPanel.add( rightLabel );
            add( labelPanel );
            
            // Add the slider that will control the gravitational acceleration of the system.
            m_gravitationalAccControl = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
            m_gravitationalAccControl.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_gravitationalAccControl.setPaintTicks( true );
            m_gravitationalAccControl.setMajorTickSpacing( GRAV_SLIDER_RANGE / 10 );
            m_gravitationalAccControl.setMinorTickSpacing( GRAV_SLIDER_RANGE / 20 );
            m_gravitationalAccControl.setValue((int)((m_model.getGravitationalAcceleration() / 
                    MultipleParticleModel.MAX_GRAVITATIONAL_ACCEL) * GRAV_SLIDER_RANGE));
            m_gravitationalAccControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    m_model.setGravitationalAcceleration( m_gravitationalAccControl.getValue() * 
                            (MultipleParticleModel.MAX_GRAVITATIONAL_ACCEL / GRAV_SLIDER_RANGE));
                }
            });
            
            add(m_gravitationalAccControl);
        }
    }
}
