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
import edu.colorado.phet.lasers.controller.LaserControlPanel;
import edu.colorado.phet.lasers.controller.RightMirrorReflectivityControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.MiddleEnergyState;
import edu.colorado.phet.lasers.model.mirror.LeftReflecting;
import edu.colorado.phet.lasers.model.mirror.PartialMirror;
import edu.colorado.phet.lasers.model.mirror.RightReflecting;
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
    static protected final double s_boxHeight = 150;
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
    // Used to save and restore state when the module is activated and deactivated
    private boolean energyDialogIsVisible;
    private EnergyLevelMonitorPanel energyLevelsMonitorPanel;
    private CollimatedBeam seedBeam;
    private CollimatedBeam pumpingBeam;
    private JPanel reflectivityControlPanel;
    private int pumpingPhotonView;
    private int lasingPhotonView = PHOTON_DISCRETE;
    private WaveBeamGraphic beamGraphic;
    private StandingWaveGraphic waveGraphic;


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
        //        ApparatusPanel apparatusPanel = new ApparatusPanel();
        setApparatusPanel( apparatusPanel );
        apparatusPanel.setBackground( Color.white );

        // Create the pumping and stimulating beams
        seedBeam = new CollimatedBeam( Photon.RED,
                                       s_origin,
                                       s_boxHeight - Photon.RADIUS,
                                       s_boxWidth + s_laserOffsetX * 2,
                                       new Vector2D.Double( 1, 0 ) );
        seedBeam.addListener( new PhotonEmissionListener() );
        seedBeam.setEnabled( true );
        getLaserModel().setStimulatingBeam( seedBeam );

        pumpingBeam = new CollimatedBeam( Photon.BLUE,
                                          new Point2D.Double( s_origin.getX() + s_laserOffsetX, s_origin.getY() - s_laserOffsetX ),
                                          s_boxHeight + s_laserOffsetX * 2,
                                          s_boxWidth,
                                          new Vector2D.Double( 0, 1 ) );
        pumpingBeam.addListener( new PhotonEmissionListener() );
        pumpingBeam.setEnabled( true );
        getLaserModel().setPumpingBeam( pumpingBeam );

        // Add the laser cavity
        laserOrigin = new Point2D.Double( s_origin.getX() + s_laserOffsetX,
                                          s_origin.getY() );
        cavity = new ResonatingCavity( laserOrigin, s_boxWidth, s_boxHeight );
        getModel().addModelElement( cavity );
        ResonatingCavityGraphic cavityGraphic = new ResonatingCavityGraphic( getApparatusPanel(), cavity );
        addGraphic( cavityGraphic, LaserConfig.CAVITY_LAYER );

        // Create the energy levels dialog
        energyLevelsMonitorPanel = new EnergyLevelMonitorPanel( laserModel );
        energyLevelsDialog = new EnergyLevelsDialog( appFrame, energyLevelsMonitorPanel );
        energyLevelsDialog.setBounds( new Rectangle( (int)( frame.getBounds().getX() + frame.getBounds().getWidth() * 1 / 2 ),
                                                     10,
                                                     (int)energyLevelsDialog.getBounds().getWidth(),
                                                     (int)energyLevelsDialog.getBounds().getHeight() ) );

        // Add the control panel
        LaserControlPanel controlPanel = new LaserControlPanel( this );
        setControlPanel( controlPanel );

        // Make a spectrum slider
        //        SpectrumSlider slider = new SpectrumSlider( getApparatusPanel() );
        //        slider.setLocation( new Point(100,100) ); // default is (0,0)
        //        slider.setOrientation( SpectrumSlider.HORIZONTAL ); // default is HORIZONTAL
        //        slider.setTransmissionWidth( 50.0 ); // default is 0.0
        //        slider.setKnobSize( new Dimension(10,15) ); // default is (20,30)
        //        slider.setSpectrumSize( new Dimension( 100, 20) ); // default is (200,25)
        //        addGraphic( slider, 20 );
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        appFrame = app.getApplicationView().getPhetFrame();
        energyLevelsDialog.setVisible( true );
        //        energyLevelsDialog.setVisible( energyDialogIsVisible );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        energyLevelsDialog.setVisible( false );
    }

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


    /////////////////////////////////////////////////////////////////////////////////////
    // Setters and getters
    //
    protected Point2D getLaserOrigin() {
        return laserOrigin;
    }

    protected ResonatingCavity getCavity() {
        return cavity;
    }

    public void setEnergyLevelsVisible( boolean isVisible ) {
        energyLevelsDialog.setVisible( isVisible );
        energyDialogIsVisible = isVisible;
    }

    public LaserModel getLaserModel() {
        return (LaserModel)getModel();
    }

    public PartialMirror getRightMirror() {
        return rightMirror;
    }

    protected EnergyLevelMonitorPanel getEnergyLevelsMonitorPanel() {
        return energyLevelsMonitorPanel;
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
        atom.addListener( new PhotonEmissionListener() );
        atom.addListener( new AtomRemovalListener( atomGraphic ) );
    }

    protected void removeAtom( Atom atom ) {
        getModel().removeModelElement( atom );
        atom.removeFromSystem();
    }

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
            //            getApparatusPanel().addGraphic( leftMirrorGraphic, LaserConfig.CAVITY_LAYER );
            //            getApparatusPanel().addGraphic( rightMirrorGraphic, LaserConfig.CAVITY_LAYER );

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
        }
        getApparatusPanel().paintImmediately( getApparatusPanel().getBounds() );
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
        rightMirror.addReflectionStrategy( new LeftReflecting() );
        rightMirrorGraphic = new MirrorGraphic( getApparatusPanel(), rightMirror, MirrorGraphic.LEFT_FACING );
        // The left mirror is 100% reflecting
        Point2D p3 = new Point2D.Double( cavity.getPosition().getX(),
                                         cavity.getPosition().getY() );
        Point2D p4 = new Point2D.Double( cavity.getPosition().getX(),
                                         cavity.getPosition().getY() + cavity.getHeight() );
        leftMirror = new PartialMirror( p3, p4 );
        leftMirror.setReflectivity( 1.0 );
        leftMirror.addReflectionStrategy( new RightReflecting() );
        leftMirrorGraphic = new MirrorGraphic( getApparatusPanel(), leftMirror, MirrorGraphic.RIGHT_FACING );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Implementations of listeners interfaces
    //
    public class PhotonEmissionListener implements PhotonEmittedListener {

        public void photonEmittedEventOccurred( PhotonEmittedEvent event ) {
            Photon photon = event.getPhoton();
            getModel().addModelElement( photon );
            // Is it a pumping beam photon, and are we viewing discrete photons?
            if( pumpingPhotonView == PHOTON_DISCRETE
                && photon.getWavelength() == pumpingBeam.getWavelength() ) {
                PhotonGraphic pg = new PhotonGraphic( getApparatusPanel(), photon );
                addGraphic( pg, LaserConfig.PHOTON_LAYER );
                // Add a listener that will remove the graphic if the photon leaves the system
                photon.addListener( new PhotonLeftSystemListener( pg ) );
            }
            // Is it a lasing wavelength photon, and are we viewing discrete photons?
            else if( lasingPhotonView == PHOTON_DISCRETE
                     && photon.getWavelength() == MiddleEnergyState.instance().getWavelength() ) {
                PhotonGraphic pg = new PhotonGraphic( getApparatusPanel(), photon );
                addGraphic( pg, LaserConfig.PHOTON_LAYER );
                // Add a listener that will remove the graphic if the photon leaves the system
                photon.addListener( new PhotonLeftSystemListener( pg ) );
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Inner classes
    //
    public class AtomRemovalListener implements Atom.RemovalListener {
        private AtomGraphic atomGraphic;

        public AtomRemovalListener( AtomGraphic atomGraphic ) {
            this.atomGraphic = atomGraphic;
        }

        public void removalOccurred( Atom.RemovalEvent event ) {
            getApparatusPanel().removeGraphic( atomGraphic );
        }
    }

    public class PhotonLeftSystemListener implements Photon.LeftSystemEventListener {
        private PhotonGraphic graphic;

        public PhotonLeftSystemListener( PhotonGraphic graphic ) {
            this.graphic = graphic;
        }

        public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
            getApparatusPanel().removeGraphic( graphic );
            getApparatusPanel().repaint( graphic.getBounds() );
        }
    }
}
