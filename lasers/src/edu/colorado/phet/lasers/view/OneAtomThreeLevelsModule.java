/**
 * Class: SingleAtomApparatusPanel
 * Class: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 1:24:50 PM
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.lasers.controller.ApparatusConfiguration;
import edu.colorado.phet.lasers.controller.ThreeLevelControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;

import java.awt.geom.Point2D;

public class OneAtomThreeLevelsModule extends SingleAtomBaseModule {

    private MonitorPanel monitorPanel;

    /**
     *
     */
    public OneAtomThreeLevelsModule( AbstractClock clock ) {
        super( "One Atom / Three Energy Levels" );

        monitorPanel = new ThreeEnergyLevelMonitorPanel( (LaserModel)getModel() );
        setMonitorPanel( monitorPanel  );
        setControlPanel( new ThreeLevelControlPanel( this, clock ) );
    }

    public void activate( PhetApplication app ) {
        super.activate( app );

        double newHeight = 100;
        ResonatingCavity cavity = this.getCavity();
        double cavityHeight =  cavity.getHeight();
        Point2D cavityPos = cavity.getPosition();
        double yNew = cavityPos.getY() + cavityHeight / 2 - newHeight / 2;
        cavity.setPosition( cavityPos.getX(), yNew );
        cavity.setHeight( newHeight );

        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setStimulatedPhotonRate( 2.0f );
        config.setMiddleEnergySpontaneousEmissionTime( 0.500f );
        config.setPumpingPhotonRate( 17f );
        config.setHighEnergySpontaneousEmissionTime( 0.05f );
        config.setReflectivity( 0.7f );
        config.configureSystem( getLaserModel() );
    }
}
