/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
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

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.model.Electrode;
import edu.colorado.phet.dischargelamps.model.Electron;
import edu.colorado.phet.dischargelamps.model.ElectronSink;
import edu.colorado.phet.dischargelamps.model.ElectronSource;
import edu.colorado.phet.dischargelamps.view.ElectronGraphic;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedEvent;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedListener;
import edu.colorado.phet.lasers.view.BeamCurtainGraphic;
import edu.colorado.phet.lasers.view.LampGraphic;
import edu.colorado.phet.lasers.view.PhotonGraphic;
import edu.colorado.phet.lasers.view.ResonatingCavityGraphic;
import edu.colorado.phet.photoelectric.PhotoelectricConfig;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;
import edu.colorado.phet.photoelectric.model.PhotoelectricTarget;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

/**
 * PhotoelectricModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricModule extends BaseLaserModule {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    static private final int BEAM_VIEW = 1;
    static private final int PHOTON_VIEW = 2;

//    public static boolean DEBUG = true;
    public static boolean DEBUG = false;

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    private ElectronSink anode;
    private ElectronSource targetPlate;

    // The scale to apply to graphics created in external applications so they appear properly
    // on the screen
    private double externalGraphicsScale;
    // AffineTransformOp that will scale graphics created in external applications so they appear
    // properly on the screen
    private AffineTransformOp externalGraphicScaleOp;
    // An ElectronSink that absorbs electrons if they come back and hit the target
    // TODO: get this into the PhotoelectronTarget class
    private ElectronSink targetSink;
    private PhetImageGraphic cathodeGraphic;
    private BeamCurtainGraphic beamGraphic;
    private PhotonGraphicManager photonGraphicManager;

    private int viewType = BEAM_VIEW;


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

        // Set the default work function for the target
        model.getTarget().setWorkFunction( PhotoelectricTarget.workFunctions.get( PhotoelectricTarget.SODIUM ) );
        // Add the tube
        addTube( model, getApparatusPanel() );


        //----------------------------------------------------------------
        // View
        //----------------------------------------------------------------

        // Add a graphic for the beam
        CollimatedBeam beam = model.getBeam();
        beamGraphic = new BeamCurtainGraphic( getApparatusPanel(), beam );
        getApparatusPanel().addGraphic( beamGraphic );
        try {
            BufferedImage lampImg = ImageLoader.loadBufferedImage( PhotoelectricConfig.LAMP_IMAGE_FILE );
            // Make the lens on the lamp the same size as the beam
            AffineTransform scaleTx = AffineTransform.getScaleInstance( 100.0 / lampImg.getWidth(),
                                                                        beam.getWidth() / lampImg.getHeight() );
            AffineTransformOp scaleTxOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
            lampImg = scaleTxOp.filter( lampImg, null );

            Point2D rp = new Point2D.Double( lampImg.getWidth(), lampImg.getHeight() / 2 );
            AffineTransform atx = AffineTransform.getRotateInstance( beam.getAngle(), rp.getX(), rp.getY() );

            LampGraphic lampGraphic = new LampGraphic( beam, getApparatusPanel(), lampImg, atx );
            // todo: this is positioned with hard numbers. Fix it
            lampGraphic.setLocation( (int)beam.getPosition().getX() - 80, (int)beam.getPosition().getY() );
            getApparatusPanel().addGraphic( lampGraphic );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        // Add a listener that will place photons right next to the plate when we are in beam view mode
        beam.addPhotonEmittedListener( new PhotonPlacementManager() );

        // Add a listener that will produce photon graphics for the beam and take them away when the photons
        // leave the system
        photonGraphicManager = new PhotonGraphicManager();
        beam.addPhotonEmittedListener( photonGraphicManager );

        // Add a listener to the target plate that will create electron graphics when electrons
        // are produced, and remove them when they the electrons leave the system.
        PhotoelectricTarget target = model.getTarget();
        target.addListener( new ElectronGraphicManager() );

        // Add the battery and wire graphic
        addCircuitGraphic( apparatusPanel );

        // Add a graphic for the target plate
        addCathodeGraphic( model, apparatusPanel );

        // Add the anode to the model
        addAnode( model, apparatusPanel, targetPlate );

        // Set the targetPlate to listen for potential changes relative to the anode
        hookCathodeToAnode();

        //----------------------------------------------------------------
        // Controls
        //----------------------------------------------------------------

        // Set up the control panel
        addControls();

        //----------------------------------------------------------------
        // Debug
        //----------------------------------------------------------------

        // Draw red dots on the beam source location and the middle of the target plate
        if( DEBUG ) {
            PhetShapeGraphic beamIndicator = new PhetShapeGraphic( getApparatusPanel(),
                                                                   new Ellipse2D.Double( beam.getPosition().getX(),
                                                                                         beam.getPosition().getY(),
                                                                                         10, 10 ),
                                                                   Color.red );
            getApparatusPanel().addGraphic( beamIndicator, 10000 );
            PhetShapeGraphic cathodIndicator = new PhetShapeGraphic( getApparatusPanel(),
                                                                     new Ellipse2D.Double( targetPlate.getPosition().getX(),
                                                                                           targetPlate.getPosition().getY(),
                                                                                           10, 10 ),
                                                                     Color.red );
            getApparatusPanel().addGraphic( cathodIndicator, 10000 );
        }
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
        final CollimatedBeam beam = getPhotoelectricModel().getBeam();

        //----------------------------------------------------------------
        // Target controls
        //----------------------------------------------------------------

        JPanel targetControlPnl = new JPanel( new GridLayout( 1, 1 ) );
        targetControlPnl.setBorder( new TitledBorder( "Target" ) );
        getControlPanel().add( targetControlPnl );
        final JComboBox targetMaterial = new JComboBox( PhotoelectricTarget.workFunctions.keySet().toArray() );
        targetControlPnl.add( targetMaterial );
        final PhotoelectricTarget target = getPhotoelectricModel().getTarget();
        targetMaterial.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                target.setMaterial( targetMaterial.getSelectedItem() );
                BufferedImage bImg = cathodeGraphic.getImage();
                HashMap lut = new HashMap();
                lut.put( PhotoelectricTarget.COPPER, new Color( 200, 220, 100 ) );
                lut.put( PhotoelectricTarget.MAGNESIUM, new Color( 100, 220, 100 ) );
                lut.put( PhotoelectricTarget.SODIUM, new Color( 240, 240, 50 ) );
                lut.put( PhotoelectricTarget.ZINC, new Color( 240, 240, 240 ) );
                MakeDuotoneImageOp imgOp = new MakeDuotoneImageOp( (Color)lut.get( targetMaterial.getSelectedItem() ) );
//                cathodeGraphic.setImage( imgOp.filter( bImg,null ));
            }
        } );
        target.setMaterial( targetMaterial.getSelectedItem() );

        //----------------------------------------------------------------
        // Beam controls
        //----------------------------------------------------------------

        JPanel beamControlPnl = new JPanel( new GridLayout( 2, 1 ) );
        beamControlPnl.setBorder( new TitledBorder( "Lamp" ) );
        getControlPanel().add( beamControlPnl );

        // A slider for the wavelength
        final ModelSlider wavelengthSlider = new ModelSlider( SimStrings.get( "Control.Wavelength" ), "nm",
                                                              LaserConfig.MIN_WAVELENGTH / 3, LaserConfig.MAX_WAVELENGTH,
                                                              ( LaserConfig.MIN_WAVELENGTH + LaserConfig.MAX_WAVELENGTH ) / 2 );
        wavelengthSlider.setPreferredSize( new Dimension( 250, 100 ) );
        beam.setWavelength( wavelengthSlider.getValue() );
        beamControlPnl.add( wavelengthSlider );
        wavelengthSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                beam.setWavelength( wavelengthSlider.getValue() );
            }
        } );

        // A slider for the beam intensity
        final ModelSlider beamIntensitySlider = new ModelSlider( SimStrings.get( "Intensity" ), "",
                                                                 0, beam.getMaxPhotonsPerSecond(),
                                                                 beam.getMaxPhotonsPerSecond() / 2 );
        beamIntensitySlider.setPreferredSize( new Dimension( 250, 100 ) );
        beam.setPhotonsPerSecond( beamIntensitySlider.getValue() / 2 );
        beamControlPnl.add( beamIntensitySlider );
        beamIntensitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                beam.setPhotonsPerSecond( beamIntensitySlider.getValue() );
            }
        } );

        //----------------------------------------------------------------
        // Battery controls
        //----------------------------------------------------------------

        // A slider for the battery voltage
        final ModelSlider batterySlider = new ModelSlider( SimStrings.get( "Control.BatteryVoltageLabel" ),
                                                           "V", -0.05, 0.05, 0 );
        batterySlider.setPreferredSize( new Dimension( 250, 100 ) );
        ControlPanel controlPanel = (ControlPanel)getControlPanel();
        controlPanel.add( batterySlider );
        targetPlate.setPotential( batterySlider.getValue() );
        batterySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                targetPlate.setPotential( batterySlider.getValue() );
                anode.setPotential( 0 );
            }
        } );
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
     * Creates a listener that manages the production rate of the targetPlate based on its potential
     * relative to the anode
     */
    private void hookCathodeToAnode() {
        anode.addStateChangeListener( new Electrode.StateChangeListener() {
            public void stateChanged( Electrode.StateChangeEvent event ) {
                double anodePotential = event.getElectrode().getPotential();
                targetPlate.setSinkPotential( anodePotential );
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
        targetPlate = model.getTarget();
        cathodeGraphic = new PhetImageGraphic( getApparatusPanel(), "images/electrode-2.png" );

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
// State/mode setters
//----------------------------------------------------------------

    /**
     * Toggles the view of the light between beam view and photon view
     *
     * @param isEnabled
     */
    public void setPhotonViewEnabled( boolean isEnabled ) {
        photonGraphicManager.setEnabled( isEnabled );
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
     * Modifies the initial placement of photons to be very near the target when we're in
     * beam view. This prevents the delay in response of the target when the wavelength or
     * intensity of the beam is changed.
     */
    private class PhotonPlacementManager implements PhotonEmittedListener {
        public void photonEmittedEventOccurred( PhotonEmittedEvent event ) {
            if( viewType == BEAM_VIEW ) {
                Photon photon = event.getPhoton();
                Line2D photonPath = new Line2D.Double( photon.getPosition().getX(),
                                                       photon.getPosition().getY(),
                                                       photon.getPosition().getX() + photon.getVelocity().getX(),
                                                       photon.getPosition().getY() + photon.getVelocity().getY() );
                Point2D p = MathUtil.getLinesIntersection( photonPath.getP1(), photonPath.getP2(),
                                                           targetPlate.getEndpoints()[0], targetPlate.getEndpoints()[1] );
                photon.setPosition( p.getX() - photon.getVelocity().getX(),
                                      p.getY() - photon.getVelocity().getY() );
            }
        }
    }

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
        private boolean isEnabled;

        public void setEnabled( boolean enabled ) {
            isEnabled = enabled;

            // Set the view state for the entire module
            viewType = isEnabled ? PHOTON_VIEW : BEAM_VIEW;
        }

        public void photonEmittedEventOccurred( PhotonEmittedEvent event ) {
            if( isEnabled ) {
                Photon photon = event.getPhoton();
                final PhotonGraphic pg = PhotonGraphic.getInstance( getApparatusPanel(), photon );
                getApparatusPanel().addGraphic( pg );

                photon.addLeftSystemListener( new Photon.LeftSystemEventListener() {
                    public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
                        getApparatusPanel().removeGraphic( pg );
                    }
                } );
            }
            beamGraphic.setVisible( !isEnabled );
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
