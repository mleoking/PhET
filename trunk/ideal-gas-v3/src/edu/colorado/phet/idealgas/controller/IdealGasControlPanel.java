// edu.colorado.phet.graphics.idealgas.IdealGasControlPanel
/*
 * User: Administrator
 * Date: Nov 5, 2002
 * Time: 7:53:21 AM
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.Gravity;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.LightSpecies;

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
import java.util.Random;

public class IdealGasControlPanel extends JPanel {

    private Gravity gravity = new Gravity( 0 );
    private NumberFormat gravityFormat = NumberFormat.getInstance();
    private JTextField gravityTF;
    private JCheckBox gravityOnCB;
    private JSlider gravitySlider;
    private JPanel gravityControlPanel;
    private IdealGasModule module;
    private IdealGasModel idealGasModel;

    /**
     *
     */
    public IdealGasControlPanel( IdealGasModule module ) {
        super();
        this.module = module;
        this.idealGasModel = (IdealGasModel)module.getModel();
        init();
    }

    /**
     *
     */
    private void init() {

//        application.addExternalForce( gravity );

        this.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        this.setPreferredSize( new Dimension( 140, 300 ) );

        final JCheckBox fastPaintTestCB = new JCheckBox( "fast paint" );
        fastPaintTestCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                IdealGasConfig.fastPaint = fastPaintTestCB.isSelected();
            }
        } );
        final JCheckBox neighbors = new JCheckBox( "Test" );
        neighbors.setSelected( true );
        final Random r = new Random();
        neighbors.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
//                boolean rx = r.nextBoolean();
//                Config.regionTest = neighbors.isSelected();
//                CollisionGod.doNeighbors = neighbors.isSelected();
//                System.out.println( "rx = " + rx );
            }
        } );

        this.add( fastPaintTestCB );
//        this.add( neighbors );

        addConstantParamControls();
        addGravityControls();
        addSpeciesControls();
        addStoveControls();
        this.add( new PressureSliceControl() );
        this.add( new RulerControl() );
        //        addLinebergerControls();

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
        final JRadioButton constantVolumeRB = new JRadioButton( "Volume" );
        constantVolumeRB.setPreferredSize( new Dimension( 80, 15 ) );
        final JRadioButton constantPressureRB = new JRadioButton( "Pressure" );
        constantPressureRB.setPreferredSize( new Dimension( 80, 15 ) );
        final ButtonGroup constantParameterGroup = new ButtonGroup();
        constantParameterGroup.add( constantVolumeRB );
        constantParameterGroup.add( constantPressureRB );
        constantParamButtonPanel.add( constantVolumeRB );
        constantParamButtonPanel.add( constantPressureRB );
        constantParamButtonPanel.setBorder( new TitledBorder( "Constant Parameter" ) );
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
        gravitySlider = new JSlider( JSlider.VERTICAL, 0, s_gravityControlPanelHeight - 30, 0 );
        gravityOnCB = new JCheckBox( "On" );
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

        Border gravityBorder = new TitledBorder( "Gravity" );
        gravityControlPanel.setBorder( gravityBorder );
        this.add( gravityControlPanel );
    }

    /**
     * Create a panel with controls for the gas species and add it to the
     * IdealGasControlPanel
     */
    private void addSpeciesControls() {
        JPanel speciesButtonPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        speciesButtonPanel.setPreferredSize( new Dimension( IdealGasConfig.CONTROL_PANEL_WIDTH, 100 ) );
        final JRadioButton heavySpeciesRB = new JRadioButton( "Heavy Species" );
        heavySpeciesRB.setForeground( Color.blue );
        final JRadioButton lightSpeciesRB = new JRadioButton( "Light Species" );
        lightSpeciesRB.setForeground( Color.red );
        final ButtonGroup speciesGroup = new ButtonGroup();
        speciesGroup.add( heavySpeciesRB );
        speciesGroup.add( lightSpeciesRB );
        speciesButtonPanel.add( heavySpeciesRB );
        heavySpeciesRB.setPreferredSize( new Dimension( 110, 15 ) );
        lightSpeciesRB.setPreferredSize( new Dimension( 110, 15 ) );
        speciesButtonPanel.add( lightSpeciesRB );
        speciesButtonPanel.setBorder( new TitledBorder( "Gas In Pump" ) );
        this.add( speciesButtonPanel );
        heavySpeciesRB.setSelected( true );
        heavySpeciesRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                if( heavySpeciesRB.isSelected() ) {
                    module.setCurrentSpecies( HeavySpecies.class );
                }
            }
        } );

        lightSpeciesRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                if( lightSpeciesRB.isSelected() ) {
                    module.setCurrentSpecies( LightSpecies.class );
                }
            }
        } );

        final JCheckBox cmLinesOnCB = new JCheckBox( "Show CM lines" );
        cmLinesOnCB.setPreferredSize( new Dimension( 110, 15 ) );
        speciesButtonPanel.add( cmLinesOnCB );
        cmLinesOnCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                module.setCmLinesOn( cmLinesOnCB.isSelected() );
            }
        } );
    }

    /**
     * Create a panel for controlling the stove
     */
    private void addStoveControls() {
        JPanel stovePanel = new JPanel( new GridLayout( 2, 1 ));
        JPanel stoveSliderPanel = new JPanel();

        JPanel iconPanel = new JPanel( new GridLayout( 3, 1 ) );
//        ResourceLoader iconLoader = new ResourceLoader();
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
        //        Image stoveAndFlameImage = iconLoader.loadImage( IdealGasConfig.STOVE_AND_FLAME_ICON_FILE ).getImage();
//        Image stoveImage = iconLoader.loadImage( IdealGasConfig.STOVE_ICON_FILE ).getImage();
//        Image stoveAndIceImage = iconLoader.loadImage( IdealGasConfig.STOVE_AND_ICE_ICON_FILE ).getImage();
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
        labelTable.put( new Integer( -40 ), new JLabel( "Remove" ) );
        labelTable.put( new Integer( 0 ), new JLabel( "0" ) );
        labelTable.put( new Integer( 40 ), new JLabel( "Add" ) );
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
        stovePanel.add( stoveSliderPanel );

        final JCheckBox testCB = new JCheckBox( "<html>Add/remove heat<br>from floor only</html>");
        testCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                IdealGasConfig.heatOnlyFromFloor = testCB.isSelected();
            }
        } );
        stovePanel.add( testCB );

        stovePanel.setBorder( new TitledBorder( "Heat Control" ) );
        this.add( stovePanel );
    }

    /**
     * This method is provided simply so a subclass can get a reference
     * to the gravity controls, so it can remove them. This is a hack
     * way of doing things, I realize. It should be done is a more
     * robust way when I get the chance.
     *
     * @return
     */
    protected Component getGravityControlPanel() {
        return gravityControlPanel;
    }

    /**
     *
     */
    private void setFlames( int value ) {
        module.setStove( value );
//        this.application.setStove( value );
    }

    /**
     *
     */
    private void updateGravity( boolean isEnabled, int value ) {

        gravityTF.setText( gravityFormat.format( value ) );
        if( !isEnabled ) {
            module.setGravity( null );
//            this.application.setGravity( null );
        }
        else {

            // The "-" sign is to work with screen and world coords
            gravity.setAmt( -value );
            module.setGravity( gravity );
//            this.application.setGravity( gravity );
        }
    }

    public void setGravityEnabled( boolean enabled ) {
        this.gravityOnCB.setSelected( enabled );
    }

    public void setGravity( double amt ) {
        this.gravitySlider.setValue( (int)amt );
    }

    public void clear() {
        // NOP
    }

