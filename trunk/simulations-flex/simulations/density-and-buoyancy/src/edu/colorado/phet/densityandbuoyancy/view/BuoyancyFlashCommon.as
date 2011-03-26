//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.flashcommon.CommonButtons;
import edu.colorado.phet.flexcommon.FlexCommon;

/**
 * Extends DensityAndBuoyancyFlashCommon to provide Buoyancy specific behavior (specifically, positioning of the common
 * buttons)
 */
public class BuoyancyFlashCommon extends DensityAndBuoyancyFlashCommon {
    private var insetX: Number;
    private var availableHeight: Number;

    public function BuoyancyFlashCommon( insetX: Number, availableHeight: Number ) {
        this.insetX = insetX;
        this.availableHeight = availableHeight;
    }

    protected override function positionButtons(): void {
        var buttons: CommonButtons = FlexCommon.getInstance().commonButtons;
        if ( buttons != null ) {
            var padding: Number = (availableHeight - buttons.height) / 2;
            buttons.setLocationXY( stage.stageWidth - buttons.getPreferredWidth() - insetX - padding, padding );
        }
    }
}
}