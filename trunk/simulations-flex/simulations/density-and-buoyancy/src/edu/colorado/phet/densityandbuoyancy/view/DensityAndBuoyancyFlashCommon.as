package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.flexcommon.FlexCommon;

import flash.events.Event;
import flash.geom.ColorTransform;

import mx.core.UIComponent;

public class DensityAndBuoyancyFlashCommon extends UIComponent {
    public function DensityAndBuoyancyFlashCommon() {
    }

    public function init(): void {
        var common: FlexCommon = FlexCommon.getInstance();
        common.initialize( this );

        common.highContrastFunction = function ( contrast: Boolean ): void {
            if ( contrast ) {
                var stretch: Number = 2.0;
                var newCenter: Number = 128;
                var offset: Number = newCenter - 128 * stretch;
                root.stage.transform.colorTransform = new ColorTransform( stretch, stretch, stretch, 1, offset, offset, offset, 1 );
            }
            else {
                root.stage.transform.colorTransform = new ColorTransform( 1, 1, 1, 1, 0, 0, 0, 0 );
            }
        }

        function positionButtons(): void {
            if ( common.commonButtons == null ) {
                return;
            }
            var height: int = common.commonButtons.getPreferredHeight();
            const y: Number = stage.stageHeight - height - 60 - DensityConstants.CONTROL_INSET;
            trace( "y=" + y );
            common.commonButtons.setLocationXY( DensityConstants.CONTROL_INSET, y );
        }

        common.addLoadListener( positionButtons );
        stage.addEventListener( Event.RESIZE, positionButtons );
        positionButtons();
    }

}
}