package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.flashcommon.CommonButtons;
import edu.colorado.phet.flexcommon.FlexCommon;

/**
 * Extends DensityAndBuoyancyFlashCommon to provide Buoyancy specific behavior.
 */
public class BuoyancyFlashCommon extends DensityAndBuoyancyFlashCommon {
    private var insetX;
    private var availableHeight;

    public function BuoyancyFlashCommon( insetX: Number, availableHeight: Number ) {
        this.insetX = insetX;
        this.availableHeight = availableHeight;
    }

    protected override function positionButtons(): void {
        var buttons: CommonButtons = FlexCommon.getInstance().commonButtons
        if ( buttons != null ) {
            var padding = (availableHeight - buttons.height) / 2;
            buttons.setLocationXY( stage.stageWidth - buttons.getPreferredWidth() - insetX - padding, padding );
        }
    }
}
}