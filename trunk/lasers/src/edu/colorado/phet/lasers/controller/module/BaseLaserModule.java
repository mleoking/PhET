/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.lasers.controller.module;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.help.HelpManager;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.lasers.controller.Kaboom;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.RightMirrorReflectivityControlPanel;
import edu.colorado.phet.lasers.help.ApparatusPanelHelp;
import edu.colorado.phet.lasers.help.EnergyLevelPanelHelp;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.mirror.BandPass;
import edu.colorado.phet.lasers.model.mirror.PartialMirror;
import edu.colorado.phet.lasers.model.photon.*;
import edu.colorado.phet.lasers.view.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * 
 */
public class BaseLaserModule extends Module {

    static protected final Point2D s_origin = LaserConfig.ORIGIN;
    static protected final double s_boxHeight = 120;
    static protected final double s_boxWidth = 300;
    static protected final double s_laserOffsetX = 50;

    static public final int PHOTON_DISCRETE = 0;
    static public final int PHOTON_WAVE = 1;
    static public final int PHOTON_CURTAIN = 2;

    private ResonatingCavity cavity;
    private Point2D laserOrigin;
    private LaserModel laserModel;
    private PartialMirror rightMirror;
    private PartialMirror leftMirror;
    private MirrorGraphic rightMirrorGraphic;
    private MirrorGraphic leftMirrorGraphic;
    private PhetFrame appFrame;
    private LaserEnergyLevelMonitorPanel laserEnergyLevelsMonitorPanel;
    private Beam seedBeam;
    private Beam pumpingBeam;
    private JPanel reflectivityControlPanel;
    private int pumpingPhotonView;
    private int lasingPhotonView = PHOTON_DISCRETE;
    private BeamCurtainGraphic beamCurtainGraphic;
    private LaserWaveGraphic waveGraphic;
    private LampGraphic pumpLampGraphic;

    private int numPhotons;
    private boolean displayHighLevelEmissions;
    protected boolean threeEnergyLevels;
    private boolean mirrorsEnabled;

    private double middleStateMeanLifetime = LaserConfig.MAXIMUM_STATE_LIFETIME / 2;
    private double highStateMeanLifetime = LaserConfig.MAXIMUM_STATE_LIFETIME / 2;
//    private double middleStateMeanLifetime = LaserConfig.MIDDLE_ENERGY_STATE_MAX_LIFETIME;
//    private double highStateMeanLifetime = LaserConfig.HIGH_ENERGY_STATE_DEFAULT_LIFETIME;
    private HelpManager energyLevelsPanelHelpManager;
    private HelpManager mainPanelHelpManager;
    private ApparatusPanelHelp apparatusPanelHelp;
    private Kaboom kaboom;

    /**
     *
     */
    public BaseLaserModule( String title, AbstractClock clock ) {
        super( title, clock );
        init();
    }

    private void init() {
        // Create the model
        laserModel = new LaserModel();
        setModel( laserModel );
        laserModel.setBounds( new Rectangle2D.Double( 0, 0, 800, 600 ) );

        // Create the apparatus panel
        final ApparatusPanel2 apparatusPanel = new ApparatusPanel2( getClock() );
        apparatusPanel.setUseOffscreenBuffer( true );
        setApparatusPanel( apparatusPanel );
        apparatusPanel.setBackground( Color.white );

        // Add a kaboom element
        kaboom = new Kaboom( this );
        getModel().addModelElement( kaboom );

        // Add the laser cavity and its graphic
        createCavity();

        // Create the pumping and stimulating beams, and their graphics
        createBeams();

        // Create the energy levels dialog
        createEnergyLevelsDialog( getClock(), null );

        // Create the mirrors
        createMirrors();

        // Set up help
        createHelp();
    }

