// edu.colorado.phet.graphics.idealgas.IdealGasControlPanel
/*
 * User: Administrator
 * Date: Nov 5, 2002
 * Time: 7:53:21 AM
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.coreadditions.ToggleButton;
import edu.colorado.phet.idealgas.model.IdealGasModel;

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

public class IdealGasControlPanel extends JPanel {

    private static final int s_stoveSliderHeight = 80;
    private static final int s_gravityControlPanelHeight = 80;
    private static final int s_maxGravity = 40;

    private NumberFormat gravityFormat = NumberFormat.getInstance();
    private JTextField gravityTF;
    private JCheckBox gravityOnCB;
    private JSlider gravitySlider;
    private JPanel gravityControlPanel;
    private IdealGasModule module;
    private IdealGasModel idealGasModel;
    private GridBagConstraints gbc;
    // Separate panels for miscellaneous controls, particle controls, and buttons
    private JPanel miscPanel;
    private JPanel particleControlsPanel;
    private JPanel buttonPanel;
    private GridBagConstraints particleControlsGbc;

    public IdealGasControlPanel( IdealGasModule module ) {
        super();
        this.module = module;
        this.idealGasModel = (IdealGasModel)module.getModel();
        init();
    }

    private void init() {

        makeMiscControls();
        makeButtonPanel();
        makeParticlesControls();

        this.setLayout( new GridBagLayout() );
        gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                      GridBagConstraints.CENTER,
                                      GridBagConstraints.HORIZONTAL,
                                      new Insets( 0,0,0,0 ), 0, 0 );
//                                      new Insets( 4, 4, 4, 4 ), 0, 0 );

        // Add control for selecting the constant parameter
        gbc.gridy = 0;
        add( constantParamControls(), gbc );

        // Add controls for the number and type of molecules to put in the box
        gbc.gridy++;
        add( particleControlsPanel, gbc );

        // Add miscellaneous controls (gravity, particle interactions, etc.
        gbc.gridy++;
        this.add( miscPanel, gbc );

        // Add the reset and measurement panel buttons
        gbc.gridy++;
        this.add( buttonPanel, gbc );

        Border border = BorderFactory.createEtchedBorder();
        this.setBorder( border );
    }

    /**
     * Creates the panel that holds controls for the particles in the box
     */
    private void makeParticlesControls() {
        particleControlsPanel = new JPanel( new GridBagLayout() );

        particleControlsGbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                                 GridBagConstraints.CENTER,
                                                                 GridBagConstraints.HORIZONTAL,
                                                                 new Insets( 0, 0, 0, 0 ), 0, 0 );

        // Add controls for the number and type of molecules to put in the box
        JPanel speciesButtonPanel = new PumpControlPanel( module, module.getPump() );
        speciesButtonPanel.setBorder( new TitledBorder( SimStrings.get( "IdealGasControlPanel.Gas_In_Pump" ) ) );
        particleControlsGbc.gridy++;
        particleControlsGbc.fill = GridBagConstraints.NONE;
        particleControlsPanel.add( speciesButtonPanel, particleControlsGbc );
        particleControlsGbc.gridy++;
