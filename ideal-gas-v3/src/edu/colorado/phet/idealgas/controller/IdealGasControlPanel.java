/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.coreadditions.ToggleButton;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.Gravity;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.Pump;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.Hashtable;

/**
 * The base control panel for all modules.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IdealGasControlPanel extends JPanel implements Gravity.ChangeListener {

    private NumberFormat gravityFormat = NumberFormat.getInstance();
    private JTextField gravityTF;
    private JSlider gravitySlider;
    private JPanel gravityControlPanel;
    private JPanel advancedPanel;
    private IdealGasModule module;
    private IdealGasModel idealGasModel;
    private GridBagConstraints gbc;
    // Separate panels for miscellaneous controls, particle controls, and buttons
    private JPanel miscPanel;
    private JPanel particleControlsPanel;
    private JPanel buttonPanel;
    private GridBagConstraints particleControlsGbc;
    private JLabel label;


    public IdealGasControlPanel( IdealGasModule module ) {
        super();
        this.module = module;
        this.idealGasModel = (IdealGasModel)module.getModel();
        idealGasModel.getGravity().addListener( this );
        init();
    }

    private void init() {
        // Create the component panels
        makeMiscControls();
        makeButtonPanel();
        makeParticlesControls();
        makeAdvancedPanel();

        // Lay out the panel
        this.setLayout( new GridBagLayout() );
        gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                      1, 1, 1, 1,
                                      GridBagConstraints.NORTH,
//                                      GridBagConstraints.CENTER,
                                      GridBagConstraints.HORIZONTAL,
                                      new Insets( 0, 0, 0, 0 ), 0, 0 );


        // Add control for selecting the constant parameter
        add( constantParamControls(), gbc );

        // Add controls for the number and type of molecules to put in the box
        add( particleControlsPanel, gbc );

        // Add miscellaneous controls (gravity, particle interactions, etc.
        this.add( miscPanel, gbc );

        // Add the reset and measurement panel buttons
        this.add( buttonPanel, gbc );

        // Add the panel with the advanced options. It should be invisible at first
        advancedPanel.setVisible( false );
        this.add( advancedPanel, gbc );

        Border border = BorderFactory.createEtchedBorder();
        this.setBorder( border );
    }

    /**
     * Creates the panel that holds controls for the particles in the box
     */
    private void makeParticlesControls() {
        particleControlsPanel = new JPanel( new GridBagLayout() );

        particleControlsGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                      1, 1, 1, 1,
                                                      GridBagConstraints.CENTER,
                                                      GridBagConstraints.HORIZONTAL,
                                                      new Insets( 0, 0, 0, 0 ), 0, 0 );

        // Add controls for the number and type of molecules to put in the box
        JPanel speciesButtonPanel = new PumpControlPanel( module, module.getPump(), module.getSpeciesNames() );
        speciesButtonPanel.setBorder( new TitledBorder( SimStrings.get( "IdealGasControlPanel.Gas_In_Chamber" ) ) );
        particleControlsPanel.add( speciesButtonPanel, particleControlsGbc );
    }

    /**
     * Creates the panel that contains controls for things that don't appear as objects on the screen
     */
    private void makeMiscControls() {
        miscPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints localGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                              1, 1, 1, 1,
                                                              GridBagConstraints.CENTER,
                                                              GridBagConstraints.HORIZONTAL,
                                                              new Insets( 0, 0, 0, 0 ), 0, 0 );
        JPanel gravityControls = gravityControls();
        miscPanel.add( gravityControls, localGbc );
    }

    /**
     * Make a panel with the advanced controls
     */
    private void makeAdvancedPanel() {
        advancedPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints localGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                              1, 1, 1, 1,
                                                              GridBagConstraints.CENTER,
                                                              GridBagConstraints.HORIZONTAL,
                                                              new Insets( 0, 0, 0, 0 ), 0, 0 );
        ParticleInteractionControl pic = new ParticleInteractionControl( module.getIdealGasModel() );
        localGbc.fill = GridBagConstraints.NONE;
        advancedPanel.add( pic, localGbc );
        Pump[] pumps = new Pump[] { module.getPump() };
        advancedPanel.add( new InputTemperatureControlPanel( getModule(), pumps ), localGbc );

    }

    /**
     * Make buttons for Reset and Measurement Tools
     */
    private void makeButtonPanel() {
        GridBagConstraints localGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                              1, 1, 1, 1,
                                                              GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                              new Insets( 2, 2, 2, 2 ), 0, 0 );

        // Reset button
        JButton resetBtn = new JButton( SimStrings.get( "IdealGasControlPanel.Reset" ) );
        resetBtn.setBackground( new Color( 180, 255, 180 ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );

        // Measurement tools button
        ToggleButton measurementDlgBtn = new MeasurementDialogButton();
        measurementDlgBtn.setAlignmentX( JButton.CENTER_ALIGNMENT );
        measurementDlgBtn.setBackground( new Color( 255, 255, 120 ) );

        // Advanced options button
        ToggleButton advancedButton = new ToggleButton( SimStrings.get("IdealGasControlPanel.MoreOptions"),
                                                        SimStrings.get("IdealGasControlPanel.FewerOptions")) {
            public void onAction() {
                advancedPanel.setVisible( true );
                SwingUtilities.getRoot( this ).invalidate();
            }

            public void offAction() {
                advancedPanel.setVisible( false );
                SwingUtilities.getRoot( this ).invalidate();
            }
        };

        // Put them on the button panel
        buttonPanel = new JPanel( new GridBagLayout() );
        buttonPanel.add( resetBtn, localGbc );
        buttonPanel.add( measurementDlgBtn, localGbc );
        buttonPanel.add( advancedButton, localGbc );
        buttonPanel.revalidate();
    }

    /**
     * Returns a panel with selections for whether volume or pressure is to be held constant
     */
    private JPanel constantParamControls() {
        JPanel constantParamButtonPanel = new JPanel( new GridBagLayout() );
        final JRadioButton constantVolumeRB = new JRadioButton( SimStrings.get( "Common.Volume" ) );
        final JRadioButton constantPressureRB = new JRadioButton( SimStrings.get( "Common.Pressure" ) );
        final ButtonGroup constantParameterGroup = new ButtonGroup();
        constantParameterGroup.add( constantVolumeRB );
        constantParameterGroup.add( constantPressureRB );

        GridBagConstraints localGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                              1, 1, 1, 1,
                                                              GridBagConstraints.WEST, GridBagConstraints.NONE,
                                                              new Insets( 0, 0, 0, 0 ), 0, 0 );
        constantParamButtonPanel.add( constantVolumeRB, localGbc );
        constantParamButtonPanel.add( constantPressureRB, localGbc );
        JPanel container = new JPanel( new GridBagLayout() );
        localGbc.gridy = 0;
        localGbc.anchor = GridBagConstraints.CENTER;
        container.add( constantParamButtonPanel, localGbc );
        container.setBorder( new TitledBorder( SimStrings.get( "IdealGasControlPanel.Constant_Parameter" ) ) );

        constantVolumeRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                idealGasModel.setConstantVolume( constantVolumeRB.isSelected() );
                idealGasModel.setConstantPressure( constantPressureRB.isSelected() );
            }
        } );
        constantPressureRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                idealGasModel.setConstantPressure( constantPressureRB.isSelected() );
                idealGasModel.setConstantVolume( constantVolumeRB.isSelected() );
            }
        } );
        constantVolumeRB.setSelected( true );
        return container;
    }

    /**
     * Create a panel with controls for gravity and add it to the IdealGasControlPanel
     */
    private JPanel gravityControls() {

        gravityControlPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );

        gravitySlider = new JSlider( JSlider.HORIZONTAL, 0, IdealGasConfig.MAX_GRAVITY, 0 );
        gravitySlider.setPreferredSize( new Dimension( 150, 50 ) );
