/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.module;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.model.*;
import edu.colorado.phet.dischargelamps.view.DischargeLampEnergyLevelMonitorPanel;
import edu.colorado.phet.dischargelamps.view.ElectronGraphic;
import edu.colorado.phet.dischargelamps.view.SpectrometerGraphic;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedEvent;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedListener;
import edu.colorado.phet.lasers.view.PhotonGraphic;
import edu.colorado.phet.lasers.view.ResonatingCavityGraphic;
import edu.colorado.phet.lasers.view.BeamCurtainGraphic;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;
import edu.colorado.phet.photoelectric.model.PhotoelectricTarget;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * DischargeLampModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricModule extends BaseLaserModule {

//    public static boolean DEBUG = true;
    public static boolean DEBUG = false;

    private ElectronSink anode;
    private ElectronSource cathode;
    private DischargeLampEnergyLevelMonitorPanel elmp;

    // The scale to apply to graphics created in external applications so they appear properly
    // on the screen
    private double externalGraphicsScale;
    // AffineTransformOp that will scale graphics created in external applications so they appear
    // properly on the screen
    private AffineTransformOp externalGraphicScaleOp;
    private ModelSlider currentSlider;
    private static final double SPECTROMETER_LAYER = 1000;
    private Spectrometer spectrometer;
    // The states in which the atoms can be
    private SpectrometerGraphic spectrometerGraphic;
    private ElectronSink targetSink;

    /**
     * Constructor
     *
     * @param clock
     */
    public PhotoelectricModule( AbstractClock clock ) {
        super( "Photoelectric Effect", clock );

        // Set up the basic stuff
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setPaintStrategy( ApparatusPanel2.OFFSCREEN_BUFFER_STRATEGY );
        apparatusPanel.setBackground( Color.white );
        setApparatusPanel( apparatusPanel );

        //----------------------------------------------------------------
        // Model
        //----------------------------------------------------------------

        // Set up the model
        PhotoelectricModel model = new PhotoelectricModel();
        setModel( model );
        setControlPanel( new ControlPanel( this ) );
        model.getTarget().addListener( new CathodeListener() );

        // Add an electron sink in the same place as the target plate, to absorb electrons
        // TODO: refactor this into the model. Prefereably into the PhotoelectricPlate or Electrode class
        {
            PhotoelectricTarget target = model.getTarget();
            targetSink = new ElectronSink( model, target.getEndpoints()[0], target.getEndpoints()[1] );
            model.addModelElement( targetSink );
            target.addListener( targetSink );
        }

        //----------------------------------------------------------------
        // View
        //----------------------------------------------------------------

        // Add a graphic for the beam
        CollimatedBeam beam = model.getBeam();
        PhetShapeGraphic beamIndicator = new PhetShapeGraphic( getApparatusPanel(),
                                                               new Ellipse2D.Double( beam.getPosition().getX(),
                                                                                     beam.getPosition().getY(),
                                                                                     10, 10 ),
                                                               Color.red );
        getApparatusPanel().addGraphic( beamIndicator, 10000 );

        BeamCurtainGraphic beamCurtainGraphic = new BeamCurtainGraphic( getApparatusPanel(), beam );
        getApparatusPanel().addGraphic( beamCurtainGraphic );

        // Add a listener that will produce photon graphics for the beam, and for each photon emitted,
        // add a listener that will remove the PhotonGraphic from the apparatus panel
        beam.addPhotonEmittedListener( new PhotonGraphicManager() );

        // Add a listener to the target plate that will create electron graphics when electrons
        // are produced, and remove them when they the electrons leave the system.
        PhotoelectricTarget target = model.getTarget();
        target.addListener( new ElectronGraphicManager() );

        // Add the battery and wire graphic
        addCircuitGraphic( apparatusPanel );

        // Add a graphic for the target plate
        addCathodeGraphic( model, apparatusPanel );

        // Add the anode to the model
        addAnode( model, apparatusPanel, cathode );

        // Set the cathode to listen for potential changes relative to the anode
        hookCathodeToAnode();

        // Add the tube
        addTube( model, apparatusPanel );


        //----------------------------------------------------------------
        // Controls
        //----------------------------------------------------------------

        // Set up the control panel
        addControls();
    }

    /**
     * Returns a typed reference to the model
     */
    private PhotoelectricModel getPhotoelectricModel() {
        return (PhotoelectricModel)getModel();
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
     * @param apparatusPanel
     */
    private void addCircuitGraphic( ApparatusPanel apparatusPanel ) {
        PhetImageGraphic circuitGraphic = new PhetImageGraphic( getApparatusPanel(), "images/battery-w-wires-2.png" );
        AffineTransform flipVertical = AffineTransform.getScaleInstance( 1, -1 );
        flipVertical.translate( 0, -circuitGraphic.getImage().getHeight() );
        AffineTransformOp flipVerticalOp = new AffineTransformOp( flipVertical, AffineTransformOp.TYPE_BILINEAR );
        BufferedImage flippedImg = flipVerticalOp.filter( circuitGraphic.getImage(), null );
        circuitGraphic.setImage( flippedImg );

        scaleImageGraphic( circuitGraphic );
        circuitGraphic.setRegistrationPoint( (int)( 124 * externalGraphicsScale ),
                                             (int)( 110 * externalGraphicsScale ) );
        circuitGraphic.setLocation( DischargeLampsConfig.CATHODE_LOCATION );
        apparatusPanel.addGraphic( circuitGraphic, DischargeLampsConfig.CIRCUIT_LAYER );
    }

    /**
     * Sets up the control panel
     */
    private void addControls() {

        // A slider for the wavelength
        final ModelSlider wavelengthSlider = new ModelSlider( SimStrings.get( "Control.Wavelength" ), "nm",
                                                              LaserConfig.MIN_WAVELENGTH, LaserConfig.MAX_WAVELENGTH,
                                                              ( LaserConfig.MIN_WAVELENGTH + LaserConfig.MAX_WAVELENGTH ) / 2 );
        wavelengthSlider.setPreferredSize( new Dimension( 250, 100 ) );
        ( (ControlPanel)getControlPanel() ).add( wavelengthSlider );
        wavelengthSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                getPhotoelectricModel().getBeam().setWavelength( wavelengthSlider.getValue() );
            }
        } );
        getPhotoelectricModel().getBeam().setWavelength( wavelengthSlider.getValue() );

        // A slider for the battery voltage
        final ModelSlider batterySlider = new ModelSlider( SimStrings.get( "Control.BatteryVoltageLabel" ),
                                                           "V", -0.05, 0.05, 0 );
        batterySlider.setPreferredSize( new Dimension( 250, 100 ) );
        ControlPanel controlPanel = (ControlPanel)getControlPanel();
        controlPanel.add( batterySlider );
        batterySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                cathode.setPotential( batterySlider.getValue() );
                anode.setPotential( 0 );
            }
        } );
        cathode.setPotential( batterySlider.getValue() );
    }

    /**
     * Creates the tube, adds it to the model and creates a graphic for it
     *
     * @param model
     * @param apparatusPanel
     */
    private void addTube( PhotoelectricModel model, ApparatusPanel apparatusPanel ) {
        double x = DischargeLampsConfig.CATHODE_LOCATION.getX() - DischargeLampsConfig.ELECTRODE_INSETS.left;
        double y = DischargeLampsConfig.CATHODE_LOCATION.getY() - DischargeLampsConfig.CATHODE_LENGTH / 2
                   - DischargeLampsConfig.ELECTRODE_INSETS.top;
        double length = DischargeLampsConfig.ANODE_LOCATION.getX() - DischargeLampsConfig.CATHODE_LOCATION.getX()
                        + DischargeLampsConfig.ELECTRODE_INSETS.left + DischargeLampsConfig.ELECTRODE_INSETS.right;
        double height = DischargeLampsConfig.CATHODE_LENGTH
                        + DischargeLampsConfig.ELECTRODE_INSETS.top + DischargeLampsConfig.ELECTRODE_INSETS.bottom;
        Point2D tubeLocation = new Point2D.Double( x, y );
        ResonatingCavity tube = new ResonatingCavity( tubeLocation, length, height );
        model.addModelElement( tube );
        ResonatingCavityGraphic tubeGraphic = new ResonatingCavityGraphic( getApparatusPanel(), tube );
        apparatusPanel.addGraphic( tubeGraphic, DischargeLampsConfig.TUBE_LAYER );
    }

    /**
     * Creates a listener that manages the production rate of the cathode based on its potential
     * relative to the anode
     */
    private void hookCathodeToAnode() {
        anode.addStateChangeListener( new Electrode.StateChangeListener() {
            public void stateChanged( Electrode.StateChangeEvent event ) {
                double anodePotential = event.getElectrode().getPotential();
                cathode.setSinkPotential( anodePotential );
            }
        } );
    }

    /**
     * @param model
     * @param apparatusPanel
     * @param cathode
     */
    private void addAnode( PhotoelectricModel model, ApparatusPanel apparatusPanel, ElectronSource cathode ) {
        ElectronSink anode = new ElectronSink( model,
                                               DischargeLampsConfig.ANODE_LINE.getP1(),
                                               DischargeLampsConfig.ANODE_LINE.getP2() );
        model.addModelElement( anode );
        this.anode = anode;
        this.anode.setPosition( DischargeLampsConfig.ANODE_LOCATION );
        PhetImageGraphic anodeGraphic = new PhetImageGraphic( getApparatusPanel(), "images/electrode-2.png" );

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
        cathode.addListener( anode );
    }

    /**
     * @param model
     * @param apparatusPanel
     */
    private void addCathodeGraphic( PhotoelectricModel model, ApparatusPanel apparatusPanel ) {
        cathode = model.getTarget();
        PhetImageGraphic cathodeGraphic = new PhetImageGraphic( getApparatusPanel(), "images/electrode-2.png" );

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

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    /**
     * @return
     */
    protected ModelSlider getCurrentSlider() {
        return currentSlider;
    }

    //-----------------------------------------------------------------
    // Inner classes
    //-----------------------------------------------------------------

    /**
     * Listens for the absorption of an electron. When such an event happens,
     * its graphic is taken off the apparatus panel
     */
    private class AbsorptionElectronAbsorptionListener implements ElectronSink.ElectronAbsorptionListener {
        private Electron electron;
        private ElectronGraphic graphic;

        AbsorptionElectronAbsorptionListener( Electron electron, ElectronGraphic graphic ) {
            this.electron = electron;
            this.graphic = graphic;
        }

        public void electronAbsorbed( ElectronSink.ElectronAbsorptionEvent event ) {
            if( event.getElectron() == electron ) {
                getApparatusPanel().removeGraphic( graphic );
                anode.removeListener( this );
            }
        }
    }

    //----------------------------------------------------------------
    // Inner classes for event handling
    //----------------------------------------------------------------

    /**
     * Creates, adds and removes graphics for electrons
     */
    private class ElectronGraphicManager implements ElectronSource.ElectronProductionListener {
        public void electronProduced( ElectronSource.ElectronProductionEvent event ) {
            Electron electron = event.getElectron();
            final ElectronGraphic eg = new ElectronGraphic( getApparatusPanel(), electron );
            getApparatusPanel().addGraphic( eg );

            electron.addChangeListener( new Electron.ChangeListener() {
                public void leftSystem( Electron.ChangeEvent changeEvent ) {
                    getApparatusPanel().removeGraphic( eg );
                }

                public void energyChanged( Electron.ChangeEvent changeEvent ) {
                    // noop
                }
            } );
        }
    }

    /**
     * Creates, adds and removes graphics for photons
     */
    private class PhotonGraphicManager implements PhotonEmittedListener {
        public void photonEmittedEventOccurred( PhotonEmittedEvent event ) {
            Photon photon = event.getPhoton();
            final PhotonGraphic pg = PhotonGraphic.getInstance( getApparatusPanel(), photon );
            getApparatusPanel().addGraphic( pg );

            photon.addLeftSystemListener( new Photon.LeftSystemEventListener() {
                public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
                    getApparatusPanel().removeGraphic( pg );
                }
            } );
        }
    }

    private class CathodeListener implements ElectronSource.ElectronProductionListener {

        public void electronProduced( ElectronSource.ElectronProductionEvent event ) {
            Electron electron = event.getElectron();

            // Create a graphic for the electron
            ElectronGraphic graphic = new ElectronGraphic( getApparatusPanel(), electron );
            getApparatusPanel().addGraphic( graphic, DischargeLampsConfig.ELECTRON_LAYER );

            // Add a listener to the electron sinks that will remove the graphics when electrons
            // are absorbed
            AbsorptionElectronAbsorptionListener listener = new AbsorptionElectronAbsorptionListener( electron, graphic );
            anode.addListener( listener );
            targetSink.addListener( listener );
        }
    }
}
