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
import edu.colorado.phet.lasers.controller.TwoLevelControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.view.TwoEnergyLevelMonitorPanel;

import java.util.ArrayList;

public class MultipleAtomTwoLevelModule extends MultipleAtomBaseModule {

    private double s_maxSpeed = .1;
    private ArrayList atoms;

    /**
     *
     */
    public MultipleAtomTwoLevelModule( AbstractClock clock ) {
        super( "Multiple Atoms / Two Levels" );
        setControlPanel( new TwoLevelControlPanel( this, clock ) );
        setEnergyMonitorPanel( new TwoEnergyLevelMonitorPanel( (LaserModel)getModel() ) );
    }
}
