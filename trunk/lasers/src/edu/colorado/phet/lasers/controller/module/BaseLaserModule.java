/* Copyright 2004, University of Colorado */

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
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.RightMirrorReflectivityControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.*;
import edu.colorado.phet.lasers.model.mirror.PartialMirror;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedEvent;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedListener;
import edu.colorado.phet.lasers.view.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Class: BaseLaserModule
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 * $Author$
 * $Date$
 * $Name$
 * $Revision$
 */
public class BaseLaserModule extends Module {

    static protected final Point2D s_origin = LaserConfig.ORIGIN;
    static protected final double s_boxHeight = 120;
    static protected final double s_boxWidth = 300;
    static protected final double s_laserOffsetX = 100;

    static public final int PHOTON_DISCRETE = 0;
    static public final int PHOTON_WAVE = 1;
    static public final int PHOTON_CURTAIN = 2;

    private ResonatingCavity cavity;
    private Point2D laserOrigin;
    private LaserModel laserModel;
    private EnergyLevelsDialog energyLevelsDialog;
    private PartialMirror rightMirror;
    private PartialMirror leftMirror;
    private MirrorGraphic rightMirrorGraphic;
    private MirrorGraphic leftMirrorGraphic;
    private Frame appFrame;
    private EnergyLevelMonitorPanel energyLevelsMonitorPanel;
    private CollimatedBeam seedBeam;
    private CollimatedBeam pumpingBeam;
    private JPanel reflectivityControlPanel;
    private int pumpingPhotonView;
    private int lasingPhotonView = PHOTON_DISCRETE;
    private WaveBeamGraphic beamGraphic;
    private StandingWaveGraphic waveGraphic;
    private int numPhotons;
    private boolean displayHighLevelEmissions;
    protected boolean threeEnergyLevels;


    /**
     *
     */
    public BaseLaserModule( String title, PhetFrame frame, AbstractClock clock ) {
        super( title );

        // Set the PhetFrame for the module
        appFrame = frame;

        // Create the model
        laserModel = new LaserModel();
        setModel( laserModel );
        laserModel.setBounds( new Rectangle2D.Double( 0, 0, 800, 600 ) );

        // Create the apparatus panel
        final ApparatusPanel2 apparatusPanel = new ApparatusPanel2( getModel(), clock );
        apparatusPanel.setUseOffscreenBuffer( true );
        setApparatusPanel( apparatusPanel );
        apparatusPanel.setBackground( Color.white );

        // Create the pumping and stimulating beams
        createBeams();

        // Add the laser cavity
        createCavity();

        // Create the energy levels dialog
        createEnergyLevelsDialog( clock, frame );

        // Add the control panel
//        LaserControlPanel controlPanel = new LaserControlPanel( this );
//        setControlPanel( controlPanel );
    }

    /**
     * Sets up the energy levels dialog
     *
     * @param clock
     * @param frame
     */
    private void createEnergyLevelsDialog( AbstractClock clock, PhetFrame frame ) {
        energyLevelsMonitorPanel = new EnergyLevelMonitorPanel( laserModel, clock );
        energyLevelsDialog = new EnergyLevelsDialog( appFrame, energyLevelsMonitorPanel );
        energyLevelsDialog.setBounds( new Rectangle( (int)( frame.getBounds().getX() + frame.getBounds().getWidth() * 1 / 2 ),
                                                     10,
                                                     (int)energyLevelsDialog.getBounds().getWidth(),
                                                     (int)energyLevelsDialog.getBounds().getHeight() ) );
    }

    /**
     * Sets up the laser cavity
     */
    private void createCavity() {
        laserOrigin = new Point2D.Double( s_origin.getX() + s_laserOffsetX,
                                          s_origin.getY() );
        cavity = new ResonatingCavity( laserOrigin, s_boxWidth, s_boxHeight );
        getModel().addModelElement( cavity );
        ResonatingGraphic cavityGraphic = new ResonatingGraphic( getApparatusPanel(), cavity );
        addGraphic( cavityGraphic, LaserConfig.CAVITY_LAYER );
    }

