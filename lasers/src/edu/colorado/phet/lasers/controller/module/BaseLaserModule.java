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
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.lasers.EventRegistry;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.atom.SpontaneouslyEmittingState;
import edu.colorado.phet.lasers.model.mirror.LeftReflecting;
import edu.colorado.phet.lasers.model.mirror.Mirror;
import edu.colorado.phet.lasers.model.mirror.Partial;
import edu.colorado.phet.lasers.model.mirror.RightReflecting;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.view.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 */
public abstract class BaseLaserModule extends Module implements CollimatedBeam.Listener {

    static protected final Point2D s_origin = LaserConfig.ORIGIN;
    static protected final double s_boxHeight = 150;
    static protected final double s_boxWidth = 500;
    static protected final double s_laserOffsetX = 100;

    private ResonatingCavity cavity;
    private Point2D laserOrigin;
    private LaserModel laserModel;
    private EnergyLevelsDialog energyLevelsDialog;
    private Mirror rightMirror;
    private Mirror leftMirror;
    private MirrorGraphic rightMirrorGraphic;
    private MirrorGraphic leftMirrorGraphic;
    private Frame appFrame;
    // Used to save and restore state when the module is activated and deactivated
    private boolean energyDialogIsVisible;

    public class PEL implements CollimatedBeam.PhotonEmittedEventListener {
        int cnt;

        // Just a test of the listener system
        public void photonEmittedEventOccurred( CollimatedBeam.PhotonEmittedEvent event ) {
            //            System.out.println( "cnt: " + cnt++ );
        }
    };


    /**
     *
     */
    public BaseLaserModule( String title ) {
        super( title );

        EventRegistry.instance.addListener( new PEL() );

        laserModel = new LaserModel();
        setModel( laserModel );
        laserModel.setBounds( new Rectangle2D.Double( 0, 0, 800, 600 ) );

        ApparatusPanel apparatusPanel = new ApparatusPanel();
        setApparatusPanel( apparatusPanel );
        apparatusPanel.setBackground( Color.white );

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
        rightMirror = new Mirror( p1, p2 );
        rightMirror.addReflectionStrategy( new LeftReflecting() );
        rightMirror.addReflectionStrategy( new Partial( .2 ) );
        rightMirrorGraphic = new MirrorGraphic( getApparatusPanel(), rightMirror, MirrorGraphic.LEFT_FACING );
        // The left mirror is 100% reflecting
        Point2D p3 = new Point2D.Double( cavity.getPosition().getX(), // - 20,
                                         cavity.getPosition().getY() );
        Point2D p4 = new Point2D.Double( cavity.getPosition().getX(), // - 20,
                                         cavity.getPosition().getY() + cavity.getHeight() );
        leftMirror = new Mirror( p3, p4 );
        leftMirror.addReflectionStrategy( new RightReflecting() );
        leftMirrorGraphic = new MirrorGraphic( getApparatusPanel(), leftMirror, MirrorGraphic.RIGHT_FACING );
    }

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

    protected void addAtom( Atom atom ) {
        getModel().addModelElement( atom );
        final AtomGraphic atomGraphic = new AtomGraphic( getApparatusPanel(), atom );
        addGraphic( atomGraphic, LaserConfig.ATOM_LAYER );

        // Add a listener to the atom that will create a photon graphic if the atom
        // emits a photon
        atom.addListener( new Atom.Listener() {
            public void photonEmitted( Atom atom, Photon photon ) {
                getModel().addModelElement( photon );
                final PhotonGraphic pg = new PhotonGraphic( getApparatusPanel(), photon );
                addGraphic( pg, LaserConfig.PHOTON_LAYER );

                // Add a listener that will remove the graphic if the photon leaves the system
                photon.addListener( new Photon.Listener() {
                    public void leavingSystem( Photon photon ) {
                        getApparatusPanel().removeGraphic( pg );
                    }
                } );
            }

            public void leftSystem( Atom atom ) {
                getApparatusPanel().removeGraphic( atomGraphic );
            }

            public void stateChanged( Atom atom, AtomicState oldState, AtomicState newState ) {
            }
        } );
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
        photon.addListener( new Photon.Listener() {
            public void leavingSystem( Photon photon ) {
                getApparatusPanel().removeGraphic( photonGraphic );
                getApparatusPanel().repaint( photonGraphic.getBounds() );
                System.out.println( "!!!" );
            }
        } );
    }

    public void setEnergyMonitorPanel( MonitorPanel monitorPanel ) {
        if( energyLevelsDialog != null ) {
            energyLevelsDialog.setVisible( false );
        }
        energyLevelsDialog = new EnergyLevelsDialog( appFrame, monitorPanel, getLaserModel() );
        //        energyLevelsDialog.setVisible( true );
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        appFrame = app.getApplicationView().getPhetFrame();
        energyLevelsDialog.setVisible( energyDialogIsVisible );

        // todo: this whole mechanism should probably be refactored
        SpontaneouslyEmittingState.setModel( getModel() );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        energyLevelsDialog.setVisible( false );
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
        if( mirrorsEnabled ) {
            getModel().addModelElement( leftMirror );
            getModel().addModelElement( rightMirror );
            getApparatusPanel().addGraphic( leftMirrorGraphic, LaserConfig.CAVITY_LAYER );
            getApparatusPanel().addGraphic( rightMirrorGraphic, LaserConfig.CAVITY_LAYER );
        }
        getApparatusPanel().repaint();
    }

    public void setThreeEnergyLevels( boolean threeEnergyLevels ) {
        if( threeEnergyLevels ) {
            setEnergyMonitorPanel( new ThreeEnergyLevelMonitorPanel( getLaserModel() ) );
            laserModel.getPumpingBeam().setActive( true );
        }
        else {
            setEnergyMonitorPanel( new TwoEnergyLevelMonitorPanel( getLaserModel() ) );
            laserModel.getPumpingBeam().setActive( false );
        }
    }
}