//        particleControlsPanel.add( new NumParticlesControls2(), particleControlsGbc );
//        particleControlsPanel.add( new NumParticlesControls(), particleControlsGbc );
    }

    /**
     * Creates the panel that contains controls for things that don't appear as objects on the screen
     */
    private void makeMiscControls() {
        miscPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        JPanel gravityControls = gravityControls();
        miscPanel.add( gravityControls, gbc );

        gbc.gridy++;
        ParticleInteractionControl pic = new ParticleInteractionControl();
        gbc.fill = GridBagConstraints.NONE;
        miscPanel.add( pic, gbc );

        Border gravityBorder = new TitledBorder( SimStrings.get( "IdealGasControlPanel.MiscPanelTitle" ) );
        miscPanel.setBorder( gravityBorder );

    }

    /**
     * Make buttons for Reset and Measurement Tools
     */
    private void makeButtonPanel() {

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

        // Put them on the button panel
        buttonPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                         new Insets( 2, 2, 2, 2 ), 0, 0 );

        buttonPanel.add( resetBtn, gbc );
        gbc.gridy++;
        buttonPanel.add( measurementDlgBtn, gbc );
        buttonPanel.revalidate();
    }

    /**
     * Returns a panel with selections for whether volume or pressure is to be held constant
     */
    private JPanel constantParamControls() {
        JPanel constantParamButtonPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        final JRadioButton constantVolumeRB = new JRadioButton( SimStrings.get( "Common.Volume" ) );
        final JRadioButton constantPressureRB = new JRadioButton( SimStrings.get( "Common.Pressure" ) );
        final ButtonGroup constantParameterGroup = new ButtonGroup();
        constantParameterGroup.add( constantVolumeRB );
        constantParameterGroup.add( constantPressureRB );
        constantParamButtonPanel.add( constantVolumeRB );
        constantParamButtonPanel.add( constantPressureRB );
        constantParamButtonPanel.setBorder( new TitledBorder( SimStrings.get( "IdealGasControlPanel.Constant_Parameter" ) ) );
        this.add( constantParamButtonPanel );

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
        return constantParamButtonPanel;
    }

    /**
     * Create a panel with controls for gravity and add it to the IdealGasControlPanel
     */
    private JPanel gravityControls() {

        gravityControlPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.WEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        // Add control for gravity, set default to OFF
        gravityOnCB = new JCheckBox( SimStrings.get( "Common.On" ) );
        gravityControlPanel.add( gravityOnCB, gbc );
        gravityOnCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                updateGravity( gravityOnCB.isSelected(), gravitySlider.getValue() );
            }
        } );
        gravityOnCB.setSelected( false );

        gravitySlider = new JSlider( JSlider.VERTICAL, 0, s_maxGravity, 0 );
        gravitySlider.setPreferredSize( new Dimension( 60, 50 ) );
        gravitySlider.setPaintTicks( true );
        gravitySlider.setMajorTickSpacing( 10 );
        gravitySlider.setMinorTickSpacing( 5 );
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 0 ), new JLabel( SimStrings.get( "Common.0" ) ) );
        labelTable.put( new Integer( s_maxGravity ), new JLabel( SimStrings.get( "Common.Max" ) ) );
        gravitySlider.setLabelTable( labelTable );
        gravitySlider.setPaintLabels( true );
        gbc.gridx = 1;
        gravityControlPanel.add( gravitySlider, gbc );
        gravitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                updateGravity( gravityOnCB.isSelected(), gravitySlider.getValue() );
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


    private void updateGravity( boolean isEnabled, int value ) {
        gravityTF.setText( gravityFormat.format( value ) );
        if( !isEnabled ) {
            module.setGravity( 0 );
        }
        else {
            module.setGravity( value );
        }
    }

    public void setGravityEnabled( boolean enabled ) {
        this.gravityOnCB.setSelected( enabled );
    }

    public void setGravity( double amt ) {
        this.gravitySlider.setValue( (int)amt );
    }

