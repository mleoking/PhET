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
import edu.colorado.phet.lasers.controller.LaserControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.view.ThreeEnergyLevelMonitorPanel;

import java.util.ArrayList;

public class MultipleAtomThreeLevelModule extends MultipleAtomBaseModule {

    private double s_maxSpeed = .1;

    private ArrayList atoms;
    private LaserControlPanel controlPanel;
    //    private PhetControlPanel controlPanel;

    /**
     *
     */
    public MultipleAtomThreeLevelModule( AbstractClock clock ) {
        super( "Multiple Atoms / Three Levels" );
        controlPanel = new LaserControlPanel( this, clock );
        setControlPanel( controlPanel );
        controlPanel.setThreeEnergyLevels( true );
        setEnergyMonitorPanel( new ThreeEnergyLevelMonitorPanel( (LaserModel)getModel() ) );
    }
}
