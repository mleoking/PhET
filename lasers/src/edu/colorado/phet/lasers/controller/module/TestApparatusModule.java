/**
 * Class: SingleAtomApparatusPanel
 * Class: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 1:24:50 PM
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller.module;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.lasers.controller.ApparatusConfiguration;
import edu.colorado.phet.lasers.model.ResonatingCavity;

import java.awt.geom.Point2D;

public class TestApparatusModule extends SingleAtomModule {

    //    private MonitorPanel monitorPanel = new TwoEnergyLevelMonitorPanel();
    //    private PhetControlPanel controlPanel = new TwoLevelControlPanel();

    /**
     *
     */
    public TestApparatusModule( AbstractClock clock ) {
        super( null );
        //        super( "Test" );
    }

    public void activate( PhetApplication app ) {
        super.activate( app );

        //        PhetApplication.instance().getPhetMainPanel().setMonitorPanel( new TwoEnergyLevelMonitorPanel() );
        //        PhetApplication.instance().getPhetMainPanel().setControlPanel( new TwoLevelControlPanel() );


        double newHeight = 100;
        ResonatingCavity cavity = this.getCavity();
        double cavityHeight = cavity.getHeight();
        Point2D cavityPos = cavity.getPosition();
        //        Vector2D cavityPos = cavity.getPosition();
        double yNew = cavityPos.getY() + cavityHeight / 2 - newHeight / 2;
        cavity.setPosition( cavityPos.getX(), yNew );
        //        cavity.setHeight( newHeight );

        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setSeedPhotonRate( 0.0f );
        config.setMiddleEnergySpontaneousEmissionTime( 10.00f );
        config.setPumpingPhotonRate( 0.0f );
        config.setReflectivity( 0.0f );
        config.configureSystem( getLaserModel() );
    }
}