//    private void setNumParticlesInBox( int numParticles ) {
//        int dn = numParticles - ( getModule().getIdealGasModel().getHeavySpeciesCnt()
//                                  + getModule().getIdealGasModel().getLightSpeciesCnt() );
//        if( dn > 0 ) {
//            for( int i = 0; i < dn; i++ ) {
//                getModule().pumpGasMolecules( 1 );
//            }
//        }
//        else if( dn < 0 ) {
//            for( int i = 0; i < -dn; i++ ) {
//                getModule().removeGasMolecule();
//            }
//        }
//    }
//
    public void addComponent( Component component ) {
        gbc.gridy++;
        this.add( component, gbc );
    }

    public void addParticleControl( Component component ) {
        particleControlsGbc.gridy++;
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


//    private class NumParticlesControls extends JPanel {
//        NumParticlesControls() {
//
//            super( new GridBagLayout() );
//            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
//                                                             GridBagConstraints.EAST,
//                                                             GridBagConstraints.NONE,
//                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
//            JLabel label = new JLabel( SimStrings.get( "MeasurementControlPanel.Number_of_particles" ) );
//            this.add( label, gbc );
//            // Set up the spinner for controlling the number of particles in
//            // the hollow sphere
//            Integer value = new Integer( 0 );
//            Integer min = new Integer( 0 );
//            Integer max = new Integer( 1000 );
//            Integer step = new Integer( 1 );
//            SpinnerNumberModel model = new SpinnerNumberModel( value, min, max, step );
//            final JSpinner particleSpinner = new JSpinner( model );
//            particleSpinner.setPreferredSize( new Dimension( 50, 20 ) );
//            gbc.gridx = 1;
//            gbc.anchor = GridBagConstraints.WEST;
//            this.add( particleSpinner, gbc );
//
//            particleSpinner.addChangeListener( new ChangeListener() {
//                public void stateChanged( ChangeEvent e ) {
//                    setNumParticlesInBox( ( (Integer)particleSpinner.getValue() ).intValue() );
//                }
//            } );
//
//            // Hook the spinner up so it will track molecules put in the box by the pump
//            getModule().getModel().addObserver( new SimpleObserver() {
//                public void update() {
//                    int h = getModule().getIdealGasModel().getHeavySpeciesCnt();
//                    int l = getModule().getIdealGasModel().getLightSpeciesCnt();
//                    particleSpinner.setValue( new Integer( l + h ) );
//                }
//            } );
//        }
//    }
//
//    private class NumParticlesControls2 extends JPanel {
//        NumParticlesControls2() {
//
//            super( new GridBagLayout() );
//            // Set up the spinner for controlling the number of particles in
//            // the hollow sphere
//            Integer value = new Integer( 0 );
//            Integer min = new Integer( 0 );
//            Integer max = new Integer( 1000 );
//            Integer step = new Integer( 1 );
//
//            // Spinner for heavy species
//            SpinnerNumberModel heavySpinnerModel = new SpinnerNumberModel( value, min, max, step );
//            final JSpinner heavySpinner = new JSpinner( heavySpinnerModel );
//            heavySpinner.setPreferredSize( new Dimension( 50, 20 ) );
//            heavySpinner.addChangeListener( new ChangeListener() {
//                public void stateChanged( ChangeEvent e ) {
//                    int dn = ((Integer)heavySpinner.getValue()).intValue()  - ( getModule().getIdealGasModel().getHeavySpeciesCnt() );
//                    if( dn > 0 ) {
//                        for( int i = 0; i < dn; i++ ) {
//                            Pump pump = getModule().getPump();
//                            pump.setCurrentGasSpecies( HeavySpecies.class );
//                            getModule().pumpGasMolecules( 1 );
//                        }
//                    }
//                    else if( dn < 0 ) {
//                        for( int i = 0; i < -dn; i++ ) {
//                            getModule().removeGasMolecule( HeavySpecies.class );
//                        }
//                    }
//                }
//            } );
//
//            // Hook the spinner up so it will track molecules put in the box by the pump
//            getModule().getModel().addObserver( new SimpleObserver() {
//                public void update() {
//                    int h = getModule().getIdealGasModel().getHeavySpeciesCnt();
//                    heavySpinner.setValue( new Integer( h ) );
//                }
//            } );
//
//
//            // Spinner for light species
//            SpinnerNumberModel lightSpinnerModel = new SpinnerNumberModel( value, min, max, step );
//            final JSpinner lightSpinner = new JSpinner( lightSpinnerModel );
//            lightSpinner.setPreferredSize( new Dimension( 50, 20 ) );
//            lightSpinner.addChangeListener( new ChangeListener() {
//                public void stateChanged( ChangeEvent e ) {
//                    int dn = ((Integer)lightSpinner.getValue()).intValue()  - ( getModule().getIdealGasModel().getLightSpeciesCnt());
//                    if( dn > 0 ) {
//                        for( int i = 0; i < dn; i++ ) {
//                            Pump pump = getModule().getPump();
//                            pump.setCurrentGasSpecies( LightSpecies.class );
//                            getModule().pumpGasMolecules( 1 );
//                        }
//                    }
//                    else if( dn < 0 ) {
//                        for( int i = 0; i < -dn; i++ ) {
//                            getModule().removeGasMolecule( LightSpecies.class );
//                        }
//                    }
//                }
//            } );
//
//            // Hook the spinner up so it will track molecules put in the box by the pump
//            getModule().getModel().addObserver( new SimpleObserver() {
//                public void update() {
//                    int h = getModule().getIdealGasModel().getLightSpeciesCnt();
//                    lightSpinner.setValue( new Integer( h ) );
//                }
//            } );
//
//
//            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
//                                                             GridBagConstraints.EAST,
//                                                             GridBagConstraints.NONE,
//                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
//            JLabel label = new JLabel( SimStrings.get( "MeasurementControlPanel.Number_of_particles" ) );
//            this.add( label, gbc );
//            gbc.gridx = 1;
//            gbc.anchor = GridBagConstraints.WEST;
//            this.add( heavySpinner, gbc );
//
//            gbc.gridy++;
//            this.add( lightSpinner, gbc );
//        }
//    }
}


