/**
 * Class: BaseLaserModule
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.LaserControlPanel;
import edu.colorado.phet.lasers.controller.command.*;
import edu.colorado.phet.lasers.physics.ResonatingCavity;
import edu.colorado.phet.lasers.physics.LaserSystem;
import edu.colorado.phet.lasers.physics.photon.CollimatedBeam;
import edu.colorado.phet.lasers.physics.photon.Photon;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.BaseModel;

import java.awt.geom.Point2D;

/**
 *
 */
public class BaseLaserModule extends Module {
//public class BaseLaserModule extends ApparatusPanel {

    private LaserControlPanel laserControlPanel;
    private ResonatingCavity cavity;
    private CollimatedBeam incomingBeam;
    private CollimatedBeam pumpingBeam;
    private Point2D.Float laserOrigin;
    private LaserSystem laserModel;

    /**
     *
     */
    public BaseLaserModule( String title ) {
        super( title );

        laserModel = new LaserSystem();
        setModel( laserModel );


        ApparatusPanel apparatusPanel = new ApparatusPanel();
        setApparatusPanel( apparatusPanel );


        laserControlPanel = new LaserControlPanel( );
//        laserControlPanel = new LaserControlPanel( PhetApplication.instance() );
        setControlPanel( laserControlPanel );
    }

    /**
     *
     */
    public void activate( PhetApplication app ) {

        super.activate( app );

//        LaserSystem laserSystem = (LaserSystem)PhetApplication.instance().getPhysicalSystem();
//        laserSystem.removeAtoms();
        laserModel.removeAtoms();

        // Set up the control panel
//        PhetApplication.instance().getPhetMainPanel().setControlPanel( laserControlPanel );

        incomingBeam = new CollimatedBeam( Photon.RED,
                                           s_origin,
                                           s_boxHeight,
                                           s_boxWidth,
                                           new Vector2D.Double( 1, 0 ) );
        incomingBeam.setPosition( s_origin );
        incomingBeam.setHeight( s_boxHeight - Photon.s_radius );
        incomingBeam.setPhotonsPerSecond( 0 );

        pumpingBeam = new CollimatedBeam( Photon.BLUE,
                                          s_origin,
                                          s_boxHeight,
                                          s_boxWidth,
                                          new Vector2D.Double( 0, 1 ) );
        // TODO: Get rid of hard-coded 100
        pumpingBeam.setPosition( (float) s_origin.getX() + 100 ,
                                 (float) s_origin.getY() - s_boxHeight / 2 );
        pumpingBeam.setPhotonsPerSecond( 0 );


        // Add the laser
        laserOrigin = new Point2D.Float( (float) ( s_origin.getX() + s_laserOffsetX ),
                                                               (float) ( s_origin.getY() ) );
        cavity = new ResonatingCavity( laserOrigin, s_boxWidth, s_boxHeight );
        new AddResonatingCavityCmd( cavity ).doIt();

        // Add the low energy beam
        incomingBeam.setActive( true );
        new SetStimulatingBeamCmd( incomingBeam ).doIt();

        // Add the pump beam
        pumpingBeam.setActive( true );
        new SetPumpingBeamCmd( pumpingBeam ).doIt();

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

    /**
     *
     * @return
     */
    protected Point2D.Float getLaserOrigin() {
        return laserOrigin;
    }

    protected ResonatingCavity getCavity() {
        return cavity;
    }

    //
    // Static fields and methods
    //
    static protected final Point2D.Float s_origin = LaserConfig.ORIGIN;
    static protected final float s_boxHeight = 250;
    static protected final float s_boxWidth = 500;
    static protected final float s_laserOffsetX = 100;
}
