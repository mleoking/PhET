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
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.LaserControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 *
 */
public abstract class BaseLaserModule extends Module implements CollimatedBeam.Listener{
//public class BaseLaserModule extends ApparatusPanel {

    static protected final Point2D s_origin = LaserConfig.ORIGIN;
    static protected final double s_boxHeight = 250;
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


        incomingBeam = new CollimatedBeam( getLaserModel(),
                                           Photon.RED,
                                           s_origin,
                                           s_boxHeight,
                                           s_boxWidth + s_laserOffsetX * 2,
                                           new Vector2D.Double( 1, 0 ) );
        incomingBeam.setPosition( s_origin );
        incomingBeam.setHeight( s_boxHeight - Photon.s_radius );
        incomingBeam.setPhotonsPerSecond( 0 );
        incomingBeam.addListener( this );

        pumpingBeam = new CollimatedBeam( getLaserModel(),
                                          Photon.BLUE,
                                          s_origin,
                                          s_boxHeight,
                                          s_boxWidth,
                                          new Vector2D.Double( 0, 1 ) );
        // TODO: Get rid of hard-coded 100
        pumpingBeam.setPosition( s_origin.getX() + 100,
                                 s_origin.getY() - s_boxHeight / 2 );
        pumpingBeam.setPhotonsPerSecond( 0 );
        pumpingBeam.addListener( this );


        // Add the laser
        laserOrigin = new Point2D.Double( s_origin.getX() + s_laserOffsetX ,
                                         s_origin.getY() );
        cavity = new ResonatingCavity( laserOrigin, s_boxWidth, s_boxHeight );
        getModel().addModelElement( cavity );
        ResonatingCavityGraphic cavityGraphic = new ResonatingCavityGraphic( getApparatusPanel(), cavity );
        addGraphic( cavityGraphic, LaserConfig.CAVITY_LAYER );

//        cavityGraphic.init( cavity );
//        new AddResonatingCavityCmd( cavity ).doIt();

        // Add the low energy beam
        incomingBeam.setActive( true );
        getLaserModel().setStimulatingBeam( incomingBeam );
//        new SetStimulatingBeamCmd( incomingBeam ).doIt();

        // Add the pump beam
        pumpingBeam.setActive( true );
        getLaserModel().setPumpingBeam( pumpingBeam );
//        new SetPumpingBeamCmd( pumpingBeam ).doIt();

        
//        laserControlPanel = new LaserControlPanel( );
//        laserControlPanel = new LaserControlPanel( PhetApplication.instance() );
//        setControlPanel( laserControlPanel );


    }

    /**
     *
     */
    public void activate( PhetApplication app ) {

        super.activate( app );

//        LaserModel laserSystem = (LaserModel)PhetApplication.instance().getPhysicalSystem();
//        laserSystem.removeAtoms();
//        laserModel.removeAtoms();

        // Set up the control panel
//        PhetApplication.instance().getPhetMainPanel().setControlPanel( laserControlPanel );

//        incomingBeam = new CollimatedBeam( getLaserModel(),
//                                           Photon.RED,
//                                           s_origin,
//                                           s_boxHeight,
//                                           s_boxWidth,
//                                           new Vector2D.Double( 1, 0 ) );
//        incomingBeam.setPosition( s_origin );
//        incomingBeam.setHeight( s_boxHeight - Photon.s_radius );
//        incomingBeam.setPhotonsPerSecond( 0 );
//
//        pumpingBeam = new CollimatedBeam( getLaserModel(),
//                                          Photon.BLUE,
//                                          s_origin,
//                                          s_boxHeight,
//                                          s_boxWidth,
//                                          new Vector2D.Double( 0, 1 ) );
//        // TODO: Get rid of hard-coded 100
//        pumpingBeam.setPosition( (double)s_origin.getX() + 100,
//                                 (double)s_origin.getY() - s_boxHeight / 2 );
//        pumpingBeam.setPhotonsPerSecond( 0 );
//
//
//        // Add the laser
//        laserOrigin = new Point2D.Double( s_origin.getX() + s_laserOffsetX ,
//                                         s_origin.getY() );
//        cavity = new ResonatingCavity( laserOrigin, s_boxWidth, s_boxHeight );
//        getModel().addModelElement( cavity );
//        ResonatingCavityGraphic cavityGraphic = new ResonatingCavityGraphic( getApparatusPanel(), cavity );
//        addGraphic( cavityGraphic, LaserConfig.CAVITY_LAYER );
////        cavityGraphic.init( cavity );
////        new AddResonatingCavityCmd( cavity ).doIt();
//
//        // Add the low energy beam
//        incomingBeam.setActive( true );
//        getLaserModel().setStimulatingBeam( incomingBeam );
////        new SetStimulatingBeamCmd( incomingBeam ).doIt();
//
//        // Add the pump beam
//        pumpingBeam.setActive( true );
//        getLaserModel().setPumpingBeam( pumpingBeam );
////        new SetPumpingBeamCmd( pumpingBeam ).doIt();
//
    }

    /**
     *
     */
    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        incomingBeam.setActive( false );
        pumpingBeam.setActive( false );

//        PhetApplication.instance().getPhysicalSystem().addPrepCmd( new ClearPhotonsCmd() );
//        PhetApplication.instance().getPhysicalSystem().clearParticles();

//        laserModel.clearParticles();
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
        AtomGraphic atomGraphic = new AtomGraphic( getApparatusPanel(), atom );
        addGraphic( atomGraphic, LaserConfig.ATOM_LAYER );
//        ResonatingCavity cavity = getLaserModel().getResonatingCavity();
//        Constraint constraintSpec = new CavityMustContainAtom( cavity, atom );
//        cavity.addConstraint( constraintSpec );
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

    public void leavingSystem( Photon photon ) {
    }
}
