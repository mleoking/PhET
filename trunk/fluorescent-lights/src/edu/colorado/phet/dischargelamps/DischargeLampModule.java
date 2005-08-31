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
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.dischargelamps.control.AtomTypeChooser;
import edu.colorado.phet.dischargelamps.model.*;
import edu.colorado.phet.dischargelamps.view.*;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
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

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    // The scale to apply to graphics created in external applications so they appear properly
    // on the screen
    private double externalGraphicsScale;
    // AffineTransformOp that will scale graphics created in external applications so they appear
    // properly on the screen
    private AffineTransformOp externalGraphicScaleOp;
    private ResonatingCavity tube;
    private ModelSlider currentSlider;
    private Spectrometer spectrometer;
    // The states in which the atoms can be
    private DischargeLampEnergyMonitorPanel2 energyLevelsMonitorPanel;
    private Random random = new Random();
    private SpectrometerGraphic spectrometerGraphic;
    private DischargeLampModel model;
    private Plate leftHandPlate;
    private Plate rightHandPlate;
    private double maxCurrent = 0.3;


    //----------------------------------------------------------------
    // Constructors and initialization
    //----------------------------------------------------------------

    /**
     * Constructor
     *
     * @param clock
     */
    protected DischargeLampModule( String name, AbstractClock clock, int numEnergyLevels ) {
        super( name, clock );

        // Set up the basic stuff
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setPaintStrategy( ApparatusPanel2.OFFSCREEN_BUFFER_STRATEGY );
        apparatusPanel.setBackground( Color.white );
        setApparatusPanel( apparatusPanel );

        // Set up the model
        model = new DischargeLampModel();
        model.setElectronProductionMode( ElectronSource.CONTINUOUS_MODE );
        model.setMaxCurrent( maxCurrent );
        leftHandPlate = model.getLeftHandPlate();
        rightHandPlate = model.getRightHandPlate();
        setModel( model );
        setControlPanel( new ControlPanel( this ) );
        model.setNumAtomicEnergyLevels( 2 );
        spectrometer = model.getSpectrometer();

        // Add graphics
        addCircuitGraphic( apparatusPanel );
        addCathodeGraphic( apparatusPanel );
        addAnodeGraphic( apparatusPanel );
        addSpectrometerGraphic();
        addHeatingElementGraphics();


        // Add the tube
        addTubeGraphic( apparatusPanel );

        // Set up the control panel
        addControls();
    }