    /**
     * Sets up the pumping and seed beams
     */
    private void createBeams() {
        seedBeam = new CollimatedBeam( Photon.RED,
                                       s_origin,
                                       s_boxHeight - Photon.RADIUS,
                                       s_boxWidth + s_laserOffsetX * 2,
                                       new Vector2D.Double( 1, 0 ),
                                       LaserConfig.MAXIMUM_SEED_PHOTON_RATE );
        seedBeam.addPhotonEmittedListener( new InternalPhotonEmittedListener() );
        seedBeam.setEnabled( true );
        getLaserModel().setStimulatingBeam( seedBeam );

        pumpingBeam = new CollimatedBeam( Photon.BLUE,
                                          new Point2D.Double( s_origin.getX() + s_laserOffsetX, s_origin.getY() - s_laserOffsetX ),
                                          s_boxHeight + s_laserOffsetX * 2,
                                          s_boxWidth,
                                          new Vector2D.Double( 0, 1 ),
                                          LaserConfig.MAXIMUM_SEED_PHOTON_RATE );
        pumpingBeam.addPhotonEmittedListener( new InternalPhotonEmittedListener() );
        pumpingBeam.setEnabled( true );
        getLaserModel().setPumpingBeam( pumpingBeam );
    }

    /**
     * @param app
     */
    public void activate( PhetApplication app ) {
        super.activate( app );
        Photon.setStimulationBounds( cavity.getBounds() );
        appFrame = app.getApplicationView().getPhetFrame();
        energyLevelsDialog.setVisible( true );

        // Clear the numbers of atoms in each state.
        GroundState.instance().clearNumInState();
        MiddleEnergyState.instance().clearNumInState();
        HighEnergyState.instance().clearNumInState();
    }

