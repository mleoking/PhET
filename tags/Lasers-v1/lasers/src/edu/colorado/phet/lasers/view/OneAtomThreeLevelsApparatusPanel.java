/**
 * Class: SingleAtomApparatusPanel
 * Class: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 1:24:50 PM
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.controller.PhetControlPanel;
import edu.colorado.phet.graphics.MonitorPanel;
import edu.colorado.phet.lasers.controller.ApparatusConfiguration;
import edu.colorado.phet.lasers.controller.TwoLevelControlPanel;
import edu.colorado.phet.lasers.controller.ThreeLevelControlPanel;
import edu.colorado.phet.lasers.physics.ResonatingCavity;
import edu.colorado.phet.physics.Vector2D;

public class OneAtomThreeLevelsApparatusPanel extends SingleAtomBaseApparatusPanel {

    private MonitorPanel monitorPanel = new ThreeEnergyLevelMonitorPanel();

    /**
     *
     */
    public OneAtomThreeLevelsApparatusPanel() {
        super( "One Atom / Three Energy Levels" );
    }

    public void activate() {
        super.activate();

        PhetApplication.instance().getPhetMainPanel().setMonitorPanel( monitorPanel  );
        PhetApplication.instance().getPhetMainPanel().setControlPanel( new ThreeLevelControlPanel() );


        float newHeight = 100;
        ResonatingCavity cavity = this.getCavity();
        float cavityHeight =  cavity.getHeight();
        Vector2D cavityPos = cavity.getPosition();
        float yNew = cavityPos.getY() + cavityHeight / 2 - newHeight / 2;
        cavity.setPosition( cavityPos.getX(), yNew );
        cavity.setHeight( newHeight );

        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setStimulatedPhotonRate( 2.0f );
        config.setMiddleEnergySpontaneousEmissionTime( 0.500f );
        config.setPumpingPhotonRate( 17f );
        config.setHighEnergySpontaneousEmissionTime( 0.05f );
        config.setReflectivity( 0.7f );
        config.configureSystem();
    }
}