    /**
     * @param app
     */
    public void activate( PhetApplication app ) {
        super.activate( app );
        LaserPhoton.setStimulationBounds( cavity.getBounds() );
        appFrame = app.getPhetFrame();
        // Needed to make the energy levels panel get its model-view transform right
        laserEnergyLevelsMonitorPanel.adjustPanel();
        getLaserModel().getMiddleEnergyState().setMeanLifetime( middleStateMeanLifetime );
        getLaserModel().getHighEnergyState().setMeanLifetime( highStateMeanLifetime );
    }

    /**
     * @param app
     */
    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        middleStateMeanLifetime = getLaserModel().getMiddleEnergyState().getMeanLifeTime();
        highStateMeanLifetime = getLaserModel().getHighEnergyState().getMeanLifeTime();
    }

    //----------------------------------------------------------------
    // Create model elements and their associated graphics
    //----------------------------------------------------------------

    /**
     * Sets up the energy levels dialog
     *
     * @param clock
     * @param frame
     */
    protected void createEnergyLevelsDialog( AbstractClock clock, PhetFrame frame ) {
        laserEnergyLevelsMonitorPanel = new LaserEnergyLevelMonitorPanel( this, clock );
    }

    /**
     * Sets up the laser cavity
     */
    private void createCavity() {
        laserOrigin = new Point2D.Double( s_origin.getX() + s_laserOffsetX,
                                          s_origin.getY() );
        cavity = new ResonatingCavity( laserOrigin, s_boxWidth, s_boxHeight );
        getModel().addModelElement( cavity );
        ResonatingCavityGraphic cavityCavityGraphic = new ResonatingCavityGraphic( getApparatusPanel(), cavity );
        addGraphic( cavityCavityGraphic, LaserConfig.CAVITY_LAYER );
    }

    /**
     * Sets up the pumping and seed beams
     */
    private void createBeams() {
        seedBeam = new Beam( Photon.RED,
                             s_origin,
                             s_boxWidth + s_laserOffsetX * 2,
                             s_boxHeight - Photon.RADIUS,
                             new Vector2D.Double( 1, 0 ),
                             LaserConfig.MAXIMUM_SEED_PHOTON_RATE,
                             LaserConfig.SEED_BEAM_FANOUT );
        seedBeam.addPhotonEmittedListener( new InternalPhotonEmittedListener() );
        seedBeam.setEnabled( true );
        getLaserModel().setStimulatingBeam( seedBeam );

        pumpingBeam = new Beam( Photon.BLUE,
                                new Point2D.Double( s_origin.getX() + s_laserOffsetX, s_origin.getY() - s_laserOffsetX ),
                                1000,
                                cavity.getWidth(),
                                new Vector2D.Double( 0, 1 ),
                                LaserConfig.MAXIMUM_SEED_PHOTON_RATE,
                                LaserConfig.PUMPING_BEAM_FANOUT );
        pumpingBeam.addPhotonEmittedListener( new InternalPhotonEmittedListener() );
        pumpingBeam.setEnabled( true );
        getLaserModel().setPumpingBeam( pumpingBeam );
    }

    /**
     * Creates mirrors and their associated graphics
     */
    protected void createMirrors() {

        // If there already mirrors in the model, get rid of them
        if( rightMirror != null ) {
            getModel().removeModelElement( rightMirror );
        }
        if( leftMirror != null ) {
            getModel().removeModelElement( leftMirror );
        }

        // The right mirror is a partial mirror
        Point2D p1 = new Point2D.Double( cavity.getPosition().getX() + cavity.getWidth(),
                                         cavity.getPosition().getY() );
        Point2D p2 = new Point2D.Double( cavity.getPosition().getX() + cavity.getWidth(),
                                         cavity.getPosition().getY() + cavity.getHeight() );
        rightMirror = new PartialMirror( p1, p2 );
        BandPass bandPass = new BandPass( LaserConfig.MIN_WAVELENGTH, LaserConfig.MAX_WAVELENGTH );
        rightMirror.addReflectionStrategy( bandPass );

//        rightMirror.addReflectionStrategy( new LeftReflecting() );
        rightMirrorGraphic = new MirrorGraphic( getApparatusPanel(), rightMirror, MirrorGraphic.LEFT_FACING );
        // The left mirror is 100% reflecting
        Point2D p3 = new Point2D.Double( cavity.getPosition().getX(),
                                         cavity.getPosition().getY() );
        Point2D p4 = new Point2D.Double( cavity.getPosition().getX(),
                                         cavity.getPosition().getY() + cavity.getHeight() );
        leftMirror = new PartialMirror( p3, p4 );
        leftMirror.addReflectionStrategy( bandPass );
        leftMirror.setReflectivity( 1.0 );
        leftMirrorGraphic = new MirrorGraphic( getApparatusPanel(), leftMirror, MirrorGraphic.RIGHT_FACING );

        // Put a reflectivity control on the panel
        JPanel reflectivityControl = new RightMirrorReflectivityControlPanel( rightMirror );
        reflectivityControlPanel = new JPanel();
        Dimension dim = reflectivityControl.getPreferredSize();
        reflectivityControlPanel.setBounds( (int)rightMirror.getPosition().getX(),
                                            (int)( rightMirror.getPosition().getY() + rightMirror.getBounds().getHeight() ),
                                            (int)dim.getWidth() + 10, (int)dim.getHeight() + 10 );
        reflectivityControlPanel.add( reflectivityControl );
        reflectivityControl.setBorder( new BevelBorder( BevelBorder.RAISED ) );
        reflectivityControlPanel.setOpaque( false );
        reflectivityControlPanel.setVisible( false );
        getApparatusPanel().add( reflectivityControlPanel );

        // Add the graphics for lasing
        addLasingGraphics();
    }

    private void addLasingGraphics() {
        double internalLaserCurtainOpacity = .7;
        final double externalLaserCurtainOpacity = .7;
        Rectangle cavityBounds = new Rectangle( (int)cavity.getBounds().getX(), (int)cavity.getBounds().getY(),
                                                (int)cavity.getBounds().getWidth(), (int)cavity.getBounds().getHeight() );
        Shape mirrorFace = new Ellipse2D.Double( cavityBounds.getMaxX() - LaserConfig.MIRROR_THICKNESS / 2,
                                                 cavityBounds.getMinY(),
                                                 LaserConfig.MIRROR_THICKNESS,
                                                 cavity.getBounds().getHeight() );
        Area a = new Area( cavityBounds );
        a.add( new Area( mirrorFace ) );
        LaserCurtainGraphic internalLaserCurtainGraphic = new LaserCurtainGraphic( getApparatusPanel(),
                                                                                   a, laserModel,
                                                                                   new AtomicState[]{
                                                                                       getLaserModel().getGroundState(),
                                                                                       getLaserModel().getMiddleEnergyState()
                                                                                   },
                                                                                   internalLaserCurtainOpacity );
        laserModel.addLaserListener( internalLaserCurtainGraphic );
        addGraphic( internalLaserCurtainGraphic, LaserConfig.LEFT_MIRROR_LAYER - 1 );

        // TODO: put this on a listener that responds to apparatus panel resizings, rather than using a hard-coded number
        Rectangle externalBounds = new Rectangle( (int)cavity.getBounds().getMaxX(), (int)cavity.getBounds().getY(),
                                                  500,
                                                  (int)cavity.getBounds().getHeight() );
        final LaserCurtainGraphic externalLaserCurtainGraphic = new LaserCurtainGraphic( getApparatusPanel(),
                                                                                         externalBounds, laserModel,
                                                                                         new AtomicState[]{
                                                                                             getLaserModel().getGroundState(),
                                                                                             getLaserModel().getMiddleEnergyState()
                                                                                         },
                                                                                         externalLaserCurtainOpacity );
        laserModel.addLaserListener( externalLaserCurtainGraphic );
        addGraphic( externalLaserCurtainGraphic, LaserConfig.RIGHT_MIRROR_LAYER - 1 );

        // Create a listener that will adjust the maximum alpha of the external beam based on the reflectivity
        // of the right-hand mirror
        rightMirror.addListener( new PartialMirror.Listener() {
            public void reflectivityChanged( PartialMirror.ReflectivityChangedEvent event ) {
                externalLaserCurtainGraphic.setMaxAlpha( 1 - ( Math.pow( event.getReflectivity(), 1.5 ) ) );
            }
        } );
    }

    //----------------------------------------------------------------
    // Help-related methods
    //----------------------------------------------------------------

    public boolean hasHelp() {
        return true;
    }

    private void createHelp() {
        mainPanelHelpManager = new HelpManager( getApparatusPanel() );
        apparatusPanelHelp = new ApparatusPanelHelp( this );
//        new ApparatusPanelHelp( mainPanelHelpManager );
        energyLevelsPanelHelpManager = new HelpManager( getEnergyLevelsMonitorPanel() );
        new EnergyLevelPanelHelp( energyLevelsPanelHelpManager );
    }

    public void setHelpEnabled( boolean h ) {
        super.setHelpEnabled( h );
//        mainPanelHelpManager.setHelpEnabled( getApparatusPanel(), h );
//        apparatusPanelHelp.setHelpEnabled( getApparatusPanel(), h );
        energyLevelsPanelHelpManager.setHelpEnabled( getEnergyLevelsMonitorPanel(), h );
    }


    //-----------------------------------------------------------------------------
    // Getter and setters
    //-----------------------------------------------------------------------------
    public int getLasingPhotonView() {
        return lasingPhotonView;
    }

    public void setLasingPhotonView( int lasingPhotonView ) {
        this.lasingPhotonView = lasingPhotonView;
        switch( lasingPhotonView ) {
            case PHOTON_DISCRETE:
                if( waveGraphic != null ) {
                    waveGraphic.setVisible( false );
                }
                break;
            case PHOTON_WAVE:
                AtomicState[] states = new AtomicState[]{getLaserModel().getGroundState(),
                                                         getLaserModel().getMiddleEnergyState()};
                if( waveGraphic == null ) {
                    waveGraphic = new LaserWaveGraphic( getApparatusPanel(), getCavity(),
                                                        rightMirror, this, states );
                }
                waveGraphic.setVisible( true );
                break;
            default :
                throw new RuntimeException( "Invalid parameter value" );
        }
    }

    public void setPumpingPhotonView( int pumpingPhotonView ) {
        this.pumpingPhotonView = pumpingPhotonView;
        switch( pumpingPhotonView ) {
            case PHOTON_DISCRETE:
                getApparatusPanel().removeGraphic( beamCurtainGraphic );
                PhotonGraphic.setAllVisible( true, getPumpingBeam().getWavelength() );
//                pumpingBeam.setFanout( pumpingBeamFanout * 0.5 );
                break;
            case PHOTON_CURTAIN:
                if( beamCurtainGraphic == null ) {
                    beamCurtainGraphic = new BeamCurtainGraphic( getApparatusPanel(), pumpingBeam );
                }
                addGraphic( beamCurtainGraphic, 1 );
                PhotonGraphic.setAllVisible( false, getPumpingBeam().getWavelength() );
//                pumpingBeam.setFanout( pumpingBeamFanout );
                break;
            default :
                throw new RuntimeException( "Invalid parameter value" );
        }
    }

    public int getNumPhotons() {
        return numPhotons;
    }

    protected Point2D getLaserOrigin() {
        return laserOrigin;
    }

    public ResonatingCavity getCavity() {
        return cavity;
    }

    public LaserModel getLaserModel() {
        return (LaserModel)getModel();
    }

    public PartialMirror getRightMirror() {
        return rightMirror;
    }

    public boolean isMirrorsEnabled() {
        return mirrorsEnabled;
    }

    public Beam getPumpingBeam() {
        return pumpingBeam;
    }

    protected PhetGraphic getBeamCurtainGraphic() {
        return beamCurtainGraphic;
    }

    public LaserEnergyLevelMonitorPanel getEnergyLevelsMonitorPanel() {
        return laserEnergyLevelsMonitorPanel;
    }

    public void setEnergyLevelsAveragingPeriod( double value ) {
        laserEnergyLevelsMonitorPanel.setAveragingPeriod( (long)value );
    }

    public double getEnerglyLevelsAveragingPeriod() {
        return laserEnergyLevelsMonitorPanel.getAveragingPeriod();
    }

    public void setDisplayHighLevelEmissions( boolean display ) {
        displayHighLevelEmissions = display;
    }

    public boolean getThreeEnergyLevels() {
        return threeEnergyLevels;
    }

    public void setThreeEnergyLevels( boolean threeEnergyLevels ) {
        int numLevels = threeEnergyLevels ? 3 : 2;
        laserModel.setNumEnergyLevels( numLevels );
        this.threeEnergyLevels = threeEnergyLevels;
        getEnergyLevelsMonitorPanel().setNumLevels( numLevels );
    }

    /**
     * Enables or disables mirrors. Also does the initial creation of the mirrors
     *
     * @param mirrorsEnabled
     */
    public void setMirrorsEnabled( boolean mirrorsEnabled ) {
        this.mirrorsEnabled = mirrorsEnabled;

        // Regardless of the value of mirrorsEnabled, we should remove the
        // model elements and graphics for the mirrors. If mirrorsEnabled is
        // true, we want to try remove them first, so they don't get added
        // twice if they were already there
        getModel().removeModelElement( leftMirror );
        getModel().removeModelElement( rightMirror );
        getApparatusPanel().removeGraphic( leftMirrorGraphic );
        getApparatusPanel().removeGraphic( rightMirrorGraphic );

        // Show/hide the reflectivity slider
        reflectivityControlPanel.setVisible( mirrorsEnabled );

        if( mirrorsEnabled ) {
            getModel().addModelElement( leftMirror );
            getModel().addModelElement( rightMirror );
            getApparatusPanel().addGraphic( leftMirrorGraphic, LaserConfig.LEFT_MIRROR_LAYER );
            getApparatusPanel().addGraphic( rightMirrorGraphic, LaserConfig.RIGHT_MIRROR_LAYER );
            getApparatusPanel().revalidate();
        }
        seedBeam.setEnabled( !mirrorsEnabled );

        getApparatusPanel().paintImmediately( getApparatusPanel().getBounds() );
    }

    protected void setPumpLampGraphic( LampGraphic lampGraphic ) {
        pumpLampGraphic = lampGraphic;
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // Other methods
    //
    protected AtomGraphic addAtom( Atom atom ) {
        getModel().addModelElement( atom );
        AtomGraphic atomGraphic = new AnnotatedAtomGraphic( getApparatusPanel(), atom );
        addGraphic( atomGraphic, LaserConfig.ATOM_LAYER );

        // Add a listener to the atom that will create a photon graphic if the atom
        // emits a photon, and another to deal with an atom leaving the system
        atom.addPhotonEmittedListener( new InternalPhotonEmittedListener() );
        atom.addLeftSystemListener( new AtomRemovalListener( atomGraphic ) );

        return atomGraphic;
    }

    protected void removeAtom( Atom atom ) {
        getModel().removeModelElement( atom );
        atom.removeFromSystem();
    }

    public void setSwingComponentsVisible( boolean areVisible ) {
        if( reflectivityControlPanel != null ) {
            if( !areVisible ) {
                reflectivityControlPanel.setVisible( areVisible );
            }
            else {
                reflectivityControlPanel.setVisible( this.mirrorsEnabled );
            }
        }
    }

    public LampGraphic getPumpLampGraphic() {
        return pumpLampGraphic;
    }

    /**
     *
     */
    public void reset() {
        // Reset the model
        laserModel.reset();

        // Reset the mirror's reflectivity
        if( rightMirror != null ) {
            rightMirror.setReflectivity( 1.0 );
        }

        // Reset the wavelengths of the beams
        seedBeam.setWavelength( Photon.RED );
        pumpingBeam.setWavelength( Photon.BLUE );

        setPumpingPhotonView( PHOTON_CURTAIN );

        // Reset the state lifetimes
        laserEnergyLevelsMonitorPanel.reset();

        // Clear the old kaboom stuff off the apparatus panel and out of the model
        getModel().removeModelElement( kaboom );
        kaboom.clearGraphics( getApparatusPanel() );

        // Make a new kaboom, ready for firing
        kaboom = new Kaboom( this );
        getModel().addModelElement( kaboom );
    }


    //-------------------------------------------------------------------------------------------------
    // Event handling
    //-------------------------------------------------------------------------------------------------

    public class InternalPhotonEmittedListener implements PhotonEmittedListener {

        public void photonEmittedEventOccurred( PhotonEmittedEvent event ) {

            // Track the number of photons
            BaseLaserModule.this.numPhotons++;
            Photon photon = event.getPhoton();
            getModel().addModelElement( photon );
            boolean isPhotonGraphicVisible = true;

            // Determine if we should display a photon graphic, or some other representations
            Object source = event.getSource();

            // Was the photon emitted by an atom?
            if( source instanceof Atom ) {
                Atom atom = (Atom)source;
                if( atom.getStates().length > 2 && atom.getCurrState() == atom.getStates()[2]
                    && !displayHighLevelEmissions ) {
                    isPhotonGraphicVisible = false;
                }
                else if( photon.getEnergy() == laserModel.getMiddleEnergyState().getEnergyLevel() -
                                               laserModel.getGroundState().getEnergyLevel() ) {
                    isPhotonGraphicVisible = lasingPhotonView == PHOTON_DISCRETE;
                }
            }

            // Is it a photon from the seed beam?
            if( source == seedBeam ) {
                isPhotonGraphicVisible = true;
            }

            // Is it a pumping beam photon, and are we viewing discrete photons?
            if( source == pumpingBeam ) {
                isPhotonGraphicVisible = ( pumpingPhotonView == PHOTON_DISCRETE );
            }

            // Create a photon graphic, add it to the appratus panel and attach a
            // listener to the photon that will remove the graphic if and when the
            // photon goes away. Set it's visibility based on the state of the simulation
            PhotonGraphic pg = PhotonGraphic.getInstance( getApparatusPanel(), photon );
            pg.setVisible( isPhotonGraphicVisible );
            addGraphic( pg, LaserConfig.PHOTON_LAYER );
            photon.addLeftSystemListener( new PhotonLeftSystemListener( photon, pg ) );
        }
    }

    /**
     * Handles cleanup when an atom is removed from the system
     */
    public class AtomRemovalListener implements Atom.LeftSystemListener {
        private AtomGraphic atomGraphic;

        public AtomRemovalListener( AtomGraphic atomGraphic ) {
            this.atomGraphic = atomGraphic;
        }

        public void leftSystem( Atom.LeftSystemEvent leftSystemEvent ) {
            getApparatusPanel().removeGraphic( atomGraphic );
        }
    }

    /**
     * Handles cleanup when a photon leaves the system. Takes care of removing the photon's
     * associated graphic
     */
    public class PhotonLeftSystemListener implements Photon.LeftSystemEventListener {
        private PhotonGraphic graphic;

        public PhotonLeftSystemListener( Photon photon, PhotonGraphic graphic ) {
            this.graphic = graphic;
        }

        public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
            // Track number of photons
            BaseLaserModule.this.numPhotons--;

            // Get rid of the graphic
            getApparatusPanel().removeGraphic( graphic );
            getApparatusPanel().repaint( graphic.getBounds() );
            graphic = null;
        }
    }
}