    /**
     * @param app
     */
    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        energyLevelsDialog.setVisible( false );
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
                getApparatusPanel().removeGraphic( waveGraphic );
                waveGraphic = null;
                break;
            case PHOTON_WAVE:
                waveGraphic = new StandingWaveGraphic( getApparatusPanel(), getCavity(),
                                                       rightMirror, getModel(), MiddleEnergyState.instance() );
                addGraphic( waveGraphic, 20 );
                break;
            default :
                throw new RuntimeException( "Invalid parameter value" );
        }
    }

    public void setPumpingPhotonView( int pumpingPhotonView ) {
        this.pumpingPhotonView = pumpingPhotonView;
        switch( pumpingPhotonView ) {
            case PHOTON_DISCRETE:
                getApparatusPanel().removeGraphic( beamGraphic );
                beamGraphic = null;
                break;
            case PHOTON_CURTAIN:
                beamGraphic = new WaveBeamGraphic( getApparatusPanel(), pumpingBeam, getCavity() );
                addGraphic( beamGraphic, 1 );
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

    public void setEnergyLevelsVisible( boolean isVisible ) {
        energyLevelsDialog.setVisible( isVisible );
    }

    public LaserModel getLaserModel() {
        return (LaserModel)getModel();
    }

    public PartialMirror getRightMirror() {
        return rightMirror;
    }

    public CollimatedBeam getPumpingBeam() {
        return pumpingBeam;
    }

    protected EnergyLevelMonitorPanel getEnergyLevelsMonitorPanel() {
        return energyLevelsMonitorPanel;
    }

    public void setEnergyLevelsAveragingPeriod( double value ) {
        energyLevelsMonitorPanel.setAveragingPeriod( (long)value );
    }

    public double getEnerglyLevelsAveragingPeriod() {
        return energyLevelsMonitorPanel.getAveragingPeriod();
    }

    public void setDisplayHighLevelEmissions( boolean display ) {
        displayHighLevelEmissions = display;
    }

    public boolean getThreeEnergyLevels() {
        return threeEnergyLevels;
    }

    public void setThreeEnergyLevels( boolean threeEnergyLevels ) {
        this.threeEnergyLevels = threeEnergyLevels;
        if( threeEnergyLevels ) {
            getEnergyLevelsMonitorPanel().setNumLevels( 3 );
            getLaserModel().getPumpingBeam().setEnabled( true );
            MiddleEnergyState.instance().setNextHigherEnergyState( HighEnergyState.instance() );
        }
        else {
            getEnergyLevelsMonitorPanel().setNumLevels( 2 );
            getLaserModel().getPumpingBeam().setEnabled( false );
            MiddleEnergyState.instance().setNextHigherEnergyState( AtomicState.MaxEnergyState.instance() );
        }
    }

    /**
     * Enables or disables mirrors. Also does the initial creation of the mirrors
     *
     * @param mirrorsEnabled
     */
    public void setMirrorsEnabled( boolean mirrorsEnabled ) {

        // Regardless of the value of mirrorsEnabled, we should remove the
        // model elements and graphics for the mirrors. If mirrorsEnabled is
        // true, we want to try remove them first, so they don't get added
        // twice if they were already there
        getModel().removeModelElement( leftMirror );
        getModel().removeModelElement( rightMirror );
        getApparatusPanel().removeGraphic( leftMirrorGraphic );
        getApparatusPanel().removeGraphic( rightMirrorGraphic );
        if( reflectivityControlPanel != null ) {
            getApparatusPanel().remove( reflectivityControlPanel );
        }

        if( mirrorsEnabled ) {
            // Create the mirrors
            createMirrors();

            getModel().addModelElement( leftMirror );
            getModel().addModelElement( rightMirror );
            getApparatusPanel().addGraphic( leftMirrorGraphic, LaserConfig.MIRROR_LAYER );
            getApparatusPanel().addGraphic( rightMirrorGraphic, LaserConfig.MIRROR_LAYER );

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
            getApparatusPanel().add( reflectivityControlPanel );
            getApparatusPanel().revalidate();
        }
        getApparatusPanel().paintImmediately( getApparatusPanel().getBounds() );
    }


    ////////////////////////////////////////////////////////////////////////////////////
    // Other methods
    //
    protected void addAtom( Atom atom ) {
        getModel().addModelElement( atom );
        final AtomGraphic atomGraphic = new AtomGraphic( getApparatusPanel(), atom );
        addGraphic( atomGraphic, LaserConfig.ATOM_LAYER );

        // Add a listener to the atom that will create a photon graphic if the atom
        // emits a photon, and another to deal with an atom leaving the system
        atom.addListener( new InternalPhotonEmittedListener() );
        atom.addListener( new AtomRemovalListener( atomGraphic ) );
    }

    protected void removeAtom( Atom atom ) {
        getModel().removeModelElement( atom );
        atom.removeFromSystem();
    }

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
//        rightMirror.addReflectionStrategy( new LeftReflecting() );
        rightMirrorGraphic = new MirrorGraphic( getApparatusPanel(), rightMirror, MirrorGraphic.LEFT_FACING );
        // The left mirror is 100% reflecting
        Point2D p3 = new Point2D.Double( cavity.getPosition().getX(),
                                         cavity.getPosition().getY() );
        Point2D p4 = new Point2D.Double( cavity.getPosition().getX(),
                                         cavity.getPosition().getY() + cavity.getHeight() );
        leftMirror = new PartialMirror( p3, p4 );
        leftMirror.setReflectivity( 1.0 );
//        leftMirror.addReflectionStrategy( new RightReflecting() );
        leftMirrorGraphic = new MirrorGraphic( getApparatusPanel(), leftMirror, MirrorGraphic.RIGHT_FACING );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Event handling
    //
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
                if( atom.getState() instanceof HighEnergyState
                    && !displayHighLevelEmissions ) {
                    isPhotonGraphicVisible = false;
                }
                else {
                    isPhotonGraphicVisible = ( lasingPhotonView == PHOTON_DISCRETE );
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


    //---------------------------------------------------------------------------------
    // Event handling
    //---------------------------------------------------------------------------------

    public class AtomRemovalListener implements Atom.Listener {
        private AtomGraphic atomGraphic;

        public AtomRemovalListener( AtomGraphic atomGraphic ) {
            this.atomGraphic = atomGraphic;
        }

        public void stateChanged( Atom.Event event ) {
        }

        public void leftSystem( Atom.Event event ) {
            getApparatusPanel().removeGraphic( atomGraphic );
        }

        public void photonEmitted( Atom.Event event ) {
        }
    }

    public class PhotonLeftSystemListener implements Photon.LeftSystemEventListener {
        private Photon photon;
        private PhotonGraphic graphic;

        public PhotonLeftSystemListener( Photon photon, PhotonGraphic graphic ) {
            this.photon = photon;
            this.graphic = graphic;
        }

        public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {

            // Track number of photons
            BaseLaserModule.this.numPhotons--;

            // Get rid of the graphic
            getApparatusPanel().removeGraphic( graphic );
            getApparatusPanel().repaint( graphic.getBounds() );

            // Take us off the listener list of the photon
            photon.removeLeftSystemListener( this );
            graphic = null;
        }
    }
}