//    public void activate( PhetApplication app ) {
//        super.activate( app );
//        model.setMaxCurrent( maxCurrent );
////        model.setElectronProductionMode( ElectronSource.CONTINUOUS_MODE );
//    }

    /**
     * Adds the graphics for the heating elements
     */
    private void addHeatingElementGraphics() {
        HeatingElementGraphic leftHeatingElementGraphic = new HeatingElementGraphic( getApparatusPanel(), true );
        getApparatusPanel().addGraphic( leftHeatingElementGraphic );
        Point leftHandGraphicLocation = new Point( (int)model.getLeftHandHeatingElement().getPosition().getX()
                                                   - leftHeatingElementGraphic.getImage().getWidth() - 25,
                                                   (int)model.getLeftHandHeatingElement().getPosition().getY()
                                                   - leftHeatingElementGraphic.getImage().getHeight() / 2 );
        leftHeatingElementGraphic.setLocation( leftHandGraphicLocation );

        HeatingElementGraphic rightHeatingElementGraphic = new HeatingElementGraphic( getApparatusPanel(), false );
        getApparatusPanel().addGraphic( rightHeatingElementGraphic );
        Point rightHandGraphicLocation = new Point( (int)model.getRightHandHeatingElement().getPosition().getX() + 25,
                                                    (int)model.getRightHandHeatingElement().getPosition().getY()
                                                    - leftHeatingElementGraphic.getImage().getHeight() / 2 );
        rightHeatingElementGraphic.setLocation( rightHandGraphicLocation );

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
        PlateGraphic anodeGraphic = new PlateGraphic( getApparatusPanel() );
        model.getRightHandHeatingElement().addChangeListener( anodeGraphic );
//        PhetImageGraphic anodeGraphic = new PhetImageGraphic( getApparatusPanel(), "images/electrode-2.png" );

        // Make the graphic the right size
        double scaleX = 1;
        double scaleY = DischargeLampsConfig.CATHODE_LENGTH / anodeGraphic.getImage().getHeight();
        AffineTransformOp scaleOp = new AffineTransformOp( AffineTransform.getScaleInstance( scaleX, scaleY ),
                                                           AffineTransformOp.TYPE_BILINEAR );
        anodeGraphic.setImage( scaleOp.filter( anodeGraphic.getImage(), null ) );
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
        PlateGraphic cathodeGraphic = new PlateGraphic( getApparatusPanel() );
        model.getLeftHandHeatingElement().addChangeListener( cathodeGraphic );

//        leftHandPlate.addElectronProductionListener( new ElectronGraphicManager( apparatusPanel ) );
//        rightHandPlate.addElectronProductionListener( new ElectronGraphicManager( apparatusPanel ) );
//        PhetImageGraphic cathodeGraphic = new PhetImageGraphic( getApparatusPanel(), "images/electrode-2.png" );

        // Make the graphic the right size
        double scaleX = 1;
        double scaleY = DischargeLampsConfig.CATHODE_LENGTH / cathodeGraphic.getImage().getHeight();
        AffineTransformOp scaleOp = new AffineTransformOp( AffineTransform.getScaleInstance( scaleX, scaleY ),
                                                           AffineTransformOp.TYPE_BILINEAR );
        cathodeGraphic.setImage( scaleOp.filter( cathodeGraphic.getImage(), null ) );
        cathodeGraphic.setRegistrationPoint( (int)cathodeGraphic.getBounds().getWidth(),
                                             (int)cathodeGraphic.getBounds().getHeight() / 2 );

        cathodeGraphic.setLocation( DischargeLampsConfig.CATHODE_LOCATION );
        apparatusPanel.addGraphic( cathodeGraphic, DischargeLampsConfig.CIRCUIT_LAYER );
    }

    /**
     * @param apparatusPanel
     */
    private void addCircuitGraphic( ApparatusPanel apparatusPanel ) {
        PhetImageGraphic circuitGraphic = new PhetImageGraphic( getApparatusPanel(), "images/battery-w-wires-2.png" );
        scaleImageGraphic( circuitGraphic );
        circuitGraphic.setRegistrationPoint( (int)( 124 * externalGraphicsScale ), (int)( 340 * externalGraphicsScale ) );
        circuitGraphic.setLocation( DischargeLampsConfig.CATHODE_LOCATION );
        apparatusPanel.addGraphic( circuitGraphic, DischargeLampsConfig.CIRCUIT_LAYER );
    }

    /**
     * Adds the spectrometer graphic
     */
    private void addSpectrometerGraphic() {
        spectrometerGraphic = new SpectrometerGraphic( getApparatusPanel(), spectrometer );
        addGraphic( spectrometerGraphic, SPECTROMETER_LAYER );
        int centerX = ( DischargeLampsConfig.ANODE_LOCATION.x + DischargeLampsConfig.CATHODE_LOCATION.x ) / 2;
        spectrometerGraphic.setLocation( centerX, 450 );
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
        if( externalGraphicScaleOp == null ) {
            int cathodeAnodeScreenDistance = 550;
            determineExternalGraphicScale( DischargeLampsConfig.ANODE_LOCATION,
                                           DischargeLampsConfig.CATHODE_LOCATION,
                                           cathodeAnodeScreenDistance );
            AffineTransform scaleTx = AffineTransform.getScaleInstance( externalGraphicsScale, externalGraphicsScale );
            externalGraphicScaleOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
        }
        imageGraphic.setImage( externalGraphicScaleOp.filter( imageGraphic.getImage(), null ) );
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
        JComponent atomTypeComboBox = new AtomTypeChooser( model );
        getControlPanel().add( atomTypeComboBox );

// A slider for the battery voltage
        final ModelSlider batterySlider = new ModelSlider( "Battery Voltage",
                                                           "V",
                                                           -DischargeLampModel.MAX_VOLTAGE,
                                                           DischargeLampModel.MAX_VOLTAGE,
                                                           DischargeLampModel.MAX_VOLTAGE * 0.66 );
        batterySlider.setPreferredSize( new Dimension( 250, 100 ) );
        batterySlider.setMajorTickSpacing( DischargeLampModel.MAX_VOLTAGE / 2 );
        ControlPanel controlPanel = (ControlPanel)getControlPanel();
        controlPanel.addControl( batterySlider );
        batterySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double voltage = batterySlider.getValue();
                model.setVoltage( voltage );
            }
        } );
        leftHandPlate.setPotential( batterySlider.getValue() );
        rightHandPlate.setPotential( 0 );

        // A slider for the battery current
        currentSlider = new ModelSlider( "Electron Production Rate", "electrons/msec",
                                         0, maxCurrent, 0, new DecimalFormat( "0.000" ) );
        currentSlider.setMajorTickSpacing( maxCurrent );
        currentSlider.setPreferredSize( new Dimension( 250, 100 ) );
        controlPanel.addControl( currentSlider );
        currentSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setCurrent( currentSlider.getValue() );
            }
        } );

        // Add an energy level monitor panel. Note that the panel has a null layout, so we have to put it in a
        // panel that does have one, so it gets laid out properly
        energyLevelsMonitorPanel = new DischargeLampEnergyMonitorPanel2( model, getClock(),
                                                                         model.getAtomicStates(),
                                                                         150,
                                                                         300 );
        getControlPanel().add( energyLevelsMonitorPanel );

        // Add a button to show/hide the spectrometer
        final JCheckBox spectrometerCB = new JCheckBox( SimStrings.get( "ControlPanel.SpectrometerButtonLabel" ) );
        spectrometerCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                spectrometerGraphic.setVisible( spectrometerCB.isSelected() );
            }
        } );
        getControlPanel().add( spectrometerCB );
        spectrometerGraphic.setVisible( spectrometerCB.isSelected() );

        final JCheckBox squiggleCB = new JCheckBox( "Show squiggles" );
        squiggleCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                energyLevelsMonitorPanel.setSquigglesEnabled( squiggleCB.isSelected() );
            }
        } );
        getControlPanel().add( squiggleCB );
    }

    /**
     * Adds some atoms and their graphics
     *
     * @param tube
     * @param numAtoms
     */
    protected void addAtoms( ResonatingCavity tube, int numAtoms, int numEnergyLevels, double maxSpeed ) {
        DischargeLampAtom atom = null;
        ArrayList atoms = new ArrayList();
        Rectangle2D tubeBounds = tube.getBounds();

        AtomicState[] atomicStates = model.getAtomicStates();

        for( int i = 0; i < numAtoms; i++ ) {
            atom = new DischargeLampAtom( (LaserModel)getModel(), atomicStates );
            atom.setPosition( ( tubeBounds.getX() + ( Math.random() ) * ( tubeBounds.getWidth() - atom.getRadius() * 4 ) + atom.getRadius() * 2 ),
                              ( tubeBounds.getY() + ( Math.random() ) * ( tubeBounds.getHeight() - atom.getRadius() * 4 ) ) + atom.getRadius() * 2 );
            atom.setVelocity( (float)( Math.random() - 0.5 ) * maxSpeed,
                              (float)( Math.random() - 0.5 ) * maxSpeed );
            atoms.add( atom );
            addAtom( atom );
            atom.addPhotonEmittedListener( getSpectrometer() );
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
     * Returns a typed reference to the model
     */
    protected DischargeLampModel getDischargeLampModel () {
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
    protected Spectrometer getSpectrometer() {
        return spectrometer;
    }

    protected DischargeLampEnergyMonitorPanel2 getEneregyLevelsMonitorPanel() {
        return energyLevelsMonitorPanel;
    }
}