//    public void update( Observable observable, Object obj ) {
//        super.update( observable, obj );
//
//        if( observable instanceof IdealGasSystem ) {
//            IdealGasSystem idealGasSystem = (IdealGasSystem)observable;
//            gravityOnCB.setSelected( idealGasSystem.getGravity() != null );
//        }
//    }

    /**
     * Creates a gas molecule of the proper species
     */
//    protected GasMolecule pumpGasMolecule() {
//
//        // Add a new gas molecule to the system
//        PumpMoleculeCmd pumpCmd = new PumpMoleculeCmd( getIdealGasApplication() );
//        GasMolecule newMolecule = (GasMolecule)pumpCmd.doIt();
//
//        // Constrain the molecule to be inside the box
//        Box2D box = ( (IdealGasSystem)PhetApplication.instance().getPhysicalSystem() ).getBox();
//        Constraint constraintSpec = new BoxMustContainParticle( box, newMolecule );
//        newMolecule.addConstraint( constraintSpec );
//        return newMolecule;
//    }

    protected class PressureSliceControl extends JPanel {
        PressureSliceControl() {
            String msg = "<html>Measure pressure<br>in layer</html>";
            final JCheckBox pressureSliceCB = new JCheckBox( msg );
            pressureSliceCB.setPreferredSize( new Dimension( 140, 30 ) );
            //            final JCheckBox pressureSliceCB = new JCheckBox( "Measure pressure in layer" );
            this.add( pressureSliceCB );
            pressureSliceCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setPressureSliceEnabled( pressureSliceCB.isSelected() );
//                    IdealGasControlPanel.this.getIdealGasApplication().setPressureSliceEnabled( pressureSliceCB.isSelected() );
                }
            } );
        }
    }

    protected class RulerControl extends JPanel {
        RulerControl() {
            final JCheckBox rulerCB = new JCheckBox( "Display ruler" );
            rulerCB.setPreferredSize( new Dimension( 140, 15 ) );
            this.add( rulerCB );
            rulerCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setRulerEnabed( rulerCB.isSelected() );
//                    IdealGasControlPanel.this.getIdealGasApplication().setRulerEnabed( rulerCB.isSelected() );
                }
            } );
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

    //
    // Static fields and methods
    //
    private static final int s_stoveSliderHeight = 80;
    private static final int s_gravityControlPanelHeight = 80;


}


