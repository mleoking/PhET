/**
 * Class: MultipleAtomThreeLevelModule
 * Class: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 1:36:10 PM
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller.module;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.lasers.controller.ApparatusConfiguration;
import edu.colorado.phet.lasers.controller.LaserControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.view.TwoEnergyLevelMonitorPanel;

public class MultipleAtomTwoLevelModule extends MultipleAtomBaseModule {

    private LaserControlPanel controlPanel;

    /**
     *
     */
    public MultipleAtomTwoLevelModule( AbstractClock clock ) {
        super( "Multiple Atoms / Two Levels" );
        controlPanel = new LaserControlPanel( this, clock );
        setControlPanel( controlPanel );
        controlPanel.setThreeEnergyLevels( false );
        setEnergyMonitorPanel( new TwoEnergyLevelMonitorPanel( (LaserModel)getModel() ) );

        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setPumpingPhotonRate( 0.0f );
        config.setReflectivity( 0.7f );
        config.configureSystem( getLaserModel() );
    }
}
