//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
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
        };

        common.addLoadListener( positionButtons );
        stage.addEventListener( Event.RESIZE, function( evt: Event ): void {
            positionButtons();
        } );
        positionButtons();
    }

    protected function positionButtons(): void {
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