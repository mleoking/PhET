/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.developer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
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

    private StatesOfMatterApplication _app;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public DeveloperControlsDialog( Frame owner, StatesOfMatterApplication app ) {
        super( owner, "Developer Controls" );
        setResizable( false );
        setModal( false );

        _app = app;

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

        Color controlPanelBackground = _app.getControlPanelBackground();
        final ColorControl controlPanelColorControl = new ColorControl( parentFrame, "control panel background color: ", controlPanelBackground );
        controlPanelColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                _app.setControlPanelBackground( controlPanelColorControl.getColor() );
            }
        } );

        Color selectedTabColor = _app.getSelectedTabColor();
        final ColorControl selectedTabColorControl = new ColorControl( parentFrame, "selected module tab color: ", selectedTabColor );
        selectedTabColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                _app.setSelectedTabColor( selectedTabColorControl.getColor() );
            }
        } );

        // Thermostat on/off check box.
        final JCheckBox thermostatCheckBox = new JCheckBox("Use Thermostat");
        Module activeModule = _app.getActiveModule();
        if ( activeModule instanceof SolidLiquidGasModule ){
            thermostatCheckBox.setSelected( ((SolidLiquidGasModule)activeModule).getMultiParticleModel().getIsThermostatEnabled() );
        }
        else if ( activeModule instanceof PhaseChangesModule ){
            thermostatCheckBox.setSelected( ((PhaseChangesModule)activeModule).getMultiParticleModel().getIsThermostatEnabled() );
        }
        thermostatCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Module activeModule = _app.getActiveModule();
                if ( activeModule instanceof SolidLiquidGasModule ){
                    ((SolidLiquidGasModule)activeModule).getMultiParticleModel().setIsThermostatEnabled( thermostatCheckBox.isSelected() );
                }
                else if ( activeModule instanceof PhaseChangesModule ){
                    ((PhaseChangesModule)activeModule).getMultiParticleModel().setIsThermostatEnabled( thermostatCheckBox.isSelected() );
                }
            }
        });
        
        // Add the slider that controls the temperature of the system.
        final LinearValueControl temperatureControl;

        temperatureControl = new LinearValueControl( MultipleParticleModel.MIN_TEMPERATURE, 
                MultipleParticleModel.MAX_TEMPERATURE, "Temperature", "##.##", "Control" );
        temperatureControl.setUpDownArrowDelta( 0.05 );
        temperatureControl.setTextFieldEditable( true );
        temperatureControl.setFont( new PhetFont( Font.PLAIN, 14 ) );
        temperatureControl.setTickPattern( "0" );
        temperatureControl.setMajorTickSpacing( 2.5 );
        temperatureControl.setMinorTickSpacing( 1.25 );
        temperatureControl.setBorder( BorderFactory.createEtchedBorder() );
        temperatureControl.setValue( MultipleParticleModel.MIN_TEMPERATURE );
        temperatureControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Module activeModule = _app.getActiveModule();
                if ( activeModule instanceof SolidLiquidGasModule ){
                    ((SolidLiquidGasModule)activeModule).getMultiParticleModel().setTemperature( temperatureControl.getValue() );
                }
                else if ( activeModule instanceof PhaseChangesModule ){
                    ((PhaseChangesModule)activeModule).getMultiParticleModel().setTemperature( temperatureControl.getValue() );
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
        layout.addComponent( thermostatCheckBox, row++, column );
        layout.addComponent( temperatureControl, row++, column );

        return panel;
    }
}
