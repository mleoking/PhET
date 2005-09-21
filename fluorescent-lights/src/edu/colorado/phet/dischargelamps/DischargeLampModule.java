/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.control.AtomTypeChooser;
import edu.colorado.phet.dischargelamps.control.BatterySlider;
import edu.colorado.phet.dischargelamps.model.*;
import edu.colorado.phet.dischargelamps.view.*;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.*;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.view.AtomGraphic;
import edu.colorado.phet.lasers.view.ResonatingCavityGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * DischargeLampModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampModule extends BaseLaserModule {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

//    public static boolean DEBUG = true;
    public static boolean DEBUG = false;
    private static final double SPECTROMETER_LAYER = 1000;
    private static double VOLTAGE_VALUE_LAYER = DischargeLampsConfig.CIRCUIT_LAYER + 1;

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    private ResonatingCavity tube;
    // The states in which the atoms can be
    private Random random = new Random();
    private DischargeLampModel model;
    private Plate leftHandPlate;
    private Plate rightHandPlate;
    private double maxCurrent = 200;
    protected double currentDisplayFactor = 300;
//    private double maxCurrent = 300;
//    private double maxCurrent = 0.3;
    private ElementProperties[] elementProperties;
    private ConfigurableElementProperties configurableElement;

    // The scale to apply to graphics created in external applications so they appear properly
    // on the screen
    private double externalGraphicsScale;
    // AffineTransformOp that will scale graphics created in external applications so they appear
    // properly on the screen
    private AffineTransformOp externalGraphicScaleOp;
    private ModelSlider currentSlider;
    private DischargeLampEnergyMonitorPanel2 energyLevelsMonitorPanel;
    private SpectrometerGraphic spectrometerGraphic;
    private HeatingElementGraphic[] heatingElementGraphics = new HeatingElementGraphic[2];
    private JCheckBox squiggleCB;

    //----------------------------------------------------------------
    // Constructors and initialization
    //----------------------------------------------------------------

    /**
     * Constructor
     *
     * @param clock
     */
    protected DischargeLampModule( String name, AbstractClock clock ) {
        super( name, clock );

        // Set up the basic stuff
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setPaintStrategy( ApparatusPanel2.OFFSCREEN_BUFFER_STRATEGY );
        apparatusPanel.setBackground( Color.white );
        setApparatusPanel( apparatusPanel );

        // Set up the model
        model = new DischargeLampModel();
        model.setElectronProductionMode( ElectronSource.CONTINUOUS_MODE );
        model.setMaxCurrent( maxCurrent / currentDisplayFactor );
        leftHandPlate = model.getLeftHandPlate();
        rightHandPlate = model.getRightHandPlate();
        setModel( model );
        setControlPanel( new ControlPanel( this ) );

        // Create a listener on the apparatus panel that will maintain the bounds of the model
        // to be conformant with the panel
        apparatusPanel.addChangeListener( new ApparatusPanel2.ChangeListener() {
            public void canvasSizeChanged( ApparatusPanel2.ChangeEvent event ) {
                model.setBounds( new Rectangle2D.Double( 0, 0, event.getCanvasSize().getWidth(), event.getCanvasSize().getHeight() ) );
            }
        } );

        // Create the element properties we will use
        configurableElement = new ConfigurableElementProperties( 2, model );
        ElementProperties hydrogen = new HydrogenProperties();
        elementProperties = new ElementProperties[]{
            hydrogen,
            configurableElement
        };

        // Add graphics
        addCircuitGraphic( apparatusPanel );
        addCathodeGraphic( apparatusPanel );
        addAnodeGraphic( apparatusPanel );
        addSpectrometerGraphic();
        addHeatingElementGraphics();
        addTubeGraphic( apparatusPanel );

        // Set up the control panel
        addGraphicBatteryControls();
        addControls();
    }

    /**
     * Places a slider and digital readout on the battery graphic
     */
    private void addGraphicBatteryControls() {
        Battery battery = model.getBattery();
        final BatterySlider bSl = new BatterySlider( getApparatusPanel(), 80 /* track length */, battery );
        bSl.setMinimum( (int)-( battery.getMaxVoltage() ) );
        bSl.setMaximum( (int)( battery.getMaxVoltage() ) );
        bSl.setValue( (int)( 0 ) );
        bSl.addTick( bSl.getMinimum() );
        bSl.addTick( bSl.getMaximum() );
        bSl.addTick( 0 );
        bSl.setLocation( (int)DischargeLampsConfig.CATHODE_LOCATION.getX() + 174, 60 );
        getApparatusPanel().addGraphic( bSl, DischargeLampsConfig.CIRCUIT_LAYER + 1 );

        final PhetGraphic batteryReadout = new BatteryReadout( getApparatusPanel(),
                                                               battery,
                                                               new Point( (int)DischargeLampsConfig.CATHODE_LOCATION.getX() + 194,
                                                                          78 ),
                                                               35 );
        addGraphic( batteryReadout, VOLTAGE_VALUE_LAYER );
    }

    /**
     * Adds the graphics for the heating elements
     */
    private void addHeatingElementGraphics() {
        HeatingElementGraphic leftHeatingElementGraphic = new HeatingElementGraphic( getApparatusPanel(), true );
        getApparatusPanel().addGraphic( leftHeatingElementGraphic );
        Point leftHandGraphicLocation = new Point( (int)model.getLeftHandHeatingElement().getPosition().getX()
                                                   - leftHeatingElementGraphic.getImage().getWidth() - 30,
                                                   (int)model.getLeftHandHeatingElement().getPosition().getY() - 80 );
        leftHeatingElementGraphic.setLocation( leftHandGraphicLocation );

        HeatingElementGraphic rightHeatingElementGraphic = new HeatingElementGraphic( getApparatusPanel(), false );
        getApparatusPanel().addGraphic( rightHeatingElementGraphic );
        Point rightHandGraphicLocation = new Point( (int)model.getRightHandHeatingElement().getPosition().getX() + 30,
                                                    (int)model.getRightHandHeatingElement().getPosition().getY() - 80 );
        rightHeatingElementGraphic.setLocation( rightHandGraphicLocation );
        heatingElementGraphics[0] = leftHeatingElementGraphic;
        heatingElementGraphics[1] = rightHeatingElementGraphic;

        model.getLeftHandHeatingElement().addChangeListener( leftHeatingElementGraphic );
        model.getRightHandHeatingElement().addChangeListener( rightHeatingElementGraphic );

        leftHandPlate.addElectronProductionListener( new ElectronGraphicManager( getApparatusPanel() ) );
        rightHandPlate.addElectronProductionListener( new ElectronGraphicManager( getApparatusPanel() ) );
    }

    /**
     * Creates the tube, adds it to the model and creates a graphic for it
     *
     * @param apparatusPanel
     */
    private void addTubeGraphic( ApparatusPanel apparatusPanel ) {
        ResonatingCavity tube = model.getTube();
        ResonatingCavityGraphic tubeGraphic = new ResonatingCavityGraphic( getApparatusPanel(), tube );
        apparatusPanel.addGraphic( tubeGraphic, DischargeLampsConfig.TUBE_LAYER );
        this.tube = tube;
    }

    /**
     * @param apparatusPanel
     */
    private void addAnodeGraphic( ApparatusPanel apparatusPanel ) {
        PlateGraphic anodeGraphic = new PlateGraphic( getApparatusPanel(), DischargeLampsConfig.CATHODE_LENGTH );
        model.getRightHandHeatingElement().addChangeListener( anodeGraphic );
        anodeGraphic.setRegistrationPoint( (int)anodeGraphic.getBounds().getWidth(),
                                           (int)anodeGraphic.getBounds().getHeight() / 2 );
        anodeGraphic.setRegistrationPoint( 0, (int)anodeGraphic.getBounds().getHeight() / 2 );
        anodeGraphic.setLocation( DischargeLampsConfig.ANODE_LOCATION );
        apparatusPanel.addGraphic( anodeGraphic, DischargeLampsConfig.CIRCUIT_LAYER );
    }

    /**
     * @param apparatusPanel
     */
    private void addCathodeGraphic( ApparatusPanel apparatusPanel ) {
        PlateGraphic cathodeGraphic = new PlateGraphic( getApparatusPanel(), DischargeLampsConfig.CATHODE_LENGTH );
        model.getLeftHandHeatingElement().addChangeListener( cathodeGraphic );
        cathodeGraphic.setRegistrationPoint( (int)cathodeGraphic.getBounds().getWidth(),
                                             (int)cathodeGraphic.getBounds().getHeight() / 2 );
        cathodeGraphic.setLocation( DischargeLampsConfig.CATHODE_LOCATION );
        apparatusPanel.addGraphic( cathodeGraphic, DischargeLampsConfig.CIRCUIT_LAYER );
    }

    /**
     * @param apparatusPanel
     */
    private void addCircuitGraphic( ApparatusPanel apparatusPanel ) {
        CircuitGraphic circuitGraphic = new CircuitGraphic( apparatusPanel, getExternalGraphicScaleOp() );
        model.addChangeListener( circuitGraphic );
        circuitGraphic.setRegistrationPoint( (int)( 124 * externalGraphicsScale ), (int)( 340 * externalGraphicsScale ) );
        circuitGraphic.setLocation( DischargeLampsConfig.CATHODE_LOCATION );
        apparatusPanel.addGraphic( circuitGraphic, DischargeLampsConfig.CIRCUIT_LAYER );
    }

    /**
     * Adds the spectrometer graphic
     */
    private void addSpectrometerGraphic() {
        spectrometerGraphic = new SpectrometerGraphic( getApparatusPanel(), model.getSpectrometer() );
        addGraphic( spectrometerGraphic, SPECTROMETER_LAYER );
        int centerX = ( DischargeLampsConfig.ANODE_LOCATION.x + DischargeLampsConfig.CATHODE_LOCATION.x ) / 2;
        spectrometerGraphic.setLocation( centerX, 430 );
        spectrometerGraphic.setRegistrationPoint( spectrometerGraphic.getWidth() / 2, 0 );
    }

    /**
     * Scales an image graphic so it appears properly on the screen. This method depends on the image used by the
     * graphic to have been created at the same scale as the battery-wires graphic. The scale is based on the
     * distance between the electrodes in that image and the screen distance between the electrodes specified
     * in the configuration file.
     *
     * @param imageGraphic
     */
    private void scaleImageGraphic( PhetImageGraphic imageGraphic ) {
        getExternalGraphicScaleOp();
        imageGraphic.setImage( externalGraphicScaleOp.filter( imageGraphic.getImage(), null ) );
    }

    /**
     * Returns an AffineTransformOp that will scale BufferedImages to the dimensions of the
     * apparatus panel
     *
     * @return
     */
    private AffineTransformOp getExternalGraphicScaleOp() {
        if( externalGraphicScaleOp == null ) {
            int cathodeAnodeScreenDistance = 550;
            determineExternalGraphicScale( DischargeLampsConfig.ANODE_LOCATION,
                                           DischargeLampsConfig.CATHODE_LOCATION,
                                           cathodeAnodeScreenDistance );
            AffineTransform scaleTx = AffineTransform.getScaleInstance( externalGraphicsScale, externalGraphicsScale );
            externalGraphicScaleOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
        }
        return externalGraphicScaleOp;
    }

    /**
     * Computes the scale to be applied to externally created graphics.
     * <p/>
     * Scale is determined by specifying a distance in the external graphics that should
     * be the same as the distance between two point on the screen.
     *
     * @param p1
     * @param p2
     * @param externalGraphicDist
     */
    private void determineExternalGraphicScale( Point p1, Point p2, int externalGraphicDist ) {
        externalGraphicsScale = p1.distance( p2 ) / externalGraphicDist;
    }

    /**
     * Sets up the control panel
     */
    private void addControls() {

        // A combo box for atom types

        JComponent atomTypeComboBox = new AtomTypeChooser( model, elementProperties );
        getControlPanel().add( atomTypeComboBox );

        // Panel with check boxes
        {
            JPanel checkBoxPanel = new JPanel( new GridLayout( 1, 2 ) );
            // Add a button to show/hide the spectrometer
            final JCheckBox spectrometerCB = new JCheckBox( SimStrings.get( "ControlPanel.SpectrometerButtonLabel" ) );
            spectrometerCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    spectrometerGraphic.setVisible( spectrometerCB.isSelected() );
                    model.getSpectrometer().reset();
                    model.getSpectrometer().start();
                }
            } );
            checkBoxPanel.add( spectrometerCB );
            spectrometerGraphic.setVisible( spectrometerCB.isSelected() );

            squiggleCB = new JCheckBox( "Squiggles" );
            squiggleCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    energyLevelsMonitorPanel.setSquigglesEnabled( squiggleCB.isSelected() );
                }
            } );
            checkBoxPanel.add( squiggleCB );
            getControlPanel().add( checkBoxPanel );
        }

        // A slider for the battery voltage
        double defaultVoltage = 25;
        final ModelSlider batterySlider = new ModelSlider( "Battery Voltage",
                                                           "V",
                                                           -30, 30,
                                                           defaultVoltage );
        batterySlider.setMajorTickSpacing( 10 );
        batterySlider.setNumMinorTicksPerMajorTick( 1 );
        batterySlider.setSliderLabelFormat( new DecimalFormat( "##" ) );
        batterySlider.setPreferredSize( new Dimension( 250, 100 ) );
        ControlPanel controlPanel = (ControlPanel)getControlPanel();
        controlPanel.addControl( batterySlider );
        batterySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double voltage = batterySlider.getValue() / DischargeLampsConfig.VOLTAGE_CALIBRATION_FACTOR;
                model.getBattery().setVoltage( voltage );
