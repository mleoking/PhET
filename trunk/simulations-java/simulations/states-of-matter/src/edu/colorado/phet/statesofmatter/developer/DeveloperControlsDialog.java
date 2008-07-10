/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.developer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.statesofmatter.StatesOfMatterApplication;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.module.phasechanges.PhaseChangesModule;
import edu.colorado.phet.statesofmatter.module.solidliquidgas.SolidLiquidGasModule;

/**
 * DeveloperControlsDialog is a dialog that contains "developer only" controls.
 * These controls will not be available to the user, and are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperControlsDialog extends JDialog {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private StatesOfMatterApplication m_app;
    private MultipleParticleModel m_model;
    private LinearValueControl m_temperatureControl;


    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public DeveloperControlsDialog( Frame owner, StatesOfMatterApplication app ) {
        super( owner, "Developer Controls" );
        setResizable( false );
        setModal( false );

        m_app = app;
        
        Module activeModule = m_app.getActiveModule();
        if (activeModule instanceof SolidLiquidGasModule){
            m_model = ((SolidLiquidGasModule)activeModule).getMultiParticleModel();
        }
        else if (activeModule instanceof PhaseChangesModule){
            m_model = ((PhaseChangesModule)activeModule).getMultiParticleModel();
        }
        
        // Register with the model for temperature change events.
        m_model.addListener( new MultipleParticleModel.Adapter(){
            public void temperatureChanged(){
                m_temperatureControl.setValue( m_model.getTemperature() );
            }
        });

        JPanel inputPanel = createInputPanel();

        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );

        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    private JPanel createInputPanel() {

        Frame parentFrame = PhetApplication.instance().getPhetFrame();

        Color controlPanelBackground = m_app.getControlPanelBackground();
        final ColorControl controlPanelColorControl = new ColorControl( parentFrame, "control panel background color: ", controlPanelBackground );
        controlPanelColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                m_app.setControlPanelBackground( controlPanelColorControl.getColor() );
            }
        } );

        Color selectedTabColor = m_app.getSelectedTabColor();
        final ColorControl selectedTabColorControl = new ColorControl( parentFrame, "selected module tab color: ", selectedTabColor );
        selectedTabColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                m_app.setSelectedTabColor( selectedTabColorControl.getColor() );
            }
        } );

        // Thermostat selection.
        ThermostatSelectionPanel thermostatSelectionPanel = new ThermostatSelectionPanel();
        
        m_temperatureControl = new LinearValueControl( MultipleParticleModel.MIN_TEMPERATURE, 
                MultipleParticleModel.MAX_TEMPERATURE, "Temperature", "#.###", "Control" );
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
                Module activeModule = m_app.getActiveModule();
                if ( activeModule instanceof SolidLiquidGasModule ){
                    ((SolidLiquidGasModule)activeModule).getMultiParticleModel().setTemperature( m_temperatureControl.getValue() );
                }
                else if ( activeModule instanceof PhaseChangesModule ){
                    ((PhaseChangesModule)activeModule).getMultiParticleModel().setTemperature( m_temperatureControl.getValue() );
                }
            }
        });
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( controlPanelColorControl, row++, column );
        layout.addComponent( selectedTabColorControl, row++, column );
        layout.addComponent( thermostatSelectionPanel, row++, column );
        layout.addComponent( m_temperatureControl, row++, column );

        return panel;
    }
    
    /**
     * This class defines the selection panel that allows the user to choose
     * the type of thermostat being used in the model.
     */
    private class ThermostatSelectionPanel extends JPanel {
        
        private JRadioButton m_noThermostatRadioButton;
        private JRadioButton m_isokineticThermostatRadioButton;
        private JRadioButton m_andersenThermostatRadioButton;

        ThermostatSelectionPanel(){
            
            setLayout( new GridLayout(0, 1) );
            
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    "Thermostat Type",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    Color.GRAY );
            
            setBorder( titledBorder );

            m_andersenThermostatRadioButton = new JRadioButton( "Andersen Thermostat" );
            m_andersenThermostatRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_andersenThermostatRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setThermostatType( MultipleParticleModel.ANDERSEN_THERMOSTAT );
                }
            } );
            m_noThermostatRadioButton = new JRadioButton( "No Thermostat" );
            m_noThermostatRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_noThermostatRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setThermostatType( MultipleParticleModel.NO_THERMOSTAT );
                }
            } );
            m_isokineticThermostatRadioButton = new JRadioButton( "Isokinetic Thermostat" );
            m_isokineticThermostatRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_isokineticThermostatRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setThermostatType( MultipleParticleModel.ISOKINETIC_THERMOSTAT );
                }
            } );
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( m_noThermostatRadioButton );
            buttonGroup.add( m_isokineticThermostatRadioButton );
            buttonGroup.add( m_andersenThermostatRadioButton );
            
            switch (m_model.getThermostatType()){
            case MultipleParticleModel.NO_THERMOSTAT:
                m_noThermostatRadioButton.setSelected( true );
                break;
            case MultipleParticleModel.ANDERSEN_THERMOSTAT:
                m_andersenThermostatRadioButton.setSelected( true );
                break;
            case MultipleParticleModel.ISOKINETIC_THERMOSTAT:
                m_isokineticThermostatRadioButton.setSelected( true );
                break;
            default:
                assert false;
                break;
            }
            
            add( m_noThermostatRadioButton );
            add( m_isokineticThermostatRadioButton );
            add( m_andersenThermostatRadioButton );
        }
    }
}
