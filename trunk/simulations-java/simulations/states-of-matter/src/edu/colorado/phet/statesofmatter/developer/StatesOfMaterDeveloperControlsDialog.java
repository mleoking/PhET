// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.developer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.ModuleEvent;
import edu.colorado.phet.common.phetcommon.application.ModuleObserver;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.statesofmatter.control.GravityControlPanel;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.module.phasechanges.PhaseChangesModule;
import edu.colorado.phet.statesofmatter.module.solidliquidgas.SolidLiquidGasModule;

/**
 * DeveloperControlsDialog is a dialog that contains "developer only" controls.
 * These controls will not be available to the user, and are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com), John Blanco
 */
public class StatesOfMaterDeveloperControlsDialog extends PaintImmediateDialog {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    private static final DecimalFormat NUMBER_FORMATTER = new DecimalFormat( "##0.000" );

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private final PhetApplication m_app;
    private MultipleParticleModel m_model;
    private LinearValueControl m_temperatureControl;
    private JLabel m_containterWidthInfo;
    private JLabel m_containterHeightInfo;
    private JLabel m_numParticles;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public StatesOfMaterDeveloperControlsDialog( Frame owner, PhetApplication app ) {
        super( owner, "Developer Controls" );
        setResizable( false );
        setModal( false );

        m_app = app;

        // Get a reference to the model.
        Module activeModule = m_app.getActiveModule();
        if ( activeModule instanceof SolidLiquidGasModule ) {
            m_model = ( (SolidLiquidGasModule) activeModule ).getMultiParticleModel();
        }
        else if ( activeModule instanceof PhaseChangesModule ) {
            m_model = ( (PhaseChangesModule) activeModule ).getMultiParticleModel();
        }

        // Register with the application for module change events.
        m_app.addModuleObserver( new ModuleObserver() {
            public void moduleAdded( ModuleEvent event ) {
            }

            public void activeModuleChanged( ModuleEvent event ) {
                // Since these developer controls are specific to the selected
                // module, the controls should disappear if the module changes.
                StatesOfMaterDeveloperControlsDialog.this.dispose();
            }

            public void moduleRemoved( ModuleEvent event ) {
            }

        } );

        // Register with the model for various events.
        m_model.addListener( new MultipleParticleModel.Adapter() {
            public void temperatureChanged() {
                m_temperatureControl.setValue( m_model.getTemperatureSetPoint() );
            }

            public void resetOccurred() {
                updateAdditionalInfo();
            }

            public void particleAdded( StatesOfMatterAtom particle ) {
                updateAdditionalInfo();
            }
        } );

        // Create and add the input panel.

        JPanel inputPanel = createInputPanel();

        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );

        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    private JPanel createInputPanel() {

        // Thermostat selection.
        ThermostatSelectionPanel thermostatSelectionPanel = new ThermostatSelectionPanel();

        // Temperature control.
        m_temperatureControl = new LinearValueControl( MultipleParticleModel.MIN_TEMPERATURE,
                                                       MultipleParticleModel.MAX_TEMPERATURE, "Temperature", "#.###", "Control" );
        m_temperatureControl.setUpDownArrowDelta( 0.05 );
        m_temperatureControl.setTextFieldEditable( true );
        m_temperatureControl.setFont( new PhetFont( Font.PLAIN, 14 ) );
        m_temperatureControl.setTickPattern( "0" );
        m_temperatureControl.setMajorTickSpacing( 100 );
        m_temperatureControl.setMinorTickSpacing( 10 );
        m_temperatureControl.setBorder( BorderFactory.createEtchedBorder() );
        m_temperatureControl.setValue( m_model.getTemperatureSetPoint() );
        m_temperatureControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Module activeModule = m_app.getActiveModule();
                if ( activeModule instanceof SolidLiquidGasModule ) {
                    ( (SolidLiquidGasModule) activeModule ).getMultiParticleModel().setTemperature( m_temperatureControl.getValue() );
                }
                else if ( activeModule instanceof PhaseChangesModule ) {
                    ( (PhaseChangesModule) activeModule ).getMultiParticleModel().setTemperature( m_temperatureControl.getValue() );
                }
            }
        } );

        // Gravity control.
        GravityControlPanel gravityControlPanel = new GravityControlPanel( m_model );

        // Create the "Additional Information" panel.
        JPanel infoPanel = new JPanel();
        m_containterWidthInfo = new JLabel();
        infoPanel.add( m_containterWidthInfo );
        m_containterHeightInfo = new JLabel();
        infoPanel.add( m_containterHeightInfo );
        m_numParticles = new JLabel();
        infoPanel.add( m_numParticles );
        BevelBorder baseBorder = (BevelBorder) BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                                                                      "Additional Info",
                                                                      TitledBorder.LEFT,
                                                                      TitledBorder.TOP,
                                                                      new PhetFont( Font.BOLD, 14 ),
                                                                      Color.BLACK );

        infoPanel.setBorder( titledBorder );
        updateAdditionalInfo();

        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( thermostatSelectionPanel, row++, column );
        layout.addComponent( m_temperatureControl, row++, column );
        layout.addComponent( gravityControlPanel, row++, column );
        layout.addComponent( infoPanel, row++, column );

        return panel;
    }

    /**
     * This class defines the selection panel that allows the user to choose
     * the type of thermostat being used in the model.
     */
    private class ThermostatSelectionPanel extends JPanel {

        private final JRadioButton m_noThermostatRadioButton;
        private final JRadioButton m_isokineticThermostatRadioButton;
        private final JRadioButton m_andersenThermostatRadioButton;
        private final JRadioButton m_adaptiveThermostatRadioButton;

        ThermostatSelectionPanel() {

            setLayout( new GridLayout( 0, 1 ) );

            BevelBorder baseBorder = (BevelBorder) BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                                                                          "Thermostat Type",
                                                                          TitledBorder.LEFT,
                                                                          TitledBorder.TOP,
                                                                          new PhetFont( Font.BOLD, 14 ),
                                                                          Color.BLACK );

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
            m_adaptiveThermostatRadioButton = new JRadioButton( "Adaptive Thermostat" );
            m_adaptiveThermostatRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_adaptiveThermostatRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setThermostatType( MultipleParticleModel.ADAPTIVE_THERMOSTAT );
                }
            } );

            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( m_noThermostatRadioButton );
            buttonGroup.add( m_isokineticThermostatRadioButton );
            buttonGroup.add( m_andersenThermostatRadioButton );
            buttonGroup.add( m_adaptiveThermostatRadioButton );

            switch( m_model.getThermostatType() ) {
                case MultipleParticleModel.NO_THERMOSTAT:
                    m_noThermostatRadioButton.setSelected( true );
                    break;
                case MultipleParticleModel.ANDERSEN_THERMOSTAT:
                    m_andersenThermostatRadioButton.setSelected( true );
                    break;
                case MultipleParticleModel.ISOKINETIC_THERMOSTAT:
                    m_isokineticThermostatRadioButton.setSelected( true );
                    break;
                case MultipleParticleModel.ADAPTIVE_THERMOSTAT:
                    m_adaptiveThermostatRadioButton.setSelected( true );
                    break;
                default:
                    assert false;
                    break;
            }

            add( m_noThermostatRadioButton );
            add( m_isokineticThermostatRadioButton );
            add( m_andersenThermostatRadioButton );
            add( m_adaptiveThermostatRadioButton );
        }
    }

    private void updateAdditionalInfo() {
        m_containterWidthInfo.setText( "Lx = " + NUMBER_FORMATTER.format( m_model.getNormalizedContainerWidth() ) );
        m_containterHeightInfo.setText( "Ly = " + NUMBER_FORMATTER.format( m_model.getNormalizedContainerHeight() ) );
        m_numParticles.setText( "N = " + m_model.getNumMolecules() );
    }
}
