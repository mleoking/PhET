/**
 * Class: BaseLaserModule
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller.module;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.LaserControlPanel;
import edu.colorado.phet.lasers.controller.RightMirrorReflectivityControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.SpontaneouslyEmittingState;
import edu.colorado.phet.lasers.model.mirror.LeftReflecting;
import edu.colorado.phet.lasers.model.mirror.Mirror;
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
 *
 */
public class BaseLaserModule extends Module {

    static protected final Point2D s_origin = LaserConfig.ORIGIN;
    static protected final double s_boxHeight = 150;
    static protected final double s_boxWidth = 300;
    static protected final double s_laserOffsetX = 100;

    static private final int PHOTON_DISCRETE = 0;
    static private final int PHOTON_WAVE = 1;

    private ResonatingCavity cavity;
    private Point2D laserOrigin;
    private LaserModel laserModel;
    private EnergyLevelsDialog energyLevelsDialog;
    private PartialMirror rightMirror;
    private Mirror leftMirror;
    private MirrorGraphic rightMirrorGraphic;
    private MirrorGraphic leftMirrorGraphic;
    private Frame appFrame;
    // Used to save and restore state when the module is activated and deactivated
    private boolean energyDialogIsVisible;
    private EnergyLevelMonitorPanel energyLevelsMonitorPanel;
    private CollimatedBeam stimulatingBeam;
    private CollimatedBeam pumpingBeam;
    private JPanel reflectivityControlPanel;
    private int photonView;
    private WaveBeamGraphic beamGraphic;


    /**
     *
     */
    public BaseLaserModule( String title, AbstractClock clock ) {
        super( title );

        // Create the model
        laserModel = new LaserModel();
        setModel( laserModel );
        laserModel.setBounds( new Rectangle2D.Double( 0, 0, 800, 600 ) );

        // Create the apparatus panel
        ApparatusPanel apparatusPanel = new ApparatusPanel();
        setApparatusPanel( apparatusPanel );
        apparatusPanel.setBackground( Color.white );

        // Create the pumping and stimulating beams
        stimulatingBeam = new CollimatedBeam( Photon.RED,
                                              s_origin,
                                              s_boxHeight - Photon.s_radius,
                                              s_boxWidth + s_laserOffsetX * 2,
                                              new Vector2D.Double( 1, 0 ) );
        stimulatingBeam.addListener( new PhotonEmissionListener() );
        stimulatingBeam.setActive( true );
        getLaserModel().setStimulatingBeam( stimulatingBeam );

        pumpingBeam = new CollimatedBeam( Photon.BLUE,
                                          new Point2D.Double( s_origin.getX() + s_laserOffsetX, s_origin.getY() - s_laserOffsetX ),
                                          s_boxHeight + s_laserOffsetX * 2,
                                          s_boxWidth,
                                          new Vector2D.Double( 0, 1 ) );
        pumpingBeam.addListener( new PhotonEmissionListener() );
        pumpingBeam.setActive( true );
        getLaserModel().setPumpingBeam( pumpingBeam );

        // Add the laser cavity
        laserOrigin = new Point2D.Double( s_origin.getX() + s_laserOffsetX,
                                          s_origin.getY() );
        cavity = new ResonatingCavity( laserOrigin, s_boxWidth, s_boxHeight );
        getModel().addModelElement( cavity );
        ResonatingCavityGraphic cavityGraphic = new ResonatingCavityGraphic( getApparatusPanel(), cavity );
        addGraphic( cavityGraphic, LaserConfig.CAVITY_LAYER );

        // Add the mirrors
        // The right mirror is a partial mirror
        Point2D p1 = new Point2D.Double( cavity.getPosition().getX() + cavity.getWidth(), // + 20,
                                         cavity.getPosition().getY() );
        Point2D p2 = new Point2D.Double( cavity.getPosition().getX() + cavity.getWidth(), // + 20,
                                         cavity.getPosition().getY() + cavity.getHeight() );
        rightMirror = new PartialMirror( p1, p2 );
        rightMirror.addReflectionStrategy( new LeftReflecting() );
        //        rightMirror.setReflectivity( 0 );
        rightMirrorGraphic = new MirrorGraphic( getApparatusPanel(), rightMirror, MirrorGraphic.LEFT_FACING );
        // The left mirror is 100% reflecting
        Point2D p3 = new Point2D.Double( cavity.getPosition().getX(), // - 20,
                                         cavity.getPosition().getY() );
        Point2D p4 = new Point2D.Double( cavity.getPosition().getX(), // - 20,
                                         cavity.getPosition().getY() + cavity.getHeight() );
        leftMirror = new Mirror( p3, p4 );
        leftMirror.addReflectionStrategy( new RightReflecting() );
        leftMirrorGraphic = new MirrorGraphic( getApparatusPanel(), leftMirror, MirrorGraphic.RIGHT_FACING );

        // Create the energy levels dialog
        energyLevelsMonitorPanel = new EnergyLevelMonitorPanel( laserModel );
        energyLevelsDialog = new EnergyLevelsDialog( appFrame, energyLevelsMonitorPanel );

        // Add the control panel
        LaserControlPanel controlPanel = new LaserControlPanel( this, clock );
        setControlPanel( controlPanel );
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        appFrame = app.getApplicationView().getPhetFrame();
        energyLevelsDialog.setVisible( true );
        //        energyLevelsDialog.setVisible( energyDialogIsVisible );

        // todo: this whole mechanism should probably be refactored
        SpontaneouslyEmittingState.setModel( getModel() );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        energyLevelsDialog.setVisible( false );
    }