//                model.setVoltage( voltage );
            }
        } );
        double voltage = batterySlider.getValue() / DischargeLampsConfig.VOLTAGE_CALIBRATION_FACTOR;
        model.setVoltage( voltage );

        // A slider for the battery current
        currentSlider = new ModelSlider( "Electron Production Rate", "electrons/sec",
                                         0, maxCurrent, 0 );
//                                         0, maxCurrent, 0, new DecimalFormat( "0.000" ) );
        currentSlider.setSliderLabelFormat( new DecimalFormat( "#" ) );
        currentSlider.setTextFieldFormat( new DecimalFormat( "#" ) );
        currentSlider.setMajorTickSpacing( 50 );
//        currentSlider.setMajorTickSpacing( maxCurrent / 3 );
        currentSlider.setNumMinorTicksPerMajorTick( 1 );
        currentSlider.setPaintLabels( true );
        currentSlider.setPreferredSize( new Dimension( 250, 100 ) );
        controlPanel.addControl( currentSlider );
        currentSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setCurrent( currentSlider.getValue(), 1 / currentDisplayFactor );
            }
        } );
        currentSlider.setValue( 5 );
        // Add an energy level monitor panel.
        energyLevelsMonitorPanel = new DischargeLampEnergyMonitorPanel2( model, getClock(),
                                                                         model.getAtomicStates(),
                                                                         200,
                                                                         300,
                                                                         configurableElement );
        getControlPanel().add( energyLevelsMonitorPanel );
    }

    /**
     * Adds some atoms and their graphics
     *
     * @param tube
     * @param numAtoms
     */
    protected void addAtoms( ResonatingCavity tube, int numAtoms, double maxSpeed ) {
        DischargeLampAtom atom = null;
        ArrayList atoms = new ArrayList();
        Rectangle2D tubeBounds = tube.getBounds();

        AtomicState[] atomicStates = model.getAtomicStates();

        for( int i = 0; i < numAtoms; i++ ) {
//            atom = new DischargeLampAtom( (LaserModel)getModel(), model.getElementProperties() );
            atom = new DischargeLampAtom( (LaserModel)getModel(), atomicStates );
            atom.setPosition( ( tubeBounds.getX() + ( Math.random() ) * ( tubeBounds.getWidth() - atom.getRadius() * 4 ) + atom.getRadius() * 2 ),
                              ( tubeBounds.getY() + ( Math.random() ) * ( tubeBounds.getHeight() - atom.getRadius() * 4 ) ) + atom.getRadius() * 2 );
            atom.setVelocity( (float)( Math.random() - 0.5 ) * maxSpeed,
                              (float)( Math.random() - 0.5 ) * maxSpeed );
            atoms.add( atom );
            addAtom( atom );
        }
        energyLevelsMonitorPanel.reset();
    }

    /**
     * Extends parent behavior to place half the atoms in a layer above the electrodes, and half below
     */
    protected AtomGraphic addAtom( Atom atom ) {
        energyLevelsMonitorPanel.addAtom( atom );
        AtomGraphic graphic = super.addAtom( atom );

        // Replace the graphic that the super class made with one that is specific to this
        // application
        getApparatusPanel().removeGraphic( graphic );
        atom.removeChangeListener( graphic );
        graphic = new DischargeLampAtomGraphic( getApparatusPanel(), atom );
        // Put some of the atoms in a layer above the circuit, and some below
        getApparatusPanel().addGraphic( graphic, DischargeLampsConfig.CIRCUIT_LAYER - 1 );
        if( random.nextBoolean() ) {
            getApparatusPanel().removeGraphic( graphic );
            getApparatusPanel().addGraphic( graphic, DischargeLampsConfig.CIRCUIT_LAYER + 1 );
        }
        return graphic;
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    /**
     * @param isVisible
     */
    protected void setHeatingElementsVisible( boolean isVisible ) {
        for( int i = 0; i < heatingElementGraphics.length; i++ ) {
            HeatingElementGraphic graphic = heatingElementGraphics[i];
            graphic.setVisible( isVisible );
        }
    }

    /**
     * Returns a typed reference to the model
     */
    protected DischargeLampModel getDischargeLampModel() {
        return (DischargeLampModel)getModel();
    }

    /**
     * @return
     */
    protected ResonatingCavity getTube() {
        return tube;
    }

    /**
     * @return
     */
    protected ModelSlider getCurrentSlider() {
        return currentSlider;
    }

    /**
     * @return
     */
    protected DischargeLampEnergyMonitorPanel2 getEneregyLevelsMonitorPanel() {
        return energyLevelsMonitorPanel;
    }

    /**
     * @param enabled
     */
    public void setSquigglesEnabled( boolean enabled ) {
        squiggleCB.setSelected( true );
        energyLevelsMonitorPanel.setSquigglesEnabled( squiggleCB.isSelected() );
    }
}
