// edu.colorado.phet.graphics.idealgas.IdealGasControlPanel
/*
 * User: Administrator
 * Date: Nov 5, 2002
 * Time: 7:53:21 AM
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.IdealGasModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Hashtable;

public class IdealGasControlPanel extends JPanel {

    private static final int s_stoveSliderHeight = 80;
    private static final int s_gravityControlPanelHeight = 80;

    private NumberFormat gravityFormat = NumberFormat.getInstance();
    private JTextField gravityTF;
    private JCheckBox gravityOnCB;
    private JSlider gravitySlider;
    private JPanel gravityControlPanel;
    private IdealGasModule module;
    private IdealGasModel idealGasModel;


    public IdealGasControlPanel( IdealGasModule module ) {
        super();
        this.module = module;
        this.idealGasModel = (IdealGasModel)module.getModel();
        init();
    }

    private void init() {

        this.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        this.setPreferredSize( new Dimension( 140, 300 ) );

//        final JCheckBox fastPaintTestCB = new JCheckBox( "fast paint" );
//        fastPaintTestCB.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                IdealGasConfig.fastPaint = fastPaintTestCB.isSelected();
//            }
//        } );
//        this.add( fastPaintTestCB );

//        final JCheckBox neighbors = new JCheckBox( "Test" );
//        neighbors.setSelected( true );
//        final Random r = new Random();
//        neighbors.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                //                boolean rx = r.nextBoolean();
//                //                Config.regionTest = neighbors.isSelected();
//                //                CollisionGod.doNeighbors = neighbors.isSelected();
//                //                System.out.println( "rx = " + rx );
//            }
//        } );


        addConstantParamControls();
        addGravityControls();
        JPanel speciesButtonPanel = new SpeciesSelectionPanel( module.getPump() );
        speciesButtonPanel.setBorder( new TitledBorder( SimStrings.get( "IdealGasControlPanel.Gas_In_Pump" ) ) );
        this.add( speciesButtonPanel );
        this.add( new NumParticlesControls() );
        addStoveControls();
        JButton measurementDlgBtn = new JButton( SimStrings.get( "IdealGasControlPanel.Measurement_Tools" ) );
        measurementDlgBtn.setAlignmentX( JButton.CENTER_ALIGNMENT );
        measurementDlgBtn.setBackground( new Color( 220, 200, 100 ) );
        measurementDlgBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JDialog dlg = new MeasurementDialog( (Frame)SwingUtilities.getRoot( IdealGasControlPanel.this ),
                                                     (IdealGasModule)getModule() );
                dlg.setVisible( true );
            }
        } );
        this.add( measurementDlgBtn );

        Border border = BorderFactory.createEtchedBorder();
        this.setBorder( border );

        // Add button for submitting a screen shot
        //        JButton screenShotBtn = new JButton( "Screen Shot" );
        //        this.add( screenShotBtn );
        //        screenShotBtn.addActionListener( new ActionListener() {
        //            public void actionPerformed( ActionEvent e ) {
        //                makeScreenShot();
        //            }
        //        } );

    }

    /**
     *
     */
    private void addConstantParamControls() {
        JPanel constantParamButtonPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        constantParamButtonPanel.setPreferredSize( new Dimension( IdealGasConfig.CONTROL_PANEL_WIDTH, 80 ) );
        final JRadioButton constantVolumeRB = new JRadioButton( SimStrings.get( "Common.Volume" ) );
        constantVolumeRB.setPreferredSize( new Dimension( 80, 15 ) );
        final JRadioButton constantPressureRB = new JRadioButton( SimStrings.get( "Common.Pressure" ) );
        constantPressureRB.setPreferredSize( new Dimension( 80, 15 ) );
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
    }

    /**
     * Create a panel with controls for gravity and add it to the IdealGasControlPanel
     */
    private void addGravityControls() {

        gravityControlPanel = new JPanel( new GridLayout( 1, 2 ) );
        gravityControlPanel.setPreferredSize( new Dimension( IdealGasConfig.CONTROL_PANEL_WIDTH, s_gravityControlPanelHeight ) );
        JPanel leftPanel = new JPanel( new GridLayout( 2, 1 ) );
        JPanel rightPanel = new JPanel( new GridLayout( 1, 1 ) );

        // Add control for gravity, set default to OFF
        //        gravitySlider = new JSlider( JSlider.VERTICAL, 0, 5000, 0 );
        gravitySlider = new JSlider( JSlider.VERTICAL, 0, s_gravityControlPanelHeight - 30, 0 );
        gravityOnCB = new JCheckBox( SimStrings.get( "Common.On" ) );
        leftPanel.add( gravityOnCB );
        gravityOnCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                updateGravity( gravityOnCB.isSelected(), gravitySlider.getValue() );
            }
        } );
        gravityOnCB.setSelected( false );
        leftPanel.add( gravityOnCB );
        gravityControlPanel.add( leftPanel );

        gravitySlider.setPreferredSize( new Dimension( 25, 100 ) );
        gravitySlider.setPaintTicks( true );
        gravitySlider.setMajorTickSpacing( 25 );
        gravitySlider.setMinorTickSpacing( 5 );
        rightPanel.add( gravitySlider );
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
        leftPanel.add( gravityTF );
        //        rightPanel.add( gravityTF );
        gravityControlPanel.add( rightPanel );

        Border gravityBorder = new TitledBorder( SimStrings.get( "Common.Gravity" ) );
        gravityControlPanel.setBorder( gravityBorder );
        this.add( gravityControlPanel );
    }

    /**
     * Create a panel for controlling the stove
     */
    private void addStoveControls() {
        JPanel stovePanel = new JPanel();
        JPanel stoveSliderPanel = new JPanel();
        JPanel iconPanel = new JPanel( new GridLayout( 3, 1 ) );
        Image stoveAndFlameImage = null;
        Image stoveImage = null;
        Image stoveAndIceImage = null;
        try {
            stoveAndFlameImage = ImageLoader.loadBufferedImage( IdealGasConfig.STOVE_AND_FLAME_ICON_FILE );
            stoveImage = ImageLoader.loadBufferedImage( IdealGasConfig.STOVE_ICON_FILE );
            stoveAndIceImage = ImageLoader.loadBufferedImage( IdealGasConfig.STOVE_AND_ICE_ICON_FILE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        Icon stoveAndFlameIcon = new ImageIcon( stoveAndFlameImage );
        Icon stoveIcon = new ImageIcon( stoveImage );
        Icon stoveAndIceIcon = new ImageIcon( stoveAndIceImage );
        iconPanel.add( new JLabel( stoveAndFlameIcon ) );
        iconPanel.add( new JLabel( stoveIcon ) );
        iconPanel.add( new JLabel( stoveAndIceIcon ) );
        stoveSliderPanel.add( iconPanel );
        iconPanel.setPreferredSize( new Dimension( 24, s_stoveSliderHeight ) );

        final JSlider stoveSlider = new JSlider( JSlider.VERTICAL, -40, 40, 0 );
        stoveSlider.setMajorTickSpacing( 10 );
        stoveSlider.setSnapToTicks( true );
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( -40 ), new JLabel( SimStrings.get( "Common.Remove" ) ) );
        labelTable.put( new Integer( 0 ), new JLabel( SimStrings.get( "Common.0" ) ) );
        labelTable.put( new Integer( 40 ), new JLabel( SimStrings.get( "Common.Add" ) ) );
        stoveSlider.setLabelTable( labelTable );
        stoveSlider.setPaintTicks( true );
        stoveSlider.setSnapToTicks( true );
        stoveSlider.setPaintLabels( true );
        stoveSlider.setPreferredSize( new Dimension( 76, s_stoveSliderHeight ) );
        stoveSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                setFlames( stoveSlider.getValue() );
            }
        } );
        stoveSliderPanel.add( stoveSlider );

        final JCheckBox heatSourceCB = new JCheckBox( SimStrings.get( "IdealGasControlPanel.Add_remove_heat_from_floor_only" ) );
        heatSourceCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                IdealGasConfig.heatOnlyFromFloor = heatSourceCB.isSelected();
            }
        } );

        // Put the panel together
        stovePanel.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = null;
        gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        stovePanel.add( stoveSliderPanel, gbc );
        gbc = new GridBagConstraints( 0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        stovePanel.add( heatSourceCB, gbc );

        stovePanel.setBorder( new TitledBorder( SimStrings.get( "IdealGasControlPanel.Heat_Control" ) ) );
        this.add( stovePanel );
    }

    private class NumParticlesControls extends JPanel {
        NumParticlesControls() {

            super( new GridBagLayout() );
            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                             GridBagConstraints.WEST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
            JLabel label = new JLabel( SimStrings.get( "MeasurementControlPanel.Number_of_particles" ) );
            this.add( label, gbc );
            // Set up the spinner for controlling the number of particles in
            // the hollow sphere
            Integer value = new Integer( 0 );
            Integer min = new Integer( 0 );
            Integer max = new Integer( 1000 );
            Integer step = new Integer( 1 );
            SpinnerNumberModel model = new SpinnerNumberModel( value, min, max, step );
            final JSpinner particleSpinner = new JSpinner( model );
            particleSpinner.setPreferredSize( new Dimension( 50, 20 ) );
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.EAST;
            this.add( particleSpinner, gbc );

            particleSpinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    setNumParticlesInBox( ( (Integer)particleSpinner.getValue() ).intValue() );
                }
            } );

            // Hook the spinner up so it will track molecules put in the box by the pump
            getModule().getModel().addObserver( new SimpleObserver() {
                public void update() {
                    int h = getModule().getIdealGasModel().getHeavySpeciesCnt();
                    int l = getModule().getIdealGasModel().getLightSpeciesCnt();
                    particleSpinner.setValue( new Integer( l + h ) );
                }
            } );
        }
    }

    private void setFlames( int value ) {
        module.setStove( value );
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

    protected IdealGasModule getModule() {
        return module;
    }

    private void setNumParticlesInBox( int numParticles ) {
        int dn = numParticles - ( getModule().getIdealGasModel().getHeavySpeciesCnt()
                                  + getModule().getIdealGasModel().getLightSpeciesCnt() );
        if( dn > 0 ) {
            for( int i = 0; i < dn; i++ ) {
                getModule().pumpGasMolecules( 1 );
            }
        }
        else if( dn < 0 ) {
            for( int i = 0; i < -dn; i++ ) {
                getModule().removeGasMolecule();
            }
        }
    }



    //    private void makeScreenShot() {
    //        Window w = SwingUtilities.getWindowAncestor( this );
    //        BufferedImage bi = new BufferedImage( w.getWidth(), w.getHeight(),
    //                                              BufferedImage.TYPE_INT_RGB );
    //        w.paint( bi.createGraphics() );
    //        try {
    //            // Save as PNG
    ////            File file = new File("newimage.png");
    ////            ImageIO.write(bi, "png", file);
    //
    //            // Save as JPEG
    ////            file = new File("newimage.jpg");
    ////            ImageIO.write(bi, "jpg", file);
    //            ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //            ImageIO.write( bi, "jpg", baos );
    //            String s = new sun.misc.BASE64Encoder().encode( baos.toByteArray() );
    ////            String s = baos.toString();
    //            int a = baos.size();
    //            int b = s.length();
    //            RemotePersistence rp = new RemotePersistence( "http://localhost/phptest/remote-jpg-pst.php", "test.jpg" );
    //            rp.store( s );
    //        }
    //        catch( IOException e ) {
    //            System.out.println( "Exception sending screen shot.\n" + e );
    //        }
    //
    //    }

}


