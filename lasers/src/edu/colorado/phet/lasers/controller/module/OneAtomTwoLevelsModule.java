/**
 * Class: SingleAtomApparatusPanel
 * Class: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 1:24:50 PM
 */
package edu.colorado.phet.lasers.controller.module;

import edu.colorado.phet.lasers.controller.ApparatusConfiguration;
import edu.colorado.phet.lasers.controller.TwoLevelControlPanel;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.view.*;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;

import java.awt.geom.Point2D;

public class OneAtomTwoLevelsModule extends SingleAtomBaseModule {

    private MonitorPanel monitorPanel;
//    private PhetControlPanel controlPanel = new TwoLevelControlPanel();

    /**
     *
     */
    public OneAtomTwoLevelsModule( AbstractClock clock) {
        super( "One Atom / Two Energy Levels" );

        monitorPanel = new TwoEnergyLevelMonitorPanel( (LaserModel)getModel() );
        setMonitorPanel( monitorPanel );
        setControlPanel( new TwoLevelControlPanel( this, clock ) );

//        double newHeight = 100;
//        ResonatingCavity cavity = this.getCavity();
//        double cavityHeight =  cavity.getHeight();
//        Point2D cavityPos = cavity.getPosition();
//        double yNew = cavityPos.getY() + cavityHeight / 2 - newHeight / 2;
//        cavity.setPosition( cavityPos.getX(), yNew );
//        cavity.setHeight( newHeight );

//        ApparatusConfiguration config = new ApparatusConfiguration();
//        config.setStimulatedPhotonRate( LaserConfig.DEFAULT_STIMULATING_PHOTON_RATE );
//        config.setMiddleEnergySpontaneousEmissionTime( LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME );
//        config.setPumpingPhotonRate( 0.0f );
//        config.setReflectivity( 0.7f );
//        config.configureSystem( getLaserModel() );
    }

    public void activate( PhetApplication app ) {
        super.activate( app );

//        PhetApplication.instance().getPhetMainPanel().setMonitorPanel( monitorPanel );
//        PhetApplication.instance().getPhetMainPanel().setControlPanel( controlPanel );
//        PhetApplication.instance().getPhetMainPanel().setMonitorPanel( new TwoEnergyLevelMonitorPanel() );
//        PhetApplication.instance().getPhetMainPanel().setControlPanel( new TwoLevelControlPanel() );


//        double newHeight = 100;
//        ResonatingCavity cavity = this.getCavity();
//        double cavityHeight =  cavity.getHeight();
//        Point2D cavityPos = cavity.getPosition();
//        double yNew = cavityPos.getY() + cavityHeight / 2 - newHeight / 2;
//        cavity.setPosition( cavityPos.getX(), yNew );
//        cavity.setHeight( newHeight );
//
//        ApparatusConfiguration config = new ApparatusConfiguration();
//        config.setStimulatedPhotonRate( 8.0f );
//        config.setMiddleEnergySpontaneousEmissionTime( 0.500f );
//        config.setPumpingPhotonRate( 0.0f );
//        config.setReflectivity( 0.7f );
//        config.configureSystem( getLaserModel() );

//        ApparatusConfiguration config = new ApparatusConfiguration();
//        config.setStimulatedPhotonRate( LaserConfig.DEFAULT_STIMULATING_PHOTON_RATE );
//        config.setMiddleEnergySpontaneousEmissionTime( LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME );
//        config.setPumpingPhotonRate( 0.0f );
//        config.setReflectivity( 0.7f );
//        config.configureSystem( getLaserModel() );
    }
}
