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
import edu.colorado.phet.lasers.physics.ResonatingCavity;
import edu.colorado.phet.physics.Vector2D;

public class OneAtomTwoLevelsApparatusPanel extends SingleAtomBaseApparatusPanel {

    private MonitorPanel monitorPanel = new TwoEnergyLevelMonitorPanel();
//    private PhetControlPanel controlPanel = new TwoLevelControlPanel();

    /**
     *
     */
    public OneAtomTwoLevelsApparatusPanel() {
        super( "One Atom / Two Energy Levels" );
    }

    public void activate() {
        super.activate();

        PhetApplication.instance().getPhetMainPanel().setMonitorPanel( monitorPanel );
//        PhetApplication.instance().getPhetMainPanel().setControlPanel( controlPanel );
//        PhetApplication.instance().getPhetMainPanel().setMonitorPanel( new TwoEnergyLevelMonitorPanel() );
        PhetApplication.instance().getPhetMainPanel().setControlPanel( new TwoLevelControlPanel() );


        float newHeight = 100;
        ResonatingCavity cavity = this.getCavity();
        float cavityHeight =  cavity.getHeight();
        Vector2D cavityPos = cavity.getPosition();
        float yNew = cavityPos.getY() + cavityHeight / 2 - newHeight / 2;
        cavity.setPosition( cavityPos.getX(), yNew );
        cavity.setHeight( newHeight );

        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setStimulatedPhotonRate( 8.0f );
        config.setMiddleEnergySpontaneousEmissionTime( 0.500f );
        config.setPumpingPhotonRate( 0.0f );
        config.setReflectivity( 0.7f );
        config.configureSystem();
    }
}