//        gravitySlider.setPaintTicks( false );
        gravitySlider.setPaintTicks( true );
        gravitySlider.setMajorTickSpacing( 10 );
        gravitySlider.setMinorTickSpacing( 5 );
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 0 ), new JLabel( SimStrings.get( "Common.0" ) ) );
        labelTable.put( new Integer( IdealGasConfig.MAX_GRAVITY ), new JLabel( SimStrings.get( "Common.Max" ) ) );
        gravitySlider.setLabelTable( labelTable );
        gravitySlider.setPaintLabels( true );
        gravityControlPanel.add( gravitySlider, gbc );
        gravitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                module.setGravity( gravitySlider.getValue() );
            }
        } );

        // Add a readout for the value of gravity
        gravityTF = new JTextField( 2 );
        gravityTF.setEditable( false );
        gravityTF.setHorizontalAlignment( JTextField.RIGHT );
        gravityFormat.setMaximumFractionDigits( 2 );
        gravityFormat.setMinimumFractionDigits( 2 );
        gravityTF.setText( gravityFormat.format( 0 ) );

        Border gravityBorder = new TitledBorder( SimStrings.get( "Common.Gravity" ) );
        gravityControlPanel.setBorder( gravityBorder );
        return gravityControlPanel;
    }

    public void setGravity( double amt ) {
        this.gravitySlider.setValue( (int)amt );
    }

    public void addComponent( Component component ) {
        this.add( component, gbc );
    }

    public void addParticleControl( Component component ) {
        particleControlsPanel.add( component, particleControlsGbc );
    }

    //--------------------------------------------------------------------------
    // Utility methods
    //--------------------------------------------------------------------------

    protected IdealGasModule getModule() {
        return module;
    }

    //--------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------

    private class MeasurementDialogButton extends ToggleButton {

        public MeasurementDialogButton() {
            super( SimStrings.get( "IdealGasControlPanel.Measurement_Tools" ),
                   SimStrings.get( "IdealGasControlPanel.Measurement_Tools" ) );
        }

        public void onAction() {
            JDialog dlg = module.setMeasurementDlgVisible( true );
            dlg.addWindowListener( new WindowAdapter() {
                public void windowClosing( WindowEvent e ) {
                    setOff();
                }
            } );
        }

        public void offAction() {
            module.setMeasurementDlgVisible( false );
        }
    }

    //-----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------
    public void gravityChanged( Gravity.ChangeEvent event ) {
        gravitySlider.setValue( (int)event.getGravity().getAmt() );
    }
}


