// edu.colorado.phet.graphics.idealgas.IdealGasControlPanel
/*
 * User: Administrator
 * Date: Nov 5, 2002
 * Time: 7:53:21 AM
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.controller.PhetControlPanel;
import edu.colorado.phet.graphics.util.ResourceLoader;
import edu.colorado.phet.idealgas.physics.*;
import edu.colorado.phet.physics.Constraint;
import edu.colorado.phet.physics.Gravity;
import edu.colorado.phet.physics.collision.Box2D;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.Observable;

public class IdealGasControlPanel extends PhetControlPanel {

    private Gravity gravity = new Gravity( 0 );
    private NumberFormat gravityFormat = NumberFormat.getInstance();
    private JTextField gravityTF;
    private IdealGasApplication application;
    private JCheckBox gravityOnCB;
    private JSlider gravitySlider;
    private JPanel gravityControlPanel;

    /**
     *
     */
    public IdealGasControlPanel( PhetApplication application ) {
        super( application );
        init();
    }

    /**
     *
     */
    private void init() {

        this.application = getIdealGasApplication();
        application.addExternalForce( gravity );
        this.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        this.setPreferredSize( new Dimension( 140, 300 ) );

        addConstantParamControls();
        addGravityControls();
        addSpeciesControls();
        addStoveControls();
        this.add( new PressureSliceControl() );
        this.add( new RulerControl() );
//        addLinebergerControls();

        Border border = BorderFactory.createEtchedBorder();
        this.setBorder( border );
    }

    /**
     *
     */
    protected IdealGasApplication getIdealGasApplication() {
        return (IdealGasApplication)PhetApplication.instance();
    }

    /**
     *
     */
    private void addConstantParamControls() {
        JPanel constantParamButtonPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        constantParamButtonPanel.setPreferredSize( new Dimension( IdealGasConfig.CONTROL_PANEL_WIDTH, 90 ) );
        final JRadioButton constantVolumeRB = new JRadioButton( "Volume" );
        final JRadioButton constantPressureRB = new JRadioButton( "Pressure" );
        final ButtonGroup constantParameterGroup = new ButtonGroup();
        constantParameterGroup.add( constantVolumeRB );
        constantParameterGroup.add( constantPressureRB );
        constantParamButtonPanel.add( constantVolumeRB );
        constantParamButtonPanel.add( constantPressureRB );
        constantParamButtonPanel.setBorder( new TitledBorder( "Constant Parameter" ) );
        this.add( constantParamButtonPanel );

        constantVolumeRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                application.getIdealGasSystem().setConstantVolume( constantVolumeRB.isSelected() );
                application.getIdealGasSystem().setConstantPressure( constantPressureRB.isSelected() );
            }
        } );
        constantPressureRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                application.getIdealGasSystem().setConstantPressure( constantPressureRB.isSelected() );
                application.getIdealGasSystem().setConstantVolume( constantVolumeRB.isSelected() );
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
        speciesButtonPanel.setPreferredSize( new Dimension( IdealGasConfig.CONTROL_PANEL_WIDTH, 120 ) );
        final JRadioButton heavySpeciesRB = new JRadioButton( "Heavy Species" );
        heavySpeciesRB.setForeground( Color.blue );
        final JRadioButton lightSpeciesRB = new JRadioButton( "Light Species" );
        lightSpeciesRB.setForeground( Color.red );
        final ButtonGroup speciesGroup = new ButtonGroup();
        speciesGroup.add( heavySpeciesRB );
        speciesGroup.add( lightSpeciesRB );
        speciesButtonPanel.add( heavySpeciesRB );
        speciesButtonPanel.add( lightSpeciesRB );
        speciesButtonPanel.setBorder( new TitledBorder( "Gas In Pump" ) );
        this.add( speciesButtonPanel );
        heavySpeciesRB.setSelected( true );
        heavySpeciesRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                if( heavySpeciesRB.isSelected() ) {
                    application.setCurrentSpecies( HeavySpecies.class );
                }
            }
        } );

        lightSpeciesRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                if( lightSpeciesRB.isSelected() ) {
                    application.setCurrentSpecies( LightSpecies.class );
                }
            }
        } );

        final JCheckBox cmLinesOnCB = new JCheckBox( "Show CM lines" );
        speciesButtonPanel.add( cmLinesOnCB );
        cmLinesOnCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                application.setCmLinesOn( cmLinesOnCB.isSelected() );
            }
        } );
    }

    /**
     * Create a panel for controlling the stove
     */
    private void addStoveControls() {
        JPanel stovePanel = new JPanel();

        JPanel iconPanel = new JPanel( new GridLayout( 3, 1 ) );
        ResourceLoader iconLoader = new ResourceLoader();
        Image stoveAndFlameImage = iconLoader.loadImage( IdealGasConfig.STOVE_AND_FLAME_ICON_FILE ).getImage();
        Image stoveImage = iconLoader.loadImage( IdealGasConfig.STOVE_ICON_FILE ).getImage();
        Image stoveAndIceImage = iconLoader.loadImage( IdealGasConfig.STOVE_AND_ICE_ICON_FILE ).getImage();
        Icon stoveAndFlameIcon = new ImageIcon( stoveAndFlameImage );
        Icon stoveIcon = new ImageIcon( stoveImage );
        Icon stoveAndIceIcon = new ImageIcon( stoveAndIceImage );
        iconPanel.add( new JLabel( stoveAndFlameIcon ) );
        iconPanel.add( new JLabel( stoveIcon ) );
        iconPanel.add( new JLabel( stoveAndIceIcon ) );
        stovePanel.add( iconPanel );
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
        stovePanel.add( stoveSlider );

        stovePanel.setBorder( new TitledBorder( "Heat Control" ) );
        this.add( stovePanel );
    }

    /**
     * This method is provided simply so a subclass can get a reference
     * to the gravity controls, so it can remove them. This is a hack
     * way of doing things, I realize. It should be done is a more
     * robust way when I get the chance.
     * @return
     */
    protected Component getGravityControlPanel() {
        return gravityControlPanel;
    }

    /**
     *
     */
    private void setFlames( int value ) {
        this.application.setStove( value );
    }

    /**
     *
     */
    private void updateGravity( boolean isEnabled, int value ) {

        gravityTF.setText( gravityFormat.format( value ) );
        if( !isEnabled ) {
            this.application.setGravity( null );
        }
        else {

            // The "-" sign is to work with screen and world coords
            gravity.setAmt( -value );
            this.application.setGravity( gravity );
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

    public void update( Observable observable, Object obj ) {
        super.update( observable, obj );

        if( observable instanceof IdealGasSystem ) {
            IdealGasSystem idealGasSystem = (IdealGasSystem)observable;
            gravityOnCB.setSelected( idealGasSystem.getGravity() != null );
        }
    }

    /**
     * Creates a gas molecule of the proper species
     */
    protected GasMolecule pumpGasMolecule() {

        // Add a new gas molecule to the system
        PumpMoleculeCmd pumpCmd = new PumpMoleculeCmd( getIdealGasApplication() );
        GasMolecule newMolecule = (GasMolecule)pumpCmd.doIt();

        // Constrain the molecule to be inside the box
        Box2D box = ( (IdealGasSystem)PhetApplication.instance().getPhysicalSystem() ).getBox();
        Constraint constraintSpec = new BoxMustContainParticle( box, newMolecule );
        newMolecule.addConstraint( constraintSpec );
        return newMolecule;
    }

    private class PressureSliceControl extends JPanel {

            PressureSliceControl() {
                final JCheckBox pressureSliceCB = new JCheckBox( "Measure pressure in layer" );
                this.add( pressureSliceCB );
                pressureSliceCB.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        IdealGasControlPanel.this.getIdealGasApplication().setPressureSliceEnabled( pressureSliceCB.isSelected() );
                    }
                } );
            }
        }

    private class RulerControl extends JPanel {

        RulerControl() {
            final JCheckBox rulerCB = new JCheckBox( "Display ruler" );
            this.add( rulerCB );
            rulerCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    IdealGasControlPanel.this.getIdealGasApplication().setRulerEnabed( rulerCB.isSelected() );
                }
            } );
        }
    }


//
// Static fields and methods
//
    private static final int s_stoveSliderHeight = 80;
    private static final int s_gravityControlPanelHeight = 80;
}


