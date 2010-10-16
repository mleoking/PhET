package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.flexcommon.FlexCommon;

import flash.events.Event;

/**
 * Extends DensityAndBuoyancyFlashCommon to provide Buoyancy specific behavior.
 */
public class BuoyancyFlashCommon extends DensityAndBuoyancyFlashCommon {
    public function BuoyancyFlashCommon() {
    }

    protected override function positionButtons( evt: Event ): void {
        if ( FlexCommon.getInstance().commonButtons != null ) {
            FlexCommon.getInstance().commonButtons.setLocationXY( stage.stageWidth - FlexCommon.getInstance().commonButtons.getPreferredWidth() - DensityConstants.CONTROL_INSET, 5 );
        }
    }
}
}