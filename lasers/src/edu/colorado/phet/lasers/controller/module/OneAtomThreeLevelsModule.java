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

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.lasers.controller.LaserControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.view.ThreeEnergyLevelMonitorPanel;

public class OneAtomThreeLevelsModule extends SingleAtomBaseModule {

    public OneAtomThreeLevelsModule( AbstractClock clock ) {
        super( "One Atom / Three Energy Levels" );

        setEnergyMonitorPanel( new ThreeEnergyLevelMonitorPanel( (LaserModel)getModel() ) );
        LaserControlPanel controlPanel = new LaserControlPanel( this, clock );
        controlPanel.setThreeEnergyLevels( true );
        controlPanel.setMaxPhotonRate( 5 );
        setControlPanel( controlPanel );
    }
}
