//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.flexcommon.FlexCommon;

//REVIEW this has nothing to do with flashcommon, it's an MX subclass. rename to DensityUIComponent?
/**
 * Extends DensityAndBuoyancyFlashCommon to provide Density specific behavior (specifically, positioning of the common
 * buttons)
 */
public class DensityFlashCommon extends DensityAndBuoyancyFlashCommon {
    public function DensityFlashCommon() {
    }

    protected override function positionButtons(): void {
        var common: FlexCommon = FlexCommon.getInstance();
        if ( common.commonButtons == null ) {
            return;
        }
        var height: int = common.commonButtons.getPreferredHeight();
        const y: Number = stage.stageHeight - height - 60 - DensityAndBuoyancyConstants.CONTROL_INSET;
        trace( "y=" + y );
        common.commonButtons.setLocationXY( DensityAndBuoyancyConstants.CONTROL_INSET, y );
    }
}
}