    public void setPhotonView() {
        setPhotonView( PHOTON_DISCRETE );
        if( beamGraphic != null ) {
            getApparatusPanel().removeGraphic( beamGraphic );
            beamGraphic = null;
        }
    }

    public void setWaveView() {
        setPhotonView( PHOTON_WAVE );
        beamGraphic = new WaveBeamGraphic( getApparatusPanel(), pumpingBeam, getCavity(), getModel() );
        addGraphic( beamGraphic, 1 );
    }

    private void setPhotonView( int viewType ) {
        photonView = viewType;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Implementations of listeners interfaces
    //

    public class PhotonEmissionListener implements PhotonEmittedListener {
        public void photonEmittedEventOccurred( PhotonEmittedEvent event ) {
            Photon photon = event.getPhoton();
            getModel().addModelElement( photon );
            if( photonView == PHOTON_DISCRETE ) {
                final PhotonGraphic pg = new PhotonGraphic( getApparatusPanel(), photon );
                addGraphic( pg, LaserConfig.PHOTON_LAYER );

                // Add a listener that will remove the graphic if the photon leaves the system
                // todo: change to new listener model
                photon.addListener( new PhotonLeftSystemListener( pg ) );
            }
            else {

            }
        }
    }

    public class AtomRemovalListener implements Atom.RemovalListener {
        private AtomGraphic atomGraphic;

        public AtomRemovalListener( AtomGraphic atomGraphic ) {
            this.atomGraphic = atomGraphic;
        }

        public void removalOccurred( Atom.RemovalEvent event ) {
            getApparatusPanel().removeGraphic( atomGraphic );
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

    public void photonCreated( CollimatedBeam beam, Photon photon ) {
        final PhotonGraphic photonGraphic = new PhotonGraphic( getApparatusPanel(), photon );
        addGraphic( photonGraphic, LaserConfig.PHOTON_LAYER );
        
        // Add a listener that will remove the graphic from the apparatus panel when the
        // photon leaves the system
        photon.addListener( new PhotonLeftSystemListener( photonGraphic ) );
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
            getModel().addModelElement( leftMirror );
            getModel().addModelElement( rightMirror );
            getApparatusPanel().addGraphic( leftMirrorGraphic, LaserConfig.CAVITY_LAYER );
            getApparatusPanel().addGraphic( rightMirrorGraphic, LaserConfig.CAVITY_LAYER );

            // Put on the panel to control reflectivity
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
        getApparatusPanel().repaint();
    }

    public void setThreeEnergyLevels( boolean threeEnergyLevels ) {
        if( threeEnergyLevels ) {
            energyLevelsMonitorPanel.setNumLevels( 3 );
            laserModel.getPumpingBeam().setActive( true );
        }
        else {
            energyLevelsMonitorPanel.setNumLevels( 2 );
            laserModel.getPumpingBeam().setActive( false );
        }
    }

    public PartialMirror getRightMirror() {
        return rightMirror;
    }
}
