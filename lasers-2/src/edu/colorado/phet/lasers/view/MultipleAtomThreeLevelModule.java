/**
 * Class: MultipleAtomThreeLevelModule
 * Class: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 1:36:10 PM
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.lasers.controller.command.AddAtomCmd;
import edu.colorado.phet.lasers.controller.ThreeLevelControlPanel;
import edu.colorado.phet.lasers.controller.ApparatusConfiguration;
import edu.colorado.phet.lasers.physics.atom.Atom;
import edu.colorado.phet.lasers.physics.ResonatingCavity;
import edu.colorado.phet.lasers.physics.LaserSystem;
import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.collision.ContactDetector;

import java.util.ArrayList;
import java.awt.geom.Point2D;

public class MultipleAtomThreeLevelModule extends BaseLaserModule {

    private ArrayList atoms;
    private MonitorPanel monitorPanel;
    private PhetControlPanel controlPanel;

    /**
     *
     */
    public MultipleAtomThreeLevelModule() {
        super( "Multiple Atoms / Three Levels" );

        monitorPanel = new ThreeEnergyLevelMonitorPanel( (LaserSystem)getModel());
        controlPanel = new ThreeLevelControlPanel();
        setMonitorPanel( monitorPanel );
        setControlPanel( controlPanel );
    }

    /**
     *
     */
    public void activate( PhetApplication app ) {
        super.activate( app );

//        PhetApplication.instance().getPhetMainPanel().setMonitorPanel( monitorPanel );
//        PhetApplication.instance().getPhetMainPanel().setControlPanel( controlPanel );

        float newHeight = 100;
        ResonatingCavity cavity = this.getCavity();
        float cavityHeight =  cavity.getHeight();
        Point2D cavityPos = cavity.getPosition();
        double yNew = cavityPos.getY() + cavityHeight / 2 - newHeight / 2;
        cavity.setPosition( cavityPos.getX(), yNew );
        cavity.setHeight( newHeight );

        Atom atom = null;
        atoms = new ArrayList();
        for( int i = 0; i < 20; i++ ) {
            atom = new Atom();
            boolean placed = false;

            // Place atoms so they don't overlap
            do {
                placed = true;
                atom.setPosition( (float)( getLaserOrigin().getX() + ( Math.random() ) * ( s_boxWidth - atom.getRadius() * 2 ) + atom.getRadius() ),
                                  (float)( getLaserOrigin().getY() + ( Math.random() ) * ( newHeight - atom.getRadius() * 2 ) ) + atom.getRadius() );
                atom.setVelocity( (float)( Math.random() - 0.5 ) * s_maxSpeed,
                                  (float)( Math.random() - 0.5 ) * s_maxSpeed );
                for( int j = 0; j < atoms.size(); j++ ) {
                    Atom atom2 = (Atom)atoms.get( j );
                    if( ContactDetector.areContacting( atom, atom2 )) {
                        placed = false;
                        break;
                    }
                }
            } while( !placed );
            atoms.add( atom );
            new AddAtomCmd( atom ).doIt();
        }

        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setStimulatedPhotonRate( 2.0f );
        config.setMiddleEnergySpontaneousEmissionTime( 0.500f );
        config.setPumpingPhotonRate( 100f );
        config.setHighEnergySpontaneousEmissionTime( 0.05f );
        config.setReflectivity( 0.7f );
        config.configureSystem();

    }

    /**
     *
     */
    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        atoms.clear();
    }

    //
    // Static fields and methods
    //
    private float s_maxSpeed = 200;
}
