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
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.LaserControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.view.ResonatingCavityGraphic;
import edu.colorado.phet.lasers.view.AtomGraphic;
import edu.colorado.phet.lasers.view.PhotonGraphic;

import java.awt.geom.Point2D;
import java.awt.*;

/**
 *
 */
public abstract class BaseLaserModule extends Module implements CollimatedBeam.Listener {

    static protected final Point2D s_origin = LaserConfig.ORIGIN;
    static protected final double s_boxHeight = 150;
    static protected final double s_boxWidth = 500;
    static protected final double s_laserOffsetX = 100;

    private LaserControlPanel laserControlPanel;
    private ResonatingCavity cavity;
    private CollimatedBeam incomingBeam;
    private CollimatedBeam pumpingBeam;
    private Point2D laserOrigin;
    private LaserModel laserModel;

    /**
     *
     */
    public BaseLaserModule( String title ) {
        super( title );

        laserModel = new LaserModel();
        setModel( laserModel );

        ApparatusPanel apparatusPanel = new ApparatusPanel();
        setApparatusPanel( apparatusPanel );
        apparatusPanel.setBackground( Color.white );

        incomingBeam = new CollimatedBeam( getLaserModel(),
                                           Photon.RED,
                                           s_origin,
                                           s_boxHeight - Photon.s_radius,
                                           s_boxWidth + s_laserOffsetX * 2,
                                           new Vector2D.Double( 1, 0 ) );
        incomingBeam.addListener( this );
        incomingBeam.setActive( true );
        getLaserModel().setStimulatingBeam( incomingBeam );

        pumpingBeam = new CollimatedBeam( getLaserModel(),
                                          Photon.BLUE,
                                          new Point2D.Double( s_origin.getX() + s_laserOffsetX, s_origin.getY() - s_laserOffsetX) ,
                                          s_boxHeight + s_laserOffsetX * 2,
                                          s_boxWidth,
                                          new Vector2D.Double( 0, 1 ) );
        pumpingBeam.addListener( this );

        // Add the laser
        laserOrigin = new Point2D.Double( s_origin.getX() + s_laserOffsetX,
                                          s_origin.getY() );
        cavity = new ResonatingCavity( laserOrigin, s_boxWidth, s_boxHeight );
        getModel().addModelElement( cavity );
        ResonatingCavityGraphic cavityGraphic = new ResonatingCavityGraphic( getApparatusPanel(), cavity );
        addGraphic( cavityGraphic, LaserConfig.CAVITY_LAYER );

        // Add the pump beam
        pumpingBeam.setActive( true );
        getLaserModel().setPumpingBeam( pumpingBeam );
    }

    protected Point2D getLaserOrigin() {
        return laserOrigin;
    }

    protected ResonatingCavity getCavity() {
        return cavity;
    }

    public void setEnergyLevelsVisible( boolean selected ) {
        throw new RuntimeException( "TBI" );
    }

    public LaserModel getLaserModel() {
        return (LaserModel)getModel();
    }

    protected void addAtom( Atom atom ) {
        getModel().addModelElement( atom );
        final AtomGraphic atomGraphic = new AtomGraphic( getApparatusPanel(), atom );
        addGraphic( atomGraphic, LaserConfig.ATOM_LAYER );
        atom.addListener( new Atom.Listener() {
            public void photonEmitted( Atom atom, Photon photon ) {
            }

            public void leftSystem( Atom atom ) {
                getApparatusPanel().removeGraphic( atomGraphic );
            }

            public void stateChanged( Atom atom, AtomicState oldState, AtomicState newState ) {
            }
        } );
//        ResonatingCavity cavity = getLaserModel().getResonatingCavity();
//        Constraint constraintSpec = new CavityMustContainAtom( cavity, atom );
//        cavity.addConstraint( constraintSpec );
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
            }
        } );
    }
}
