/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.solidliquidgas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;

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


public class SolidLiquidGasControlPanel extends ControlPanel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    MultipleParticleModel m_model;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param alphaRadiationModule
     * @param parentFrame parent frame, for creating dialogs
     */
    public SolidLiquidGasControlPanel( SolidLiquidGasModule solidLiquidGasModule, Frame parentFrame ) {
        
        super();
        m_model = solidLiquidGasModule.getMultiParticleModel();
        
        // Set the control panel's minimum width.
        int minimumWidth = StatesOfMatterResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
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
    private class MoleculeSelectionPanel extends JPanel {
        
        private JRadioButton m_oxygenRadioButton;
        private JRadioButton m_nitrogenRadioButton;
        private JRadioButton m_carbonDioxideRadioButton;
        
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
            m_nitrogenRadioButton = new JRadioButton( StatesOfMatterStrings.NITROGEN_SELECTION_LABEL );
            m_nitrogenRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_carbonDioxideRadioButton = new JRadioButton( StatesOfMatterStrings.CARBON_DIOXIDE_SELECTION_LABEL );
            m_carbonDioxideRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( m_oxygenRadioButton );
            buttonGroup.add( m_nitrogenRadioButton );
            buttonGroup.add( m_carbonDioxideRadioButton );
            m_oxygenRadioButton.setSelected( true );
            
            add( m_oxygenRadioButton );
            add( m_nitrogenRadioButton );
            add( m_carbonDioxideRadioButton );
        }
    }
    
    private class ParticleSystemControlPanel extends JPanel {
        
        private LinearValueControl m_temperatureControl;
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
                    m_temperatureControl.setValue( m_model.getTemperature() );
                    m_gravitationalAccControl.setValue( m_model.getGravitationalAcceleration() );
                }
            });
            
            // Add the slider that controls the temperature of the system.
            m_temperatureControl = new LinearValueControl( 0, 5, "Temperature", "##.##", "Control" );
            m_temperatureControl.setUpDownArrowDelta( 0.05 );
            m_temperatureControl.setTextFieldEditable( true );
            m_temperatureControl.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_temperatureControl.setTickPattern( "0" );
            m_temperatureControl.setMajorTickSpacing( 2.5 );
            m_temperatureControl.setMinorTickSpacing( 1.25 );
            m_temperatureControl.setBorder( BorderFactory.createEtchedBorder() );
            m_temperatureControl.setValue( m_model.getTemperature() );
            m_temperatureControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    m_model.setTemperature( m_temperatureControl.getValue() );
                }
            });
            
            add(m_temperatureControl);
            
            // Add the slider that controls the gravitational acceleration of the system.
            m_gravitationalAccControl = new LinearValueControl( 0, 2, "Gravity", "##.##", "Control" );
            m_gravitationalAccControl.setUpDownArrowDelta( 0.01 );
            m_gravitationalAccControl.setTextFieldEditable( true );
            m_gravitationalAccControl.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_gravitationalAccControl.setTickPattern( "0.0" );
            m_gravitationalAccControl.setMajorTickSpacing( 1 );
            m_gravitationalAccControl.setMinorTickSpacing( 0.5 );
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
